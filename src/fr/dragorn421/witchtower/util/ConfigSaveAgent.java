package fr.dragorn421.witchtower.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

final public class ConfigSaveAgent
{

	static public enum ValueType
	{

		BOOLEAN(false, Boolean.TYPE),
		BOOLEAN_O(false, Boolean.class),
		BOOLEAN_LIST(new ArrayList<Boolean>(0), List.class, Boolean.class),
		LONG(0L, Long.TYPE),
		LONG_O(0L, Long.class),
		LONG_LIST(new ArrayList<Long>(0), List.class, Long.class),
		DOUBLE(0D, Double.TYPE),
		DOUBLE_O(0D, Double.class),
		DOUBLE_LIST(new ArrayList<Double>(0), List.class, Double.class),
		FLOAT(0f, Float.TYPE),
		FLOAT_O(0f, Float.class),
		FLOAT_LIST(new ArrayList<Float>(0), List.class, Float.class),
		INTEGER(0, Integer.TYPE),
		INTEGER_O(0, Integer.class),
		INTEGER_LIST(new ArrayList<Integer>(0), List.class, Integer.class),
		SHORT_LIST(new ArrayList<Short>(0), List.class, Short.class),
		CHARACTER_LIST(new ArrayList<Character>(0), List.class, Character.class),
		COLOR(Color.WHITE, Color.class),
		CONFIGURATION_SECTION(null, ConfigurationSection.class),
		ITEM_STACK(new ItemStack(Material.AIR), ItemStack.class),
		OFFLINE_PLAYER(null, OfflinePlayer.class),
		STRING("", String.class),
		STRING_LIST(new ArrayList<String>(0), List.class, String.class),
		LIST(new ArrayList<Object>(0), List.class),
		VECTOR(new Vector(), Vector.class),

		ENUM(null, Enum.class),
		LOCATION(null, Location.class);

		final private Object defaultValue;
		final private Class<?> clazz;
		final private Class<?> generics[];

		private <T> ValueType(final T defaultValue, final Class<T> clazz, final Class<?> ...generics)
		{
			this.defaultValue = defaultValue;
			this.clazz = clazz;
			this.generics = generics;
		}

		final public Object getDefaultValue()
		{
			return this.defaultValue;
		}

		final static private ValueType get(final Field field)
		{
			for(final ValueType vt : ValueType.values())
			{
				if(ConfigSaveAgent.testType(field, vt.clazz, vt.generics))
				{
					return vt;
				}
			}
			return null;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		final public Object get(final String key, final ConfigurationSection c)
		{
			final ConfigurationSection cs;
			Object value = null;
			switch(this)
			{
			case BOOLEAN:
			case BOOLEAN_O:
				value = c.getBoolean(key);
				break;
			case BOOLEAN_LIST:
				value = c.getBooleanList(key);
				break;
			case CHARACTER_LIST:
				value = c.getCharacterList(key);
				break;
			case COLOR:
				value = c.getColor(key);
				break;
			case CONFIGURATION_SECTION:
				value = c.getConfigurationSection(key);
				break;
			case DOUBLE:
			case DOUBLE_O:
				value = c.getDouble(key);
				break;
			case DOUBLE_LIST:
				value = c.getDoubleList(key);
				break;
			case FLOAT:
			case FLOAT_O:
				value = (float) c.getDouble(key);
				break;
			case FLOAT_LIST:
				value = c.getFloatList(key);
				break;
			case INTEGER:
			case INTEGER_O:
				value = c.getInt(key);
				break;
			case INTEGER_LIST:
				value = c.getIntegerList(key);
				break;
			case ITEM_STACK:
				value = c.getItemStack(key);
				break;
			case LIST:
				value = c.getList(key);
				break;
			case LONG:
			case LONG_O:
				value = c.getLong(key);
				break;
			case LONG_LIST:
				value = c.getLongList(key);
				break;
			case OFFLINE_PLAYER:
				value = c.getOfflinePlayer(key);
				break;
			case SHORT_LIST:
				value = c.getShortList(key);
				break;
			case STRING:
				value = c.getString(key);
				break;
			case STRING_LIST:
				value = c.getStringList(key);
				break;
			case VECTOR:
				value = c.getVector(key);
				break;
			case ENUM:
				cs = c.getConfigurationSection(key);
				final Class<Enum> clazz;
				try {
					clazz = (Class<Enum>) Class.forName(cs.getString(key + "-class"));
				} catch(final ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				} catch(final ClassCastException e) {
					e.printStackTrace();
					return null;
				}
				try {
					value = Enum.valueOf(clazz, cs.getString(key));
				} catch(final IllegalArgumentException e) {
					e.printStackTrace();
					value = null;
				}
				break;
			case LOCATION:
				value = ConfigSaveAgent.getLocation(c.getConfigurationSection(key));
				break;
			}
			return value;
		}

		final public boolean set(final Object value, final String key, final ConfigurationSection c, final boolean overwrite)
		{
			boolean modified = false;
			final ConfigurationSection cs;
			switch(this)
			{
			case CONFIGURATION_SECTION:
				if(overwrite || !c.isConfigurationSection(key))
				{
					c.set(key, null);
					c.createSection(key);
				}
				cs = c.getConfigurationSection(key);
				for(final String k : ((ConfigurationSection) value).getKeys(false))
				{
					if(((ConfigurationSection) value).isConfigurationSection(k))
					{
						modified = ValueType.CONFIGURATION_SECTION.set(((ConfigurationSection) value).getConfigurationSection(k), k, cs, overwrite);
					}
					else
					{
						if(overwrite || !cs.isSet(k))
						{
							cs.set(k, ((ConfigurationSection) value).get(k));
							modified = true;
						}
					}
				}
				break;
			case ENUM:
				boolean validEnum = true;
				try {
					validEnum = ((Enum<?>) ValueType.ENUM.get(key, c)).getClass().equals(value.getClass());
				} catch(final Exception e) {
					validEnum = false;
				}
				if(overwrite || !validEnum)
				{
					c.set(key, null);
					cs = c.createSection(key);
					cs.set(key, ((Enum<?>) value).name());
					cs.set(key + "-class", value.getClass().getName());
					modified = true;
				}
				break;
			case LOCATION:
				if(overwrite || ConfigSaveAgent.getLocation(c.getConfigurationSection(key)) == null)
				{
					c.set(key, null);
					ConfigSaveAgent.setLocation(c.createSection(key), (Location) value);
					modified = true;
				}
				break;
				//$CASES-OMITTED$
			default:
				if(overwrite || !c.isSet(key))
				{
					c.set(key, value);
					modified = true;
				}
				break;
			}
			return modified;
		}

	}

	static abstract public class Config
	{

		final public void load(final ConfigurationSection config)
		{
			this.beforeLoading(config);
			ConfigSaveAgent.loadIn(this, config);
			this.afterLoading(config);
		}

		final public void save(final ConfigurationSection config)
		{
			this.beforeSaving(config);
			ConfigSaveAgent.save(this, config);
			this.afterSaving(config);
		}

		final public boolean saveNonExistent(final ConfigurationSection config)
		{
			this.beforeSaving(config);
			final boolean somethingWasMissing = ConfigSaveAgent.saveNonExistent(this, config);
			this.afterSaving(config);
			return somethingWasMissing;
		}

		final public boolean saveNonExistentAndLoad(final ConfigurationSection config)
		{
			final boolean somethingWasMissing = this.saveNonExistent(config);
			this.load(config);
			return somethingWasMissing;
		}

		protected void beforeSaving(final ConfigurationSection config) {}

		protected void afterSaving(final ConfigurationSection config) {}

		protected void beforeLoading(final ConfigurationSection config) {}

		protected void afterLoading(final ConfigurationSection config) {}

	}

	private ConfigSaveAgent() {}

	final static public void loadIn(final Object object, final ConfigurationSection config)
	{
		Field field;
		ValueType vt;
		Object value;
		for(final String key : config.getKeys(false))
		{
			field = ConfigSaveAgent.getField(object.getClass(), key);
			if(field == null)
				continue;
			field.setAccessible(true);
			if(field.isSynthetic())
			{
				continue;
			}
			if(Modifier.isTransient(field.getModifiers()))
			{
				continue;
			}
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			try {
				vt = ValueType.get(field);
				if(vt == null)
				{
					System.out.println("Unknown value type for field " + field.getName());
					continue;
				}
				value = vt.get(key, config);
				field.set(object, value);
			} catch (final Exception e) {
				System.out.println("Error while getting a value for: " + field.getName() + " from " + object.getClass().getName());
				e.printStackTrace();
			}
		}
	}

	final static public void save(final Object object, final ConfigurationSection config)
	{
		ValueType vt;
		Object value;
		for(final Field field : ConfigSaveAgent.getFields(object.getClass()))
		{
			field.setAccessible(true);
			if(field.isSynthetic())
			{
				continue;
			}
			if(Modifier.isTransient(field.getModifiers()))
			{
				continue;
			}
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			try {
				vt = ValueType.get(field);
				if(vt == null)
				{
					System.out.println("Unknown value type for field " + field.getName());
					continue;
				}
				value = field.get(object);
				vt.set(value, field.getName(), config, true);
			} catch (final Exception e) {
				System.out.println("Error while saving: " + field.getName());
				e.printStackTrace();
				continue;
			}
		}
	}

	final static public boolean saveNonExistent(final Object object, final ConfigurationSection config)
	{
		boolean somethingMissing = false;
		ValueType vt;
		Object value;
		for(final Field field : ConfigSaveAgent.getFields(object.getClass()))
		{
			field.setAccessible(true);
			if(field.isSynthetic())
			{
				continue;
			}
			if(Modifier.isTransient(field.getModifiers()))
			{
				continue;
			}
			if(Modifier.isStatic(field.getModifiers()))
			{
				continue;
			}
			try {
				vt = ValueType.get(field);
				if(vt == null)
				{
					System.out.println("Unknown value type for field " + field.getName());
					continue;
				}
				value = field.get(object);
				if(vt.set(value, field.getName(), config, false))
					somethingMissing = true;
			} catch (final Exception e) {
				System.out.println("Error while saving: " + field.getName());
				e.printStackTrace();
				continue;
			}
		}
		return somethingMissing;
	}

	final static public boolean saveNonExistentAndLoad(final Object object, final ConfigurationSection config)
	{
		final boolean somethingWasMissing = ConfigSaveAgent.saveNonExistent(object, config);
		ConfigSaveAgent.loadIn(object, config);
		return somethingWasMissing;
	}

	final static private void setLocation(ConfigurationSection cs, Location l)
	{
		if(cs == null || l == null)
			return;
		cs.set("world", l.getWorld().getName());
		cs.set("x", l.getX());
		cs.set("y", l.getY());
		cs.set("z", l.getZ());
		cs.set("yaw", l.getYaw());
		cs.set("pitch", l.getPitch());
	}

	final static private Location getLocation(ConfigurationSection cs)
	{
		try {
			return new Location(Bukkit.getWorld(cs.getString("world")), cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"), (float)cs.getDouble("yaw"), (float)cs.getDouble("pitch"));
		} catch (Exception e) {
			return null;
		}
	}

	final static private boolean testType(final Field field, final Class<?> type, final Class<?> ...generics)
	{
		//if the type of the field can't be cast to the required type, then return false
		if(!type.isAssignableFrom(field.getType()))
		{
			return false;
		}
		if(generics.length == 0)
		{
			return true;
		}
		try {
			final Type genericTypes[] = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
			for(int i=0;i<generics.length;i++)
			{
				if(!generics[i].equals(genericTypes[i]))
				{
					return false;
				}
			}
			return true;
		} catch(final Exception e) {
			return false;
		}
	}

	final static private Field getField(final Class<?> clazz, final String field)
	{
		try {
			try {
				return clazz.getField(field);
			} catch(final NoSuchFieldException e) {}
			try {
				return clazz.getDeclaredField(field);
			} catch(final NoSuchFieldException e) {}
		} catch(final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	final static private Set<Field> getFields(final Class<?> clazz)
	{
		final Set<Field> fields = new HashSet<>(Arrays.asList(clazz.getFields()));
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		return fields;
	}

}
