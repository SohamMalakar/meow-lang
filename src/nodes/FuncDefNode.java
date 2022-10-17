package src.nodes;

import java.util.ArrayList;

import src.Token;

public class FuncDefNode extends Node
{
    public Token varName;
    public ArrayList<Token> args;
    public Node body;

    public FuncDefNode(Token varName, ArrayList<Token> args, Node body)
    {
        this.varName = varName;
        this.args = args;
        this.body = body;
    }

    public String repr() throws Exception
    {
        String str = "FUNCTION_DEFINITION: " + (varName != null ? varName.value : "<anonymous>") + "(";

        for (int i = 0; i < args.size(); i++)
            str += args.get(i).value + (i != args.size() - 1 ? ", " : "");

        return str + ") {" + body.repr() + "}";
    }
}
