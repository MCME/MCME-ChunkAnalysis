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

import org.bukkit.block.Block;

/**
 *
 * @author Eriol_Eandur
 */
public class JobActionCount extends JobAction {
    
    private final int[][] search;
        
    public JobActionCount(int[][] search, long processed, long found) {
        super(processed, found);
        foundBlocks = found;
        this.search = search;
    }
    
    @Override
    public void execute(Block block) {
        super.execute(block);
        for (int[] blockData : search) {
            if (block.getTypeId() == blockData[0] && (blockData[1] == -1 || block.getData() == blockData[1])) {
                foundBlocks++;
                break;
            }
        }
    }

    @Override
    public String statMessage() {
        String result = "Found "+foundBlocks+" of (";
        for(int i=0; i<search.length-1;i++) {
            result = result+search[i][0]+":"+search[i][1]+", ";
        }
        if(search.length>0) {
            result = result+search[search.length-1][0]+":"+search[search.length-1][1];
        }
        return result+")";
    }

    @Override
    public int[][] getBlockIds() {
        return search;
    }
}
