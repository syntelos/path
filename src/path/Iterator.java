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
 * Iterator of {@link Operand Operands} used by some {@link Path} implementors.
 * 
 * @see Operand
 * @see Path
 */
public class Iterator
    extends Object
    implements java.lang.Iterable<Operand>,
               java.util.Iterator<Operand>
{
    private final Path path;
    private final Op[] operators;
    private final float[] vertices;

    private int index, length;


    public Iterator(Path path, Op[] operators, float[] vertices){
        super();
        if (null != path){
            this.path = path;
            if (null == operators || 0 == operators.length || null == vertices || 0 == vertices.length){
                this.operators = null;
                this.vertices = null;
                this.length = 0;
            }
            else {
                this.operators = operators;
                this.vertices = vertices;
                this.length = operators.length;
            }
        }
        else
            throw new IllegalArgumentException();
    }


    public boolean hasNext(){
        return (this.index < this.length);
    }
    public Operand next(){
        if (this.index < this.length)

            return new Operand(this.path,this.index++,this.operators,this.vertices);
        else
            throw new java.util.NoSuchElementException(String.valueOf(this.index));
    }
    public void remove(){
        throw new java.lang.UnsupportedOperationException();
    }
    public java.util.Iterator<Operand> iterator(){
        return this;
    }
}
