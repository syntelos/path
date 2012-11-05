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
 * An operator and its operands
 * 
 * @see Iterator
 * @see Path
 */
public class Operand
    extends Object
{
    public final static float[] EmptySet = {};


    public final Op op;

    public final float[] vertices;


    /**
     * @param op Operator
     * @param vertices User copy of operands to operator.  This ctor
     * does not clone the argument, but passes the argument (by
     * reference) to its users.
     */
    public Operand( Op op, float[] vertices){
        super();
        if (null != op){
            this.op = op;
            if (null == vertices)
                this.vertices = EmptySet;
            else
                this.vertices = vertices;
        }
        else
            throw new IllegalArgumentException();
    }
    /**
     * Calls {@link Path#getVerticesPath Path getVerticesPath}
     * 
     * @param path Using {@link Path#getVerticesPath Path
     * getVerticesPath}
     * 
     * @param index Index into operators path list for current op
     * 
     * @param operators Non-empty path list
     * 
     * @param vertices For input to {@link Path#getVerticesPath
     * getVerticesPath}.
     */
    public Operand(Path path, int index, Op[] operators, float[] vertices){
        super();
        if (null != path && -1 < index && null != operators && 0 < operators.length){

            this.op = operators[index];

            this.vertices = path.getVerticesPath(index,this.op,vertices);
        }
        else
            throw new IllegalArgumentException();
    }


    public StringBuilder format(StringBuilder string){
        switch(this.op){
        case MoveTo:
            string.append('M');
            break;
        case LineTo:
            string.append('L');
            break;
        case QuadTo:
            string.append('Q');
            break;
        case CubicTo:
            string.append('C');
            break;
        case Close:
            string.append('Z');
            break;
        default:
            throw new IllegalStateException(this.op.name());
        }

        return Format(this.vertices,string);
    }
    public String toString(){

        return this.format(new StringBuilder()).toString();
    }

    public final static StringBuilder Format(float[] vertices, StringBuilder string){
        boolean once = true;
        for (float value : vertices){
            if (once)
                once = false;
            else
                string.append(',');

            string.append(String.format("%f",value));
        }
        return string;
    }
}
