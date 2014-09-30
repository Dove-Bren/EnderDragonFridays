package com.SkyIsland.EnderDragonFridays.Items;

import java.util.Collection;

import org.bukkit.inventory.ItemStack;

import com.SkyIsland.EnderDragonFridays.Name.DefaultNames;
import com.SkyIsland.EnderDragonFridays.Name.NameGenerator;


public class LootGenerator {
	
	/**
	 * Core rarity for this generator. This affects the stats put on the items as well as the
	 * items given. This is more a seed than anything. This is set for a group of generations,
	 * and is not to be confused with the individual contribution used to determine exact
	 * drops for each player.
	 */
	private double rarity;
	
	private Collection<String> names;
	
	private NameGenerator generator;
	
	/**
	 * Creates a loot generator with the passed rarity.<br />
	 * The loot generator will use a list of names defined in {@link com.SkyIsland.EnderDragonFridays.Name.DefaultNames DefaultNames}
	 * @param rarity The relative rarity of items produced through this generator. A good range of values are from 1 to 10, but any number is theoritically supported.
	 */
	public LootGenerator(double rarity) {
		this.rarity = rarity;
		this.names = DefaultNames.generate();
	}
	
	public LootGenerator(double rarity, Collection<String> names) {
		this.rarity = rarity;
		this.names = names;
	}
	
	public LootGenerator(double rarity, NameGenerator generator) {
		this.rarity = rarity;
		this.generator = generator;
	}
	
	/**
	 * Generates an item, given the generator's rarity and the provided weight.
	 * <p>The weight provided modifies the item within the realm of the generator's given rarity.<br />
	 * In other words, rarity specifies the quality of items for all items from this generator, where as the
	 * prescribed weight specifies which subset quality the item will return.</p>
	 * <p>A concrete example is a boss fight. Let's say you run many boss fights, and the one we're looking at
	 * right now is only moderately difficulty. If you give a huge, very difficulty boss fight a rarity
	 * of 10, you might want to give this one a 5. Let's also say you want to scale the items that players
	 * get based on how much damage they do (in % of max of the boss). This second parameter is what's termed
	 * as weight. You would create a single LootGenerator for the fight with an overall weight of 5, and generate
	 * items for each player based on how much they contributed.
	 * @param weight Individual item parameter for this specific item
	 * @return
	 */
	public ItemStack generateItem(double weight) {
		ItemStack item = null;
		
		
		
		return item;
	}
}
