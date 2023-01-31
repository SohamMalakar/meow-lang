package src.values;

import java.util.Objects;
import src.utils.Mathf;

public class _String extends _Value
{
    private String value;
    private int hashCode;

    public _String(String value)
    {
        this.value = value;
        hashCode = Objects.hash(value);
    }

    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        else if (other == null || getClass() != other.getClass())
            return false;

        _String that = (_String)other;
        return value.equals(that.rawValue());
    }

    public int hashCode()
    {
        return hashCode;
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
            int index = Integer.parseInt(other.value());
            index += index < 0 ? value.length() : 0;

            if (index < 0 || index >= value.length())
                throw new Exception("IndexError: string index out of range");

            char charAt = value.charAt(index);
            return new _String(String.valueOf(charAt)).setContext(context);
        }

        return super.get(other);
    }

    public _Value get(_Value start, _Value end, _Value step) throws Exception
    {
        if ((!start.type().equals("NoneType") && !start.type().equals("int")) ||
            (!end.type().equals("NoneType") && !end.type().equals("int")) ||
            (!step.type().equals("NoneType") && !step.type().equals("int")))
            return super.get(start, end, step);

        if (step.type().equals("NoneType"))
            step = new _Number("int", "1");

        int start_val = 0;
        int end_val = 0;
        int step_val = Integer.parseInt(step.value());

        if (step_val > 0)
        {
            if (start.type().equals("int"))
                start_val = Mathf.clamp(Integer.parseInt(start.value()), -value.length(), value.length());
            else
                start_val = 0;

            if (end.type().equals("int"))
                end_val = Mathf.clamp(Integer.parseInt(end.value()), -value.length(), value.length());
            else
                end_val = value.length();
        }
        else if (step_val < 0)
        {
            if (start.type().equals("int"))
                start_val = Mathf.clamp(Integer.parseInt(start.value()), ~value.length(), value.length() - 1);
            else
                start_val = -1;

            if (end.type().equals("int"))
                end_val = Mathf.clamp(Integer.parseInt(end.value()), ~value.length(), value.length() - 1);
            else
                end_val = ~value.length();
        }
        else
        {
            throw new Exception("ValueError: slice step cannot be zero");
        }

        start_val += start_val < 0 ? value.length() : 0;
        end_val += end_val < 0 ? value.length() : 0;

        if (step_val > 0)
        {
            if (start_val >= end_val)
                return new _String("").copy().setContext(context);
        }
        else
        {
            if (start_val <= end_val)
                return new _String("").copy().setContext(context);
        }

        String newStr = "";

        if (step_val > 0)
        {
            for (int i = start_val; i < end_val; i += step_val)
                newStr += value.charAt(i);
        }
        else
        {
            for (int i = start_val; i > end_val; i += step_val)
                newStr += value.charAt(i);
        }

        return new _String(newStr).copy().setContext(context);
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
        else if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
            return super.getComparisonEq(other);

        return new _Bool("false");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _String.class)
            return new _Bool(Boolean.toString(!this.value().equals(other.value())));
        else if (other.getClass() == _Function.class || other.getClass() == _BuiltInFunction.class)
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
