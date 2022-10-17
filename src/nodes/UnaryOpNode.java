package src.nodes;

import src.Token;

public class UnaryOpNode extends Node
{
    public Token token;
    public Node node;

    public UnaryOpNode(Token token, Node node)
    {
        this.token = token;
        this.node = node;
    }

    public String repr() throws Exception
    {
        return "(" + token.repr() + ", " + node.repr() + ")";
    }
}
