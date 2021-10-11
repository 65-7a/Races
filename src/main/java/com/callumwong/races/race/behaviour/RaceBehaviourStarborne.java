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

package com.callumwong.races.race.behaviour;

import com.callumwong.races.Races;
import com.callumwong.races.item.RacesItems;
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAbilitiesRace;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;

public class RaceBehaviourStarborne extends AbstractRaceBehaviour implements IAbilitiesRace {
    public static final NamespacedKey STARBORNE_FEATHER_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedStarborneFeather");
    public static final NamespacedKey STARBORNE_STAR_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedStarborneStar");
    public static final NamespacedKey STARBORNE_DEATH_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedStarborneDeathExplosion");

    @Override
    public void onSelect(Player player) {
        addFeather(player);
        addStar(player);
    }

    @Override
    public void onRespawn(Player player) {
        addFeather(player);
        addStar(player);
    }

    @Override
    public void onSecond(Player player) {
        long featherCooldownEnd = player.getPersistentDataContainer().getOrDefault(STARBORNE_FEATHER_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 10000L;
        long starCooldownEnd = player.getPersistentDataContainer().getOrDefault(STARBORNE_STAR_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 45000L;
        long deathCooldownEnd = player.getPersistentDataContainer().getOrDefault(STARBORNE_DEATH_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 1800000L; // 30 mins
        String cooldownMessage = "";

        if (System.currentTimeMillis() >= featherCooldownEnd) {
            addFeather(player);
            cooldownMessage += "Dash: READY ";
        } else cooldownMessage += "Dash: " + ((featherCooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        if (System.currentTimeMillis() >= starCooldownEnd) {
            addStar(player);
            cooldownMessage += "Beam: READY ";
        } else cooldownMessage += "Beam: " + ((starCooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        if (System.currentTimeMillis() < deathCooldownEnd) cooldownMessage += " Death Explosion: " + ((deathCooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";
        else cooldownMessage += "Death Explosion: READY ";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + cooldownMessage));

        if (!isDay(player.getWorld())) {
            player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(40, 1));
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(40, 0));
            player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(40, 0));
        }
    }

    private void addFeather(Player player) {
        if (player.getInventory().firstEmpty() > -1) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Starborne's Dash"))) {
                if (player.getInventory().getContents()[8] == null) {
                    player.getInventory().setItem(8, RacesItems.getStarborneFeather());
                } else {
                    player.getInventory().addItem(RacesItems.getStarborneFeather());
                }
            }
        }
    }

    private void addStar(Player player) {
        if (player.getInventory().firstEmpty() > -1) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Starborne's Beam"))) {
                if (player.getInventory().getContents()[7] == null) {
                    player.getInventory().setItem(7, RacesItems.getStarborneStar());
                } else {
                    player.getInventory().addItem(RacesItems.getStarborneStar());
                }
            }
        }
    }

    private boolean isDay(World world) {
        long time = world.getTime();
        return time < 12300 || time > 23850;
    }

    @Override
    public HashMap<ItemStack, String> abilities() {
        HashMap<ItemStack, String> hashMap = new HashMap<>();
        hashMap.put(RacesItems.getStarborneFeather(), "Starborne's Dash");
        hashMap.put(RacesItems.getStarborneStar(), "Starborne's Beam");
        return hashMap;
    }
}
