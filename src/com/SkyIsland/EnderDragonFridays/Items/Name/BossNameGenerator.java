package com.SkyIsland.EnderDragonFridays.Items.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BossNameGenerator implements NameGenerator {
	
	private Random rand;
	private List<String> firstNames;
	private List<String> lastNames;
	
	public BossNameGenerator() {
		rand = new Random();
		firstNames = new ArrayList<String>();
		lastNames = new ArrayList<String>();
		
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
		firstNames.add("Uldar");
		firstNames.add("Bred");
		firstNames.add("Lyssur");
		firstNames.add("Nervi");
		firstNames.add("Gerptor");
		firstNames.add("Venox");
		firstNames.add("Cervox");
		firstNames.add("Bromethius");
		firstNames.add("Vincent");
		firstNames.add("Greg");
		firstNames.add("Bob");
		firstNames.add("Zeike");
		firstNames.add("Lloyd");
		firstNames.add("Ogur");
		firstNames.add("Prunt");
		firstNames.add("Reque");
		firstNames.add("Rex");
		firstNames.add("Hethe");
		firstNames.add("Lugutharr");
		firstNames.add("Yggdrasil");
		firstNames.add("Wyste");
	}
	
	private void setupLast() {
		lastNames.add("Bonecrusher");
		lastNames.add("Stoneweaver");
		lastNames.add("The Destroyer");
		lastNames.add("The Crusher");
		lastNames.add("The Slayer");
		lastNames.add("DragonBreathe");
		lastNames.add("Ronningston");
		lastNames.add("Smith");
		lastNames.add("Wirthmire");
		lastNames.add("The Great");
		lastNames.add("The Grand");
		lastNames.add("The Nimble");
		lastNames.add("The Weak");
		lastNames.add("The Pitiful");
		lastNames.add("Sorrowbreath");
		lastNames.add("Deathfang");
		lastNames.add("Ironsong");
		lastNames.add("Sharpspeak");
		lastNames.add("Reyunk");
		lastNames.add("Firebreather");
	}
}
