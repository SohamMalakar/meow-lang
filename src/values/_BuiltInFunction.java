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

    public ArrayList<String> param_chr(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_chr(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        var value = execCtx.symbolTable.get("value");

        if (!value.type().equals("int"))
            throw new Exception("chr takes first argument as int");

        Character temp = (char)Integer.parseInt(value.value());
        return new RTResult().success(new _String(temp.toString()));
    }

    public ArrayList<String> param_ord(ArrayList<_Value> args)
    {
        return new ArrayList<String>(Arrays.asList("value"));
    }

    public RTResult execute_ord(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("value");

        if (!value.type().equals("str"))
            throw new Exception("TypeError: ord() expected string of length 1, but " + value.type() + " found");

        if (value.size() != 1)
            throw new Exception("TypeError: ord() expected a character, but string of length " + value.size() +
                                " found");

        return new RTResult().success(new _Number("int", String.valueOf((int)value.rawValue().charAt(0))));
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

    // for testing purpose only
    public ArrayList<String> param_update(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("collection", "val0", "val1"));
    }

    // for testing purpose only
    public RTResult execute_update(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("collection");

        if (!value.type().equals("dict") && !value.type().equals("list") && !value.type().equals("str"))
            throw new Exception("update takes first argument as dict, list or str");

        _Value val0 = execCtx.symbolTable.get("val0");
        _Value val1 = execCtx.symbolTable.get("val1");

        return new RTResult().success(value.update(val0, val1));
    }

    public ArrayList<String> param_fopen(ArrayList<_Value> args) throws Exception
    {
        ArrayList<String> params;

        if (args.size() == 1)
            params = new ArrayList<>(Arrays.asList("filename"));
        else if (args.size() == 2)
            params = new ArrayList<>(Arrays.asList("filename", "mode"));
        else if (args.size() == 3)
            params = new ArrayList<>(Arrays.asList("filename", "mode", "buffering"));
        else if (args.size() == 4)
            params = new ArrayList<>(Arrays.asList("filename", "mode", "buffering", "encoding"));
        else
            throw new Exception("fopen expected 1 to 4 arguments, got " + args.size());

        return params;
    }

    public RTResult execute_fopen(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        String filename;
        _Value value = execCtx.symbolTable.get("filename");

        if (!value.type().equals("str"))
            throw new Exception("fopen takes first argument as str");

        filename = value.rawValue();

        String mode = "r";

        if (args.size() > 1)
        {
            value = execCtx.symbolTable.get("mode");

            if (!value.type().equals("str"))
                throw new Exception("fopen takes second argument as str");

            mode = value.rawValue();
        }

        int buffering = 4096;

        if (args.size() > 2)
        {
            value = execCtx.symbolTable.get("buffering");

            if (!value.type().equals("int"))
                throw new Exception("fopen takes third argument as int");

            buffering = Integer.parseInt(value.value());
        }

        String encoding = "utf-8";

        if (args.size() > 3)
        {
            value = execCtx.symbolTable.get("encoding");

            if (!value.type().equals("str"))
                throw new Exception("fopen takes fourth argument as str");

            encoding = value.rawValue();
        }

        _File file = new _File(filename, mode, buffering, encoding);

        if (file.getRandomAccessFile() == null)
            return new RTResult().success(new _None());

        return new RTResult().success(file);
    }

    public ArrayList<String> param_fread(ArrayList<_Value> args) throws Exception
    {
        ArrayList<String> params = new ArrayList<String>();

        if (args.size() < 1 || args.size() > 2)
            throw new Exception("fread expected either 1 or 2 arguments, got " + args.size());
        else if (args.size() == 1)
            params = new ArrayList<>(Arrays.asList("file"));
        else
            params = new ArrayList<>(Arrays.asList("file", "size"));

        return params;
    }

    public RTResult execute_fread(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _File file;
        _Value value;

        value = execCtx.symbolTable.get("file");

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("fread takes first argument as file");

        long bufferSize = file.size() - file.getPointer();

        if (args.size() == 2)
        {
            value = execCtx.symbolTable.get("size");

            if (!value.type().equals("int"))
                throw new Exception("fread takes second argument as int");

            bufferSize = Integer.parseInt(value.value());
        }

        byte[] buffer = new byte[(int)bufferSize];
        file.read(buffer);

        return new RTResult().success(new _String(new String(buffer)));
    }

    public ArrayList<String> param_freadline(ArrayList<_Value> args) throws Exception
    {
        return new ArrayList<String>(Arrays.asList("file"));
    }

    public RTResult execute_freadline(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("freadline takes first argument as file");

        return new RTResult().success(new _String(file.readline()));
    }

    public ArrayList<String> param_freadlines(ArrayList<_Value> args) throws Exception
    {
        return new ArrayList<String>(Arrays.asList("file"));
    }

    public RTResult execute_freadlines(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("freadlines takes first argument as file");

        return new RTResult().success(new _List(file.readlines()));
    }

    public ArrayList<String> param_fwrite(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file", "buffer"));
    }

    public RTResult execute_fwrite(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _File file;
        _Value value;

        value = execCtx.symbolTable.get("file");

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("fwrite takes first argument as file");

        value = execCtx.symbolTable.get("buffer");

        String buffer;

        if (value.type().equals("str"))
            buffer = value.rawValue();
        else
            throw new Exception("fwrite takes second argument as str");

        file.write(buffer.getBytes());
        return new RTResult().success(new _Number("int", String.valueOf(buffer.length())));
    }

    public ArrayList<String> param_fwritelines(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file", "list"));
    }

    public RTResult execute_fwritelines(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;
        _List list;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("fwritelines takes first argument as file");

        value = execCtx.symbolTable.get("list");

        if (value.type().equals("list"))
            list = (_List)value;
        else
            throw new Exception("fwritelines takes second argument as list");

        file.writelines(list);
        return new RTResult().success(new _None());
    }

    public ArrayList<String> param_fseek(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file", "offset", "position"));
    }

    public RTResult execute_fseek(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value;
        _File file;
        int offset;
        int position;

        value = execCtx.symbolTable.get("file");

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("fseek takes first argument as file");

        value = execCtx.symbolTable.get("offset");

        if (value.type().equals("int"))
            offset = Integer.parseInt(value.value());
        else
            throw new Exception("fseek takes second argument as int");

        value = execCtx.symbolTable.get("position");

        if (value.type().equals("int"))
            position = Integer.parseInt(value.value());
        else
            throw new Exception("fseek takes third argument as int");

        long pointer = file.seek(offset, position);
        return new RTResult().success(new _Number("int", String.valueOf(pointer)));
    }

    public ArrayList<String> param_ftell(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_ftell(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("ftell takes first argument as file");

        return new RTResult().success(new _Number("int", String.valueOf(file.seek(0, 1))));
    }

    public ArrayList<String> param_flen(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_flen(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("flen takes first argument as file");

        return new RTResult().success(new _Number("int", String.valueOf(file.size())));
    }

    public ArrayList<String> param_frewind(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_frewind(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("frewind takes first argument as file");

        return new RTResult().success(new _Number("int", String.valueOf(file.seek(0, 0))));
    }

    public ArrayList<String> param_feof(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_feof(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("feof takes first argument as file");

        return new RTResult().success(new _Bool(String.valueOf(file.getPointer() == file.size())));
    }

    public ArrayList<String> param_freadable(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_freadable(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("freadable takes first argument as file");

        return new RTResult().success(new _Bool(String.valueOf(file.isReadable())));
    }

    public ArrayList<String> param_fwritable(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_fwritable(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");
        _File file;

        if (value.type().equals("file"))
            file = (_File)value;
        else
            throw new Exception("fwritable takes first argument as file");

        return new RTResult().success(new _Bool(String.valueOf(file.isWritable())));
    }

    public ArrayList<String> param_fflush(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_fflush(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");

        if (!value.type().equals("file"))
            throw new Exception("fflush takes first argument as file");

        ((_File)value).flush();
        return new RTResult().success(new _None());
    }

    public ArrayList<String> param_fclosed(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_fclosed(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");

        if (!value.type().equals("file"))
            throw new Exception("fclosed takes first argument as file");

        return new RTResult().success(new _Bool(String.valueOf(((_File)value).isClosed())));
    }

    public ArrayList<String> param_fclose(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("file"));
    }

    public RTResult execute_fclose(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("file");

        if (!value.type().equals("file"))
            throw new Exception("fclose takes first argument as file");

        ((_File)value).close();
        return new RTResult().success(new _None());
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

    /* public ArrayList<String> param_sys(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("value"));
    }

    public RTResult execute_sys(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        // TODO: implement this
        return new RTResult().success(new _None());
    } */

    public ArrayList<String> param_exit(ArrayList<_Value> args)
    {
        return new ArrayList<>(Arrays.asList("value"));
    }

    public RTResult execute_exit(Context execCtx, ArrayList<_Value> args) throws Exception
    {
        _Value value = execCtx.symbolTable.get("value");

        if (!value.type().equals("int"))
            throw new Exception("exit takes first argument as int");

        System.exit(Integer.parseInt(value.value()));
        return new RTResult().success(new _None());
    }
}
