package src.values;

import java.util.ArrayList;
import src.utils.Mathf;

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
        {
            int index = Integer.parseInt(other.value());
            index += index < 0 ? size() : 0;

            if (index < 0 || index >= size())
                throw new Exception("IndexError: list index out of range");

            return elements.get(index).setContext(context);
        }

        return super.get(other);
    }

    public _Value get(_Value start, _Value end, _Value step) throws Exception
    {
        if ((!start.type().equals("NoneType") && !start.type().equals("int")) ||
            (!end.type().equals("NoneType") && !end.type().equals("int")) ||
            (!step.type().equals("NoneType") && !step.type().equals("int")))
            return super.get(start, end, step);

        // do the actual slicing, enough boilerplate
        if (step.type().equals("NoneType"))
            step = new _Number("int", "1");

        int start_val;
        int end_val;
        int step_val = Integer.parseInt(step.value());

        // step 1: clamping
        if (step_val > 0)
        {
            if (start.type().equals("int"))
                start_val = Mathf.clamp(Integer.parseInt(start.value()), -size(), size());
            else
                start_val = 0;

            if (end.type().equals("int"))
                end_val = Mathf.clamp(Integer.parseInt(end.value()), -size(), size());
            else
                end_val = size();
        }
        else if (step_val < 0)
        {
            if (start.type().equals("int"))
                start_val = Mathf.clamp(Integer.parseInt(start.value()), ~size(), size() - 1);
            else
                start_val = -1;

            if (end.type().equals("int"))
                end_val = Mathf.clamp(Integer.parseInt(end.value()), ~size(), size() - 1);
            else
                end_val = ~size();
        }
        else
        {
            throw new Exception("ValueError: slice step cannot be zero");
        }

        // step 2: evaluating
        start_val += start_val < 0 ? size() : 0;
        end_val += end_val < 0 ? size() : 0;

        // step 3: limiting
        if (step_val > 0)
        {
            if (start_val >= end_val)
                return new _List(new ArrayList<_Value>()).copy().setContext(context);
        }
        else
        {
            if (start_val <= end_val)
                return new _List(new ArrayList<_Value>()).copy().setContext(context);
        }

        // step 4: now do it
        ArrayList<_Value> newElements = new ArrayList<_Value>();

        if (step_val > 0)
        {
            for (int i = start_val; i < end_val; i += step_val)
                newElements.add(elements.get(i));
        }
        else
        {
            for (int i = start_val; i > end_val; i += step_val)
                newElements.add(elements.get(i));
        }

        return new _List(newElements).copy().setContext(context);
    }

    public _Value update(_Value index, _Value newVal) throws Exception
    {
        if (!index.type().equals("int"))
            throw new Exception("update function for list takes second argument as int");

        int i = Integer.parseInt(index.value());
        elements.set(i, newVal);

        return this;
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

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonEq(other);
        else if (!this.type().equals(other.type()) || this.size() != other.size())
            return new _Bool(Boolean.toString(false));

        for (int i = 0; i < this.size(); i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(false));
        }

        return new _Bool(Boolean.toString(true));
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonNe(other);
        else if (!this.type().equals(other.type()) || this.size() != other.size())
            return new _Bool(Boolean.toString(true));

        for (int i = 0; i < this.size(); i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(true));
        }

        return new _Bool(Boolean.toString(false));
    }

    public _Value getComparisonLt(_Value other) throws Exception
    {
        if (!this.type().equals(other.type()))
            return super.getComparisonLt(other);

        int size = this.size() < other.size() ? this.size() : other.size();

        for (int i = 0; i < size; i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(this.get(index).getComparisonLt(other.get(index)).isTrue()));
        }

        return new _Bool(Boolean.toString(this.size() < other.size()));
    }

    public _Value getComparisonGt(_Value other) throws Exception
    {
        if (!this.type().equals(other.type()))
            return super.getComparisonGt(other);

        int size = this.size() < other.size() ? this.size() : other.size();

        for (int i = 0; i < size; i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(this.get(index).getComparisonGt(other.get(index)).isTrue()));
        }

        return new _Bool(Boolean.toString(this.size() > other.size()));
    }

    public _Value getComparisonLte(_Value other) throws Exception
    {
        if (!this.type().equals(other.type()))
            return super.getComparisonLte(other);

        int size = this.size() < other.size() ? this.size() : other.size();

        for (int i = 0; i < size; i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(this.get(index).getComparisonLte(other.get(index)).isTrue()));
        }

        return new _Bool(Boolean.toString(this.size() <= other.size()));
    }

    public _Value getComparisonGte(_Value other) throws Exception
    {
        if (!this.type().equals(other.type()))
            return super.getComparisonGte(other);

        int size = this.size() < other.size() ? this.size() : other.size();

        for (int i = 0; i < size; i++)
        {
            var index = new _Number("int", Integer.toString(i));

            if (!this.get(index).getComparisonEq(other.get(index)).isTrue())
                return new _Bool(Boolean.toString(this.get(index).getComparisonGte(other.get(index)).isTrue()));
        }

        return new _Bool(Boolean.toString(this.size() >= other.size()));
    }
}
