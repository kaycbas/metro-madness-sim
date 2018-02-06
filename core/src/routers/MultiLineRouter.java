package com.unimelb.swen30006.metromadness.routers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.trains.Train;

public class MultiLineRouter implements PassengerRouter{
	@Override
	public boolean shouldLeave(Train t, Passenger p) {
		Station currentStation = t.getStation();
		Station nextStationOnLine = null;
		try{
			nextStationOnLine = t.trainLine.nextStation(t.getStation(), t.isForward());
		} catch(Exception e) {
			System.out.println("End of line");
		}
		Station passengersNextStation = p.getRoute().getFirst();
		System.out.println(p.getRoute().getFirst().getName());
		if (nextStationOnLine == null) {
			return true;
		}
		if (!nextStationOnLine.equals(passengersNextStation) || 
				currentStation.equals(p.getDestination())) {
			return true;
		} else {
			p.moveAlongRoute();
			return false;
		}
	}
	
	@Override
	public boolean shouldBoard(Train t, Passenger p) {
		Station nextStationOnLine = null;
		try{
			nextStationOnLine = t.trainLine.nextStation(t.getStation(), t.isForward());
		} catch(Exception e) {
			System.out.println("End of line");
		}
		Station passengersNextStation = p.getRoute().getFirst();
		if (nextStationOnLine == null) {
			return false;
		}
		if (nextStationOnLine.equals(passengersNextStation)) {
			p.moveAlongRoute();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void getRoute(Passenger p) {
		LinkedList<Station> route = new LinkedList<Station>();
		Station startStation = p.getBegining();
		Station endStation = p.getDestination();
		if (bfs(startStation, endStation)) {
			//System.out.println("bfs complete");
			Station stationPtr = endStation;
			while (stationPtr != startStation) {
				route.addFirst(stationPtr);
				stationPtr = stationPtr.getParent();
			}
			route.addFirst(startStation);
			for (Station s : route) {
				System.out.print(s.getName() + "--->");
			}
			System.out.println(route.size());
			p.setRoute(route);
		} else {
			System.out.println("Can't find route");
		}
	}

	private boolean bfs(Station startStation, Station endStation) {

		Queue<Station> stationQueue = new LinkedList<Station>();
		ArrayList<Station> explored = new ArrayList<Station>();
		stationQueue.add(startStation);

		while(!stationQueue.isEmpty()) {
			Station currentStation = stationQueue.remove();

			if(currentStation==endStation) {
				return true;
			} else {
				for (Station adjStation : currentStation.getAdjacentStations()) {
					if (!explored.contains(adjStation) && !stationQueue.contains(adjStation)) {
						adjStation.setParent(currentStation);
						stationQueue.add(adjStation);
					}
				}
			}
			explored.add(currentStation);
		}
		return false;
	}
}
