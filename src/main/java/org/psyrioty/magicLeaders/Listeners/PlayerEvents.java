package org.psyrioty.magicLeaders.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;

import java.util.Set;
import java.util.UUID;

public class PlayerEvents implements Listener {

    @EventHandler
    private void PlayerJoin(PlayerJoinEvent event){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        Player player = event.getPlayer();

        for(Leader leader: leaders){
            if(leader.getUuid().equals(player.getUniqueId())){
                return;
            }
        }

        Leader leader = new Leader(
            player
        );
        leaders.add(leader);
    }

    @EventHandler
    private void PlayerExit(PlayerQuitEvent event){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();

        for(Leader leader: leaders){
            if(leader.getUuid().equals(player.getUniqueId())){
                leaders.remove(leader);
                return;
            }
        }
    }
}
