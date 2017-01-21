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

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import java.util.logging.Logger;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class CuboidJob extends Job {
    
    private final World world;
    
    private Vector minCorner;
    private Vector maxCorner;
    
    private Vector current;
    
    private boolean finished=false;
    
    public CuboidJob(CuboidSelection region, JobAction action) {
        super(action);
        world = region.getWorld();
Logger.getGlobal().info("create job task in world "+world);
        //x and y are chunk coordinates; y is block coordinate
        minCorner = new Vector(world.getChunkAt(region.getMinimumPoint()).getX(), 
                               region.getMinimumPoint().getBlockY(),
                               world.getChunkAt(region.getMinimumPoint()).getZ());
Logger.getGlobal().info("create job task with min corner "+minCorner);
        maxCorner = new Vector(world.getChunkAt(region.getMaximumPoint()).getX(), 
                               region.getMaximumPoint().getBlockY(),
                               world.getChunkAt(region.getMaximumPoint()).getZ());
Logger.getGlobal().info("create job task with max corner "+maxCorner);
        current = minCorner.clone();
    }
    
    @Override
    public boolean isFinished() {
        return finished;
    }
    
    @Override
    protected BukkitRunnable createJobTask(final int taskSize) {
        return new BukkitRunnable() {
            @Override
            public void run() {
Logger.getGlobal().info("start job task current "+current.getBlockX()+" "+current.getBlockZ());
                Vector lastFinished=null;
                int startX = current.getBlockX();
                int endX = current.getBlockX()+(current.getBlockZ()
                                               -minCorner.getBlockZ()
                                               +taskSize)/chunksPerRow();
                int stopEarly=0;
                if(endX>maxCorner.getBlockX()) {
                    endX=maxCorner.getBlockX()+1;
                    stopEarly=1;
                }
                int startZ = current.getBlockZ();
                for(int x = startX; x<=endX-stopEarly; x++) {
                    int endZ = (x==endX?minCorner.getBlockZ()
                                        +(current.getBlockZ()
                                          -minCorner.getBlockZ()
                                          +taskSize)%chunksPerRow()
                                        :maxCorner.getBlockZ());
                    for(int z = startZ; z<=endZ; z++) {
                        if(taskCancelled) {
                            saveProgress(lastFinished);
                            if(lastFinished!=null) {
                                current = nextCurrent(lastFinished);
                            }
                            return;
                        }
Logger.getGlobal().info("handle chunk "+x+" "+z);
                        handleChunk(x,z);
                        lastFinished = new Vector(x,0,z);
                    }
                    startZ = minCorner.getBlockZ();
                }
                saveProgress(lastFinished);
                if(lastFinished.getBlockX()==maxCorner.getBlockX() 
                        && lastFinished.getBlockZ() == maxCorner.getBlockZ()) {
                    finished = true;
Logger.getGlobal().info("job finished");
                }
                current = nextCurrent(lastFinished);
Logger.getGlobal().info("end job task next current "+current.getBlockX()+" "+current.getBlockZ());
            }
        };
    }
    
    private void handleChunk(int chunkX, int chunkZ) {
        Chunk chunk = world.getChunkAt(chunkX, chunkZ);
        for(int x = 0; x<16;x++) {
            for(int z=0; z<16;z++) {
                for(int y=minCorner.getBlockY();y<=maxCorner.getBlockY();y++) {
                    action.execute(chunk.getBlock(x, y, z));
                }
            }
        }
    }
    
    private int chunksPerRow() {
        return maxCorner.getBlockZ()-minCorner.getBlockZ()+1;
    }
    
    private Vector nextCurrent(Vector lastFinished) {
        if(lastFinished.getBlockZ()==maxCorner.getBlockZ()) {
            return new Vector(lastFinished.getBlockX()+1,0,minCorner.getBlockZ());
        } else {
            return new Vector(lastFinished.getBlockX(),0, lastFinished.getBlockZ()+1);
        }
    }
    
}