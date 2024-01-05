package io.github.cats1337.battlekingdom;

import io.github.cats1337.battlekingdom.commands.BattleKingdomCommands;
import io.github.cats1337.battlekingdom.playerdata.PlayerContainer;
import com.marcusslover.plus.lib.command.CommandManager;
import com.marcusslover.plus.lib.container.ContainerManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BattleKingdom extends JavaPlugin {
//    Minecraft Plugin for a 100-player battle royale. There are four teams, kingdoms, and each team has a leader. While the leader is still alive team members can respawn, but when the leader dies, the players get kicked on death. The team leaders get set into spectator mode and don't get kicked. The four teams are each given different spawn points.

    private CommandManager cmdManager;
    private ContainerManager containerManager;

    public static BattleKingdom getInstance() {return BattleKingdom.getPlugin(BattleKingdom.class);}

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        cmdManager = CommandManager.get(this);
        cmdManager.register(new BattleKingdomCommands());


        containerManager = new ContainerManager();
        containerManager.register("players", new PlayerContainer());
        containerManager.init(this);
    }

    @Override
    public void onDisable() {
        cmdManager.clearCommands();
    }

    public ContainerManager getContainerManager() {return containerManager;}
}
