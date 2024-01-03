package io.github.cats1337.battlekingdom.commands;

import com.marcusslover.plus.lib.text.Text;
import io.github.cats1337.battlekingdom.BattleKingdom;
import io.github.cats1337.battlekingdom.playerdata.*;
import io.github.cats1337.battlekingdom.utils.ITabCompleterHelper;
import com.marcusslover.plus.lib.command.Command;
import com.marcusslover.plus.lib.command.CommandContext;
import com.marcusslover.plus.lib.command.ICommand;
import com.marcusslover.plus.lib.command.TabCompleteContext;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    private final List<String> subCommands = List.of("set", "randomteams", "respawn", "kick", "reset", "list", "info", "help");
    private final List<String> setSubCommands = List.of("team", "leader", "spawn");
    private final List<String> randomTeamsSubCommands = List.of("all", "player");
    private final List<String> respawnSubCommands = List.of("player", "team");
    private final List<String> kickSubCommands = List.of("player");
    private final List<String> resetSubCommands = List.of("all", "player", "team");
    private final List<String> listSubCommands = List.of("team");
    private final List<String> infoSubCommands = List.of("team");
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
                                        serverPlayer.setTeamName(team);
                                        serverPlayer.setTeamLeader(true);
                                        playerContainer.writeData(UUID.fromString(playerName), serverPlayer);
                                        Text.of("&aSuccessfully set " + playerName + " as the leader of " + team + ".").send(sender);
                                        return true;
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
                                Text.of("&cInvalid subcommand.").send(sender);
                                Text.of("&7set <&bteam&7|&bleader&7|&bspawn&7>").send(sender);
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
                                Text.of("&cInvalid subcommand.").send(sender);
                                Text.of("&7randomteams <&ball&7|&bplayer&7>").send(sender);
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
                                            serverPlayer.setAlive(true);
                                            PlayerHandler.unbanPlayer(p);
                                            if (serverPlayer.isExemptFromKick()) {
                                                p.setGameMode(GameMode.SURVIVAL);
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                            if (serverPlayer.isTeamLeader()) {
                                                serverPlayer.setAlive(true);
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
                                            serverPlayer.setAlive(true);
                                            PlayerHandler.unbanPlayer(p);
                                            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                            if (serverPlayer.isExemptFromKick()) {
                                                p.setGameMode(GameMode.SURVIVAL);
                                                Text.of("&aSuccessfully revived " + p.getName() + ".").send(sender);
                                            }
                                        }
                                        serverPlayer.setAlive(true);
                                        Text.of("&aSuccessfully revived " + team + ".").send(sender);
                                    });
                                    return true;
                                }
                            default:
                                Text.of("&cInvalid subcommand.").send(sender);
                                Text.of("&7respawn <&bplayer&7|&bteam&7>").send(sender);
                                return true;
                        }
                    }
                case "kick":
//                    TODO: Complete
            }

        }

        return true;
    }
}
