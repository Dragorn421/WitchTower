package fr.dragorn421.witchtower;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.dragorn421.witchtower.tower.WitchTower;
import fr.dragorn421.witchtower.util.Util;

public class WTListener implements Listener
{

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if(e.hasItem() && e.getItem().getType() == Material.MAGMA_CREAM)
		{
			// left click
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
				e.getPlayer().chat("/witchtower shoot");
			// right click
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				e.getPlayer().chat("/witchtower projectile");
		}
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent e)
	{
		final WitchTower tower = Util.getMetadata(e.getBlock(), WitchTowerPlugin.BLOCK_BELONGS_TO_TOWER, WitchTower.class);
		if(tower == null)
			return;
		e.getPlayer().sendMessage("You are placing disgusting blocks near my beautiful tower! Die!");
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e)
	{
		final WitchTower tower = Util.getMetadata(e.getBlock(), WitchTowerPlugin.BLOCK_BELONGS_TO_TOWER, WitchTower.class);
		if(tower == null)
			return;
		e.getPlayer().sendMessage("You are breaking my beautiful tower! Die!");
	}

}
