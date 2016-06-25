package fr.dragorn421.witchtower;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.dragorn421.witchtower.parameters.WTParameters;
import fr.dragorn421.witchtower.tower.WitchTower;

public class WTManager
{

	static private WTManager instance = null;

	/**
	 * Stores existing WitchTower
	 */
	final private Map<Integer, WitchTower> towers;
	/**
	 * Custom WitchTower parameters per player
	 */
	final private Map<UUID, WTParameters> customParameters;

	private int nextTowerId;

	public WTManager()
	{
		this.towers = new HashMap<>();
		this.customParameters = new HashMap<>();
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

	/**
	 * Uses player UUID to associate custom parameters and a player, will be kept after disconnect.
	 * @param p Target player
	 * @return Parameters associated with given player.
	 */
	public WTParameters getCustomParameters(final Player p)
	{
		WTParameters wtp = this.customParameters.get(p.getUniqueId());
		if(wtp == null)
		{
			wtp = WTParameters.DEFAULT.clone();
			this.customParameters.put(p.getUniqueId(), wtp);
		}
		return wtp;
	}

	final static public WTManager get()
	{
		if(WTManager.instance == null)
			WTManager.instance = new WTManager();
		return WTManager.instance;
	}

}
