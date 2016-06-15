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
	private double speed;

	/**
	 * @param target Target entity
	 * @param start Current location
	 * @param speed Speed in blocks per next() call
	 */
	public EntityTracker(final Entity tracked, final Location start, final double speed)
	{
		this(tracked, start, start.getDirection(), speed);
	}

	/**
	 * @param target Target entity
	 * @param start Current location
	 * @param startDirection Current moving direction
	 * @param speed Speed in blocks per next() call
	 */
	public EntityTracker(final Entity target, final Location start, final Vector startDirection, final double speed)
	{
		this.target = target;
		this.loc = start.clone();
		this.lastDirection = startDirection.clone().normalize().multiply(speed);
		this.sharpTurnFactor = 0.1;// [0;1]
		this.speed = speed;
		this.loc.add(this.lastDirection);
	}

	/**
	 * @return Squared distance between current location and target
	 */
	public double getDistanceSquaredLeft()
	{
		return this.target.getLocation().toVector().subtract(this.loc.toVector()).lengthSquared();
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
		// speed is in blocks per next() call, make length of direct equal to speed
		direct.normalize().multiply(this.speed);
		// alter old direction with new one, more or less depending on sharpTurnFactor value
		this.lastDirection.multiply(1 - this.sharpTurnFactor).add(direct.multiply(this.sharpTurnFactor));
		this.loc.add(this.lastDirection);
		return this.lastDirection;
	}

	/**
	 * @param speed The new speed in blocks per next() call
	 */
	public void setSpeed(final double speed)
	{
		this.speed = speed;
	}

}
