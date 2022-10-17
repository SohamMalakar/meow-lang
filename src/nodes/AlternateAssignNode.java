package src.nodes;

import src.Token;

public class AlternateAssignNode extends Node
{
    public Token token;
    public Node node;

    public AlternateAssignNode(Token token, Node expr)
    {
        this.token = token;
        this.node = expr;
    }

    public String repr() throws Exception
    {
        return "(" + token.repr() + ", ASSIGN, " + node.repr() + ")";
    }
}
