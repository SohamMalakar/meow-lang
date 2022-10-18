package src.values;

import src.Token;
import src.TokenType;
import src.nodes.StringNode;

public class _String extends _Value
{
    private StringNode node;

    public _String(StringNode node)
    {
        this.node = node;
    }

    public String value()
    {
        return "'" + rawValue() + "'";
    }

    private String rawValue()
    {
        return node.token.value;
    }

    public String type()
    {
        return "str";
    }

    public boolean isTrue()
    {
        return !node.token.value.isEmpty();
    }

    public _String copy()
    {
        return new _String(node);
    }

    public _Value get(_Value other) throws Exception
    {
        if (other.type().equals("int"))
        {
            char charAt = node.token.value.charAt(Integer.parseInt(other.value()));
            return new _String(new StringNode(new Token(TokenType.STRING, String.valueOf(charAt)))).setContext(context);
        }

        return super.get(other);
    }

    public _Value addedTo(_Value other) throws Exception
    {
        if (other.getClass() != _String.class)
            return super.addedTo(other);

        String newStr = rawValue() + ((_String)other).rawValue();
        return new _String(new StringNode(new Token(TokenType.STRING, newStr)));
    }

    public _Value multedBy(_Value other) throws Exception
    {
        if (!other.type().equals("int"))
            return super.multedBy(other);

        String newStr = rawValue().repeat(Integer.parseInt(other.value()));
        return new _String(new StringNode(new Token(TokenType.STRING, newStr)));
    }
}
