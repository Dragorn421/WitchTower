package fr.dragorn421.witchtower.parameters;

import fr.dragorn421.witchtower.parameters.WTParameters.Shape;

public enum ParameterType
{

	HEIGHT {
		@Override
		public String get(final WTParameters params)
		{
			return Integer.toString(params.height);
		}

		@Override
		public boolean set(final WTParameters params, final String value)
		{
			try {
				final int height = Integer.parseInt(value);
				if(height < 1 || height > WTParameters.MAX_HEIGHT)
					return false;
				params.height = height;
				return true;
			} catch(final IllegalArgumentException e) {
				return false;
			}
		}
	},
	BASE_SIZE {
		@Override
		public String get(final WTParameters params)
		{
			return Integer.toString(params.baseSize);
		}

		@Override
		public boolean set(final WTParameters params, final String value)
		{
			try {
				final int baseSize = Integer.parseInt(value);
				if(baseSize < 1 || baseSize > WTParameters.MAX_BASE_SIZE || baseSize < params.topSize)
					return false;
				params.baseSize = baseSize;
				return true;
			} catch(final IllegalArgumentException e) {
				return false;
			}
		}
	},
	TOP_SIZE {
		@Override
		public String get(final WTParameters params)
		{
			return Integer.toString(params.topSize);
		}

		@Override
		public boolean set(final WTParameters params, final String value)
		{
			try {
				final int topSize = Integer.parseInt(value);
				if(topSize < 1 || topSize > WTParameters.MAX_TOP_SIZE || topSize > params.baseSize)
					return false;
				params.topSize = topSize;
				return true;
			} catch(final IllegalArgumentException e) {
				return false;
			}
		}
	},
	LAYER_NOISE {
		@Override
		public String get(final WTParameters params)
		{
			return Double.toString(params.layerNoise);
		}

		@Override
		public boolean set(final WTParameters params, final String value)
		{
			try {
				final double layerNoise = Double.parseDouble(value);
				if(layerNoise < 0D)
					return false;
				params.layerNoise = layerNoise;
				return true;
			} catch(final IllegalArgumentException e) {
				return false;
			}
		}
	},
	SHAPE {
		@Override
		public String get(final WTParameters params)
		{
			return params.shape.toString();
		}

		@Override
		public boolean set(final WTParameters params, final String value)
		{
			try {
				params.shape = Shape.valueOf(value.toUpperCase());
				return true;
			} catch(final IllegalArgumentException e) {
				return false;
			}
		}
	};

	abstract public String get(final WTParameters params);

	abstract public boolean set(final WTParameters params, final String value);

}
