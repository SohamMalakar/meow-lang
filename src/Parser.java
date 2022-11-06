package src;

import java.util.ArrayList;
import src.nodes.AlternateAssignNode;
import src.nodes.BinOpNode;
import src.nodes.BoolNode;
import src.nodes.BreakNode;
import src.nodes.CallNode;
import src.nodes.ContinueNode;
import src.nodes.FuncDefNode;
import src.nodes.IfNode;
import src.nodes.ListNode;
import src.nodes.Node;
import src.nodes.NoneTypeNode;
import src.nodes.NumberNode;
import src.nodes.ReturnNode;
import src.nodes.StringNode;
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

        if (currentToken != null)
            throw new Exception("SyntaxError: expected a newline or EOF");

        return ast;
    }

    private Node statements() throws Exception
    {
        ArrayList<Node> statements = new ArrayList<Node>();

        while (currentToken != null && currentToken.type == TokenType.NEWLINE)
            advance();

        Node statement = statement();
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
                statement = statement();
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

    private Node statement() throws Exception
    {
        if (currentToken != null)
        {
            if (currentToken.matches(TokenType.KEYWORD, "return"))
            {
                advance();
                Node expr = null;
                int checkPoint = position;

                try
                {
                    expr = expr();
                }
                catch (Exception e)
                {
                    currentToken = (position = checkPoint) < tokens.size() ? tokens.get(position) : null;
                }

                return new ReturnNode(expr);
            }
            else if (currentToken.matches(TokenType.KEYWORD, "break"))
            {
                advance();
                return new BreakNode();
            }
            else if (currentToken.matches(TokenType.KEYWORD, "continue"))
            {
                advance();
                return new ContinueNode();
            }
        }

        return expr();
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

        return orExpr();
    }

    private Node orExpr() throws Exception
    {
        Node left = andExpr();

        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, "or"))
        {
            Token token = currentToken;
            advance();
            Node right = andExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node andExpr() throws Exception
    {
        Node left = compExpr();

        while (currentToken != null && currentToken.matches(TokenType.KEYWORD, "and"))
        {
            Token token = currentToken;
            advance();
            Node right = compExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node compExpr() throws Exception
    {
        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "not"))
        {
            Token token = currentToken;
            advance();
            Node compExpr = compExpr();
            return new UnaryOpNode(token, compExpr);
        }

        Node left = bitOrExpr();

        while (currentToken != null && (currentToken.type == TokenType.EE || currentToken.type == TokenType.NE ||
                                        currentToken.type == TokenType.LT || currentToken.type == TokenType.GT ||
                                        currentToken.type == TokenType.LTE || currentToken.type == TokenType.GTE))
        {
            Token token = currentToken;
            advance();
            Node right = bitOrExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node bitOrExpr() throws Exception
    {
        Node left = bitXorExpr();

        while (currentToken != null && currentToken.type == TokenType.BITOR)
        {
            Token token = currentToken;
            advance();
            Node right = bitXorExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node bitXorExpr() throws Exception
    {
        Node left = bitAndExpr();

        while (currentToken != null && currentToken.type == TokenType.XOR)
        {
            Token token = currentToken;
            advance();
            Node right = bitAndExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node bitAndExpr() throws Exception
    {
        Node left = shiftExpr();

        while (currentToken != null && currentToken.type == TokenType.BITAND)
        {
            Token token = currentToken;
            advance();
            Node right = shiftExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node shiftExpr() throws Exception
    {
        Node left = arithExpr();

        while (currentToken != null && (currentToken.type == TokenType.LSHIFT || currentToken.type == TokenType.RSHIFT))
        {
            Token token = currentToken;
            advance();
            Node right = arithExpr();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node arithExpr() throws Exception
    {
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
        Node left = factor();

        while (currentToken != null && (currentToken.type == TokenType.MUL || currentToken.type == TokenType.DIV ||
                                        currentToken.type == TokenType.INTDIV || currentToken.type == TokenType.MOD))
        {
            Token token = currentToken;
            advance();
            Node right = factor();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node factor() throws Exception
    {
        if (currentToken != null && (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS ||
                                     currentToken.type == TokenType.BITNOT))
        {
            Token token = currentToken;
            advance();
            Node factor = factor();
            return new UnaryOpNode(token, factor);
        }

        return power();
    }

    private Node power() throws Exception
    {
        Node left = call();

        while (currentToken != null && currentToken.type == TokenType.POW)
        {
            Token token = currentToken;
            advance();
            Node right = factor();
            left = new BinOpNode(left, token, right);
        }

        return left;
    }

    private Node call() throws Exception
    {
        Node atom = atom();

        while (currentToken != null)
        {
            if (currentToken.type == TokenType.LPAREN)
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

                atom = new CallNode(atom, args);
            }
            else if (currentToken.type == TokenType.LSQUARE)
            {
                advance();
                Node expr = expr();

                if (currentToken == null || currentToken.type != TokenType.RSQUARE)
                    throw new Exception("Expected ']'");

                atom = new SubscriptableNode(atom, expr);
                advance();
            }
            else
            {
                break;
            }
        }

        return atom;
    }

    private Node atom() throws Exception
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
            else if (currentToken.type == TokenType.STRING)
            {
                Token token = currentToken;
                advance();
                return new StringNode(token);
            }
            else if (currentToken.matches(TokenType.KEYWORD, "none"))
            {
                advance();
                return new NoneTypeNode();
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
        var allCases = ifExprCases("if");
        var cases = allCases.key;
        var elseCase = allCases.value;
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
                elseCase = new Pair<>(statements, true);

                if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "end"))
                    advance();
                else
                    throw new Exception("Expected 'end'");
            }
            else
            {
                Node expr = statement();
                elseCase = new Pair<>(expr, false);
            }
        }

        return elseCase;
    }

    private Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> ifExprBOrC() throws Exception
    {
        var cases = new ArrayList<Pair<Pair<Node, Node>, Boolean>>();
        Pair<Node, Boolean> elseCase = null;

        if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "elif"))
        {
            var allCases = ifExprB();
            cases = allCases.key;
            elseCase = allCases.value;
        }
        else
        {
            elseCase = ifExprC();
        }

        return new Pair<>(cases, elseCase);
    }

    private Pair<ArrayList<Pair<Pair<Node, Node>, Boolean>>, Pair<Node, Boolean>> ifExprCases(String caseKeyword)
        throws Exception
    {
        var cases = new ArrayList<Pair<Pair<Node, Node>, Boolean>>();
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
            cases.add(new Pair<>(new Pair<>(condition, statements), true));

            if (currentToken != null && currentToken.matches(TokenType.KEYWORD, "end"))
            {
                advance();
            }
            else
            {
                var allCases = ifExprBOrC();
                var newCases = allCases.key;
                elseCase = allCases.value;

                for (var newCase : newCases)
                    cases.add(newCase);
            }
        }
        else
        {
            Node expr = statement();
            cases.add(new Pair<>(new Pair<>(condition, expr), false));

            var allCases = ifExprBOrC();
            var newCases = allCases.key;
            elseCase = allCases.value;

            for (var newCase : newCases)
                cases.add(newCase);
        }

        return new Pair<>(cases, elseCase);
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

        Node body = statement();

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
            return new FuncDefNode(varName, args, body, true);
        }

        if (currentToken == null || currentToken.type != TokenType.NEWLINE)
            throw new Exception("Expected '->' or NEWLINE");

        advance();
        var body = statements();

        if (currentToken == null || !currentToken.matches(TokenType.KEYWORD, "end"))
            throw new Exception("Expected 'end'");

        advance();
        return new FuncDefNode(varName, args, body, false);
    }
}
