package fr.dragorn421.witchtower;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.dragorn421.witchtower.boss.WTProjectile;
import fr.dragorn421.witchtower.boss.WitchBoss;
import fr.dragorn421.witchtower.tower.WitchTower;
import fr.dragorn421.witchtower.tower.WitchTowerParameters;
import fr.dragorn421.witchtower.util.Util;

public class WitchTowerCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String args[]) {
		if(args.length != 0)
		{
			final Player p;
			final Location loc;
			final WTProjectile wtp;
			final WitchTower witchTower;
			final int id;
			switch(args[0].toLowerCase())
			{
			case "projectile":
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only");
					return true;
				}
				p = (Player) sender;
				wtp = new WTProjectile(p.getLocation());
				wtp.setFollow(p);
				Bukkit.getScheduler().runTaskLater(WitchTowerPlugin.get(), new Runnable(){
					@Override
					public void run()
					{
						//wtp.remove();
					}
				}, 200L);
				return true;
			case "shoot":
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only");
					return true;
				}
				p = (Player) sender;
				loc = Util.getLookedAt(p);
				if(loc == null)
				{
					p.sendMessage("You need to look at a block");
					return true;
				}
				final Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, 5, 5, 5);
				wtp = new WTProjectile(p.getLocation());
				if(nearby.size() == 0)
					wtp.setDirection(p.getLocation().getDirection());
				else
					wtp.setFollow(nearby.iterator().next());
				Bukkit.getScheduler().runTaskLater(WitchTowerPlugin.get(), new Runnable(){
					@Override
					public void run()
					{
						//wtp.remove();
					}
				}, 200L);
				return true;
			case "tower":
				if(!(sender instanceof Player))
				{
					sender.sendMessage("Players only");
					return true;
				}
				p = (Player) sender;
				loc = Util.getLookedAt(p);
				if(loc == null)
				{
					p.sendMessage("You need to look at a block");
					return true;
				}
				final WitchTowerParameters params = WitchTowerParameters.DEFAULT;
				loc.add(-params.baseSize/2D, 1D, -params.baseSize/2D);
				final long start = System.nanoTime();
				witchTower = new WitchTower(loc, params, false);
				final long end = System.nanoTime();
				witchTower.build();
				id = WTManager.get().registerTower(witchTower);
				sender.sendMessage("#" + id + " done (" + (end - start)/1000000D + "ms)");
				witchTower.getTop().getBlock().setType(Material.GLOWSTONE);
				return true;
			case "witch":
				try {
					id = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					return false;
				}
				witchTower = WTManager.get().getTower(id);
				new WitchBoss(witchTower.getTop());
				return true;
			}
		}
		return false;
	}

}
