package src.values;

import src.utils.Mathf;

public class _Number extends _Value
{
    private String type;
    private String value;

    public _Number(String type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public String rawValue()
    {
        return value;
    }

    public String type()
    {
        return type;
    }

    public boolean isTrue() throws NumberFormatException, Exception
    {
        return Double.parseDouble(value()) != 0;
    }

    public _Number copy()
    {
        return (_Number) new _Number(type, value).setContext(context);
    }

    public _Number addedTo(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.addedTo(other);

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int res = Integer.parseInt(this.value()) + Integer.parseInt(other.value());
            return (_Number) new _Number("int", String.valueOf(res)).setContext(context);
        }
        else
        {
            double res = Double.parseDouble(this.value()) + Double.parseDouble(other.value());
            return (_Number) new _Number("float", String.valueOf(res)).setContext(context);
        }
    }

    public _Number subbedBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.subbedBy(other);

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int res = Integer.parseInt(this.value()) - Integer.parseInt(other.value());
            return (_Number) new _Number("int", String.valueOf(res)).setContext(context);
        }
        else
        {
            double res = Double.parseDouble(this.value()) - Double.parseDouble(other.value());
            return (_Number) new _Number("float", String.valueOf(res)).setContext(context);
        }
    }

    public _Value multedBy(_Value other) throws Exception
    {
        if (other.getClass() == _Number.class)
        {
            if (this.type().equals("int") && other.type().equals("int"))
            {
                int res = Integer.parseInt(this.value()) * Integer.parseInt(other.value());
                return (_Number) new _Number("int", String.valueOf(res)).setContext(context);
            }
            else
            {
                double res = Double.parseDouble(this.value()) * Double.parseDouble(other.value());
                return (_Number) new _Number("float", String.valueOf(res)).setContext(context);
            }
        }
        else if ((other.getClass() == _List.class || other.getClass() == _String.class) && type().equals("int"))
        {
            return other.multedBy(this);
        }

        return super.multedBy(other);
    }

    public _Number divedBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.divedBy(other);

        double r = Double.parseDouble(other.value());

        if (r == 0)
            throw new Exception("ZeroDivisionError: division or float division by zero");

        double l = Double.parseDouble(this.value());
        return (_Number) new _Number("float", String.valueOf(l / r)).setContext(context);
    }

    public _Number intdivedBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.intdivedBy(other);

        double l = Double.parseDouble(this.value());
        double r = Double.parseDouble(other.value());

        if (this.type().equals("int") && other.type().equals("int"))
        {
            if (r == 0)
                throw new Exception("ZeroDivisionError: integer division by zero");

            return (_Number) new _Number("int", String.valueOf((int)Math.floor(l / r))).setContext(context);
        }
        else
        {
            if (r == 0)
                throw new Exception("ZeroDivisionError: float floor division by zero");

            return (_Number) new _Number("float", String.valueOf(Math.floor(l / r))).setContext(context);
        }
    }

    public _Number moduloBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.moduloBy(other);

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int r = Integer.parseInt(other.value());

            if (r == 0)
                throw new Exception("ZeroDivisionError: modulo by zero");

            int l = Integer.parseInt(this.value());
            return (_Number) new _Number("int", String.valueOf((int)Mathf.modulus(l, r))).setContext(context);
        }
        else
        {
            double r = Double.parseDouble(other.value());

            if (r == 0)
                throw new Exception("ZeroDivisionError: float modulo");

            double l = Double.parseDouble(this.value());
            return (_Number) new _Number("float", String.valueOf(Mathf.modulus(l, r))).setContext(context);
        }
    }

    public _Number poweredBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.poweredBy(other);

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int res = (int)Math.pow(Integer.parseInt(this.value()), Integer.parseInt(other.value()));
            return (_Number) new _Number("int", String.valueOf(res)).setContext(context);
        }
        else
        {
            double res = Math.pow(Double.parseDouble(this.value()), Double.parseDouble(other.value()));

            if (Double.isNaN(res))
                throw new Exception("MathError: imaginary numbers are not supported yet");

            return (_Number) new _Number("float", String.valueOf(res)).setContext(context);
        }
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Number.class)
        {
            if (this.type().equals("int") && other.type().equals("int"))
                return new _Bool(Boolean.toString(Integer.parseInt(this.value()) == Integer.parseInt(other.value())));
            else
                return new _Bool(
                    Boolean.toString(Double.parseDouble(this.value()) == Double.parseDouble(other.value())));
        }
        else if (other.getClass() == _Bool.class)
        {
            return ((_Bool)other).getComparisonEq(this);
        }
        else if (other.getClass() == _BaseFunction.class)
        {
            return super.getComparisonEq(other);
        }

        return new _Bool("false");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Number.class)
        {
            if (this.type().equals("int") && other.type().equals("int"))
                return new _Bool(Boolean.toString(Integer.parseInt(this.value()) != Integer.parseInt(other.value())));
            else
                return new _Bool(
                    Boolean.toString(Double.parseDouble(this.value()) != Double.parseDouble(other.value())));
        }
        else if (other.getClass() == _Bool.class)
        {
            return ((_Bool)other).getComparisonNe(this);
        }
        else if (other.getClass() == _BaseFunction.class)
        {
            return super.getComparisonNe(other);
        }

        return new _Bool("true");
    }

    public _Value getComparisonLt(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return super.getComparisonLt(other);

        if (this.type().equals("int") && other.type().equals("int"))
            return new _Bool(Boolean.toString(Integer.parseInt(this.value()) < Integer.parseInt(other.value())));
        else
            return new _Bool(Boolean.toString(Double.parseDouble(this.value()) < Double.parseDouble(other.value())));
    }

    public _Value getComparisonGt(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return super.getComparisonGt(other);

        if (this.type().equals("int") && other.type().equals("int"))
            return new _Bool(Boolean.toString(Integer.parseInt(this.value()) > Integer.parseInt(other.value())));
        else
            return new _Bool(Boolean.toString(Double.parseDouble(this.value()) > Double.parseDouble(other.value())));
    }

    public _Value getComparisonLte(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return super.getComparisonLte(other);

        if (this.type().equals("int") && other.type().equals("int"))
            return new _Bool(Boolean.toString(Integer.parseInt(this.value()) <= Integer.parseInt(other.value())));
        else
            return new _Bool(Boolean.toString(Double.parseDouble(this.value()) <= Double.parseDouble(other.value())));
    }

    public _Value getComparisonGte(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return super.getComparisonGte(other);

        if (this.type().equals("int") && other.type().equals("int"))
            return new _Bool(Boolean.toString(Integer.parseInt(this.value()) >= Integer.parseInt(other.value())));
        else
            return new _Bool(Boolean.toString(Double.parseDouble(this.value()) >= Double.parseDouble(other.value())));
    }

    public _Value bitandedBy(_Value other) throws Exception
    {
        if (this.type().equals("int") && other.type().equals("int"))
            return new _Number("int",
                               Integer.toString(Integer.parseInt(this.value()) & Integer.parseInt(other.value())));

        return super.bitandedBy(other);
    }

    public _Value bitoredBy(_Value other) throws Exception
    {
        if (this.type().equals("int") && other.type().equals("int"))
            return new _Number("int",
                               Integer.toString(Integer.parseInt(this.value()) | Integer.parseInt(other.value())));

        return super.bitoredBy(other);
    }

    public _Value bitnotted() throws Exception
    {
        if (this.type().equals("int"))
            return new _Number("int", Integer.toString(~Integer.parseInt(this.value())));

        return super.bitnotted();
    }

    public _Value xoredBy(_Value other) throws Exception
    {
        if (this.type().equals("int") && other.type().equals("int"))
            return new _Number("int",
                               Integer.toString(Integer.parseInt(this.value()) ^ Integer.parseInt(other.value())));

        return super.xoredBy(other);
    }

    public _Value lshiftedBy(_Value other) throws Exception
    {
        if (this.type().equals("int") && other.type().equals("int"))
            return new _Number("int",
                               Integer.toString(Integer.parseInt(this.value()) << Integer.parseInt(other.value())));

        return super.lshiftedBy(other);
    }

    public _Value rshiftedBy(_Value other) throws Exception
    {
        if (this.type().equals("int") && other.type().equals("int"))
            return new _Number("int",
                               Integer.toString(Integer.parseInt(this.value()) >> Integer.parseInt(other.value())));

        return super.rshiftedBy(other);
    }
}
