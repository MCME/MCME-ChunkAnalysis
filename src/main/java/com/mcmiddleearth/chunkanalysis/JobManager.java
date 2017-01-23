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

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class JobManager {
    
    private static BukkitTask schedulerTask;
    
    @Getter
    private static JobScheduler jobScheduler;
    
    private static final List<Job> pendingJobs = new ArrayList<>();
    
    public static void addJob(Job newJob) {
        pendingJobs.add(newJob);
        if(!isSchedulerTaskRunning()) {
            DevUtil.log("start scheduler with tps "+15);
            jobScheduler = new JobScheduler(pendingJobs);
            jobScheduler.setServerTps(15);
            schedulerTask=jobScheduler.runTaskAsynchronously(ChunkAnalysis.getInstance());
        }
    }
   
    public static void cancelJob(Job cancelJob) {
        pendingJobs.remove(cancelJob);
    }
    
    private static boolean isSchedulerTaskRunning() {
        return schedulerTask!=null 
               && Bukkit.getScheduler().isCurrentlyRunning(schedulerTask.getTaskId());
    }
    
}
