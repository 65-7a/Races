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
import com.callumwong.races.util.RacesUtils;
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

public class RaceBehaviourBlazeborn extends AbstractAbilityRaceBehaviour {
    public static final NamespacedKey BLAZEBORN_FIREBALL_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedBlazebornFireball");

    @Override
    public void onJoin(Player player) {
        player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(100, 0));
    }

    @Override
    public void onSelect(Player player) {
        addFireball(player);
    }

    @Override
    public void onRespawn(Player player) {
        addFireball(player);
    }

    @Override
    public void onTick(Player player) {
        if (player.getFireTicks() > 0) {
            player.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(40, 0));
        }

        player.removePotionEffect(PotionEffectType.HUNGER);
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WITHER);
    }

    private void addFireball(Player player) {
        if (Arrays.stream(Arrays.copyOfRange(player.getInventory().getContents(), 0, 8 + 1)).anyMatch(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Blazeborn's Fireball"))) {
                if (player.getInventory().getContents()[8] != null) {
                    ItemStack clone = player.getInventory().getContents()[8].clone();
                    player.getInventory().addItem(clone).forEach((integer, itemStack) -> {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }); // try to add the item to the player's inv, if not drop it to the floor
                }
                player.getInventory().setItem(8, RacesItems.getBlazebornFireball());
            }
        }
    }

    @Override
    public void onSecond(Player player) {
        super.onSecond(player);

        long cooldownEnd = player.getPersistentDataContainer().getOrDefault(BLAZEBORN_FIREBALL_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 4000L;
        String cooldownMessage = "";

        if (System.currentTimeMillis() >= cooldownEnd) {
            addFireball(player);
            cooldownMessage += "Fireball: READY ";
        } else cooldownMessage += "Fireball: " + ((cooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + cooldownMessage));

        if (player.getWorld().hasStorm()
                && player.getLocation().getBlock().getLightFromSky() >= 15
                && player.getWorld().getEnvironment() != World.Environment.NETHER && player.getWorld().getEnvironment() != World.Environment.THE_END
                && !Arrays.asList(RacesUtils.WARM_BIOMES).contains(player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()))
                || player.isInWater()) {
            player.damage(2);
        }

        player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(100, 0));
    }

    @Override
    public HashMap<ItemStack, String> abilities() {
        HashMap<ItemStack, String> hashMap = new HashMap<>();
        hashMap.put(RacesItems.getBlazebornFireball(), "Blazeborn's Fireball");
        return hashMap;
    }
}
