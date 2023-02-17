package src.values;

import java.util.HashMap;
import java.util.Map;

public class _Dict extends _Value
{
    private Map<_Value, _Value> dict = new HashMap<_Value, _Value>();

    public _Dict(Map<_Value, _Value> elements)
    {
        dict = elements;
    }

    public int size()
    {
        return dict.keySet().size();
    }

    public boolean containsKey(_Value other)
    {
        return dict.containsKey(other);
    }

    public String rawValue() throws Exception
    {
        String str = "{";
        int i = 0;

        for (var elem : dict.entrySet())
            str += elem.getKey().value() + ": " + elem.getValue().value() +
                   (i++ != dict.entrySet().size() - 1 ? ", " : "");

        return str + "}";
    }

    public String type() throws Exception
    {
        return "dict";
    }

    public boolean isTrue() throws Exception
    {
        return !dict.isEmpty();
    }

    public _Value copy() throws Exception
    {
        return super.copy();
    }

    public _Value get(_Value other) throws Exception
    {
        if (!dict.containsKey(other))
            throw new Exception("KeyError: " + other.value());

        return dict.get(other);
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonEq(other);
        else if (!this.type().equals(other.type()) || this.size() != other.size())
            return new _Bool("false");

        for (var elem : dict.entrySet())
            if (!((_Dict)other).containsKey(elem.getKey()) ||
                !elem.getValue().getComparisonEq(other.get(elem.getKey())).isTrue())
                return new _Bool("false");

        return new _Bool("true");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonNe(other);
        else if (!this.type().equals(other.type()) || this.size() != other.size())
            return new _Bool("true");

        for (var elem : dict.entrySet())
            if (!((_Dict)other).containsKey(elem.getKey()) ||
                !elem.getValue().getComparisonEq(other.get(elem.getKey())).isTrue())
                return new _Bool("true");

        return new _Bool("false");
    }
}
