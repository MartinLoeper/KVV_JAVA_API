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

import com.google.api.client.util.Key;

/**
 * A logical location in the KVV structure.
 * Matches with location names in the reality.
 * @author Martin Löper
 * @version 1.0
 */
public class Location implements Model {
	  @Key("name")
	  private String name;
	  
	  @Key("anyType")
	  private String type;
	  
	  /**
	   * Indicates the type of location.
	   * This is used to group the available locations.
	   * @author Martin Löper
	   * @version 1.0
	   */
	  public enum Type {
		  /**
		   * A specific tram or bus stop.
		   * Means of transport have to start and end at locations with this type.
		   * So even if the user selects another type of location, the trip will always start at a STOP.
		   */
		  STOP("stop"),
		  /**
		   * A street name or other specific location that has a name.
		   */
		  LOCATION("loc"),
		  /**
		   * A wider area such as a city or district name.
		   */
		  POINT("poi");
		  
		  private String mType;
		  
		  private Type(String pType) {
			  this.mType = pType;
		  };
		  
		  public String get() {
			  return this.mType;
		  }
	  }
	  
	  /**
	   * Returns the name of the location.
	   * @return
	   */
	  public String getName() {
		  return this.name;
	  }
	  
	  /**
	   * Returns the type of this location.
	   * Must be one of the {@link Type} constants.
	   * @return the type of location
	   */
	  public String getType() {
		  return this.type;
	  }
	  
	  @Override
	  public String toString() {
		  return "name=" + getName() + ",type=" + getType();
	  }
}