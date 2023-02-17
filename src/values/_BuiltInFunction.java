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

        Method method = getClass().getDeclaredMethod(methodName, executeCtx.getClass());

        String paramMethod = "param_" + name;
        Method param = getClass().getDeclaredMethod(paramMethod);

        @SuppressWarnings("unchecked") ArrayList<String> argNames = (ArrayList<String>)param.invoke(this);

        res.register(checkAndPopulateArgs(argNames, args, executeCtx));

        if (res.shouldReturn())
            return res;

        _Value returnValue = res.register((RTResult)method.invoke(this, executeCtx));

        if (res.shouldReturn())
            return res;
        return res.success(returnValue);
    }

    public ArrayList<String> param_print()
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_print(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        System.out.print(value.rawValue());
        return new RTResult().success(new _None());
    }

    public ArrayList<String> param_input()
    {
        return new ArrayList<String>();
    }

    public RTResult execute_input(Context execCtx) throws Exception
    {
        String str = Input.nextLine();
        return new RTResult().success(new _String(str));
    }

    public ArrayList<String> param_str()
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_str(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _String(value.rawValue()));
    }

    public ArrayList<String> param_len()
    {
        return new ArrayList<String>(Arrays.asList("list"));
    }

    public RTResult execute_len(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("list");
        return new RTResult().success(new _Number("int", String.valueOf(value.size())));
    }

    public ArrayList<String> param_int()
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_int(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Number("int", String.valueOf(Integer.parseInt(value.rawValue()))));
    }

    public ArrayList<String> param_float()
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_float(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Number("float", String.valueOf(Double.parseDouble(value.rawValue()))));
    }

    public ArrayList<String> param_bool()
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_bool(Context execCtx) throws Exception
    {
        var value = execCtx.symbolTable.get("value");
        return new RTResult().success(new _Bool(String.valueOf(value.isTrue())));
    }

    public ArrayList<String> param_run()
    {
        return new ArrayList<String>(Arrays.asList("fn"));
    }

    public RTResult execute_run(Context execCtx) throws Exception
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
