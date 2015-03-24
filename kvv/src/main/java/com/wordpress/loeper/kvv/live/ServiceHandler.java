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
package com.wordpress.loeper.kvv.live;

import java.io.IOException;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.wordpress.loeper.kvv.live.model.Departures;
import com.wordpress.loeper.kvv.live.model.GeoStops;
import com.wordpress.loeper.kvv.live.model.Model;
import com.wordpress.loeper.kvv.live.model.Stop;
import com.wordpress.loeper.kvv.live.model.Stops;

public final class ServiceHandler {
	private static ServiceHandler sServiceHandler = null;
	private static int mConnectTimeout = 20000;		// default is 20sec
	private static int mNumRetries = 10;			// default is 10 retries
	private static int mMax = 10;
	
	private ServiceHandler() {}
	
	public static ServiceHandler create() {
		if (sServiceHandler == null)
			sServiceHandler = new ServiceHandler();
		
		return sServiceHandler; 
	}
	
	public static void setMaxInfos(int pMax) {
		mMax = pMax;
	}
	
	@Nonnull		
	public Stops searchStopsByName(String pStopName) throws IOException {
		return Network.getJSON(Stops.class,"stops/byname/" + pStopName);
	}
	
	@Nonnull
	public Stop getStopById(String pStopId) throws IOException {
		return Network.getJSON(Stop.class, "stops/bystop/" + pStopId);
	}
	
	@Nonnull
	public GeoStops searchStopByGeoLocation(double pLatitude, double pLongitude) throws IOException {
		return Network.getJSON(GeoStops.class, "stops/bylatlon/" + pLatitude + "/" + pLongitude);
	}
	
	@Nonnull
	public Departures getDeparturesByStopId(String pStopId) throws IOException {
		return Network.getJSON(Departures.class, "departures/bystop/" + pStopId);
	}
	
	@Nonnull
	public Departures getDeparturesByStopIdAndRoute(String pStopId, String pRoute) throws IOException {
		return Network.getJSON(Departures.class, "departures/byroute/" + pRoute + "/" +pStopId);
	}
		
	private static class Network {
		private static final String API_KEY = "377d840e54b59adbe53608ba1aad70e8";
		private final static String BASE_URL = "http://live.kvv.de/webapp/";
		private static final NetHttpTransport mClient = new NetHttpTransport();
		private static final Random mRandomizer = new Random();
		private static final HttpRequestFactory mFactory = Network.mClient.createRequestFactory(new HttpRequestInitializer() {
            @Override
          public void initialize(HttpRequest request) {
            	request.setParser(new JsonObjectParser(new GsonFactory()));
            	request.setConnectTimeout(mConnectTimeout);
            	request.setNumberOfRetries(mNumRetries);
            }
        });;
		
    	@Nonnull
		public static <T extends Model> T getJSON(Class<T> mDataModelClass, String pUrl) throws IOException {
    		String maxInfos = "";
    		if(mMax > 0) {
    			maxInfos = "&maxInfos=" + mMax;
    		}
			return mFactory.buildGetRequest(new GenericUrl(BASE_URL + pUrl + "?key=" + API_KEY + "&_=" + mRandomizer.nextInt() + maxInfos)).execute().parseAs(mDataModelClass);
		}
	}
}
