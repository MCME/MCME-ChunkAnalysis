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
    
    private BukkitTask jobTask;
    
    protected boolean taskCancelled;

    protected JobAction action;
    
    public Job(JobAction action) {
        this.action = action;
    }
    public boolean isTaskFinished() {
        return jobTask == null || !(Bukkit.getScheduler().isCurrentlyRunning(jobTask.getTaskId())
                   || Bukkit.getScheduler().isQueued(jobTask.getTaskId()));
    }
    
    public abstract boolean isFinished();
    
    public void startTask(int taskSize) {
        if(isTaskFinished()) {
            taskCancelled=false;
            jobTask = createJobTask(taskSize).runTask(ChunkAnalysis.getInstance());
        }
    }
    
    protected abstract BukkitRunnable createJobTask(int askSize);
    
    public void stopTask() {
        taskCancelled = true;
    }
    
    protected void saveProgress(Vector last) {
        
    }
    

}
