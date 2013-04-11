/* This file was generated by SableCC (http://www.sablecc.org/). */

package texparser.node;

import texparser.analysis.*;

@SuppressWarnings("nls")
public final class TArgument extends Token
{
    public TArgument(String text)
    {
        setText(text);
    }

    public TArgument(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TArgument(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTArgument(this);
    }
}
