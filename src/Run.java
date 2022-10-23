package src;

import java.util.ArrayList;
import src.nodes.Node;
import src.values._BuiltInFunction;
import src.values._Value;

public class Run
{
    private static SymbolTable symbolTable = new SymbolTable(null);

    static
    {
        symbolTable = new SymbolTable(null);
        symbolTable.set("print", new _BuiltInFunction("print"));
        symbolTable.set("str", new _BuiltInFunction("str"));
    }

    public static void run(String text) throws Exception
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
        _Value result = interpreter.visit(ast, context);

        System.out.println(result.value());
    }
}
