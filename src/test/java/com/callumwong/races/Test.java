package com.callumwong.races;

import com.callumwong.races.race.RaceType;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class Test {
    @org.junit.jupiter.api.Test
    public void printRaceIds() {
        System.out.println(String.join("|", Arrays.stream(RaceType.values()).map(type -> (CharSequence) type.getId()).collect(Collectors.toSet())));
    }

    @org.junit.jupiter.api.Test
    public void printUUID() {
        System.out.println(UUID.randomUUID());
    }
}
