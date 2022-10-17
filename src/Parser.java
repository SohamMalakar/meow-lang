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
import src.utils.Pair;

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
        boolean moreStatements = true;

        while (true)
        {
            newlineCount = 0;

            while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            {
                advance();
                newlineCount++;
            }

            if (newlineCount == 0)
                moreStatements = false;

            if (!moreStatements)
                break;

            int checkPoint = position; // i know it's spaghetti, so don't worry

            try
            {
                statement = expr();
            }
            catch (Exception e)
            {
                position = checkPoint;
                currentToken = position < tokens.size() ? tokens.get(position) : null;
                moreStatements = false;
                continue;
            }

            statements.add(statement);
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
        Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> allCases = ifExprCases("if");
        ArrayList<Pair<Pair<Node, Node>, Boolean>> cases = allCases.key;
        Pair<Node, Boolean> elseCase = allCases.value;
        return new IfNode(cases, elseCase);
    }

    private Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> ifExprB() throws Exception
    {
        return ifExprCases("elif");
    }

    private Pair<Node, Boolean> ifExprC() throws Exception
    {
        Pair<Node, Boolean> elseCase = null;

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "else"))
        {
            advance();

            if (currentToken != null && currentToken.type == TokenType.NEWLINE)
            {
                advance();
                Node statements = statements();
                elseCase = new Pair<Node, Boolean>(statements, true);

                if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "end"))
                    advance();
                else
                    throw new Exception("Expected 'end'");
            }
            else
            {
                Node expr = expr();
                elseCase = new Pair<Node, Boolean>(expr, false);
            }
        }

        return elseCase;
    }

    private Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> ifExprBOrC() throws Exception
    {
        ArrayList<Pair<Pair<Node, Node>, Boolean>> cases = new ArrayList<Pair<Pair<Node, Node>, Boolean>>();
        Pair<Node, Boolean> elseCase = null;

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "elif"))
        {
            Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> allCases = ifExprB();
            cases = allCases.key;
            elseCase = allCases.value;
        }
        else
        {
            elseCase = ifExprC();
        }

        return new Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>>(cases, elseCase);
    }

    private Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> ifExprCases(String caseKeyword)
        throws Exception
    {
        ArrayList<Pair<Pair<Node, Node>, Boolean>> cases = new ArrayList<Pair<Pair<Node, Node>, Boolean>>();
        Pair<Node, Boolean> elseCase = null;

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, caseKeyword))
            throw new Exception("Expected '" + caseKeyword + "'");

        advance();
        Node condition = expr();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "then"))
            throw new Exception("Expected 'then'");

        advance();

        if (currentToken != null && currentToken.type == TokenType.NEWLINE)
        {
            advance();
            Node statements = statements();
            cases.add(new Pair<Pair<Node, Node>, Boolean>(new Pair<Node, Node>(condition, statements), true));

            if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "end"))
            {
                advance();
            }
            else
            {
                Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> allCases = ifExprBOrC();
                ArrayList<Pair<Pair<Node, Node>, Boolean>> newCases = allCases.key;
                elseCase = allCases.value;

                for (var newCase : newCases)
                    cases.add(newCase);
            }
        }
        else
        {
            Node expr = expr();
            cases.add(new Pair<Pair<Node, Node>, Boolean>(new Pair<Node, Node>(condition, expr), false));

            Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> allCases = ifExprBOrC();
            ArrayList<Pair<Pair<Node, Node>, Boolean>> newCases = allCases.key;
            elseCase = allCases.value;

            for (var newCase : newCases)
                cases.add(newCase);
        }

        return new Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>>(cases, elseCase);
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

        if (currentToken != null && currentToken.type == TokenType.NEWLINE)
        {
            advance();
            var body = statements();

            if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "end"))
                throw new Exception("Expected 'end'");

            advance();

            return new WhileNode(condition, body, true);
        }

        Node body = expr();

        return new WhileNode(condition, body, false);
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

        if (currentToken != null && currentToken.type == TokenType.ARROW)
        {
            advance();
            Node body = expr();
            return new FuncDefNode(varName, args, body, false);
        }

        if (currentToken == null || currentToken.type != TokenType.NEWLINE)
            throw new Exception("Expected '->' or NEWLINE");

        advance();
        var body = statements();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "end"))
            throw new Exception("Expected 'end'");

        advance();
        return new FuncDefNode(varName, args, body, true);
    }
}
