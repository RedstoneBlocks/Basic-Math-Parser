package com.redstoneblocks.java.basic_math_parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

public class RPN {

    public static TypedTokenStream rpn(TypedTokenStream stream) {
        Deque<TypedTokenStream.TypedToken> operatorStack = new ArrayDeque<TypedTokenStream.TypedToken>();
        Deque<TypedTokenStream.TypedToken> outputQueue = new ArrayDeque<TypedTokenStream.TypedToken>();

        int i = 0;
        while (stream.length() > i) {
            TypedTokenStream.TypedToken token = stream.get(i++);
            switch (token.getKind()) {
                case NUMBER:
                    outputQueue.push(token);
                    break;
                case OPERATOR_UNARY_MINUS:
                case OPERATOR_ADD:
                case OPERATOR_SUBTRACT:
                case OPERATOR_MULTIPLY:
                case OPERATOR_DIVIDE:
                    TypedTokenStream.TypedToken.OperatorToken.OperatorTokenKind kind = ((TypedTokenStream.TypedToken.OperatorToken.OperatorToken)token).getOperatorKind();
                    while(
                            !operatorStack.isEmpty() &&
                            operatorStack.peek().getKind() != TypedTokenStream.TypedTokenKind.PARENTHESIS_OPEN && (
                            ((TypedTokenStream.TypedToken.OperatorToken)operatorStack.peek()).getOperatorKind().getPrecedence() > kind.getPrecedence()
                                    || ((TypedTokenStream.TypedToken.OperatorToken)operatorStack.peek()).getOperatorKind().getPrecedence() == kind.getPrecedence() && kind.isLeftAssociative()
                            )
                    ) {
                        outputQueue.push(operatorStack.pop());
                    }
                    operatorStack.push(token);
                    break;
                case PARENTHESIS_OPEN:
                    operatorStack.push(token);
                    break;
                case PARENTHESIS_CLOSE:
                    while(true) {
                        assert operatorStack.peek() != null;
                        if (operatorStack.peek().getKind() == TypedTokenStream.TypedTokenKind.PARENTHESIS_OPEN)
                            break;
                        assert operatorStack.size() != 0;
                        outputQueue.push(operatorStack.pop());
                    }
                    assert (operatorStack.peek() != null ? operatorStack.peek().getKind() : null) == TypedTokenStream.TypedTokenKind.PARENTHESIS_OPEN;
                    operatorStack.pop();
                    break;
                default:
                    throw new IllegalStateException("invalid state");
            }
        }
        while (!operatorStack.isEmpty()) {
            assert operatorStack.getLast().getKind() != TypedTokenStream.TypedTokenKind.PARENTHESIS_OPEN;
            outputQueue.push(operatorStack.pop());
        }
        return new TypedTokenStream(outputQueue.descendingIterator());
    }
}
