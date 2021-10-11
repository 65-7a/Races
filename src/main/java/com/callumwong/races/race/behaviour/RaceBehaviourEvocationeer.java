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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;

public class RaceBehaviourEvocationeer extends AbstractRaceBehaviour implements IAbilitiesRace {
    public static final NamespacedKey EVOCATIONEER_WRATH_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedEvocationeerWrath");

    @Override
    public void onJoin(Player player) {
        player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(100, 0));
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(100, 1));
        player.addPotionEffect(PotionEffectType.JUMP.createEffect(100, 1));
        player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(100, 1));
    }

    @Override
    public void onSelect(Player player) {
        addWrath(player);
    }

    @Override
    public void onRespawn(Player player) {
        addWrath(player);
    }

    @Override
    public void onTick(Player player) {
        player.getNearbyEntities(10, 5, 10).forEach(entity -> {
            if (entity instanceof IronGolem) {
                if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                    ((IronGolem) entity).setTarget(player);
                }
            }
        });
    }

    private void addWrath(Player player) {
        if (player.getInventory().firstEmpty() > -1) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Evocationeer's Wrath"))) {
                if (player.getInventory().getContents()[8] == null) {
                    player.getInventory().setItem(8, RacesItems.getEvocationeerEmerald());
                } else {
                    player.getInventory().addItem(RacesItems.getEvocationeerEmerald());
                }
            }
        }
    }

    @Override
    public void onSecond(Player player) {
        long cooldownEnd = player.getPersistentDataContainer().getOrDefault(EVOCATIONEER_WRATH_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 10000L;
        String cooldownMessage = "";

        if (System.currentTimeMillis() >= cooldownEnd) {
            addWrath(player);
            cooldownMessage += "Wrath: READY ";
        } else cooldownMessage += "Wrath: " + ((cooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + cooldownMessage));

        player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(100, 0));
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(100, 1));
        player.addPotionEffect(PotionEffectType.JUMP.createEffect(100, 1));
        player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(100, 1));

        Arrays.stream(player.getInventory().getArmorContents()).filter(itemStack -> itemStack != null &&
                (itemStack.getType().name().toLowerCase().contains("diamond") || itemStack.getType().name().toLowerCase().contains("netherite"))).forEach(itemStack -> {
            if (!Races.getPlugin(Races.class).getConfig().getBoolean("DestroyProhibitedArmor")) {
                player.getInventory().addItem(itemStack);
            }
            itemStack.setAmount(0);
            player.sendMessage(ChatColor.RED + "Can't wear that.");
        });
    }

    @Override
    public HashMap<ItemStack, String> abilities() {
        HashMap<ItemStack, String> hashMap = new HashMap<>();
        hashMap.put(RacesItems.getEvocationeerEmerald(), "Evocationeer's Wrath");
        return hashMap;
    }
}
