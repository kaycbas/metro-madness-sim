/**
 * 
 */
package com.unimelb.swen30006.metromadness.routers;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.trains.Train;

public interface PassengerRouter {

	public boolean shouldLeave(Train t, Passenger p);
	public boolean shouldBoard(Train t, Passenger p);
	public void getRoute(Passenger p);
}
