/* This file was generated by SableCC (http://www.sablecc.org/). */

package texparser.node;

import texparser.analysis.*;

@SuppressWarnings("nls")
public final class TCnew extends Token
{
    public TCnew(String text)
    {
        setText(text);
    }

    public TCnew(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCnew(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCnew(this);
    }
}
