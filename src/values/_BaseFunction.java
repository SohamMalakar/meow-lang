package src.values;

import java.util.ArrayList;

import src.Context;
import src.SymbolTable;

public class _BaseFunction extends _Value
{
    public String name;

    public _BaseFunction(String name)
    {
        this.name = name != null ? name : "<anonymous>";
    }

    public String type() throws Exception
    {
        return "function";
    }

    public Context generateNewContext()
    {
        Context newContext = new Context(context);
        newContext.symbolTable = new SymbolTable(newContext.parent.symbolTable);
        return newContext;
    }

    private void checkArgs(ArrayList<String> argNames, ArrayList<_Value> argValues) throws Exception
    {
        if (argValues.size() > argNames.size())
            throw new Exception(argValues.size() - argNames.size() + " too many arguments passed into '" + name + "'");
        else if (argValues.size() < argNames.size())
            throw new Exception(argNames.size() - argValues.size() + " too few arguments passed into '" + name + "'");
    }

    private void populateArgs(ArrayList<String> argNames, ArrayList<_Value> argValues, Context execCtx)
    {
        for (int i = 0; i < argValues.size(); i++)
        {
            String argName = argNames.get(i);
            _Value argValue = argValues.get(i);
            argValue.setContext(execCtx);
            execCtx.symbolTable.set(argName, argValue);
        }
    }

    public void checkAndPopulateArgs(ArrayList<String> argNames, ArrayList<_Value> argValues, Context context)
        throws Exception
    {
        checkArgs(argNames, argValues);
        populateArgs(argNames, argValues, context);
    }
}
