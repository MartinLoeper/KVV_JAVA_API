/*
 * This file is part of the unofficial Java KVV API.
 * 
 * Copyright (c) 2014, Martin L�per
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
package com.wordpress.loeper.kvv.live.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.util.Key;

public class Departures implements Model {
	@Key("departures")
	private List<Departure> departures = new ArrayList<>();
	
	@Key("timestamp")
	private String timestamp;
	
	@Key("stopName")
	private String stopName;

	/**
	 * Returns the departures.
	 * @return the departures
	 */
	public List<Departure> getDepartures() {
		return this.departures;
	}
	
	/**
	 * Returns the timestamp which represents the creation time of this dataset.
	 * @return the data creation timestamp
	 */
	public long getTimestamp() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormat.parse(this.timestamp).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;				// -1 indicates failure
		}
	}
	
	/**
	 * Returns the stop name for which the dataset was created.
	 * @return the stop name
	 */
	public String getStopName() {
		return this.stopName;
	}
	
	public String toString() {
		String out = "";
		int i = 0;
		for(Departure cDeparture : this.getDepartures()) {
			if(i > 0)
				out += ",";
			
			out += cDeparture.toString();
			i++;
		}
		
		return out;
	}
}
