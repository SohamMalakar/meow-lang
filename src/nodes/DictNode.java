package src.nodes;

import java.util.ArrayList;
import src.utils.Pair;

public class DictNode extends Node
{
    public ArrayList<Pair<Node, Node>> node;

    public DictNode(ArrayList<Pair<Node, Node>> node)
    {
        this.node = node;
    }

    public String repr() throws Exception
    {
        String str = "{";

        for (int i = 0; i < node.size(); i++)
            str += node.get(i).key.repr() + ": " + node.get(i).value.repr() + (i != node.size() - 1 ? ", " : "");

        return str + "}";
    }
}
