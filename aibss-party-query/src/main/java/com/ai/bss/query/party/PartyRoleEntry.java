package com.ai.bss.query.party;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PT_PARTY_ROLE")
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="PARTY_ROLE_TYPE",discriminatorType=DiscriminatorType.STRING)
public abstract class PartyRoleEntry {
	@Id
	private long partyRoleId;	
	public PartyRoleEntry(){}
	
	
	@Column(insertable = false, updatable = false,name="PARTY_ROLE_TYPE")
	String partyRoleType;
	
	@ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JoinColumn(name="PARTY_ID")
	private PartyEntry party;		
		
	public String getPartyRoleType() {
		return partyRoleType;
	}

	public void setPartyRoleType(String partyRoleType) {
		this.partyRoleType = partyRoleType;
	}

	
	public PartyRoleEntry(PartyEntry party){
		this.party=(PartyEntry)party;
	}
	
	public PartyEntry getParty() {		
		return party;
	}

	public void setParty(PartyEntry party) {
		this.party =(PartyEntry) party;
	}

	public long getPartyRoleId() {
		return partyRoleId;
	}

	public void setPartyRoleId(long partyRoleId) {
		this.partyRoleId = partyRoleId;
	}

}
