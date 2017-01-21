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

import lombok.Getter;
import org.bukkit.block.Block;

/**
 *
 * @author Eriol_Eandur
 */
public class JobActionCount extends JobAction {
    
    @Getter
    private int foundBlocks=0;
    
    private final int material;
    
    private final byte dataValue;
    
    public JobActionCount(int id, int dataValue) {
        material = id;
        this.dataValue = (byte) dataValue;
    }
    @Override
    public void execute(Block block) {
        super.execute(block);
        if(block.getTypeId()==material 
                && (dataValue == -1 || block.getData()==dataValue) ) {
            foundBlocks++;
        }
    }
    
    
}
