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
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
    private final static String prefix = ChunkAnalysis.getMessageUtil().getPREFIX();
    
    @Getter
    private final static String indented = "";
    
    private final static Set<Player> listeningPlayers = new HashSet<>();
    
    public static void sendMessage(String[] messages) {
        Set<Player> offline=new HashSet<>();
        for(Player player: listeningPlayers) {
            if(!player.isOnline()) {
                offline.add(player);
            }
        }
        listeningPlayers.removeAll(offline);
        for(Player player: listeningPlayers) {
            player.sendMessage(messages[0]+prefix+messages[1]);
            for(int msgIndex = 2; msgIndex < messages.length; msgIndex++) {
                String message = messages[msgIndex];
                player.sendMessage(messages[0]+indented+message);
            }
        }
    }
    
    public static void addListeningPlayer(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if(player!=null) {
            listeningPlayers.add(player);
        }
    }
        
    public static void removeListeningPlayer(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if(player!=null) {
            listeningPlayers.remove(player);
        }
    }
        
    public static void sendCurrentJobStatus(Job job) {
        sendMessage(new String[]{""+ChatColor.AQUA,"Running job "
                                          +ChatColor.GREEN+"["+job.getId()+"]"
                                          +ChatColor.AQUA+ " queued by "
                                          +ChatColor.GREEN+job.getOwnerName(),
                                   ChatColor.AQUA+job.statMessage()});
        if(!DBUtil.isConnected()) {
            sendDBConnectionLost();
        }
    }
    
    public static void sendJobFinished(Job job) {
        String[] message = new String[]{""+ChatColor.AQUA,"Finished job "
                                          +ChatColor.GREEN+"["+job.getId()+"]"
                                          +ChatColor.AQUA+ " queued by "
                                          +ChatColor.GREEN+job.getOwnerName()
                                          +ChatColor.AQUA+" in "+job.getDuration()+" sec.",
                                        job.detailMessage()};
        sendMessage(message);
        DBUtil.logMessage(message);
    }

    public static void sendJobCancelled(Job job) {
        String[] message = new String[]{""+ChatColor.AQUA,"Cancelled job "
                                          +ChatColor.GREEN+"["+job.getId()+"]"
                                          +ChatColor.AQUA+ " queued by "
                                          +ChatColor.GREEN+job.getOwnerName(),
                                         job.statMessage(),
                                         job.detailMessage()};
        sendMessage(message);
        DBUtil.logMessage(message);
    }

    public static void sendJobStarted(Job job) {
        String[] message = new String[]{""+ChatColor.AQUA,"Started job "
                                          +ChatColor.GREEN+"["+job.getId()+"]"
                                          +ChatColor.AQUA+ " queued by "
                                          +ChatColor.GREEN+job.getOwnerName()
                                          +ChatColor.AQUA+" with "+ChatColor.GREEN+job.getJobSize()
                                          +ChatColor.AQUA+" chunks."};
        sendMessage(message);
        DBUtil.logMessage(message);
    }
    
    public static void sendDBConnectionLost() {
        String[] message = new String[]{""+ChatColor.RED,"Warning, connection to database lost. Will not be able to continue the job correctly after server restart."};
        sendMessage(message);
        DBUtil.logMessage(message);
    }

    public static List<String[]> getMessages() {
        deleteOldMessages();
        return DBUtil.getMessages();
    }
    
    public static void deleteOldMessages() {
        DBUtil.deleteMessages(ChunkAnalysis.getMessageStoragePeriod());
    }
    
}
