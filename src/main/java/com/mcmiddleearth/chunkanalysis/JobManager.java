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

import com.mcmiddleearth.chunkanalysis.util.DevUtil;
import com.mcmiddleearth.chunkanalysis.job.Job;
import com.mcmiddleearth.chunkanalysis.util.DBUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
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
    
    private static List<Job> pendingJobs = new ArrayList<>();
    
    public static void init() {
        pendingJobs = DBUtil.loadJobs();
        if(pendingJobs.size()>0) {
            startScheduler();
        }
    }
    public static void addJob(Job newJob) {
        pendingJobs.add(newJob);
        startScheduler();
    }
    
    private static void startScheduler() {
        if(!isSchedulerTaskRunning()) {
            DevUtil.log("start scheduler with tps "+15);
            boolean dbConnected = DBUtil.checkConnection();
            DevUtil.log("Database connected: "+dbConnected);
            jobScheduler = new JobScheduler(pendingJobs);
            jobScheduler.setServerTps(15);
            schedulerTask=jobScheduler.runTaskAsynchronously(ChunkAnalysis.getInstance());
        }
    }
   
    public static void cancelJob(Job cancelJob) {
        pendingJobs.remove(cancelJob);
    }
    
    public static boolean isSchedulerTaskRunning() {
        return schedulerTask!=null 
               && (Bukkit.getScheduler().isCurrentlyRunning(schedulerTask.getTaskId())
                   || Bukkit.getScheduler().isQueued(schedulerTask.getTaskId()));
    }
    
}
