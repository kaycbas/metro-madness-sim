package com.unimelb.swen30006.metromadness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

// Imports for parsing XML files
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.unimelb.swen30006.metromadness.passengers.PassengerGenerator;
import com.unimelb.swen30006.metromadness.routers.MultiLineRouter;
// The things we are generating
import com.unimelb.swen30006.metromadness.routers.PassengerRouter;
import com.unimelb.swen30006.metromadness.routers.SimpleRouter;
//import com.unimelb.swen30006.metromadness.stations.ActiveStation;
import com.unimelb.swen30006.metromadness.stations.Station;
import com.unimelb.swen30006.metromadness.tracks.Line;
//import com.unimelb.swen30006.metromadness.trains.BigPassengerTrain;
//import com.unimelb.swen30006.metromadness.trains.SmallPassengerTrain;
import com.unimelb.swen30006.metromadness.trains.Train;
import sun.security.provider.certpath.AdjacencyList;

public class MapReader {

	private ArrayList<Train> trains;
	private HashMap<String, Station> stations;
	private HashMap<String, Line> lines;

	private boolean processed;
	private String filename;

	public MapReader(String filename){
		this.trains = new ArrayList<Train>();
		this.stations = new HashMap<String, Station>();
		this.lines = new HashMap<String, Line>();
		this.filename = filename;
		this.processed = false;
	}

	public void process(){
		try {
			// Build the doc factory
			FileHandle file = Gdx.files.internal("desktop/assets/maps/melbourne.xml");
			XmlReader reader = new XmlReader();
			Element root = reader.parse(file);
			
			// Process stations
			Element stations = root.getChildByName("stations");
			Array<Element> stationList = stations.getChildrenByName("station");
			for(Element e : stationList){
				Station s = processStation(e);
				this.stations.put(s.getName(), s);
			}
			
			// Process Lines
			Element lines = root.getChildByName("lines");
			Array<Element> lineList = lines.getChildrenByName("line");
			for(Element e : lineList){
				Line l = processLine(e);
				this.lines.put(l.getName(), l);
			}

			// Process Trains
			Element trains = root.getChildByName("trains");
			Array<Element> trainList = trains.getChildrenByName("train");
			for(Element e : trainList){
				Train t = processTrain(e);
				this.trains.add(t);
			}

			// Add adjacent stations
			for (Station s : this.stations.values()) {
				addAdjacentStations(s);
			}
			System.out.println("adjacent stations added");
			
			// Add all lines to generators, so they generate passengers with
			// destinations on any line
			addLinesToGenerators();
			
			this.processed = true;
			
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void addAdjacentStations(Station s) {
		for (Line l : s.getLines()) {
			try {
				Station nextStationOnLine = l.nextStation(s, true);	//retrieves next station in forward direction on line
				if(!s.getAdjacentStations().contains(nextStationOnLine)) {
					s.getAdjacentStations().add(nextStationOnLine);
				}
				nextStationOnLine = l.nextStation(s, false);	//retrieves next station in backward direction on line
				if(!s.getAdjacentStations().contains(nextStationOnLine)) {
					s.getAdjacentStations().add(nextStationOnLine);
				}
			} catch (Exception e) {
			}
		}
	}
	
	public void addLinesToGenerators() {
		ArrayList<Line> allLines = new ArrayList<Line>();
		for (Line l : this.lines.values()) {
			allLines.add(l);
		}
		for (Station s : this.stations.values()) {
			s.getPassengerGenerator().setLines(allLines);
		}
	}

	public void genStationGraph() {
		//AdjacencyList al = new AdjacencyList();
	}
	
	public Collection<Train> getTrains(){
		if(!this.processed) { this.process(); }
		return this.trains;
	}
	
	public Collection<Line> getLines(){
		if(!this.processed) { this.process(); }
		return this.lines.values();
	}
	
	public Collection<Station> getStations(){
		if(!this.processed) { this.process(); }
		return this.stations.values();
	}

	private Train processTrain(Element e){
		// Retrieve the values
		String type = e.get("type");
		String line = e.get("line");
		String start = e.get("start");
		boolean dir = e.getBoolean("direction");

		// Retrieve the lines and stations
		Line l = this.lines.get(line);
		Station s = this.stations.get(start);
		
		int MAX_PASSENGERS = 100;
		return new Train(l, s, dir, MAX_PASSENGERS);
	}

	private Station processStation(Element e){
		String type = e.get("type");
		String name = e.get("name");
		int x_loc = e.getInt("x_loc")/8;
		int y_loc = e.getInt("y_loc")/8;
		String router = e.get("router");
		PassengerRouter r = createRouter(router);
		if(type.equals("Active")){
			int maxPax = e.getInt("max_passengers");
			return new Station(x_loc, y_loc, r, name, maxPax);
		} else if (type.equals("Passive")){
			return new Station(x_loc, y_loc, r, name);
		}
		
		return null;
	}

	private Line processLine(Element e) {
		Color stationCol = extractColour(e.getChildByName("station_colour"));
		Color lineCol = extractColour(e.getChildByName("line_colour"));
		String name = e.get("name");
		Line l = new Line(stationCol, lineCol, name);
		
		Array<Element> stations = e.getChildrenByNameRecursively("station");
		for(Element s: stations){
			Station station = this.stations.get(s.get("name"));
			boolean twoWay = s.getBoolean("double");
			l.addStation(station, twoWay);
		}
		
		return l;
	}
	
	private PassengerRouter createRouter(String type){
		return new MultiLineRouter();
		/*
		if(type.equals("simple")){
			return new SimpleRouter();
		}
		return null;
		*/
	}
	
	private Color extractColour(Element e){
		float red = e.getFloat("red")/255f;
		float green = e.getFloat("green")/255f;
		float blue = e.getFloat("blue")/255f;
		return new Color(red, green, blue, 1f);
	}

}
