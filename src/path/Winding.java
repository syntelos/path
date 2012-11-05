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
 * Winding algorithms make more sense for a ray from centroid
 * point to subject point.
 * 
 * <h3>Non zero winding</h3>
 * 
 * <p> From P, a ray R intersects the outline having clock-wise
 * (CW) or counter-clock-wise (CCW) direction.  </p>
 * 
 * <p> An accumulator A is initialized to zero, and incremented
 * for a CCW intersection, and decremented for a CW
 * intersection.</p>
 * 
 * <p> For A equal to zero P is "outside" the outline, otherwise P
 * is "inside" the outline. </p>
 * 
 * <h3>Even odd winding</h3>
 * 
 * <p> From P, a ray R intersects the outline an even or odd
 * number of times.  If even, P is "outside" the outline.
 * Otherwise when P is odd, P is "inside" the outline.  </p>
 * 
 * <h3>Future</h3>
 * 
 * <p> The Winding enum constant {@link Path$Winding#Future
 * Future} represents and unknown, wait and see status. </p>
 */
public enum Winding {
    EvenOdd, NonZero, Future;


    public final static Winding For(int rule){
        switch(rule){
        case 0:
            return EvenOdd;
        case 1:
            return NonZero;
        default:
            return null;
        }
    }

    /**
     * Missing a required winding 
     */
    public static class Missing
    extends IllegalStateException
    {

        public Missing(){
            super("Require winding");
        }
    }
}
