package src.nodes;

import src.Token;

public class BinOpNode extends Node
{
    public Node left;
    public Token token;
    public Node right;

    public BinOpNode(Node left, Token token, Node right)
    {
        this.left = left;
        this.token = token;
        this.right = right;
    }

    public String repr() throws Exception
    {
        return "(" + left.repr() + ", " + token.repr() + ", " + right.repr() + ")";
    }
}
