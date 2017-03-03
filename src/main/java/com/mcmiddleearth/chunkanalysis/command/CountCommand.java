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

import com.mcmiddleearth.chunkanalysis.job.action.JobAction;
import com.mcmiddleearth.chunkanalysis.job.action.JobActionCount;
import com.mcmiddleearth.pluginutil.NumericUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eriol_Eandur
 */
public class CountCommand extends AbstractStartCommand {

    public CountCommand(String... permissionNodes) {
        super(1, false, permissionNodes);
        setShortDescription(": Starts a chunkAnalysis job.");
        setUsageDescription(": Starts a chunkAnalysis job.");
    }
    
    @Override
    protected JobAction getAction(List<String> args) {
        List<int[]> searchDataList = new ArrayList<>();
        for(String arg: args) {
            if(NumericUtil.isInt(arg) || arg.contains(":")) {
                int[] searchData = new int[2];
                searchData[0] = getId(arg);
                searchData[1] = getDv(arg);
                searchDataList.add(searchData);
            }
        }
        return new JobActionCount(searchDataList.toArray(new int[0][0]));
    }
    
    
}
