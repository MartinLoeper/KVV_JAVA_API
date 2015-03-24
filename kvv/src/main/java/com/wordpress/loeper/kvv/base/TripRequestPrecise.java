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
import java.util.HashMap;

import com.wordpress.loeper.kvv.base.exception.AmbiguousTargetException;

/**
 * A slightly more precise TripRequest which is needed if some station names are similar.
 * This one cannot be constructed by the user manually, because the session values are obtained by the kvv server.
 * This is why it can be obtained by {@link AmbiguousTargetException#getModifiedTripRequest(TripSuggestion, TripSuggestion)}.
 * @author Martin Löper
 * @version 1.0
 */
public class TripRequestPrecise extends TripRequest {
	private String mOriginSessionValue;
	private String mDestinationSessionValue;
	
	public TripRequestPrecise(String pOrigin, String pDestination, Calendar pCalendar, String pOriginSessionValue, String pDestinationSessionValue) {
		super(pOrigin, pDestination, pCalendar);
		this.mDestinationSessionValue = pDestinationSessionValue;
		this.mOriginSessionValue = pOriginSessionValue;
	}
	
	public TripRequestPrecise(String pOrigin, String pDestination, long pTimestamp, String pOriginSessionValue, String pDestinationSessionValue) {
		super(pOrigin, pDestination, pTimestamp);
		this.mDestinationSessionValue = pDestinationSessionValue;
		this.mOriginSessionValue = pOriginSessionValue;
	}
	
	/* copy constructor */
	public TripRequestPrecise(TripRequest pTripRequest, String pOriginSessionValue, String pDestinationSessionValue) {
		super(pTripRequest.getOrigin(), pTripRequest.getDestination(), pTripRequest.getTimestamp());
		setChangeSpeed(pTripRequest.getChangeSpeed());
		setLanguage(pTripRequest.getLanguage());
		setOmitElevators(pTripRequest.getOmitElevators());
		setOmitEscalators(pTripRequest.getOmitEscalators());
		setOmitSolidStairs(pTripRequest.getOmitSolidStairs());
		setRequireLowPlatformVhcl(pTripRequest.getRequireLowPlatformVhcl());
		setRequireWheelchairVhcl(pTripRequest.getRequireWheelchairVhcl());
		setSession(pTripRequest.getSession());
		setTravelType(pTripRequest.getTravelType());
		
		this.mDestinationSessionValue = pDestinationSessionValue;
		this.mOriginSessionValue = pOriginSessionValue;
	}
	
	public String getOriginSessionValue() {
		return this.mOriginSessionValue;
	}
	
	public String getDestinationSessionValue() {
		return this.mDestinationSessionValue;
	}

	@Override
	public HashMap<String, String> getHttpParameters() {
		HashMap<String, String> parameters = super.getHttpParameters();
		parameters.put("ix_originSessionValue", this.mOriginSessionValue);
		parameters.put("ix_destinationSessionValue", this.mDestinationSessionValue);
		
		if(this.mOriginSessionValue.contains(":"))
			parameters.put("nameState_origin", "list");
		
		if(this.mDestinationSessionValue.contains(":"))
			parameters.put("nameState_destination", "list");
		
		return parameters;
	}
}
