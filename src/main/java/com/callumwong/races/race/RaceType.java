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

import com.callumwong.races.race.behaviour.*;

public enum RaceType {
    HUMAN("Human", "human",
            "It's a human.\n" +
                    "No traits, advantages or disadvantages.", new RaceBehaviourHuman()),

    ENDERIAN("Enderian", "enderian",
            "+ Can launch ender-pearls on a 3 second cooldown\n" +
                    "+ Has 2.5 extra hearts\n" +
                    "+ Resists half of knockback\n" +
                    "+ Negates damage from enderpearling\n" +
                    "- Takes damage in water or rain (1 heart per second)\n" +
                    "If in a 3 block radius of another player wearing a carved pumpkin:\n" +
                    "- Temporary Slowness\n" +
                    "- Temporary Blindness", new RaceBehaviourEnderian()),

    MERLING("Merling", "merling",
            "+ Can breathe in water\n" +
                    "+ Night vision only in water\n" +
                    "+ Haste 4 only in water\n" +
                    "+ Dolphin's Grace only in water\n" +
                    "+ 1 extra armor point\n" +
                    "- Takes 1 heart of damage each second on land after 15 seconds of not being submerged under water or under rain, unless the water breathing effect is active", new RaceBehaviourMerling()),

    STARBORNE("Starborne", "starborne",
            "+ Can create a purple beam that explodes on impact\n" +
                    "+ A block-breaking explosion is created on death\n" +
                    "+ Can dash in any direction, leaving a low damage AoE behind\n" +
                    "+ Regeneration 2, Speed 1, and Slow Falling 1 at night time\n" +
                    "- When hit, has a 10% chance of getting Slowness 10 and Blindness for 2 seconds\n" +
                    "- Takes double damage from fire", new RaceBehaviourStarborne()),

    BLAZEBORN("Blazeborn", "blazeborn",
            "+ Infinite Fire Resistance\n" +
                    "+ While burning or in lava, gains strength 1\n" +
                    "+ Immune to hunger, poison and wither effects\n" +
                    "+ Can shoot fireballs on a 4 second cooldown wherever they are aiming (not affected by gravity)\n" +
                    "= Natural spawnpoint in the nether dimension\n" +
                    "- Takes damage in water or rain (1 heart per second)\n" +
                    "- Cannot use bow, crossbor or shield", new RaceBehaviourBlazeborn()),

    AVIAN("Avian", "avian",
            "+ Fall damage is completely negated\n" +
                    "+ Permanent Speed 1\n" +
                    "+ Permanent Jump Boost 2\n" +
                    "- Slowness 1 when critical hit with a sword or axe\n" +
                    "- Cannot eat meat", new RaceBehaviourAvian()),

    ARACHNID("Arachnid", "arachnid",
            "+ Wall climbing\n" +
                    "+ Permanent Speed 1\n" +
                    "+ 8 cobwebs per 10 minutes\n" +
                    "+ Immune to fall damage\n" +
                    "- 8 hearts\n" +
                    "- Cannot use shield", new RaceBehaviourArachnid()),

    SHULK("Shulk", "shulk",
            "+ Given a portable backpack with 10 slots\n" +
                    "+ 6 natural armor points\n" +
                    "+ Haste 1\n" +
                    "- Cannot use shield\n" +
                    "- Higher Exhaustion", new RaceBehaviourShulk()),

    VEXIAN("Vexian", "vexian",
            "+ Creative Flight (unless player is above Y100)\n" +
                    "+ Infinite Slow-falling\n" +
                    "- 6 hearts only\n" +
                    "- Permanent Weakness 2 (weakness 1 with iron sword)\n" +
                    "- Cannot use shields\n" +
                    "- Slowfalling and Flight are lost whenever you touch water (activated again when you leave water)", new RaceBehaviourVexian()),

    EVOCATIONEER("Evocationeer", "evocationeer",
            "+ All hostile mobs are passive towards you, except for bosses and other Evocationeer's vexes\n" +
                    "+ Resistance 1\n" +
                    "+ Speed 2\n" +
                    "+ Jump Boost 2\n" +
                    "+ Can summon evoker fangs and 3 vexes on a 10 second cooldown\n" +
                    "- Cannot wear diamond or netherite armor\n" +
                    "- Permanent Weakness 2\n" +
                    "- Iron Golems automatically attack you\n" +
                    "- Cannot trade with villagers", new RaceBehaviourEvocationeer()),

    ELYTRIAN("Elytrian", "elytrian",
            "+ Gets elytra attached on automatically (Curse of Binding)\n" +
                    "+ Use feather on a 30 second cooldown for Jump Boost 10 for 3 seconds\n" +
                    "+ Gets strength 1 when not on land or in water\n" +
                    "- Cannot wear diamond or netherite armor\n" +
                    "- 2x as much kinetic damage\n" +
                    "- Slowness 2 and Weakness 1 in Low Ceiling Places (2-3 block tall spaces)", new RaceBehaviourElytrian());

    private final String displayName;
    private final String id;
    private final String description;
    private final AbstractRaceBehaviour behaviour;

    RaceType(String displayName, String id, String description, AbstractRaceBehaviour behaviour) {
        this.displayName = displayName;
        this.id = id;
        this.description = description;
        this.behaviour = behaviour;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public AbstractRaceBehaviour getBehaviour() {
        return behaviour;
    }
}
