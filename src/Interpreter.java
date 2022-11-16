package src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import src.nodes.AlternateAssignNode;
import src.nodes.BinOpNode;
import src.nodes.BoolNode;
import src.nodes.BreakNode;
import src.nodes.CallNode;
import src.nodes.ContinueNode;
import src.nodes.DictNode;
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
import src.values._BaseFunction;
import src.values._Bool;
import src.values._Dict;
import src.values._Function;
import src.values._List;
import src.values._None;
import src.values._Number;
import src.values._String;
import src.values._Value;

public class Interpreter
{
    public RTResult visit(Node node, Context context)
        throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
    {
        Class<? extends Node> nodeClass = node.getClass();
        Class<? extends Context> contextClass = context.getClass();
        Method method = getClass().getDeclaredMethod("visit", nodeClass, contextClass);
        return (RTResult)method.invoke(this, node, context);
    }

    public RTResult visit(NumberNode node, Context context)
    {
        boolean isInt = node.token.type == TokenType.INT;
        node.token.value = isInt ? String.valueOf(Integer.parseInt(node.token.value))
                                 : String.valueOf(Double.parseDouble(node.token.value));
        return new RTResult().success(new _Number(isInt ? "int" : "float", node.token.value).setContext(context));
    }

    public RTResult visit(BoolNode node, Context context)
    {
        return new RTResult().success(new _Bool(node.token.value).setContext(context));
    }

    public RTResult visit(StringNode node, Context context)
    {
        return new RTResult().success(new _String(node.token.value).setContext(context));
    }

    public RTResult visit(NoneTypeNode node, Context context)
    {
        return new RTResult().success(new _None().setContext(context));
    }

    public RTResult visit(ListNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        ArrayList<_Value> elements = new ArrayList<>();

        for (Node elementNode : node.elementNodes)
        {
            elements.add(res.register(visit(elementNode, context)));

            if (res.shouldReturn())
                return res;
        }

        return res.success(new _List(elements).setContext(context));
    }

    public RTResult visit(DictNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        Map<_Value, _Value> elements = new HashMap<>();

        for (var elementNode : node.node)
        {
            _Value key = res.register(visit(elementNode.key, context));
            _Value value = res.register(visit(elementNode.value, context));

            if (key.getClass() == _BaseFunction.class || key.getClass() == _List.class || key.getClass() == _Dict.class)
                throw new Exception("TypeError: unhashable type: '" + key.type() + "'");

            elements.put(key, value);

            if (res.shouldReturn())
                return res;
        }

        return res.success(new _Dict(elements).setContext(context));
    }

    public RTResult visit(SubscriptableNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        return res.success(res.register(visit(node.subscriptableNode, context))
                               .get(res.register(visit(node.node, context)))
                               .setContext(context));
    }

    public RTResult visit(BinOpNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        _Value left = res.register(visit(node.left, context));

        if (res.shouldReturn())
            return res;

        _Value right = res.register(visit(node.right, context));

        if (res.shouldReturn())
            return res;

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
        else if (node.token.type == TokenType.BITAND)
            result = left.bitandedBy(right);
        else if (node.token.type == TokenType.BITOR)
            result = left.bitoredBy(right);
        else if (node.token.type == TokenType.XOR)
            result = left.xoredBy(right);
        else if (node.token.type == TokenType.LSHIFT)
            result = left.lshiftedBy(right);
        else if (node.token.type == TokenType.RSHIFT)
            result = left.rshiftedBy(right);
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
        else if (node.token.matches(TokenType.KEYWORD, "and"))
            result = left.andedBy(right);
        else if (node.token.matches(TokenType.KEYWORD, "or"))
            result = left.oredBy(right);

        return res.success(result);
    }

    public RTResult visit(UnaryOpNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        _Value value = res.register(visit(node.node, context));

        if (res.shouldReturn())
            return res;

        if (node.token.type == TokenType.MINUS)
            value = value.multedBy(new _Number("int", "-1"));
        else if (node.token.matches(TokenType.KEYWORD, "not"))
            value = value.notted();
        else if (node.token.type == TokenType.BITNOT)
            value = value.bitnotted();

        return res.success(value);
    }

    public RTResult visit(VarAccessNode node, Context context) throws Exception
    {
        String varName = node.token.value;
        _Value value = context.symbolTable.get(varName);

        if (value != null)
            // return new RTResult().success(value.copy().setContext(context)); // why?????
            return new RTResult().success(value.setContext(context));

        throw new Exception("NameError: name '" + varName + "' is not defined");
    }

    public RTResult visit(VarAssignNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        String varName = node.token.value;
        _Value value = res.register(visit(node.node, context));

        if (res.shouldReturn())
            return res;

        context.symbolTable.set(varName, value);
        return res.success(value);
    }

    public RTResult visit(AlternateAssignNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        String varName = node.token.value;
        _Value value = res.register(visit(node.node, context));

        if (res.shouldReturn())
            return res;

        context.symbolTable.altset(varName, value);
        return res.success(value);
    }

    public RTResult visit(IfNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();

        for (var pair : node.cases)
        {
            _Value conditionValue = res.register(visit(pair.key.key, context));

            if (res.shouldReturn())
                return res;

            if (conditionValue.isTrue())
            {
                var exprValue = res.register(visit(pair.key.value, context));

                if (res.shouldReturn())
                    return res;

                return res.success(pair.value ? new _None() : exprValue);
            }
        }

        if (node.elseCase != null)
        {
            var elseValue = res.register(visit(node.elseCase.key, context));

            if (res.shouldReturn())
                return res;

            return res.success(node.elseCase.value ? new _None() : elseValue);
        }

        return res.success(new _None());
    }

    public RTResult visit(WhileNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        ArrayList<_Value> elements = new ArrayList<>();

        while (true)
        {
            _Value condition = res.register(visit(node.condition, context));

            if (res.shouldReturn())
                return res;

            if (!condition.isTrue())
                break;

            _Value value = res.register(visit(node.body, context));

            if (res.shouldReturn() && !res.loopShouldContinue && !res.loopShouldBreak)
                return res;

            if (res.loopShouldContinue)
                continue;

            if (res.loopShouldBreak)
                break;

            elements.add(value);
        }

        return res.success(node.shouldReturnNull ? new _None() : new _List(elements).setContext(context));
    }

    public RTResult visit(FuncDefNode node, Context context)
    {
        String funcName = node.varName != null ? node.varName.value : null;
        Node body = node.body;
        ArrayList<String> args = new ArrayList<>();

        for (Token arg : node.args)
            args.add(arg.value);

        _Function funcValue =
            (_Function) new _Function(funcName, body, args, node.shouldAutoReturn).setContext(context);

        if (funcName != null)
            context.symbolTable.set(funcName, funcValue);

        return new RTResult().success(funcValue);
    }

    public RTResult visit(CallNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        ArrayList<_Value> args = new ArrayList<>();
        _Value valueToCall = res.register(visit(node.node, context));

        if (res.shouldReturn())
            return res;

        // valueToCall = valueToCall.copy(); // why?

        for (Node arg : node.args)
        {
            args.add(res.register(visit(arg, context)));
            if (res.shouldReturn())
                return res;
        }

        _Value returnValue = res.register(valueToCall.execute(args));

        if (res.shouldReturn())
            return res;

        // returnValue = returnValue.copy().setContext(context); // do not uncomment
        // uncommenting will set the context's value to it's older value
        // and every variable defined in the function will get destroyed
        // SO DO NOT UNCOMMENT THE DAMN LINE
        return res.success(returnValue);
        // return returnValue;
    }

    public RTResult visit(ReturnNode node, Context context) throws Exception
    {
        RTResult res = new RTResult();
        _Value value;

        if (node.nodeToReturn != null)
        {
            value = res.register(visit(node.nodeToReturn, context));

            if (res.shouldReturn())
                return res;
        }
        else
        {
            value = new _None();
        }

        return res.successReturn(value);
    }

    public RTResult visit(BreakNode node, Context context)
    {
        return new RTResult().successBreak();
    }

    public RTResult visit(ContinueNode node, Context context)
    {
        return new RTResult().successContinue();
    }
}
