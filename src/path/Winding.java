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
    EvenOdd("even-odd"), NonZero("non-zero"), Future("future");


    public final String label;


    private Winding(String label){
        this.label = label;
    }


    public String toString(){
        return this.label;
    }


    /**
     * SVG's default winding rule for path "fill-rule" and "clip-path"
     * is "non-zero".
     * 
     * @see http://www.w3.org/TR/SVG/painting.html#FillProperties
     * @see http://www.w3.org/TR/SVG/masking.html#ClipPathElement
     */
    public final static Winding Default = NonZero;


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
    private final static java.util.Map<String,Winding> Map = new java.util.HashMap<String,Winding>();
    static {
        for (Winding w: Winding.values()){

            Map.put(w.name().toUpperCase(),w);

            Map.put(w.label.toUpperCase(),w);
        }
    }
    /**
     * Case insensitive "evenodd", "even-odd", "nonzero" and
     * "non-zero".
     */
    public final static Winding For(String name){

        return Map.get(name.toUpperCase());
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
