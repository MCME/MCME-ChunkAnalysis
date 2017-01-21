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

import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 *
 * @author Eriol_Eandur
 */
public class JobActionReplace extends JobAction {
    
    @Getter
    private int replacedBlocks=0;
    
    private final int findMaterial;
    
    private final byte findDataValue;

    private final int replaceMaterial;
    
    private final byte replaceDataValue;
    
    public JobActionReplace(int findId, int findDv, int replaceId, int replaceDv) {
        findMaterial = findId;
        findDataValue = (byte) findDv;
        replaceMaterial = replaceId;
        replaceDataValue = (byte) replaceDv;
    }
    
    @Override
    public void execute(Block block) {
        super.execute(block);
        if(block.getTypeId()==findMaterial 
                && (findDataValue == -1 || block.getData()== findDataValue) ) {
            BlockState state = block.getState();
            state.setTypeId(replaceMaterial);
            if(replaceDataValue>=0) {
                state.setRawData(replaceDataValue);
            }
            state.update(true, false);
            replacedBlocks++;
        }
    }
}
