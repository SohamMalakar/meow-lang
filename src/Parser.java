package src;

import java.util.ArrayList;

import src.nodes.AlternateAssignNode;
import src.nodes.BinOpNode;
import src.nodes.BoolNode;
import src.nodes.CallNode;
import src.nodes.FuncDefNode;
import src.nodes.IfNode;
import src.nodes.ListNode;
import src.nodes.Node;
import src.nodes.NoneTypeNode;
import src.nodes.NumberNode;
import src.nodes.SubscriptableNode;
import src.nodes.UnaryOpNode;
import src.nodes.VarAccessNode;
import src.nodes.VarAssignNode;
import src.nodes.WhileNode;

public class Parser
{
    private ArrayList<Token> tokens;
    private int position;
    private Token currentToken;

    public Parser(ArrayList<Token> tokens)
    {
        this.tokens = tokens;
        position = -1;
        currentToken = null;
        advance();
    }

    private void advance()
    {
        currentToken = ++position < tokens.size() ? tokens.get(position) : null;
    }

    private void backtrack()
    {
        currentToken = --position >= 0 ? tokens.get(position) : null;
    }

    public Node parse() throws Exception
    {
        Node ast = statements();

        if (currentToken != null && currentToken.type != TokenType.EOF)
            throw new Exception("SyntaxError: expected a newline or EOF");

        return ast;
    }

    private Node statements() throws Exception
    {
        ArrayList<Node> statements = new ArrayList<Node>();

        while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            advance();

        Node statement = expr();
        statements.add(statement);

        int newlineCount;

        while (true)
        {
            newlineCount = 0;

            while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            {
                advance();
                newlineCount++;
            }

            if (newlineCount == 0)
                break;

            if (currentToken != null)
            {
                statement = expr();
                statements.add(statement);
            }
        }

        while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            advance();

        return new ListNode(statements);
    }

    private Node expr() throws Exception
    {
        if (currentToken != null && currentToken.type == TokenType.IDENTIFIER)
        {
            Token token = currentToken;
            advance();

            if (currentToken != null && currentToken.type == TokenType.EQ)
            {
                advance();
                return new AlternateAssignNode(token, expr());
            }
            if (currentToken != null && currentToken.type == TokenType.ASSIGN)
            {
                advance();
                return new VarAssignNode(token, expr());
            }
            else
            {
                backtrack();
            }
        }

        Node left = term();

        while (currentToken != null && (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS))
        {
            Token token = currentToken;
            advance();
            Node right = term();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node term() throws Exception
    {
        Node left = subscript();

        while (currentToken != null && (currentToken.type == TokenType.MUL || currentToken.type == TokenType.DIV))
        {
            Token token = currentToken;
            advance();
            Node right = subscript();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node subscript() throws Exception
    {
        Node factor = call();

        while (currentToken != null && currentToken.type == TokenType.LSQUARE)
        {
            advance();
            Node expr = expr();

            if (currentToken == null || currentToken.type != TokenType.RSQUARE)
                throw new Exception("Expected ']'");

            factor = new SubscriptableNode(factor, expr);
            advance();
        }

        return factor;
    }

    private Node call() throws Exception
    {
        Node factor = factor();

        while (currentToken != null && currentToken.type == TokenType.LPAREN)
        {
            advance();
            ArrayList<Node> args = new ArrayList<Node>();

            if (currentToken != null && currentToken.type == TokenType.RPAREN)
            {
                advance();
            }
            else
            {
                args.add(expr());

                while (currentToken != null && currentToken.type == TokenType.COMMA)
                {
                    advance();
                    args.add(expr());
                }

                if (currentToken == null || currentToken.type != TokenType.RPAREN)
                    throw new Exception("Expected ',' or ')'");

                advance();
            }

            factor = new CallNode(factor, args);
        }

        return factor;
    }

    private Node factor() throws Exception
    {
        if (currentToken != null)
        {
            if (currentToken.type == TokenType.INT || currentToken.type == TokenType.FLOAT)
            {
                Token token = currentToken;
                advance();
                return new NumberNode(token);
            }
            // else if (currentToken.type == TokenType.BOOL)
            else if (currentToken.matches(TokenType.KEYWORD, "true") ||
                     currentToken.matches(TokenType.KEYWORD, "false"))
            {
                Token token = currentToken;
                advance();
                return new BoolNode(token);
            }
            else if (currentToken.matches(TokenType.KEYWORD, "none"))
            {
                advance();
                return new NoneTypeNode();
            }
            else if (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS)
            {
                Token token = currentToken;
                advance();
                Node factor = factor();
                return new UnaryOpNode(token, factor);
            }
            else if (currentToken.type == TokenType.IDENTIFIER)
            {
                Token token = currentToken;
                advance();
                return new VarAccessNode(token);
            }
            else if (currentToken.type == TokenType.LPAREN)
            {
                advance();
                Node expr = expr();

                if (currentToken != null && currentToken.type == TokenType.RPAREN)
                {
                    advance();
                    return expr;
                }

                throw new Exception("SyntaxError: expected a right parenthesis");
            }
            else if (currentToken.type == TokenType.LSQUARE)
            {
                return listExpr();
            }
            else if (currentToken.matches(TokenType.KEYWORD, "if"))
            {
                return ifExpr();
            }
            else if (currentToken.matches(TokenType.KEYWORD, "while"))
            {
                return whileExpr();
            }
            else if (currentToken.matches(TokenType.KEYWORD, "def"))
            {
                return funcDef();
            }
        }

        throw new Exception("SyntaxError: expected an int, float, plus, minus or left parenthesis");
    }

    private Node listExpr() throws Exception
    {
        ArrayList<Node> elementNodes = new ArrayList<>();

        if (currentToken == null || currentToken.type != TokenType.LSQUARE)
            throw new Exception("Expected '['");

        advance();

        if (currentToken != null && currentToken.type == TokenType.RSQUARE)
        {
            advance();
        }
        else
        {
            elementNodes.add(expr());

            while (currentToken != null && currentToken.type == TokenType.COMMA)
            {
                advance();
                elementNodes.add(expr());
            }

            if (currentToken.type == null || currentToken.type != TokenType.RSQUARE)
                throw new Exception("Expected ',' or ']'");

            advance();
        }

        return new ListNode(elementNodes);
    }

    private Node ifExpr() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "if"))
            throw new Exception("Expected 'if'");

        advance();
        Node condition = expr();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "then"))
            throw new Exception("Expected 'then'");

        advance();
        Node expr = expr();
        ArrayList<Pair<Node, Node>> cases = new ArrayList<Pair<Node, Node>>();
        Node elseCase = null;
        cases.add(new Pair<Node, Node>(condition, expr));

        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, "elif"))
        {
            advance();
            condition = expr();

            if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "then"))
                throw new Exception("Expected 'then'");

            advance();
            expr = expr();
            cases.add(new Pair<Node, Node>(condition, expr));
        }

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "else"))
        {
            advance();
            elseCase = expr();
        }

        return new IfNode(cases, elseCase);
    }

    private Node whileExpr() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "while"))
            throw new Exception("Expected 'while'");

        advance();
        Node condition = expr();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "then"))
            throw new Exception("Expected 'then'");

        advance();
        Node body = expr();

        return new WhileNode(condition, body);
    }

    private Node funcDef() throws Exception
    {
        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "def"))
            throw new Exception("Expected 'def'");

        advance();
        Token varName;

        if (currentToken != null && currentToken.type == TokenType.IDENTIFIER)
        {
            varName = currentToken;
            advance();

            if (currentToken == null || currentToken.type != TokenType.LPAREN)
                throw new Exception("Expected '('");
        }
        else
        {
            varName = null;

            if (currentToken == null || currentToken.type != TokenType.LPAREN)
                throw new Exception("Expected identifier or '('");
        }

        advance();
        ArrayList<Token> args = new ArrayList<>();

        if (currentToken != null && currentToken.type == TokenType.IDENTIFIER)
        {
            args.add(currentToken);
            advance();

            while (currentToken != null && currentToken.type == TokenType.COMMA)
            {
                advance();

                if (currentToken == null || currentToken.type != TokenType.IDENTIFIER)
                    throw new Exception("Expected identifier");

                args.add(currentToken);
                advance();
            }

            if (currentToken == null || currentToken.type != TokenType.RPAREN)
                throw new Exception("Expected ',' or ')'");
        }
        else
        {
            if (currentToken == null || currentToken.type != TokenType.RPAREN)
                throw new Exception("Expected identifier or ')'");
        }

        advance();

        if (currentToken == null || currentToken.type != TokenType.ARROW)
            throw new Exception("Expected '->'");

        advance();
        Node body = expr();

        return new FuncDefNode(varName, args, body);
    }
}
