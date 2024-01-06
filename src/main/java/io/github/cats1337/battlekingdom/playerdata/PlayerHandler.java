package io.github.cats1337.battlekingdom.playerdata;


import io.github.cats1337.battlekingdom.BattleKingdom;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

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
        if (!BattleKingdom.getInstance().getContainerManager().getByType(PlayerContainer.class).isPresent()){
            return null;
        }
        return BattleKingdom.getInstance().getContainerManager().getByType(PlayerContainer.class).get();
    }

    // TODO: Doesn't actually ban, might just use console :/
    public static void tempBanPlayer(Player p, int dungeonTime, String reason) {
        long durationInMillis = (long) dungeonTime * 60 * 60 * 1000;
        Date banTime = new Date(System.currentTimeMillis() + durationInMillis);

        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry banEntry = banList.getBanEntry(p.getName());

        if (banEntry == null) {
            banList.addBan(p.getName(), reason, banTime, null);
        } else {
            banEntry.setExpiration(banTime);
        }
    }

    public static void unbanPlayer(Player p){
        if (p.isBanned()) {
            BanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
            BanEntry banEntry = banList.getBanEntry(p.getName());
            if (banEntry != null) {
                banList.pardon(banEntry);
            }
        }
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
