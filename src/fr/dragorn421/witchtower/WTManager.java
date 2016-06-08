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

	public int registerTower(final WitchTower tower)
	{
		this.towers.put(this.nextTowerId, tower);
		return this.nextTowerId++;
	}

	public WitchTower getTower(final int id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void clear()
	{
		for(final WitchTower t : this.towers.values())
			t.delete();
		this.towers.clear();
	}

	final static public WTManager get()
	{
		if(WTManager.instance == null)
			WTManager.instance = new WTManager();
		return WTManager.instance;
	}

}
