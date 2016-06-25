package fr.dragorn421.witchtower.tower;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import fr.dragorn421.witchtower.boss.WitchBoss;
import fr.dragorn421.witchtower.parameters.WTParameters;
import fr.dragorn421.witchtower.parameters.WTParameters.Shape;
import fr.dragorn421.witchtower.util.Util;

public class WitchTower
{

	final private Location location;
	final private WTParameters params;
	final private List<WitchTowerLayer> layers;

	private WitchBoss boss;

	/**
	 * Generates a tower with the given parameters. Won't be actually built unless {@link WitchTower#build()} is called.
	 * @param location Minimum corner of tower base
	 * @param params The generation parameters
	 * @param independentLayerGeneration Generate each layer independently rather than scaling down the first one
	 */
	public WitchTower(final Location location, final WTParameters params, final boolean independentLayerGeneration)
	{
		this.location = location;
		this.params = params;
		this.layers = new ArrayList<>(this.params.height);
		if(independentLayerGeneration)
		{
			// TODO better delete this before anyone see the result
			double offsetX = 0, offsetZ = 0;
			for(int i=0;i<this.params.height;i++)
			{
				this.layers.add(new WitchTowerLayer((int)offsetX, (int)offsetZ, this.params));
				offsetX += ((Util.RANDOM.nextDouble() * 3) - 1.5);
				offsetZ += ((Util.RANDOM.nextDouble() * 3) - 1.5);
				//System.out.print("offset(" + offsetX + ";" + offsetZ + "");
			}
		}
		else
		{
			// random base layer
			WitchTowerLayer first = new WitchTowerLayer(0, 0, this.params);
			this.layers.add(first);
			if(this.params.shape == Shape.CONCAVE)
			{
				final double q = 1/Math.pow(this.params.baseSize - this.params.topSize, 1D/this.params.height);
				System.out.println("height=" + this.params.height + ",q=" + q);
				for(int i=0;i<this.params.height;i++)
				{
					this.layers.add(first.next(this.params, Math.pow(q, i)));
				}
			}
			else if(this.params.shape == Shape.STRAIGHT)
			{
				// use Thales theorem to find this
				final double ht = -(double)this.params.height / (((double)this.params.topSize / 2) / ((double)this.params.baseSize / 2) - 1D);
				System.out.println("ht=" + ht);
				for(int i=0;i<this.params.height;i++)
				{
					this.layers.add(first.next(this.params, 1D-(double)i/ht));
				}
			}
			else if(this.params.shape == Shape.CONVEX)
			{//TODO not working
				final double q = 1/Math.pow(this.params.baseSize - this.params.topSize, 1D/this.params.height);
				System.out.println("height=" + this.params.height + ",q=" + q);
				final double ht = -(double)this.params.height / (((double)this.params.topSize / 2) / ((double)this.params.baseSize / 2) - 1D);
				System.out.println("ht=" + ht);
				for(int i=0;i<this.params.height;i++)
				{
					// =====  GRAVEYARD OF NON-WORKING FORMULAS   =====
					// menhir 2*(1-(double)i/ht-Math.pow(q, i))
					// in ground menhir 2*(1-(double)i/ht-Math.pow(q, i)) + Math.pow(q, i)/2)
					// ===== You now are out of this creepy place =====
					this.layers.add(first.next(this.params, 2*(1-(double)i/ht) - Math.pow(q, i)));
				}
			}
		}
		this.boss = null;
	}

	/**
	 * Builds the tower at the location provided in the constructor.
	 * @param replaceByAir Change block even if new one is air
	 */
	public void build(final boolean replaceByAir)
	{
		final Location loc = this.location.clone();
		for(int i=0;i<this.layers.size();i++)
		{
			this.layers.get(i).build(loc, true, this);
			loc.setY(loc.getY() + 1);
		}
	}

	/**
	 * Destroys the tower as if it was built at the location provided in the constructor.
	 * @param replaceByAir Delete blocks even if corresponding one is air
	 */
	public void delete(final boolean replaceByAir)
	{
		final Location loc = this.location.clone();
		for(int i=0;i<this.layers.size();i++)
		{
			this.layers.get(i).delete(loc, true);
			loc.setY(loc.getY() + 1);
		}
		if(this.getBoss() != null)
		{
			this.boss.remove();
			this.boss = null;
		}
	}

	/**
	 * @return The location up 1 block from the center of the top layer of the tower.
	 */
	public Location getTop()
	{
		return this.layers.get(this.layers.size()-1).getCenter(this.location.clone()).add(0D, this.params.height + 1D, 0D);
	}

	/**
	 * @return The parameters of this tower
	 */
	public WTParameters getParameters()
	{
		return this.params;
	}

	/**
	 * Creates a new boss for this tower, removing the old one if any
	 * @return The boss of this tower
	 */
	public WitchBoss newBoss()
	{
		if(this.getBoss() != null)
			this.boss.remove();
		return this.boss = new WitchBoss(this, this.getTop());
	}

	/**
	 * Returns the boss of this tower, creates one if there is none
	 * @return The boss of this tower, not null
	 */
	public WitchBoss needBoss()
	{
		return this.getBoss() == null?this.newBoss():this.boss;
	}

	/**
	 * @return The boss of this tower, null if none
	 */
	public WitchBoss getBoss()
	{
		if(this.boss != null && !this.boss.isValid())
			this.boss = null;
		return this.boss;
	}

}
