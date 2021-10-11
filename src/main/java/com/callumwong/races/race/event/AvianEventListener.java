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
import com.callumwong.races.util.RacesUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AvianEventListener implements Listener {
    private static final Material[] MEAT = {
            Material.BEEF,
            Material.COOKED_BEEF,
            Material.PORKCHOP,
            Material.COOKED_PORKCHOP,
            Material.CHICKEN,
            Material.COOKED_CHICKEN,
            Material.COD,
            Material.COOKED_COD,
            Material.SALMON,
            Material.COOKED_SALMON,
            Material.MUTTON,
            Material.COOKED_MUTTON,
            Material.RABBIT,
            Material.COOKED_RABBIT,
            Material.ROTTEN_FLESH,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH,
            Material.RABBIT_STEW
    };
    
    private static final Material[] WEAPONS = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    };

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (RacesUtils.getRace(victim) == RaceType.AVIAN) {
                if (event.getDamager() instanceof Player) {
                    Player attacker = (Player) event.getDamager();
                    @SuppressWarnings("deprecation")
                    boolean crit = !attacker.isSprinting()
                            && attacker.getFallDistance() > 0.0F
                            && !attacker.isOnGround()
                            && attacker.getLocation().getBlock().getType() != Material.LADDER
                            && attacker.getLocation().getBlock().getType() != Material.VINE
                            && !attacker.isInWater()
                            && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS)
                            && !attacker.isInsideVehicle()
                            && attacker.getAttackCooldown() > 0.9F;
                    if (crit && Arrays.stream(WEAPONS).anyMatch(material -> attacker.getInventory().getItemInMainHand().getType().equals(material))) {
                        int rand = ThreadLocalRandom.current().nextInt(60, 100 + 1);
                        victim.addPotionEffect(PotionEffectType.SLOW.createEffect(rand, 0));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity() instanceof Player
                && RacesUtils.getRace(((Player) event.getEntity())) == RaceType.AVIAN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (RacesUtils.getRace(event.getPlayer()) == RaceType.AVIAN) {
            if (Arrays.stream(MEAT).anyMatch(material -> event.getItem().getType().equals(material))) {
                event.getPlayer().sendMessage(ChatColor.RED + "Can't eat that.");
                event.setCancelled(true);
            }
        }
    }
}
