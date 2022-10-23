package src.values;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import src.Context;
import src.Token;
import src.TokenType;
import src.nodes.StringNode;

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

    public _Value execute_print(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("arg0");
        System.out.print(value.rawValue());
        return new _None();
    }

    public _Value execute_str(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("arg0");
        return new _String(new StringNode(new Token(TokenType.STRING, value.rawValue())));
    }
}
