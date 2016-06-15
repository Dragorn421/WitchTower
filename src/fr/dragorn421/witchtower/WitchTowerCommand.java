package fr.dragorn421.witchtower;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.dragorn421.witchtower.boss.WTProjectile;
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
				wtp.setFollow(p, 1);
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
				// search for nearby entities: if none, makes the projectile go in a straight line
				final Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, 5, 5, 5);
				Entity follow = null;
				for(final Entity e : nearby)
				{
					if(e != p)// if player is too close to his targeted block he may not be a target
						follow = e;
				}
				wtp = new WTProjectile(p.getLocation());
				if(follow == null)
					wtp.setDirection(p.getLocation().getDirection());
				else
					wtp.setFollow(follow, 1);
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
				final WitchTowerParameters params = WitchTowerParameters.DEFAULT;//TODO allow custom parameters
				// so that the tower center is at the pointed block
				loc.add(-params.baseSize/2D, 1D, -params.baseSize/2D);
				final long start = System.nanoTime();
				witchTower = new WitchTower(loc, params, false);
				final long end = System.nanoTime();
				witchTower.build(true);
				id = WTManager.get().registerTower(witchTower);
				sender.sendMessage("#" + id + " done (" + (end - start)/1000000D + "ms)");
				witchTower.getTop().getBlock().setType(Material.GLOWSTONE);// debug
				return true;
			case "witch":
				if(args.length == 1)
				{
					sender.sendMessage("No tower id");
					return false;
				}
				try {
					id = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					sender.sendMessage("Not a valid number");
					return false;
				}
				witchTower = WTManager.get().getTower(id);
				if(witchTower == null)
				{
					sender.sendMessage("Unknown tower id");
					return true;
				}
				witchTower.newBoss();
				sender.sendMessage("Spawned new boss");
				return true;
			}
		}
		return false;
	}

}
