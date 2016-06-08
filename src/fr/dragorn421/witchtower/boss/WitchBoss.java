package fr.dragorn421.witchtower.boss;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Witch;

public class WitchBoss
{

	private Witch mob;

	public WitchBoss(final Location loc)
	{
		this.mob = loc.getWorld().spawn(loc, Witch.class);
		this.mob.setAI(false);
		Bukkit.getScheduler().runtas
	}

}
