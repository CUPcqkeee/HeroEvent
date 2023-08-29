package me.cupcqkeee.heroevent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HeroCommands implements CommandExecutor {

    private final Plugin plugin;
    private final HerobrineManager herobrineManager;

    public HeroCommands(Plugin plugin, HerobrineManager herobrineManager) {
        this.plugin = plugin;
        this.herobrineManager = herobrineManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadconfig")) {
            if (sender instanceof Player || sender.hasPermission("herobrine.admin")) {
                plugin.reloadConfig();
                sender.sendMessage(plugin.getName() + " Успешно перезагружен");
            } else {
                sender.sendMessage("У вас нет прав для использования этой команды");
            }
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("hstart")) {
                herobrineManager.startEventForced();
                player.sendMessage("Ивент спавна Herobrine начат.");
                return true;
            }
        }
        return false;
    }
}
