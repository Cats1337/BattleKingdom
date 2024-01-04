package io.github.cats1337.battlekingdom.commands;

import com.marcusslover.plus.lib.command.TabCompleteContext;
import com.marcusslover.plus.lib.text.Text;
import io.github.cats1337.battlekingdom.playerdata.PlayerContainer;
import io.github.cats1337.battlekingdom.playerdata.PlayerHandler;
import io.github.cats1337.battlekingdom.playerdata.ServerPlayer;
import io.github.cats1337.battlekingdom.playerdata.TeamManager;
import com.marcusslover.plus.lib.command.Command;
import com.marcusslover.plus.lib.command.CommandContext;
import com.marcusslover.plus.lib.command.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.github.cats1337.battlekingdom.playerdata.TeamManager.assignRandomTeam;

@Command(name = "kingdom")
public class CommandManager implements ICommand {

    // Command: /kingdom
    // /kingdom <player> set team <team> - sets a player's team
    // /kingdom randomteams <all|player> - randomly assigns players to teams
    // /kingdom <team> set leader <random|player> - sets the team leader to a random player or a specific player
    // /kingdom <team> set spawn - sets the team spawn point
    // /kingdom respawn <player|team> - unbans/respawns a player or revives a team
    // /kingdom kick <player> - kicks a player from their team
    // /kingdom reset <all|player|team> - resets all player data, specific player's data, or a specific team's data (including team leader, team spawn, and team members)
    // /kingdom <team> <list|info> - Lists Team Name, If alive or dead, Spawn Point(If permission), Team Leader, Team Members
    // /kingdom help - lists all commands
    private final List<String> subCommands = List.of("set", "randomteams", "respawn", "kick", "reset", "info", "list", "help");
    private final List<String> setSubCommands = List.of("team", "leader", "spawn");
    private final List<String> randomTeamsSubCommands = List.of("all", "player");
    private final List<String> respawnSubCommands = List.of("player", "team");
    private final List<String> kickSubCommands = List.of("player");
    private final List<String> resetSubCommands = List.of("all", "player", "team");
    private final List<String> infoSubCommands = List.of("team", "player");
    private final List<String> listSubCommands = List.of("team");
    private final List<String> helpSubCommands = List.of("help");

    @Override
    public boolean execute(@NotNull CommandContext cmd){
        CommandSender sender = cmd.sender();
        String[] args = cmd.args();
        if (args.length == 0) {
            Text.of("&cYou must specify a subcommand.").send(sender);
            return true;
        } else {
            String arg = args[0];
            switch (arg) {
                case "set":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "team":
                                if (args.length == 2) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else {
                                    String team = args[2];
                                    if (args.length == 3) {
                                        Text.of("&cYou must specify a player.").send(sender);
                                        return true;
                                    } else {
                                        String playerName = args[3];
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(UUID.fromString(playerName));
                                        serverPlayer.setTeamName(team);
                                        playerContainer.writeData(UUID.fromString(playerName), serverPlayer);
                                        Text.of("&aSuccessfully set " + playerName + "'s team to " + team + ".").send(sender);
                                        return true;
                                    }
                                }
                            case "leader":
                                if (args.length == 2) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else {
                                    String team = args[2];
                                    if (args.length == 3) {
                                        Text.of("&cYou must specify a player.").send(sender);
                                        return true;
                                    } else {
                                        String playerName = args[3];
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(UUID.fromString(playerName));
                                        if (Bukkit.getPlayer(playerName) != null) {
                                            Player p = Bukkit.getPlayer(playerName);
                                            serverPlayer.setTeamLeader(true);
                                            playerContainer.writeData(UUID.fromString(playerName), serverPlayer);
                                            TeamManager.setTeamLeader(p, team);
                                            Text.of("&aSuccessfully set " + playerName + " as the leader of " + team + ".").send(sender);
                                            return true;
                                        } else {
                                            Text.of("&cYou must specify a valid player.").send(sender);
                                            return true;
                                        }
                                    }
                                }
                            case "spawn":
                                if (args.length == 2) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else {
                                    String team = args[2];
                                    Player p = (Player) sender;
                                    TeamManager.setTeamSpawnPoint(team, p.getLocation());
                                    Text.of("&aSuccessfully set " + team + "'s spawn point.").send(sender);
                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.\n"
                                        + "&7set <&bteam&7|&bleader&7|&bspawn&7>").send(sender);
                                return true;
                        }
                    }
                case "randomteams":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "all":
                                if (sender.hasPermission("kingdom.randomteams.all")) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        assignRandomTeam(p);
                                    }
                                    Text.of("&aSuccessfully assigned random teams to all players.").send(sender);
                                    return true;
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                    return true;
                                }
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.randomteams.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else {
                                    String playerName = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        if (p.getName().equals(playerName)) {
                                            assignRandomTeam(p);
                                            Text.of("&aSuccessfully assigned a random team to " + playerName + ".").send(sender);
                                        }
                                    });
                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.\n"
                                        + "&7randomteams <&ball&7|&bplayer&7>").send(sender);
                                return true;
                        }
                    }
                case "respawn":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.respawn.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.respawn.player")) {
                                    String playerName = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        if (p.getName().equals(playerName)) {
                                            PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                            ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                            serverPlayer.setTeamAlive(true);
                                            PlayerHandler.unbanPlayer(p);
                                            if (serverPlayer.isExemptFromKick()) {
                                                p.setGameMode(GameMode.SURVIVAL);
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                            if (serverPlayer.isTeamLeader()) {
                                                serverPlayer.setTeamAlive(true);
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                        }
                                    });


                                    return true;
                                }
                            case "team":
                                if (args.length == 2 && sender.hasPermission("kingdom.respawn.team")) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else {
                                    String team = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                        if (serverPlayer.getTeamName().equals(team)) {
                                            serverPlayer.setTeamAlive(true);
                                            PlayerHandler.unbanPlayer(p);
                                            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                            if (serverPlayer.isExemptFromKick()) {
                                                p.setGameMode(GameMode.SURVIVAL);
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                        }
                                        serverPlayer.setTeamAlive(true);
                                        Text.of("&aSuccessfully revived " + team + ".").send(sender);
                                    });
                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.\n"
                                        + "&7respawn <&bplayer&7|&bteam&7>").send(sender);
                                return true;
                        }
                    }
                case "kick":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.kick.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.kick.player")) {
                                    String playerName = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        if (p.getName().equals(playerName)) {
                                            PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                            ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                            serverPlayer.setTeamName("");
                                            if (serverPlayer.isTeamLeader()) {
                                                Text.of("&cYou cannot kick the team leader.").send(sender);
                                            } else {
                                                Text.of("&aSuccessfully kicked " + p.getName() + ".").send(sender);
                                            }
                                        }
                                    });

                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.\n"
                                        + "&7kick <&bplayer&7>").send(sender);
                                return true;
                        }
                    }
                case "reset":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "all":
                                if (sender.hasPermission("kingdom.reset.all")) {
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                        serverPlayer.setTeamName("");
                                        serverPlayer.setTeamLeader(false);
                                        serverPlayer.setTeamAlive(true);
                                        playerContainer.writeData(p.getUniqueId(), serverPlayer);
                                    });
                                    Text.of("&aSuccessfully reset all player data.").send(sender);
                                    return true;
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                    return true;
                                }
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.reset.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.reset.player")) {
                                    String playerName = args[2];
                                    PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                    ServerPlayer serverPlayer = playerContainer.loadData(UUID.fromString(playerName));
                                    serverPlayer.setTeamName("");
                                    serverPlayer.setTeamLeader(false);
                                    playerContainer.writeData(UUID.fromString(playerName), serverPlayer);
                                    Text.of("&aSuccessfully reset " + playerName + "'s data.").send(sender);
                                    return true;
                                }
                            case "team":
                                if (args.length == 2 && sender.hasPermission("kingdom.reset.team")) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.reset.team")) {
                                    String team = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                        if (serverPlayer.getTeamName().equals(team)) {
                                            serverPlayer.setTeamName("");
                                            serverPlayer.setTeamLeader(false);
                                            serverPlayer.setTeamAlive(true);
                                            playerContainer.writeData(p.getUniqueId(), serverPlayer);
                                            Text.of("&aSuccessfully reset " + team + "'s data.").send(sender);
                                        }
                                    });
                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.\n" +
                                        "&7reset <&ball&7|&bplayer&7|&bteam&7>").send(sender);
                                return true;
                        }
                    }
                case "info":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "team":
                                if (args.length == 2 && sender.hasPermission("kingdom.info.team")) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.info.team")) {
                                    String team = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                        if (serverPlayer.getTeamName().equals(team)) {
                                            Text.of("&7Team Name: &9" + team + "\n" +
                                                    "&7Team Color: " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamColor() + "\n" +
                                                    "&7Team Alive: &9" + serverPlayer.isTeamAlive() + "\n" +
                                                    "&7Team Spawn Point: &9" + TeamManager.getSpawnLocationForTeam(team) + "\n" +
                                                    "&7Team Leader: &9" + TeamManager.getTeamLeader(team)  + "\n" +
                                                    "&7Team Members: &9" + TeamManager.getTeamMembers(team)
                                            ).send(sender);
                                        }
                                    });
                                    return true;
                                }
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.info.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.info.player")) {
                                    String playerName = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                        if (p.getName().equals(playerName) && serverPlayer.isTeamLeader()) {
                                            Text.of("&9" + serverPlayer.getPlayerName() + "&7 is the &6&lleader&7 of Team &9" + serverPlayer.getTeamName() + "\n" +
                                                    "&7Team Respawn: &9" + serverPlayer.isTeamAlive()).send(sender);
                                        }
                                        if (p.getName().equals(playerName)) {
                                            Text.of("&9" + serverPlayer.getPlayerName() + "&7 is on Team " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamName() + "\n" +
                                                    "&7Team Respawn: &9" + serverPlayer.isTeamAlive()
                                            ).send(sender);
                                        }
                                    });
                                    return true;
                                }

                            default:
                                Text.of("&cInvalid subcommand.\n" +
                                        "&7<info> <&bteam&7|&bplayer&7>").send(sender);
                                return true;
                        }
                    }
                case "list":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        if (subArg.equals("team")) {
                            if (args.length == 2 && sender.hasPermission("kingdom.list.team")) {
                                Text.of("&cYou must specify a team.").send(sender);
                                return true;
                            } else if (sender.hasPermission("kingdom.list.team")) {
                                String team = args[2];
                                Bukkit.getOnlinePlayers().forEach(p -> {
                                    PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
                                    ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
                                    if (serverPlayer.getTeamName().equals(team)) {
                                        Text.of("&6&l&m" + serverPlayer.getTeamColor() + serverPlayer.getTeamName() + "\n" +
                                                "&7Team Leader: &9" + TeamManager.getLeaderStatus(team) + "\n" +
                                                "&7Team Alive: &9" + TeamManager.getTeamStatus(team) + "\n" +
                                                "&7Team Members: &9" + TeamManager.getTeamMembers(team)
                                        ).send(sender);


                                    }
                                });
                                return true;
                            }
                        }
                        Text.of("&cInvalid subcommand.\n" +
                                "&7<list> <&bteam&7>").send(sender);
                        return true;
                    }
                case "help":
                    if (args.length == 1) {
                        Text.of("&cYou must specify a subcommand.").send(sender);
                    } else {
                        String subArg = args[1];
                        if (subArg.equals("help")) {
                            Text.of("&7<set> <&bteam&7|&bleader&7|&bspawn&7>\n" +
                                    "&7<randomteams> <&ball&7|&bplayer&7>\n" +
                                    "&7<respawn> <&bplayer&7|&bteam&7>\n" +
                                    "&7<kick> <&bplayer&7>\n" +
                                    "&7<reset> <&ball&7|&bplayer&7|&bteam&7>\n" +
                                    "&7<info> <&bteam&7|&bplayer&7>\n" +
                                    "&7<list> <&bteam&7>\n" +
                                    "&7<help>"
                            ).send(sender);
                            return true;
                        }
                        Text.of("&cInvalid subcommand.\n" +
                                "&7<help>").send(sender);
                    }
                    return true;
            }
        }
        return true;
    }

    @Override
    public @NotNull List<@NotNull String> tab(@NotNull TabCompleteContext tab) {
        CommandSender sender = tab.sender();
        @NotNull String[] args = tab.args();

//        from config get team names
//        from config get team spawn points


        if (args.length == 0) {
            return subCommands;
        } else {
            String arg = args[0];
            switch (arg) {
                case "set":
                    if (args.length == 1) {
                        return setSubCommands;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            // /kingdom <player> set team <team>
                            case "team":
                                if (args.length == 2) {
                                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                                } else if (args.length == 3) {
                                    return TeamManager.getTeamNames();
                                } else {
                                    return new ArrayList<>();
                                }
                                // /kingdom <team> set leader <random|player>
                            case "leader", "spawn":
                                if (args.length == 2) {
                                    return TeamManager.getTeamNames();
                                } else {
                                    return new ArrayList<>();
                                }
                            default:
                                return new ArrayList<>();
                        }
                    }
                case "randomteams":
                    if (args.length == 1) {
                        return randomTeamsSubCommands;
                    } else {
                        String subArg = args[1];
                        if (subArg.equals("player")) {
                            if (args.length == 2) {
                                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                            } else {
                                return new ArrayList<>();
                            }
                        }
                        return new ArrayList<>();
                    }
                case "respawn":
                    if (args.length == 1) {
                        return respawnSubCommands;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "player", "team":
                                if (args.length == 2) {
                                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                                } else {
                                    return new ArrayList<>();
                                }
                            default:
                                return new ArrayList<>();
                        }
                    }
                case "kick":
                    if (args.length == 1) {
                        return kickSubCommands;
                    } else {
                        String subArg = args[1];
                        if (subArg.equals("player")) {
                            if (args.length == 2) {
                                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
                            } else {
                                return new ArrayList<>();
                            }
                        }
                        return new ArrayList<>();
                    }
                case "reset":
                    return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
}

