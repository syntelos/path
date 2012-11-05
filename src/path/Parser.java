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
 * Parse SVG Path "d" attribute value expression.
 */
public class Parser
    extends Object
    implements Iterable<Token>,
               java.util.Iterator<Token>
{


    private final char[] string;
    private int index;
    private java.lang.Float coordinate;


    public Parser(String string){
        super();
        if (null != string){
            this.string = string.trim().toCharArray();
            if (0 == this.string.length)
                throw new IllegalArgumentException();
        }
        else
            throw new IllegalArgumentException();
    }


    public java.lang.Float getCoordinate(){
        java.lang.Float c = this.coordinate;
        if (null != this.coordinate){
            this.coordinate = null;
            return c;
        }
        else if (this.hasNext() && Token.Coordinate == this.next()){
            c = this.coordinate;
            this.coordinate = null;
            return c;
        }
        else
            throw new java.util.NoSuchElementException();
    }
    public boolean hasNext(){
        return (this.index < this.string.length);
    }
    public Token next(){
        this.coordinate = null;
        if (this.index < this.string.length){
            Token token = null;
            int start = this.index;
            int end = start;
            boolean decpt = false;
            scan:
            while (this.index < this.string.length){

                switch(this.string[this.index]){
                case ' ':
                case ',':
                    if (this.index != start){
                        end = (this.index-1);
                        this.index++;
                        break scan;
                    }
                    break;
                case '.':
                    if (null != token){
                        if (decpt || Token.Coordinate != token){
                            end = (this.index-1);
                            break scan;
                        }
                    }
                    else
                        token = Token.Coordinate;

                    decpt = true;
                    break;
                case '-':
                    if (null != token){
                        end = (this.index-1);
                        break scan;
                    }
                    else
                        token = Token.Coordinate;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (null == token)
                        token = Token.Coordinate;
                    else if (token != Token.Coordinate){
                        end = (this.index-1);
                        break scan;
                    }
                    break;
                default:
                    if (null == token)
                        return Token.valueOf(String.valueOf(this.string[this.index++]));
                    else {
                        end = (this.index-1);
                        break scan;
                    }
                }
                this.index++;
            }

            if (Token.Coordinate == token){

                if (start == end && this.index == this.string.length)
                    end = (this.index-1);

                this.coordinate = java.lang.Float.parseFloat(new String(this.string,start,(end-start+1)));
                return token;
            }
            else
                return Token.Unknown;
        }
        else
            throw new java.util.NoSuchElementException();
    }
    public void remove(){
        throw new UnsupportedOperationException();
    }
    public java.util.Iterator<Token> iterator(){
        return this;
    }

    public final static Path Apply(Path path, Parser p){
        Token last = null;

        float mx = 0, my = 0, sx = 0, sy = 0;

        for (Token tok : p){
            switch(tok){
            case Coordinate:
            case M:
                path.moveTo((mx = p.getCoordinate()),(my = p.getCoordinate()));
                sx = mx;
                sy = my;
                break;
            case m:
                path.moveTo((mx += p.getCoordinate()),(my += p.getCoordinate()));
                sx = mx;
                sy = my;
                break;
            case Z:
            case z:
                path.close();
                break;
            case L:
                path.lineTo((sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case l:
                path.lineTo((sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case H:
                sx = p.getCoordinate();
                path.lineTo(sx,sy);
                break;
            case h:
                sx += p.getCoordinate();
                path.lineTo(sx,sy);
                break;
            case V:
                sy = p.getCoordinate();
                path.lineTo(sx,sy);
                break;
            case v:
                sy += p.getCoordinate();
                path.lineTo(sx,sy);
                break;
            case C:
                path.curveTo(p.getCoordinate(),p.getCoordinate(),
                             p.getCoordinate(),p.getCoordinate(),
                             (sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case c:
                path.curveTo((sx + p.getCoordinate()),(sy + p.getCoordinate()),
                             (sx + p.getCoordinate()),(sy + p.getCoordinate()),
                             (sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case S:
            case s:
                throw new UnsupportedOperationException(tok.name());
            case Q:
                path.quadTo(p.getCoordinate(),p.getCoordinate(),
                            (sx = p.getCoordinate()),(sy = p.getCoordinate()));
                break;
            case q:
                path.quadTo((sx + p.getCoordinate()),(sy + p.getCoordinate()),
                            (sx += p.getCoordinate()),(sy += p.getCoordinate()));
                break;
            case T:
            case t:
                throw new UnsupportedOperationException(tok.name());
            case A:
            case a:
                throw new UnsupportedOperationException(tok.name());
            default:
                throw new IllegalArgumentException(tok.name());
            }
            last = tok;
        }
        return path;
    }
}
