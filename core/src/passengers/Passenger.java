package com.unimelb.swen30006.metromadness.passengers;

import com.unimelb.swen30006.metromadness.stations.Station;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Passenger {

	private Station begining;
	private Station destination;
	private float travelTime;
	private boolean reachedDestination;
	private LinkedList<Station> route;
	
	public Passenger(Station start, Station end){
		this.begining = start;
		this.destination = end;
		this.reachedDestination = false;
		this.travelTime = 0;
		//route = new LinkedList<Station>();
		//route = getRoute(start, end);
		/*System.out.print("Route: ");
		for (Station s : route) {
			System.out.print(" --> " + s.name);
		}
		System.out.println();*/
	}
	
	public Station getBegining() {
		return this.begining;
	}
	
	public Station getDestination() {
		return this.destination;
	}
	
	public LinkedList<Station> getRoute() {
		return this.route;
	}
	public void setRoute(LinkedList<Station> route) {
		this.route = route;
	}
	
	public void moveAlongRoute() {
		this.route.removeFirst();
	}
	
	public void update(float time){
		if(!this.reachedDestination){
			this.travelTime += time;
		}
	}
	
}
