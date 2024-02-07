package io.github.cats1337.battlekingdom.playerdata;


import io.github.cats1337.battlekingdom.BattleKingdom;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import com.destroystokyo.paper.profile.PlayerProfile;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class PlayerHandler implements Listener {
    private static PlayerHandler instance;
    public PlayerHandler() {
        instance = this;
    }

    public static PlayerHandler getInstance() {
        return Objects.requireNonNullElseGet(instance, PlayerHandler::new);
    }

    public @NotNull Collection<ServerPlayer> getGamePlayers() {
        return getContainer().getValues();
    }

    public PlayerContainer getContainer() {
        if (BattleKingdom.getInstance().getContainerManager().getByType(PlayerContainer.class).isEmpty()){
            return null;
        }
        return BattleKingdom.getInstance().getContainerManager().getByType(PlayerContainer.class).get();
    }

    public static void tempBanPlayer(Player p, int dungeonTime, String reason) {
        long durationInMillis = (long) dungeonTime * 60 * 60 * 1000;
        Date banTime = new Date(System.currentTimeMillis() + durationInMillis);
    
        BanList<PlayerProfile> banList = Bukkit.getBanList(BanList.Type.PROFILE);
        PlayerProfile profile = (p.getPlayerProfile());
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(profile);        
    
        if (banEntry == null) {
            banList.addBan(profile, ("§c" + reason), banTime, null);
            p.kickPlayer("§c" + reason);
        } else {
            banEntry.setExpiration(banTime);
        }
    }
    
    public static boolean untempbanPlayer(String p){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p);
        if (offlinePlayer.isBanned()) {
            BanList<PlayerProfile> banList = Bukkit.getBanList(BanList.Type.PROFILE);
            PlayerProfile profile = Bukkit.createProfile(offlinePlayer.getUniqueId(), p);
            BanEntry<PlayerProfile> banEntry = banList.getBanEntry(profile);
            if (banEntry != null) {
                banList.pardon(profile);
                return true;
            }
        }
        return false;
    }

    public static boolean untempbanTeam(String t){
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer player : playerContainer.getValues()) {
            if (player.getTeamName().equals(t)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUuid());
                if (offlinePlayer.isBanned()) {
                    BanList<PlayerProfile> banList = Bukkit.getBanList(BanList.Type.PROFILE);
                    PlayerProfile profile = Bukkit.createProfile(offlinePlayer.getUniqueId(), player.getPlayerName());
                    BanEntry<PlayerProfile> banEntry = banList.getBanEntry(profile);
                    if (banEntry != null) {
                        banList.pardon(profile);
                        return true;
                    }
                }
            }
        }
        // get banned-players.json file from container
        // get for all players in the team that are marked as eliminated
        // unban all players in the team

        // check if ban message is the same as the one in the config, if not, ignore them




        return false;
    }

    public static boolean setOffEliminated(String p, boolean status) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p);
        // set player to not eliminated
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        ServerPlayer serverPlayer = playerContainer.loadData(offlinePlayer.getUniqueId());

        serverPlayer.setEliminated(status);
        playerContainer.writeData(offlinePlayer.getUniqueId(), serverPlayer);
        return true;
    }

    public static boolean setOffEliminatedTeam(String t, boolean status) {
        PlayerContainer playerContainer = PlayerHandler.getInstance().getContainer();
        for (ServerPlayer player : playerContainer.getValues()) {
            if (player.getTeamName().equals(t)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUuid());
                ServerPlayer serverPlayer = playerContainer.loadData(offlinePlayer.getUniqueId());
                serverPlayer.setEliminated(status);
                playerContainer.writeData(offlinePlayer.getUniqueId(), serverPlayer);
            }
        }
        return true;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ServerPlayer serverPlayer = getContainer().loadData(uuid);
        if (serverPlayer.getPlayerName() == null) {
            serverPlayer.setPlayerName(p.getName());
            getContainer().writeData(uuid, serverPlayer);
        } else {
            serverPlayer.setPlayerName(p.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        ServerPlayer serverPlayer = getContainer().loadData(uuid);
        getContainer().writeData(uuid, serverPlayer);
    }
}
