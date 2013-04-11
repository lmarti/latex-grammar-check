/* This file was generated by SableCC (http://www.sablecc.org/). */

package texparser.node;

import texparser.analysis.*;

@SuppressWarnings("nls")
public final class TClabel extends Token
{
    public TClabel()
    {
        super.setText("\\label");
    }

    public TClabel(int line, int pos)
    {
        super.setText("\\label");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TClabel(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTClabel(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TClabel text.");
    }
}
