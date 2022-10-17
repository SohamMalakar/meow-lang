package src;

public class Token
{
    public TokenType type;
    public String value;

    public Token(TokenType type)
    {
        this(type, null);
    }

    public Token(TokenType type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public String repr()
    {
        return type + (value != null ? ": " + value : "");
    }

    public boolean matches(TokenType type, String value)
    {
        return this.type == type && this.value.equals(value);
    }
}
