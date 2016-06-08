package fr.dragorn421.witchtower;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.dragorn421.witchtower.util.YAMLConfigHandler;

public class WitchTowerPlugin extends JavaPlugin// implements Listener
{

	static private WitchTowerPlugin instance;

	private YAMLConfigHandler yamlConfigHandler;

	@Override
	public void onEnable()
	{
		WitchTowerPlugin.instance = this;
		try {
			this.yamlConfigHandler = new YAMLConfigHandler(this);
		} catch (final IOException e) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				@Override
				public void run()
				{
					Bukkit.getPluginManager().disablePlugin(WitchTowerPlugin.instance);
				}
			}, 0L);
			throw new IllegalStateException("Unable to load configuration.", e);
		}
		WTManager.get();//load class so there is no error on disable if manager wasnt used
		Bukkit.getPluginCommand("witchtower").setExecutor(new WitchTowerCommand());
		Bukkit.getPluginManager().registerEvents(new WTListener(), this);
		super.getLogger().info(super.getName() + " enabled!");
	}

	@Override
	public void onDisable()
	{
		WTManager.get().clear();
		super.getLogger().info(super.getName() + " disabled!");
	}

	@Override
	public FileConfiguration getConfig()
	{
		return this.yamlConfigHandler.getConfig();
	}

	@Override
	public void reloadConfig()
	{
		this.yamlConfigHandler.reloadConfigSilent();
	}

	@Override
	public void saveConfig()
	{
		this.yamlConfigHandler.save();
	}

	static public WitchTowerPlugin get()
	{
		return WitchTowerPlugin.instance;
	}

}
