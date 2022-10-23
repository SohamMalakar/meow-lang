package src.values;

import src.Token;
import src.TokenType;
import src.nodes.BoolNode;

public class _None extends _Value
{
    public String rawValue() throws Exception
    {
        return "none";
    }

    public String type() throws Exception
    {
        return "NoneType";
    }

    public boolean isTrue()
    {
        return false;
    }

    public _None copy()
    {
        return new _None();
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _Bool.class)
            return ((_Bool)other).getComparisonEq(this);
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonEq(other);
        else
            return new _Bool(
                new BoolNode(new Token(TokenType.KEYWORD, Boolean.toString(other.type().equals("NoneType")))));
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _Bool.class)
            return ((_Bool)other).getComparisonNe(this);
        else if (other.getClass() == _BaseFunction.class)
            return super.getComparisonNe(other);
        else
            return new _Bool(
                new BoolNode(new Token(TokenType.KEYWORD, Boolean.toString(!other.type().equals("NoneType")))));
    }
}
