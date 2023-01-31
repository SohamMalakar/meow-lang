package src.nodes;

public class SliceNode extends Node
{
    public Node node;
    public Node start;
    public Node end;
    public Node step;

    public SliceNode(Node node, Node start, Node end, Node step)
    {
        this.node = node;
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public String repr() throws Exception
    {
        return node.repr() + " @ [" + start.repr() + ", " + end.repr() + ", " + step.repr() + "]";
    }
}
