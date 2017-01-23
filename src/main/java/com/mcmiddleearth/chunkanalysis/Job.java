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

import de.schlichtherle.io.File;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class Job {
    
    private UUID owner;
    
    private File statsFile;
    
    @Getter
    protected int jobSize;
    
    @Getter
    protected int chunksDone;
    
    @Getter
    protected Vector current;
    
    private BukkitTask jobTask;
    
    @Setter
    private float taskSize;
    
    protected boolean taskCancelled;

    @Getter
    protected JobAction action;
    
    public Job(JobAction action) {
        this.action = action;
    }
    
    public boolean isTaskPending() {
        return jobTask != null && (Bukkit.getScheduler().isCurrentlyRunning(jobTask.getTaskId())
                   || Bukkit.getScheduler().isQueued(jobTask.getTaskId()));
    }
    
    public abstract boolean isFinished();
    
    public void startTask() {
        if(!isTaskPending()) {
            taskCancelled=false;
            //jobTask = createJobTask(taskSize).runTask(ChunkAnalysis.getInstance());
            jobTask = new BukkitRunnable() {
                @Override
                public void run() {
                    DevUtil.log(6,"Startin job with size: "+taskSize);
                    int taskSizeInt = Math.round(taskSize);
                    if(taskSizeInt>0) {
                        executeTask(taskSizeInt);
                    }
                    if(isFinished()) {
                        cancel();
                    }
                }
            }.runTaskTimer(ChunkAnalysis.getInstance(), 0, 1);
        }
    }
    
    //protected abstract BukkitRunnable createJobTask(int askSize);
    
    public void stopTask() {
        if(!taskCancelled) {
            taskCancelled = true;
            jobTask.cancel();
        }
    }
    
    protected void saveProgress(Vector last) {
        
    }
    
    protected abstract void executeTask(int size);

}
