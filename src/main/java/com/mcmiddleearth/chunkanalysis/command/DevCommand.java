/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.chunkanalysis.command;

import com.mcmiddleearth.chunkanalysis.util.DevUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class DevCommand extends AbstractCommand{
    

    public DevCommand(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": Get debug output to chat or console.");
        setUsageDescription("[true|false|r|#level]: Get debug output to chat or console.");
    }
    
    @Override
    protected void execute(CommandSender sender, String... argz) {
        if(argz.length>0 && argz[0].equalsIgnoreCase("true")) {
            DevUtil.setConsoleOutput(true);
            showDetails(sender);
            return ;
        }
        else if(argz.length>0 && argz[0].equalsIgnoreCase("false")) {
            DevUtil.setConsoleOutput(false);
            showDetails(sender);
            return ;
        }
        else if(argz.length>0) {
            try {
                int level = Integer.parseInt(argz[0]);
                DevUtil.setLevel(level);
                showDetails(sender);
                return ;
            }
            catch(NumberFormatException e){};
        }
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(argz.length>0 && argz[0].equalsIgnoreCase("r")) {
                DevUtil.remove(player);
                showDetails(sender);
                return ;
            }
            DevUtil.add(player);
            showDetails(sender);
        }
    }
    
    private void showDetails(CommandSender cs) {
        cs.sendMessage("DevUtil: Level - "+DevUtil.getLevel()+"; Console - "+DevUtil.isConsoleOutput()+"; ");
        cs.sendMessage("         Developer:");
        for(OfflinePlayer player:DevUtil.getDeveloper()) {
        cs.sendMessage("                "+player.getName());
        }
    }
    
}
