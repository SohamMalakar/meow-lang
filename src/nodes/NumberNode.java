package src.nodes;

import src.Token;

public class NumberNode extends Node
{
    public Token token;

    public NumberNode(Token token)
    {
        this.token = token;
    }

    public String repr()
    {
        return token.repr();
    }
}
