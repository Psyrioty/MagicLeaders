package org.psyrioty.magicLeaders.Objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Leader {
    Player player;
    UUID uuid;

    public Leader(
            Player player
    ){
        this.player = player;
        this.uuid = player.getUniqueId();
    }
}
