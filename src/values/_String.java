package src.values;

public class _String extends _Value
{
    private String value;

    public _String(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return "'" + rawValue() + "'";
    }

    public String rawValue()
    {
        return value;
    }

    public String type()
    {
        return "str";
    }

    public boolean isTrue()
    {
        return !value.isEmpty();
    }

    public _String copy()
    {
        return new _String(value);
    }

    public _Value get(_Value other) throws Exception
    {
        if (other.type().equals("int"))
        {
            char charAt = value.charAt(Integer.parseInt(other.value()));
            return new _String(String.valueOf(charAt)).setContext(context);
        }

        return super.get(other);
    }

    public _Value addedTo(_Value other) throws Exception
    {
        if (other.getClass() != _String.class)
            return super.addedTo(other);

        String newStr = rawValue() + ((_String)other).rawValue();
        return new _String(newStr);
    }

    public _Value multedBy(_Value other) throws Exception
    {
        if (!other.type().equals("int"))
            return super.multedBy(other);

        int count = Integer.parseInt(other.value());
        count = count < 0 ? 0 : count;

        String newStr = rawValue().repeat(count);
        return new _String(newStr);
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _String.class)
            return new _Bool(Boolean.toString(this.value().equals(other.value())));
        else if (other.getClass() == _Bool.class)
            return ((_Bool)other).getComparisonEq(this);
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonEq(other);

        return new _Bool("false");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _String.class)
            return new _Bool(Boolean.toString(!this.value().equals(other.value())));
        else if (other.getClass() == _Bool.class)
            return ((_Bool)other).getComparisonNe(this);
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonNe(other);

        return new _Bool("true");
    }

    public _Value getComparisonLt(_Value other) throws Exception
    {
        if (!other.type().equals("str"))
            return super.getComparisonLt(other);

        return new _Bool(Boolean.toString(this.value().compareTo(other.value()) < 0));
    }

    public _Value getComparisonGt(_Value other) throws Exception
    {
        if (!other.type().equals("str"))
            return super.getComparisonGt(other);

        return new _Bool(Boolean.toString(this.value().compareTo(other.value()) > 0));
    }

    public _Value getComparisonLte(_Value other) throws Exception
    {
        if (!other.type().equals("str"))
            return super.getComparisonLte(other);

        return new _Bool(Boolean.toString(this.value().compareTo(other.value()) <= 0));
    }

    public _Value getComparisonGte(_Value other) throws Exception
    {
        if (!other.type().equals("str"))
            return super.getComparisonGte(other);

        return new _Bool(Boolean.toString(this.value().compareTo(other.value()) >= 0));
    }
}
