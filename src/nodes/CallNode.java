package src.nodes;

import java.util.ArrayList;

public class CallNode extends Node
{
    public Node node;
    public ArrayList<Node> args;

    public CallNode(Node node, ArrayList<Node> args)
    {
        this.node = node;
        this.args = args;
    }

    public String repr() throws Exception
    {
        String str = "FUNCTION_CALL: " + node.repr() + "(";

        for (int i = 0; i < args.size(); i++)
            str += args.get(i).repr() + (i != args.size() - 1 ? ", " : "");

        return str + ")";
    }
}
