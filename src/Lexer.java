package src;

import java.util.ArrayList;

public class Lexer
{
    private String text;
    private int position;
    private Character currentChar;

    public Lexer(String text)
    {
        this.text = text;
        position = -1;
        currentChar = null;
        advance();
    }

    private void advance()
    {
        currentChar = ++position < text.length() ? text.charAt(position) : null;
    }

    public ArrayList<Token> makeTokens() throws Exception
    {
        ArrayList<Token> tokens = new ArrayList<Token>();

        while (currentChar != null)
        {
            if (currentChar == ' ' || currentChar == '\t')
            {
                advance();
            }
            else if (currentChar == ';' || currentChar == '\n')
            {
                tokens.add(new Token(TokenType.NEWLINE));
                advance();
            }
            else if (Character.isDigit(currentChar) || currentChar == '.')
            {
                tokens.add(makeNumber());
            }
            else if (Character.isLetter(currentChar) || currentChar == '_')
            {
                tokens.add(makeIdentifier());
            }
            else if (currentChar == '+')
            {
                tokens.add(new Token(TokenType.PLUS));
                advance();
            }
            else if (currentChar == '-')
            {
                tokens.add(makeMinusOrArrow());
            }
            else if (currentChar == '*')
            {
                tokens.add(new Token(TokenType.MUL));
                advance();
            }
            else if (currentChar == '/')
            {
                tokens.add(new Token(TokenType.DIV));
                advance();
            }
            else if (currentChar == '=')
            {
                tokens.add(new Token(TokenType.EQ));
                advance();
            }
            else if (currentChar == ':')
            {
                tokens.add(makeAssignment());
            }
            else if (currentChar == '(')
            {
                tokens.add(new Token(TokenType.LPAREN));
                advance();
            }
            else if (currentChar == ')')
            {
                tokens.add(new Token(TokenType.RPAREN));
                advance();
            }
            else if (currentChar == '[')
            {
                tokens.add(new Token(TokenType.LSQUARE));
                advance();
            }
            else if (currentChar == ']')
            {
                tokens.add(new Token(TokenType.RSQUARE));
                advance();
            }
            else if (currentChar == ',')
            {
                tokens.add(new Token(TokenType.COMMA));
                advance();
            }
            else
            {
                throw new Exception("Illegal character: '" + currentChar + "'");
            }
        }

        return tokens;
    }

    private Token makeAssignment() throws Exception
    {
        advance();

        if (currentChar != null && currentChar == '=')
        {
            advance();
            return new Token(TokenType.ASSIGN);
        }

        throw new Exception("Illegal character: ':'");
    }

    private Token makeMinusOrArrow()
    {
        TokenType type = TokenType.MINUS;
        advance();

        if (currentChar != null && currentChar == '>')
        {
            type = TokenType.ARROW;
            advance();
        }

        return new Token(type);
    }

    private Token makeIdentifier()
    {
        String idStr = "";

        while (currentChar != null && (Character.isLetterOrDigit(currentChar) || currentChar == '_'))
        {
            idStr += currentChar;
            advance();
        }

        return new Token(Keywords.values.contains(idStr) ? TokenType.KEYWORD : TokenType.IDENTIFIER, idStr);
    }

    private Token makeNumber() throws Exception
    {
        String numStr = "";
        int dotCount = 0;

        while (currentChar != null && (Character.isDigit(currentChar) || currentChar == '.'))
        {
            if (currentChar == '.')
            {
                if (dotCount == 1)
                    break;

                dotCount++;
            }

            numStr += currentChar;
            advance();
        }

        if (numStr.equals("."))
            throw new Exception("Illegal character: '.'");

        return new Token(dotCount == 0 ? TokenType.INT : TokenType.FLOAT, numStr);
    }
}
