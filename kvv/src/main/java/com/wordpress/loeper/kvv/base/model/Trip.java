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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.wordpress.loeper.external.StringToTime;


/**
 * This class represents a trip.
 * A trip consists of multiple sections that suggest how to get to the destination.
 * @author Martin Löper
 * @version 1.0
 */
public class Trip implements Model {
	private String mName;
	private String[] mInterval;
	private String mDuration;
	private String[] mWith;
	private int mChanges;
	private List<Section> mSections;
	private long mTripRequestTimestamp;
	
	public Trip(String pName, String pInterval, String pDuration, String pWith, String pChanges, List<Section> pSections,
			long pTripRequestTimestamp) {
		this.mName = pName;
		String[] interval = pInterval.split("-");
		this.mInterval = new String[]{ interval[0], interval[1] };
		this.mDuration = pDuration;
		String[] tWith = pWith.split("with:");
		this.mWith = (tWith.length > 1) ? tWith[1].trim().split(",") : new String[]{};
		/* trim each value */
		for(int i = 0; i < mWith.length; i++) {
			this.mWith[i] = this.mWith[i].trim();
		}
		this.mChanges = Integer.valueOf(pChanges.split("Changes: ")[1].trim());
		this.mSections = pSections;
		this.mTripRequestTimestamp = pTripRequestTimestamp;
	}
	
	/**
	 * Returns the name of the trip.
	 * This might useful for localization, but is otherwise useless (and therefore considered to be removed in the future)...
	 * @return the label of the trip (i.e. 1.Journey)
	 */
	@Deprecated
	public String getName() {
		return this.mName;
	}
	
	// TODO error because kvv does not specify the full date... an algorithm to determine is missing here!
	// problem: if time is 22:06 and current time is 22:07, the day of month will be increased by 1 :(((
	private long calculateUnixTimestamp(String pTimeAsString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Calendar currentTimeCalendar = Calendar.getInstance();
		currentTimeCalendar.setTime(new Date(mTripRequestTimestamp * 1000));
			
		dateFormat.setCalendar(currentTimeCalendar);
		StringToTime checkVal = new StringToTime(pTimeAsString, new Date(mTripRequestTimestamp * 1000));
		long newTime;
			
		if(checkVal.getHours() < currentTimeCalendar.get(Calendar.HOUR_OF_DAY)
					|| (checkVal.getHours() <= currentTimeCalendar.get(Calendar.HOUR_OF_DAY) 
					&& (checkVal.getMinutes() + 30 < currentTimeCalendar.get(Calendar.MINUTE)))) {	// TODO the +30 is the tolerance....

			newTime = (new StringToTime(pTimeAsString, new StringToTime("tomorrow", new Date(mTripRequestTimestamp * 1000))).getTime() / 1000);
		}
		else {
			newTime = (checkVal.getTime() / 1000);
		}
		
		return newTime;
	}
	
	@Override
	public boolean equals(Object pOtherObject) {
		if(!(pOtherObject instanceof Trip))
			return false;
		
		Trip otherTrip = (Trip) pOtherObject;
		
		return (Arrays.deepEquals(this.getIntervalAsString(), otherTrip.getIntervalAsString()) && this.getChanges() == otherTrip.getChanges()
				&& this.getDuration() == otherTrip.getDuration() && this.getSections().equals(otherTrip.getSections()) 
				&& Arrays.deepEquals(this.getWith(), otherTrip.getWith())) ? true : false;
	}
	
	/**
	 * Returns the interval in which the trip will be done.
	 * @return an array containing the start value at index 0 and the end value at index 1 (both are UNIX timestamps in seconds)
	 */
	public long[] getInterval() {
		return new long[]{ calculateUnixTimestamp(getIntervalAsString()[0]),
						   calculateUnixTimestamp(getIntervalAsString()[1])};
	}
	
	/**
	 * Returns the interval in the relative format as it is received by the KVV server.
	 * @see #getInterval()
	 * @return an array containing the start value at index 0 and the end value at index 1 (both are Strings formatted the following way: 1:22 - that means: HH:mm) 
	 */
	@Deprecated
	public String[] getIntervalAsString() {
		return this.mInterval;
	}
	
	/**
	 * Returns the duration of the trip.
	 * @see #getDuration()
	 * @return the duration of the trip in the following format: HH:mm
	 */
	@Deprecated
	public String getDurationAsString() {
		return this.mDuration;
	}
	
	/**
	 * Returns the duration of the trip in seconds
	 * @return the duration of the trip in seconds
	 */
	public long getDuration() {	// format: seconds
		String[] temp = getDurationAsString().split(":");
		return Integer.valueOf(temp[0]) * 3600 + Integer.valueOf(temp[1]) * 60;
	}
	
	/**
	 * Returns the routes which are used during the trip.
	 * @return the routes being used
	 */
	public String[] getWith() {
		return this.mWith;
	}
	
	/**
	 * Returns the number of changes which are needed for the trip
	 * @return number of changes
	 */
	public int getChanges() {
		return this.mChanges;
	}
	
	/**
	 * Returns the detailed {@link Section Sections} informations about vehicles and passed {@link Station Stations}.
	 * @return a list of Sections being used
	 */
	public List<Section> getSections() {
		return this.mSections;
	}
	
	@Override
	public String toString() {
		String out = "";
		int index = 0;
		for(Section cSection : this.mSections) {
			if(index > 0)
				out += ",";
			out += "[" + cSection.toString() + "]";
			index++;
		}
		
		String out2 = "";
		int index2 = 0;
		for(String cWith : this.mWith) {
			if(index2 > 0)
				out2 += ",";
			out2 += cWith;
			index2++;
		}
		
		return "name:" + getName() + ",interval:[" + getInterval()[0] + "," + getInterval()[1] + "],duration:" + getDuration() + ",with:[" 
			+ out2 + "],changes:" + getChanges() + ",sections:[" + out + "]";
	}
}
