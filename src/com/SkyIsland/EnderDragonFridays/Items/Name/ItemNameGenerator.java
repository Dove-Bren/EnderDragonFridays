package com.SkyIsland.EnderDragonFridays.Items.Name;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class ItemNameGenerator implements NameGenerator {
	
	private Random rand;
	private List<String> firstNames; 
	private List<String> lastNames;
	
	public ItemNameGenerator() {
		this.rand = new Random();
		this.firstNames = new ArrayList<String>();
		this.lastNames = new ArrayList<String>();
		
		setupFirst();
		setupLast();
	}
	
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
	
	private void setupFirst() {
		firstNames.add("Stone");
		firstNames.add("Dirt");
		firstNames.add("Sand");
		firstNames.add("Air");
		firstNames.add("Extreme");
		firstNames.add("Ultra");
		firstNames.add("Super");
		firstNames.add("Weak");
		firstNames.add("Regular");
		firstNames.add("World");
	}
	
	private void setupLast() {
		lastNames.add("Eater");
		lastNames.add("Destroyer");
		lastNames.add("Maniac");
		lastNames.add("Conqueror");
		lastNames.add("Harbinger");
		lastNames.add("Levithan");
		lastNames.add("Cracker");
		lastNames.add("Breaker");
	}
}
