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
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAttributedRace;
import com.callumwong.races.util.RacesUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class RaceBehaviourMerling extends AbstractRaceBehaviour implements IAttributedRace {
    public static final AttributeModifier MERLING_ARMOR_POINTS = new AttributeModifier(UUID.fromString("89d55248-79f9-4341-a2c3-b578a01f0962"), "MerlingArmorPoints", 1, AttributeModifier.Operation.ADD_NUMBER);
    public static final NamespacedKey MERLING_OUT_OF_WATER = new NamespacedKey(Races.getPlugin(Races.class), "lastMerlingOutOfWater");

    private static final Material[] WATER_BLOCKS = {
            Material.WATER,
            Material.BUBBLE_COLUMN,
            Material.KELP,
            Material.KELP_PLANT,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.TUBE_CORAL,
            Material.BRAIN_CORAL,
            Material.BUBBLE_CORAL,
            Material.FIRE_CORAL,
            Material.CONDUIT
    };

    @Override
    public void onSecond(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Block eyeBlock = player.getEyeLocation().getBlock();
        if ((Arrays.stream(WATER_BLOCKS).anyMatch(material -> material == eyeBlock.getType()) || eyeBlock.getBlockData() instanceof Waterlogged && ((Waterlogged) eyeBlock.getBlockData()).isWaterlogged())
                || player.hasPotionEffect(PotionEffectType.WATER_BREATHING)
                || (player.getWorld().hasStorm() || player.getWorld().isThundering())
                && player.getLocation().getBlock().getLightFromSky() >= 15
                && player.getWorld().getEnvironment() != World.Environment.NETHER && player.getWorld().getEnvironment() != World.Environment.THE_END
                && !Arrays.asList(RacesUtils.WARM_BIOMES).contains(player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()))) {
            if (container.getOrDefault(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, 15) < 15) {
                container.set(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, 15);
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "\u2714"));
        } else {
            if (container.getOrDefault(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, 15) > 0) {
                container.set(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, container.getOrDefault(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, 15) - 1);
            } else {
                player.damage(2);
            }

            int outOfWaterTimer = player.getPersistentDataContainer().getOrDefault(MERLING_OUT_OF_WATER, PersistentDataType.INTEGER, 15);
            String message;

            if (outOfWaterTimer <= 0) message = ChatColor.RED + "Return to water!";
            else if (outOfWaterTimer <= 5) message = ChatColor.RED + "" + outOfWaterTimer;
            else if (outOfWaterTimer <= 10) message = ChatColor.YELLOW + "" + outOfWaterTimer;
            else message = ChatColor.WHITE + "" + outOfWaterTimer;

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }

//      player.addPotionEffect(PotionEffectType.WATER_BREATHING.createEffect(100, 0));
        player.setRemainingAir(300);

        if (player.isInWater()) {
            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(40, 0));
            player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(40, 3));
            player.addPotionEffect(PotionEffectType.DOLPHINS_GRACE.createEffect(40, 0));
        }
    }

    @Override
    public @NotNull HashMap<Attribute, AttributeModifier> attributeModifiers() {
        HashMap<Attribute, AttributeModifier> hashMap = new HashMap<>();
        hashMap.put(Attribute.GENERIC_ARMOR, MERLING_ARMOR_POINTS);
        return hashMap;
    }
}
