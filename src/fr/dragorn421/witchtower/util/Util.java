package fr.dragorn421.witchtower.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import fr.dragorn421.witchtower.WitchTowerPlugin;

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

	@SuppressWarnings("unchecked")
	final static public <T> T getMetadata(final Metadatable from, final String key, final Class<T> clazz)
	{
		for(final MetadataValue v : from.getMetadata(key))
		{
			if(v.getOwningPlugin() == WitchTowerPlugin.get() && clazz.isAssignableFrom(v.value().getClass()))
				return (T) v.value();
		}
		return null;
	}

	final static public void setMetadata(final Metadatable to, final String key, final Object value)
	{
		if(value == null)
			to.removeMetadata(key, WitchTowerPlugin.get());
		else
			to.setMetadata(key, new FixedMetadataValue(WitchTowerPlugin.get(), value));
	}

}
