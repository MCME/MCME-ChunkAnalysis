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
import java.util.logging.Logger;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class JobManager {
    
    private static BukkitTask scheduler;
    
    private static final List<Job> pendingJobs = new ArrayList<>();
    
    @Setter
    private static int serverTps = 15;
    
    public static void addJob(Job newJob) {
        pendingJobs.add(newJob);
        if(!isSchedulerTaskRunning()) {
Logger.getGlobal().info("start scheduler with tps "+serverTps);
            JobScheduler jobScheduler = new JobScheduler(pendingJobs);
            jobScheduler.setServerTps(serverTps);
            scheduler=jobScheduler.runTaskAsynchronously(ChunkAnalysis.getInstance());
        }
    }
   
    public static void cancelJob(Job cancelJob) {
        pendingJobs.remove(cancelJob);
    }
    
    private static boolean isSchedulerTaskRunning() {
        return scheduler!=null 
               && Bukkit.getScheduler().isCurrentlyRunning(scheduler.getTaskId());
    }
    
}
