package fr.dragorn421.witchtower.tower;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import fr.dragorn421.witchtower.tower.WitchTowerParameters.Shape;
import fr.dragorn421.witchtower.util.Util;

public class WitchTower
{

	final private Location location;
	final private WitchTowerParameters params;
	final private List<WitchTowerLayer> layers;

	public WitchTower(final Location location, final WitchTowerParameters params, final boolean independantLayerGeneration)
	{
		this.location = location;
		this.params = params;
		this.layers = new ArrayList<>(this.params.height);
		if(independantLayerGeneration)
		{
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
			WitchTowerLayer first = new WitchTowerLayer(0, 0, this.params);
			this.layers.add(first);
/*
u0 = 1
un+1 = un - un/20 = 19/20un
un = 1 * 19/20n
un = qn
uh = x
uh = qh = x
u50 = 5 = q^50
 */
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
				final double ht = -(double)this.params.height / (((double)this.params.topSize / 2) / ((double)this.params.baseSize / 2) - 1D);
				System.out.println("ht=" + ht);
				for(int i=0;i<this.params.height;i++)
				{
					this.layers.add(first.next(this.params, 1D-(double)i/ht));//TODO
				}
			}
			else if(this.params.shape == Shape.CONVEX)
			{//TODO not working
				final double q = 1/Math.pow(this.params.baseSize - this.params.topSize, 1D/this.params.height);
				System.out.println("height=" + this.params.height + ",q=" + q);
				final double ht = -(double)this.params.height / (((double)this.params.topSize / 2) / ((double)this.params.baseSize / 2) - 1D);
				System.out.println("ht=" + ht);
				for(int i=0;i<this.params.height;i++)
				{//menhir 2*(1-(double)i/ht-Math.pow(q, i))
					//in ground menhir 2*(1-(double)i/ht-Math.pow(q, i)) + Math.pow(q, i)/2)
					this.layers.add(first.next(this.params, 2*(1-(double)i/ht) - Math.pow(q, i)));
				}
			}
		}
	}

	public void build()
	{
		final Location loc = this.location.clone();
		for(int i=0;i<this.layers.size();i++)
		{
			this.layers.get(i).buildAround(loc);
			loc.setY(loc.getY() + 1);
		}
	}

	public void delete()
	{
		final Location loc = this.location.clone();
		for(int i=0;i<this.layers.size();i++)
		{
			this.layers.get(i).delete(loc);
			loc.setY(loc.getY() + 1);
		}
	}

	public Location getTop()
	{
		return this.layers.get(this.layers.size()-1).getCenter(this.location.clone()).add(0D, this.params.height + 1D, 0D);
	}

}