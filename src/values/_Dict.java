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
}
