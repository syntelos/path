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
 * {@link Path} is a list of path operations and their operands.  
 * 
 * @author jdp
 */
public interface Path
{

    public java.lang.Iterable<Operand> toPathIterable();

    public java.util.Iterator<Operand> toPathIterator();
    /**
     * Clear path and define winding.
     */
    public Path setWinding(Winding winding);

    public Winding getWinding();

    public boolean isWindingNonZero();

    public boolean isWindingEvenOdd();

    public Path setWindingNonZero();

    public Path setWindingEvenOdd();
    /**
     * Called by second {@link Operand} ctor, used by Fv3
     */
    public float[] getVerticesPath(int index, Op op, float[] vertices);

    public void add(Op op, float[] operands);

    public void moveTo(float[] operands);

    public void moveTo(float x, float y);

    public void lineTo(float[] operands);

    public void lineTo(float x, float y);

    public void quadTo(float[] operands);

    public void quadTo(float x1, float y1,
                       float x2, float y2);

    public void cubicTo(float[] operands);

    public void cubicTo(float x1, float y1,
                        float x2, float y2,
                        float x3, float y3);

    public void close();

    public Path apply(String pexpr);

    public Path apply(Parser p);
    /**
     * Clear existing path data - state
     */
    public void reset();
    /**
     * Reset and copy
     */
    public void set(Path path);
    /**
     * Append
     */
    public void add(Path path);
    /**
     * @return In format of attribute 'd' of SVG Element 'Path'.
     */
    public String toString();
}
