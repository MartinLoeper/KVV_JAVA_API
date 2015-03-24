/*
 * This file is part of the unofficial Java KVV API.
 * 
 * Copyright (c) 2014, Martin Löper
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.wordpress.loeper.kvv.base;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.wordpress.loeper.kvv.base.ServiceHandler.Language;
import com.wordpress.loeper.kvv.base.ServiceHandler.TravelChangeSpeed;
import com.wordpress.loeper.kvv.base.ServiceHandler.TravelType;

/**
 * A TripRequest for the KVV server to query trip suggestions.
 * @author Martin Löper
 * @version 1.0
 */
public class TripRequest {
	private String mOrigin;
	private String mDestination;
	private long mTimestamp;
	private Language mLanguage = ServiceHandler.Language.ENGLISH;
	private TravelType mTravelType = ServiceHandler.TravelType.DEPARTURE;
	private boolean mOmitSolidStairs = false;
	private boolean mOmitElevators = false;
	private boolean mOmitEscalators = false;
	private boolean mRequireLowPlatformVhcl = false;
	private boolean mRequireWheelchairVhcl = false;
	private TravelChangeSpeed mChangeSpeed = ServiceHandler.TravelChangeSpeed.NORMAL;
	private Session mSession = new Session();
	
	/**
	 * Creates a new TripRequest with given settings.
	 * @param pOrigin the origin
	 * @param pDestination the destination
	 * @param pTimestamp the unix timestamp
	 */
	public TripRequest(String pOrigin, String pDestination, long pTimestamp) {
		this.mOrigin = pOrigin;
		this.mDestination = pDestination;
		this.mTimestamp = pTimestamp;
	}
	
	/**
	 * Creates a new TripRequest with given settings.
	 * @param pOrigin the origin
	 * @param pDestination the destination
	 * @param pTimestamp the date as calendar object
	 */
	public TripRequest(String pOrigin, String pDestination, Calendar pCalendar) {
		this(pOrigin, pDestination, (pCalendar.getTime().getTime() / 1000));
	}
	
	/**
	 * Creates a new TripRequest with given settings.
	 * @param pOrigin the origin
	 * @param pDestination the destination
	 */
	public TripRequest(String pOrigin, String pDestination) {
		this(pOrigin, pDestination, (Calendar.getInstance().getTime().getTime() / 1000));
	}
	
	public String getOrigin() {
		return this.mOrigin;
	}
	
	public String getDestination() {
		return this.mDestination;
	}
	
	public long getTimestamp() {
		return this.mTimestamp;
	}
	
	public Language getLanguage() {
		return this.mLanguage;
	}
	
	public TravelType getTravelType() {
		return this.mTravelType;
	}
	
	public boolean getOmitSolidStairs() {
		return this.mOmitSolidStairs;
	}
	
	public boolean getOmitElevators() {
		return this.mOmitElevators;
	}
	
	public boolean getOmitEscalators() {
		return this.mOmitEscalators;
	}
	
	public boolean getRequireLowPlatformVhcl() {
		return this.mRequireLowPlatformVhcl;
	}
	
	public boolean getRequireWheelchairVhcl() {
		return this.mRequireWheelchairVhcl;
	}
	
	public TravelChangeSpeed getChangeSpeed() {
		return this.mChangeSpeed;
	}
	
	public Session getSession() {
		return this.mSession;
	}
	
	public TripRequest setLanguage(Language pLanguage) {
		this.mLanguage = pLanguage;
		return this;
	}
	
	public void setDestination(String pDestination) {
		this.mDestination = pDestination;
	}
	
	public void setTimestamp(long pTimestamp) {
		this.mTimestamp = pTimestamp;
	}
	
	public void setTimestamp(Calendar pCalendar) {
		this.mTimestamp = (pCalendar.getTime().getTime() / 1000);
	}
	
	public void setOrigin(String pOrigin) {
		this.mOrigin = pOrigin;
	}
	
	public TripRequest setBarrierFree() {
		setOmitElevators(true);
		setOmitEscalators(true);
		setOmitSolidStairs(true);
		setRequireLowPlatformVhcl(true);
		setRequireWheelchairVhcl(true);
		return this;
	}
	
	public TripRequest setTravelType(TravelType pTravelType) {
		this.mTravelType = pTravelType;
		return this;
	}
	
	public TripRequest setOmitSolidStairs(boolean pOmitSolidStairs) {
		this.mOmitSolidStairs = pOmitSolidStairs;
		return this;
	}
	
	public TripRequest setOmitElevators(boolean pOmitElevators) {
		this.mOmitElevators = pOmitElevators;
		return this;
	}
	
	public TripRequest setOmitEscalators(boolean pOmitEscalators) {
		this.mOmitEscalators = pOmitEscalators;
		return this;
	}
	
	public TripRequest setRequireWheelchairVhcl(boolean pRequireWheelchairVhcl) {
		this.mRequireWheelchairVhcl = pRequireWheelchairVhcl;
		return this;
	}
	
	public TripRequest setRequireLowPlatformVhcl(boolean pRequireLowPlatformVhcl) {
		this.mRequireLowPlatformVhcl = pRequireLowPlatformVhcl;
		return this;
	}
	
	public TripRequest setChangeSpeed(TravelChangeSpeed pChangeSpeed) {
		this.mChangeSpeed = pChangeSpeed;
		return this;
	}
	
	public TripRequest setSession(Session pSession) {
		this.mSession = pSession;
		return this;
	}
	
	/**
	 * Returns the HTTP post parameter to be sent to the kvv server.
	 * @return the parameters as a key value hash map
	 */
	public HashMap<String, String> getHttpParameters() {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("ix_originValue", this.mOrigin);
		parameters.put("ix_originText", this.mOrigin);
		parameters.put("type_origin", "any");	// TODO consider a change here

		parameters.put("ix_destinationValue", this.mDestination);
		parameters.put("ix_destinationText", this.mDestination);
		parameters.put("type_destination", "any");	// TODO consider a change here
		
		Calendar ix_date = Calendar.getInstance();
		ix_date.setTime(new Date(this.mTimestamp * 1000));
		parameters.put("ix_date", ((ix_date.get(Calendar.DAY_OF_MONTH) < 10) ? "0" : "") + ix_date.get(Calendar.DAY_OF_MONTH) + "." + ((ix_date.get(Calendar.MONTH) + 1 < 10) ? "0" : "") + (ix_date.get(Calendar.MONTH) + 1) + "." + ix_date.get(Calendar.YEAR));
		parameters.put("ix_hour", String.valueOf(ix_date.get(Calendar.HOUR_OF_DAY)));
		parameters.put("ix_minute", String.valueOf(ix_date.get(Calendar.MINUTE)));
		
		parameters.put("ix_travelType", this.mTravelType.get());
		parameters.put("ix_noSolidStairs", String.valueOf(this.mOmitSolidStairs ? 1 : 0));
		parameters.put("ix_noEscalators", String.valueOf(this.mOmitEscalators ? 1 : 0));
		parameters.put("ix_noElevators", String.valueOf(this.mOmitElevators ? 1 : 0));
		parameters.put("ix_lowPlatformVhcl", String.valueOf(this.mRequireLowPlatformVhcl ? 1 : 0));
		parameters.put("ix_wheelchair", String.valueOf(this.mRequireWheelchairVhcl? 1 : 0));
		parameters.put("ix_changeSpeed", String.valueOf(this.mChangeSpeed.get()));
		
		parameters.put("ix_language", this.mLanguage.get());		
		parameters.put(ServiceHandler.KVV_SEARCH_ENGINE_KEY_CONST, ServiceHandler.KVV_SEARCH_ENGINE_VALUE_CONST);
		parameters.put("ix_action", ServiceHandler.Action.TRIP_SEARCH.get());
		parameters.put("sessionID", this.mSession.SESSION_ID);
		parameters.put("requestID", this.mSession.REQUEST_ID);
		
		return parameters;
	}
}
