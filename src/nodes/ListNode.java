package src.nodes;

import java.util.ArrayList;

public class ListNode extends Node
{
    public ArrayList<Node> elementNodes;

    public ListNode(ArrayList<Node> elementNodes)
    {
        this.elementNodes = elementNodes;
    }

    public String repr() throws Exception
    {
        String str = "[";

        for (int i = 0; i < elementNodes.size(); i++)
            str += elementNodes.get(i).repr() + (i != elementNodes.size() - 1 ? ", " : "");

        return str + "]";
    }
}
