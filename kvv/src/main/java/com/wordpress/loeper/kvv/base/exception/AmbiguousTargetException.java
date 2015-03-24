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
package com.wordpress.loeper.kvv.base.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import com.wordpress.loeper.kvv.base.ServiceHandler;
import com.wordpress.loeper.kvv.base.TripRequest;
import com.wordpress.loeper.kvv.base.TripRequestPrecise;
import com.wordpress.loeper.kvv.base.TripSuggestion;
import com.wordpress.loeper.kvv.base.model.Trips;
import com.wordpress.loeper.kvv.helper.SimilarString;

/**
 * Indicates that the previous request lacks information or was ambiguous.
 * This exception is usually thrown if the KVV could not properly select origin and destination station.
 * The most causes are similar station names.
 * Sometimes if a user does a typo and no station could be found, the subclass {@link NoTargetFoundException} is thrown.
 * @author Martin Löper
 * @version 1.0
 */
public class AmbiguousTargetException extends Exception {
	private static final long serialVersionUID = 4808514137296336921L;
	/**
	 * A list of suggestion for the origin station.
	 */
	private List<TripSuggestion> mOriginSuggestions;
	/**
	 * A list of suggestions for the destination station.
	 */
	private List<TripSuggestion> mDestinationSuggestion;
	/**
	 * A reference to the calling ServiceHandler.
	 */
	private ServiceHandler mServiceHandler;
	/**
	 * A reference to the used TripRequest.
	 */
	private TripRequest mTripRequest;	
	
	/**
	 * Creates a new Exception.
	 * @param pTripSuggestionContainer a container with origin and destination station suggestions
	 * @param pServiceHandler the ServiceHandler which is responsible for the request
	 * @param pTripRequest the TripRequest which was used for the request
	 */
	@Nonnull
	public AmbiguousTargetException(HashMap<TripSuggestion.Type, List<TripSuggestion>> pTripSuggestionContainer, ServiceHandler pServiceHandler, TripRequest pTripRequest) {
		super("The destination or origin parameter was not unique. The user has to choose one possibility now.");
		
		this.mOriginSuggestions = pTripSuggestionContainer.get(TripSuggestion.Type.ORIGIN);
		this.mDestinationSuggestion = pTripSuggestionContainer.get(TripSuggestion.Type.DESTINATION);
		this.mServiceHandler = pServiceHandler;
		this.mTripRequest = pTripRequest;
	}
	
	/**
	 * Returns the origin station suggestions
	 * @return the origin station suggestions
	 */
	public List<TripSuggestion> getOriginStations() {
		return this.mOriginSuggestions;
	}
	
	/**
	 * Returns the destination station suggestions
	 * @return the destination station suggestions
	 */
	public List<TripSuggestion> getDestinationStations() {
		return this.mDestinationSuggestion;
	}
	
	/**
	 * Retries the request using the given suggestions.
	 * @param pOriginSuggestion the suggestion used as new origin
	 * @param pDestinationSuggestion the suggestion used as new destination
	 * @return an object ({@link com.wordpress.loeper.kvv.base.model.Trips Trips}) acting as container for {@link com.wordpress.loeper.kvv.base.model.Trip Trip} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws AmbiguousTargetException thrown if route could not be determined
	 */
	@Nonnull
	public Trips utilize(TripSuggestion pOriginSuggestion, TripSuggestion pDestinationSuggestion) throws IOException, AmbiguousTargetException {
		this.mTripRequest.setOrigin(pOriginSuggestion.getHtmlValue());
		this.mTripRequest.setDestination(pDestinationSuggestion.getHtmlValue());
		
		return this.mServiceHandler.getTrips(new TripRequestPrecise(this.mTripRequest, pOriginSuggestion.getValue(), pDestinationSuggestion.getValue()));
	}
	
	/**
	 * Returns an instance of a new TripRequest with additional parameters, called {@link TripRequestPrecise}.
	 * @param pOriginSuggestion the suggestion used as new origin 
	 * @param pDestinationSuggestion the suggestion used as new destination
	 * @return a more precise TripRequest object
	 */
	@Nonnull
	public TripRequestPrecise getModifiedTripRequest(TripSuggestion pOriginSuggestion, TripSuggestion pDestinationSuggestion) {
		this.mTripRequest.setOrigin(pOriginSuggestion.getHtmlValue());
		this.mTripRequest.setDestination(pDestinationSuggestion.getHtmlValue());		
		
		return new TripRequestPrecise(this.mTripRequest, pOriginSuggestion.getValue(), pDestinationSuggestion.getValue());
	}
	
	/**
	 * Returns true if either origin or destination suggestions are not available.
	 * This is usually the case if this is a {@link NoTargetFoundException} (subclass).
	 * @return
	 */
	public boolean isTargetAvailable() {
		return (this.mOriginSuggestions == null || this.mDestinationSuggestion == null 
				|| this.mOriginSuggestions.size() == 0 && this.mDestinationSuggestion.size() == 0) ? false : true;
	}
	
	/**
	 * If multiple suggestions are available, select the one which seems to be the best (according to the user's mind).
	 * @return new TripRequest with precise information that should most probably match the user's intention
	 */
	public TripRequestPrecise getMostProbableMatch() {
		if(!this.isTargetAvailable())
			return null;	// TODO maybe throw exception?
		
		TripSuggestion maxScoredOrigin = this.mOriginSuggestions.get(0);
		int maxScoreOrigin = 0;
		for(TripSuggestion cSuggestion : this.mOriginSuggestions) {
			int cScore = new SimilarString(cSuggestion.getHtmlValue(), this.mTripRequest.getOrigin()).similar();
			if(cScore > maxScoreOrigin) {
				maxScoreOrigin = cScore;
				maxScoredOrigin = cSuggestion;
			}
		}
		
		TripSuggestion maxScoredDestination = mDestinationSuggestion.get(0);
		int maxScoreDestination = 0;
		for(TripSuggestion cSuggestion : this.mDestinationSuggestion) {
			int cScore = new SimilarString(cSuggestion.getHtmlValue(), this.mTripRequest.getDestination()).similar();
			if(cScore > maxScoreDestination) {
				maxScoreDestination = cScore;
				maxScoredDestination = cSuggestion;
			}
		}
		
		return this.getModifiedTripRequest(maxScoredOrigin, maxScoredDestination);	
	}
}
