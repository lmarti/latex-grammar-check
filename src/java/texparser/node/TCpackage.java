/* This file was generated by SableCC (http://www.sablecc.org/). */

package texparser.node;

import texparser.analysis.*;

@SuppressWarnings("nls")
public final class TCpackage extends Token
{
    public TCpackage()
    {
        super.setText("\\usepackage");
    }

    public TCpackage(int line, int pos)
    {
        super.setText("\\usepackage");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCpackage(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCpackage(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TCpackage text.");
    }
}
