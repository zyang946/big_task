import java.util.regex.Pattern;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return UintOrDouble_Literal();
        } else if (Character.isAlphabetic(peek)||peek == '_') {
            return Ident();
        } else if(peek == '"'){
            return String_Literal();
        } else if(peek == '\\'){
            return Char_Literal();
        }else if(peek == '/'){
            return Comment();
        }else {
            return OperatorOrUnknown();
        }
    }

    private Token UintOrDouble_Literal() throws TokenizeError {
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        Pos startPos = it.currentPos();
        char peek = it.peekChar();
        String num = "";
        while(Character.isDigit(peek)){
            num += it.nextChar();
            peek = it.peekChar();
        }
        System.out.println(num);
        if(Pattern.matches("[0-9]+", num)){
            return new Token(TokenType.UINT_LITERAL,Long.parseLong(num),startPos,it.currentPos());
        }
        else if(Pattern.matches("[0-9]+.[0-9]+([eE][-+]?[0-9]+)?", num)){
            return new Token(TokenType.DOUBLE_LITERAL,Double.parseDouble(num),startPos,it.currentPos());
        }
        //拓展C0，浮点数
        else{
            throw new Error("Not implemented");
        }
    }

    private Token Ident() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        Pos startPos = it.currentPos();
        char peek = it.peekChar();
        String value = "";
        while(Character.isDigit(peek)||Character.isAlphabetic(peek)||peek == '_'){
            value += it.nextChar();
            peek = it.peekChar();
        }
        Pos endPos = it.currentPos();
        if(value.equals("fn"))
            return new Token(TokenType.FN_KW, value, startPos, endPos);
        else if(value.equals("let"))
            return new Token(TokenType.LET_KW,value,startPos,endPos);
        else if(value.equals("const"))
            return new Token(TokenType.CONST_KW,value,startPos,endPos);
        else if(value.equals("as"))
            return new Token(TokenType.AS_KW,value,startPos,endPos);
        else if(value.equals("while"))
            return new Token(TokenType.WHILE_KW,value,startPos,endPos);
        else if(value.equals("if"))
            return new Token(TokenType.IF_KW,value,startPos,endPos);
        else if(value.equals("else"))
            return new Token(TokenType.ELSE_KW,value,startPos,endPos);
        else if(value.equals("return"))
            return new Token(TokenType.RETURN_KW,value,startPos,endPos);
        else if(value.equals("break"))
            return new Token(TokenType.BREAK_KW,value,startPos,endPos);
        else if(value.equals("continue"))
            return new Token(TokenType.CONTINUE_KW,value,startPos,endPos);
        else if(value.equals("int")||value.equals("void")||value.equals("double"))
            return new Token(TokenType.ty,value,startPos,endPos);
        else if(value.equals("as"))
            return new Token(TokenType.AS,value,startPos,endPos);
        else
            return new Token(TokenType.IDENT,value,startPos,endPos);
    }
    private Token String_Literal() throws TokenizeError{
        Pos startPos =it.currentPos();
        String value = "";
        value += it.nextChar();
        char peek = it.peekChar();
        while(peek != '"'){
            if(peek=='\\'){
                it.nextChar();
                switch(it.peekChar()){
                    case '\'':
                        value += '\'';
                        it.nextChar();
                        break;
                    case '"':
                        value += '"';
                        it.nextChar();
                        break;
                    case '\\':
                        value +='\\';
                        it.nextChar();
                        break;
                    case 'n':
                        value +='\n';
                        it.nextChar();
                        break;
                    case 't':
                        value += '\t';
                        it.nextChar();
                        break;
                    case 'r':
                        value += '\r';
                        it.nextChar();
                        break;
                    default:
                        // 不认识这个输入，摸了
                        throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
            }
            else{
                value += it.nextChar();
            }
            peek = it.peekChar();
        }
        value += it.nextChar();
        Pos endPos = it.currentPos();
        return new Token(TokenType.STRING_LITERAL,value,startPos,endPos);
    }
    private Token Char_Literal() throws TokenizeError{
        throw new Error("Not implemented");
    }
    private Token Comment() throws TokenizeError{
        throw new Error("Not implemented");
    }
    private Token OperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());

            case '-':
                if(it.peekChar()=='>'){
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.ARROW,"->",startPos,it.currentPos());
                }
                else// 填入返回语句
                    return new Token(TokenType.MINUS, '+', it.previousPos(), it.currentPos());

            case '*':
                // 填入返回语句
                return new Token(TokenType.MUL, '+', it.previousPos(), it.currentPos());

            case '/':
                // 填入返回语句
                return new Token(TokenType.DIV, '+', it.previousPos(), it.currentPos());

            // 填入更多状态和返回语句
            case '=':
                if(it.peekChar()=='='){
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.EQ,"==",startPos,it.currentPos());
                }
            // 填入返回语句
                else
                    return new Token(TokenType.ASSIGN, '+', it.previousPos(), it.currentPos());
            case '!':
                if(it.peekChar()=='='){
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.NEQ,"!=",startPos,it.currentPos());
                }
                else
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            case '<':
            // 填入返回语句
                if(it.peekChar()=='='){
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.LE,"<=",startPos,it.currentPos());
                }
                return new Token(TokenType.LT, '<', it.previousPos(), it.currentPos());
            case '>':
                if(it.peekChar()=='='){
                    Pos startPos = it.previousPos();
                    it.nextChar();
                    return new Token(TokenType.GE,">=",startPos,it.currentPos());
                }
            // 填入返回语句
                return new Token(TokenType.GT, '>', it.previousPos(), it.currentPos());
            case '(':
            // 填入返回语句
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());
            case ')':
            // 填入返回语句
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());
            case '{':
                // 填入返回语句
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());
            case '}':
                // 填入返回语句
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());
            case ',':
                    // 填入返回语句
                return new Token(TokenType.COMMA, ',', it.previousPos(), it.currentPos());
            case ':':
                // 填入返回语句
                return new Token(TokenType.COLON, ':', it.previousPos(), it.currentPos());
            case ';':
                // 填入返回语句
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos()); 

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
