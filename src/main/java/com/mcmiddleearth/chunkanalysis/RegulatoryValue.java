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

/**
 *
 * @author Eriol_Eandur
 */
public final class RegulatoryValue {
        
    private final float[] values;

    @Getter
    private float desired;

    @Getter
    private float average;
    
    @Getter
    private float difference;

    @Getter
    private float velocity;

    @Getter
    private float integral;

    private int counter = 0;

    public RegulatoryValue(float desired, float start, int length) {
        this.desired = desired;
        values = new float[length];
        reset(start);
    }
    
    public void add(float tps) {
        values[counter] = tps;
        calculate();
        counter++;
        if(counter == values.length) {
            counter = 0;
        }
    }

    public void setDesired(float desired) {
        this.desired = desired;
        integral = 0;
        calculate();
        velocity = 0;
    }
    
    public void reset(float startValue) {
        for(int i=0; i< values.length;i++) {
            values[i]=startValue;
        }
        integral = 0;
        calculate();
        velocity = 0;
    }

    private void calculate() {
        velocity = difference;
        average = 0;
        for(int i=0; i<values.length;i++) {
            average += values[i];
        }
        average = average/values.length;
        difference = average-desired;
        integral+=difference;
        velocity = difference - velocity;
    }
}
