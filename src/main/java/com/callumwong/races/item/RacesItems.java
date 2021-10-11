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

package com.callumwong.races.item;

import com.callumwong.races.Races;
import com.callumwong.races.enchantment.Glow;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RacesItems {
    public static ItemStack getArachnidCobweb() {
        ItemStack itemStack = new ItemStack(Material.COBWEB);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(new Glow(new NamespacedKey(Races.getPlugin(Races.class), "glow")), 1, true);
        meta.setDisplayName(ChatColor.AQUA + "Arachnid's Cobweb");
        meta.setLocalizedName("Arachnid's Cobweb");
        itemStack.setItemMeta(meta);
        itemStack.setAmount(8);
        return itemStack;
    }

    public static ItemStack getBlazebornFireball() {
        ItemStack itemStack = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Blazeborn's Fireball");
        meta.setLocalizedName("Blazeborn's Fireball");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getEnderianPearl() {
        ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.AQUA + "Enderian's Pearl");
        meta.setLocalizedName("Enderian's Pearl");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getEvocationeerEmerald() {
        ItemStack itemStack = new ItemStack(Material.EMERALD);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Evocationeer's Wrath");
        meta.setLocalizedName("Evocationeer's Wrath");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getShulkBackpack() {
        ItemStack itemStack = new ItemStack(Material.CHEST);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Shulk's Backpack");
        meta.setLocalizedName("Shulk's Backpack");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getElytrianElytra() {
        ItemStack itemStack = new ItemStack(Material.ELYTRA);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setUnbreakable(true);
        meta.setDisplayName(ChatColor.AQUA + "Elytrian's Elytra");
        meta.setLocalizedName("Elytrian's Elytra");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getElytrianFeather() {
        ItemStack itemStack = new ItemStack(Material.FEATHER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Elytrian's Launch");
        meta.setLocalizedName("Elytrian's Launch");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getStarborneFeather() {
        ItemStack itemStack = new ItemStack(Material.FEATHER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Starborne's Dash");
        meta.setLocalizedName("Starborne's Dash");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getStarborneStar() {
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setDisplayName(ChatColor.GOLD + "Starborne's Beam");
        meta.setLocalizedName("Starborne's Beam");
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getFillerItem() {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + "");
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
