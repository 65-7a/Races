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
import com.callumwong.races.race.AbstractAbilityRaceBehaviour;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;

public class RaceBehaviourElytrian extends AbstractAbilityRaceBehaviour {
    public static final NamespacedKey ELYTRIAN_FEATHER_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedElytrianFeather");

    @Override
    public void onSelect(Player player) {
        addFeather(player);
    }

    @Override
    public void onRespawn(Player player) {
        addFeather(player);
    }

    @Override
    public void onReset(Player player) {
        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getItemMeta() != null
                && player.getInventory().getChestplate().getItemMeta().getLocalizedName().equals("Elytrian's Elytra")) {
            player.getInventory().setChestplate(null);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTick(Player player) {
        if (!player.isOnGround() && !player.isInWater()) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(40, 0));
        } else {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }

        if (player.getEyeLocation().add(0, 1, 0).getBlock().getType().isSolid() || player.getEyeLocation().add(0, 2, 0).getBlock().getType().isSolid()) {
            player.addPotionEffect(PotionEffectType.SLOW.createEffect(20, 1));
            player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(20, 0));
        }
    }

    private void addFeather(Player player) {
        if (Arrays.stream(Arrays.copyOfRange(player.getInventory().getContents(), 0, 8 + 1)).anyMatch(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Elytrian's Launch"))) {
                if (player.getInventory().getContents()[8] != null) {
                    ItemStack clone = player.getInventory().getContents()[8].clone();
                    player.getInventory().addItem(clone).forEach((integer, itemStack) -> {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }); // try to add the item to the player's inv, if not drop it to the floor
                }
                player.getInventory().setItem(8, RacesItems.getElytrianFeather());
            }
        }
    }

    @Override
    public void onSecond(Player player) {
        super.onSecond(player);

        long cooldownEnd = player.getPersistentDataContainer().getOrDefault(ELYTRIAN_FEATHER_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 30000L;
        String cooldownMessage = "";

        if (System.currentTimeMillis() >= cooldownEnd) {
            addFeather(player);
            cooldownMessage += "Launch: READY ";
        } else cooldownMessage += "Launch: " + ((cooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + cooldownMessage));

        if (player.getInventory().getChestplate() == null || player.getInventory().getChestplate().getType() != Material.ELYTRA) {
            player.getInventory().setChestplate(RacesItems.getElytrianElytra());
        }

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
        hashMap.put(RacesItems.getElytrianElytra(), "Elytrian's Elytra");
        hashMap.put(RacesItems.getElytrianFeather(), "Elytrian's Launch");
        return hashMap;
    }
}
