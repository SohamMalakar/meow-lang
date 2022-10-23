package src.values;

import java.util.ArrayList;
import src.Context;
import src.Interpreter;
import src.nodes.Node;

public class _Function extends _BaseFunction
{
    private Node body;
    private ArrayList<String> argNames;
    private boolean shouldReturnNull;

    public _Function(String name, Node body, ArrayList<String> args, boolean shouldReturnNull)
    {
        super(name);
        this.body = body;
        this.argNames = args;
        this.shouldReturnNull = shouldReturnNull;
    }

    public String rawValue() throws Exception
    {
        return "<function " + name + ">";
    }

    public _Value copy()
    {
        return new _Function(name, body, argNames, shouldReturnNull).setContext(context);
    }

    public _Value execute(ArrayList<_Value> argValues) throws Exception
    {
        Interpreter interpreter = new Interpreter();
        Context newContext = generateNewContext();

        checkAndPopulateArgs(argNames, argValues, newContext);

        _Value value = interpreter.visit(body, newContext);
        return shouldReturnNull ? new _None() : value;
    }
}
