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
 * {@link Path} string tool
 */
public class Formatter {

    public final static String ToString(Path p){

        StringBuilder string = new StringBuilder();

        for (Operand operand: p.toPathIterable()){

            operand.format(string);
        }
        return string.toString();
    }
}
