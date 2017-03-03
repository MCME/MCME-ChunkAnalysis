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
package com.mcmiddleearth.chunkanalysis.command;

import com.mcmiddleearth.chunkanalysis.ChunkAnalysis;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class HelpCommand extends AbstractCommand {

    public HelpCommand(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": displays help about ChunkAnalysis commands.");
        setUsageDescription(" [command | #page]: Shows a description for [command]. If [command] is not specified a list of short descriptions for all vote commands is shown. Point at a description with mouse cursor for detailed help. Click to get the command in chat.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Map <String, AbstractCommand> commands = ((ChunkAnalyisisCommandExecutor)Bukkit.getPluginCommand("block").getExecutor())
                                                           .getCommands();
        if(args.length>0 && args[0].equalsIgnoreCase("block")) {
            args[0]="";
        } 
        if(args.length>0 && !NumericUtil.isInt(args[0])){
            AbstractCommand command = commands.get(args[0]);
            if(command==null) {
                sendNoSuchCommandMessage(cs, args[0]);
            }
            else {
                if(command.hasPermissions(cs)) {
                    ChunkAnalysis.getMessageUtil().sendInfoMessage(cs, "Help for:\n"
                                                   +ChatColor.GREEN+" /block "+args[0]
                                                   +" "+getUsageMessage(command));
                } else {
                   command.sendNoPermsErrorMessage(cs);
                }
            }
        }
        else {
            int page = 1;
            if(args.length>0 && NumericUtil.isInt(args[0])) {
                page = NumericUtil.getInt(args[0]);
            }
            FancyMessage header = new FancyMessage(MessageType.INFO,
                                                    ChunkAnalysis.getMessageUtil())
                                            .addSimple("Help for /block command ");
            List<FancyMessage> messages = new ArrayList<>();
            for(String key:commands.keySet()) {
                if(commands.get(key).hasPermissions(cs)) {
                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX,
                                                            ChunkAnalysis.getMessageUtil())
                            .addFancy(" "+ChatColor.GREEN+"/block "+key
                                         +ChatColor.AQUA+getShortMessage(commands.get(key)),
                                      "/vote "+key+" ",
                                      ChunkAnalysis.getMessageUtil()
                                              .hoverFormat("/block "+key+" "+getUsageMessage(commands.get(key)),
                                                           ":",true));
                    messages.add(message);
                }
            }
            ChunkAnalysis.getMessageUtil().sendFancyListMessage((Player)cs, header, messages,
                                                             "/block help ", page);
        }
    }

    private String getUsageMessage(AbstractCommand command) {
        String description = command.getUsageDescription();
        if(description==null){
            description = getShortMessage(command);
        }
        return description;
    }
    
    private String getShortMessage(AbstractCommand command) {
        String description = command.getShortDescription();
        if(description==null) {
            return ": There is no help for this command.";
        } else {
            return description;
        }
    }
    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "Command not found.");    
    }

}
