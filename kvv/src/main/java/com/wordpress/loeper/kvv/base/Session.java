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

import javax.annotation.Nonnull;

/**
 * This class represents a container for all session data which the KVV server needs among single requests.
 * @author Martin Löper
 * @version 1.0
 */
public class Session {
	/**
	 * The session identifier.
	 */
	public final String SESSION_ID;
	/**
	 * The request identifier.
	 */
	public final String REQUEST_ID;
	
	/**
	 * Create a new Session with default values.
	 */
	public Session() {
		this("0", "0");
	}
	
	/**
	 * Create a new Session with given values.
	 * @param pSessionId the session id to use
	 * @param pRequestId the request id to use
	 */
	@Nonnull
	public Session(String pSessionId, String pRequestId) {
		this.SESSION_ID = pSessionId;
		this.REQUEST_ID = pRequestId;
	} 
	
	/**
	 * Returns true if this session object is the default one.
	 * Using this method one can find out if a request was already made.
	 * @return true if session is default one (created by default constructor and not modified by setters), false otherwise
	 */
	public boolean isDefault() {
		return (this.SESSION_ID.isEmpty() && this.REQUEST_ID.isEmpty()) ? true : false;
	}
	
	@Override
	public boolean equals(Object pOtherSession) {
		if(!(pOtherSession instanceof Session))
			return false;
		
		Session otherSession = (Session) pOtherSession;
		
		return (this.SESSION_ID.equals(otherSession.SESSION_ID) 
				&& this.REQUEST_ID.equals(otherSession.REQUEST_ID)) ? true : false;
	}
}
