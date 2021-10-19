/*
 * Copyright (C) 2021  Callum Wong
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.callumwong.races;

import co.aikar.commands.BukkitCommandManager;
import com.callumwong.races.command.RacesCommand;
import com.callumwong.races.enchantment.Glow;
import com.callumwong.races.event.EventListener;
import com.callumwong.races.race.event.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;

public class Races extends JavaPlugin {
    public static final String NAME = "Races";

    private File racesFile;
    private File backpacksFile;
    private FileConfiguration racesConfig;
    private FileConfiguration backpacksConfig;

    @Override
    @SuppressWarnings("deprecation")
    public void onEnable() {
        saveDefaultConfig();
        getConfig().addDefault("FirstJoinMessage", "\u00a7fTo play on this server, you must select a class. Each class has different traits, advantages and disadvantages.\n" +
                "\u00a7cYou cannot change your class after selection. Think carefully about your choice.\n" +
                "\u00a7rTo list each race, use \u00a75/races list\n" +
                "\u00a7rTo select a race, use \u00a75/races select <race>");
        getConfig().addDefault("NetherWorldName", "world_nether");
        getConfig().addDefault("DestroyProhibitedItems", false);
        getConfig().addDefault("DestroyProhibitedArmor", false);
        getConfig().options().copyDefaults(true);
        saveConfig();

        createCustomConfigs();

        EventListener mainListener = new EventListener();
        getServer().getPluginManager().registerEvents(mainListener, this);
        getServer().getPluginManager().registerEvents(new EnderianEventListener(), this);
        getServer().getPluginManager().registerEvents(new MerlingEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlazebornEventListener(), this);
        getServer().getPluginManager().registerEvents(new AvianEventListener(), this);
        getServer().getPluginManager().registerEvents(new ArachnidEventListener(), this);
        getServer().getPluginManager().registerEvents(new ShulkEventListener(), this);
        getServer().getPluginManager().registerEvents(new VexianEventListener(), this);
        getServer().getPluginManager().registerEvents(new EvocationeerEventListener(), this);
        getServer().getPluginManager().registerEvents(new ElytrianEventListener(), this);
        getServer().getPluginManager().registerEvents(new StarborneEventListener(), this);

        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.registerCommand(new RacesCommand());

        registerGlow();

        new BukkitRunnable() {
            @Override
            public void run() {
                saveBackpacksConfig();
            }
        }.runTaskTimer(this, 0L, 6000L);

        if (getServer().getOnlinePlayers().size() > 0) {
            Bukkit.getConsoleSender().sendMessage("[Races] Reload detected. Please refrain from reloading as this may break the plugin.");
            for (Player player : getServer().getOnlinePlayers()) {
                mainListener.onPlayerJoin(new PlayerJoinEvent(player, null));
                if (player.isOp()) {
                    player.sendMessage(ChatColor.DARK_RED + "[Races] Reload detected. Please refrain from reloading as this may break the plugin.");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        saveRacesConfig();
        saveBackpacksConfig();
        saveConfig();
    }

    public FileConfiguration getRacesConfig() {
        return racesConfig;
    }

    public FileConfiguration getBackpacksConfig() {
        return backpacksConfig;
    }

    private void createCustomConfigs() {
        racesFile = new File(getDataFolder(), "races.yml");
        backpacksFile = new File(getDataFolder(), "backpacks.yml");
        if (!racesFile.exists()) {
            racesFile.getParentFile().mkdirs();
            saveResource("races.yml", false);
        }
        if (!backpacksFile.exists()) {
            backpacksFile.getParentFile().mkdirs();
            saveResource("backpacks.yml", false);
        }

        racesConfig = new YamlConfiguration();
        backpacksConfig = new YamlConfiguration();
        try {
            racesConfig.load(racesFile);
            backpacksConfig.load(backpacksFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveRacesConfig() {
        try {
            racesConfig.save(racesFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + racesFile, e);
        }
    }

    public void saveBackpacksConfig() {
        try {
            backpacksConfig.save(backpacksFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save config to " + backpacksFile, e);
        }
    }

    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NamespacedKey key = new NamespacedKey(this, "glow");

            Glow glow = new Glow(key);
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
