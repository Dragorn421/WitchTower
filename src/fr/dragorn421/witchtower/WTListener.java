package fr.dragorn421.witchtower;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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

}
