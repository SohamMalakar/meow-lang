package src.values;

import java.util.Objects;

public class _Bool extends _Value
{
    private String value;
    private int hashCode;

    public _Bool(String value)
    {
        this.value = value;
        hashCode = Objects.hash(type(), value);
    }

    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        else if (other == null || getClass() != other.getClass())
            return false;

        _Bool that = (_Bool)other;
        return value.equals(that.rawValue());
    }

    public int hashCode()
    {
        return hashCode;
    }

    public String rawValue()
    {
        return value;
    }

    public String type()
    {
        return "bool";
    }

    public boolean isTrue() throws Exception
    {
        return value().equals("true");
    }

    public _Bool copy()
    {
        return (_Bool) new _Bool(value).setContext(context);
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Bool.class)
            return new _Bool(Boolean.toString(this.value().equals(other.value())));
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonEq(other);

        return new _Bool("false");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Bool.class)
            return new _Bool(Boolean.toString(!this.value().equals(other.value())));
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonNe(other);

        return new _Bool("true");
    }
}
