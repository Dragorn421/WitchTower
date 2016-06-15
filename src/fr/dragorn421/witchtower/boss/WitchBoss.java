package fr.dragorn421.witchtower.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import fr.dragorn421.witchtower.WitchTowerPlugin;
import fr.dragorn421.witchtower.tower.WitchTower;
import fr.dragorn421.witchtower.util.Util;

public class WitchBoss implements Runnable
{

	private WitchTower tower;
	private Location loc;
	private LivingEntity mob;
	private List<Entity> attack;
	private int attackTimer;
	private BukkitTask task;
	private List<WTProjectile> thrown;
	private long straightLineTicks;

	/**
	 * Creates a new boss for the given tower.
	 * @param tower The tower this boss is attached to.
	 * @param loc The location where to spawn the boss.
	 */
	public WitchBoss(final WitchTower tower, final Location loc)
	{
		this.tower = tower;
		this.loc = loc.clone();
		this.loc.setPitch(0f);
		// boss entity spawn
		this.mob = this.loc.getWorld().spawn(this.loc, Witch.class);
//		this.mob.getEquipment().setHelmet(new ItemStack(Material.SKULL_ITEM));
		this.mob.getEquipment().setItemInMainHand(new ItemStack(Material.BLAZE_ROD));
		this.mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1D);
		this.mob.setAI(false);
		this.mob.setCollidable(false);// no effect?
		//TODO cant rotate the entity >.>
		//this.mob.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 255, false, false));// works for players only it seems
		this.mob.setInvulnerable(true);
		this.mob.setGlowing(true);// for fun
		Util.setMetadata(this.mob, WitchTowerPlugin.BOSS_BELONGS_TO_TOWER, this);
		this.attack = new ArrayList<>();
		this.attackTimer = 0;
		this.task = Bukkit.getScheduler().runTaskTimer(WitchTowerPlugin.get(), this, 1L, 1L);
		this.thrown = new ArrayList<>();
		// sqrt(2*(baseSize/2)²) = blocks = ticks (because speed = 1 block/tick)
		this.straightLineTicks = (long) Math.sqrt(this.tower.getParameters().baseSize*this.tower.getParameters().baseSize/2);
	}

	@Override
	public void run()
	{
		if(!Bukkit.getOnlinePlayers().isEmpty())
			this.mob.getEquipment().setItemInMainHand(Bukkit.getOnlinePlayers().iterator().next().getEquipment().getItemInMainHand());
		// if boss died (didn't use an event to catch it because can't catch armor stand break)
		if(!this.mob.isValid())
		{
			this.remove();
			return;
		}
		this.attackTimer++;
		// every 20 ticks, shoot a projectile at someone
		if(this.attackTimer == 20)
		{
			this.attackTimer = 0;
			final Entity e = this.getRandomAttack();
			if(e != null)
			{
				final Vector attackDirection = e.getLocation().toVector().subtract(this.loc.toVector()).setY(0).normalize();
				final double	x = attackDirection.getX(),
								z = attackDirection.getZ();
				float y = (float) -Math.toDegrees(Math.atan(x / z)) + (z<0?(x<0?180:-180):0);
				this.loc.setYaw(y);
				// the projectile will go in a straight line (y stays the same all along)
				// in the direction of the target then after 2 seconds will follow the target
				final WTProjectile wtp = new WTProjectile(this.loc);
				wtp.setDirection(attackDirection);
				this.thrown.add(wtp);
				final BukkitTask followTask = Bukkit.getScheduler().runTaskLater(WitchTowerPlugin.get(), new Runnable() {
					@Override
					public void run()
					{
						wtp.setFollow(e, 1D);
					}
				}, this.straightLineTicks);
				wtp.addObserver(new Observer() {
					@Override
					public void update(final Observable o, final Object arg)
					{
						followTask.cancel();
						WitchBoss.this.thrown.remove(o);
					}
				});
			}
			//this.mob.teleport(this.loc);
			Util.dirtyTeleport(this.mob, this.loc);
		}
	}

	private Entity getRandomAttack()
	{
		switch(this.attack.size())
		{
		case 1:
			final Entity e = this.attack.get(0);
			if(e.isValid())
				return e;
			this.attack.clear();
		// FALL-THROUGH
		case 0:
			return null;
		}
		// get a random valid entity in the attack list
		Entity e = null;
		while(e == null && !this.attack.isEmpty())
		{
			final int i = Util.RANDOM.nextInt(this.attack.size());
			e = this.attack.get(i);
			if(!e.isValid())
			{
				this.attack.remove(i);
				e = null;
			}
		}
		return e;
	}

	/**
	 * Removes this boss. Removes all thrown projectiles.
	 * @throws IllegalStateException if this boss is removed already
	 */
	public void remove()
	{
		if(!this.isValid())
			throw new IllegalStateException("This WitchBoss has been removed already");
		this.task.cancel();
		this.task = null;
		Util.setMetadata(this.mob, WitchTowerPlugin.BOSS_BELONGS_TO_TOWER, null);
		this.mob.remove();
		this.mob = null;
		this.attack.clear();
		this.attack = null;
		while(this.thrown.size() != 0)
			this.thrown.remove(0).remove();
		this.thrown = null;
		this.tower = null;
	}

	/**
	 * A WitchBoss is valid as long as the {@link WitchBoss#remove()} method is not called.<br>
	 * It will also invalidate if the boss entity is not valid anymore, if the tower is deleted or if a new boss is created instead.
	 * @return true if this boss is valid
	 */
	public boolean isValid()
	{
		return this.task != null;
	}

	/**
	 * @return The tower this boss is linked to.
	 */
	public WitchTower getTower()
	{
		return this.tower;
	}

	/**
	 * Adds a target to this boss.
	 * @param e The entity that should be attacked
	 */
	public void addAttack(final Entity e)
	{
		if(!this.attack.contains(e))
			this.attack.add(e);
	}

}
