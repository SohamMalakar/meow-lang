package src.values;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import src.Context;

public class _BuiltInFunction extends _BaseFunction
{
    public _BuiltInFunction(String name)
    {
        super(name);
    }

    public String rawValue() throws Exception
    {
        return "<built-in function " + name + ">";
    }

    public _Value copy() throws Exception
    {
        var copy = new _BuiltInFunction(name);
        copy.setContext(context);
        return copy;
    }

    public _Value execute(ArrayList<_Value> args) throws Exception
    {
        Context executeCtx = generateNewContext();

        String methodName = "execute_" + name;
        ArrayList<String> argNames = new ArrayList<String>();

        Method method = getClass().getDeclaredMethod(methodName, executeCtx.getClass());

        for (Parameter arg : method.getParameters())
            argNames.add(arg.getName());

        checkAndPopulateArgs(argNames, args, executeCtx);

        _Value returnValue = (_Value)method.invoke(this, executeCtx);
        return returnValue;
    }
}
