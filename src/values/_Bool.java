package src.values;

import src.Token;
import src.TokenType;
import src.nodes.BoolNode;

public class _Bool extends _Value
{
    private BoolNode node;

    public _Bool(BoolNode node)
    {
        this.node = node;
    }

    public String rawValue()
    {
        return node.token.value;
    }

    public String type()
    {
        return "bool";
    }

    public boolean isTrue() throws Exception
    {
        return value().equals("true");
    }

    public _Bool copy()
    {
        return (_Bool) new _Bool(node).setContext(context);
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (other.getClass() == _BaseFunction.class)
            return super.getComparisonEq(other);

        return new _Bool(new BoolNode(new Token(TokenType.KEYWORD, Boolean.toString(this.isTrue() == other.isTrue()))));
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (other.getClass() == _BaseFunction.class)
            return super.getComparisonNe(other);

        return new _Bool(new BoolNode(new Token(TokenType.KEYWORD, Boolean.toString(this.isTrue() != other.isTrue()))));
    }
}
