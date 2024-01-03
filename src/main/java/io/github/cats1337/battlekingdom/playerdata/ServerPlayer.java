package io.github.cats1337.battlekingdom.playerdata;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

import static io.github.cats1337.battlekingdom.playerdata.TeamManager.plugin;

@Data
@Accessors(chain = true)
public class ServerPlayer {
    private final UUID uuid;

    @Setter(AccessLevel.PUBLIC)
    private String playerName;
    private String teamName;
    private String teamColor; // set the team colors to be RED, GREEN, BLUE, YELLOW
    private boolean isTeamLeader;
    private boolean isAlive;
    private boolean isExemptFromKick;

    // Constructor to set default team color based on team name
    public ServerPlayer(UUID uuid, String playerName, String teamName) {
        this.uuid = uuid;
        this.playerName = (playerName != null) ? playerName : "";
        this.teamName = (teamName != null) ? teamName : "";
        this.teamColor = getDefaultTeamColor(this.teamName);
    }

    // Method to get default team color based on team name
    private String getDefaultTeamColor(String team) {
        String key = "teams." + team + ".name";
        return plugin.getConfig().getString(key, "GREY"); // Default to "GREY" if the key is not found
    }

}
