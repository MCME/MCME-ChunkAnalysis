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
public class ResumeCommand extends AbstractCommand {

    public ResumeCommand(String... permissionNodes) {
        super(1, false, permissionNodes);
        setShortDescription(": Resumes a peviously suspended job.");
        setUsageDescription(": Resumes a peviously suspended job.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!JobManager.isSchedulerTaskRunning()) {
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "No jobs scheduled.");
        } else if(!JobManager.getJobScheduler().isSuspended()){
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "Job execution is not suspended.");
        } else {
            JobManager.getJobScheduler().setSuspended(false);
            ChunkAnalysis.getMessageUtil().sendInfoMessage(cs, "Job execution resumed.");
        }
    }
    
    
}
