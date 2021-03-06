package com.ai.bss.aggregate.party;

import org.axonframework.eventhandling.annotation.EventHandler;
import com.ai.bss.api.party.PartyId;
import com.ai.bss.api.party.event.IndividualCreatedEvent;
import com.ai.bss.api.party.event.IndividualRenamedEvent;
import com.ai.bss.api.party.event.IndividualTerminatedEvent;
import com.ai.bss.exception.party.NewPartyNameSameAsOldException;
import com.ai.bss.mutitanent.TenantContext;

public class Individual extends Party {
	private String firstName;
	private String lastName;	

	public Individual(){
		
	}
	
	public Individual(PartyId partyId,String firstName,String lastName){
		IndividualCreatedEvent event = new IndividualCreatedEvent(partyId, firstName,lastName);
		event.setTenantId(TenantContext.getCurrentTenant());
		apply(event);
	}

	@EventHandler
	public void onIndividualCreated(IndividualCreatedEvent event){
		this.setPartyId(event.getPartyId());
		this.firstName=event.getFirstName();
		this.lastName=event.getLastName();
		this.setState("Initial");
	}
	
	public void rename(String firstName,String lastName) throws Exception{
		if (firstName.equalsIgnoreCase(this.firstName) &&
				lastName.equalsIgnoreCase(this.lastName)	){
			throw new NewPartyNameSameAsOldException(this.getName());
		}
		this.firstName=firstName;
		this.lastName=lastName;
		IndividualRenamedEvent event=new IndividualRenamedEvent(this.getPartyId(), firstName, lastName);
		event.setTenantId(TenantContext.getCurrentTenant());
		apply(event);
	}
	
	public void terminate() throws Exception{
		//TODO check if party has partyRoles, if have, notify to terminate partyRole first.
		this.setState("Terminated");
		IndividualTerminatedEvent event =new IndividualTerminatedEvent(this.getPartyId());
		event.setTenantId(TenantContext.getCurrentTenant());
		apply(event);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getName() {
		StringBuffer name=new StringBuffer();
		name.append(this.getLastName()).append(" ").append(this.getFirstName());
		return name.toString();
	}
}
