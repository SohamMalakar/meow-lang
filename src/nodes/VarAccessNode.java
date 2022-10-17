package src.nodes;

import src.Token;

public class VarAccessNode extends Node
{
    public Token token;

    public VarAccessNode(Token token)
    {
        this.token = token;
    }

    public String repr()
    {
        return token.repr();
    }
}
