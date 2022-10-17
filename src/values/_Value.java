package src.values;

import java.util.ArrayList;

import src.Context;

public class _Value
{
    public Context context = null;

    public _Value setContext(Context context)
    {
        this.context = context;
        return this;
    }

    public String value() throws Exception
    {
        throw new Exception("No value method defined");
    }

    public String type() throws Exception
    {
        throw new Exception("No type method defined");
    }

    public boolean isTrue()
    {
        return false;
    }

    public _Value copy() throws Exception
    {
        throw new Exception("No copy method defined");
    }

    public _Value get(_Value other) throws Exception
    {
        throw new Exception("No get method defined");
    }

    public _Value execute(ArrayList<_Value> args) throws Exception
    {
        throw new Exception("No execute method defined");
    }

    public _Value addedTo(_Value other) throws Exception
    {
        throw new Exception("TypeError: unsupported operand type(s) for +: '" + type() + "' and '" + other.type() +
                            "'");
    }

    public _Value subbedBy(_Value other) throws Exception
    {
        throw new Exception("TypeError: unsupported operand type(s) for -: '" + type() + "' and '" + other.type() +
                            "'");
    }

    public _Value multedBy(_Value other) throws Exception
    {
        throw new Exception("TypeError: unsupported operand type(s) for *: '" + type() + "' and '" + other.type() +
                            "'");
    }

    public _Value divedBy(_Value other) throws Exception
    {
        throw new Exception("TypeError: unsupported operand type(s) for /: '" + type() + "' and '" + other.type() +
                            "'");
    }
}
