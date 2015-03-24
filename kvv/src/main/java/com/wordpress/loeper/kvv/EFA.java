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
package com.wordpress.loeper.kvv;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.wordpress.loeper.kvv.base.Base;
import com.wordpress.loeper.kvv.base.Session;
import com.wordpress.loeper.kvv.base.TripRequest;
import com.wordpress.loeper.kvv.base.ServiceHandler.Language;
import com.wordpress.loeper.kvv.base.exception.AmbiguousTargetException;
import com.wordpress.loeper.kvv.base.model.Locations;
import com.wordpress.loeper.kvv.base.model.Trips;
import com.wordpress.loeper.kvv.helper.SimilarString;
import com.wordpress.loeper.kvv.live.Live;
import com.wordpress.loeper.kvv.live.exception.StopNotFoundException;
import com.wordpress.loeper.kvv.live.model.Departures;
import com.wordpress.loeper.kvv.live.model.GeoStops;
import com.wordpress.loeper.kvv.live.model.Stop;
import com.wordpress.loeper.kvv.live.model.Stops;

/**
 * Provides simple access to both API components (base and live).<br>
 * EFA is the german abbreviation for 'electronic timetable information'.<br><br>
 * The live API has direct access to live data over JSON and therefore is very fast.<br>
 * Methods using the live endpoint are marked with the {@code @Live} annotation.<br><br>
 * 
 * The base API parses HTML output from the kvv servers and thus is slow, but provides extended functionality.<br>
 * Methods using the base endpoint are marked with the {@code @Base} annotation.<br><br>
 * Please keep in mind that this is an unofficial API and thus is subject to change without notice.<br>
 * If some method does not work as expected anymore, feel free to contact the source-code author.<br><br>
 * 
 * @version 1.0
 * @author Martin Löper
 *
 */
public class EFA {
	/**
	 * Live API Handler
	 */
	private com.wordpress.loeper.kvv.live.ServiceHandler mServiceHandlerLive = null;
	/**
	 * Base API Handler
	 */
	private com.wordpress.loeper.kvv.base.ServiceHandler mServiceHandlerBase = null;
	
	/**
	 * Default constructor.
	 * Do nothing. Using default values: i.e. English language
	 */
	public EFA() {}
	
	/**
	 * Creates an EFA object and instructs the API to request data in the given {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language}.
	 * This is actually only useful for the {@link com.wordpress.loeper.kvv.base.Base Base API} components.
	 * @param pLanguage one of the predefined {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language} constants
	 */
	@Nonnull
	public EFA(Language pLanguage) {
		this.setLanguage(pLanguage);
	}
	
	/**
	 * Creates an EFA object and reuses the given {@link com.wordpress.loeper.kvv.base.Session Session} object.
	 * @param pSession the {@link com.wordpress.loeper.kvv.base.Session Session} to reuse (only useful for Base API)
	 */
	@Nonnull
	public EFA(Session pSession) {
		this.setSession(pSession);
	}
	
	/**
	 * Creates an EFA object, reuses the given {@link com.wordpress.loeper.kvv.base.Session Session} object 
	 * and instructs the API to request data in the given {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language}.
	 * @param pLanguage one of the predefined {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language} constants
	 * @param pSession the {@link com.wordpress.loeper.kvv.base.Session Session} to reuse (only useful for Base API)
	 */
	@Nonnull
	public EFA(Language pLanguage, Session pSession) {
		this.setLanguage(pLanguage);
		this.setSession(pSession);
	}
	
	/**
	 * Returns the currently used {@link com.wordpress.loeper.kvv.live.ServiceHandler ServiceHandler}.
	 * @return the currently used ServiceHandler (for Live API requests)
	 */
	@Live
	private com.wordpress.loeper.kvv.live.ServiceHandler getLiveServiceHandler() {
		if(this.mServiceHandlerLive == null)
			this.mServiceHandlerLive = com.wordpress.loeper.kvv.live.ServiceHandler.create();
		
		return this.mServiceHandlerLive;
	}
	
	/**
	 * Returns the currently used {@link com.wordpress.loeper.kvv.base.ServiceHandler ServiceHandler}.
	 * @return the currently used ServiceHandler (for Base API requests)
	 */
	@Base
	private com.wordpress.loeper.kvv.base.ServiceHandler getBaseServiceHandler() {
		if(this.mServiceHandlerBase == null)
			this.mServiceHandlerBase = new com.wordpress.loeper.kvv.base.ServiceHandler();
		
		return this.mServiceHandlerBase;
	}
	
	/**
	 * Set the {@link com.wordpress.loeper.kvv.base.Session Session} to use for subsequent Base API requests.
	 * @param pSession the {@link com.wordpress.loeper.kvv.base.Session Session} to reuse
	 */
	@Base
	@Nonnull
	public void setSession(Session pSession) {
		this.getBaseServiceHandler().setSession(pSession);
	}
	
	/**
	 * Set the {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language} to use for subsequent Base API requests.
	 * @param pLanguage one of the predefined {@link com.wordpress.loeper.kvv.base.ServiceHandler.Language Language} constants
	 */
	@Base
	@Nonnull
	public void setLanguage(Language pLanguage) {
		this.getBaseServiceHandler().setLanguage(pLanguage);
	}
	
	/**
	 * Returns {@link com.wordpress.loeper.kvv.base.model.Location Locations} with a similar name compared to the given String.<br>
	 * This method provides similar functionality to the Live API method {@link #searchStopByName(String)}.
	 * {@link #searchStopByName(String)} as part of the Live endpoint is marginal faster than this method and returns a different container: 
	 * {@link com.wordpress.loeper.kvv.live.model.Stops Stops}.
	 * @param pName the searchphrase for the {@link com.wordpress.loeper.kvv.base.model.Location Location}'s name attribute
	 * @return an object ({@link com.wordpress.loeper.kvv.base.model.Locations Locations}) acting as container for {@link com.wordpress.loeper.kvv.base.model.Location Location} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 */
	@Base
	@Nonnull
	public Locations autocomplete(String pName) throws IOException {
		return this.getBaseServiceHandler().autocomplete(pName.trim());
	}
	
	/**
	 * Search for routes between two locations.
	 * @param pTripRequest the request containing information about time, location, etc.
	 * @return an object ({@link com.wordpress.loeper.kvv.base.model.Trips Trips}) acting as container for {@link com.wordpress.loeper.kvv.base.model.Trip Trip} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws AmbiguousTargetException thrown if route could not be determined
	 */
	@Base
	@Nonnull
	public Trips getTrips(TripRequest pTripRequest) throws IOException, AmbiguousTargetException {
		return this.getBaseServiceHandler().getTrips(pTripRequest);
	}
	
	/**
	 * Set the number of max infos to the given value.
	 * This number is important for the Live endpoint to set the max number of expected items.
	 * @param pMax the max number of expected replies from the kvv server (default is 10)
	 */
	@SuppressWarnings("static-access")
	@Live
	public void setMaxInfos(int pMax) {
		getLiveServiceHandler().setMaxInfos(pMax);
	}
	
	/**
	 * Returns {@link com.wordpress.loeper.kvv.live.model.Stop Stops} with a similar name compared to the given String.<br>
	 * This method provides similar functionality to the Live API method {@link #autocomplete(String)}.
	 * @param pStopName the searchphrase for the {@link com.wordpress.loeper.kvv.live.model.Stop Stop}'s name attribute
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.Stops Stops}) acting as container for {@link com.wordpress.loeper.kvv.live.model.Stop Stop} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 */
	@Live
	@Nonnull
	public Stops searchStopByName(String pStopName) throws IOException {
		return this.getLiveServiceHandler().searchStopsByName(pStopName.trim());
	}
	
	/**
	 * Returns the {@link com.wordpress.loeper.kvv.live.model.Stop Stop} object whose id matches those provided the given String.
	 * @param pStopId the searchphrase for the {@link com.wordpress.loeper.kvv.live.model.Stop Stop}'s id attribute
	 * @return the {@link com.wordpress.loeper.kvv.live.model.Stop Stop} if available - StopNotFoundException is thrown otherwise
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws StopNotFoundException thrown if a stop with the given id does not exist
	 */
	@Live
	@Nonnull
	public Stop getStopById(String pStopId) throws IOException, StopNotFoundException {
		try {
			return this.getLiveServiceHandler().getStopById(pStopId.trim());
		}
		catch(com.google.api.client.http.HttpResponseException e) {
			if(e.getStatusCode() > 500)	// server errors do not indicate a StopNotFoundException
				throw new StopNotFoundException(pStopId);
			else 
				throw e;
		}
	}
	
	/**
	 * Returns {@link com.wordpress.loeper.kvv.live.model.GeoStop GeoStops} which are near to the provided coordinates.
	 * @param pLatitude the latitude to examine
	 * @param pLongitude the longitude to examine
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.GeoStops GeoStops}) acting as container for {@link com.wordpress.loeper.kvv.live.model.GeoStop GeoStop} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 */
	@Live
	@Nonnull
	public GeoStops searchStopByGeoLocation(double pLatitude, double pLongitude) throws IOException {
		return this.getLiveServiceHandler().searchStopByGeoLocation(pLatitude, pLongitude);
	}
	
	/**
	 * Returns current (realtime) {@link com.wordpress.loeper.kvv.live.model.Departures Departures} from the given stop.
	 * @param pStopId the id of the stop to examine
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.Departures Departures}) acting as container for {@link com.wordpress.loeper.kvv.live.model.Departure Departure} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws StopNotFoundException thrown if a stop with the given id does not exist
	 */
	@Live
	@Nonnull
	public Departures getDeparturesByStopId(String pStopId) throws IOException, StopNotFoundException {
		try {
			return this.getLiveServiceHandler().getDeparturesByStopId(pStopId.trim());
		}
		catch(com.google.api.client.http.HttpResponseException e) {
			if(e.getStatusCode() != 500)
				throw new StopNotFoundException(pStopId);
			else 
				throw e;
		}
	}
	
	/**
	 * Returns current (realtime) {@link com.wordpress.loeper.kvv.live.model.Departures Departures} from the given stop.
	 * @param pStop the stop to examine
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.Departures Departures}) acting as container for {@link com.wordpress.loeper.kvv.live.model.Departure Departure} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws StopNotFoundException thrown if the given stop does not exist
	 */
	@Live
	@Nonnull
	public Departures getDeparturesByStopId(Stop pStop) throws IOException, StopNotFoundException {
		return this.getDeparturesByStopId(pStop.getId());
	}
	
	/**
	 * Returns upcoming departures from the given stop for a given route.
	 * @param pStopId the id of the stop to examine
	 * @param pRoute the route to examine
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.Departures Departures}) acting as container for {@link com.wordpress.loeper.kvv.live.model.Departure Departure} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws StopNotFoundException thrown if a stop with the given id does not exist
	 */
	@Live
	@Nonnull
	public Departures getDeparturesByStopIdAndRoute(String pStopId, String pRoute) throws IOException, StopNotFoundException {
		try {
			return this.getLiveServiceHandler().getDeparturesByStopIdAndRoute(pStopId.trim(), pRoute.trim());
		}
		catch(com.google.api.client.http.HttpResponseException e) {
			if(e.getStatusCode() != 500)
				throw new StopNotFoundException(pStopId);
			else 
				throw e;
		}
	}
	
	/**
	 * Returns upcoming departures from the given stop for a given route.
	 * @param pStopId the stop to examine
	 * @param pRoute the route to examine
	 * @return an object ({@link com.wordpress.loeper.kvv.live.model.Departures Departures}) acting as container for {@link com.wordpress.loeper.kvv.live.model.Departure Departure} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws StopNotFoundException thrown if the given stop does not exist
	 */
	@Live
	@Nonnull
	public Departures getDeparturesByStopIdAndRoute(Stop pStop, String pRoute) throws IOException, StopNotFoundException {
		return this.getDeparturesByStopIdAndRoute(pStop.getId(), pRoute);
	}
	
	/**
	 * Returns true if a stop with the given id is part of the route provided.
	 * @param pStopId the id of the stop to examine
	 * @param pRoute the route where it should be part of
	 * @return true if pRoute is part of pStopId, false otherwise
	 */
	@Live
	@Nonnull
	public boolean isStopPartOfRoute(String pStopId, String pRoute) {
		try {
			this.getDeparturesByStopIdAndRoute(pStopId, pRoute);
			return true;
		}
		catch(StopNotFoundException | IOException e) {
			return false;
		}
	}
	
	/**
	 * Returns true if a stop with the given id is part of the route provided.
	 * @param pStopId the stop to examine
	 * @param pRoute the route where it should be part of
	 * @return true if pRoute is part of pStopId, false otherwise
	 */	
	@Live
	@Nonnull
	public boolean isStopPartOfRoute(Stop pStop, String pRoute) {
		return this.isStopPartOfRoute(pStop.getId(), pRoute);
	}
	
	/**
	 * Returns the {@link com.wordpress.loeper.kvv.live.model.Stop Stop} object among the provided ones whose name is most likely the given String.
	 * @param pStops the stops between which to choose
	 * @param pUserInput the name which should be matched against
	 * @return the Stop that has the highest ranking (i.e. is lexicographically that one, which the user had in mind)
	 */
	@Live
	@Nonnull
	public Stop getMostProbableMatch(Stops pStops, String pUserInput) {
		if(pStops.getStops().size() == 0)
			return null;
		
		Stop maxScoredStop = pStops.getStops().get(0);
		int maxScore = 0;
		for(Stop cStop : pStops.getStops()) {
			int cScore = new SimilarString(cStop.getName(), pUserInput).similar();
			if(cScore > maxScore) {
				maxScore = cScore;
				maxScoredStop = cStop;
			}
		}
		
		return maxScoredStop;
	}
}
