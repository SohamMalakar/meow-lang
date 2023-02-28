package src;

import java.util.ArrayList;
import src.nodes.Node;
import src.values._BuiltInFunction;
import src.values._List;
import src.values._Number;
import src.values._String;
import src.values._Value;

public class Run
{
    private static SymbolTable symbolTable = new SymbolTable(null);

    static
    {
        symbolTable.set("print", new _BuiltInFunction("print"));
        symbolTable.set("input", new _BuiltInFunction("input"));
        symbolTable.set("len", new _BuiltInFunction("len"));
        symbolTable.set("int", new _BuiltInFunction("int"));
        symbolTable.set("float", new _BuiltInFunction("float"));
        symbolTable.set("bool", new _BuiltInFunction("bool"));
        symbolTable.set("str", new _BuiltInFunction("str"));
        symbolTable.set("chr", new _BuiltInFunction("chr"));
        symbolTable.set("ord", new _BuiltInFunction("ord"));
        symbolTable.set("update", new _BuiltInFunction("update"));
        symbolTable.set("run", new _BuiltInFunction("run"));
        // symbolTable.set("sys", new _BuiltInFunction("sys"));
        symbolTable.set("exit", new _BuiltInFunction("exit"));

        // command line arguments
        ArrayList<_Value> argv = new ArrayList<_Value>();

        for (var arg : Arguments.values)
            argv.add(new _String(arg));

        symbolTable.set("args", new _List(argv));
    }

    public static void run(String text, boolean noEcho) throws Exception
    {
        Lexer lexer = new Lexer(text);
        ArrayList<Token> tokens = lexer.makeTokens();

        // for (int i = 0; i < tokens.size(); i++)
        //     System.out.print(tokens.get(i).repr() + (i != tokens.size() - 1 ? ", " : ""));

        // System.out.println();

        Parser parser = new Parser(tokens);
        Node ast = parser.parse();

        // System.out.println(ast.repr());

        Interpreter interpreter = new Interpreter();
        Context context = new Context(null);
        context.symbolTable = symbolTable;
        _Value result = new RTResult().register(interpreter.visit(ast, context));

        if (!noEcho && result != null)
            prettyPrint((_List)result);
    }

    private static void prettyPrint(_List result) throws Exception
    {
        if (result.size() == 1)
        {
            var index = new _Number("int", "0");
            var res = result.get(index);

            if (!res.type().equals("NoneType"))
                System.out.println(res.value());
        }
        else
        {
            for (int i = 0; i < result.size(); i++)
                System.out.println(result.get(new _Number("int", String.valueOf(i))).value());
        }
    }
}
