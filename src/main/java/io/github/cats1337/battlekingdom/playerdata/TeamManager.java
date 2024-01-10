package io.github.cats1337.battlekingdom.playerdata;

import io.github.cats1337.battlekingdom.BattleKingdom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.marcusslover.plus.lib.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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
    public static String getTeamColor(String team) {
        String key = "teams." + team + ".color";
        String color = plugin.getConfig().getString(key, "GREY");
        return color.toUpperCase();
    }
    public static void setTeamColor(String team, String color) {
        plugin.getConfig().set("teams." + team + ".color", color);
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }
    public static void setTeamLeader(Player p, String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        // set old leader to false
        for (ServerPlayer player : playerContainer.getValues()) {
            if (player.getTeamName().equals(team) && player.isTeamLeader()) {
                player.setTeamLeader(false);
                UUID playerUUID = p.getUniqueId();
                playerContainer.writeData(playerUUID, player);
            }
        }
        // add player to team
        serverPlayer.setTeamName(team);
        // set new leader to true
        serverPlayer.setTeamLeader(true);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
        config.set("teams." + team + ".leader", p.getName());
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }
    public static boolean isTeamLeader(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        return serverPlayer.isTeamLeader();
    }
    public static String getLeaderStatus(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                if (serverPlayer.isTeamRespawn()) {
                    return "&6&l" + serverPlayer.getPlayerName();
                } else {
                    return "&c&m" + serverPlayer.getPlayerName();
                }
            }
        }
        return null;
    }
    public static List<String> getTeamNames() {
        return new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false));
    }
    public static String getTeamName(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        return serverPlayer.getTeamName();
    }
    public static void setTeamName(String teamName, String newName) {
        plugin.getConfig().set("teams." + teamName + ".name", newName);
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }

    public static boolean getRespawnStatus(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                return serverPlayer.isTeamRespawn();
            }
        }
        return false;
    }
    public static void setRespawnStatus(String team, boolean status) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team) && serverPlayer.isTeamLeader()) {
                serverPlayer.setTeamRespawn(status);
                playerContainer.writeData(serverPlayer.getUuid(), serverPlayer);
            }
        }
    }
    public static String getTeamMembers(String team) {
    
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
    
        List<String> teamMembers = new ArrayList<>();
    
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (team.equals(serverPlayer.getTeamName())) {
                if (serverPlayer.isEliminated()) {
                    teamMembers.add("&c&m" + serverPlayer.getPlayerName());
                } else {
                    teamMembers.add("&a" + serverPlayer.getPlayerName());
                }
            }
        }
    
        if (teamMembers.isEmpty()) {
            return "No members in the team";
        }
    
        return String.join("&7, ", teamMembers);
    }
    public static String getTeamStatus(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team)) {
                if (serverPlayer.isTeamRespawn()) {
                    return "&aAlive!";
                } else {
                    return "&c&mEliminated!";
                }
            }
        }
        return null;
    }
    public static boolean getTeamEliminated(String team) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team)) {
                return serverPlayer.isTeamEliminated();
            }
        }
        return false;
    }
    public static void setTeamEliminated(String team, boolean status) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer serverPlayer : playerContainer.getValues()) {
            if (serverPlayer.getTeamName().equals(team)) {
                serverPlayer.setTeamEliminated(status);
                playerContainer.writeData(serverPlayer.getUuid(), serverPlayer);
            }
        }
    }
    public static void setSpectatorMode(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        TeamManager.setEliminated(p, true);
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

        x = Math.round(x * 100.0) / 100.0;
        y = Math.round(y * 100.0) / 100.0;
        z = Math.round(z * 100.0) / 100.0;

        return (x + ", " + y + ", " + z);
    }
    public static void setTeamSpawnPoint(String team, Location location) {
        plugin.getConfig().set("teams." + team + ".x", location.getX());
        plugin.getConfig().set("teams." + team + ".y", location.getY());
        plugin.getConfig().set("teams." + team + ".z", location.getZ());
        plugin.getConfig().set("teams." + team + ".world", location.getWorld().getName());
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }
    public static void teleportToTeamSpawnPoint(Player p) {
        String team = getTeamName(p);
        double x = plugin.getConfig().getDouble("teams." + team + ".x");
        double y = plugin.getConfig().getDouble("teams." + team + ".y");
        double z = plugin.getConfig().getDouble("teams." + team + ".z");
        String worldName = plugin.getConfig().getString("teams." + team + ".world");

        assert worldName != null;

        Location spawnLocation = new Location(Bukkit.getWorld(worldName), x, y, z);

        p.teleport(spawnLocation);
    }
    public static void setExemptFromKick(Player p, boolean status) {
        plugin.getConfig().set("teams." + getTeamName(p) + ".exempt", status);
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }
    public static boolean isExemptFromKick(Player p) {
        return plugin.getConfig().getBoolean("teams." + getTeamName(p) + ".exempt");
    }
    public static void setEliminated(Player p, boolean status) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        serverPlayer.setEliminated(status);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
    }
    public static boolean isEliminated(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        return serverPlayer.isEliminated();
    }
}
