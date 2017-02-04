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
package com.mcmiddleearth.chunkanalysis.job;

import com.mcmiddleearth.chunkanalysis.ChunkAnalysis;
import com.mcmiddleearth.chunkanalysis.MessageManager;
import com.mcmiddleearth.chunkanalysis.util.DevUtil;
import com.mcmiddleearth.chunkanalysis.job.action.JobAction;
import com.mcmiddleearth.chunkanalysis.util.DBUtil;
import de.schlichtherle.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class Job {
    
    @Getter
    private int id;
    
    @Getter
    private UUID owner;
    
    private File statsFile;
    
    @Getter
    protected int jobSize; //number of chunks
    
    @Getter
    protected int chunksDone;
    
    @Getter
    protected Vector currentChunk;
    
    @Getter
    protected Vector currentBlock = new Vector(0,0,0);
    
    private BukkitTask jobTask;
    
    @Setter
    private float taskSize;
    
    @Getter
    private long startTime=-1;
    
    private long finishTime = -1;
    
    protected boolean taskCancelled;

    @Getter
    protected JobAction action;
    
    @Getter
    protected World world;
    
    private final Job self;
    
    public Job(UUID owner, World world, JobAction action, int id, long startTime, int chunksDone) {
        this.chunksDone = chunksDone;
        this.startTime = startTime;
        this.owner = owner;
        this.world = world;
        this.action = action;
        this.id = id;
        //if(Bukkit.getOfflinePlayer(owner).)
        //MessageManager.addListeningPlayer();
        self = this;
    }
    
    public boolean isTaskPending() {
        return jobTask != null && (Bukkit.getScheduler().isCurrentlyRunning(jobTask.getTaskId())
                   || Bukkit.getScheduler().isQueued(jobTask.getTaskId()));
    }
    
    public abstract boolean isFinished();
    
    public void startTask() {
        if(!isTaskPending()) {
            taskCancelled=false;
            if(startTime==-1) {
                startTime=System.currentTimeMillis()/1000;
                MessageManager.sendJobStarted(this);
                DBUtil.logStartTime(this);
            }
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
                        finishTime = System.currentTimeMillis()/1000;
                        cancel();
                        MessageManager.sendJobFinished(self);
                        clearJobData();
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
    
    public void clearJobData() {
        DBUtil.deleteJob(this);
    }

    public long getDuration() {
        return System.currentTimeMillis()/1000-startTime;
    }
    
    protected abstract void executeTask(int size);
    
    public abstract Vector[] getCoords();

    protected abstract String detailsMessage();
    
    public String statMessage() {
        if(isFinished()) {
            return "Finished in "+(finishTime-startTime)+"seconds. "+action.statMessage();
        } else if(startTime==-1){
            return "Not yet started.";
        } else {
            return Math.min(100,(getChunksDone()*100.0/getJobSize()))+"% done. Working at "
                    + (currentChunk.getBlockX()*16)+" "+(currentChunk.getBlockZ()*16)+". "+action.statMessage();
        }
    }
    
    protected static int getFreeJobId() {
        int[] usedIds = DBUtil.getJobIds();
        int free = 0;
        boolean found = false;
        while(!found) {
            found = true;
            for(int i=0; i<usedIds.length;i++) {
                if(usedIds[i]==free) {
                    found=false;
                    free++;
                    break;
                }
            }
        }
        return free;
    }
    
    protected static JobAction createAction(String type, int[][] blockData, long processed, long found) {
        Class actionClass;
        try {
            actionClass = Class.forName(type);
            Constructor constructor = actionClass.getConstructor(new Class[]{blockData.getClass(),
                                                                 long.class, long.class});
            return (JobAction) constructor.newInstance((Object) blockData, processed, found);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
