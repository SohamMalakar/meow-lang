package src.values;

import src.Token;
import src.TokenType;
import src.nodes.NumberNode;
import src.utils.Mathf;

public class _Number extends _Value
{
    private NumberNode node;

    public _Number(NumberNode node)
    {
        this.node = node;
    }

    public String value()
    {
        return node.token.value;
    }

    public String type()
    {
        return node.token.type == TokenType.INT ? "int" : "float";
    }

    public boolean isTrue()
    {
        return Double.parseDouble(value()) != 0;
    }

    public _Number copy()
    {
        return (_Number) new _Number(node).setContext(context);
    }

    public _Number addedTo(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.addedTo(other);

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;

        if (left.type == TokenType.INT && right.type == TokenType.INT)
        {
            int res = Integer.parseInt(left.value) + Integer.parseInt(right.value);
            return (_Number) new _Number(new NumberNode(new Token(TokenType.INT, String.valueOf(res))))
                .setContext(context);
        }
        else
        {
            double res = Double.parseDouble(left.value) + Double.parseDouble(right.value);
            return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(res))))
                .setContext(context);
        }
    }

    public _Number subbedBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.subbedBy(other);

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;

        if (left.type == TokenType.INT && right.type == TokenType.INT)
        {
            int res = Integer.parseInt(left.value) - Integer.parseInt(right.value);
            return (_Number) new _Number(new NumberNode(new Token(TokenType.INT, String.valueOf(res))))
                .setContext(context);
        }
        else
        {
            double res = Double.parseDouble(left.value) - Double.parseDouble(right.value);
            return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(res))))
                .setContext(context);
        }
    }

    public _Value multedBy(_Value other) throws Exception
    {
        if (other.getClass() == _Number.class)
        {
            Token left = this.node.token;
            Token right = ((_Number)other).node.token;

            if (left.type == TokenType.INT && right.type == TokenType.INT)
            {
                int res = Integer.parseInt(left.value) * Integer.parseInt(right.value);
                return (_Number) new _Number(new NumberNode(new Token(TokenType.INT, String.valueOf(res))))
                    .setContext(context);
            }
            else
            {
                double res = Double.parseDouble(left.value) * Double.parseDouble(right.value);
                return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(res))))
                    .setContext(context);
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

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;
        double r = Double.parseDouble(right.value);

        if (r == 0)
            throw new Exception("ZeroDivisionError: division or float division by zero");

        double l = Double.parseDouble(left.value);
        return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(l / r))))
            .setContext(context);
    }

    public _Number intdivedBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.intdivedBy(other);

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;

        double l = Double.parseDouble(left.value);
        double r = Double.parseDouble(right.value);

        if (this.type().equals("int") && other.type().equals("int"))
        {
            if (r == 0)
                throw new Exception("ZeroDivisionError: integer division by zero");

            return (_Number) new _Number(
                       new NumberNode(new Token(TokenType.INT, String.valueOf((int)Math.floor(l / r)))))
                .setContext(context);
        }
        else
        {
            if (r == 0)
                throw new Exception("ZeroDivisionError: float floor division by zero");

            return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(Math.floor(l / r)))))
                .setContext(context);
        }
    }

    public _Number moduloBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.moduloBy(other);

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int r = Integer.parseInt(right.value);

            if (r == 0)
                throw new Exception("ZeroDivisionError: modulo by zero");

            int l = Integer.parseInt(left.value);
            return (_Number) new _Number(
                       new NumberNode(new Token(TokenType.INT, String.valueOf((int)Mathf.modulus(l, r)))))
                .setContext(context);
        }
        else
        {
            double r = Double.parseDouble(right.value);

            if (r == 0)
                throw new Exception("ZeroDivisionError: float modulo");

            double l = Double.parseDouble(left.value);
            return (_Number) new _Number(
                       new NumberNode(new Token(TokenType.FLOAT, String.valueOf(Mathf.modulus(l, r)))))
                .setContext(context);
        }
    }

    public _Number poweredBy(_Value other) throws Exception
    {
        if (other.getClass() != _Number.class)
            return (_Number)super.poweredBy(other);

        Token left = this.node.token;
        Token right = ((_Number)other).node.token;

        if (this.type().equals("int") && other.type().equals("int"))
        {
            int res = (int)Math.pow(Integer.parseInt(left.value), Integer.parseInt(right.value));
            return (_Number) new _Number(new NumberNode(new Token(TokenType.INT, String.valueOf(res))))
                .setContext(context);
        }
        else
        {
            double res = Math.pow(Double.parseDouble(left.value), Double.parseDouble(right.value));

            if (Double.isNaN(res))
                throw new Exception("MathError: imaginary numbers are not supported yet");

            return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(res))))
                .setContext(context);
        }
    }
}
