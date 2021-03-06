package com.ai.bss.webui.customerorder.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ai.bss.api.base.CharacteristicValue;
import com.ai.bss.api.customerorder.CustomerOrderId;
import com.ai.bss.api.customerorder.command.StartBuyOrderCommand;
import com.ai.bss.api.product.dto.BuyOffer;
import com.ai.bss.api.product.dto.Price;
import com.ai.bss.api.product.dto.Product;
import com.ai.bss.api.product.dto.ProductCharacteristicValue;
import com.ai.bss.api.shoppingcart.ShoppingCartId;
import com.ai.bss.api.shoppingcart.ShoppingCartItemId;
import com.ai.bss.api.shoppingcart.command.AddShoppingCartItemCommand;
import com.ai.bss.api.shoppingcart.command.CreateShoppingCartCommand;
import com.ai.bss.api.shoppingcart.command.DecreaseShoppingCartItemQuantityCommand;
import com.ai.bss.api.shoppingcart.command.DeleteShoppingCartItemCommand;
import com.ai.bss.api.shoppingcart.command.IncreaseShoppingCartItemQuantityCommand;
import com.ai.bss.mutitanent.TenantContext;
import com.ai.bss.query.api.customerorder.CustomerOrderEntry;
import com.ai.bss.query.api.shoppingcart.ShoppingCartEntry;
import com.ai.bss.webui.customerorder.model.CustomerOrder;
import com.ai.bss.webui.customerorder.model.NewCustomerOrder;
import com.ai.bss.webui.productsale.model.OfferDetail;
import com.ai.bss.webui.shoppingcart.model.ShoppingCartItem;
import com.ai.bss.webui.util.BaseController;
/**
 * @author Lianhua Zhang
 */
@Controller
@RequestMapping("/customerorder")
public class CustomerOrderController extends BaseController{

	public CustomerOrderController() {
	}
	@RequestMapping(value = "/{customerOrderId}", method = RequestMethod.GET)
    public String details(@PathVariable String customerOrderId,BindingResult bindingResult,Model model) {
		CustomerOrderEntry customerOrder = client.getForObject("http://customerorder-query-service/customerorder/customerOrderId/"+customerOrderId,CustomerOrderEntry.class);
		model.addAttribute("customerOrder",customerOrder);
		return "/customerorder/detail";
	}
		
	@RequestMapping(value = "/offerDetail/{customerId}/{offerId}", method = RequestMethod.GET)
    public String buyNow(@PathVariable String customerId,@PathVariable String offerId,Model model) {
		BuyOffer offerDetail=new BuyOffer();
		offerDetail.setProductOfferingId(offerId);
		//offerDetail.setName("iphone 6S");
		//TODO to be removed, test only
		BuyOffer iPhone7offer = new BuyOffer();
		iPhone7offer.setProductOfferingId("1011");
		Price oneTimeFee = new Price();
		oneTimeFee.setPriceSpecificationId("30001");
		oneTimeFee.setUnitPrice(10000);		
		iPhone7offer.addOneTimeFee(oneTimeFee);
		Product iPhone7=new Product();
		iPhone7.setProductSpecificationId("20001");
		iPhone7offer.addProduct(iPhone7);
        model.addAttribute("offer", iPhone7offer);
        return "customerorder/buyNow";
    }
	
	@RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(@PathVariable String customerId,@ModelAttribute("newCustomerOrder") @Valid NewCustomerOrder newCustomerOrder, BindingResult bindingResult, Model model) {
    	if (!bindingResult.hasErrors()) {
    		CustomerOrderId customerOrderId=new CustomerOrderId();
    		StartBuyOrderCommand command =new StartBuyOrderCommand();
    		command.setCustomerId(newCustomerOrder.getCustomerId());
    		command.setCustomerOrderId(customerOrderId);
    		command.setOffers(newCustomerOrder.getOffers());
    		command.setCharacterValues(newCustomerOrder.getCharacterValues());
    		command.setTenantId(TenantContext.getCurrentTenant());  		
    		try {
    			command=client.postForObject("http://customerorder-service/customerorder/startBuyOrderCommand",command,StartBuyOrderCommand.class);
			} catch (Exception e) {
				bindingResult.rejectValue("offerId",
                        "error.buy.failed",
                        e.getCause().getMessage());
			}
    	}
    	return "/customerorder/list";
    }	
}
