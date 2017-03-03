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
package com.mcmiddleearth.chunkanalysis.job.action;

import com.mcmiddleearth.chunkanalysis.util.DBUtil;
import org.bukkit.block.Block;

/**
 *
 * @author Eriol_Eandur
 */
public class JobActionCount extends JobAction {
    
    private final int[][] searchData;
    private final int ID = 0;
    private final int DV = 1;
    private final int COUNT = 2;
    
    private boolean[] changedResults;
    
    public JobActionCount(int[][] search) {
        this(search,0,0);
    }
    
    public JobActionCount(int[][] search, long processed, long found) {
        super(processed, found);
        this.searchData = search;
        this.changedResults = new boolean[search.length];
        if(search.length>0 && search[0].length<=COUNT) {
            for(int i = 0; i<search.length;i++) {
                int[] data = searchData[i];
                searchData[i] = new int[]{data[ID],data[DV],0};
            }
        }
    }
    
    @Override
    public void execute(Block block) {
        super.execute(block);
        for (int i = 0; i < searchData.length; i++) {
            int[] blockData = searchData[i];
            if (block.getTypeId() == blockData[ID] && (blockData[DV] == -1 || block.getData() == blockData[DV])) {
                foundBlocks++;
                searchData[i][COUNT]++;
                changedResults[i]=true;
                break;
            }
        }
    }

    /*@Override
    public String statMessage() {
        String result = "Found "+foundBlocks+" of (";
        for(int i=0; i<searchData.length-1;i++) {
            result = result+searchData[i][ID]+":"+searchData[i][DV]+", ";
        }
        if(searchData.length>0) {
            result = result+searchData[searchData.length-1][ID]+":"+searchData[searchData.length-1][DV];
        }
        return result+")";
    }*/

    @Override
    public int[][] getBlockIds() {
        return searchData;
    }

    @Override
    public void saveResults(int jobId) {
        for(int i=0; i<searchData.length;i++) {
            if(changedResults[i]) {
                DBUtil.logJobResult(jobId, i, searchData[i][COUNT]);
                changedResults[i]=false;
            }
        }
    }
    
    @Override
    public String getName() {
        return "analyse";
    }
    
    @Override
    public String getDetails() {
        String result = "Counting blocks:\n";
        for (int[] searchData1 : searchData) {
            result = result + " - ["+searchData1[ID] + ":" + (searchData1[DV]==-1?"?":searchData1[DV]) + "] - "
                    +(searchData1[COUNT]>100000?">100000":searchData1[COUNT])+"\n";
        }
        return result;
    }

}
