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

import java.util.HashMap;
import java.util.List;

import com.wordpress.loeper.kvv.base.ServiceHandler;
import com.wordpress.loeper.kvv.base.TripRequest;
import com.wordpress.loeper.kvv.base.TripSuggestion;
import com.wordpress.loeper.kvv.base.TripSuggestion.Type;

/**
 * Special case of ambiguous target exception when no destination or origin station suggestion is available.
 * It is also thrown to indicate that there is no way to determine a route between two locations.
 * Sometimes this might occur if a route is traveled by not much trams and no connection is available at night.
 * @author Martin Löper
 * @version 1.0
 */
public class NoTargetFoundException extends AmbiguousTargetException {
	private static final long serialVersionUID = 6573268883904784354L;

	/**
	 * Create a new Exception.
	 * @param pServiceHandler the ServiceHandler which is responsible for the request
	 * @param pTripRequest the TripRequest which was used for the request
	 */
	public NoTargetFoundException(ServiceHandler pServiceHandler, TripRequest pTripRequest) {
		super(new HashMap<Type, List<TripSuggestion>>(), pServiceHandler, pTripRequest);
	}

}
