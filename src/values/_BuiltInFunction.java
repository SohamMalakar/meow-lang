package src.values;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import src.Context;
import src.RTResult;
import src.Run;
import src.utils.Input;

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

        Method method = getClass().getDeclaredMethod(methodName, executeCtx.getClass(), args.getClass());

        String paramMethod = "param_" + name;
        Method param = getClass().getDeclaredMethod(paramMethod, args.getClass());

        @SuppressWarnings("unchecked") ArrayList<String> argNames = (ArrayList<String>)param.invoke(this, args);

        res.register(checkAndPopulateArgs(argNames, args, executeCtx));

        if (res.shouldReturn())
            return res;

        _Value returnValue = res.register((RTResult)method.invoke(this, executeCtx, args));

        if (res.shouldReturn())
            return res;
        return res.success(returnValue);
    }

    public ArrayList<String> param_print(ArrayList<_Value> args) throws Exception
    {
        ArrayList<String> params = new ArrayList<String>();

        for (int i = 0; i < args.size(); i++)
            params.add("value" + i);

        return params;
    }

    public RTResult execute_print(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value;

        for (int i = 0; i < args.size(); i++)
        {
            value = execCtx.symbolTable.get("value" + i);
            System.out.print(value.rawValue());
        }

        return new RTResult().success(new _None());
    }

    public ArrayList<String> param_input(ArrayList<_Value> args) throws Exception
    {
        ArrayList<String> params = new ArrayList<String>();

        if (args.size() > 1)
            throw new Exception("input expected at most 1 argument, got " + args.size());
        else if (args.size() == 1)
            params.add("value");

        return params;
    }

    public RTResult execute_input(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        if (args.size() == 1)
        {
            _Value value = execCtx.symbolTable.get("value");
            System.out.print(value.rawValue());
        }

        String str = Input.nextLine();
        return new RTResult().success(new _String(str));
    }

    public ArrayList<String> param_str(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_str(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _String(value.rawValue()));
    }

    public ArrayList<String> param_len(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("list"));
    }

    public RTResult execute_len(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("list");
        return new RTResult().success(new _Number("int", String.valueOf(value.size())));
    }

    public ArrayList<String> param_int(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_int(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Number("int", String.valueOf(Integer.parseInt(value.rawValue()))));
    }

    public ArrayList<String> param_float(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_float(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Number("float", String.valueOf(Double.parseDouble(value.rawValue()))));
    }

    public ArrayList<String> param_bool(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_bool(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Bool(String.valueOf(value.isTrue())));
    }

    public ArrayList<String> param_run(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("fn"));
    }

    public RTResult execute_run(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var fn = execCtx.symbolTable.get("fn");

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
