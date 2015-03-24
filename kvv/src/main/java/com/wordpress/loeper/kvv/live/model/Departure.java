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
package com.wordpress.loeper.kvv.live.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.clutch.dates.StringToTime;
import com.google.api.client.util.Key;

/**
 * This class represents a departure.
 * It contains information when a vehicle departs and in which direction in moves.
 * @author Martin Löper
 * @version 1.0
 */
public class Departure implements Model {
	  @Key("route")
	  private String route;
	  
	  @Key("destination")
	  private String destination;
	  
	  @Key("direction")
	  private String direction;	
	  
	  @Key("time")
	  private String time;	
	  
	  @Key("vehicleType")
	  @Deprecated
	  private Object vehicleType;
	  
	  @Key("lowfloor")
	  private boolean lowfloor;
	  	  
	  @Key("realtime")
	  @Deprecated
	  private boolean realtime;
	  
	  @Key("traction")
	  @Deprecated
	  private int traction;
	  
	  @Key("stopPosition")
	  private int stopPosition;
	  
	  /**
	   * Returns the route label.
	   * @return the route label, e.g. S1
	   */
	  public String getRoute() {
		  return this.route;
	  }
	  
	  public VehicleType getType() {
		  if(getRoute().startsWith("S")) {
			  return VehicleType.S;
		  }
		  
		  try {
			  int route = Integer.parseInt(getRoute());
			  if(route < 10) {
				  return VehicleType.TRAM;
			  }
			  else {
				  return VehicleType.BUS;
			  }
		  }
		  catch(NumberFormatException e) {
			  return VehicleType.UNKNOWN;
		  }
	  }
	  
	  /**
	   * Represents the type of vehicle.
	   * @author Martin Löper
	   * @version 1.0	   *
	   */
	  public static enum VehicleType {
		  S, TRAM, BUS, UNKNOWN;
	  }
	  
	  /**
	   * Returns the destination.
	   * @return the destination, e.g. Karlsruhe Hauptbahnhof
	   */
	  public String getDestination() {
		  return this.destination;
	  }
	  
	  /**
	   * Returns the direction.
	   * @return the direction, either 1 or 2
	   */
	  public int getDirection() {
		  return Integer.valueOf(this.direction);
	  }
	  
	  /**
	   * Returns the time for the departure as unix timestamp in seconds.
	   * @return the time when the vehicle departs
	   */
	  public long getTime() {
		  long newTime = 0;		// new unix timestamp value

		  if(this.time.equalsIgnoreCase("sofort") || this.time.equals("0")) {
			  newTime = (new Date().getTime() / 1000);
		  }
		  else if(this.time.substring(this.time.length() - 1, this.time.length()).toLowerCase().equals("h")) {
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(new Date()); // set to current timestamp
			  cal.add(Calendar.HOUR_OF_DAY, Integer.valueOf(this.time.substring(0, this.time.length() - 1).trim()));
			  newTime = (cal.getTime().getTime() / 1000);
		  }
		  else if(this.time.substring(this.time.length() - 3, this.time.length()).toLowerCase().equals("min")) {
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(new Date()); // set to current timestamp
			  cal.add(Calendar.MINUTE, Integer.valueOf(this.time.substring(0, this.time.length() - 3).trim()));
			  newTime = (cal.getTime().getTime() / 1000);			  
		  }
		  else {
			  SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
			  Calendar currentTimeCalendar = Calendar.getInstance();
			  currentTimeCalendar.setTime(new Date());
				
			  dateFormat.setCalendar(currentTimeCalendar);
			  StringToTime checkVal = new StringToTime(this.time, new Date());
				
			  if(checkVal.getHours() < currentTimeCalendar.get(Calendar.HOUR_OF_DAY)
						|| (checkVal.getHours() <= currentTimeCalendar.get(Calendar.HOUR_OF_DAY) 
						&& (checkVal.getMinutes() < currentTimeCalendar.get(Calendar.MINUTE)))) {

				  newTime = (new StringToTime(this.time, new StringToTime("tomorrow")).getTime() / 1000);
			  }
			  else {
				  newTime = (checkVal.getTime() / 1000);
			  }
		  }
		  
		  return newTime;
	  }
	  
	  @Deprecated
	  public Object getVehicleType() {
		  return this.vehicleType;
	  }
	  
	  /**
	   * Returns whether the vehicle has a lowfloor.
	   * @return true if lowfloor, false otherwise
	   */
	  public boolean isLowfloor() {
		  return this.lowfloor;
	  }
	  
	  @Deprecated
	  public boolean isRealtime() {
		  return this.realtime;
	  }
	  
	  @Deprecated
	  public int getTraction() {
		  return this.traction;
	  }
	  
	  /**
	   * Returns the stop position number on the route.
	   * @return the stop position number
	   */
	  public int getStopPosition() {
		  return this.stopPosition;
	  }
	  
	  @Override
	  public String toString() {
		  return "route:" + getRoute() + ",destination:" + getDestination() + ",direction:" + getDirection()+",time:" + getTime() + ",lowfloor:"
				  +isLowfloor() + ",stopPosition:" +getStopPosition();
	  }
}
