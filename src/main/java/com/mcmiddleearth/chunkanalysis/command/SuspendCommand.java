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
package com.mcmiddleearth.chunkanalysis.command;

import com.mcmiddleearth.chunkanalysis.ChunkAnalysis;
import com.mcmiddleearth.chunkanalysis.JobManager;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class SuspendCommand extends AbstractCommand {

    public SuspendCommand(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Suspends the current job.");
        setUsageDescription(": Suspends the current job. use /block resume to continue the job.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!JobManager.isSchedulerTaskRunning()) {
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "No jobs scheduled.");
        } else if(JobManager.getJobScheduler().isSuspended()){
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "Job already suspended.");
        } else {
            JobManager.getJobScheduler().setSuspended(true);
            ChunkAnalysis.getMessageUtil().sendInfoMessage(cs, "Job execution halted.");
        }
    }
    
    
}
