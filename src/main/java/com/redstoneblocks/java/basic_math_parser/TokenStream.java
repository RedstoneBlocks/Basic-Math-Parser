package com.redstoneblocks.java.basic_math_parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class TokenStream {
    public enum TokenKind {
        NUMBER,
        OPERATOR,
        PARENTHESIS,
    }

    public static class Token {
        private TokenKind kind;
        private String raw;

        public Token(TokenKind kind, String raw) {
            this.raw = raw;
            this.kind = kind;
        }

        public String getRaw() {
            return raw;
        }

        public TokenKind getKind() {
            return kind;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "kind=" + kind +
                    ", raw='" + raw + '\'' +
                    '}';
        }
    }

    private final Token[] tokens;
    private String raw;

    public TokenStream(String string) throws ParseException {
        this.raw = string;
        ArrayList<Token> tokens = new ArrayList<Token>();
        for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '_':
                    tokens.add(new Token(TokenKind.OPERATOR, Character.toString(c)));
                    break;
                case '(':
                case ')':
                    tokens.add(new Token(TokenKind.PARENTHESIS, Character.toString(c)));
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    StringBuilder current = new StringBuilder();
                    current.append(c);
                    i++;
                    while(i < string.length() && "0123456789".indexOf(string.charAt(i)) != -1 ) {
                        current.append(string.charAt(i));
                        i++;
                    }
                    tokens.add(new Token(TokenKind.NUMBER, current.toString()));
                    i--;
                    continue;
                case ' ':
                    break;
                default:
                    throw new ParseException("Invalid token \"" + c + "\"", i);
            }
        }

        // fix negative number at beginning
        if (tokens.get(0).getKind() == TokenKind.OPERATOR && "-".equals(tokens.get(0).getRaw())) {
            tokens.get(0).raw = "_";
        }
        // fix more negative numbers
        for (int i = 0; i + 2 < tokens.size(); i++) {
            Token op = tokens.get(i);
            Token minus = tokens.get(i+1);
            Token number = tokens.get(i+2);

            if(
                op.getKind() == TokenKind.OPERATOR && !"_".equals(op.getRaw()) &&
                minus.getKind() == TokenKind.OPERATOR &&
                "-".equals(minus.getRaw()) &&
                number.getKind() == TokenKind.NUMBER
            ) {
                minus.raw = "_";
                i += 2;
            }
        }

        // fix unary - infront of parens
        for (int i = 0; i + 2 < tokens.size(); i++) {
            Token op = tokens.get(i);
            Token minus = tokens.get(i+1);
            Token openParen = tokens.get(i+2);

            if(
                (op.getKind() == TokenKind.OPERATOR || (op.getKind() == TokenKind.PARENTHESIS && "(".equals(op.getRaw()))) &&
                minus.getKind() == TokenKind.OPERATOR &&
                "-".equals(minus.getRaw()) &&
                openParen.getKind() == TokenKind.PARENTHESIS &&
                "(".equals(openParen.getRaw())
            ) {
                minus.raw = "_";
                i += 2;
            }
        }

        // fix unary - after of parens
        for (int i = 0; i + 2 < tokens.size(); i++) {
            Token openParen = tokens.get(i);
            Token minus = tokens.get(i+1);

            if(
                minus.getKind() == TokenKind.OPERATOR &&
                "-".equals(minus.getRaw()) &&
                openParen.getKind() == TokenKind.PARENTHESIS &&
                "(".equals(openParen.getRaw())
            ) {
                minus.raw = "_";
                i++;
            }
        }

        this.tokens = tokens.toArray(new Token[0]);
    }

    public Token get(int i) {
        return tokens[i];
    }
    public int length() {
        return tokens.length;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        return "TokenStream{" +
                "tokens=" + Arrays.toString(tokens) +
                '}';
    }
}
