package me.cupcqkeee.heroevent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HerobrineEventListener implements Listener {

    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public HerobrineEventListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        NPC npc = getHerobrineNPC();

        if (npc != null && npc.isSpawned()) {
            Location npcLocation = npc.getEntity().getLocation();
            double distance = player.getLocation().distance(npcLocation);

            if (distance <= 4) {
                npc.despawn();

                Location explosionLocation = npcLocation.clone().add(0, 1, 0);
                spawnParticles(explosionLocation);
                playSound(explosionLocation);

                saveConfigOptions(); // Сохраняем опции в конфигурацию

            }
        }
    }


    private void spawnParticles(Location location) {
        Particle particle = Particle.valueOf(config.getString("npc-disappear-particle"));
        location.getWorld().spawnParticle(
                particle,
                location,
                50, // Количество партиклов
                0.2, 0.2, 0.2, // Смещение для более плотных партиклов
                1 // Размер партиклов
        );
    }

    private void playSound(Location location) {
        Sound sound = Sound.valueOf(config.getString("npc-disappear-sound"));
        location.getWorld().playSound(
                location,
                sound,
                1.0f,
                0.5f
        );
    }

    private void saveConfigOptions() {
        plugin.saveConfig();
    }

    private NPC getHerobrineNPC() {
        int npcId = config.getInt("herobrine-npc-id");
        return CitizensAPI.getNPCRegistry().getById(npcId);
    }
}


