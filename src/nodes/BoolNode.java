package src.nodes;

import src.Token;

public class BoolNode extends Node
{
    public Token token;

    public BoolNode(Token token)
    {
        this.token = token;
    }

    public String repr()
    {
        return token.repr();
    }
}
