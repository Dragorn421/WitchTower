package fr.dragorn421.witchtower.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

	/**
	 * Returns the possibilities strings that start with arg.<br>
	 * arg and each possibilities string will be toLowerCase()'d
	 * @param arg The start to match
	 * @param possibilities The list of strings to check
	 * @return The list of matching strings
	 */
	final static public List<String> complete(String arg, final String...possibilities)
	{
		arg = arg.toLowerCase();
		final List<String> list = new ArrayList<String>(possibilities.length);
		for(final String s : possibilities)
			if(s.toLowerCase().startsWith(arg))
				list.add(s);
		return list;
	}

	/**
	 * Same as {@link Util#complete(String, String...)} using a Collection as possibilities.
	 * @see Util#complete(String, String...)
	 */
	final static public List<String> complete(String arg, final Collection<String> possibilities)
	{
		arg = arg.toLowerCase();
		final List<String> list = new ArrayList<String>(possibilities.size());
		for(final String s : possibilities)
			if(s.toLowerCase().startsWith(arg))
				list.add(s);
		return list;
	}

	static private List<String> filterOut = new ArrayList<>();

	static private class MyFilter implements Filter
	{

		public Filter.Result filter(LogEvent event)
		{
			return Util.filterOut.contains(event.getMessage().getFormattedMessage())?Filter.Result.DENY:null;
		}
		
		public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String arg3, Object... arg4)
		{
			return null;
		}
		
		public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object arg3, Throwable arg4)
		{
			return null;
		}
		
		public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message arg3, Throwable arg4)
		{
			return null;
		}
		
		public Filter.Result getOnMatch()
		{
			return null;
		}
		
		public Filter.Result getOnMismatch()
		{
			return null;
		}

	}

	final static public void dirtyTeleport(final Entity e, final Location loc)
	{
		// remove message from log using log4j core classes
		final Logger rootLogger = ((Logger)LogManager.getRootLogger());
		final Iterator<Filter> itf = rootLogger.getFilters();
		boolean addNewFilter = true;
		while(itf.hasNext())
		{
			final Filter f = itf.next();
			if(f instanceof MyFilter)
				addNewFilter = false;
		}
		if(addNewFilter)
			rootLogger.addFilter(new MyFilter());
		@SuppressWarnings("deprecation")
		final String expected = "Teleported " + e.getType().getName() + " to " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ();
		Util.filterOut.add(expected);
		// remove message sent to op using sendCommandFeedback gamerule
		final List<World> worlds = Bukkit.getWorlds();
		final List<World> worldsWithGameRuleSetToTrue = new ArrayList<>(worlds.size());
		final String sendCommandFeedback = "sendCommandFeedback";
		for(int i=0;i<worlds.size();i++)
		{
			final World w = worlds.get(i);
			if(w.getGameRuleValue(sendCommandFeedback).equals("true"))
			{
				worldsWithGameRuleSetToTrue.add(w);
				w.setGameRuleValue(sendCommandFeedback, "false");
			}
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + e.getUniqueId() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " " + loc.getYaw() + " " + loc.getPitch());
		for(int i=0;i<worldsWithGameRuleSetToTrue.size();i++)
			worldsWithGameRuleSetToTrue.get(i).setGameRuleValue(sendCommandFeedback, "true");
		Util.filterOut.remove(expected);
	}

/*
	final static public void nmsTeleport(final Entity e, final Location loc)
	{
		final net.minecraft.server.v1_9_R2.Entity nmse = ((CraftEntity)e).getHandle();
		nmse.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		nmse.recalcPosition();
		final EntityPlayer p;
		final packetplayout
		p.playerConnection.sendPacket(packet);
//		nmse.al();
//		nmse.aQ();
//		nmse.at();
//		nmse.aw();
//		nmse.m();
//		nmse.Q();
//		nmse.U();
	}

	final static public void rotateEntityFor(final Player p, final Entity e, final float yaw)
	{
		Util.sendPacket(p, Util.packetEntityLook(e, yaw));
	}

	final static public void sendPacket(final Player p, final Packet<?> packet)
	{
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}

	final static public PacketPlayOutEntityHeadRotation packetEntityLook(final Entity e, final float yaw)
	{
		final PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation(((CraftEntity)e).getHandle(), (byte)(yaw/360f*256f));
		return packet;
	}//*/

}
