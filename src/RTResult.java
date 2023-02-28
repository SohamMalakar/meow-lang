package src;

import src.values._Value;

public class RTResult
{
    private _Value value;
    public _Value functionReturnValue;
    public Boolean loopShouldContinue;
    public Boolean loopShouldBreak;

    public RTResult()
    {
        reset();
    }

    private void reset()
    {
        value = null;
        functionReturnValue = null;
        loopShouldContinue = false;
        loopShouldBreak = false;
    }

    public _Value register(RTResult res)
    {
        functionReturnValue = res.functionReturnValue;
        loopShouldBreak = res.loopShouldBreak;
        loopShouldContinue = res.loopShouldContinue;
        return res.value;
    }

    public RTResult success(_Value value)
    {
        reset();
        this.value = value;
        return this;
    }

    public RTResult successReturn(_Value value)
    {
        reset();
        this.functionReturnValue = value;
        return this;
    }

    public RTResult successContinue()
    {
        reset();
        this.loopShouldContinue = true;
        return this;
    }

    public RTResult successBreak()
    {
        reset();
        this.loopShouldBreak = true;
        return this;
    }

    public boolean shouldReturn() throws Exception
    {
        return functionReturnValue != null ? true : (loopShouldBreak || loopShouldContinue);
    }

    public RTResult successPass()
    {
        reset();
        return this;
    }
}
