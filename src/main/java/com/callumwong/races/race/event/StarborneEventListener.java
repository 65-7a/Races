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
import com.callumwong.races.race.behaviour.RaceBehaviourStarborne;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class StarborneEventListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (RacesUtils.getRace(event.getEntity()) == RaceType.STARBORNE) {
            Long cooldown = event.getEntity().getPersistentDataContainer().get(RaceBehaviourStarborne.STARBORNE_DEATH_KEY, PersistentDataType.LONG);
            // preventing NullPointerException from unboxing (should'nt be too performance heavy, as long as people don't die every second)
            if (cooldown != null) {
                long realCooldownEnd = cooldown + 1800000L; // 30 mins
                if (System.currentTimeMillis() >= realCooldownEnd) {
                    Location location = event.getEntity().getLocation();
                    event.getEntity().getWorld().createExplosion(location, ThreadLocalRandom.current().nextInt(4, 5 + 1), false, true, event.getEntity());
                    event.getEntity().getPersistentDataContainer().set(RaceBehaviourStarborne.STARBORNE_DEATH_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                }
            } else {
                Location location = event.getEntity().getLocation();
                event.getEntity().getWorld().createExplosion(location, ThreadLocalRandom.current().nextInt(4, 5 + 1), false, true, event.getEntity());
                event.getEntity().getPersistentDataContainer().set(RaceBehaviourStarborne.STARBORNE_DEATH_KEY, PersistentDataType.LONG, System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RacesUtils.getRace(player) == RaceType.STARBORNE && event.getDamager() instanceof Player) {
                if (ThreadLocalRandom.current().nextInt(0, 10) == 0) {
                    player.addPotionEffect(PotionEffectType.SLOW.createEffect(40, 9));
                    player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(40, 0));
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (RacesUtils.getRace((Player) event.getEntity()) == RaceType.STARBORNE && (event.getCause() == EntityDamageEvent.DamageCause.FIRE
                    || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
                event.setDamage(event.getDamage() * 2);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null) {
                if (event.getItem().getItemMeta().getLocalizedName().equals("Starborne's Dash")) {
                    Location oldLocation = event.getPlayer().getLocation();
                    if (event.getPlayer().isOnGround())
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, 0.5, 0)));
                    Location location = event.getPlayer().getLocation();
                    location.setPitch(0);
                    event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(location.getDirection().multiply(new Vector(2.0, 0, 2.0))));

                    new BukkitRunnable() {
                        private int i = 0;

                        @Override
                        public void run() {
                            i++;

                            if (i > 20) {
                                AreaEffectCloud aoe = (AreaEffectCloud) event.getPlayer().getWorld().spawnEntity(oldLocation.add(0, 0.5, 0), EntityType.AREA_EFFECT_CLOUD);
                                aoe.setRadius(2f);
                                aoe.setParticle(Particle.FIREWORKS_SPARK);
                                aoe.addCustomEffect(PotionEffectType.HARM.createEffect(1, 0), false);
                                aoe.setDuration(60);
                                aoe.setReapplicationDelay(20);
                                this.cancel();
                                return;
                            }

                            event.getPlayer().getWorld().spawnParticle(Particle.REDSTONE, event.getPlayer().getLocation().add(0, 1, 0), 1, new Particle.DustOptions(Color.PURPLE, 1));
                        }
                    }.runTaskTimer(Races.getPlugin(Races.class), 0L, 1L);

                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                        event.getItem().setAmount(event.getItem().getAmount() - 1);

                    event.getPlayer().getPersistentDataContainer().set(RaceBehaviourStarborne.STARBORNE_FEATHER_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                    event.setCancelled(true);
                } else if (event.getItem().getItemMeta().getLocalizedName().equals("Starborne's Beam")) {
                    RayTraceResult rayTrace = event.getPlayer().getWorld().rayTrace(event.getPlayer().getEyeLocation(), event.getPlayer().getEyeLocation().getDirection(),
                            128, FluidCollisionMode.NEVER, true, 1, entity -> !(entity instanceof Player));

                    if (rayTrace != null) {
                        Location target = rayTrace.getHitPosition().toLocation(event.getPlayer().getWorld());
                        spawnParticleAlongLine(event.getPlayer().getEyeLocation(), target, Particle.REDSTONE, 30, 3,
                                0.25, 0.25, 0.25, 0, new Particle.DustOptions(Color.PURPLE, 1), false, null);
                        spawnParticleAlongLine(event.getPlayer().getEyeLocation(), target, Particle.FIREWORKS_SPARK, 30, 1,
                                0.5, 0.5, 0.5, 0, null, false, null);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getPlayer().getWorld().createExplosion(target, 2f, false, false, event.getPlayer());
                            }
                        }.runTaskLater(Races.getPlugin(Races.class), 4L);
                    }

                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
                        event.getItem().setAmount(event.getItem().getAmount() - 1);

                    event.getPlayer().getPersistentDataContainer().set(RaceBehaviourStarborne.STARBORNE_STAR_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                    event.setCancelled(true);
                }
            }
        }
    }

    private <T> void spawnParticleAlongLine(Location start, Location end, Particle particle, int pointsPerLine, int particleCount, double offsetX, double offsetY, double offsetZ,
                                            double extra, @Nullable T data, boolean forceDisplay, @Nullable Predicate<Location> operationPerPoint) {
        double d = start.distance(end) / pointsPerLine;
        for (int i = 0; i < pointsPerLine; i++) {
            Location l = start.clone();
            Vector direction = end.toVector().subtract(start.toVector()).normalize();
            Vector v = direction.multiply(i * d);
            l.add(v.getX(), v.getY(), v.getZ());
            if (operationPerPoint == null) {
                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, data, forceDisplay);
                continue;
            }
            if (operationPerPoint.test(l)) {
                start.getWorld().spawnParticle(particle, l, particleCount, offsetX, offsetY, offsetZ, extra, data, forceDisplay);
            }
        }
    }
}
