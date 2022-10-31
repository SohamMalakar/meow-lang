package src.nodes;

import java.util.ArrayList;
import src.utils.Pair;

public class IfNode extends Node
{
    public ArrayList<Pair<Pair<Node, Node>, Boolean>> cases;
    public Pair<Node, Boolean> elseCase;

    public IfNode(ArrayList<Pair<Pair<Node, Node>, Boolean>> cases, Pair<Node, Boolean> elseCase)
    {
        this.cases = cases;
        this.elseCase = elseCase;
    }

    public String repr() throws Exception
    {
        String str = "";

        for (int i = 0; i < cases.size(); i++)
            str += (i == 0 ? "" : " EL") + "IF: (" + cases.get(i).key.key.repr() + ") {" +
                   cases.get(i).key.value.repr() + "}";

        if (elseCase != null)
            str += " ELSE: {" + elseCase.key.repr() + "}";

        return str;
    }
}
