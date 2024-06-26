package me.dreamdevs.randomlootchest.managers;

import lombok.Getter;
import me.dreamdevs.randomlootchest.api.Config;
import me.dreamdevs.randomlootchest.RandomLootChestMain;
import me.dreamdevs.randomlootchest.api.event.player.PlayerCooldownExpiredEvent;
import me.dreamdevs.randomlootchest.api.event.player.PlayerCooldownSetEvent;
import me.dreamdevs.randomlootchest.database.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class CooldownManager {

    private final List<PlayerData> players;
    private final Map<Location, AtomicInteger> locations;

    public CooldownManager() {
        players = new ArrayList<>();
        locations = new ConcurrentHashMap<>();

        onSecond();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return players.stream().filter(playerData -> playerData.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public int getCooldownByLocation(Player player, Location location) {
        return getPlayerData(player.getUniqueId()).getLeftCooldownSeconds(location);
    }

    public int getCooldownForAllByLocation(Location location) {
        return locations.getOrDefault(location, new AtomicInteger(0)).get();
    }

    public void setCooldown(OfflinePlayer player, Location location, int seconds, boolean fireEvent) {
        PlayerData playerData = getPlayerData(player.getUniqueId());
        if (playerData == null) {
            playerData = new PlayerData(player.getUniqueId());
            players.add(playerData);
        }
        playerData.applyCooldown(location, seconds);
        if (fireEvent) {
            PlayerCooldownSetEvent rlcCooldownSetEvent = new PlayerCooldownSetEvent(player.getUniqueId(), location, seconds);
            Bukkit.getPluginManager().callEvent(rlcCooldownSetEvent);
        }
    }

    private void onSecond() {
        Bukkit.getScheduler().runTaskTimer(RandomLootChestMain.getInstance(), () -> {

            if (Config.USE_PERSONAL_COOLDOWN.toBoolean()) {
                if (players.isEmpty()) {
                    // Nothing else will happen...
                    return;
                }
                for (PlayerData playerData : getPlayers()) {
                    for (Location location : playerData.getCooldown().keySet()) {
                        int value = playerData.getCooldown().get(location).decrementAndGet();
                        if (value <= 0) {
                            playerData.getCooldown().remove(location);
                            PlayerCooldownExpiredEvent cooldownExpiredEvent = new PlayerCooldownExpiredEvent(playerData.getPlayer().getUniqueId(), location);
                            Bukkit.getPluginManager().callEvent(cooldownExpiredEvent);
                        }
                    }
                }
            } else {
                if (locations.isEmpty()) {
                    // Nothing else will happen...
                    return;
                }

                locations.forEach((location, atomicInteger) -> {
                    int value = atomicInteger.decrementAndGet();
                    if (value <= 0) {
                        if (location.getChunk().isLoaded()) {
                            location.getBlock().setType(Material.CHEST);
                            locations.remove(location);
                        }
                    }
                });
            }
        }, 0L, 20L);
    }

}