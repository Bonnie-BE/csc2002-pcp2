//M. M. Kuttel 2024 mkuttel@gmail.com
//Modified by B. Tshwane 2024 tshbon035
// Class for storing  locations of people (swimmers only for now, but could add other types) in the simulation

package medleySimulation;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeopleLocation  { // this is made a separate class so don't have to access thread
	
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	
	private AtomicBoolean inStadium; //are they here?  atomicity prevents race conditions
	private AtomicBoolean arrived; //have they arrived at the event? atomicity prevents race conditions
	private GridBlock location; //which GridBlock are they on?
	
	//constructor
	PeopleLocation(int ID , Color c) {
		myColor = c;
		inStadium = new AtomicBoolean(false); //not in pool  ,atomicity prevents race conditions
		arrived = new AtomicBoolean(false); //have not arrived  ,atomicity prevents race conditions
		this.ID=ID;
	}
	
	//setter
	public synchronized void setInStadium(boolean in) {
		this.inStadium.set(in);  //added synchronized this to prevent race conditions
	}
	
	//getter and setter
	public synchronized boolean getArrived() {
		return arrived.get();
	}  //added synchronized this to prevent race conditions
	public synchronized void setArrived() {
		this.arrived.set(true);
	}  //added synchronized this to prevent race conditions

//getter and setter
	public synchronized GridBlock getLocation() {
		return location;
	}  //added synchronized this to prevent race conditions

	public synchronized void setLocation(GridBlock location) {
		this.location = location;
	}  //added synchronized this to prevent race conditions

	//getter
	public synchronized int getX() { return location.getX();}  //added synchronized this to prevent race conditions
	
	//getter
	public synchronized int getY() {	return location.getY();	}  //added synchronized this to prevent race conditions
	
	//getter
	public  int getID() {	return ID;	}

	//getter
	public synchronized boolean inPool() {
		return inStadium.get();
	}  //added synchronized this to prevent race conditions
	//getter and setter
	public  Color getColor() { return myColor; }  //added synchronized this to prevent race conditions
	public  void setColor(Color myColor) { this.myColor= myColor; }  //added synchronized this to prevent race conditions
}
