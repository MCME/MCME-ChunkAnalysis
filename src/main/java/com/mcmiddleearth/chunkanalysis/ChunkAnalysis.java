/*
 * This file is part of ChunkAnalysis.
 * 
 * EnforcerSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EnforcerSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EnforcerSuite.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package com.mcmiddleearth.chunkanalysis;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class ChunkAnalysis extends JavaPlugin {
    
    @Override
    public void onEnable(){
        this.getCommand("block").setExecutor(new Commands());
    }
    
    public class Commands implements CommandExecutor{

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] argz) {
            List<String> args = Arrays.asList(argz);
            if(args.size() > 0){
                if(args.get(0).equalsIgnoreCase("analyse") && args.size() > 1){
                    Material block;
                    byte data;
                    String tags;
                    boolean clip = false;
                    String pack;
                    if(args.get(1).contains(":")){
                        String[] bd = args.get(1).split(":");
                        block = Material.getMaterial(bd[0]);
                        data = Byte.parseByte(bd[1]);
                    }else{
                        block = Material.getMaterial(args.get(1));
                        data = (byte) 0;
                    }
                    if(args.contains("-t")){
                        int i = args.indexOf("-t");
                        tags = args.get(i+1);
                    }
                    if(args.contains("-s")){
                        clip = true;
                    }
                    if(args.contains("-p")){
                        int i = args.indexOf("-p");
                        pack = args.get(i+1);
                    }
                    
                }else if(args.get(0).equalsIgnoreCase("replace")){
                    Material blockFind;
                    byte dataFind;
                    String tagsFind;
                    Material blockReplace;
                    byte dataReplace;
                    String tagsReplace;
                    boolean clip = false;
                    String pack;
                    int j = 2;
                    for(;j<args.size();j+=2){
                        if(!args.get(j).contains("-")){
                            break;
                        }
                    }
                    List<String> findArgs = args.subList(1, j);
                    List<String> replaceArgs = args.subList(j, args.size());
                    if(findArgs.get(1).contains(":")){
                        String[] bd = findArgs.get(1).split(":");
                        blockFind = Material.getMaterial(bd[0]);
                        dataFind = Byte.parseByte(bd[1]);
                    }else{
                        blockFind = Material.getMaterial(findArgs.get(1));
                        dataFind = (byte) 0;
                    }
                    if(replaceArgs.get(1).contains(":")){
                        String[] bd = replaceArgs.get(1).split(":");
                        blockReplace = Material.getMaterial(bd[0]);
                        dataReplace = Byte.parseByte(bd[1]);
                    }else{
                        blockFind = Material.getMaterial(replaceArgs.get(1));
                        dataFind = (byte) 0;
                    }
                    if(findArgs.contains("-t")){
                        int i = findArgs.indexOf("-t");
                        tagsFind = findArgs.get(i+1);
                    }
                    if(replaceArgs.contains("-t")){
                        int i = replaceArgs.indexOf("-t");
                        tagsReplace = replaceArgs.get(i+1);
                    }
                    if(args.contains("-s")){
                        clip = true;
                    }
                    if(args.contains("-p")){
                        int i = args.indexOf("-p");
                        pack = args.get(i+1);
                    }
                    
                }
            }
            return false;
        }
    }
}
