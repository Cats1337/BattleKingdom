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
import io.github.cats1337.battlekingdom.utils.ITabCompleterHelper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.cats1337.battlekingdom.playerdata.TeamManager.assignRandomTeam;

@Command(name = "kingdom")
public class BattleKingdomCommands implements ICommand {

    // Command: /kingdom

    // HELP - permission: kingdom.help
    // /kingdom help - lists all commands that the player has permission to use

    // SET - permission: kingdom.set
    // /kingdom set <team|leader|spawn>
        // /kingdom set <team> <player> - sets a player's team
        // /kingdom set leader <team> <player> - sets team leader
        // /kingdom set spawn <team> - sets team spawn point
        // /kingdom set name <team> <name> - sets a team's name

    // RANDOMTEAMS - permission: kingdom.randomteams
    // /kingdom randomteams <all|player> - randomly assigns players to teams
        // /kingdom randomteams all - randomly assigns all players to teams
        // /kingdom randomteams player <player> - randomly assigns a specific player to a team

    // INFO - permission: kingdom.info
    // /kingdom info <team|player> - lists team info or player info
        // /kingdom info <team> - lists team info (team name, team color, team alive, team spawn point, team leader, team members)
        // /kingdom info <player> - lists player info (player name, is leader or not, player team, player alive)

    // LIST - permission: kingdom.list
    // /kingdom list <team> - lists team info - team name, team leader, team status (alive or eliminated), list team members

    // RESPAWN - permission: kingdom.respawn
    // /kingdom respawn <player|team> - unbans/respawns a player or revives a team
        // /kingdom respawn player <player> - unbans/respawns a player
        // /kingdom respawn team <team> - revives a team

    // KICK - permission: kingdom.kick
    // /kingdom kick <player> - kicks a player from their team

    // RESET - permission: kingdom.reset
    // /kingdom reset <all|player|team> - resets all player data, specific player's data, or a specific team's data (including team leader, team spawn, and team members)
        // /kingdom reset all - resets all player data
        // /kingdom reset player <player> - resets a specific player's data
        // /kingdom reset team <team> - resets a specific team's data
    private final List<String> subCommands = List.of("help", "set", "randomteams", "info", "list", "respawn", "kick", "reset");

    @Override
    public boolean execute(@NotNull CommandContext cmd){
        CommandSender sender = cmd.sender();
        String[] args = cmd.args();
        if (args.length == 0) {
            Text.of("&cYou must specify a subcommand.").send(sender);
            return true;
        } else {
            String arg = args[0];
            if (arg.equalsIgnoreCase("help")) {
                    Text.of("&b[« &4&l💠 &b»] &6&l&nBattle&e&l 👑 &6&l&nKingdom&9&l Help&r &b[« &4&l💠 &b»]").send(sender);
                    if (sender.hasPermission("kingdom.set")) {
                        Text.of("\n&f/kingdom set &e<team> <player> &7-&6 Sets player's team\n" +
                                "&f/kingdom set leader &e<team> <player> &7-&6 Sets team's leader\n" +
                                "&f/kingdom set spawn &e<team> &7-&6 Sets team's spawn point\n" +
                                "&f/kingdom set name &e<team> <name> &7-&6 Set's a team's name"
                        ).send(sender);
                    }
                    if (sender.hasPermission("kingdom.randomteams")) {
                        Text.of("\n&f/kingdom randomteams all &7-&6 Randomly assigns all players to teams\n" +
                                "&f/kingdom randomteams player &e<player> &7-&6 Randomly assigns a specific player to a team"
                        ).send(sender);
                    }
                    if (sender.hasPermission("kingdom.info.team")) {
                        Text.of("\n&f/kingdom info &e<team> &7-&6 Lists team info").send(sender);
                    }
                    Text.of("\n&f/kingdom info &e<player> &7-&6 Lists player info").send(sender);
                    Text.of("\n&f/kingdom list &e<team> &7-&6 Lists team info").send(sender);
                    if (sender.hasPermission("kingdom.respawn")) {
                        Text.of("\n&f/kingdom respawn &e<player> &7-&6 Revives a player\n" +
                                "&f/kingdom respawn &e<team> &7-&6 Revives a team"
                        ).send(sender);
                    }
                    if (sender.hasPermission("kingdom.kick")) {
                        Text.of("\n&f/kingdom kick &e<player> &7-&6 Kicks a player from their team"
                        ).send(sender);
                    }
                    if (sender.hasPermission("kingdom.reset")) {
                        Text.of("\n&f/kingdom reset &e<all> &7- &4&lResets all player data\n" +
                                "&f/kingdom reset &e<player> &7-&6 Resets a specific player's data\n" +
                                "&f/kingdom reset &e<team> &7-&6 Resets a specific team's data"
                        ).send(sender);
                    }
                    Text.of("\n&f/kingdom help &7-&6 To display this message").send(sender);

                return true;
            }
            switch (arg) {
                case "set":
                    if (args.length == 1) {
                        Text.of("&cInvalid Command Usage.").send(sender);
                        Text.of("\n&f/kingdom set &e<team|leader|spawn>\n" +
                                "&7run /kingdom help for more info"
                        ).send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        switch (subArg) {
                            case "team":
                                if (args.length < 3) {
                                    Text.of("&cUsage: /kingdom set team <team> <player>").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.set.team")) {
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
                                    }
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                            case "leader":
                                if (args.length < 3) {
                                    Text.of("&cUsage: /kingdom set leader <team> <player>").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.set.leader")) {
                                    String team = args[2].toUpperCase();
                                    if (args.length == 3) {
                                        Text.of("&cYou must specify a player.").send(sender);
                                        return true;
                                    } else {
                                        Player p = Bukkit.getPlayer(args[3]);

                                        if (p != null) {
                                            TeamManager.setTeamLeader(p, team); // Corrected line
                                            Text.of("&aSuccessfully set " + p.getName() + " as the leader of " + team + ".").send(sender);
                                            return true;
                                        } else {
                                            Text.of("&cPlayer not found.").send(sender);
                                        }
                                    }
                                    return true;
                                } else {
                                Text.of("&cYou do not have permission to use this command.").send(sender);
                            }

                            case "spawn":
                                if (args.length < 3) {
                                    Text.of("&cUsage: /kingdom set spawn <team>").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.set.spawn")){
                                    String team = args[2];
                                    Player p = (Player) sender;
                                    TeamManager.setTeamSpawnPoint(team, p.getLocation());
                                    Text.of("&aSuccessfully set " + team + "'s spawn point.").send(sender);
                                    return true;
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                            case "name":
//                                TODO: Write this
                                return true;
                        }
                    }
                case "randomteams":
                    if (args.length < 2) {
                        Text.of("&cInvalid Command Usage.").send(sender);
                        Text.of("\n&f/kingdom randomteams &e<all|player>\n" +
                                "&7run /kingdom help for more info"
                        ).send(sender);
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
                                }
                            case "player":
                                if (args.length == 2 && sender.hasPermission("kingdom.randomteams.player")) {
                                    Text.of("&cYou must specify a player.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.randomteams.player")){
                                    String playerName = args[2];
                                    Bukkit.getOnlinePlayers().forEach(p -> {
                                        if (p.getName().equals(playerName)) {
                                            assignRandomTeam(p);
                                            Text.of("&aSuccessfully assigned a random team to " + playerName + ".").send(sender);
                                        }
                                    });
                                    return true;
                                } else {
                                Text.of("&cYou do not have permission to use this command.").send(sender);
                            }
                        }
                    }
                case "info":
                    if (args.length < 2) {
                        Text.of("&cInvalid Command Usage.").send(sender);
                        Text.of("\n&f/kingdom info &e<team|player>\n" +
                                "&7run /kingdom help for more info"
                        ).send(sender);
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
                                            Text.of("&7Team Name: " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamName() + "\n" +
                                                    "&7Team Color: " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamColor() + "\n" +
                                                    "&7Team Alive: " + TeamManager.getTeamStatus(team) + "\n" +
                                                    "&7Team Spawn Point: " + TeamManager.getSpawnLocationForTeam(team) + "\n" +
                                                    "&7Team Leader: " + TeamManager.getTeamLeader(team)  + "\n" +
                                                    "&7Team Members: " + TeamManager.getTeamMembers(team) // TODO: Fix, empty?
                                            ).send(sender);
                                        }
                                    });
                                    return true;
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
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
                                            Text.of("&9" + serverPlayer.getPlayerName() + "&7 is the &6&lleader&7 of Team &9" + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamName() + "\n" +
                                                    "&7Team Respawn: &9" + serverPlayer.isTeamAlive()).send(sender);
                                        }
                                        if (p.getName().equals(playerName) && !serverPlayer.isTeamLeader()) {
                                            Text.of("&9" + serverPlayer.getPlayerName() + "&7 is on Team " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamName() + "\n" +
                                                    "&7Team Respawn: &9" + serverPlayer.isTeamAlive()
                                            ).send(sender);
                                        }
                                    });
                                    return true;
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                        }
                    }
                case "list":
                    if (args.length == 2 && sender.hasPermission("kingdom.list.team")) {
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
                                        Text.of("&7Team Name: " + TeamManager.getTeamColorCode(serverPlayer.getTeamName()) + serverPlayer.getTeamName() + "\n" + "&7Team Leader: " + TeamManager.getLeaderStatus(team) + "\n" +
                                                "&7Team Alive: " + TeamManager.getTeamStatus(team) + "\n" +
                                                "&7Team Members: " + TeamManager.getTeamMembers(team) // TODO: Fix, empty? x2
                                        ).send(sender);
                                    }
                                });
                                return true;
                            }
                        }
                        return true;
                    } else {
                        Text.of("&cYou do not have permission to use this command.").send(sender);
                    }
                case "respawn":
                    if (args.length < 2) {
                        Text.of("&cInvalid Command Usage.").send(sender);
                        Text.of("\n&f/kingdom respawn &e<player|team>\n" +
                                "&7run /kingdom help for more info"
                        ).send(sender);
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
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                            case "team":
                                if (args.length == 2 && sender.hasPermission("kingdom.respawn.team")) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.respawn.team")) {
                                    String team = args[2].toUpperCase();
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
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                        }
                    }
                case "kick":
                    if (args.length == 1) {
                        Text.of("&cInvalid subcommand.\n" +
                                "&7kick <&bplayer&7>").send(sender);
                        return true;
                    } else {
                        String subArg = args[1];
                        if (subArg.equals("player")) {
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
                            } else {
                                Text.of("&cYou do not have permission to use this command.").send(sender);
                            }
                        }
                    }
                case "reset":
                    if (args.length < 2) {
                        Text.of("&cInvalid Command Usage.").send(sender);
                        Text.of("\n&f/kingdom reset &e<all|player|team>\n" +
                                "&7run /kingdom help for more info"
                        ).send(sender);
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
                                }
                            case "player": // TODO: FIX reset player
//                    org.bukkit.command.CommandException: Unhandled exception executing 'kingdom reset player iCats' in com.marcusslover.plus.lib.command.CommandManager$1(kingdom)
//                    at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:178) ~[purpur-api-1.20.1-R0.1-SNAPSHOT.jar:?]
//                    at org.bukkit.craftbukkit.v1_20_R1.CraftServer.dispatchCommand(CraftServer.java:1021) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at org.bukkit.craftbukkit.v1_20_R1.command.BukkitCommandWrapper.run(BukkitCommandWrapper.java:64) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at com.mojang.brigadier.CommandDispatcher.execute(CommandDispatcher.java:265) ~[purpur-1.20.1.jar:?]
//                    at net.minecraft.commands.Commands.performCommand(Commands.java:332) ~[?:?]
//                    at net.minecraft.commands.Commands.performCommand(Commands.java:316) ~[?:?]
//                    at net.minecraft.server.network.ServerGamePacketListenerImpl.performChatCommand(ServerGamePacketListenerImpl.java:2447) ~[?:?]
//                    at net.minecraft.server.network.ServerGamePacketListenerImpl.lambda$handleChatCommand$22(ServerGamePacketListenerImpl.java:2407) ~[?:?]
//                    at net.minecraft.util.thread.BlockableEventLoop.lambda$submitAsync$0(BlockableEventLoop.java:59) ~[?:?]
//                    at java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1768) ~[?:?]
//                    at net.minecraft.server.TickTask.run(TickTask.java:18) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.util.thread.BlockableEventLoop.doRunTask(BlockableEventLoop.java:153) ~[?:?]
//                    at net.minecraft.util.thread.ReentrantBlockableEventLoop.doRunTask(ReentrantBlockableEventLoop.java:24) ~[?:?]
//                    at net.minecraft.server.MinecraftServer.doRunTask(MinecraftServer.java:1365) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.server.MinecraftServer.d(MinecraftServer.java:197) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.util.thread.BlockableEventLoop.pollTask(BlockableEventLoop.java:126) ~[?:?]
//                    at net.minecraft.server.MinecraftServer.pollTaskInternal(MinecraftServer.java:1342) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.server.MinecraftServer.pollTask(MinecraftServer.java:1335) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.util.thread.BlockableEventLoop.managedBlock(BlockableEventLoop.java:136) ~[?:?]
//                    at net.minecraft.server.MinecraftServer.waitUntilNextTick(MinecraftServer.java:1313) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.server.MinecraftServer.runServer(MinecraftServer.java:1201) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at net.minecraft.server.MinecraftServer.lambda$spin$0(MinecraftServer.java:322) ~[purpur-1.20.1.jar:git-Purpur-2051]
//                    at java.lang.Thread.run(Thread.java:840) ~[?:?]
//                    Caused by: java.lang.IllegalArgumentException: Invalid UUID string: iCats
//                    at java.util.UUID.fromString1(UUID.java:280) ~[?:?]
//                    at java.util.UUID.fromString(UUID.java:258) ~[?:?]
//                    at io.github.cats1337.battlekingdom.commands.BattleKingdomCommands.execute(BattleKingdomCommands.java:348) ~[BattleKingdom-1.0.0.jar:?]
//                    at com.marcusslover.plus.lib.command.CommandManager$1.execute(CommandManager.java:61) ~[BattleKingdom-1.0.0.jar:?]
//                    at org.bukkit.command.SimpleCommandMap.dispatch(SimpleCommandMap.java:168) ~[purpur-api-1.20.1-R0.1-SNAPSHOT.jar:?]
//        ... 22 more
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
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                            case "team":
                                if (args.length == 2 && sender.hasPermission("kingdom.reset.team")) {
                                    Text.of("&cYou must specify a team.").send(sender);
                                    return true;
                                } else if (sender.hasPermission("kingdom.reset.team")) {
                                    String team = args[2].toUpperCase();
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
                                } else {
                                    Text.of("&cYou do not have permission to use this command.").send(sender);
                                }
                        }
                    }
            }
        }
        return true;
    }

    // Command: /kingdom

    // HELP - permission: kingdom.help
    // /kingdom help - lists all commands that the player has permission to use

    // SET - permission: kingdom.set
    // /kingdom set <team|leader|spawn>
        // /kingdom set <team> <player> - sets a player's team
        // /kingdom set leader <team> <player> - sets team leader
        // /kingdom set spawn <team> - sets team spawn point
//    &f/kingdom set &e<team> <player> &7-&6 Sets player's team
//    &f/kingdom set leader &e<team> <player> &7-&6 Sets team's leader
//    &f/kingdom set spawn &e<team> &7-&6 Sets team's spawn point

    // RANDOMTEAMS - permission: kingdom.randomteams
    // /kingdom randomteams <all|player> - randomly assigns players to teams
        // /kingdom randomteams all - randomly assigns all players to teams
        // /kingdom randomteams player <player> - randomly assigns a specific player to a team

    // INFO - permission: kingdom.info
    // /kingdom info <team|player> - lists team info or player info
        // /kingdom info <team> - lists team info (team name, team color, team alive, team spawn point, team leader, team members)
        // /kingdom info <player> - lists player info (player name, is leader or not, player team, player alive)

    // LIST - permission: kingdom.list
    // /kingdom list <team> - lists team info - team name, team leader, team status (alive or eliminated), list team members

    // RESPAWN - permission: kingdom.respawn
    // /kingdom respawn <player|team> - unbans/respawns a player or revives a team
        // /kingdom respawn player <player> - unbans/respawns a player
        // /kingdom respawn team <team> - revives a team

    // KICK - permission: kingdom.kick
    // /kingdom kick player <player> - kicks a player from their team

    // RESET - permission: kingdom.reset
    // /kingdom reset <all|player|team> - resets all player data, specific player's data, or a specific team's data (including team leader, team spawn, and team members)
        // /kingdom reset all - resets all player data
        // /kingdom reset player <player> - resets a specific player's data
        // /kingdom reset team <team> - resets a specific team's data

    @Override
    public @NotNull List<@NotNull String> tab(@NotNull TabCompleteContext tab) {
        CommandSender sender = tab.sender();
        @NotNull String[] args = tab.args();

        if (sender.hasPermission("kingdom.admin")) {
            if (args.length == 1) {
                return ITabCompleterHelper.tabComplete(args[0], subCommands);
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("team", "leader", "spawn", "name"));
                }
                if (args[0].equalsIgnoreCase("randomteams")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("all", "player"));
                }
                if (args[0].equalsIgnoreCase("info")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("team", "player"));
                }
                if (args[0].equalsIgnoreCase("list")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("team"));
                }
                if (args[0].equalsIgnoreCase("respawn")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("player", "team"));
                }
                if (args[0].equalsIgnoreCase("kick")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("player"));
                }
                if (args[0].equalsIgnoreCase("reset")) {
                    return ITabCompleterHelper.tabComplete(args[1], List.of("all", "player", "team"));
                }
            }
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("team") || args[1].equalsIgnoreCase("leader") || args[1].equalsIgnoreCase("spawn") || args[1].equalsIgnoreCase("name")) {
                    return ITabCompleterHelper.tabComplete(args[2], TeamManager.getTeamNames());
                }
                if (args[1].equalsIgnoreCase("player")) {
                    return ITabCompleterHelper.tabComplete(args[2], Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .toList());
                }
            }
        }
        return new ArrayList<>();
    }



}

