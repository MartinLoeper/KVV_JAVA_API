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

/**
 * This class represents a physical station on the way of a section.
 * A time value is assigned to name. This is the time when the transport should pass/arrive at the station.
 * @author Martin Löper
 * @version 1.0
 */
public class Station implements Model {
	private String mName;
	private String mTime;
	
	public Station(String pName, String pTime) {
		this.mName = pName;
		this.mTime = pTime;
	}

	/**
	 * Returns the name of the station.
	 * @return the name
	 */
	public String getName() {
		return this.mName;
	}
	
	/**
	 * Returns the time when the transport passes this station.
	 * @return the time of pass
	 */
	public String getTime() {
		return this.mTime;
	}
	
	@Override
	public boolean equals(Object pOtherObject) {
		if(!(pOtherObject instanceof Station))
			return false;
		
		Station otherStation = (Station) pOtherObject;
		
		return (this.getName().equals(otherStation.getName())
				&& this.getTime().equals(otherStation.getTime())) ? true : false;
	}
	
	@Override
	public String toString() {
		return "name=" + getName() + ",time=" + getTime();
	}
}
