package src.values;

import java.util.ArrayList;
import src.Context;
import src.Interpreter;
import src.RTResult;
import src.nodes.Node;

public class _Function extends _BaseFunction
{
    private Node body;
    private ArrayList<String> argNames;
    private boolean shouldAutoReturn;

    public _Function(String name, Node body, ArrayList<String> args, boolean shouldAutoReturn)
    {
        super(name);
        this.body = body;
        this.argNames = args;
        this.shouldAutoReturn = shouldAutoReturn;
    }

    public String rawValue() throws Exception
    {
        return "<function " + name + ">";
    }

    public _Value copy()
    {
        return new _Function(name, body, argNames, shouldAutoReturn).setContext(context);
    }

    public RTResult execute(ArrayList<_Value> argValues) throws Exception
    {
        RTResult res = new RTResult();
        Interpreter interpreter = new Interpreter();
        Context newContext = generateNewContext();

        res.register(checkAndPopulateArgs(argNames, argValues, newContext));

        if (res.shouldReturn())
            return res;

        _Value value = res.register(interpreter.visit(body, newContext));

        if (res.shouldReturn() && res.functionReturnValue == null)
            return res;

        var retValue =
            shouldAutoReturn ? value : (res.functionReturnValue != null ? res.functionReturnValue : new _None());
        return res.success(retValue);
    }
}
