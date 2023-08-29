package me.cupcqkeee.heroevent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Particle;

public class HerobrineManager {

    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final List<String> actionBarMessages;
    private boolean eventInProgress = false;
    long eventDuration;
    private NPC currentNPC;

    public HerobrineManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.eventDuration = config.getLong("event-duration") * 20;
        this.actionBarMessages = config.getStringList("action-bar-messages");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!eventInProgress && shouldStartEvent()) {
                    teleportRandomNPC();
                }
            }
        }.runTaskTimer(plugin, 0, 20 * 60); // 1 минута = 20 тиков * 60
    }


    private void teleportRandomNPC() {
        if (eventInProgress) {
            return;
        }
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            return;
        }

        eventInProgress = true;

        int randomIndex = new Random().nextInt(onlinePlayers.size());
        Player randomPlayer = onlinePlayers.get(randomIndex);
        Location randomLocation = getRandomTeleportLocation(randomPlayer.getLocation());

        if (!isLocationSafe(randomLocation)) {
            teleportRandomNPC(); // Повторяем спавн, если место небезопасно
            return;
        }

//        Bukkit.broadcastMessage("Ивент начался, был выбран " + randomPlayer.getName());

        NPC npc = getHerobrineNPC();
        if (npc != null) {
            Particle particleTypeAppear = Particle.valueOf(config.getString("particle-type-appear"));
            randomLocation.getWorld().spawnParticle(particleTypeAppear, randomLocation, 50);


            npc.spawn(randomLocation);
            npc.teleport(randomLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            npc.faceLocation(randomPlayer.getLocation());

            BukkitRunnable lookTask = new BukkitRunnable() {
                @Override
                public void run() {
                    npc.faceLocation(randomPlayer.getLocation());
                }
            };
            lookTask.runTaskTimer(plugin, 0, 1);

            randomPlayer.playSound(randomPlayer.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.5f);

            String randomMessage = getRandomActionBarMessage();
            randomPlayer.sendActionBar(randomMessage);

            currentNPC = npc;

            new BukkitRunnable() {
                @Override
                public void run() {
                    despawnNPC();
                }
            }.runTaskLater(plugin, 20 * 60); // 1 минута в тиках

        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            eventInProgress = false;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (shouldStartEvent()) {
                        teleportRandomNPC();
                    }
                }
            }.runTaskLater(plugin, 20 * 60 * 3); // 3 минуты в тиках
            eventInProgress = false;
        }, 20 * 10);
    }


    private boolean isLocationSafe(Location location) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
            if (entity instanceof Player) {
                return false; // Место занято игроком
            }
        }
        return true;
    }

    private void despawnNPC() {
        if (currentNPC != null) {
            currentNPC.despawn();
            currentNPC = null;
        }
    }

    private boolean shouldStartEvent() {
        int eventChance = config.getInt("event-chance");
        return new Random().nextInt(100) < eventChance;
    }

    private int getRandomCooldownTime() {
        return new Random().nextInt(8 * 60) + 3 * 60;
    }

    private Location getRandomTeleportLocation(Location playerLocation) {
        int distance = new Random().nextInt(6) + 5;
        Location randomLocation = playerLocation.clone().add(distance, 0, 0);
        return randomLocation;
    }

    private NPC getHerobrineNPC() {
        int npcId = config.getInt("herobrine-npc-id");
        return CitizensAPI.getNPCRegistry().getById(npcId);
    }

    private String getRandomActionBarMessage() {
        int randomIndex = new Random().nextInt(actionBarMessages.size());
        return actionBarMessages.get(randomIndex);
    }

    public void startEventForced() {
        if (!eventInProgress) {
            teleportRandomNPC();
        }
    }
}

