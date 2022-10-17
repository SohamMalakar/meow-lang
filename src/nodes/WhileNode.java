package src.nodes;

public class WhileNode extends Node
{
    public Node condition;
    public Node body;

    public WhileNode(Node condition, Node body)
    {
        this.condition = condition;
        this.body = body;
    }

    public String repr() throws Exception
    {
        return "WHILE: (" + condition.repr() + ") {" + body.repr() + "}";
    }
}
