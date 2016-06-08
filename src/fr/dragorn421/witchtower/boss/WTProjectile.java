package fr.dragorn421.witchtower.boss;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import fr.dragorn421.witchtower.WitchTowerPlugin;
import fr.dragorn421.witchtower.util.Util;

public class WTProjectile
{

	private ArmorStand display[];
	private Vector direction;
	private BukkitTask task;
	private EntityTracker tracker;

	/**
	 * Creates a new WTProjectile. Will instantly spawn in armor stands and make them spin. Won't move.
	 * @param spawn Where to spawn it
	 */
	public WTProjectile(final Location spawn)
	{
		this.display = new ArmorStand[4];
		this.direction = new Vector();
		final short color = (short) Util.RANDOM.nextInt(16);// same random color for all four glass
		for(int i=0;i<this.display.length;i++)
		{
			final ArmorStand as = spawn.getWorld().spawn(spawn, ArmorStand.class);
			as.setHelmet(new ItemStack(Material.STAINED_GLASS, 1, color));
			as.setHeadPose(new EulerAngle(
				i==0?0:Math.toRadians(120),// first one stays on top, others below
				i*Math.toRadians(120),// spread last three armor stands around the first one
				0));
			as.setGravity(false);
			as.setVisible(false);
			as.setInvulnerable(true);
			this.display[i] = as;
		}
		// tick every tick
		this.task = Bukkit.getScheduler().runTaskTimer(WitchTowerPlugin.get(), new Runnable() {
			@Override
			public void run()
			{
				WTProjectile.this.tick();
			}
		}, 1L, 1L);
		this.tracker = null;
	}

	private void tick()
	{
		// all armor stands have the same location
		final Location loc = this.display[0].getLocation();
		// if block is not air or tracked entity is close enough
		if(!loc.getBlock().isEmpty() || (this.tracker != null && this.tracker.getDistanceLeft() < 0.5))
		{
			loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), 5f, false, true);
			//loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
			this.remove();
			return;
		}
		// if we got a tracker, override direction using it
		if(this.tracker != null)
			this.direction = this.tracker.next();
		// move and rotate each armor stand
		for(int i=0;i<this.display.length;i++)
		{
			final ArmorStand as = this.display[i];
			as.setHeadPose(as.getHeadPose().add(
				0,
				Math.toRadians(5),
				Math.toRadians(5)));
			as.teleport(as.getLocation(loc).add(this.direction));
		}
	}

	/**
	 * Removes this projectile, removes all armor stands.
	 */
	public void remove()
	{
		for(int i=0;i<this.display.length;i++)
			this.display[i].remove();
		this.display = null;
		this.task.cancel();
		this.task = null;
	}

	/**
	 * Set the direction of the projectile. Will have no effect if an entity is followed.
	 * @param direction The new direction.
	 */
	public void setDirection(final Vector direction)
	{
		this.direction = direction;
	}

	/**
	 * Makes this projectile follow an entity.
	 * @param follow The entity to follow. null to not follow
	 */
	public void setFollow(final Entity follow)
	{
		if(follow == null)
			this.tracker = null;
		else
			this.tracker = new EntityTracker(follow, this.display[0].getLocation());
	}

}
