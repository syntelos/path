/*
 * Path data processor abstraction (http://github.com/syntelos/path)
 * Copyright (C) 2012, John Pritchard
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package path;

/**
 * {@link Path} operators have a fixed number of {@link Operand
 * operands}.
 */
public enum Op {
    MoveTo(1), LineTo(1), QuadTo(2), CubicTo(3), Close(1);


    public final int operands;


    private Op(int operands){
        this.operands = operands;
    }


    public String format(float[] operands){
        if (null == operands || 1 > operands.length)
            return "";
        else {
            switch(operands.length){
            case 1:
                return String.format("%4.4f",operands[0]);
            case 2:
            case 3:
                return String.format("%4.4f,%4.4f",operands[0],operands[1]);

            case 4:
                return String.format("%4.4f,%4.4f,%4.4f,%4.4f",operands[0],operands[1],operands[2],operands[3]);
            case 6:
                if (QuadTo == this)
                    return String.format("%4.4f,%4.4f,%4.4f,%4.4f",operands[0],operands[1],operands[3],operands[4]);
                else
                    return String.format("%4.4f,%4.4f,%4.4f,%4.4f",operands[0],operands[1],operands[2],operands[3],operands[4],operands[5]);
            case 8:
                return String.format("%4.4f,%4.4f,%4.4f,%4.4f",operands[0],operands[1],operands[3],operands[4],operands[6],operands[7]);
            default:
                throw new IllegalArgumentException(String.valueOf(operands.length));
            }
        }
    }

    public final static Op[] Add(Op[] list, Op item){
        if (null == item)
            return list;
        else if (null == list)
            return new Op[]{item};
        else {
            int len = list.length;
            Op[] copier = new Op[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = item;
            return copier;
        }
    }

}
