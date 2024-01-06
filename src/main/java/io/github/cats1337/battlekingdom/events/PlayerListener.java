package io.github.cats1337.battlekingdom.events;

import com.marcusslover.plus.lib.text.Text;
import io.github.cats1337.battlekingdom.BattleKingdom;
import io.github.cats1337.battlekingdom.playerdata.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    FileConfiguration config = BattleKingdom.getInstance().getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        System.out.println("onPlayerJoin: " + e + p.getName());
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
        System.out.println("onPlayerDeath: " + e + p.getName());
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());

        if(serverPlayer.isTeamLeader()) {
            serverPlayer.setTeamAlive(false);
            TeamManager.setSpectatorMode(p);
            playerContainer.writeData(p.getUniqueId(), serverPlayer);
            String teamName = config.getString("teams." + serverPlayer.getTeamName() + ".name");
            Text.of("&6&lThe " + (serverPlayer.getTeamColor()) + teamName + " &6&lKing is dead!").send(Bukkit.getOnlinePlayers());
        } else if (!serverPlayer.isTeamAlive() && serverPlayer.isExemptFromKick()) {
            TeamManager.setSpectatorMode(p);
        }
        else {
            PlayerHandler.tempBanPlayer(p, config.getInt("DUNGEON_TIME"), config.getString("DUNGEON_MESSAGE"));
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        Player p = (Player)e.getEntity();

        System.out.println("onDeath: " + e + p.getName());

        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(p.getUniqueId());

        if(serverPlayer.isTeamLeader()) {
            serverPlayer.setTeamAlive(false);
            TeamManager.setSpectatorMode(p);
            playerContainer.writeData(p.getUniqueId(), serverPlayer);
            String teamName = config.getString("teams." + serverPlayer.getTeamName() + ".name");
            Text.of("&6&lThe " + (serverPlayer.getTeamColor()) + teamName + " &6&lKing is dead!").send(Bukkit.getOnlinePlayers());
        } else if (!serverPlayer.isTeamAlive() && serverPlayer.isExemptFromKick()) {
            TeamManager.setSpectatorMode(p);
        }
        else {
            PlayerHandler.tempBanPlayer(p, config.getInt("DUNGEON_TIME"), config.getString("DUNGEON_MESSAGE"));
        }
    }

}
