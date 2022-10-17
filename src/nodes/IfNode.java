package src.nodes;

import java.util.ArrayList;

import src.Pair;

public class IfNode extends Node
{
    public ArrayList<Pair<Node, Node>> cases;
    public Node elseCase;

    public IfNode(ArrayList<Pair<Node, Node>> cases, Node elseCase)
    {
        this.cases = cases;
        this.elseCase = elseCase;
    }

    public String repr() throws Exception
    {
        String str = "";

        for (int i = 0; i < cases.size(); i++)
            str += (i == 0 ? "" : " EL") + "IF: (" + cases.get(i).key.repr() + ") {" + cases.get(i).value.repr() + "}";

        if (elseCase != null)
            str += " ELSE: {" + elseCase.repr() + "}";

        return str;
    }
}
