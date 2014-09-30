package com.SkyIsland.EnderDragonFridays.Items;


public class LootGenerator {
	
	/**
	 * Core rarity for this generator. This affects the stats put on the items as well as the
	 * items given. This is more a seed than anything. This is set for a group of generations,
	 * and is not to be confused with the individual contribution used to determine exact
	 * drops for each player.
	 */
	private double rarity;
	
	public LootGenerator(double rarity) {
		this.rarity = rarity;
	}
}
