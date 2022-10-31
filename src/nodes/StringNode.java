package src.nodes;

import src.Token;

public class StringNode extends Node
{
    public Token token;

    public StringNode(Token token)
    {
        this.token = token;
    }

    public String repr()
    {
        return "'" + token.repr() + "'";
    }
}
