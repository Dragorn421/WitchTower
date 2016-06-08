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

	public WitchTowerLayer(final WitchTowerParameters params)
	{
		this(Util.RANDOM.nextDouble()*4-2, Util.RANDOM.nextDouble()*4-2, params);
	}

	public WitchTowerLayer(final double offsetX, final double offsetZ, final WitchTowerParameters params)
	{
		this(offsetX, offsetZ, WitchTowerLayer.genLayer(params));
	}

	public WitchTowerLayer(final double offsetX, final double offsetZ, final MaterialData blocks[][])
	{
		/* trying to center the layer, maybe it doesnt work, no great results anyway
		int firstX = -1, firstZ = -1, lastX = -1, lastZ = -1;
		for(int x=0;x<blocks.length;x++)
			for(int z=0;z<blocks[x].length;z++)
			{
				if(blocks[x][z] != null)
				{
					if(firstX == -1)
						firstX = x;
					lastX = x;
					lastZ = z;
				}
			}
		for(int z=0;z<blocks[0].length;z++)
			for(int x=0;x<blocks.length;x++)
			{
				if(blocks[x][z] != null)
				{
					if(firstZ == -1)
						firstZ = z;
				}
			}
		this.offsetX = offsetX - firstX/2;
		this.offsetZ = offsetZ - firstZ/2;//*/
		this.blocks = blocks;
		// supposed to center the layer, which is useless anyway unless every layer is random
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
		this.offsetZ = offsetZ - centerOffsetZ;
		*/
		this.offsetX = offsetX;
		this.offsetZ = offsetZ;
	}

	public WitchTowerLayer next(final WitchTowerParameters params, final double erodeFactor)
	{
		final MaterialData[][] layer = new MaterialData[this.blocks.length][this.blocks[0].length];
		final int	xOffset = (int) ((this.blocks.length - this.blocks.length * erodeFactor) / 2),
					zOffset = (int) ((this.blocks[0].length - this.blocks[0].length * erodeFactor) / 2);
		//System.out.println("blocks.length=" + this.blocks.length + ",erodeFactor=" + erodeFactor);
		//System.out.println("xOffset=" + xOffset + ",zOffset=" + zOffset);
		for(int i=0;i<this.blocks.length;i++)
			for(int j=0;j<this.blocks[i].length;j++)
			{
				MaterialData b = this.blocks[i][j];
				if(b != null)
				{
					layer	[(int) Math.round(erodeFactor*i) + xOffset]
							[(int) Math.round(erodeFactor*j) + zOffset] = b.clone();
				}
			}
		return new WitchTowerLayer(this.offsetX + Util.RANDOM.nextDouble() * params.layerNoise - params.layerNoise / 2, this.offsetZ + Util.RANDOM.nextDouble() * params.layerNoise - params.layerNoise / 2, layer);
	}

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

	public void delete(final Location from)
	{
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

	public Location getCenter(final Location loc)
	{
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
		if(filled != 0)
		{
			loc.add(iSum / filled, 0, jSum / filled);
		}
		else
			return null;
		return loc;
	}

	//TODO redo basic layer generation, too square

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

	static private void expandBlockInLayer(final MaterialData[][] layer, final int xSize, final int zSize, final int x, final int z)
	{
		if(!(x < xSize && z < zSize) || layer[x][z] != null && layer[x][z].getItemType() == Material.COBBLESTONE)
			return;
		final MaterialData B = new MaterialData(Material.COBBLESTONE);
		layer[x][z] = B;
		final int xRadius = ((int) xSize / 2);
		final int distX = Math.abs(x - xRadius);
		final double xExpandChance = (double)(xRadius - distX) / (double)(xRadius) * 0.8D * 100D;
		if(Util.RANDOM.nextInt(100) < xExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x-1, z);
		if(Util.RANDOM.nextInt(100) < xExpandChance)
			WitchTowerLayer.expandBlockInLayer(layer, xSize, zSize, x+1, z);
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
