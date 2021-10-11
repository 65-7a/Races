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

package com.callumwong.races.event;

import com.callumwong.races.Races;
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAbilitiesRace;
import com.callumwong.races.race.IAttributedRace;
import com.callumwong.races.util.RacesUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (RacesUtils.isRaceInvalid(event.getPlayer())) {
            event.getPlayer().sendMessage("This server uses the " + ChatColor.BLUE + Races.NAME + ChatColor.RESET + " plugin by 65_7a.");
            event.getPlayer().sendMessage(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("FirstJoinMessage")));
            event.getPlayer().setInvulnerable(true);
        } else {
            AbstractRaceBehaviour behaviour = RacesUtils.getRace(event.getPlayer()).getBehaviour();
            if (behaviour instanceof IAttributedRace) {
                ((IAttributedRace) behaviour).attributeModifiers().forEach(((attribute, attributeModifier) -> {
                    if (event.getPlayer().getAttribute(attribute).getModifiers().stream().noneMatch(activeModifiers -> activeModifiers.equals(attributeModifier)))
                        event.getPlayer().getAttribute(attribute).addModifier(attributeModifier);
                }));
            }
            behaviour.onJoin(event.getPlayer());
            new BukkitRunnable() {
                @Override
                public void run() {
                    RacesUtils.getRace(event.getPlayer()).getBehaviour().onTick(event.getPlayer());
                }
            }.runTaskTimer(Races.getPlugin(Races.class), 0L, 1L);
            new BukkitRunnable() {
                @Override
                public void run() {
                    RacesUtils.getRace(event.getPlayer()).getBehaviour().onSecond(event.getPlayer());
                }
            }.runTaskTimer(Races.getPlugin(Races.class), 0L, 20L);
        }

        event.getPlayer().setGravity(true);
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getPlayer().getGameMode() != GameMode.SPECTATOR) {
            event.getPlayer().setAllowFlight(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (RacesUtils.isRaceInvalid(event.getPlayer())) {
            if (event.getTo() != event.getFrom()) {
                event.getPlayer().addPotionEffect(PotionEffectType.SLOW.createEffect(1000000, 255));
                event.getPlayer().addPotionEffect(PotionEffectType.JUMP.createEffect(1000000, 137));
                event.setTo(new Location(event.getFrom().getWorld(), event.getFrom().getX(), event.getFrom().getY(), event.getFrom().getZ(), event.getTo().getYaw(), event.getTo().getPitch()));
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player && RacesUtils.isRaceInvalid(((Player) event.getTarget()))) {
            event.setCancelled(true);
            event.setTarget(null);
        }

        if (event.getEntity() instanceof Mob) {
            Mob mob = (Mob) event.getEntity();
            if (mob.getTarget() instanceof Player && RacesUtils.isRaceInvalid(((Player) mob.getTarget()))) {
                mob.setTarget(null);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RacesUtils.getRace(event.getPlayer()).getBehaviour().onQuit(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        RacesUtils.getRace(event.getPlayer()).getBehaviour().onRespawn(event.getPlayer());
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (RacesUtils.isRaceInvalid(player)) {
                event.setCancelled(true);
            }

            if (RacesUtils.getRace(player).getBehaviour() instanceof IAbilitiesRace) {
                IAbilitiesRace abilitiesRace = (IAbilitiesRace) RacesUtils.getRace(player).getBehaviour();
                event.setCancelled(abilitiesRace.abilities().values().stream().anyMatch(s -> event.getItem().getItemStack().getItemMeta() != null && event.getItem().getItemStack().getItemMeta().getLocalizedName().equals(s)));
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (RacesUtils.isRaceInvalid(player)) {
            event.setCancelled(true);
        }

        if (RacesUtils.getRace(player).getBehaviour() instanceof IAbilitiesRace) {
            IAbilitiesRace abilitiesRace = (IAbilitiesRace) RacesUtils.getRace(player).getBehaviour();
            event.setCancelled(abilitiesRace.abilities().values().stream().anyMatch(s -> event.getItemDrop().getItemStack().getItemMeta() != null && event.getItemDrop().getItemStack().getItemMeta().getLocalizedName().equals(s)));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (RacesUtils.getRace(player).getBehaviour() instanceof IAbilitiesRace) {
            IAbilitiesRace abilitiesRace = (IAbilitiesRace) RacesUtils.getRace(player).getBehaviour();
            event.getDrops().removeIf(itemStack -> abilitiesRace.abilities().values().stream().anyMatch(s -> itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals(s)));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (RacesUtils.getRace(player).getBehaviour() instanceof IAbilitiesRace) {
                IAbilitiesRace abilitiesRace = (IAbilitiesRace) RacesUtils.getRace(player).getBehaviour();
                event.setCancelled(abilitiesRace.abilities().values().stream().anyMatch(s -> event.getClickedInventory() != null && event.getClickedInventory().getItem(event.getSlot()) != null
                        && event.getClickedInventory().getItem(event.getSlot()).getItemMeta() != null && event.getClickedInventory().getItem(event.getSlot()).getItemMeta().getLocalizedName().equals(s)));
            }
        }
    }
}
