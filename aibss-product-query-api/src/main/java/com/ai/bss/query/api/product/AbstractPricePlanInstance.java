package com.ai.bss.query.api.product;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.ai.bss.api.base.TimePeriod;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractPricePlanInstance{
	public enum PriceState {
		INITIATED(0),
		CREATED(1),
		SUBMITTED(2),
		PAID(3),
		ASSIGNED(5),
		COMPLETED(6);
		private int value;  

	    private PriceState(int value){ 
	        this.value=value; 
	    } 
 
	    public int getValue(){ 
	        return value; 
	    } 
	}

	private String pricePlanId;
	private long priceValue;
	private int payState;
	private String discountReason;
	private String roleId;
	@Column(name="PRICE_TYPE")
	private int priceType;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;	
	
	@ManyToOne
	private AbstractOfferInstance offerInstance;
	
	@OneToMany(mappedBy="price",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private Set<AbstractProductPriceRel> assignedTo=new LinkedHashSet<AbstractProductPriceRel>();
	
	@OneToMany(mappedBy="pricePlanInstance",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	private Set<PricePlanInstanceCharacterValueEntry> characterValues=new LinkedHashSet<PricePlanInstanceCharacterValueEntry>();

	public  Set<PricePlanInstanceCharacterValueEntry> getCharacterValues(){
		return this.characterValues;
	}
	
	public void addCharacterValue(PricePlanInstanceCharacterValueEntry characterValue){
		if(null!=characterValue){
			this.characterValues.add(characterValue);
		}
	}
	
	public AbstractOfferInstance getOfferInstance() {
		return offerInstance;
	}

	
	public void setOfferInstance(AbstractOfferInstance offerInstance) {
		this.offerInstance=offerInstance;
	}

	
	public Set<AbstractProductPriceRel> getAssignedTo() {
		return assignedTo;
	}
	
	protected abstract AbstractProductPriceRel newProductPriceRel();
	
	public void assignTo(AbstractProduct product,TimePeriod validPeriod) {
		if(null!=product){
			AbstractProductPriceRel productPriceRel = newProductPriceRel();
			productPriceRel.setPricePlanInstance(this);
			productPriceRel.setProduct(product);
			productPriceRel.setValidPeriod(validPeriod);
			if(!assignedTo.contains(productPriceRel)){
				assignedTo.add(productPriceRel);
			}
			
			if(!product.getAssignedPrices().contains(productPriceRel)){
				product.getAssignedPrices().add(productPriceRel);
			}
		}
		
	}
		
	public void unAssignTo(AbstractProduct product) {
		if(null!=product){
			AbstractProductPriceRel productPriceRel= newProductPriceRel();
			productPriceRel.setPricePlanInstance(this);
			productPriceRel.setProduct(product);
			if(assignedTo.contains(productPriceRel)){
				assignedTo.remove(productPriceRel);
			}
			if(product.getAssignedPrices().contains(productPriceRel)){
				product.getAssignedPrices().remove(productPriceRel);
			}			
		}
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	

	public AbstractPricePlanInstance() {
	}

	
	public String getPricePlanId() {
		return pricePlanId;
	}

	
	public void setPricePlanId(String pricePlanId) {
		this.pricePlanId=pricePlanId;
	}
	
	
	public long getPriceValue() {
		return this.priceValue;
	}

	
	public void setPriceValue(long priceValue) {
		this.priceValue=priceValue;
	}	

	
	public int getPayState() {
		return this.payState;
	}

	
	public void unPaid() {
		this.payState=AbstractPricePlanInstance.PriceState.SUBMITTED.getValue();
	}

	
	public void paid() {
		this.payState=AbstractPricePlanInstance.PriceState.PAID.getValue();
	}

	
	public String getDiscountReason() {
		return this.discountReason;
	}

	
	public void setDiscountReason(String discountReason) {
		this.discountReason=discountReason;
	}
	
	public String getRoleId() {
		return this.roleId;
	}

	
	public void setRoleId(String roleId) {
		this.roleId=roleId;
	}

	public int getPriceType() {
		return priceType;
	}

	public void setPriceType(int priceType) {
		this.priceType = priceType;
	}
}