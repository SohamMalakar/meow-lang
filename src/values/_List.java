package src.values;

import java.util.ArrayList;

public class _List extends _Value
{
    private ArrayList<_Value> elements;

    public _List(ArrayList<_Value> elements)
    {
        this.elements = elements;
    }

    public int size()
    {
        return elements.size();
    }

    public String rawValue() throws Exception
    {
        String str = "[";

        for (int i = 0; i < elements.size(); i++)
            str += elements.get(i).value() + (i != elements.size() - 1 ? ", " : "");

        return str + "]";
    }

    public String type()
    {
        return "list";
    }

    public boolean isTrue()
    {
        return !elements.isEmpty();
    }

    public _List copy() throws Exception
    {
        ArrayList<_Value> newElements = new ArrayList<_Value>();

        for (var element : elements)
            // newElements.add(element.copy());
            newElements.add(element); // why?

        return (_List) new _List(newElements).setContext(context);
    }

    public _Value get(_Value other) throws Exception
    {
        if (other.type().equals("int"))
            return elements.get(Integer.parseInt(other.value())).setContext(context);

        return super.get(other);
    }

    public _List addedTo(_Value other) throws Exception
    {
        if (other.getClass() != _List.class)
            return (_List)super.addedTo(other);

        _List newList = copy();

        for (_Value element : ((_List)other).elements)
            newList.elements.add(element);

        return newList;
    }

    public _List multedBy(_Value other) throws Exception
    {
        if (!other.type().equals("int"))
            return (_List)super.multedBy(other);

        ArrayList<_Value> newElements = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(other.value()); i++)
            for (_Value element : elements)
                newElements.add(element);

        return new _List(newElements);
    }
}
