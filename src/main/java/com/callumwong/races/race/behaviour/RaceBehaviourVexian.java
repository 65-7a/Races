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

import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAttributedRace;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class RaceBehaviourVexian extends AbstractRaceBehaviour implements IAttributedRace {
    private static final AttributeModifier VEXIAN_HEALTH_DEBUFF = new AttributeModifier(UUID.fromString("f84497f2-1db7-42b7-9436-60a0ed2021f8"), "VexianHealthDebuff", -8, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public void onJoin(Player player) {
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(100, 0));
        player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(100, 1));
    }

    @Override
    public void onTick(Player player) {
        player.setAllowFlight(!player.isInWater() && player.getLocation().getY() < 100.0);

        if (!player.isInWater()) {
            player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(100, 0));
        } else {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
        }

        if (player.getInventory().getItemInMainHand().getType() != Material.IRON_SWORD) {
            player.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(100, 0));
        }
    }

    @Override
    public @NotNull HashMap<Attribute, AttributeModifier> attributeModifiers() {
        HashMap<Attribute, AttributeModifier> hashMap = new HashMap<>();
        hashMap.put(Attribute.GENERIC_MAX_HEALTH, VEXIAN_HEALTH_DEBUFF);
        return hashMap;
    }
}
