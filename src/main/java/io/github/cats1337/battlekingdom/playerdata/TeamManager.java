package io.github.cats1337.battlekingdom.playerdata;

import io.github.cats1337.battlekingdom.BattleKingdom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.marcusslover.plus.lib.text.Text;


public class TeamManager {
    static final BattleKingdom plugin = BattleKingdom.getInstance();

    public static void assignRandomTeam(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());

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
        serverPlayer.setTeamName(team);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
    }

    public static void setSpectatorMode(Player p) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());
        serverPlayer.setAlive(false);
        playerContainer.writeData(p.getUniqueId(), serverPlayer);
        p.setGameMode(GameMode.SPECTATOR);
        Text.of("&7You're exempt from the dungeon, you are now a spectator.").send(p);
    }

    public static void setPlayerSpawnPoint(Player p, String team) {
        Location spawnLocation = getSpawnLocationForTeam(team);
        p.teleport(spawnLocation);
        p.setBedSpawnLocation(spawnLocation, true);
    }

    private static Location getSpawnLocationForTeam(String team) {
        double x = plugin.getConfig().getDouble("teams." + team + ".x");
        double y = plugin.getConfig().getDouble("teams." + team + ".y");
        double z = plugin.getConfig().getDouble("teams." + team + ".z");
        String worldName = plugin.getConfig().getString("teams." + team + ".world");

        assert worldName != null;
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public static void setTeamSpawnPoint(String team, Location location) {
        plugin.getConfig().set("teams." + team + ".x", location.getX());
        plugin.getConfig().set("teams." + team + ".y", location.getY());
        plugin.getConfig().set("teams." + team + ".z", location.getZ());
        plugin.getConfig().set("teams." + team + ".world", location.getWorld().getName());
        Bukkit.getScheduler().runTask(plugin, plugin::saveConfig);
    }
}
