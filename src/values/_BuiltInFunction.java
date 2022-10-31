package src.values;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import src.Context;
import src.RTResult;
import src.Run;

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

    public RTResult execute(ArrayList<_Value> args) throws Exception
    {
        RTResult res = new RTResult();
        Context executeCtx = generateNewContext();

        String methodName = "execute_" + name;
        ArrayList<String> argNames = new ArrayList<String>();

        Method method = getClass().getDeclaredMethod(methodName, executeCtx.getClass());

        for (Parameter arg : method.getParameters())
            argNames.add(arg.getName());

        res.register(checkAndPopulateArgs(argNames, args, executeCtx));

        if (res.shouldReturn())
            return res;

        _Value returnValue = res.register((RTResult)method.invoke(this, executeCtx));

        if (res.shouldReturn())
            return res;
        return res.success(returnValue);
    }

    public RTResult execute_print(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("arg0");
        System.out.print(value.rawValue());
        return new RTResult().success(new _None());
    }

    public RTResult execute_str(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("arg0");
        return new RTResult().success(new _String(value.rawValue()));
    }

    public RTResult execute_run(Context execCtx) throws Exception
    {
        var fn = execCtx.symbolTable.get("arg0");

        if (fn.getClass() != _String.class)
            throw new Exception("Argument must be string");

        File file = new File(fn.rawValue());
        StringBuilder buffer = new StringBuilder();

        if (!file.exists() || !file.canRead())
            throw new Exception("Failed to load script");

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = bufferedReader.readLine()) != null)
            buffer.append(line).append("\n");

        bufferedReader.close();

        Run.run(buffer.toString(), true);
        return new RTResult().success(new _None());
    }
}
