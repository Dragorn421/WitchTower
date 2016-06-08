package fr.dragorn421.witchtower.tower;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import fr.dragorn421.witchtower.util.Util;

public class WitchTowerLayer
{

	final private double offsetX;
	final private double offsetZ;
	final private MaterialData blocks[][];

	/**
	 * @param offsetX
	 * @param offsetZ
	 * @param params Generation parameters
	 */
	public WitchTowerLayer(final double offsetX, final double offsetZ, final WitchTowerParameters params)
	{
		this(offsetX, offsetZ, WitchTowerLayer.genLayer(params));
	}

	/**
	 * @param offsetX
	 * @param offsetZ
	 * @param blocks The blocks that make the layer
	 */
	public WitchTowerLayer(final double offsetX, final double offsetZ, final MaterialData blocks[][])
	{
		this.blocks = blocks;
		// supposed to center the layer, which is useless anyway unless every layer is random
		// keeping it for further use
/*		int filled = 0, iSum = 0, jSum = 0;
		for(int i=0;i<this.blocks.length;i++)
			for(int j=0;j<this.blocks[i].length;j++)
			{
				if(this.blocks[i][j] != null)
				{
					filled++;
					iSum += i;
					jSum += j;
				}
			}
		int centerOffsetX = 0, centerOffsetZ = 0;
		if(filled != 0)
		{
			centerOffsetX = iSum / filled - (int) Math.round(this.blocks.length / 2D);
			centerOffsetZ = jSum / filled - (int) Math.round(this.blocks[0].length / 2D);
		}
		System.out.println("centerOffsetX=" + centerOffsetX + ",centerOffsetZ=" + centerOffsetZ);
		this.offsetX = offsetX - centerOffsetX;
		this.offsetZ = offsetZ - centerOffsetZ;//*/
		this.offsetX = offsetX;
		this.offsetZ = offsetZ;
	}

	/**
	 * Generates a new layer that is a scaled down version of the current one, according to the erodeFactor value
	 * @param params Generation parameters
	 * @param erodeFactor How much to scale down the current layer (between 0 and 1 inclusive)
	 * @return The new layer
	 */
	public WitchTowerLayer next(final WitchTowerParameters params, final double erodeFactor)
	{
		final MaterialData[][] layer = new MaterialData[this.blocks.length][this.blocks[0].length];
		// x and z offsets are used so that the old layer and the new one have the same center
		// otherwise layers would stack up in a corner
		final int	xOffset = (int) ((this.blocks.length - this.blocks.length * erodeFactor) / 2),
					zOffset = (int) ((this.blocks[0].length - this.blocks[0].length * erodeFactor) / 2);
		//System.out.println("blocks.length=" + this.blocks.length + ",erodeFactor=" + erodeFactor);
		//System.out.println("xOffset=" + xOffset + ",zOffset=" + zOffset);
		for(int i=0;i<this.blocks.length;i++)
			for(int j=0;j<this.blocks[i].length;j++)
			{
				final MaterialData b = this.blocks[i][j];
				// note that if b is null we do *nothing*, that is so we don't remove a block if there was one
				if(b != null)
				{
					layer	[(int) Math.round(erodeFactor*i) + xOffset]
							[(int) Math.round(erodeFactor*j) + zOffset] = b.clone();
				}
			}
		// construct the new layer, offsetting it a bit
		return new WitchTowerLayer(
				this.offsetX + Util.RANDOM.nextDouble() * params.layerNoise - params.layerNoise / 2,
				this.offsetZ + Util.RANDOM.nextDouble() * params.layerNoise - params.layerNoise / 2,
				layer);
	}

	/**
	 * Build the layer at the given location.
	 * @param from Minimum corner where to build the layer
	 */
	@SuppressWarnings("deprecation")
	public void buildAround(final Location from)
	{
		final Location loc = from.clone();
		loc.setX(from.getX() + this.offsetX);
		for(int i=0;i<this.blocks.length;i++)
		{
			loc.setZ(from.getZ() + this.offsetZ);
			for(int j=0;j<this.blocks[i].length;j++)
			{
				final Block b = loc.getBlock();
				final MaterialData m = this.blocks[i][j];
				if(m == null)
					b.setType(Material.AIR);
				else
				{
					b.setType(m.getItemType());
					b.setData(m.getData());
				}
				loc.setZ(loc.getZ() + 1);
			}
			loc.setX(loc.getX() + 1);
		}
	}

	/**
	 * Delete the layer as if it was built at the given location.
	 * @param from Minimum corner where to delete the layer
	 */
	public void delete(final Location from)
	{
		// pretty much a copy paste from buildAround() function
		final Location loc = from.clone();
		loc.setX(from.getX() + this.offsetX);
		for(int i=0;i<this.blocks.length;i++)
		{
			loc.setZ(from.getZ() + this.offsetZ);
			for(int j=0;j<this.blocks[i].length;j++)
			{
				final Block b = loc.getBlock();
				b.setType(Material.AIR);
				loc.setZ(loc.getZ() + 1);
			}
			loc.setX(loc.getX() + 1);
		}
	}

	/**
	 * Get the center block location of the layer as if it was built at the given location.
	 * @param loc Minimum corner of layer
	 * @return The center of the layer
	 */
	public Location getCenter(final Location loc)
	{
		// getting the average of block positions
		int filled = 0, iSum = 0, jSum = 0;
		for(int i=0;i<this.blocks.length;i++)
			for(int j=0;j<this.blocks[i].length;j++)
			{
				if(this.blocks[i][j] != null)
				{
					filled++;
					iSum += i;
					jSum += j;
				}
			}
		// avoid divide by zero
		if(filled != 0)
			loc.add(iSum / filled, 0, jSum / filled);
		else
			return null;
		return loc;
	}

	//TODO redo basic layer generation, too square in some cases

	static private MaterialData[][] genLayer(final WitchTowerParameters params)
	{
		final MaterialData[][] layer = new MaterialData[params.baseSize][params.baseSize];
		for(int i=0;i<layer.length;i++)//(debug) fill with default block
			Arrays.fill(layer[i], null);
		final int x = params.baseSize/2;
		final int z = params.baseSize/2;
		WitchTowerLayer.expandBlockInLayer(layer, params.baseSize, params.baseSize, x, z);
		return layer;
	}

	/*
	 * recursive function that adds blocks besides another, becomes more unlikely to expand the further it is from center
	 * should be redone, there is a tiny chance a layer would be only composed of 1 block, or a really low amount
	 */
	static private void expandBlockInLayer(final MaterialData[][] layer, final int xSize, final int zSize, final int x, final int z)
	{
		// if out of bounds or block already filled
		if(!(x < xSize && z < zSize) || layer[x][z] != null && layer[x][z].getItemType() == Material.COBBLESTONE)
			return;
		layer[x][z] = new MaterialData(Material.COBBLESTONE);
		// x
		final int xRadius = ((int) xSize / 2);
		final int distX = Math.abs(x - xRadius);// x distance between current block and center
		// expand chance scales down the further we are from the center
		final double xExpandChance = (double)(xRadius - distX) / (double)(xRadius) * 0.8D * 100D;
		// expand in both direction if lucky enough
		if(Util.RANDOM.nextInt(100) < xExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x-1, z);
		if(Util.RANDOM.nextInt(100) < xExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x+1, z);
		// z (copy paste of x chunk above)
		final int zRadius = ((int) zSize / 2);
		final int distZ = Math.abs(z - zRadius);
		final double zExpandChance = (double)(zRadius - distZ) / (double)(zRadius) * 0.8D * 100D;
		if(Util.RANDOM.nextInt(100) < zExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x, z-1);
		if(Util.RANDOM.nextInt(100) < zExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x, z+1);
//		System.out.print("xRadius=" + xRadius + ", distX=" + distX + ", xExpandChance=" + xExpandChance + ", zRadius=" + zRadius + ", distZ=" + distZ + ", zExpandChance=" + zExpandChance);
	}

}
