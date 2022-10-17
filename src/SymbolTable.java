package src;

import java.util.Dictionary;
import java.util.Hashtable;

import src.values._Value;

public class SymbolTable
{
    private Dictionary<String, _Value> symbols = new Hashtable<String, _Value>();
    private SymbolTable parent = null;

    public SymbolTable(SymbolTable parent)
    {
        this.parent = parent;
    }

    public _Value get(String key)
    {
        _Value value = symbols.get(key);

        if (value == null && parent != null)
            return parent.get(key);

        return value;
    }

    public void set(String key, _Value value)
    {
        symbols.put(key, value);
    }

    public void altset(String key, _Value value)
    {
        if (symbols.get(key) != null)
            symbols.put(key, value);
        else if (parent != null && parent.get(key) != null)
            parent.altset(key, value);
        else
            symbols.put(key, value);
    }

    public void remove(String key)
    {
        symbols.remove(key);
    }
}
