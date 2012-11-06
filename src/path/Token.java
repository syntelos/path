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
 * 
 * @author jdp
 */
public enum Token {
    Unknown, Coordinate, M, m, Z, z, L, l, H, h, V, v, C, c, S, s, Q, q, T, t, A, a;


    public Op opfor(){
        switch(this){
        case M:
        case m:
            return Op.MoveTo;
        case Z:
        case z:
            return Op.Close;
        case L:
        case l:
            return Op.LineTo;
        case H:
        case h:
        case V:
        case v:
            return Op.MoveTo;
        case C:
        case c:
            return Op.CubicTo;
        case Q:
        case q:
            return Op.QuadTo;
        default:
            throw new IllegalStateException(this.name());
        }
    }
    public String format(float[] operands){

        return (this.name()+this.opfor().format(operands));
    }
}
