package fr.dragorn421.witchtower.boss;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityTracker
{

	private Entity tracked;
	private Location loc;
	private Vector lastDirection;
	private double sharpTurnFactor;
	private double stepLength;

	public EntityTracker(final Entity tracked, final Location start)
	{
		this(tracked, start, start.getDirection());
	}

	public EntityTracker(final Entity tracked, final Location start, final Vector startDirection)
	{
		this.tracked = tracked;
		this.loc = start.clone();
		this.lastDirection = startDirection.clone();
		this.sharpTurnFactor = 0.1;
		this.stepLength = this.lastDirection.length();
		this.loc.add(this.lastDirection);
	}

	public double getDistanceLeft()
	{
		return this.tracked.getLocation().toVector().subtract(this.loc.toVector()).length();
	}

	public Vector next()
	{
		final Location to = this.tracked.getLocation().clone();
		final Vector direct = to.subtract(this.loc).toVector();
		//System.out.println("stepLength=" + this.stepLength + ",direct=" + direct + ",direct.length()=" + direct.length());
		direct.multiply(this.stepLength / direct.length());
		this.lastDirection.multiply(1 - this.sharpTurnFactor).add(direct.multiply(this.sharpTurnFactor));
		this.loc.add(this.lastDirection);
		return this.lastDirection;
	}

}
