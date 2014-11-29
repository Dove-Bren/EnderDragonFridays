package com.SkyIsland.EnderDragonFridays.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TurkeyNameGenerator implements NameGenerator {
	
	private Random rand;
	private List<String> firstNames;
	private List<String> lastNames;
	
	/**
	 * Creates a name generator ready to spit out awesome boss names!
	 */
	public TurkeyNameGenerator() {
		rand = new Random();
		firstNames = new ArrayList<String>(); //we use array lists for fast random access
		lastNames = new ArrayList<String>();
		
		setupFirst(); //load up first names
		setupLast(); //load up last names
		
	}
	/**
	 * Returns a boss name.<br />
	 * This is a complete name, composed of a first name and a last name separated by a space.
	 * <p />
	 * This is the same as 
	 *        getFirstName() + " " + getLastName()
	 */
	@Override
	public String getName() {
		return (getFirstName() + " " + getLastName());
	}
	
	@Override
	public String getFirstName() {
		return firstNames.get(rand.nextInt(firstNames.size()));
	}

	@Override
	public String getLastName() {
		return lastNames.get(rand.nextInt(lastNames.size()));
	}
	
	/**
	 * Add our first names to a list
	 */
	private void setupFirst() {
		firstNames.add("Raptrix");
		firstNames.add("Turk");
		firstNames.add("Meleagris");
		firstNames.add("Fatty");
	}
	
	/**
	 * Add all our last names to a list
	 */
	private void setupLast() {
		lastNames.add("The Stuffed");
		lastNames.add("Gallopavo");
		lastNames.add("The Fierce");
		lastNames.add("The Turkey");
		lastNames.add("Hawkcousin");
	}
}
