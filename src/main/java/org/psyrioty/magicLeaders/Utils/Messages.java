package org.psyrioty.magicLeaders.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Messages {

    static List<String> header = new ArrayList<>(List.of(
            "",
            "",
            " <gradient:#32CD32:#ADFF2F><bold>ТОПЫ</bold></gradient>",
            ""
    ));

    static List<String> footer = new ArrayList<>(List.of(
            "",
            " <hover:show_text:'Нажмите, чтобы открыть'><#3CB371><bold><click:run_command:'/leaders'>/leaders</click></bold></#3CB371></hover>",
            ""
    ));

    public static void newTopMessage(HashMap<Leader, Integer> newLeaders, Leaderboard leaderboard){
        MiniMessage mm = MiniMessage.miniMessage();

        List<Component> message = new ArrayList<>();

        for(String line: header){
            message.add(mm.deserialize(
                line
            ));
        }

        message.add(mm.deserialize(
            " <bold>" + leaderboard.getName() + "</bold>"
        ));

        message.add(mm.deserialize(
                ""
        ));

        for(Leader leader: newLeaders.keySet()){
            message.add(mm.deserialize(
               " <#98FB98>Игрок <#3CB371>" + leader.getOfflinePlayer().getName() + " <#98FB98>сдвинулся на позицию <#3CB371>" + (newLeaders.get(leader) + 1)
            ));
        }

        for(String line: footer){
            message.add(mm.deserialize(
                    line
            ));
        }

        sendAllMessage(message);
    }

    private static void sendAllMessage(List<Component> message){
        for(Player player: Bukkit.getOnlinePlayers()){
            for(Component line: message){
                try {
                    player.sendMessage(line);
                }catch (Exception exception){

                }
            }
        }
    }
}
