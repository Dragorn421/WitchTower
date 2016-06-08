package fr.dragorn421.witchtower.tower;

public class WitchTowerParameters
{

	static enum Shape
	{

		STRAIGHT,
		CONCAVE,
		CONVEX;

	}

	final static public WitchTowerParameters DEFAULT = new WitchTowerParameters().height(150).baseSize(60).topSize(8).layerNoise(1).shape(Shape.CONCAVE);

	public int height;
	public int baseSize;
	public int topSize;
	public double layerNoise;
	public Shape shape;

	public WitchTowerParameters height(final int height)
	{
		this.height = height;
		return this;
	}

	public WitchTowerParameters baseSize(final int baseSize)
	{
		this.baseSize = baseSize;
		return this;
	}

	public WitchTowerParameters topSize(final int topSize)
	{
		this.topSize = topSize;
		return this;
	}

	public WitchTowerParameters layerNoise(final double layerNoise)
	{
		this.layerNoise = layerNoise;
		return this;
	}

	public WitchTowerParameters shape(final Shape shape)
	{
		this.shape = shape;
		return this;
	}

}
