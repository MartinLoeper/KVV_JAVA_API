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

import java.util.List;

/**
 * A section is a part of a trip where a person uses the same transport.
 * So a Section contains information about that transport and which stations it passes.
 * Caution! If there is no transport available, a <b>footpath</b> is used as a Section, too.
 * @author Martin Löper
 * @version 1.0
 */
public class Section implements Model {
	private List<String> mInfos;
	private String mOriginTime;
	private String mOriginPlace;
	private String mDestinationTime;
	private String mDestinationPlace;
	private List<Station> mStations;
	
	public Section(List<String> pInfos, String pOriginTime, String pOriginPlace, String pDestinationTime, String pDestinationPlace, List<Station> pStations) {
		this.mInfos = pInfos;
		this.mOriginTime = pOriginTime;
		this.mOriginPlace = pOriginPlace;
		this.mDestinationTime = pDestinationTime;
		this.mDestinationPlace = pDestinationPlace;
		this.mStations = pStations;
	}
	
	/**
	 * Additional information about the transport, such as direction or restricted mobility.
	 * @return a List of informative Strings
	 */
	public List<String> getInfos() {
		return this.mInfos;
	}
	
	/**
	 * Return the time when the section starts.
	 * @return start time of section
	 */
	public String getOriginTime() {
		return this.mOriginTime;
	}
	
	/**
	 * Return where the section starts.
	 * @return start location of section
	 */
	public String getOriginPlace() {
		return this.mOriginPlace;
	}
	
	/**
	 * Return the time when the section ends.
	 * @return end time of section
	 */
	public String getDestinationTime() {
		return this.mDestinationTime;
	}
	
	/**
	 * Return the location when the section ends.
	 * @return end location of section
	 */
	public String getDestinationPlace() {
		return this.mDestinationPlace;
	}
	
	/**
	 * Returns a list of {@link Station} objects.
	 * @return list of Stations
	 */
	public List<Station> getStations() {
		return this.mStations;
	}
	
	@Override
	public boolean equals(Object pOtherObject) {
		if(!(pOtherObject instanceof Section))
			return false;
		
		Section otherSection = (Section) pOtherObject;
		
		return (this.getDestinationPlace().equals(otherSection.getDestinationPlace())
				&& this.getDestinationTime().equals(otherSection.getDestinationTime())
				&& this.getOriginPlace().equals(otherSection.getOriginPlace())
				&& this.getOriginTime().equals(otherSection.getOriginTime())
				&& this.getStations().equals(otherSection.getStations())) ? true : false;
	}
	
	@Override
	public String toString() {
		String out = "";
		int index = 0;
		for(String cString : this.mInfos) {
			if(index > 0)
				out += ",";
			out += cString.toString();
			index++;
		}
		
		String out2 = "";
		int index2 = 0;
		for(Station cStation : this.mStations) {
			if(index2 > 0)
				out2 += ",";
			out2 += cStation.toString();
			index2++;
		}
		
		return "infos:[" + out + "],originTime:" + getOriginTime() + ",originPlace:" + getOriginPlace() + ",destinationTime:" + getDestinationTime() + ",destinationPlace:"
				+ getDestinationPlace() + ",stations:[" + out2 + "]";
	}
}
