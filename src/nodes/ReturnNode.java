package src.nodes;

public class ReturnNode extends Node
{
    public Node nodeToReturn;

    public ReturnNode(Node nodeToReturn)
    {
        this.nodeToReturn = nodeToReturn;
    }

    public String repr()
    {
        return "KEYWORD: return";
    }
}
