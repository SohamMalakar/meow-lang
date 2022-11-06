package src.values;

public class _Bool extends _Value
{
    private String value;

    public _Bool(String value)
    {
        this.value = value;
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
