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

import com.mcmiddleearth.resourceregions.DevUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 *
 * @author Eriol_Eandur
 */
public class JobActionReplace extends JobAction {
    
    private final int[][] searchBlocks;
    
    private final int[][] replaceBlocks;
    
    public JobActionReplace(int[][] searchBlocks, int[][] replaceBlocks, long processed, long found) {
        super(processed, found);
        this.searchBlocks=searchBlocks;
        this.replaceBlocks=replaceBlocks;
    }
    
    public JobActionReplace(int[][] blockData, long processed, long found) {
        super(processed, found);
        searchBlocks = new int[blockData.length/2][];
        replaceBlocks = new int[blockData.length/2][];
        System.arraycopy(blockData, 0, searchBlocks, 0, blockData.length/2);
        System.arraycopy(blockData, blockData.length/2, replaceBlocks, 0, blockData.length/2);
        DevUtil.log("Search for "+ searchBlocks.length+" blocks ");
    }
    
    @Override
    public void execute(Block block) {
        super.execute(block);
        for(int i=0;i< searchBlocks.length;i++) {
            int[] blockData = searchBlocks[i];
            if(block.getTypeId()==blockData[0] 
                    && (blockData[1] == -1 || block.getData()== blockData[1]) ) {
                int[] replaceData = replaceBlocks[i];
                BlockState state = block.getState();
                state.setTypeId(replaceData[0]);
                if(replaceData[1]>=0) {
                    state.setRawData((byte) replaceData[1]);
                }
                state.update(true, false);
                foundBlocks++;
            }
        }
    }
    
    @Override
    public String statMessage() {
        DevUtil.log("Search for "+ searchBlocks.length+" blocks "+searchBlocks[0][0]);
        DevUtil.log("Replace with for "+ replaceBlocks.length+" blocks "+replaceBlocks[0][0]);
        String result = "Replaced "+foundBlocks+" of ID ";
        for(int i=0; i<searchBlocks.length-1;i++) {
            result = result+searchBlocks[i][0]+":"+searchBlocks[i][1]+", ";
        }
        if(searchBlocks.length>0) {
            result = result+searchBlocks[searchBlocks.length-1][0]+":"+searchBlocks[searchBlocks.length-1][1];
        }
        return result;
    }
    
    @Override
    public int[][] getBlockIds() {
        int[][] result = new int[searchBlocks.length*2][];
        System.arraycopy(searchBlocks, 0, result, 0, searchBlocks.length);
        System.arraycopy(replaceBlocks, 0, result, searchBlocks.length, searchBlocks.length);
        return result;
    }
}
