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
import com.callumwong.races.race.behaviour.RaceBehaviourBlazeborn;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class BlazebornEventListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null && event.getItem().getItemMeta().getLocalizedName().equals("Blazeborn's Fireball")) {
                Fireball fireball = event.getPlayer().getWorld().spawn(event.getPlayer().getEyeLocation(), Fireball.class);
                fireball.setYield(0);
                fireball.setShooter(event.getPlayer());
                fireball.setCustomName("BlazebornFireball");
                fireball.setDirection(event.getPlayer().getLocation().getDirection());
                fireball.setVelocity(fireball.getVelocity().multiply(4));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!fireball.isDead()) {
                            fireball.getWorld().spawnParticle(Particle.FLAME, fireball.getLocation(), 1);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Races.getPlugin(Races.class), 0L, 2L);

                if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                    event.getItem().setAmount(event.getItem().getAmount() - 1);

                event.getPlayer().getPersistentDataContainer().set(RaceBehaviourBlazeborn.BLAZEBORN_FIREBALL_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                event.setCancelled(true);
            } else if (event.getItem() != null && event.getItem().getType() == Material.SHIELD && RacesUtils.getRace(event.getPlayer()) == RaceType.BLAZEBORN) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't use that.");
                event.setCancelled(true);
                if (Races.getPlugin(Races.class).getConfig().getBoolean("DestroyProhibitedItems")) {
                    if (event.getItem() != null) event.getItem().setAmount(0);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Fireball && event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equalsIgnoreCase("BlazebornFireball")) {
            if (event.getHitEntity() == event.getEntity().getShooter() || event.getHitEntity() instanceof Fireball) {
                event.setCancelled(true);
            } else {
                event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 0.8f, true, false, event.getEntity());
                for (Entity entity : event.getEntity().getNearbyEntities(2.5, 3, 2.5)) {
                    if (entity instanceof LivingEntity) {
                        Vector awayFromFireball = entity.getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize();
                        if (entity == event.getHitEntity()) {
                            entity.setVelocity(awayFromFireball.add(new Vector(0, 0.8, 0)));
                        } else {
                            if (event.getHitEntity() instanceof Player && ((Player) event.getHitEntity()).isBlocking()) {
                                entity.setVelocity(awayFromFireball.multiply(new Vector(0.8, 0.8, 0.8)));
                            } else {
                                entity.setVelocity(awayFromFireball.add(new Vector(0, 0.5, 0)));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShootArrow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RacesUtils.getRace(player) == RaceType.BLAZEBORN) {
                event.getEntity().sendMessage(ChatColor.RED + "Can't use that.");
                event.setConsumeItem(false);
                event.setCancelled(true);
                player.updateInventory();
                if (Races.getPlugin(Races.class).getConfig().getBoolean("DestroyProhibitedItems")) {
                    if (event.getBow() != null) event.getBow().setAmount(0);
                    event.getBow().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn() || event.isAnchorSpawn() || RacesUtils.getRace(event.getPlayer()) != RaceType.BLAZEBORN)
            return;
        if (event.getPlayer().getServer().getWorld(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("NetherWorldName"))) != null) {
            Location oldLocation = event.getRespawnLocation();
            double y = oldLocation.getY() > 90.0 ? 64.0 : oldLocation.getY();
            event.setRespawnLocation(new Location(event.getPlayer().getServer().getWorld(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("NetherWorldName"))),
                    oldLocation.getX() / 8.0, y, oldLocation.getZ() / 8.0));
        }
    }
}
