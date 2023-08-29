package me.cupcqkeee.heroevent;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class HeroEvent extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private boolean eventInProgress = false;


    @Override
    public void onEnable() {
        onLoad();
        config = getConfig();

        HerobrineManager herobrineManager;
        herobrineManager = new HerobrineManager(this);
        getCommand("reloadconfig").setExecutor(new HeroCommands(this, herobrineManager));
        getCommand("hstart").setExecutor(new HeroCommands(this, herobrineManager));
        getServer().getPluginManager().registerEvents(new HerobrineEventListener(this), this);

        Bukkit.getLogger().info(getName() + " успешно запущен");

    }

    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        }
    }
