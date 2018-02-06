package com.unimelb.swen30006.metromadness.stations;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.unimelb.swen30006.metromadness.passengers.Passenger;
import com.unimelb.swen30006.metromadness.passengers.PassengerGenerator;
import com.unimelb.swen30006.metromadness.routers.PassengerRouter;
import com.unimelb.swen30006.metromadness.tracks.Line;
import com.unimelb.swen30006.metromadness.trains.Train;

public class Station {
	
	public static final int PLATFORMS=2;
	
	private Point2D.Float position;
	public static final float RADIUS=6;
	public static final int NUM_CIRCLE_STATMENTS=100;
	public static final int MAX_LINES=3;
	private String name;
	private ArrayList<Line> lines;
	private ArrayList<Train> trains;
	public static final float DEPARTURE_TIME = 2;
	private PassengerRouter router;

	private boolean active;
	private PassengerGenerator g;			//added from ActiveStation class
	private ArrayList<Passenger> waiting;	//added from ActiveStation class

	private ArrayList<Station> adjacentStations;
	private Station parent; //for use in bfs search

	public Station(float x, float y, PassengerRouter router, String name){
		this.name = name;
		this.router = router;
		this.position = new Point2D.Float(x,y);
		this.lines = new ArrayList<Line>();
		this.trains = new ArrayList<Train>();
		this.active = false;
		adjacentStations = new ArrayList<Station>();
		this.parent = null;
		System.out.println("Created inactive station");
	}
	
	public Station(float x, float y, PassengerRouter router, String name, float maxPax){
		this.name = name;
		this.router = router;
		this.position = new Point2D.Float(x,y);
		this.lines = new ArrayList<Line>();
		this.trains = new ArrayList<Train>();

		this.active = true;
		this.g = new PassengerGenerator(this, this.lines, maxPax); //from ActiveStation contructor
		this.waiting = new ArrayList<Passenger>();
		adjacentStations = new ArrayList<Station>();
		this.parent = null;
		System.out.println("Created active station");
	}
	
	public ArrayList<Line> getLines(){
		return this.lines;
	}
	
	public Point2D.Float getPosition() {
		return this.position;
	}
	
	public String getName() {
		return this.name;
	}
	
	public PassengerGenerator getPassengerGenerator() {
		return this.g;
	}
	
	public ArrayList<Station> getAdjacentStations() {
		return this.adjacentStations;
	}
	
	public Station getParent() {
		return this.parent;
	}
	
	public void setParent(Station p) {
		this.parent = p;
	}
	
	public void registerLine(Line l){
		this.lines.add(l);
	}
	
	public void render(ShapeRenderer renderer){
		float radius = RADIUS;
		for(int i=0; (i<this.lines.size() && i<MAX_LINES); i++){
			Line l = this.lines.get(i);
			renderer.setColor(l.getLineColour());
			renderer.circle(this.position.x, this.position.y, radius, NUM_CIRCLE_STATMENTS);
			radius = radius - 1;
		}
		
		// Calculate the percentage
		float t = this.trains.size()/(float)PLATFORMS;
		Color c = Color.WHITE.cpy().lerp(Color.DARK_GRAY, t);
		renderer.setColor(c);
		renderer.circle(this.position.x, this.position.y, radius, NUM_CIRCLE_STATMENTS);		
	}
	
	public void enter(Train t) throws Exception {
		if (trains.size() >= PLATFORMS) {
			throw new Exception();
		} else {
			this.trains.add(t);
		}
	}

	public void embarkNewPassengers(Train t) {

			// Add the waiting passengers
			Iterator<Passenger> pIter = this.waiting.iterator();
			System.out.println("Waiting to embark: " + waiting.size());
			while(pIter.hasNext()){
				Passenger p = pIter.next();
				if (shouldBoard(t, p)) {		//checks if passenger destination is on the train's line
					try {
						t.embark(p);
						System.out.println("embarked passenger");
						pIter.remove();
					} catch (Exception e) {
						// Do nothing, already waiting
						System.out.println("Train full, can't embark");
						break;
					}
				}
			}

			// Add the new passenger
			Passenger[] ps = this.g.generatePassengers();
			for(Passenger p: ps){
				if (shouldBoard(t, p)) {
					try {
						t.embark(p);
					} catch (Exception e) {
						this.waiting.add(p);
					}
				}
			}
		}
		//System.out.println(name + ": " + waiting.size());
	
	
	public void depart(Train t) throws Exception {
		if(this.trains.contains(t)){
			this.trains.remove(t);
		} else {
			throw new Exception();
		}
	}
	
	public boolean canEnter(Line l) throws Exception {
		return trains.size() < PLATFORMS;
	}

	// Returns departure time in seconds
	public float getDepartureTime() {
		return DEPARTURE_TIME;
	}
	
	public boolean shouldLeave(Train t, Passenger p) {
		return this.router.shouldLeave(t, p);
	}

	public boolean shouldBoard(Train t, Passenger p) {
		return this.router.shouldBoard(t, p);
		/*
		if (t.trainLine.stations.contains(p.destination)) {
			return true;
		} else {
			return false;
		}
		*/
	}

	@Override
	public String toString() {
		return "Station [position=" + position + ", name=" + name + ", trains=" + trains.size()
				+ ", router=" + router + "]";
	}

	public Passenger generatePassenger(Station s) {
		Passenger newPassenger = new Passenger(this, s);
		// got route
		router.getRoute(newPassenger);
		return newPassenger;
	}
	
	
}
