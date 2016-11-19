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

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.dags.resourceregions.region.Region;
import me.dags.resourceregions.region.RegionManager;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class ChunkAnalysis extends JavaPlugin {
    
    private static Point maxChunk;
    private static Point minChunk;
    
    private static WorldEditPlugin worldEdit;
    
    private static RegionManager rm = null;
    
    @Override
    public void onEnable(){
        this.getCommand("block").setExecutor(new Commands());
        worldEdit = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
        try {
            Method gi = RegionManager.class.getMethod("i");
            gi.setAccessible(true);
            rm = (RegionManager) gi.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ChunkAnalysis.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.saveDefaultConfig();
        maxChunk = confToPoint("maxChunk");
        minChunk = confToPoint("minChunk");
    }
    
    private Point confToPoint(String conf){
        String[] crds = this.getConfig().getString(conf).split(",");
        return new Point(Integer.parseInt(crds[0]), Integer.parseInt(crds[1]));
    }
    
    public class Commands implements CommandExecutor{

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] argz) {
            List<String> args = Arrays.asList(argz);
            if(args.size() > 0){
                if(args.get(0).equalsIgnoreCase("analyse") && args.size() > 1){
                    Material block;
                    byte data;
                    String tags = null;
                    boolean clip = false;
                    String pack = null;
                    if(args.get(1).contains(":")){
                        String[] bd = args.get(1).split(":");
                        block = Material.getMaterial(bd[0]);
                        data = Byte.parseByte(bd[1]);
                    }else{
                        block = Material.getMaterial(args.get(1));
                        data = (byte) 0;
                    }
                    if(args.contains("-t")){
                        if(args.contains("-s") && args.contains("-p")){
                            int i = args.indexOf("-t");
                            tags = args.get(i+1);
                        }else{
                            sender.sendMessage("Cannot search whole map with block flags");
                            return true;
                        }
                    }
                    if(args.contains("-s") && args.contains("-p")){
                        sender.sendMessage("Cannot have both clip and pack");
                        return true;
                    }
                    if(args.contains("-s")){
                        if(sender instanceof Player){
                            clip = true;
                        }else{
                            sender.sendMessage("Cannot use selection with console!");
                            return true;
                        }
                    }else if(args.contains("-p")){
                        if(sender instanceof Player && rm != null){
                            int i = args.indexOf("-p");
                            pack = args.get(i+1);
                        }else if(rm == null){
                            sender.sendMessage("Internal error: could not locate resource regions plugin data");
                            return true;
                        }else{
                            sender.sendMessage("Cannot use pack param with console!");
                            return true;
                        }
                    }
                    sender.sendMessage("Query Results:");
                    sender.sendMessage("==============");
                    int count = 0;
                    if(clip){
                        Selection sel = worldEdit.getSelection((Player) sender);
                        for(int x = sel.getMinimumPoint().getBlockX(); x <= sel.getMaximumPoint().getBlockX(); x++){
                            for(int y = sel.getMinimumPoint().getBlockY(); y <= sel.getMaximumPoint().getBlockY(); y++){
                                for(int z = sel.getMinimumPoint().getBlockZ(); z <= sel.getMaximumPoint().getBlockZ(); z++){
                                    Block b = new Location(sel.getWorld(), x, y, z).getBlock();
                                    if(b.getType().equals(block) && b.getData() == data && (tags != null ? checkFlags(tags, b) : true)){
                                        sender.sendMessage(" - ("+x+","+y+","+z+")");
                                        count++;
                                    }
                                }
                            }
                        }
                    }else if(pack != null){
                        Region reg = null;
                        for(Region r : (Set<Region>) forceGet(rm, "regions")){
                            if(r.getName().equalsIgnoreCase(pack)){
                                reg=r;
                                break;
                            }
                        }
                        if(reg==null){
                            sender.sendMessage("Pack not found!");
                            return true;
                        }
                        int n = (int) forceGet(rm, "n");
                        int[] xpoints = (int[]) forceGet(rm, "xpoints");
                        int[] zpoints = (int[]) forceGet(rm, "zpoints");
                        Rectangle bounds = new Polygon(xpoints, zpoints, n).getBounds();
                        for(int x = bounds.x; x < bounds.x + bounds.width; x++){
                            for(int z = bounds.y; z < bounds.y + bounds.height; z++){
                                for(int y = 0; y < 255; y++){
                                    Block b = new Location(((Player) sender).getWorld(), x, y, z).getBlock();
                                    if(b.getType().equals(block) && b.getData() == data && (tags != null ? checkFlags(tags, b) : true)){
                                        sender.sendMessage(" - ("+x+","+y+","+z+")");
                                        count++;
                                    }
                                }
                            }
                        }
                    }else{
                        World w = ((Player) sender).getWorld();
                        for(int cx = minChunk.x; cx <= maxChunk.x; cx++){
                            for(int cy = minChunk.y; cy <= maxChunk.y; cy++){
                                ChunkSnapshot c = w.getChunkAt(cx, cy).getChunkSnapshot(true, false, false);
                                for(int x = 0; x <= 15; x++){
                                    for(int y = 0; y <= 127; y++){
                                        for(int z = 0; z <= 15; z++){
                                            if(c.getBlockTypeId(x, y, z) == block.getId() && c.getBlockData(x, y, z) == data){
                                                sender.sendMessage(" - ("+(cx*16+x)+","+y+","+(cy*16+z)+")");
                                                count++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sender.sendMessage(count + "results found");
                }else if(args.get(0).equalsIgnoreCase("replace")){
                    Material blockFind;
                    byte dataFind;
                    String tagsFind = null;
                    Material blockReplace;
                    byte dataReplace;
                    String tagsReplace = null;
                    boolean clip = false;
                    String pack = null;
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
                        blockReplace = Material.getMaterial(replaceArgs.get(1));
                        dataReplace = (byte) 0;
                    }
                    if(findArgs.contains("-t")){
                        if(findArgs.contains("-s") && findArgs.contains("-p")){
                            int i = findArgs.indexOf("-t");
                            tagsFind = findArgs.get(i+1);
                        }else{
                            sender.sendMessage("Cannot search whole map with block flags");
                            return true;
                        }
                    }
                    if(replaceArgs.contains("-t")){
                        if(replaceArgs.contains("-s") && replaceArgs.contains("-p")){
                            int i = replaceArgs.indexOf("-t");
                            tagsReplace = replaceArgs.get(i+1);
                        }else{
                            sender.sendMessage("Cannot search whole map with block flags");
                            return true;
                        }
                    }
                    if(args.contains("-s")){
                        if(sender instanceof Player){
                            clip = true;
                        }else{
                            sender.sendMessage("Cannot use selection with console!");
                            return true;
                        }
                    }
                    if(args.contains("-p")){
                        if(sender instanceof Player){
                            int i = args.indexOf("-p");
                        pack = args.get(i+1);
                        }else{
                            sender.sendMessage("Cannot use pack param with console!");
                            return true;
                        }
                    }
                    int count = 0;
                    if(clip){
                        Selection sel = worldEdit.getSelection((Player) sender);
                        for(int x = sel.getMinimumPoint().getBlockX(); x <= sel.getMaximumPoint().getBlockX(); x++){
                            for(int y = sel.getMinimumPoint().getBlockY(); y <= sel.getMaximumPoint().getBlockY(); y++){
                                for(int z = sel.getMinimumPoint().getBlockZ(); z <= sel.getMaximumPoint().getBlockZ(); z++){
                                    Block b = new Location(sel.getWorld(), x, y, z).getBlock();
                                    if(b.getType().equals(blockFind) && b.getData() == dataFind && (tagsFind != null ? checkFlags(tagsFind, b) : true)){
                                        b.setType(blockReplace);
                                        b.setData(dataReplace, false);
                                        setFlags(tagsReplace, b);
                                        count++;
                                    }
                                }
                            }
                        }
                    }else if(pack != null){
                        Region reg = null;
                        for(Region r : (Set<Region>) forceGet(rm, "regions")){
                            if(r.getName().equalsIgnoreCase(pack)){
                                reg=r;
                                break;
                            }
                        }
                        if(reg==null){
                            sender.sendMessage("Pack not found!");
                            return true;
                        }
                        int n = (int) forceGet(rm, "n");
                        int[] xpoints = (int[]) forceGet(rm, "xpoints");
                        int[] zpoints = (int[]) forceGet(rm, "zpoints");
                        Rectangle bounds = new Polygon(xpoints, zpoints, n).getBounds();
                        for(int x = bounds.x; x < bounds.x + bounds.width; x++){
                            for(int z = bounds.y; z < bounds.y + bounds.height; z++){
                                for(int y = 0; y < 255; y++){
                                    Block b = new Location(((Player) sender).getWorld(), x, y, z).getBlock();
                                    if(b.getType().equals(blockFind) && b.getData() == dataFind && (tagsFind != null ? checkFlags(tagsFind, b) : true)){
                                        b.setType(blockReplace);
                                        b.setData(dataReplace, false);
                                        setFlags(tagsReplace, b);
                                        count++;
                                    }
                                }
                            }
                        }
                    }else{
                        World w = ((Player) sender).getWorld();
                        for(int cx = minChunk.x; cx <= maxChunk.x; cx++){
                            for(int cy = minChunk.y; cy <= maxChunk.y; cy++){
                                ChunkSnapshot c = w.getChunkAt(cx, cy).getChunkSnapshot(true, false, false);
                                for(int x = 0; x <= 15; x++){
                                    for(int y = 0; y <= 127; y++){
                                        for(int z = 0; z <= 15; z++){
                                            if(c.getBlockTypeId(x, y, z) == blockFind.getId() && c.getBlockData(x, y, z) == dataFind){
                                                Block b = w.getBlockAt(cx*16+x, y, cy*16+z);
                                                b.setType(blockReplace);
                                                b.setData(dataReplace, false);
                                                count++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    sender.sendMessage(count + "results found");
                }
            }
            return false;
        }
        
        private boolean checkFlags(String flags, Block block){
            return false;
        }
        
        private void setFlags(String flags, Block block){
            
        }
        
        private Object forceGet(Object o, String name){
            try {
                Field f = o.getClass().getDeclaredField(name);
                f.setAccessible(true);
                return f.get(o);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ChunkAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
}
