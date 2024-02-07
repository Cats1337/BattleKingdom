package io.github.cats1337.battlekingdom.events;

import com.marcusslover.plus.lib.text.Text;
import io.github.cats1337.battlekingdom.BattleKingdom;
import io.github.cats1337.battlekingdom.playerdata.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    FileConfiguration config = BattleKingdom.getInstance().getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());

        // check if player exists in container, if not add them, assign random team, set spawn point
        if (serverPlayer.getPlayerName() == null) {
            serverPlayer.setPlayerName(p.getName());
            playerContainer.writeData(p.getUniqueId(), serverPlayer);
            // Assign a random team to the p on join
            TeamManager.assignRandomTeam(p);
            // Set spawn point and other initializations
            TeamManager.setPlayerSpawnPoint(p, serverPlayer.getTeamName());
        } else { // if player exists in container, update their name
            serverPlayer.setPlayerName(p.getName());
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        UUID playerUUID = p.getUniqueId();
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(playerUUID);

        String teamName = serverPlayer.getTeamName();
        if (serverPlayer.isTeamLeader()) {
            TeamManager.setRespawnStatus(teamName, false);
            TeamManager.setSpectatorMode(p);
            Text.of(TeamManager.getTeamColorCode(teamName) + teamName + "'s &e&l👑 &6&lKing &e&l👑 &6has been &cvanquished!").send(Bukkit.getOnlinePlayers());
            for (ServerPlayer player : playerContainer.getValues()) {
                if (player.getTeamName().equals(serverPlayer.getTeamName())) {
                    Player teamPlayer = Bukkit.getPlayer(player.getUuid());
                    if (teamPlayer != null) {
                        Text.of("&c&oYou will no longer respawn!").send(teamPlayer);
                    }
                }
            }
        }

        if (!TeamManager.getRespawnStatus(teamName)) {
            p.setGameMode(GameMode.SPECTATOR);
            if (serverPlayer.isExemptFromKick() || serverPlayer.isTeamLeader()) {
                TeamManager.setSpectatorMode(p);
                TeamManager.setEliminated(p, true);
            }
            if (!serverPlayer.isExemptFromKick() && !serverPlayer.isTeamLeader() && config.getBoolean("DUNGEON")) {
                PlayerHandler.tempBanPlayer(p, config.getInt("DUNGEON_TIME"), config.getString("DUNGEON_MESSAGE"));
                PlayerHandler.setOffEliminated(p.getName(), true);
            }
        }

        // check if anyone on the team is still alive
        for (ServerPlayer player : playerContainer.getValues()) {
            if (player.getTeamName().equals(teamName) && !player.isEliminated()) {
                return;
            } else { // no one on the team is alive on the server
                TeamManager.setTeamEliminated(teamName, true);
                // kill all offline players on the team
                for (ServerPlayer offlinePlayer : playerContainer.getValues()) {
                    if (offlinePlayer.getTeamName().equals(teamName)) {
                        Player offline = Bukkit.getPlayer(offlinePlayer.getUuid());
                        if (offline == null) {
                            PlayerHandler.setOffEliminated(offlinePlayer.getPlayerName(), true);
                            Player ofp = Bukkit.getOfflinePlayer(offlinePlayer.getUuid()).getPlayer();
                            if (ofp != null) {
                                PlayerHandler.tempBanPlayer(ofp, config.getInt("DUNGEON_TIME"), config.getString("DUNGEON_MESSAGE"));
                            }
                        }
                    }
                }
            }
        }
        if (TeamManager.getTeamEliminated(teamName)) {
            Text.of(TeamManager.getTeamColorCode(teamName) + teamName + " &chas been &celiminated!").send(Bukkit.getOnlinePlayers());
        }
    }
}
