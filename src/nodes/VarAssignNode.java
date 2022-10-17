package src.nodes;

import src.Token;

public class VarAssignNode extends Node
{
    public Token token;
    public Node node;

    public VarAssignNode(Token token, Node expr)
    {
        this.token = token;
        this.node = expr;
    }

    public String repr() throws Exception
    {
        return "(" + token.repr() + ", EQ, " + node.repr() + ")";
    }
}
