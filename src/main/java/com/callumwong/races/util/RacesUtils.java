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

package com.callumwong.races.util;

import com.callumwong.races.Races;
import com.callumwong.races.race.RaceType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class RacesUtils {
    private RacesUtils() {
        throw new AssertionError("No com.callumwong.races.RacesUtils instances for you!");
    }

    public static final Biome[] WARM_BIOMES = {
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.SHATTERED_SAVANNA,
            Biome.SHATTERED_SAVANNA_PLATEAU,
            Biome.DESERT,
            Biome.DESERT_HILLS,
            Biome.DESERT_LAKES
    };

    public static boolean isRaceInvalid(@NotNull Player player) {
        FileConfiguration config = Races.getPlugin(Races.class).getRacesConfig();
        return config == null
                || !config.contains(player.getUniqueId().toString())
                || Arrays.stream(RaceType.values()).noneMatch(type -> type.getId().equalsIgnoreCase(config.getString(player.getUniqueId().toString())));
    }

    public static void resetRace(@NotNull Player player) {
        FileConfiguration config = Races.getPlugin(Races.class).getRacesConfig();
        config.set(player.getUniqueId().toString(), "none");
        Races.getPlugin(Races.class).saveRacesConfig();
    }

    public static void setRace(@NotNull Player player, @NotNull RaceType type) {
        FileConfiguration config = Races.getPlugin(Races.class).getRacesConfig();
        config.set(player.getUniqueId().toString(), type.getId());
        Races.getPlugin(Races.class).saveRacesConfig();
    }

    public static RaceType getRace(@NotNull Player player) {
        if (isRaceInvalid(player)) return RaceType.HUMAN;
        FileConfiguration config = Races.getPlugin(Races.class).getRacesConfig();
        return Arrays.stream(RaceType.values()).filter(type -> type.getId().equalsIgnoreCase(config.getString(player.getUniqueId().toString()))).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find race of player"));
    }
}
