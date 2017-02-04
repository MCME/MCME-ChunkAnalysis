/*
 * This file is part of ChunkAnalysis.
 * 
 * ChunkAnalysis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ChunkAnalysis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ChunkAnalysis.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package com.mcmiddleearth.chunkanalysis;

import com.mcmiddleearth.chunkanalysis.job.Job;
import com.mcmiddleearth.chunkanalysis.util.DBUtil;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class MessageManager {
    
    @Getter
    private final static String prefix = "[ChunkAnalyis] ";
    
    @Getter
    private final static String indented = "               ";
    
    private final static Set<Player> listeningPlayers = new HashSet<>();
    
    public static void sendMessage(String[] messages, ChatColor color) {
        int i=0;
        Set<Player> offline=new HashSet<>();
        for(Player player: listeningPlayers) {
            if(!player.isOnline()) {
                offline.add(player);
            }
        }
        listeningPlayers.removeAll(offline);
        for(Player player: listeningPlayers) {
            player.sendMessage(color+prefix+messages[0]);
            for(int msgIndex = 1; msgIndex < messages.length; msgIndex++) {
                String message = messages[msgIndex];
                player.sendMessage(color+indented+message);
            }
        }
    }
    
    public static void addListeningPlayer(Player player) {
        listeningPlayers.add(player);
    }
        
    public static void removeListeningPlayer(Player player) {
        listeningPlayers.remove(player);
    }
        
    public static void sendCurrentJobStatus(Job job) {
        sendMessage(new String[]{"Running job JID"+job.getId()+" created by "+ChatColor.GREEN+Bukkit.getOfflinePlayer(job.getOwner()).getName(),
                                 job.statMessage()},ChatColor.AQUA);
        if(!DBUtil.isConnected()) {
            sendDBConnectionLost();
        }
    }
    
    public static void sendJobFinished(Job job) {
        sendMessage(new String[]{"Finished job JID "+job.getId()+" created by "+ChatColor.GREEN+Bukkit.getOfflinePlayer(job.getOwner()).getName()
                                 +ChatColor.AQUA+" in "+job.getDuration()+" sec.",
                                 job.getAction().statMessage()},ChatColor.AQUA);
    }

    public static void sendJobStarted(Job job) {
        sendMessage(new String[]{"Started job JID "+job.getId()+" created by "+ChatColor.GREEN+Bukkit.getOfflinePlayer(job.getOwner()).getName()
                                 +ChatColor.AQUA+" with "+job.getJobSize()+" chunks.",
                                 job.getAction().statMessage()},ChatColor.AQUA);
    }
    
    public static void sendDBConnectionLost() {
        sendMessage(new String[]{"Warning, connection to database lost. Will not be able to continue the job correctly after server restart."},
                    ChatColor.RED);
    }

    
}
