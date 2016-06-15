package fr.dragorn421.witchtower.tower;

public class WitchTowerParameters
{

	static enum Shape
	{

		/**
		 * A line
		 * eye -> |
		 */
		STRAIGHT,
		/**
		 * Curved
		 * eye -> )
		 */
		CONCAVE,
		/**
		 * Curved
		 * eye -> (
		 */
		CONVEX;

	}

	/**
	 * Default generation parameters
	 */
	final static public WitchTowerParameters DEFAULT = new WitchTowerParameters().height(30).baseSize(20).topSize(10).layerNoise(1).shape(Shape.CONCAVE);

	/**
	 * The number of layers
	 */
	public int height;
	/**
	 * The width/length of the base = bottom layer
	 */
	public int baseSize;
	/**
	 * The width/length of the top = top layer
	 */
	public int topSize;
	/**
	 * In which range varies the x and z offset for each layer
	 */
	public double layerNoise;
	/**
	 * The shape
	 */
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
