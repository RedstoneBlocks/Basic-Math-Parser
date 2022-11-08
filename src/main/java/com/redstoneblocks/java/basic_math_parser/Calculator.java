package com.redstoneblocks.java.basic_math_parser;

import java.text.ParseException;
import java.util.*;

public class Calculator {
    public static int compute(String expression) throws ParseException {
        TokenStream stream = new TokenStream(expression);
        TypedTokenStream typed = new TypedTokenStream(stream);
        TypedTokenStream rpn = RPN.rpn(typed);
        return compute(rpn);
    }
    public static int compute(TypedTokenStream rpn) {
        Deque<Integer> stack = new ArrayDeque<Integer>();
        for (int i = 0; i < rpn.length(); i++) {
            TypedTokenStream.TypedToken token = rpn.get(i);
            switch (token.getKind()) {
                case NUMBER:
                    stack.push(((TypedTokenStream.TypedToken.NumberToken)token).getNumber());
                    break;
                case OPERATOR_ADD:
                    int second = stack.pop();
                    int first = stack.pop();
                    stack.push(first + second);
                    break;
                case OPERATOR_SUBTRACT:
                    second = stack.pop();
                    first = stack.pop();
                    stack.push(first - second);
                    break;
                case OPERATOR_MULTIPLY:
                    second = stack.pop();
                    first = stack.pop();
                    stack.push(first * second);
                    break;
                case OPERATOR_DIVIDE:
                    second = stack.pop();
                    first = stack.pop();
                    stack.push(first / second);
                    break;
                case OPERATOR_UNARY_MINUS:
                    stack.push(-stack.pop());
                    break;
                default:
                    throw new IllegalStateException("Invalid rpn stream");
            }
        }
        return stack.pop();
    }
}

