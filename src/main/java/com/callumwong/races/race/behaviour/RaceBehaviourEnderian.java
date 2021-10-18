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
import com.callumwong.races.race.IAbilitiesRace;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class RaceBehaviourEnderian extends AbstractRaceBehaviour implements IAttributedRace, IAbilitiesRace {
    private static final AttributeModifier ENDERIAN_HEALTH_BOOST = new AttributeModifier(UUID.fromString("ca4f36ac-ca64-4d40-9998-5dae6015fdd1"), "EnderianHealthBoost", 5, AttributeModifier.Operation.ADD_NUMBER);
    private static final AttributeModifier ENDERIAN_KNOCKBACK_RESISTANCE = new AttributeModifier(UUID.fromString("b20c57f1-e06f-4899-ab5f-c208d8fbf280"), "EnderianKnockbackResist", 0.5, AttributeModifier.Operation.ADD_NUMBER);

    public static final NamespacedKey ENDERIAN_PEARL_KEY = new NamespacedKey(Races.getPlugin(Races.class), "lastUsedEnderianPearl");

    @Override
    public void onSelect(Player player) {
        addPearl(player);
    }

    @Override
    public void onRespawn(Player player) {
        addPearl(player);
    }

    @Override
    public void onTick(Player player) {
        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player && ((Player) entity).getInventory().getHelmet() != null && ((Player) entity).getInventory().getHelmet().getType() == Material.CARVED_PUMPKIN) {
                player.addPotionEffect(PotionEffectType.SLOW.createEffect(100, 0));
                player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(100, 0));

//                PacketContainer packet = Races.protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
//                packet.getIntegers().write(0, entity.getEntityId());
//                WrappedDataWatcher watcher = new WrappedDataWatcher();
//                WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
//                watcher.setEntity(player);
//                watcher.setObject(0, serializer, (byte) (0x20));
//                packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
//                try {
//                    Races.protocolManager.sendServerPacket(player, packet);
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }

                break;
            }
        }
    }

    private void addPearl(Player player) {
        if (Arrays.stream(Arrays.copyOfRange(player.getInventory().getContents(), 0, 8 + 1)).anyMatch(itemStack -> itemStack == null || itemStack.getType() == Material.AIR)) {
            if (player.getItemOnCursor().getType() == Material.AIR
                    && Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals("Enderian's Pearl"))) {
                if (player.getInventory().getContents()[8] == null) {
                    player.getInventory().setItem(8, RacesItems.getEnderianPearl());
                } else {
                    player.getInventory().addItem(RacesItems.getEnderianPearl());
                }
            }
        }
    }

    @Override
    public void onSecond(Player player) {
        long cooldownEnd = player.getPersistentDataContainer().getOrDefault(ENDERIAN_PEARL_KEY, PersistentDataType.LONG, System.currentTimeMillis()) + 3000L;
        String cooldownMessage = "";

        if (System.currentTimeMillis() >= cooldownEnd) {
            addPearl(player);
            cooldownMessage += "Pearl: READY ";
        } else cooldownMessage += "Pearl: " + ((cooldownEnd - System.currentTimeMillis()) / 1000L + 1L) + " ";

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + cooldownMessage));

        if (player.getWorld().hasStorm()
                && player.getLocation().getBlock().getLightFromSky() >= 15
                && player.getWorld().getEnvironment() != World.Environment.NETHER && player.getWorld().getEnvironment() != World.Environment.THE_END
                && !Arrays.asList(RacesUtils.WARM_BIOMES).contains(player.getWorld().getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()))
                || player.isInWater()) {
            player.damage(2);
        }
    }

    @Override
    public @NotNull HashMap<Attribute, AttributeModifier> attributeModifiers() {
        HashMap<Attribute, AttributeModifier> hashMap = new HashMap<>();
        hashMap.put(Attribute.GENERIC_MAX_HEALTH, ENDERIAN_HEALTH_BOOST);
        hashMap.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, ENDERIAN_KNOCKBACK_RESISTANCE);
        return hashMap;
    }

    @Override
    public HashMap<ItemStack, String> abilities() {
        HashMap<ItemStack, String> hashMap = new HashMap<>();
        hashMap.put(RacesItems.getEnderianPearl(), "Enderian's Pearl");
        return hashMap;
    }
}
