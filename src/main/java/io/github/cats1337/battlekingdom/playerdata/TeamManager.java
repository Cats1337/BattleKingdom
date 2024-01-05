package io.github.cats1337.battlekingdom.playerdata;

import io.github.cats1337.battlekingdom.BattleKingdom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.marcusslover.plus.lib.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class TeamManager {
    static final BattleKingdom plugin = BattleKingdom.getInstance();
    static FileConfiguration config = BattleKingdom.getInstance().getConfig();


    public static void assignRandomTeam(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        if (serverPlayer.isTeamLeader()) {
            serverPlayer.setTeamLeader(false);
        }
        String randomTeam = getRandomTeam();
        serverPlayer.setTeamName(randomTeam);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
        setPlayerSpawnPoint(p, randomTeam);
    }

    private static String getRandomTeam() {
        String[] teams = {"TEAM1", "TEAM2", "TEAM3", "TEAM4"};
        int randomIndex = (int) (Math.random() * teams.length);
        return teams[randomIndex];
    }

    public static void setTeamLeader(Player p, String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        serverPlayer.setTeamLeader(true);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
        config.set("teams." + team + ".leader", p.getName());
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }

    public static String getTeamLeader(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                return serverPlayer.getPlayerName();
            }
        }
        return null;
    }

    // TODO: Doesn't output.
    public static String getTeamMembers(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        List<String> teamMembers = new ArrayList<>();

        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (team.equals(serverPlayer.getTeamName())) {
                if (serverPlayer.isTeamAlive()) {
                    teamMembers.add("&a" + serverPlayer.getPlayerName());
                } else {
                    teamMembers.add("&c&m" + serverPlayer.getPlayerName());
                }
            }
        }

        if (teamMembers.isEmpty()) {
            return "No members in the team";
        }

        return String.join("&7, ", teamMembers);
    }

    public static String getTeamColorCode(String team) {
        String key = "teams." + team + ".color";
        String color = plugin.getConfig().getString(key, "GREY");
        color = color.toUpperCase();
        return switch (color) {
            case "GREEN" -> "&a";
            case "BLUE" -> "&b";
            case "RED" -> "&c";
            case "PINK" -> "&d";
            case "YELLOW" -> "&e";
            case "WHITE" -> "&f";
            case "BLACK" -> "&0";
            case "DARK_BLUE" -> "&1";
            case "DARK_GREEN" -> "&2";
            case "DARK_AQUA" -> "&3";
            case "DARK_RED" -> "&4";
            case "PURPLE" -> "&5";
            case "GOLD" -> "&6";
            case "GREY" -> "&7";
            case "DARK_GREY" -> "&8";
            case "LIGHT_BLUE" -> "&9";
            default -> "&r";
        };
    }

    public static String getLeaderStatus(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                if (serverPlayer.isTeamAlive()) {
                    return "&6&l" + serverPlayer.getPlayerName();
                } else {
                    return "&c&m" + serverPlayer.getPlayerName();
                }
            }
        }
        return null;
    }

    public static String getTeamStatus(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team)) {
                if (serverPlayer.isTeamAlive()) {
                    return "&a Alive!";
                } else {
                    return "&c&m Eliminated";
                }
            }
        }
        return null;
    }

    public static void setSpectatorMode(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        serverPlayer.setTeamAlive(false);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
        p.setGameMode(GameMode.SPECTATOR);
        Text.of("&7You're exempt from the dungeon, you are now a spectator.").send(p);
    }

    public static void setPlayerSpawnPoint(Player p, String team) {
        double x = plugin.getConfig().getDouble("teams." + team + ".x");
        double y = plugin.getConfig().getDouble("teams." + team + ".y");
        double z = plugin.getConfig().getDouble("teams." + team + ".z");
        String worldName = plugin.getConfig().getString("teams." + team + ".world");

        assert worldName != null;

        Location spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

        p.teleport(spawnLocation);
        p.setBedSpawnLocation(spawnLocation, true);
    }

    public static String getSpawnLocationForTeam(String team) {
        double x = plugin.getConfig().getDouble("teams." + team + ".x");
        double y = plugin.getConfig().getDouble("teams." + team + ".y");
        double z = plugin.getConfig().getDouble("teams." + team + ".z");

        return (x + ", " + y + ", " + z);
    }

    public static void setTeamSpawnPoint(String team, Location location) {
        plugin.getConfig().set("teams." + team + ".x", location.getX());
        plugin.getConfig().set("teams." + team + ".y", location.getY());
        plugin.getConfig().set("teams." + team + ".z", location.getZ());
        plugin.getConfig().set("teams." + team + ".world", location.getWorld().getName());
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }


    public static List<String> getTeamNames() {
        return new ArrayList<>(config.getConfigurationSection("teams").getKeys(false));
    }

    public static List<String> getTeamLeaders() {
        List<String> teamLeaders = new ArrayList<>();
        for (String team : getTeamNames()) {
            String teamLeader = config.getString("teams." + team + ".leader");
            if (teamLeader != null) {
                teamLeaders.add(teamLeader);
            }
        }
        return teamLeaders;
    }

}
