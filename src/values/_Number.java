package src.values;

import src.Token;
import src.TokenType;
import src.nodes.NumberNode;

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
        Token left = this.node.token;

        if (other.getClass() != _Number.class)
            super.addedTo(other);

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
        Token left = this.node.token;

        if (other.getClass() != _Number.class)
            super.subbedBy(other);

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
        Token left = this.node.token;

        if (other.getClass() == _Number.class)
        {
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
        else if (other.getClass() == _List.class && type().equals("int"))
        {
            return other.multedBy(this);
        }

        return super.multedBy(other);
    }

    public _Number divedBy(_Value other) throws Exception
    {
        Token left = this.node.token;

        if (other.getClass() != _Number.class)
            super.divedBy(other);

        Token right = ((_Number)other).node.token;

        double res = Double.parseDouble(left.value) / Double.parseDouble(right.value);
        return (_Number) new _Number(new NumberNode(new Token(TokenType.FLOAT, String.valueOf(res))))
            .setContext(context);
    }
}
