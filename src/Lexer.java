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
            else if (currentChar == '#')
            {
                skipComment();
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
            else if (currentChar == '"' || currentChar == '\'')
            {
                tokens.add(makeString());
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
                tokens.add(makeMul());
            }
            else if (currentChar == '/')
            {
                tokens.add(makeDiv());
            }
            else if (currentChar == '%')
            {
                tokens.add(new Token(TokenType.MOD));
                advance();
            }
            else if (currentChar == '&')
            {
                tokens.add(new Token(TokenType.BITAND));
                advance();
            }
            else if (currentChar == '|')
            {
                tokens.add(new Token(TokenType.BITOR));
                advance();
            }
            else if (currentChar == '~')
            {
                tokens.add(new Token(TokenType.BITNOT));
                advance();
            }
            else if (currentChar == '^')
            {
                tokens.add(new Token(TokenType.XOR));
                advance();
            }
            else if (currentChar == '=')
            {
                tokens.add(makeEquals());
            }
            else if (currentChar == '!')
            {
                tokens.add(makeNotEquals());
            }
            else if (currentChar == '<')
            {
                tokens.add(makeLessThan());
            }
            else if (currentChar == '>')
            {
                tokens.add(makeGreaterThan());
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

    private void skipComment()
    {
        advance();

        while (currentChar != null && currentChar != '\n')
            advance();

        advance();
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

    private Token makeEquals()
    {
        TokenType type = TokenType.EQ;
        advance();

        if (currentChar != null && currentChar == '=')
        {
            type = TokenType.EE;
            advance();
        }

        return new Token(type);
    }

    private Token makeNotEquals() throws Exception
    {
        advance();

        if (currentChar != null && currentChar == '=')
        {
            advance();
            return new Token(TokenType.NE);
        }

        throw new Exception("Illegal character: '!'");
    }

    private Token makeLessThan()
    {
        TokenType type = TokenType.LT;
        advance();

        if (currentChar != null)
        {
            if (currentChar == '=')
            {
                type = TokenType.LTE;
                advance();
            }
            else if (currentChar == '<')
            {
                type = TokenType.LSHIFT;
                advance();
            }
        }

        return new Token(type);
    }

    private Token makeGreaterThan()
    {
        TokenType type = TokenType.GT;
        advance();

        if (currentChar != null)
        {
            if (currentChar == '=')
            {
                type = TokenType.GTE;
                advance();
            }
            else if (currentChar == '>')
            {
                type = TokenType.RSHIFT;
                advance();
            }
        }

        return new Token(type);
    }

    private Token makeString() throws Exception
    {
        String str = "";
        char invertedComma = currentChar;

        advance();

        while (currentChar != null)
        {
            if (currentChar == '\\')
            {
                advance();

                if (currentChar == null)
                    break;

                if (currentChar == 'n')
                    str += '\n';
                else if (currentChar == 't')
                    str += '\t';
                else if (currentChar == '\'')
                    str += '\'';
                else if (currentChar == '\"')
                    str += '\"';
                else if (currentChar == '\\')
                    str += '\\';
                else
                    str += "\\" + currentChar;

                advance();
                continue;
            }
            else if (currentChar == invertedComma)
            {
                advance();
                return new Token(TokenType.STRING, str);
            }

            str += currentChar;
            advance();
        }

        throw new Exception("SyntaxError: unterminated string literal");
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

    private Token makeMul()
    {
        TokenType type = TokenType.MUL;
        advance();

        if (currentChar != null && currentChar == '*')
        {
            type = TokenType.POW;
            advance();
        }

        return new Token(type);
    }

    private Token makeDiv()
    {
        TokenType type = TokenType.DIV;
        advance();

        if (currentChar != null && currentChar == '/')
        {
            type = TokenType.INTDIV;
            advance();
        }

        return new Token(type);
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
