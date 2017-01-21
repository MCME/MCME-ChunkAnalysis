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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class JobScheduler extends BukkitRunnable {
    
    private int workInterval = 75;
    
    @Setter
    private int maxJobTaskTicks = 2;
    
    private final List<Job> pendingJobs;
    
    private long tickCounter;
    
    //Job currentJob;
    
    public JobScheduler(List<Job> pendingJobs) {
        super();
        this.pendingJobs = pendingJobs;
    }
    
    @Override
    public void run() {
        tickCounter = 0;
Logger.getGlobal().info("start ticker");
        BukkitTask ticker = new BukkitRunnable() {
            @Override
            public void run() {
                tickCounter++;
            }
        }.runTaskTimer(ChunkAnalysis.getInstance(), 0, 1);
        int missingTicks = 0;
        int taskSize = 10;
        long jobStartTick = 0;
        while(!(pendingJobs.isEmpty())){// && isJobFinished())) {
            if(tickCounter==jobStartTick) {
Logger.getGlobal().info("missed tick");
                missingTicks++;
                if(missingTicks>maxJobTaskTicks) {
Logger.getGlobal().info("cancel job");
                    cancelJobTask();
                }
            }
            if(isJobTaskFinished()){
                switch(missingTicks) {
                    case 0:
                        taskSize=(int)(taskSize*1.1)+1;
                        break;
                    case 1:
                        break;
                    case 2:
                        taskSize=(int)(taskSize*0.9);
                        break;
                    default:
                        taskSize=(int)(taskSize*0.5);
                }
Logger.getGlobal().info("start job task with size "+taskSize);
                startJobTask(taskSize);
                jobStartTick = tickCounter;
                missingTicks = 0;
            }
            try {
                Thread.sleep(workInterval);
            } catch (InterruptedException ex) {
Logger.getLogger(JobScheduler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ticker.cancel();
    }
    
    private boolean isJobTaskFinished() {
        return pendingJobs.get(0).isTaskFinished();
    }
    
    private boolean isJobFinished() {
        return pendingJobs.get(0).isFinished();
    }
    
    private void startJobTask(int taskSize) {
        if(pendingJobs.get(0).isFinished()) {
            pendingJobs.remove(0);
        }
        if(!pendingJobs.isEmpty()) {
            if(taskSize==0) {
                return;
            }
            pendingJobs.get(0).startTask(taskSize);
        }
    }
    
    private void cancelJobTask() {
        pendingJobs.get(0).stopTask();
    }
    
    public void setServerTps(int tps) {
        workInterval = 1000/tps;
        if(workInterval<50) {
            workInterval=50;
        }
    }
    
    
    
    
}
