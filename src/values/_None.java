package src.values;

import java.util.Objects;

public class _None extends _Value
{
    private int hashCode;

    public _None()
    {
        hashCode = Objects.hash("NoneType", "none");
    }

    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        else if (other == null || getClass() != other.getClass())
            return false;
        else
            return true;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public String rawValue() throws Exception
    {
        return "none";
    }

    public String type() throws Exception
    {
        return "NoneType";
    }

    public boolean isTrue()
    {
        return false;
    }

    public _None copy()
    {
        return new _None();
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonEq(other);
        else
            return new _Bool(Boolean.toString(other.type().equals("NoneType")));
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonNe(other);
        else
            return new _Bool(Boolean.toString(!other.type().equals("NoneType")));
    }
}
