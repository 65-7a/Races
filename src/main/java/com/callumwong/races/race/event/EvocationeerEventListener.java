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
import com.callumwong.races.race.behaviour.RaceBehaviourEvocationeer;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class EvocationeerEventListener implements Listener {
    private static final NamespacedKey VEX_OWNER_KEY = new NamespacedKey(Races.getPlugin(Races.class), "ownerUUID");

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Boss || event.getEntity() instanceof IronGolem) return;
        if (event.getTarget() instanceof Player) {
            if (event.getEntity() instanceof Vex) {
                Vex vex = (Vex) event.getEntity();
                PersistentDataContainer container = vex.getPersistentDataContainer();
                if (!container.isEmpty() && container.has(VEX_OWNER_KEY, PersistentDataType.STRING) && container.get(VEX_OWNER_KEY, PersistentDataType.STRING) != null) {
                    UUID owner = UUID.fromString(container.get(VEX_OWNER_KEY, PersistentDataType.STRING));
                    Player player = (Player) event.getTarget();
                    if (player.getUniqueId().equals(owner) && RacesUtils.getRace(player) == RaceType.EVOCATIONEER) {
                        event.setCancelled(true);
                        event.setTarget(null);
                    }
                }
            } else {
                Player player = (Player) event.getTarget();
                if (RacesUtils.getRace(player) == RaceType.EVOCATIONEER) {
                    event.setCancelled(true);
                    event.setTarget(null);
                }

                if (event.getEntity() instanceof Mob) {
                    Mob mob = (Mob) event.getEntity();
                    if (mob.getTarget() instanceof Player && RacesUtils.getRace(((Player) mob.getTarget())) == RaceType.EVOCATIONEER) {
                        mob.setTarget(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getLocalizedName().equals("Evocationeer's Wrath")) {
                Location start = event.getPlayer().getLocation();
                Vector direction = start.getDirection();
                for (double i = 1.5; i < 9.0; i += 1.5) {
                    event.getPlayer().getWorld().spawnEntity(start.clone().add(direction.clone().multiply(new Vector(i, 0, i))), EntityType.EVOKER_FANGS);
                }

                Location location = event.getPlayer().getEyeLocation().add(0, 1, 0);
                for (int i = 0; i < 3; i++) {
                    LivingEntity entity = (LivingEntity) event.getPlayer().getWorld().spawnEntity(location, EntityType.VEX);
                    entity.getPersistentDataContainer().set(VEX_OWNER_KEY, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.remove();
                        }
                    }.runTaskLater(Races.getPlugin(Races.class), 200);
                }

                if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                    event.getItem().setAmount(event.getItem().getAmount() - 1);

                event.getPlayer().getPersistentDataContainer().set(RaceBehaviourEvocationeer.EVOCATIONEER_WRATH_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (RacesUtils.getRace(event.getPlayer()) == RaceType.EVOCATIONEER && event.getRightClicked() instanceof Villager) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't trade with villagers.");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RacesUtils.getRace(player) == RaceType.EVOCATIONEER) {
                if (event.getDamager() instanceof EvokerFangs) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
