package org.mri;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AxonFlowBuilder {
    private final EventHandlerIdentificationStrategy eventHandlerIdentificationStrategy;
    private final Map<CtTypeReference, CtMethodImpl> commandHandlers;
    private final MethodCallHierarchyBuilder callHierarchyBuilder;
    private List<CtTypeReference> aggregates;

    public AxonFlowBuilder(Map<CtTypeReference, Set<CtTypeReference>> classHierarchy,
                           Map<MethodWrapper, List<CtExecutableReference>> callList,
                           Map<CtTypeReference, List<CtMethodImpl>> eventHandlers,
                           Map<CtTypeReference, CtMethodImpl> commandHandlers,
                           List<CtTypeReference> aggregates, boolean matchEventsByName) {
        this.aggregates = aggregates;
        if (matchEventsByName) {
            this.eventHandlerIdentificationStrategy = new EventHandlerIdentificationByNameStrategy(eventHandlers);
        }
        else {
            this.eventHandlerIdentificationStrategy = new EventHandlerIdentificationBySignatureStrategy(eventHandlers);
        }
        this.commandHandlers = commandHandlers;
        this.callHierarchyBuilder = new MethodCallHierarchyBuilder(callList, classHierarchy);
    }

    List<AxonNode> buildFlow(ArrayList<CtExecutableReference> methodReferences) {
        List<AxonNode> nodes = new ArrayList<>();
        for (CtExecutableReference each : methodReferences) {
            AxonNode root = new AxonNode(AxonNode.Type.CONTROLLER, each);
            nodes.add(root);
            buildCommandFlow(root);
        }
        return nodes;
    }

    private void buildCommandFlow(AxonNode node) {
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());
        Iterable<MethodCall> allCommandConstructionCalls = Iterables.filter(methodCall.asList(), isCommandPredicate());
        System.out.println("buildCommandFlow::start::"+node.reference());
        for (MethodCall commandConstruction : allCommandConstructionCalls) {        	
            CtMethodImpl commandHandler = commandHandlers.get(commandConstruction.reference().getDeclaringType());
            System.out.println("commandConstruction.reference().name::"+commandConstruction.reference().getDeclaration()+commandConstruction.reference().getSimpleName());
    		if (!commandConstruction.reference().getSimpleName().equalsIgnoreCase("<init>")){
    			continue;
    		}
            AxonNode commandConstructionNode = new AxonNode(AxonNode.Type.COMMAND, commandConstruction.reference());
            node.add(commandConstructionNode);
            AxonNode commandHandlerNode = new AxonNode(AxonNode.Type.COMMAND_HANDLER, commandHandler.getReference());
            commandConstructionNode.add(commandHandlerNode);
            buildAggregateFlow(commandHandlerNode);
        }
        System.out.println("buildCommandFlow::end::"+node.reference());
    }

    private Predicate<MethodCall> isCommandPredicate() {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall input) {
                return commandHandlers.keySet().contains(input.reference().getDeclaringType());
            }
        };
    }

    private void buildAggregateFlow(AxonNode node) {
    	System.out.println("node"+":"+node.reference().toString());
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());

        Iterable<MethodCall> aggregateCallInstances =
                Iterables.filter(methodCall.asList(), isAggregatePredicate());
        boolean hasAggregateCall=false;
    	//aggregate call
        for (MethodCall aggregateCall : aggregateCallInstances) {
        	if (!hasAggregateCall){
        		hasAggregateCall=true;
        	}
            AxonNode aggregateNode = new AxonNode(AxonNode.Type.AGGREGATE, aggregateCall.reference());
            System.out.println("aggregate"+":"+aggregateNode.reference().toString());
            buildEventFlow(aggregateNode);
            if (aggregateNode.hasChildren()) {
                node.add(aggregateNode);
            }                  }        	
        if (!hasAggregateCall){
        	//dispatch other command
        	 buildCommandFlow(node);
        }
        
    }

    private Predicate<? super MethodCall> isAggregatePredicate() {
        return new Predicate<MethodCall>() {
            @Override
            public boolean apply(MethodCall input) {
                return aggregates.contains(input.reference().getDeclaringType());
            }
        };
    }

    private AxonNode buildEventFlow(AxonNode node) {
        MethodCall methodCall = this.callHierarchyBuilder.buildCalleesMethodHierarchy(node.reference());

        Iterable<MethodCall> eventConstructionInstances =
                Iterables.filter(methodCall.asList(), eventHandlerIdentificationStrategy.isEventPredicate());
        for (MethodCall eventConstruction : eventConstructionInstances) {
            AxonNode eventNode = new AxonNode(AxonNode.Type.EVENT, eventConstruction.reference());
            node.add(eventNode);
            for (CtMethodImpl eventHandler : eventHandlerIdentificationStrategy.findEventHandlers(eventNode.reference().getDeclaringType())) {
                AxonNode eventHandlerNode = new AxonNode(AxonNode.Type.EVENT_LISTENER, eventHandler.getReference());
                if (!eventHandlerNode.reference().getSimpleName().equalsIgnoreCase("<init>")){
        			continue;
        		}
                eventNode.add(eventHandlerNode);
                buildCommandFlow(eventHandlerNode);
            }
        }
        return node;
    }
}
