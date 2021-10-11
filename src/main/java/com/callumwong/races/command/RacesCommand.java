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

package com.callumwong.races.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.callumwong.races.Races;
import com.callumwong.races.race.AbstractRaceBehaviour;
import com.callumwong.races.race.IAttributedRace;
import com.callumwong.races.race.RaceType;
import com.callumwong.races.util.RacesUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@CommandAlias("races")
public class RacesCommand extends BaseCommand {
    @Default
    @HelpCommand
    @CommandPermission("races.help")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @Description("Reloads the configuration file.")
    @CommandPermission("races.reload")
    public static void onReload(CommandSender sender) {
        Races.getPlugin(Races.class).reloadConfig();
        sender.sendMessage("Successfully reloaded configuration.");
    }

    @Subcommand("list|types")
    @Description("Lists all races.")
    @CommandPermission("races.list")
    public static void onList(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "Hover over a race to see the race's traits.");
        sender.sendMessage(ChatColor.GRAY + "Click on the race to select it.");
        for (RaceType raceType : RaceType.values()) {
            TextComponent component = new TextComponent(raceType.getDisplayName());
            component.setColor(net.md_5.bungee.api.ChatColor.BLUE);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(raceType.getDescription())));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/races select " + raceType.getId()));
            sender.spigot().sendMessage(component);
        }
    }

    @Subcommand("select")
    @Description("Selects a race.")
    @CommandCompletion("human|starborne|blazeborn|enderian|evocationeer|arachnid|merling|shulk|elytrian|vexian|avian confirm")
    public static void onSelect(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender);
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "You must specify which race you would like to select.");
            } else {
                if (RacesUtils.isRaceInvalid(player)) {
                    if (Arrays.stream(RaceType.values()).anyMatch(type -> args[0].equalsIgnoreCase(type.getId()))) {
                        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                            RacesUtils.setRace(player, Arrays.stream(RaceType.values()).filter(type -> args[0].equalsIgnoreCase(type.getId())).findFirst().orElseThrow(() -> new IllegalArgumentException("Race " + args[0] + " does not exist!")));

                            player.removePotionEffect(PotionEffectType.SLOW);
                            player.removePotionEffect(PotionEffectType.JUMP);
                            player.setInvulnerable(false);
                            player.sendMessage("You are now a" + n(RacesUtils.getRace(player).getDisplayName()) + " " + ChatColor.DARK_PURPLE + RacesUtils.getRace(player).getDisplayName() + ChatColor.RESET + ".");

                            if (RacesUtils.getRace(player) == RaceType.BLAZEBORN) {
                                if (player.getServer().getWorld(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("NetherWorldName"))) != null) {
                                    Location oldLocation = player.getLocation();
                                    player.teleport(new Location(player.getServer().getWorld(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("NetherWorldName"))),
                                            oldLocation.getX() / 8.0, oldLocation.getY(), oldLocation.getZ() / 8.0));
                                    RaceType.BLAZEBORN.getBehaviour().onJoin(player);
                                }
                            }
                            AbstractRaceBehaviour behaviour = RacesUtils.getRace(player).getBehaviour();
                            if (behaviour instanceof IAttributedRace) {
                                ((IAttributedRace) behaviour).attributeModifiers().forEach((attribute, attributeModifier) -> {
                                    if (player.getAttribute(attribute).getModifiers().stream().noneMatch(activeModifiers -> activeModifiers.equals(attributeModifier)))
                                        player.getAttribute(attribute).addModifier(attributeModifier);
                                });
                            }
                            behaviour.onSelect(player);
                            behaviour.onJoin(player);
                        } else {
                            player.sendMessage("Are you sure you would like to select: " + ChatColor.DARK_PURPLE + args[0].substring(0, 1).toUpperCase() + args[0].substring(1) + ChatColor.RESET + "?");

                            TextComponent component = new TextComponent("To confirm, do ");
                            TextComponent component1 = new TextComponent(ChatColor.DARK_PURPLE + "/races select " + args[0].toLowerCase() + " confirm");
                            component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to type this command in chat")));
                            component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/races select " + args[0].toLowerCase() + " confirm"));
                            component.addExtra(component1);
                            component.addExtra(ChatColor.RESET + ".");
                            player.spigot().sendMessage(component);
                        }
                    } else {
                        TextComponent component = new TextComponent(ChatColor.RED + "There is no such race. Use ");
                        TextComponent component1 = new TextComponent(ChatColor.DARK_PURPLE + "/races list");
                        component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to type this command in chat")));
                        component1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/races list"));
                        component.addExtra(component1);
                        component.addExtra(ChatColor.RED + ".");
                        player.spigot().sendMessage(component);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot reselect your race.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
        }
    }

    @Subcommand("reset|resetrace")
    @Description("Resets a player's race.")
    @CommandPermission("races.reset")
    @CommandCompletion("@players")
    public static void onReset(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You must specify which player's race to reset.");
        } else {
            Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
            if (players.stream().anyMatch(player -> player.getName().equalsIgnoreCase(args[0]))) {
                Player player = players.stream().filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().get();
                if (RacesUtils.isRaceInvalid(player)) {
                    player.sendMessage(ChatColor.RED + player.getName() + " does not have an existing race.");
                } else {
                    AbstractRaceBehaviour behaviour = RacesUtils.getRace(player).getBehaviour();
                    behaviour.onQuit(player);
                    behaviour.onReset(player);
                    if (behaviour instanceof IAttributedRace) {
                        ((IAttributedRace) behaviour).attributeModifiers().forEach((attribute, attributeModifier) -> player.getAttribute(attribute).removeModifier(attributeModifier));
                    }
                    RacesUtils.resetRace(player);
                    player.sendMessage(ChatColor.GRAY + "Your race has been reset by an administrator.");
                    player.sendMessage(Objects.requireNonNull(Races.getPlugin(Races.class).getConfig().getString("FirstJoinMessage")));
                    player.setInvulnerable(true);
                    player.setGravity(true);
                    if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                        player.setAllowFlight(false);
                    }
                    sender.sendMessage(ChatColor.GREEN + player.getName() + "'s race has been reset.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Player is either offline or does not exist.");
            }
        }
    }

    @Subcommand("get")
    @Description("Gets someone's race.")
    @CommandPermission("races.get")
    @CommandAlias("race")
    @CommandCompletion("@players")
    public static void onGet(CommandSender sender, String[] args) {
        if (args.length < 1) {
            if (sender instanceof Player) {
                RaceType raceType = RacesUtils.getRace(((Player) sender));
                TextComponent component = new TextComponent("You are a" + n(raceType.getDisplayName()) + " ");
                TextComponent component1 = new TextComponent(raceType.getDisplayName());
                component1.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(raceType.getDescription())));
                component.addExtra(component1);
                component.addExtra(ChatColor.RESET + ".");
                sender.spigot().sendMessage(component);
            } else {
                sender.sendMessage(ChatColor.RED + "You must specify a player name in the console.");
            }
        } else {
            Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
            if (players.stream().anyMatch(player -> player.getName().equalsIgnoreCase(args[0]))) {
                Player player = players.stream().filter(p -> p.getName().equalsIgnoreCase(args[0])).findFirst().get();
                RaceType raceType = RacesUtils.getRace(player);
                TextComponent component = new TextComponent(player.getName() + " is a" + n(raceType.getDisplayName()) + " ");
                TextComponent component1 = new TextComponent(raceType.getDisplayName());
                component1.setColor(net.md_5.bungee.api.ChatColor.BLUE);
                component1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(raceType.getDescription())));
                component.addExtra(component1);
                component.addExtra(ChatColor.RESET + ".");
                sender.spigot().sendMessage(component);
            } else {
                sender.sendMessage(ChatColor.RED + "Player is either offline or does not exist.");
            }
        }
    }

    private static String n(String str) {
        return str.matches("(?i)^[aeiouy].*$") ? "n" : "";
    }
}
