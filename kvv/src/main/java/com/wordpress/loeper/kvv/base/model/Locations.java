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

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;

/**
 * A wrapper for multiple {@link Location} objects.
 * @author Martin Löper
 * @version 1.0
 */
public class Locations implements Model {
	@Key("stopFinder")
	private LocationInternal stopFinder;

	/**
	 * Returns a list of {@link Location Locations}.
	 * @return the list of Locations
	 */
	public List<Location> getLocations() {	// delegate...
		return this.stopFinder.getLocations();
	}
	
	@Override
	public String toString() {	// delegate...
		return this.stopFinder.toString();
	}
	
	/**
	 * This class should not be instantiated or used.
	 * It is really an internal of the Locations class, but cannot be made private due to google library restriction.
	 * @author Martin Löper
	 * @version 1.0
	 */
	public static class LocationInternal {
		@Key("points")
		private List<Location> points = new ArrayList<>();

		public List<Location> getLocations() {
			return this.points;
		}
		
		@Override
		public String toString() {
			String out = "";
			int i = 0;
			for(Location cStop : this.getLocations()) {
				if(i > 0)
					out += ",";
				
				out += cStop.toString();
				i++;
			}
			
			return out;
		}
	}
}
