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
 * Single - use stream iterator 
 * 
 * Parses SVG Path "d" attribute value expressions 
 * 
 * Accepts expression operands in two or three dimensions
 * 
 * Ignores third dimension operands, preserving two dimensional data
 * 
 */
public class Parser
    extends Object
    implements Iterable<Token>,
               java.util.Iterator<Token>
{
    public final static boolean Debug;
    static {

        final String config = System.getProperty("path.Parser.Debug");

        Debug = ((null != config)&&("true".equalsIgnoreCase(config)));
    }
    public final static java.io.PrintStream Out = java.lang.System.err;
    static {

        if (Debug)
            Out.printf("path.Parser.Debug=%b%n",Debug);
    }

    public final static float[] EmptySet = {};



    private final char[] string;
    private int index;
    private java.lang.Float coordinate;
    private Token token;


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


    private java.lang.Float getCoordinate(){
        java.lang.Float c = this.coordinate;
        if (null != c){
            this.token = null;
            this.coordinate = null;
            return c;
        }
        else if (this.hasNext() && Token.Coordinate == this.next()){
            c = this.coordinate;
            this.token = null;
            this.coordinate = null;
            return c;
        }
        else
            throw new java.util.NoSuchElementException();
    }
    private boolean hasCoordinate(){
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
        this.token = null;

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
        if (null != this.token)
            return this.token;
        else {
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
                            return (this.token = Token.valueOf(String.valueOf(this.string[this.index++])));
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

                    return (this.token = token);
                }
                else
                    return Token.Unknown;
            }
            else
                throw new java.util.NoSuchElementException();
        }
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

            final float[] operands = p.listCoordinates();

            switch(tok){

            case M:{

                float x0, y0;

                switch(operands.length){
                case 2:
                case 3:
                    x0 = operands[0];
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                path.moveTo((mx = x0),(my = y0));

                sx = mx;
                sy = my;

                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));

                break;
            }
            case m:{

                float x0, y0;

                switch(operands.length){
                case 2:
                case 3:
                    x0 = operands[0];
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                path.moveTo((mx += x0),(my += y0));

                sx = mx;
                sy = my;


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));

                break;
            }
            case Z:
            case z:
                path.close();

                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.opfor());

                break;
            case L:{

                float x0, y0;

                switch(operands.length){
                case 2:
                case 3:
                    x0 = operands[0];
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                path.lineTo((sx = x0),(sy = y0));


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case l:{

                float x0, y0;

                switch(operands.length){
                case 2:
                case 3:
                    x0 = operands[0];
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                path.lineTo((sx += x0),(sy += y0));


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case H:{

                float x0;

                switch(operands.length){
                case 1:
                case 2:
                case 3:
                    x0 = operands[0];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                sx = x0;

                path.lineTo(sx,sy);


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case h:{

                float x0;

                switch(operands.length){
                case 1:
                case 2:
                case 3:
                    x0 = operands[0];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                sx += x0;

                path.lineTo(sx,sy);


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case V:{

                float y0;

                switch(operands.length){
                case 1:
                    y0 = operands[0];
                    break;
                case 2:
                case 3:
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                sy = y0;

                path.lineTo(sx,sy);


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case v:{

                float y0;

                switch(operands.length){
                case 1:
                    y0 = operands[0];
                    break;
                case 2:
                case 3:
                    y0 = operands[1];
                    break;

                default:
                    throw new IllegalArgumentException(String.format("TODO operands: %d",operands.length));
                }
                sy += y0;

                path.lineTo(sx,sy);


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case C:{

                float x0, y0, x1, y1, x2, y2;

                switch(operands.length){
                case 6:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[2];
                       y1 = operands[3];

                       x2 = operands[4];
                       y2 = operands[5];

                       path.cubicTo(x0,y0,x1,y1,x2,y2);

                       sx = x2;
                       sy = y2;

                       break;

                case 8:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[3];
                       y1 = operands[4];

                       x2 = operands[6];
                       y2 = operands[7];

                       path.cubicTo(x0,y0,x1,y1,x2,y2);

                       sx = x2;
                       sy = y2;

                       break;

                default:{
                    /*
                     * 2D poly-bezier
                     */
                    final int polylen = operands.length;

                    for (int ofs = 0; ofs < polylen; ofs += 6){
                        x0 = operands[ofs+0];
                        y0 = operands[ofs+1];

                        x1 = operands[ofs+2];
                        y1 = operands[ofs+3];

                        x2 = operands[ofs+4];
                        y2 = operands[ofs+5];

                        path.cubicTo(x0,y0,x1,y1,x2,y2);

                        sx = x2;
                        sy = y2;

                    }

                    break;
                }
                }


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case c:{

                float x0, y0, x1, y1, x2, y2;

                switch(operands.length){
                case 6:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[2]+sx;
                       y1 = operands[3]+sy;

                       x2 = operands[4]+sx;
                       y2 = operands[5]+sy;

                       path.cubicTo(x0,y0,x1,y1,x2,y2);

                       sx = x2;
                       sy = y2;
                       break;

                case 8:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[3]+sx;
                       y1 = operands[4]+sy;

                       x2 = operands[6]+sx;
                       y2 = operands[7]+sy;

                       path.cubicTo(x0,y0,x1,y1,x2,y2);

                       sx = x2;
                       sy = y2;
                       break;

                default:{
                    /*
                     * 2D poly-bezier
                     */
                    final int polylen = operands.length;

                    for (int ofs = 0; ofs < polylen; ofs += 6){
                        x0 = operands[ofs+0]+sx;
                        y0 = operands[ofs+1]+sy;

                        x1 = operands[ofs+2]+sx;
                        y1 = operands[ofs+3]+sy;

                        x2 = operands[ofs+4]+sx;
                        y2 = operands[ofs+5]+sy;

                        path.cubicTo(x0,y0,x1,y1,x2,y2);

                        sx = x2;
                        sy = y2;

                    }

                    break;
                }
                }



                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));

                break;
            }
            case S:
            case s:
                throw new UnsupportedOperationException(tok.name());
            case Q:{

                float x0, y0, x1, y1;

                switch(operands.length){
                case 4:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[2];
                       y1 = operands[3];

                       path.quadTo(x0,y0,x1,y1);

                       sx = x1;
                       sy = y1;
                       break;

                case 6:
                       x0 = operands[0];
                       y0 = operands[1];

                       x1 = operands[3];
                       y1 = operands[4];

                       path.quadTo(x0,y0,x1,y1);

                       sx = x1;
                       sy = y1;
                       break;

                default:{
                    /*
                     * 2D poly-bezier
                     */
                    final int polylen = operands.length;

                    for (int ofs = 0; ofs < polylen; ofs += 4){
                        x0 = operands[ofs+0];
                        y0 = operands[ofs+1];

                        x1 = operands[ofs+2];
                        y1 = operands[ofs+3];

                        path.quadTo(x0,y0,x1,y1);

                        sx = x1;
                        sy = y1;
                    }

                    break;
                }
                }


                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


                break;
            }
            case q:{

                float x0, y0, x1, y1;

                switch(operands.length){
                case 4:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[2]+sx;
                       y1 = operands[3]+sy;

                       path.quadTo(x0,y0,x1,y1);

                       sx = x1;
                       sy = y1;
                       break;

                case 6:
                       x0 = operands[0]+sx;
                       y0 = operands[1]+sy;

                       x1 = operands[3]+sx;
                       y1 = operands[4]+sy;

                       path.quadTo(x0,y0,x1,y1);


                       sx = x1;
                       sy = y1;
                       break;

                default:{
                    /*
                     * 2D poly-bezier
                     */
                    final int polylen = operands.length;

                    for (int ofs = 0; ofs < polylen; ofs += 4){
                        x0 = operands[ofs+0]+sx;
                        y0 = operands[ofs+1]+sy;

                        x1 = operands[ofs+2]+sx;
                        y1 = operands[ofs+3]+sy;

                        path.quadTo(x0,y0,x1,y1);

                        sx = x1;
                        sy = y1;
                    }

                    break;
                }
                }

                if (Debug)
                    Out.printf("path.Parser APPLY(%s)%n",tok.format(operands));


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
