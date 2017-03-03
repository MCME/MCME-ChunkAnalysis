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
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class CancelCommand extends AbstractCommand {

    public CancelCommand(String... permissionNodes) {
        super(0, false, permissionNodes);
        setShortDescription(": Cancels a job.");
        setUsageDescription(" [#jobID]: Cancels the job with ID [#jobID]. Without optional argument [#jobID] the currently executed job is cancelled.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!JobManager.isSchedulerTaskRunning()) {
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "No jobs scheduled.");
        } else {
            int jobId = -1;
            if(args.length>0) {
                if(!NumericUtil.isInt(args[0])) {
                    sendNotANumberErrorMessage(cs);
                    return;
                }
                jobId = NumericUtil.getInt(args[0]);
            }
            if(jobId==-1 || JobManager.isCurrentJob(jobId)) {
                JobManager.getJobScheduler().setCancel(true);
                ChunkAnalysis.getMessageUtil().sendInfoMessage(cs, "Request to cancel current Job was sent.");
                return;
            }
            if(JobManager.hasJob(jobId)) {
                JobManager.removeJob(jobId);
                ChunkAnalysis.getMessageUtil().sendInfoMessage(cs, "Job removed from queue.");
                return;
            }
            ChunkAnalysis.getMessageUtil().sendErrorMessage(cs, "No job with ID "+jobId+" found.");
        }
    }
    
    
}
