package fr.dragorn421.witchtower.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Util
{

	final static public Random RANDOM = new Random(System.currentTimeMillis() + 421);

	/**
	 * @param p
	 * @return Location of looked at block, null if no block found.
	 */
	static public Location getLookedAt(final Player p)
	{
		final Set<Material> transparent = null;
		final List<Block> lineOfSight = p.getLineOfSight(transparent, 300);
		Location loc = null;
		for(final Block b : lineOfSight)
			if(!b.isEmpty())
			{
				loc = b.getLocation();
				break;
			}
		return loc;
	}

}
