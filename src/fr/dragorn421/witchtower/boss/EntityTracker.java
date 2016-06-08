package fr.dragorn421.witchtower.boss;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityTracker
{

	private Entity target;
	private Location loc;
	private Vector lastDirection;
	private double sharpTurnFactor;
	private double stepLength;

	/**
	 * @param target Target entity
	 * @param start Current location
	 */
	public EntityTracker(final Entity tracked, final Location start)
	{
		this(tracked, start, start.getDirection());
	}

	/**
	 * @param target Target entity
	 * @param start Current location
	 * @param startDirection Current moving direction
	 */
	public EntityTracker(final Entity target, final Location start, final Vector startDirection)
	{
		this.target = target;
		this.loc = start.clone();
		this.lastDirection = startDirection.clone();
		this.sharpTurnFactor = 0.1;// [0;1]
		// stepLength is used to have consistent move length
		this.stepLength = this.lastDirection.length();
		this.loc.add(this.lastDirection);
	}

	/**
	 * @return Distance between current location and target
	 */
	public double getDistanceLeft()
	{
		return this.target.getLocation().toVector().subtract(this.loc.toVector()).length();
	}

	/**
	 * Move a step forwards to the target.
	 * @return The move that was made.
	 */
	public Vector next()
	{
		final Location to = this.target.getLocation().clone();
		// distance to go before reaching the target
		final Vector direct = to.subtract(this.loc).toVector();
		//System.out.println("stepLength=" + this.stepLength + ",direct=" + direct + ",direct.length()=" + direct.length());
		// keeps moves consistent
		direct.normalize().multiply(this.stepLength);
		// alter old direction with new one, more or less depending on sharpTurnFactor value
		this.lastDirection.multiply(1 - this.sharpTurnFactor).add(direct.multiply(this.sharpTurnFactor));
		this.loc.add(this.lastDirection);
		return this.lastDirection;
	}

}
