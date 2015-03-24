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
package com.wordpress.loeper.kvv.base.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wordpress.loeper.kvv.base.ServiceHandler;
import com.wordpress.loeper.kvv.base.TripRequest;
import com.wordpress.loeper.kvv.base.exception.AmbiguousTargetException;

/**
 * This class is a container which holds relevant information about a trip request response.
 * Its main target is the bundling of trips and the supply with helper methods that retrieve further trips.
 * @author Martin Löper
 * @version 1.0
 */
public class Trips implements Model {
	private List<Trip> trips = new ArrayList<Trip>();
	private ServiceHandler mServiceHandler;
	private TripRequest mTripRequest;
	
	public Trips(ServiceHandler pServiceHandler, TripRequest pTripRequest) {
		this.mServiceHandler = pServiceHandler;
		this.mTripRequest = pTripRequest;
	}

	/**
	 * Returns a list of trips
	 * @return list of trips
	 */
	public List<Trip> getTrips() {
		return this.trips;
	}
	
	public void add(Trip pTrip) {
		this.trips.add(pTrip);
	}
	
	/**
	 * Returns the origin's name of the trips.
	 * This method can be used to determine which origin station the KVV server used and if it matches the user's intention.
	 * @return the origin station of the trips
	 */
	public String getOriginName() {
		if(this.trips.size() == 0 || this.trips.get(0).getSections().size() == 0)
			return "";	// TODO maybe throw exception here
		
		return this.trips.get(0).getSections().get(0).getOriginPlace();	// section with id 0 should exist...
	}
	
	/**
	 * Returns the destination's name of the trips.
	 * This method can be used to determine which destination station the KVV server used and if it matches the user's intention.
	 * @return the destination station of the trips
	 */
	public String getDestinationName() {
		if(this.trips.size() == 0)
			return "";	// TODO maybe throw exception here
		
		Trip firstTrip = this.trips.get(0);
		
		if(firstTrip.getSections().size() == 0)
			return "";	// TODO maybe throw exception here
		
		return firstTrip.getSections().get(firstTrip.getSections().size() - 1).getDestinationPlace();
	}
	
	/**
	 * Returns the trips following those recently requested.
	 * @return the chronologically next trips
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws AmbiguousTargetException thrown if route could not be determined
	 */
	public Trips getNext() throws IOException, AmbiguousTargetException {
		Trip lastTrip = this.trips.get(this.trips.size() - 1);
		this.mTripRequest.setTimestamp(lastTrip.getInterval()[1]);
		Trips newTrips = mServiceHandler.getTrips(this.mTripRequest);
				
		for(Iterator<Trip> it = newTrips.getTrips().iterator();it.hasNext();) {
			if(this.getTrips().contains(it.next()))
				it.remove();
		}
		
		return newTrips;
	}
	
	/**
	 * Returns the trips preceding those recently requested.
	 * @return the chronologically previous trips
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws AmbiguousTargetException thrown if route could not be determined
	 */
	public Trips getPrevious() throws IOException, AmbiguousTargetException {
		Trip firstTrip = this.trips.get(0);
		this.mTripRequest.setTimestamp(firstTrip.getInterval()[0]);
		this.mTripRequest.setTravelType(ServiceHandler.TravelType.ARRIVAL);
		Trips newTrips = mServiceHandler.getTrips(this.mTripRequest);
		
		for(Iterator<Trip> it = newTrips.getTrips().iterator();it.hasNext();) {
			if(this.getTrips().contains(it.next()))
				it.remove();
		}
		
		return newTrips;	
	}
	
	@Override
	public String toString() {
		String out = "";
		int i = 0;
		for(Trip cTrip : this.getTrips()) {
			if(i > 0)
				out += ",";
			
			out += cTrip.toString();
			i++;
		}
		
		return out;
	}
}
