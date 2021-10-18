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

package com.callumwong.races.race;

import com.callumwong.races.item.RacesItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public abstract class AbstractAbilityRaceBehaviour extends AbstractRaceBehaviour {
    public abstract HashMap<ItemStack, String> abilities();

    @Override
    public void onSecond(Player player) {
        checkInventory(player);
    }

    private void checkInventory(Player player) {
        Arrays.stream(Arrays.copyOfRange(player.getInventory().getContents(), 9, 35)).forEach(itemStack -> {
            if (abilities().values().stream().anyMatch(s -> itemStack != null
                    && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLocalizedName().equals(s))) {
                ItemStack clone = player.getInventory().getContents()[8].clone();
                player.getInventory().setItem(8, RacesItems.getEnderianPearl());
                if (player.getInventory().getContents()[8] != null) {
                    player.getInventory().addItem(clone).forEach((integer, stack) -> {
                        player.getWorld().dropItem(player.getLocation(), stack);
                    }); // try to add the item to the player's inv, if not drop it to the floor
                }
            }
        });
    }
}
