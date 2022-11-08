package com.redstoneblocks.java.basic_math_parser;

import jdk.nashorn.internal.parser.TokenType;

import java.util.*;

public class TypedTokenStream {
    public enum TypedTokenKind {
        NUMBER,
        OPERATOR_ADD,
        OPERATOR_SUBTRACT,
        OPERATOR_MULTIPLY,
        OPERATOR_DIVIDE,
        OPERATOR_UNARY_MINUS,
        PARENTHESIS_OPEN,
        PARENTHESIS_CLOSE,
    }
    public static abstract class TypedToken {
        protected TypedTokenKind kind;
        public TypedTokenKind getKind() {
            return kind;
        }

        public static TypedToken typed(TokenStream.Token token) {
            switch (token.getKind()) {
                case NUMBER:
                    int num;
                    try {
                        num = Integer.parseInt(token.getRaw());
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("Invalid raw value for token: " + token, e);
                    }
                    return new NumberToken(num);
                case PARENTHESIS:
                    switch (token.getRaw().charAt(0)) {
                        case '(':
                            return new ParenthesisToken(ParenthesisToken.ParenthesisKind.OPEN);
                        case ')':
                            return new ParenthesisToken(ParenthesisToken.ParenthesisKind.CLOSE);
                        default:
                            throw new IllegalStateException("Invalid raw value for token: " + token);
                    }
                case OPERATOR:
                    switch (token.getRaw().charAt(0)) {
                        case '+':
                            return new OperatorToken(OperatorToken.OperatorTokenKind.ADD);
                        case '-':
                            return new OperatorToken(OperatorToken.OperatorTokenKind.SUBTRACT);
                        case '*':
                            return new OperatorToken(OperatorToken.OperatorTokenKind.MULTIPLY);
                        case '/':
                            return new OperatorToken(OperatorToken.OperatorTokenKind.DIVIDE);
                        case '_':
                            return new OperatorToken(OperatorToken.OperatorTokenKind.UNARY_MINUS);
                        default:
                            throw new IllegalStateException("Invalid raw value for token: " + token);
                    }
            }
            throw new IllegalStateException("Invalid token type: " + token.getKind());
        }

        public static class NumberToken extends TypedToken {
            private final int number;

            public int getNumber() {
                return number;
            }

            public NumberToken(int number) {
                this.number = number;
                kind = TypedTokenKind.NUMBER;
            }

            @Override
            public String toString() {
                return "" + number;
}
        }

        public static class OperatorToken extends TypedToken {
            public enum OperatorTokenKind {
                ADD,
                SUBTRACT,
                MULTIPLY,
                DIVIDE,
                UNARY_MINUS;
                public TypedTokenKind intoTypedTokenKind() {
                    switch (this) {
                        case ADD:
                            return TypedTokenKind.OPERATOR_ADD;
                        case SUBTRACT:
                            return TypedTokenKind.OPERATOR_SUBTRACT;
                        case MULTIPLY:
                            return TypedTokenKind.OPERATOR_MULTIPLY;
                        case DIVIDE:
                            return TypedTokenKind.OPERATOR_DIVIDE;
                        case UNARY_MINUS:
                            return TypedTokenKind.OPERATOR_UNARY_MINUS;
                        default:
                            throw new IllegalStateException("Cannot convert into kind");
                    }
                }

                public int getPrecedence() {
                    switch (this) {
                        case ADD:
                        case SUBTRACT:
                            return 1;
                        case MULTIPLY:
                        case DIVIDE:
                            return 2;
                        case UNARY_MINUS:
                            return 4;
                        default:
                            return -1;
                    }
                }

                public boolean isLeftAssociative() {
                    switch (this) {
                        case ADD:
                        case SUBTRACT:
                        case MULTIPLY:
                        case DIVIDE:
                            return true;
                        default: // UNARY_MINUS
                            return false;
                    }
                }


                @Override
                public String toString() {

                        switch (this) {
                            case ADD:
                                return "+";
                            case SUBTRACT:
                                return "-";
                            case MULTIPLY:
                                return "*";
                            case DIVIDE:
                                return "/";
                            case UNARY_MINUS:
                                return "_";
                            default:
                                throw new IllegalStateException("Cannot convert into kind");
                    }
                }
            }
            private final OperatorTokenKind operatorKind;

            public OperatorTokenKind getOperatorKind() {
                return operatorKind;
            }

           public OperatorToken(OperatorTokenKind kind) {
                this.kind = kind.intoTypedTokenKind();
                this.operatorKind = kind;
            }

            @Override
            public String toString() {
                return "" + operatorKind;
            }
        }

        public static class ParenthesisToken extends TypedToken {
            public enum ParenthesisKind {
                OPEN,
                CLOSE;

                public TypedTokenKind intoTypedTokenKind() {
                    switch (this) {
                        case OPEN:
                            return TypedTokenKind.PARENTHESIS_OPEN;
                        case CLOSE:
                            return TypedTokenKind.PARENTHESIS_CLOSE;
                        default:
                            throw new IllegalStateException("Cannot convert into kind");
                    }
                }

                @Override
                public String toString() {
                    switch (this) {
                        case OPEN:
                            return "(";
                        case CLOSE:
                            return ")";
                        default:
                            throw new IllegalStateException("Invalid enum value");
                    }
                }
            }

            private final ParenthesisKind parenthesisKind;

            public ParenthesisKind getParenthesisKind() {
                return parenthesisKind;
            }

            public ParenthesisToken(ParenthesisKind kind) {
                this.kind = kind.intoTypedTokenKind();
                parenthesisKind = kind;
            }

            @Override
            public String toString() {
                return "" + parenthesisKind;
            }
        }
    }

    private final TypedToken[] tokens;

    public TypedTokenStream(TokenStream stream) {
        tokens = new TypedToken[stream.length()];
        for (int i = 0; i < stream.length(); i++) {
            tokens[i] = TypedToken.typed(stream.get(i));
        }
    }

    public TypedTokenStream(Iterator<TypedToken> iterator) {
        ArrayList<TypedToken> collector = new ArrayList<TypedToken>();
        while (iterator.hasNext()) {
            collector.add(iterator.next());
        }
        tokens = collector.toArray(new TypedToken[0]);
    }

    public TypedToken get(int i) {
        return tokens[i];
    }
    public int length() {
        return tokens.length;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            builder.append(tokens[i]);
            if(i + 1 < tokens.length) {
                builder.append(" ");
            }
        }
        return "TypedTokenStream{" +
                "tokens=" + builder +
                '}';
    }
}
