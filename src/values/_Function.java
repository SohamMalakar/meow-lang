package src.values;

import java.util.ArrayList;

import src.Context;
import src.Interpreter;
import src.nodes.Node;

public class _Function extends _BaseFunction
{
    private Node body;
    private ArrayList<String> argNames;

    public _Function(String name, Node body, ArrayList<String> args)
    {
        super(name);
        this.body = body;
        this.argNames = args;
    }

    public String value() throws Exception
    {
        return "<function " + name + ">";
    }

    public String type() throws Exception
    {
        return "function";
    }

    public _Value copy()
    {
        return new _Function(name, body, argNames).setContext(context);
    }

    public _Value execute(ArrayList<_Value> argValues) throws Exception
    {
        Interpreter interpreter = new Interpreter();
        Context newContext = generateNewContext();

        checkAndPopulateArgs(argNames, argValues, newContext);

        _Value value = interpreter.visit(body, newContext);
        return value;
    }
}
