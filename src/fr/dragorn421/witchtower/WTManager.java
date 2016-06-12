package fr.dragorn421.witchtower;

import java.util.HashMap;
import java.util.Map;

import fr.dragorn421.witchtower.tower.WitchTower;

public class WTManager
{

	static private WTManager instance = null;

	final private Map<Integer, WitchTower> towers;

	private int nextTowerId;

	public WTManager()
	{
		this.towers = new HashMap<>();
		this.nextTowerId = 0;
	}

	/**
	 * Registers a tower.
	 * @param tower
	 * @return id of registered tower.
	 */
	public int registerTower(final WitchTower tower)
	{
		this.towers.put(this.nextTowerId, tower);
		return this.nextTowerId++;
	}

	/**
	 * @param id Tower id
	 * @return Corresponding {@link WitchTower}, null if none
	 */
	public WitchTower getTower(final int id)
	{
		return this.towers.get(id);
	}

	/**
	 * Destroy all towers.
	 */
	public void clear()
	{
		for(final WitchTower t : this.towers.values())
			t.delete(true);
		this.towers.clear();
	}

	final static public WTManager get()
	{
		if(WTManager.instance == null)
			WTManager.instance = new WTManager();
		return WTManager.instance;
	}

}
