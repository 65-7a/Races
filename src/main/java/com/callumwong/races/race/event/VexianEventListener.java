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
import com.callumwong.races.race.RaceType;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class VexianEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (RacesUtils.getRace(event.getPlayer()) == RaceType.VEXIAN & event.getItem() != null && event.getItem().getType() == Material.SHIELD) {
            event.getPlayer().sendMessage(ChatColor.RED + "Can't use that.");
            event.setCancelled(true);
            if (Races.getPlugin(Races.class).getConfig().getBoolean("DestroyProhibitedItems")) {
                if (event.getItem() != null) event.getItem().setAmount(0);
            }
        }
    }
}
