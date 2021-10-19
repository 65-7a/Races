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
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAttributedRace;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaceBehaviourShulk extends AbstractAbilityRaceBehaviour implements IAttributedRace {
    public static final AttributeModifier SHULK_ARMOR_POINTS = new AttributeModifier(UUID.fromString("7dbb7147-534a-4d0f-a9ef-3a3e87ece054"), "ShulkArmorPoints", 6, AttributeModifier.Operation.ADD_NUMBER);

    public static HashMap<UUID, Inventory> BACKPACKS = new HashMap<>();

    @Override
    public void onJoin(Player player) {
        final int rows = 4;
        Inventory inv = Bukkit.getServer().createInventory(player, 9 * rows, "Shulk's Backpack");
        FileConfiguration config = Races.getPlugin(Races.class).getBackpacksConfig();

        if (config.contains(String.valueOf(player.getUniqueId()))) {
            ConfigurationSection section = config.getConfigurationSection(String.valueOf(player.getUniqueId()));
            ItemStack[] contents;
            if (section.get("contents") instanceof ItemStack[]) {
                contents = (ItemStack[]) section.get("contents");
            } else {
                contents = ((List<ItemStack>) section.get("contents")).toArray(new ItemStack[0]);
            }
            inv.setContents(contents);
        } else {
            ItemStack f = RacesItems.getFillerItem();
            ItemStack n = new ItemStack(Material.AIR);
            inv.setContents(new ItemStack[]{
                    f, f, f, n, n, n, f, f, f,
                    f, f, f, n, n, n, f, f, f,
                    f, f, f, n, n, n, f, f, f,
                    f, f, f, f, n, f, f, f, f
            });
        }

        BACKPACKS.put(player.getUniqueId(), inv);

        player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(100, 0));
    }

    @Override
    public void onQuit(Player player) {
        FileConfiguration config = Races.getPlugin(Races.class).getBackpacksConfig();

        if (!config.contains(String.valueOf(player.getUniqueId()))) {
            config.createSection(String.valueOf(player.getUniqueId()));
        }

        ConfigurationSection section = config.getConfigurationSection(String.valueOf(player.getUniqueId()));
        section.set("contents", BACKPACKS.get(player.getUniqueId()).getContents());

        Races.getPlugin(Races.class).saveBackpacksConfig();
    }

    @Override
    public void onSecond(Player player) {
        if (Arrays.stream(Arrays.copyOfRange(player.getInventory().getContents(), 0, 8 + 1)).anyMatch(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Shulk's Backpack"))) {
                if (player.getInventory().getContents()[8] != null) {
                    ItemStack clone = player.getInventory().getContents()[8].clone();
                    player.getInventory().addItem(clone).forEach((integer, itemStack) -> {
                        player.getWorld().dropItem(player.getLocation(), itemStack);
                    }); // try to add the item to the player's inv, if not drop it to the floor
                }
                player.getInventory().setItem(8, RacesItems.getShulkBackpack());
            }
        }

        player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(100, 0));
    }

    @Override
    public @NotNull HashMap<Attribute, AttributeModifier> attributeModifiers() {
        HashMap<Attribute, AttributeModifier> hashMap = new HashMap<>();
        hashMap.put(Attribute.GENERIC_ARMOR, SHULK_ARMOR_POINTS);
        return hashMap;
    }

    @Override
    public HashMap<ItemStack, String> abilities() {
        HashMap<ItemStack, String> hashMap = new HashMap<>();
        hashMap.put(RacesItems.getShulkBackpack(), "Shulk's Backpack");
        return hashMap;
    }
}
