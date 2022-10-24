package src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import src.nodes.StringNode;
import src.nodes.SubscriptableNode;
import src.nodes.UnaryOpNode;
import src.nodes.VarAccessNode;
import src.nodes.VarAssignNode;
import src.nodes.WhileNode;
import src.values._Bool;
import src.values._Function;
import src.values._List;
import src.values._None;
import src.values._Number;
import src.values._String;
import src.values._Value;

public class Interpreter
{
    public _Value visit(Node node, Context context)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
    {
        Class<? extends Node> nodeClass = node.getClass();
        Class<? extends Context> contextClass = context.getClass();
        Method method = getClass().getDeclaredMethod("visit", nodeClass, contextClass);
        return (_Value)method.invoke(this, node, context);
    }

    public _Value visit(NumberNode node, Context context)
    {
        boolean isInt = node.token.type == TokenType.INT;
        node.token.value = isInt ? String.valueOf(Integer.parseInt(node.token.value))
                                 : String.valueOf(Double.parseDouble(node.token.value));
        return new _Number(isInt ? "int" : "float", node.token.value).setContext(context);
    }

    public _Value visit(BoolNode node, Context context)
    {
        return new _Bool(node.token.value).setContext(context);
    }

    public _Value visit(StringNode node, Context context)
    {
        return new _String(node.token.value).setContext(context);
    }

    public _Value visit(NoneTypeNode node, Context context)
    {
        return new _None().setContext(context);
    }

    public _Value visit(ListNode node, Context context)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
    {
        ArrayList<_Value> elements = new ArrayList<>();

        for (Node elementNode : node.elementNodes)
            elements.add(visit(elementNode, context));

        return new _List(elements).setContext(context);
    }

    public _Value visit(SubscriptableNode node, Context context) throws Exception
    {
        return visit(node.subscriptableNode, context).get(visit(node.node, context)).setContext(context);
    }

    public _Value visit(BinOpNode node, Context context) throws Exception
    {
        _Value left = visit(node.left, context);
        _Value right = visit(node.right, context);

        _Value result = null;

        if (node.token.type == TokenType.PLUS)
            result = left.addedTo(right);
        else if (node.token.type == TokenType.MINUS)
            result = left.subbedBy(right);
        else if (node.token.type == TokenType.MUL)
            result = left.multedBy(right);
        else if (node.token.type == TokenType.DIV)
            result = left.divedBy(right);
        else if (node.token.type == TokenType.INTDIV)
            result = left.intdivedBy(right);
        else if (node.token.type == TokenType.MOD)
            result = left.moduloBy(right);
        else if (node.token.type == TokenType.POW)
            result = left.poweredBy(right);
        else if (node.token.type == TokenType.EE)
            result = left.getComparisonEq(right);
        else if (node.token.type == TokenType.NE)
            result = left.getComparisonNe(right);
        else if (node.token.type == TokenType.LT)
            result = left.getComparisonLt(right);
        else if (node.token.type == TokenType.GT)
            result = left.getComparisonGt(right);
        else if (node.token.type == TokenType.LTE)
            result = left.getComparisonLte(right);
        else if (node.token.type == TokenType.GTE)
            result = left.getComparisonGte(right);

        return result;
    }

    public _Value visit(UnaryOpNode node, Context context) throws Exception
    {
        _Value value = visit(node.node, context);

        if (node.token.type == TokenType.MINUS)
            value = value.multedBy(new _Number("int", "-1"));

        return value;
    }

    public _Value visit(VarAccessNode node, Context context) throws Exception
    {
        String varName = node.token.value;
        _Value value = context.symbolTable.get(varName);

        if (value != null)
            return value.setContext(context);

        throw new Exception("NameError: name '" + varName + "' is not defined");
    }

    public _Value visit(VarAssignNode node, Context context)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
    {
        String varName = node.token.value;
        _Value value = visit(node.node, context);
        context.symbolTable.set(varName, value);
        return value;
    }

    public _Value visit(AlternateAssignNode node, Context context)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
    {
        String varName = node.token.value;
        _Value value = visit(node.node, context);
        context.symbolTable.altset(varName, value);
        return value;
    }

    public _Value visit(IfNode node, Context context) throws Exception
    {
        for (var pair : node.cases)
        {
            _Value conditionValue = visit(pair.key.key, context);

            if (conditionValue.isTrue())
            {
                var exprValue = visit(pair.key.value, context);
                return pair.value ? new _None() : exprValue;
            }
        }

        if (node.elseCase != null)
        {
            var elseValue = visit(node.elseCase.key, context);
            return node.elseCase.value ? new _None() : elseValue;
        }

        return new _None();
    }

    public _Value visit(WhileNode node, Context context) throws Exception
    {
        ArrayList<_Value> elements = new ArrayList<>();

        while (true)
        {
            var condition = visit(node.condition, context);

            if (!condition.isTrue())
                break;

            elements.add(visit(node.body, context));
        }

        return node.shouldReturnNull ? new _None() : new _List(elements).setContext(context);
    }

    public _Value visit(FuncDefNode node, Context context)
    {
        String funcName = node.varName != null ? node.varName.value : null;
        Node body = node.body;
        ArrayList<String> args = new ArrayList<>();

        for (Token arg : node.args)
            args.add(arg.value);

        _Function funcValue =
            (_Function) new _Function(funcName, body, args, node.shouldReturnNull).setContext(context);

        if (funcName != null)
            context.symbolTable.set(funcName, funcValue);

        return funcValue;
    }

    public _Value visit(CallNode node, Context context) throws Exception
    {
        ArrayList<_Value> args = new ArrayList<>();
        _Value valueToCall = visit(node.node, context);
        valueToCall = valueToCall.copy(); // why?

        for (Node arg : node.args)
            args.add(visit(arg, context));

        _Value returnValue = valueToCall.execute(args);
        return returnValue.copy().setContext(context);
        // return returnValue;
    }
}
