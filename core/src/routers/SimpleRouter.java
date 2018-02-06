package com.unimelb.swen30006.metromadness.routers;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.trains.Train;

public class SimpleRouter implements PassengerRouter {

	@Override
	public boolean shouldLeave(Train t, Passenger p) {
		return t.getStation().equals(p.getDestination());
	}
	
	@Override
	public boolean shouldBoard(Train t, Passenger p) {
		if (t.trainLine.getStations().contains(p.getDestination())) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void getRoute(Passenger p) {
		System.out.println("Simple router. No route set");
	}

}
