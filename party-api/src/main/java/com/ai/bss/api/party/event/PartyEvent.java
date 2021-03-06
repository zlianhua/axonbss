package com.ai.bss.api.party.event;

import com.ai.bss.api.base.BaseEvent;
import com.ai.bss.api.party.PartyId;

public abstract class PartyEvent extends BaseEvent{
	private PartyId partyId;
	
	protected PartyEvent(){}
	
	protected PartyEvent(PartyId partyId) {
		this.partyId=partyId;
	}
	
	
	public PartyId getPartyId() {
		return partyId;
	}
	public void setPartyId(PartyId partyId) {
		this.partyId = partyId;
	}
}
