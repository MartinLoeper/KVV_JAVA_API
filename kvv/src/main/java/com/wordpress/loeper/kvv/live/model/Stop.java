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

import com.google.api.client.util.Key;

/**
 * A stop in the KVV structure.
 * Matches with location names in the reality.
 * @author Martin Löper
 * @version 1.0
 */
public class Stop implements Model {
	  @Key
	  private String id;
	  
	  @Key
	  private String name;
	  
	  @Key
	  private float lon;
	  
	  @Key
	  private float lat;
	  
	  /**
	   * Returns the internal KVV id for this stop.
	   * @return the kvv id of the stop
	   */
	  public String getId() {
		  return this.id;
	  }
	  
	  /**
	   * Returns the name of this stop.
	   * @return the name of the stop
	   */
	  public String getName() {
		  return this.name;
	  }
	  
	  /**
	   * Returns the longitude of this stop.
	   * @return the longitude
	   */
	  public float getLongitude() {
		  return this.lon;
	  }
	  
	  /**
	   * Returns the latitude of this stop
	   * @return the latitude
	   */
	  public float getLatitude() {
		  return this.lat;
	  }
	  
	  @Override
	  public String toString() {
		  return "id:" + getId() + ",name:" + getName() + ",longitude:" + getLongitude() + ",latitude:" + getLatitude();
	  }
}