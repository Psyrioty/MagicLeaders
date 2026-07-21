package org.psyrioty.magicLeaders.Utils;

import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;

import java.util.Set;
import java.util.UUID;

public class APIHelper {
    public static Leader findLeaderForUUID(UUID uuid){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        for(Leader leader: leaders){
            if(leader.getUuid().equals(uuid)){
                return leader;
            }
        }

        return null;
    }

    public static Leader findLeaderForUUID(String uuid){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        for(Leader leader: leaders){
            if(leader.getUuid().toString().equals(uuid)){
                return leader;
            }
        }

        return null;
    }
}
