package src.nodes;

public class WhileNode extends Node
{
    public Node condition;
    public Node body;
    public boolean shouldReturnNull;

    public WhileNode(Node condition, Node body, boolean shouldReturnNull)
    {
        this.condition = condition;
        this.body = body;
        this.shouldReturnNull = shouldReturnNull;
    }

    public String repr() throws Exception
    {
        return "WHILE: (" + condition.repr() + ") {" + body.repr() + "}";
    }
}
