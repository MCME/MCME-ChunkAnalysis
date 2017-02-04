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

import lombok.Getter;
import org.bukkit.block.Block;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class JobAction {
    
    @Getter
    private long processedBlocks=0;
    
    @Getter
    protected long foundBlocks=0;
    
    public JobAction(long processed, long found) {
        processedBlocks = processed;   
        foundBlocks = found;
    }
    
    public void execute(Block block) {
        processedBlocks++;
    }

    public abstract String statMessage();
    
    public abstract int[][] getBlockIds();
}
