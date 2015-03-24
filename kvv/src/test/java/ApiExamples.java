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
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import com.wordpress.loeper.kvv.EFA;
import com.wordpress.loeper.kvv.base.TripRequest;
import com.wordpress.loeper.kvv.base.exception.AmbiguousTargetException;
import com.wordpress.loeper.kvv.base.model.Location;
import com.wordpress.loeper.kvv.base.model.Locations;
import com.wordpress.loeper.kvv.base.model.Trip;
import com.wordpress.loeper.kvv.base.model.Trips;
import com.wordpress.loeper.kvv.live.exception.StopNotFoundException;
import com.wordpress.loeper.kvv.live.model.Departure;
import com.wordpress.loeper.kvv.live.model.Departures;
import com.wordpress.loeper.kvv.live.model.GeoStop;
import com.wordpress.loeper.kvv.live.model.GeoStops;
import com.wordpress.loeper.kvv.live.model.Stop;
import com.wordpress.loeper.kvv.live.model.Stops;

public class ApiExamples {
	
	public static void main(String[] args) throws IOException, StopNotFoundException {
		EFA kvv = new EFA();
		Scanner reader = new Scanner(System.in);
		
		System.out.println("Origin: ");
		String origin = reader.nextLine();
		System.out.println("\nDestination: ");
		String destination = reader.nextLine();
		System.out.println("Requesting data, please wait....");
		
		Trips trips = null;
		
		TripRequest tripRequest = new TripRequest(origin, destination);
		try {
			trips = kvv.getTrips(tripRequest);
		} catch (AmbiguousTargetException e) {
			if(!e.isTargetAvailable()) {
				System.err.println("No target found!!");
				return;
			}
			else {
				System.out.println("\nSeveral matching entries. Please choose the desired option:\n");
			}
			
			try {
				if(e.getOriginStations().size() > 1) {
					System.out.println("Origin:");
					for(int i = 0;i < e.getOriginStations().size();i++) {
						System.out.println("[" + i + "] " + e.getOriginStations().get(i));
					}
					
					System.out.print("\nPlease provide the origin index:");
					origin = reader.nextLine();
				}
				else {
					origin = "0";
				}
				
				if(e.getDestinationStations().size() > 1) {
					System.out.println("\nDestination:");
					for(int i = 0;i < e.getDestinationStations().size();i++) {
						System.out.println("[" + i + "] " + e.getDestinationStations().get(i));
					}
					
					System.out.print("\nPlease provide the destination index:");
					destination = reader.nextLine();
				}
				else {
					destination = "0";
				}
			
				trips = e.utilize(e.getOriginStations().get(Integer.parseInt(origin)), e.getDestinationStations().get(Integer.parseInt(destination)));
			} catch (AmbiguousTargetException e1) {
				System.err.println("Route does not exist. Maybe it is too late and no vehicles are underway or the route is not active.");
				return;
			}
		}
		finally {	
			reader.close();
		}
		
		System.out.println("\n");
		
		for(Trip cTrip : trips.getTrips()) {
			System.out.println(cTrip.toString() + "\n");
		}
	}
	
	public void old_example() throws IOException {
		EFA kvv = new EFA();
		
		System.out.println(kvv.searchStopByName("Poststraße"));
		
		Trips trips = null;
		try {
			trips = kvv.getTrips(new TripRequest("Karlsruhe Poststrasse", "KIT  Durlacher Tor", Calendar.getInstance()));
		} catch (AmbiguousTargetException e) {			
			if(!e.isTargetAvailable()) {
				System.err.println("No target found!!");
				return;
			}
			else {
				System.err.println("Too many targets match the given locations.");
			}
			
			try {
				// TODO let the user choose here
				System.out.println("Retry with most probable match...");
				trips = kvv.getTrips(e.getMostProbableMatch());
			} catch (AmbiguousTargetException e1) {
				System.err.println("Route does not exist. Maybe it is too late and no vehicles are underway or the route is not active.");
				return;
			}
		}
		System.out.println(trips);	
		System.out.println("Current Origin: "+ trips.getOriginName());
		System.out.println("Current Destination: "+ trips.getDestinationName());
		
		for(Trip cTrip : trips.getTrips()) {
			System.out.println(new Date(cTrip.getInterval()[0] * 1000) + " UNTIL " + new Date(cTrip.getInterval()[1] * 1000));
		}

		try {
			trips = trips.getPrevious();
			for(Trip cTrip : trips.getTrips()) {
				System.out.println(cTrip.getInterval()[0]+"..");
				System.out.println(new Date(cTrip.getInterval()[0] * 1000) + " UNTIL " + new Date(cTrip.getInterval()[1] * 1000));
			}
		} catch (AmbiguousTargetException e) {
			// should actually not happen... except it is too late...
			e.printStackTrace();
		}
	}
	
	public static void base_test() throws IOException {
		EFA kvv = new EFA();
		Locations locations = kvv.autocomplete("Karlsruhe Durlach");
		for(Location cLocation : locations.getLocations()) {
			System.out.println(cLocation.getName() + " (" + cLocation.getType() + ")");
		}
		
		System.out.println("-----------------");
		
		//TripRequest tripRequest = new TripRequest("Karlsruhe, Hauptbahnhof", "Karlsruhe, Durlacher Tor / KIT-Campus Süd", Calendar.getInstance());
		
		TripRequest tripRequest = new TripRequest("Karlsruhe Poststrasse", "KIT", Calendar.getInstance());
		Trips trips = null;
		try {
			trips = kvv.getTrips(tripRequest);
		} catch (AmbiguousTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			if(!e.isTargetAvailable()) {
				System.err.println("No traget found!!");
				return;
			}
			
			// let the user choose here
			tripRequest = e.getModifiedTripRequest(e.getOriginStations().get(0), e.getDestinationStations().get(0));
			try {
				trips = kvv.getTrips(tripRequest);
			} catch (AmbiguousTargetException e1) {
				System.err.println("Route does not exist. Maybe it is too late and no vehicles are underway.");
				return;
			}
		}
		System.out.println(trips);
	}
	
	public static void live_test() throws StopNotFoundException {
		EFA kvv = new EFA();
		try {
			Stops stopsModel = kvv.searchStopByName("Durlach");
			for(Stop cStop : stopsModel.getStops()) {
				System.out.println(cStop.getName() + "(" + cStop.getId() + ")");
			}
			
			System.out.println("-----------------");
			
			Stop stopModel = kvv.getStopById("de:8212:4608");
			System.out.println(stopModel.getName() + "-" + stopModel.getId());
			
			System.out.println("-----------------");
			
			GeoStops stopsModel2 = kvv.searchStopByGeoLocation(48.992912f, 8.465128f);
			for(GeoStop cStop : stopsModel2.getStops()) {
				System.out.println(cStop.getName() + "-" + cStop.getDistance());
			}
			
			System.out.println("-----------------");
			
			Departures departures = kvv.getDeparturesByStopId("de:8212:4608");
			for(Departure cDep : departures.getDepartures()) {
				System.out.println(cDep);
			}
			System.out.println(new Date(departures.getTimestamp()));
			System.out.println(departures.getStopName());
			
			System.out.println("-----------------");
			
			Departures departures2 = kvv.getDeparturesByStopIdAndRoute("de:8212:89", "S1");
			for(Departure cDep : departures2.getDepartures()) {
				System.out.println(cDep);
			}
			System.out.println(new Date(departures2.getTimestamp()));
			System.out.println(departures2.getStopName());
			
			System.out.println("-----------------");
			
			Stop maxScoredStop = kvv.getMostProbableMatch(kvv.searchStopByName("Karlsruhe Durlacher Tor"), "Karlsruhe Durlacher Tor");
			System.out.println(maxScoredStop.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
