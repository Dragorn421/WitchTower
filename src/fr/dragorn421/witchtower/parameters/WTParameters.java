package fr.dragorn421.witchtower.parameters;

public class WTParameters
{

	/**
	 * The maximum height parameters can allow.
	 * Will only be used to handle in-game player input.
	 */
	final static public int MAX_HEIGHT = 100;

	/**
	 * The maximum base size parameters can allow.
	 * Will only be used to handle in-game player input.
	 */
	final static public int MAX_BASE_SIZE = 50;

	/**
	 * The maximum top size parameters can allow.
	 * Will only be used to handle in-game player input.
	 */
	final static public int MAX_TOP_SIZE = 50;

	static public enum Shape
	{

		/**
		 * A line<br>
		 * [eye] -> |
		 */
		STRAIGHT,
		/**
		 * Curved<br>
		 * [eye] -> )
		 */
		CONCAVE,
		/**
		 * Curved<br>
		 * [eye] -> (
		 */
		CONVEX;

	}

	/**
	 * Default generation parameters
	 */
	final static public WTParameters DEFAULT = new WTParameters().height(30).baseSize(20).topSize(10).layerNoise(1).shape(Shape.CONCAVE);

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

	public WTParameters height(final int height)
	{
		this.height = height;
		return this;
	}

	public WTParameters baseSize(final int baseSize)
	{
		this.baseSize = baseSize;
		return this;
	}

	public WTParameters topSize(final int topSize)
	{
		this.topSize = topSize;
		return this;
	}

	public WTParameters layerNoise(final double layerNoise)
	{
		this.layerNoise = layerNoise;
		return this;
	}

	public WTParameters shape(final Shape shape)
	{
		this.shape = shape;
		return this;
	}

	@Override
	public WTParameters clone()
	{
		return new WTParameters()
					.height(this.height)
					.baseSize(this.baseSize)
					.topSize(this.topSize)
					.layerNoise(this.layerNoise)
					.shape(this.shape);
	}

}
