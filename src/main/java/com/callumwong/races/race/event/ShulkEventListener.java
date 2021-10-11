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

package com.callumwong.races.race.event;

import com.callumwong.races.Races;
import com.callumwong.races.item.RacesItems;
import com.callumwong.races.race.RaceType;
import com.callumwong.races.race.behaviour.RaceBehaviourShulk;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class ShulkEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getLocalizedName().equals("Shulk's Backpack")) {
                if (RacesUtils.getRace(event.getPlayer()) == RaceType.SHULK && RaceBehaviourShulk.BACKPACKS.get(event.getPlayer().getUniqueId()) != null) {
                    Inventory inventory = RaceBehaviourShulk.BACKPACKS.get(event.getPlayer().getUniqueId());
                    event.getPlayer().openInventory(inventory);
                }
                event.setCancelled(true);
            }
            if (RacesUtils.getRace(event.getPlayer()) == RaceType.SHULK && event.getItem() != null && event.getItem().getType() == Material.SHIELD) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't use that.");
                event.setCancelled(true);
                if (Races.getPlugin(Races.class).getConfig().getBoolean("DestroyProhibitedItems")) {
                    if (event.getItem() != null) event.getItem().setAmount(0);
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta() != null && event.getItemDrop().getItemStack().getItemMeta().getLocalizedName().equals("Shulk's Backpack")
                && RacesUtils.getRace(event.getPlayer()) == RaceType.SHULK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (RacesUtils.getRace(event.getEntity()) == RaceType.SHULK) {
            event.getDrops().removeIf(itemStack -> itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Shulk's Backpack"));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && RacesUtils.getRace(((Player) event.getWhoClicked())) == RaceType.SHULK) {
            if (event.getView().getTitle().equals("Shulk's Backpack") && event.getClickedInventory() != null) {
                if (event.getClickedInventory().getItem(event.getSlot()) != null && event.getClickedInventory().getItem(event.getSlot()).isSimilar(RacesItems.getFillerItem())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onExhaust(EntityExhaustionEvent event) {
        if (event.getEntity() instanceof Player && RacesUtils.getRace((Player) event.getEntity()) == RaceType.SHULK) {
            event.setExhaustion(event.getExhaustion() * 1.5f);
        }
    }
}
