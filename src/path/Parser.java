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
 * Parse SVG Path "d" attribute value expressions for two and three
 * dimensional operands.
 */
public class Parser
    extends Object
    implements Iterable<Token>,
               java.util.Iterator<Token>
{
    public final static float[] EmptySet = {};



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
        if (null != c){
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
    public boolean hasCoordinate(){
        java.lang.Float c = this.coordinate;
        if (null != c){

            return true;
        }
        else if (this.hasNext() && Token.Coordinate == this.next()){

            return true;
        }
        else
            return false;
    }
    public final float[] listCoordinates(){
        float[] list = null;

        while (this.hasCoordinate()){
            float c = this.getCoordinate();
            if (null == list)
                list = new float[]{c};
            else {
                int len = list.length;
                float[] copier = new float[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                list = copier;
            }
        }

        if (null == list)
            return EmptySet;
        else
            return list;
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
    public <P extends Path> P apply(P path){

        return Parser.Apply(path,this);
    }

    /**
     * 
     */
    public final static <P extends Path> P Apply(P path, Parser p){
        Token last = null;

        float mx = 0, my = 0, sx = 0, sy = 0;

        for (Token tok : p){
            switch(tok){
            case Coordinate:
            case M:
                path.moveTo((mx = p.getCoordinate()),(my = p.getCoordinate()));

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                sx = mx;
                sy = my;
                break;
            case m:
                path.moveTo((mx += p.getCoordinate()),(my += p.getCoordinate()));

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                sx = mx;
                sy = my;
                break;
            case Z:
            case z:
                path.close();
                break;
            case L:
                path.lineTo((sx = p.getCoordinate()),(sy = p.getCoordinate()));

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                break;
            case l:
                path.lineTo((sx += p.getCoordinate()),(sy += p.getCoordinate()));

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                break;
            case H:
                sx = p.getCoordinate();

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                path.lineTo(sx,sy);
                break;
            case h:
                sx += p.getCoordinate();

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                path.lineTo(sx,sy);
                break;
            case V:
                sy = p.getCoordinate();

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                path.lineTo(sx,sy);
                break;
            case v:
                sy += p.getCoordinate();

                /*
                 * Discard higher dimensions
                 */
                while (p.hasCoordinate()){
                    p.getCoordinate();
                }

                path.lineTo(sx,sy);
                break;
            case C:{
                final float[] operands = p.listCoordinates();
                float x0, y0, x1, y1, x2, y2;

                switch(operands.length){
                case 6:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[2];
                       y1 = operands[3];

                       x2 = operands[4];
                       y2 = operands[5];
                       break;

                case 8:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[3];
                       y1 = operands[4];

                       x2 = operands[6];
                       y2 = operands[7];
                       break;

                default:
                    throw new IllegalArgumentException(String.format("Operands-dimension not 2 or 3: %d",operands.length));
                }
                path.cubicTo(x0,y0,x1,y1,x2,y2);

                sx = x2;
                sy = y2;

                break;
            }
            case c:{
                final float[] operands = p.listCoordinates();
                float x0, y0, x1, y1, x2, y2;

                switch(operands.length){
                case 6:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[2]+sx;
                       y1 = operands[3]+sy;

                       x2 = operands[4]+sx;
                       y2 = operands[5]+sy;
                       break;

                case 8:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[3]+sx;
                       y1 = operands[4]+sy;

                       x2 = operands[6]+sx;
                       y2 = operands[7]+sy;
                       break;

                default:
                    throw new IllegalArgumentException(String.format("Operands-dimension not 2 or 3: %d",operands.length));
                }

                path.cubicTo(x0,y0,x1,y1,x2,y2);

                sx = x2;
                sy = y2;
                break;
            }
            case S:
            case s:
                throw new UnsupportedOperationException(tok.name());
            case Q:{
                final float[] operands = p.listCoordinates();
                float x0, y0, x1, y1;

                switch(operands.length){
                case 4:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[2];
                       y1 = operands[3];
                       break;

                case 6:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[3];
                       y1 = operands[4];
                       break;

                default:
                    throw new IllegalArgumentException(String.format("Operands-dimension not 2 or 3: %d",operands.length));
                }
                path.quadTo(x0,y0,x1,y1);

                sx = x1;
                sy = y1;

                break;
            }
            case q:{
                final float[] operands = p.listCoordinates();
                float x0, y0, x1, y1;

                switch(operands.length){
                case 4:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[2]+sx;
                       y1 = operands[3]+sy;
                       break;

                case 6:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[3]+sx;
                       y1 = operands[4]+sy;
                       break;

                default:
                    throw new IllegalArgumentException(String.format("Operands-dimension not 2 or 3: %d",operands.length));
                }
                path.quadTo(x0,y0,x1,y1);

                sx = x1;
                sy = y1;

                break;
            }
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
