package src.values;

import java.util.ArrayList;
import src.Context;
import src.RTResult;

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
        return rawValue();
    }

    public String rawValue() throws Exception
    {
        throw new Exception("No value method defined");
    }

    public String type() throws Exception
    {
        throw new Exception("No type method defined");
    }

    public boolean isTrue() throws Exception
    {
        throw new Exception("No isTrue method defined");
    }

    public _Value copy() throws Exception
    {
        throw new Exception("No copy method defined");
    }

    public _Value get(_Value other) throws Exception
    {
        throw new Exception("No get method defined");
    }

    public RTResult execute(ArrayList<_Value> args) throws Exception
    {
        throw new Exception("No execute method defined");
    }

    public _Value addedTo(_Value other) throws Exception
    {
        return illegalOperation(other, "+");
    }

    public _Value subbedBy(_Value other) throws Exception
    {
        return illegalOperation(other, "-");
    }

    public _Value multedBy(_Value other) throws Exception
    {
        return illegalOperation(other, "*");
    }

    public _Value divedBy(_Value other) throws Exception
    {
        return illegalOperation(other, "/");
    }

    public _Value intdivedBy(_Value other) throws Exception
    {
        return illegalOperation(other, "//");
    }

    public _Value moduloBy(_Value other) throws Exception
    {
        return illegalOperation(other, "%");
    }

    public _Value poweredBy(_Value other) throws Exception
    {
        return illegalOperation(other, "**");
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        return illegalOperation(other, "==");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        return illegalOperation(other, "!=");
    }

    public _Value getComparisonLt(_Value other) throws Exception
    {
        return illegalOperation(other, "<");
    }

    public _Value getComparisonGt(_Value other) throws Exception
    {
        return illegalOperation(other, ">");
    }

    public _Value getComparisonLte(_Value other) throws Exception
    {
        return illegalOperation(other, "<=");
    }

    public _Value getComparisonGte(_Value other) throws Exception
    {
        return illegalOperation(other, ">=");
    }

    public _Value andedBy(_Value other) throws Exception
    {
        if (!this.isTrue())
            return this;
        return other;
    }

    public _Value oredBy(_Value other) throws Exception
    {
        if (this.isTrue())
            return this;
        return other;
    }

    public _Value notted() throws Exception
    {
        return new _Bool(Boolean.toString(!isTrue()));
    }

    private _Value illegalOperation(_Value other, String operator) throws Exception
    {
        throw new Exception("TypeError: unsupported operand type(s) for " + operator + ": '" + type() + "' and '" +
                            other.type() + "'");
    }
}
