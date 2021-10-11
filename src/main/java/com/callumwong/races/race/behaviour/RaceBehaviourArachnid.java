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
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAttributedRace;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class RaceBehaviourArachnid extends AbstractRaceBehaviour implements IAttributedRace {
    private static final AttributeModifier ARACHNID_HEALTH_DEBUFF = new AttributeModifier(UUID.fromString("52d9dfe0-5180-42a4-a30f-0a5a24398d86"), "ArachnidHealthDebuff", -4, AttributeModifier.Operation.ADD_NUMBER);
    private static final NamespacedKey ARACHNID_COBWEB_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastGivenArachnidCobweb");

    private final ArrayList<Climber> climbers = new ArrayList<>();

    @Override
    public void onJoin(Player player) {
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(100, 0));
        climbers.add(new Climber(player));
    }

    @Override
    public void onQuit(Player player) {
        climbers.remove(getClimber(player));
    }

    public Climber getClimber(Player player) {
        return climbers.stream().filter(climber -> climber.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    @Override
    public void onSelect(Player player) {
        addCobweb(player);
    }

    @Override
    public void onTick(Player player) {
        for (Climber climber : climbers) {
            if (!climber.getPlayer().isFlying()) climber.climb();
        }
    }

    private void addCobweb(Player player) {
        if (player.getInventory().firstEmpty() > -1) {
            if (player.getItemOnCursor().getItemMeta() == null || !player.getItemOnCursor().getItemMeta().getLocalizedName().equals("Arachnid's Cobweb")) {
                if (Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null
                        && itemStack.getItemMeta().getLocalizedName().equals("Arachnid's Cobweb"))) {
                    if (player.getInventory().getContents()[8] == null) {
                        player.getInventory().setItem(8, RacesItems.getArachnidCobweb());
                    } else {
                        player.getInventory().addItem(RacesItems.getArachnidCobweb());
                    }
                    player.getPersistentDataContainer().set(ARACHNID_COBWEB_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                } else {
                    ArrayList<ItemStack> cobwebStacks = Arrays.stream(player.getInventory().getContents())
                            .filter(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Arachnid's Cobweb"))
                            .collect(Collectors.toCollection(ArrayList::new));
                    int totalCobwebAmount = cobwebStacks.stream().mapToInt(ItemStack::getAmount).sum();
                    cobwebStacks.stream().findFirst().ifPresent(itemStack -> {
                        if (totalCobwebAmount < 56) {
                            itemStack.setAmount(itemStack.getAmount() + 8);
                            player.getPersistentDataContainer().set(ARACHNID_COBWEB_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                        } else if (totalCobwebAmount < 64) {
                            int amountToGive = 64 - totalCobwebAmount;
                            itemStack.setAmount(itemStack.getAmount() + amountToGive);
                            player.getPersistentDataContainer().set(ARACHNID_COBWEB_KEY, PersistentDataType.LONG, System.currentTimeMillis());
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onSecond(Player player) {
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(100, 0));

        long cooldownEnd = player.getPersistentDataContainer().getOrDefault(ARACHNID_COBWEB_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 600000L; // 10 minutes
        if (System.currentTimeMillis() >= cooldownEnd) {
            addCobweb(player);
        } else {
            int totalCobwebAmount = Arrays.stream(player.getInventory().getContents())
                    .filter(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Arachnid's Cobweb"))
                    .collect(Collectors.toCollection(ArrayList::new)).stream().mapToInt(ItemStack::getAmount).sum();
            if (totalCobwebAmount < 64) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "" + ((cooldownEnd - System.currentTimeMillis()) / 1000L + 1L)));
            }
        }
    }

    @Override
    public @NotNull HashMap<Attribute, AttributeModifier> attributeModifiers() {
        HashMap<Attribute, AttributeModifier> hashMap = new HashMap<>();
        hashMap.put(Attribute.GENERIC_MAX_HEALTH, ARACHNID_HEALTH_DEBUFF);
        return hashMap;
    }

    private static class Climber {
        private final Player player;
        private boolean climbing, mounting, canReclimb;
        private int prevMaxY;
        private int ticksMounting;
        private final int maxMount;
        private final int reclimbFallLimit;
        private final double climbRate;

        public Climber(Player player) {
            this.player = player;
            this.climbing = true;
            this.canReclimb = true;
            this.mounting = false;
            this.ticksMounting = -1;
            this.maxMount = 3;
            this.reclimbFallLimit = 11;
            this.climbRate = 0.1;
        }

        public Player getPlayer() {
            return player;
        }

        @SuppressWarnings("deprecation")
        public void climb() {
            Location location = player.getLocation();
            if (location.getBlockY() > prevMaxY) prevMaxY = location.getBlockY();
            if (!nearWall(.32)) {
                climbing = false;
                if (ticksMounting >= 0 && ticksMounting <= maxMount && canReclimb) {
                    mounting = true;
                    if (location.getBlockY() > prevMaxY) prevMaxY = location.getBlockY();
                    ticksMounting++;
                    player.setFallDistance(0.0f);
                } else {
                    if (prevMaxY - location.getBlockY() > reclimbFallLimit) canReclimb = false;
                    mounting = false;
                    ticksMounting = -1;
                }
            } else {
                mounting = false;
                if (!canReclimb) {
                    if (player.isOnGround()) {
                        canReclimb = true;
                        prevMaxY = location.getBlockY();
                    } else {
                        canReclimb = false;
                    }
                }
                if (canReclimb && !player.getLocation().subtract(0, 0.5, 0).getBlock().getType().isSolid()) {
                    climbing = true;
                    ticksMounting = 0;
                }
            }

            if (climbing || mounting) {
                player.setVelocity(player.getVelocity().setY(climbing ? (player.isSneaking() ? -1 : 1) * climbRate : climbRate));
                player.setGravity(false);
            } else player.setGravity(true);
        }

        public boolean nearWall(double dist) {
            Vector locale = player.getLocation().toVector();
            int y = locale.getBlockY() + 1;
            double x = locale.getX(), z = locale.getZ();
            World world = player.getWorld();
            Block b1 = world.getBlockAt(new Location(world, x + dist, y, z));
            Block b2 = world.getBlockAt(new Location(world, x - dist, y, z));
            Block b3 = world.getBlockAt(new Location(world, x, y, z + dist));
            Block b4 = world.getBlockAt(new Location(world, x, y, z - dist));
            return (b1.getType().isSolid()) || (b2.getType().isSolid()) || (b3.getType().isSolid()) || (b4.getType().isSolid());
        }
    }
}
