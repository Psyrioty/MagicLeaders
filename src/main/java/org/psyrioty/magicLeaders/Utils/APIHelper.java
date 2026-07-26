package org.psyrioty.magicLeaders.Utils;

import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class APIHelper {
    public static Leader findLeaderForUUID(UUID uuid){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        for(Leader leader: leaders){
            if(leader.getOfflinePlayer().getUniqueId().toString().equals(uuid.toString())){
                return leader;
            }
        }

        return null;
    }

    public static Leader findLeaderForUUID(String uuid){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        for(Leader leader: leaders){
            if(leader.getOfflinePlayer().getUniqueId().toString().equals(uuid)){
                return leader;
            }
        }

        return null;
    }

    public static int findTopPositionForLeader(Leader leader, HashMap<Leader, Double> tops){
        int i = 0;
        for(Leader leaderTop: tops.keySet()){
            if(leader.equals(leaderTop)){
                return i;
            }

            i++;
        }

        return -1;
    }
}
