package com.SkyIsland.EnderDragonFridays.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Generates a chest-ful of equipment for players!<br />
 * Takes a mapping of players to their contribution and generates loot. The results are returned
 * as a map between players and chests.<br />The chest the players are mapped to contain the loot
 * generated for that player.
 * @author Skyler
 * @note This should be abstracted out when converting to a framework. Let people make their own ChestGenerators that don't,<br />
 * for example, exclude 0% contribution
 */
public class ChestContentGenerator {
	
	private static LootGenerator gen;
	
	public static Map<UUID, Inventory> generate(double rarity, Map<UUID, Double> inputMap) {
		
		//First, create our generator
		gen = new LootGenerator(rarity);
		
		//Next, we set up our new map that will connect players to their chests
		Map<UUID, Inventory> output = new HashMap<UUID, Inventory>();
		
		Inventory chest;
		for (UUID player : inputMap.keySet()) {
			
			//Before anything, make sure they contributed!
			if (inputMap.get(player) <= .01) {
				//they have only contributed 1% or less of total health!
				continue; //nothing for them!
			}
			
			//Create a chest
			chest = Bukkit.getServer().createInventory(null, 27);
			//we are going to populate it with two items
			//chest.addItem(gen.generateItem(  inputMap.get(player)  )); //generate item. Use the double passed with player as weight
			//chest.addItem(gen.generateItem(  inputMap.get(player)  ));
			chest.addItem(new ItemStack(Material.DIAMOND_AXE));
			
			//add this inventory to the map
			output.put(player, chest);
		}
		
		return output;
	}
}
