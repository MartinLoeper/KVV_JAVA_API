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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.wordpress.loeper.kvv.base.TripSuggestion.Type;
import com.wordpress.loeper.kvv.base.exception.AmbiguousTargetException;
import com.wordpress.loeper.kvv.base.exception.NoTargetFoundException;
import com.wordpress.loeper.kvv.base.model.Locations;
import com.wordpress.loeper.kvv.base.model.Model;
import com.wordpress.loeper.kvv.base.model.Section;
import com.wordpress.loeper.kvv.base.model.Station;
import com.wordpress.loeper.kvv.base.model.Trip;
import com.wordpress.loeper.kvv.base.model.Trips;

/**
 * This class handles session management and network operations for the Base API.
 * Thus it has to parse a lot of plain HTML into object {@link com.wordpress.loeper.kvv.base.model.Model Models}.
 * @author Martin Löper
 * @version 1.0
 *
 */
public class ServiceHandler {
	/**
	 * The connection timeout for network operations.
	 */
	private static int mConnectTimeout = 20000;		// default is 20sec
	/**
	 * the number of retries for network requests.
	 */
	private static int mNumRetries = 10;			// default is 10 retries
	/**
	 * The session to operate on. It will be automatically added during TripRequest preparation.
	 */
	private Session mSession = null;
	/**
	 * The language which will be requested. Default is English.
	 */
	private Language mLanguage = Language.ENGLISH;
	/**
	 * The KVV key for search operations.
	 */
	public static final String KVV_SEARCH_ENGINE_KEY_CONST = "eID";
	/**
	 * The corresponding value for the {@link #KVV_SEARCH_ENGINE_KEY_CONST KVV key} {@value #KVV_SEARCH_ENGINE_KEY_CONST}.
	 */
	public static final String KVV_SEARCH_ENGINE_VALUE_CONST = "ix_efa_searchService";
	
	/**
	 * The search operations which can be requested from the KVV server.
	 * @author Martin Löper
	 * @version 1.0
	 */
	public enum Action {
		STOP_SEARCH("stopRequest"),
		TRIP_SEARCH("tripRequest"),
		TRIP_SCROLL("tripScroll");
		
		private String mAction;
		
		private Action(String pAction){
			this.mAction = pAction;
		};
		
		public String get() {
			return this.mAction;
		}
	}
	
	/**
	 * The travel types for a TripRequest. It repesents the context in which a timestamp is used.
	 * It does affect the KVV's search algorithm.
	 * @author Martin Löper
	 * @version 1.0
	 */
	public enum TravelType {
		/**
		 * Indicates that the given timestamp should be used as departure time.
		 */
		DEPARTURE("dep"),
		/**
		 * Indicates that the given timestamp should be used as arrival time.
		 */
		ARRIVAL("arr");
		
		private String mTravelType;
		
		private TravelType(String pTravelType) {
			this.mTravelType = pTravelType;
		};
		
		public String get() {
			return this.mTravelType;
		}
	}
	
	/**
	 * The amount of time a user needs afoot.
	 * It is unknown how the KVV uses this value for computation.
	 * @author Martin Löper
	 * @version 1.0
	 */
	public enum TravelChangeSpeed {
		SLOW("slow"),
		NORMAL("normal"),
		FAST("fast");
		
		private String mTravelChangeSpeed;
		
		private TravelChangeSpeed(String pTravelChangeSpeed) {
			this.mTravelChangeSpeed = pTravelChangeSpeed;
		};
		
		public String get() {
			return this.mTravelChangeSpeed;
		}
	}
	
	/**
	 * The language in which the KVV server should respond.
	 * @author Martin Löper
	 * @version 1.0
	 */
	public enum Language {
		GERMAN(""),
		ENGLISH("1"),
		FRENCH("2"),
		ITALIAN("3"),
		TURKISH("4");
		
		private String mLanguage;
		
		private Language(String pLanguage) {
			this.mLanguage = pLanguage;
		};
		
		public String get() {
			return this.mLanguage;
		}
	}
	
	/**
	 * Creates a SessionHandler with default session.
	 */
	public ServiceHandler() {
		this.mSession = new Session();	// create default session
	}
	
	/**
	 * Set the session to be used for future requests.
	 * @param pSession the session to use
	 */
	@Nonnull
	public void setSession(Session pSession) {
		this.mSession = pSession;
	}
	
	/**
	 * Returns the currently used session.
	 * @return the currently used session
	 */
	public Session getSession() {
		return this.mSession;
	}
	
	/**
	 * Set the language which will be requested from the KVV server.
	 * @param pLanguage the language being requested
	 */
	@Nonnull
	public void setLanguage(Language pLanguage) {
		this.mLanguage = pLanguage;
	}
	
	/**
	 * Returns {@link com.wordpress.loeper.kvv.base.model.Location Locations} with a similar name compared to the given String.<br>
	 * This method provides similar functionality to the Live API method {@link #searchStopByName(String)}.
	 * {@link #searchStopByName(String)} as part of the Live endpoint is marginal faster than this method and returns a different container: 
	 * {@link com.wordpress.loeper.kvv.live.model.Stops Stops}.
	 * @param pName the searchphrase for the {@link com.wordpress.loeper.kvv.base.model.Location Location}'s name attribute
	 * @return an object ({@link com.wordpress.loeper.kvv.base.model.Locations Locations}) acting as container for {@link com.wordpress.loeper.kvv.base.model.Location Location} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 */
	@Nonnull
	public Locations autocomplete(String pName) throws IOException {		// alias for searchStopsByName (because of same name in kvv life)
		return this.searchStopsByName(pName);
	}
	
	/**
	 * Alias for {@link #autocomplete(String)}.
	 * @see #autocomplete(String)
	 */
	@Nonnull
	public Locations searchStopsByName(String pName) throws IOException {
		HashMap<String, String> parameters = new HashMap<>();
		parameters.put("ix_action", Action.STOP_SEARCH.get());
		parameters.put("name_sf", pName);
		parameters.put(KVV_SEARCH_ENGINE_KEY_CONST, KVV_SEARCH_ENGINE_VALUE_CONST);

		return Network.getJSON(Locations.class, parameters);
	}
	
	/**
	 * Search for routes between two locations.
	 * Parses the plain HTML code from KVV server.
	 * Has big potential to fail someday when KVV changes some HTML IDs or output style.
	 * @param pTripRequest the request containing information about time, location, etc.
	 * @return an object ({@link com.wordpress.loeper.kvv.base.model.Trips Trips}) acting as container for {@link com.wordpress.loeper.kvv.base.model.Trip Trip} objects.
	 * @throws IOException thrown if an error occurs which is caused by networking issues
	 * @throws AmbiguousTargetException thrown if route could not be determined
	 */
	@Nonnull
	public Trips getTrips(TripRequest pTripRequest) throws IOException, AmbiguousTargetException {
		String response = Network.get(pTripRequest.setLanguage(this.mLanguage).setSession(this.mSession).getHttpParameters());
		/* workaround unicode problems */
		response = response.replaceAll("&#150;", "-");
		
		Document doc = Jsoup.parse(response);
		Trips trips = new Trips(this, pTripRequest);
		
		// System.out.println(response);	// IMPORTANT FOR DEBUGGING!!
		
		/* Update Session */
		Element sessionIdContainer = doc.getElementById("sessionID");
		Element requestIdContainer = doc.getElementById("requestID");
		
		if(sessionIdContainer != null && requestIdContainer != null)
			this.mSession = new Session(sessionIdContainer.val(), requestIdContainer.val());
		
		/* check if we need to provide more precise data */
		if(response.trim().contains("<!-- ix_chooser.html  DO NOT CHANGE THIS FIRST LINE: parsed in javascript !!-->")) {
			// System.out.println("more needed");
			HashMap<Type, List<TripSuggestion>> pTripSuggestionContainer = new HashMap<>();
			
			List<TripSuggestion> origin_stations = new ArrayList<>();
			for(Element cElement : doc.getElementById("ix_origin").getElementsByTag("option")) {
				origin_stations.add(new TripSuggestion(cElement.val(), cElement.html().trim().replaceAll("\\[(.*?)\\]", ""), cElement.html().trim()));
			}
			pTripSuggestionContainer.put(TripSuggestion.Type.ORIGIN, origin_stations);
			
			List<TripSuggestion> destination_stations = new ArrayList<>();
			for(Element cElement : doc.getElementById("ix_destination").getElementsByTag("option")) {
				destination_stations.add(new TripSuggestion(cElement.val(), cElement.html().trim().replaceAll("\\[(.*?)\\]", ""), cElement.html().trim()));
			}
			pTripSuggestionContainer.put(TripSuggestion.Type.DESTINATION, destination_stations);
			
			throw new AmbiguousTargetException(pTripSuggestionContainer, this, pTripRequest);
		}
		else if(response.trim().contains("<!-- ix_trips.html  DO NOT CHANGE THIS FIRST LINE-->")) {
			// System.out.println("no more needed");
			Elements tripObjs = doc.select("ul#fahrten > span");
			
			if(tripObjs.size() == 0)
				throw new NoTargetFoundException(this, pTripRequest);
						
			for(Element cElement : tripObjs) {
				String name = cElement.select(".tab1").first().html();
				String interval = cElement.select(".tab2 strong").get(0).html();
				String duration = cElement.select(".tab4").get(0).html();
				duration = duration.substring(duration.length() - 5);
				String with = cElement.select(".tab3").first().html();
				String changes = cElement.select(".tab4").get(1).html();
				List<Section> sections = new ArrayList<Section>();
				
				for(Element cSection : cElement.select("ul.fahrtliste")) {
					Element details = cSection.select("li").first();
					List<String> infos = new ArrayList<String>();
					for(String cDetail : details.html().split("<br>")) {
						String cleared = cDetail.replaceAll("\\<.*?\\>", "").trim();
						if(!cleared.equals("")) {
							infos.add(cleared);
						}
					}
					Element beginning = cSection.select("li.fahrtenlist").first();
					String origin_time = beginning.select(".tab1 strong").html();
					String origin_place = beginning.select(".tab3 strong").first().html();
					
					Element end = cSection.select("li.fahrtenlistende").first();
					String destination_time = end.select(".tab1 strong").first().html();
					String destination_place = end.select(".tab3 strong").first().html();
					
					Elements stationObj = cSection.select("span.toggle li");
					List<Station> stations = new ArrayList<Station>();
					if(stationObj != null) {
						for(Element cStation : stationObj) {
							String time = cStation.select("span").first().html();
							String stationName = cStation.html().replaceAll("\\<.*?\\>", "").replace(time, "").trim();
							stations.add(new Station(stationName, time));
						}
					}
					
					sections.add(new Section(infos, origin_time, origin_place, destination_time, destination_place, stations));
				}
				trips.add(new Trip(name, interval, duration, with, changes, sections, pTripRequest.getTimestamp()));
			}
		}
		else {
			throw new NoTargetFoundException(this, pTripRequest);
		}
		
		return trips;
	}
	
	/**
	 * This class is responsible for the data transport through the network.
	 * @author Martin Löper
	 * @version 1.0
	 */
	private static class Network {
		/**
		 * The KVV's endpoint URL.
		 */
		private final static String BASE_URL = "http://info.kvv.de/index.php";
		/**
		 * The HTTP Client Object.
		 */
		private static final NetHttpTransport mClient = new NetHttpTransport();
		/**
		 * The randomizer used to ensure a fresh (live) response which is not cached.
		 */
		private static final Random mRandomizer = new Random();
		/**
		 * HTTP Request Factory Object is responsible for HTTP response management etc.
		 */
		private static final HttpRequestFactory mFactory = Network.mClient.createRequestFactory(new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) {
				request.setParser(new JsonObjectParser(new GsonFactory()));
				request.setConnectTimeout(mConnectTimeout);
				request.setNumberOfRetries(mNumRetries);
			}
        });
		
		/**
		 * Requests the content at given URL relative to {@link #BASE_URL} with no additional post parameters.
		 * @param pUrl the url to request
		 * @return the content as String
		 * @throws IOException thrown if an error occurs which is caused by networking issues
		 */
		@Nonnull
		public static String get(String pUrl) throws IOException {
			return get(pUrl, new HashMap<String, String>());
		}
		
		/**
		 * Requests the content at <b>the base url</b> with given additional post parameters.
		 * @param pParameters the parameters to add
		 * @return the content as String
		 * @throws IOException thrown if an error occurs which is caused by networking issues
		 */
		@Nonnull
		public static String get(HashMap<String, String> pParameters) throws IOException {
			return get("", pParameters);	// use base url only
		}
		
		/**
		 * Converts the provided parameters into an encoded String format.
		 * @param pParameters the parameters to add
		 * @return the parameters properly encoded as a string
		 * @throws UnsupportedEncodingException should not be thrown here...
		 */
		@Nonnull
		private static String parameterToString(HashMap<String, String> pParameters) throws UnsupportedEncodingException {
			String parameterString = "";
			Iterator<Entry<String, String>> it = pParameters.entrySet().iterator();
			while(it.hasNext()) {
				Entry<String, String> cEntry = it.next();
				parameterString += URLEncoder.encode(cEntry.getKey(), "UTF-8").replace("+", "%20").replaceAll("%3A", ":").replaceAll("%2C", ",").replaceAll("%2F", "/")
								+ "=" + URLEncoder.encode(cEntry.getValue(), "UTF-8").replace("+", "%20").replaceAll("%3A", ":").replaceAll("%2C", ",").replaceAll("%2F", "/");
				if(it.hasNext())
					parameterString += "&";
			}
			parameterString += "&_=" + mRandomizer.nextInt();
			
			// System.out.println(parameterString);
			return parameterString;
		}
		
		/**
		 * Requests the content at given url relative to {@link #BASE_URL} and additional parameters.
		 * @param pUrl the url relative to base url
		 * @param pParameters the parameters to add
		 * @return the content as String
		 * @throws IOException thrown if an error occurs which is caused by networking issues
		 */
		@Nonnull
		public static String get(String pUrl, HashMap<String, String> pParameters) throws IOException {
			HttpContent httpContent = ByteArrayContent.fromString(null, parameterToString(pParameters));
			return mFactory.buildPostRequest(new GenericUrl(BASE_URL + pUrl), httpContent).execute().parseAsString();
		}
		
		/**
		 * Requests the content at base with additional parameters.
		 * Furthermore it parses the content into a given data model.
		 * @param mDataModelClass the class to use as model
		 * @param pParameters the parameters to add
		 * @return an instance of the model which was specified as mDataModelClass
		 * @throws IOException thrown if an error occurs which is caused by networking issues
		 */
		@Nonnull
		public static <T extends Model> T getJSON(Class<T> mDataModelClass, HashMap<String, String> pParameters) throws IOException {
			return getJSON(mDataModelClass, "", pParameters);	// use base url only
		}
		
		/**
		 * Requests the content at given url relative to {@link #BASE_URL} and additional parameters.
		 * Furthermore it parses the content into a given data model.
		 * @param mDataModelClass the class to use as model
		 * @param pUrl the url relative to base url
		 * @param pParameters the parameters to add
		 * @return an instance of the model which was specified as mDataModelClass
		 * @throws IOException thrown if an error occurs which is caused by networking issues
		 */
		@Nonnull
		public static <T extends Model> T getJSON(Class<T> mDataModelClass, String pUrl, HashMap<String, String> pParameters) throws IOException {
			HttpContent httpContent = ByteArrayContent.fromString(null, parameterToString(pParameters));
			return mFactory.buildPostRequest(new GenericUrl(BASE_URL + pUrl), httpContent).execute().parseAs(mDataModelClass);
		}
	}
}
