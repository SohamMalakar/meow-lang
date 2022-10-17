package src;

public class Context
{
    public Context parent;
    public SymbolTable symbolTable;

    public Context(Context parent)
    {
        this.parent = parent;
        symbolTable = null;
    }
}
