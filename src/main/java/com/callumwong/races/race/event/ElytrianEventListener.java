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

import com.callumwong.races.race.RaceType;
import com.callumwong.races.race.behaviour.RaceBehaviourElytrian;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

public class ElytrianEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getLocalizedName().equals("Elytrian's Launch")) {
                event.getPlayer().addPotionEffect(PotionEffectType.JUMP.createEffect(60, 9));

                if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                    event.getItem().setAmount(event.getItem().getAmount() - 1);

                event.getPlayer().getPersistentDataContainer().set(RaceBehaviourElytrian.ELYTRIAN_FEATHER_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RacesUtils.getRace(player) == RaceType.ELYTRIAN) {
                if (event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
                    event.setDamage(event.getDamage() * 2);
                }
            }
        }
    }
}
