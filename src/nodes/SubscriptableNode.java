package src.nodes;

public class SubscriptableNode extends Node
{
    public Node subscriptableNode;
    public Node node;

    public SubscriptableNode(Node subscriptableNode, Node node)
    {
        this.subscriptableNode = subscriptableNode;
        this.node = node;
    }

    public String repr() throws Exception
    {
        return subscriptableNode.repr() + " @ [" + node.repr() + "]";
    }
}
