

import java.io.FileNotFoundException;
import java.util.*;



public final class Analyser {

    Tokenizer tokenizer;
    //指令集
    ArrayList<Instruction> instructions;

    //符号栈
    Stack<TokenType> op = new Stack<>();
    /** 当前偷看的 token */
    Token peekedToken = null;

    /** 符号表 因为会用重复的元素在，所以只能用链表*/
    ArrayList<SymbolEntry> symbolTable = new ArrayList<>();
    //全局变量表，因为会用重复的元素在，所以只能用链表
    ArrayList<GlobalEntry> globalTable = new ArrayList<>();
    //函数表
    LinkedHashMap<String, FunctionEntry> FunctionTable = new LinkedHashMap<>();
    //标准库表
    HashMap<String, SymbolEntry> libraryTable = SymbolEntry.getLibraryTable();
    //break和continue有多个，所以必须要链表
    ArrayList<BreakAndContinue> breakInstructions = new ArrayList<>();
    ArrayList<BreakAndContinue> continueInstructions = new ArrayList<>();
    int whileNum=0;

    //正在运行的函数
    SymbolEntry runningFunction = new SymbolEntry();
    //刚刚结束的函数
    SymbolEntry returnFunction = new SymbolEntry();
    int globalNum = 0;
    int functionNum = 1;
    int localNum = 0;
    /** 下一个变量的栈偏移 */
    int nextOffset = 0;
    int floor = 1;
    FunctionEntry _start;



    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> analyse() throws CompileError,FileNotFoundException{
        analyseProgram();
        return instructions;
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            Token token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        Token token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    /**
     * 获取下一个变量的栈偏移
     * 
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }


    private SymbolEntry find(String name){
        for (int i = symbolTable.size() - 1; i >= 0; i--) {
            SymbolEntry symbol = symbolTable.get(i); 
            if(symbol.getName().equals(name))
                return symbol;
        }
        return null;
    }
    /**
     * 添加一个符号
     * 
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    private void addSymbol(String name, Pos curPos,boolean isConstant,String type,boolean isInitialized,boolean isFuction,HashMap<String,SymbolEntry> params,int paramId,String functionName,String returnType,int floor,int globalId,int localId) throws AnalyzeError {
        // SymbolEntry s = symbolTable.get(name);
        // System.out.println(s.getFloor());
        if (find(name) != null&&find(name).getFloor()>=floor) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.add(new SymbolEntry(name, isConstant, type,isInitialized, getNextVariableOffset(),isFuction,params,paramId,functionName,returnType,floor,globalId,localId));
        }
    }

    /**
     * 设置符号为已赋值
     * 
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    private void initializeSymbol(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry = find(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     * 
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    private int getOffset(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry = find(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     * 
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    private boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry = find(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }
    /**
     * program -> decl_stmt* function*
     * @return
     * @throws CompileError
     */
    public void analyseProgram() throws CompileError {
        while(check(TokenType.LET_KW)||check(TokenType.CONST_KW)){
            if(check(TokenType.LET_KW))
                analyseLet_decl_stmt();
            else
                analyseConst_decl_stmt();
        }
        ArrayList<Instruction> initInstructions = instructions;
        while(check(TokenType.FN_KW)){
            instructions = new ArrayList<>();
            analyseFunction();
            functionNum++;
        }
        FunctionEntry mainFunction = FunctionTable.get("main");
        if(mainFunction==null)
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        globalTable.add(new GlobalEntry(true, 6,"_start"));
        SymbolEntry mainSymbol = find("main");
        if(mainSymbol.getReturnType().equals("void")){
            initInstructions.add(new Instruction(OperationType.stackalloc,0));
            initInstructions.add(new Instruction(OperationType.call,functionNum-1));
        }
        else{
            initInstructions.add(new Instruction(OperationType.stackalloc,0));
            initInstructions.add(new Instruction(OperationType.call,functionNum-1));
            initInstructions.add(new Instruction(OperationType.popn,1));
        }
        _start = new FunctionEntry(functionNum,globalNum, 0, 0, 0, "void", initInstructions);
        FunctionTable.put("_start", _start);
        globalNum++;
    }   
    /**
     *  function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
     */
    public void analyseFunction() throws CompileError{
        localNum=0;
        HashMap<String,SymbolEntry> params = new HashMap<>();
        expect(TokenType.FN_KW);
        Token token = expect(TokenType.IDENT);
        expect(TokenType.L_PAREN);
        String name = (String) token.getValue();
        String returnType="";
        addSymbol(name, token.getStartPos(), false, "void", true, true, params, -1,null,returnType, floor, globalNum, -1);
        SymbolEntry symbol = find(name);
        if(!check(TokenType.R_PAREN))
            params = analyseFunction_param_list(name,symbol);
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        Token type = expect(TokenType.ty);
        returnType =  (String) type.getValue();
        symbol.setParams(params);
        symbol.setReturnType(returnType);
        runningFunction = symbol;
        int retSlots=0;
        if(returnType.equals("int")) retSlots=1;
        if(returnType.equals("double")) retSlots =1;
        FunctionEntry function = new FunctionEntry(functionNum,globalNum, retSlots, params.size(), localNum, returnType,instructions);
        FunctionTable.put(name, function);
        analyseBlock_stmt();
        function.setId(globalNum);
        function.setLocSlots(localNum);
        function.setinnerInstructions(instructions);
        if(returnType.equals("void"))
            instructions.add(new Instruction(OperationType.ret,-1));
        else if(!returnFunction.equals(runningFunction)){
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        }

        globalTable.add(new GlobalEntry(true,name.length(),name));
        globalNum++;
    }
    /**
     * function_param_list -> function_param (',' function_param)*
     * @param name
     * @param symbol
     * @return
     */
    public HashMap<String,SymbolEntry> analyseFunction_param_list(String name,SymbolEntry symbol) throws CompileError{
        HashMap<String,SymbolEntry> params = new HashMap<>();
        int i=1;
        params.put(i+"",analyseFunction_param(name,i));
        while(check(TokenType.COMMA)){
            expect(TokenType.COMMA);
            i++;
            params.put(i+"",analyseFunction_param(name,i));
        }
        return params;
    }

    /**
     * function_param -> 'const'? IDENT ':' ty
     * @return
     * @throws CompileError
     */
    public SymbolEntry analyseFunction_param(String name,int i) throws CompileError{
        SymbolEntry symbol = new SymbolEntry();
        boolean isConstant =false;
        if(check(TokenType.CONST_KW)){
            expect(TokenType.CONST_KW);
            isConstant = true;
        }
        Token token = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        Token type = expect(TokenType.ty);
        symbol.setType((String)type.getValue());
        addSymbol((String) token.getValue(),token.getStartPos(), isConstant, (String)type.getValue(), false, false, null, i,name,null, floor+1, -1, -1);
        System.out.println(floor+1);
        return symbol; 
    }
    /**
     * 
     * stmt ->
        expr_stmt
        | decl_stmt
        | if_stmt
        | while_stmt
        | return_stmt
        | block_stmt
        | empty_stmt
        | break_stmt
        | continue_stmt

     */
    public void analyseStmt() throws CompileError{
        // System.out.println(peek().getTokenType() + (String)_peek().getValue());
        if(check(TokenType.SEMICOLON))
            analyseEmpty_stmt();
        else if(check(TokenType.L_BRACE))
            analyseBlock_stmt();
        else if(check(TokenType.RETURN_KW))
            analyseReturn_stmt();
        else if(check(TokenType.LET_KW))
            analyseLet_decl_stmt();
        else if(check(TokenType.CONST_KW))
            analyseConst_decl_stmt();
        else if(check(TokenType.IF_KW))
            analyseIf_stmt();
        else if(check(TokenType.WHILE_KW))
            analyseWhile_stmt();
        else if(check(TokenType.BREAK_KW))
            analyseBreak_stmt();
        else if(check(TokenType.CONTINUE_KW))
            analyseContinue_stmt();
        else
            analyseExpr_stmt();
    }

    /**
     * expr_stmt -> expr ';'
     * @throws CompileError
     */
    public void analyseExpr_stmt() throws CompileError{
        String type = analyseExpr();
        while (!op.empty())
            Operation.OperationInstruction(op.pop(), instructions, type);
        expect(TokenType.SEMICOLON);
    }

    /**
     * empty_stmt -> ';'
     * @return
     * @throws CompileError
     */
    public void analyseEmpty_stmt() throws CompileError {
        expect(TokenType.SEMICOLON);
    }
    /**
     * block_stmt -> '{' stmt* '}'
     * 
     */
    public void analyseBlock_stmt() throws CompileError{
        expect(TokenType.L_BRACE);
        floor++;
        System.out.println(floor);
        while(!check(TokenType.R_BRACE))
            analyseStmt();
        expect(TokenType.R_BRACE);
        Iterator<SymbolEntry> sListIterator = symbolTable.iterator();
        while(sListIterator.hasNext()){
            SymbolEntry e = sListIterator.next();
            if(e.getFloor()==floor){
                sListIterator.remove();
            }
        }
        // for (SymbolEntry symbol : symbolTable){
        //     if(symbol.getFloor()==floor)
        //         symbolTable.remove(symbol);
        // }
        floor--;
    }
    /**
     * 
     * return_stmt -> 'return' expr? ';'
     */
    public void analyseReturn_stmt() throws CompileError{
        expect(TokenType.RETURN_KW);
        int flag =0;
        if(!check(TokenType.SEMICOLON)){
            flag = 1;
            if(!runningFunction.getReturnType().equals("void")){
                instructions.add(new Instruction(OperationType.arga,0));
                String type = analyseExpr();
                if(!runningFunction.getReturnType().equals(type))
                    throw new AnalyzeError(ErrorCode.Break, peekedToken.getStartPos());
                while(!op.empty()){
                    Operation.OperationInstruction(op.pop(), instructions, type);
                }
                instructions.add(new Instruction(OperationType.store_64,-1));
            }
        }
        expect(TokenType.SEMICOLON);
        if((flag==0&&runningFunction.getReturnType().equals("int"))||(flag==1&&runningFunction.getReturnType().equals("void")))
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        instructions.add(new Instruction(OperationType.ret,-1));
        returnFunction = runningFunction;
    }

    /**
     * let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
     */
    public void analyseLet_decl_stmt() throws CompileError {
        expect(TokenType.LET_KW);
        Token token = expect(TokenType.IDENT);
        String name = (String)token.getValue();
        boolean isInitialized = false;
        expect(TokenType.COLON);
        Token typetoken = expect(TokenType.ty);
        String type = (String)typetoken.getValue();
        if(type.equals("void"))
            throw new AnalyzeError(ErrorCode.Break, token.getStartPos());
        if(check(TokenType.ASSIGN)){
            isInitialized = true;
            if(floor==1)
                instructions.add(new Instruction(OperationType.globa,globalNum));
            else
                instructions.add(new Instruction(OperationType.loca,localNum));
            expect(TokenType.ASSIGN);
            String newtype = analyseExpr();
            //System.out.println(newtype);
            if(!newtype.equals(type))
                throw new AnalyzeError(ErrorCode.Break, token.getStartPos());
            while(!op.empty())
                Operation.OperationInstruction(op.pop(), instructions, type);
            instructions.add(new Instruction(OperationType.store_64,-1));
        }
        expect(TokenType.SEMICOLON);
        addSymbol(name,token.getStartPos(),false,type,isInitialized,false,null,-1,null,null,floor,globalNum,localNum);
        if(floor==1){
            globalNum++;
            globalTable.add(new GlobalEntry(false,0,name));
        }
        else
            localNum++;  
    }
    /**
     * const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
     * @throws CompileError
     */
    public void analyseConst_decl_stmt() throws CompileError{
        expect(TokenType.CONST_KW);
        Token token = expect(TokenType.IDENT);
        String name = (String)token.getValue();
        boolean isInitialized = false;
        expect(TokenType.COLON);
        Token typetoken = expect(TokenType.ty);
        String type = (String)typetoken.getValue();
        if(check(TokenType.ASSIGN)){
            isInitialized = true;
            if(floor==1)
                instructions.add(new Instruction(OperationType.globa,globalNum));
            else
                instructions.add(new Instruction(OperationType.loca,localNum));
            expect(TokenType.ASSIGN);
            String newtype = analyseExpr();
            if(!newtype.equals(type))
                throw new AnalyzeError(ErrorCode.Break, token.getStartPos());
            while(!op.empty())
                Operation.OperationInstruction(op.pop(), instructions, type);
            instructions.add(new Instruction(OperationType.store_64,-1));
        }
        expect(TokenType.SEMICOLON);
        addSymbol(name,token.getStartPos(),true,type,isInitialized,false,null,-1,null,null,floor,globalNum,localNum);
        if(floor==1){
            globalNum++;
            globalTable.add(new GlobalEntry(true,0,name));
        }
        else
            localNum++;  
    }
    /**
     * if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
                ^~~~ ^~~~~~~~~~         ^~~~~~~~~~~~~~~~~~~~~~
                |     if_block           else_block
                condition
     * @throws CompileError
     */
    public void analyseIf_stmt() throws CompileError{
        expect(TokenType.IF_KW);
        String type = analyseExpr();
        while(!op.empty()){
            Operation.OperationInstruction(op.pop(), instructions, type);
        }
        if(!type.equals("int")&&!type.equals("double"))
            throw new AnalyzeError(ErrorCode.Break, peekedToken.getStartPos());
        instructions.add(new Instruction(OperationType.br_true,1));
        Instruction jumpTrue = new Instruction(OperationType.br,0);
        instructions.add(jumpTrue);
        int init = instructions.size();
        analyseBlock_stmt();
        int len = instructions.size();
        if(instructions.get(len-1).getOpt().equals(OperationType.ret)){
            jumpTrue.setX(len-init);
            if(check(TokenType.ELSE_KW)){
                expect(TokenType.ELSE_KW);
                if(check(TokenType.IF_KW))
                    analyseIf_stmt();
                else if(check(TokenType.L_BRACE)){
                    analyseBlock_stmt();
                    if(!instructions.get(instructions.size()-1).getOpt().equals(OperationType.ret))
                        instructions.add(new Instruction(OperationType.br,0));
                }
                else 
                    throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
            }
        }
        else{
            Instruction jumpfalse = new Instruction(OperationType.br,0);
            instructions.add(jumpfalse);
            jumpTrue.setX(len-init+1);
            if(check(TokenType.ELSE_KW)){
                expect(TokenType.ELSE_KW);
                if(check(TokenType.IF_KW))
                    analyseIf_stmt();
                else if(check(TokenType.L_BRACE)){
                    analyseBlock_stmt();
                    instructions.add(new Instruction(OperationType.br,0));
                }
                else 
                    throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
            }
            jumpfalse.setX(instructions.size()-len-1);
        }
    }

    /**
     * while_stmt -> 'while' expr block_stmt
                      ^~~~ ^~~~~~~~~~while_block
                       condition
     * @throws CompileError
     */
    public void analyseWhile_stmt() throws CompileError{
        expect(TokenType.WHILE_KW);
        instructions.add(new Instruction(OperationType.br,0));
        int i = instructions.size();
        String type = analyseExpr();
        while(!op.empty())
            Operation.OperationInstruction(op.pop(), instructions, type);
        if(!type.equals("int")&&!type.equals("double"))
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        instructions.add(new Instruction(OperationType.br_true,1));
        Instruction jump = new Instruction(OperationType.br,0);
        instructions.add(jump);
        int j= instructions.size();
        whileNum++;
        analyseBlock_stmt();
        Instruction jumpback = new Instruction(OperationType.br,0);
        instructions.add(jumpback);
        int k=instructions.size();
        jumpback.setX(i-k);
        jump.setX(k-j);
        
        for(BreakAndContinue b : breakInstructions){
            if(b.getWhileNum()==whileNum)
                b.getInstruction().setX(k-b.getLocal());
        }
        System.out.println("continue"+continueInstructions.size());
        for(BreakAndContinue c : continueInstructions){
            if(c.getWhileNum()==whileNum)
                c.getInstruction().setX(i-c.getLocal());
        }
        whileNum--;
        if(whileNum==0){
            breakInstructions = new ArrayList<>();
            continueInstructions = new ArrayList<>();
        }
    }
    /**
     * break_stmt -> 'break' ';'
     * @return
     * @throws CompileError
     */
    public void analyseBreak_stmt() throws CompileError{
        expect(TokenType.BREAK_KW);

        Instruction  instruction = new Instruction(OperationType.br,0);
        breakInstructions.add(new BreakAndContinue(instruction,instructions.size()+1,whileNum));
        instructions.add(instruction);
        expect(TokenType.SEMICOLON);
    }

    /**
     * continue_stmt -> 'continue' ';'
     * @return
     * @throws CompileError
     */
    public void analyseContinue_stmt() throws CompileError{
        expect(TokenType.CONTINUE_KW);
        Instruction instruction = new Instruction(OperationType.br,0);
        continueInstructions.add(new BreakAndContinue(instruction, instructions.size()+1,whileNum));
        instructions.add(instruction);
        expect(TokenType.SEMICOLON);
    }
       /**
     * 表达式分析函数
     * expr ->
     *   operator_expr
     * | negate_expr
     * | assign_expr
     * | as_expr
     * | call_expr
     * | literal_expr
     * | ident_expr
     * | group_expr
     * | group_expr
     * 在operator和as里为了消除左递归，可以将其变为
     * expr -> (binary_operator expr||'as' ty)*
     * @return
     * @throws CompileError
     */
    private String analyseExpr() throws CompileError{
        String type = "";
        //Token test = peek();
        //System.out.println(test.getTokenType());
        if(check(TokenType.MINUS))
            type = analyseNegate_expr();
        else if(check(TokenType.IDENT)){
            Token token = next();
            String name = (String) token.getValue();
            SymbolEntry symbol = find(name);
            SymbolEntry library = libraryTable.get(name);
            //System.out.println(name);
            if(library!=null){
                library.setFloor(floor);
                library.setLocalId(-1);
                library.setGlobalId(-1);
                //System.out.println("1");
            }
            if(symbol == null&& library==null)
                    throw new AnalyzeError(ErrorCode.NotDeclared,null);
            if(check(TokenType.ASSIGN))
                type = analyseAssign_expr(symbol);
            else if(check(TokenType.L_PAREN)){
                type = analyseCall_expr(token,symbol,library);
            }
            else{
                type = analyseIdent_expr(symbol);
            }
        }
        else if(check(TokenType.UINT_LITERAL)||check(TokenType.DOUBLE_LITERAL)||check(TokenType.STRING_LITERAL)){
            type = analyseLiteral_expr();
        }
        else if(check(TokenType.L_PAREN)){
            type = analyseGroup_expr();
        }
        while(check(TokenType.AS_KW)||check(TokenType.PLUS)||check(TokenType.MINUS)||check(TokenType.MUL)||check(TokenType.DIV)||
              check(TokenType.EQ)||check(TokenType.NEQ)||check(TokenType.LT)||check(TokenType.LE)||check(TokenType.GE)||check(TokenType.GT)){
            
            if(check(TokenType.AS_KW)){
                type = analyseAs_expr(type);
            }
            else{
                type = analyseOperator_expr(type);
            }

        }
        return type;
    }
    /**
     * negate_expr -> '-' expr
     * 
     * @throws CompileError
     */
    public String analyseNegate_expr()throws CompileError{
        String type ="";
        expect(TokenType.MINUS);
        op.push(TokenType.NEG);
        type = analyseExpr();
        if(!type.equals("int") && !type.equals("double"))
                throw new AnalyzeError(ErrorCode.NotType, peekedToken.getStartPos());
        if(!op.empty()){
            int in = Operation.getOrder(op.peek());
            int out = Operation.getOrder(TokenType.NEG);
            if (Operation.priority[in][out] > 0)
                Operation.OperationInstruction(op.pop(),instructions,type);
        }
        return type;
    }
    
    /**
     * l_expr -> IDENT 
     * assign_expr -> l_expr '=' expr
     * 
     * @throws CompileError
     */
    public String analyseAssign_expr(SymbolEntry symbol) throws CompileError {
        String type = "";
        if(symbol.isConstant)
            throw new AnalyzeError(ErrorCode.AssignToConstant, peekedToken.getStartPos());
        if(symbol.paramId!=-1){
            String returnType = find(symbol.getFunctionName()).getReturnType();
            if(returnType.equals("void"))
                instructions.add(new Instruction(OperationType.arga,symbol.getParamId()-1));
            else
                instructions.add(new Instruction(OperationType.arga,symbol.getParamId()));
        }
        else if(symbol.floor !=1)
            instructions.add(new Instruction(OperationType.loca,symbol.getLocalId()));
        else
            instructions.add(new Instruction(OperationType.globa,symbol.getGlobalId()));        
        expect(TokenType.ASSIGN);
        type = analyseExpr();
        while(!op.empty())
            Operation.OperationInstruction(op.pop(), instructions, type);
        if(!symbol.getType().equals(type))
            throw new AnalyzeError(ErrorCode.NotType,null);
        instructions.add(new Instruction(OperationType.store_64,-1));
        return type;
    } 
    /**
     * literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL
     * 
     * @throws CompileError
     */
    public String analyseLiteral_expr() throws CompileError{
        String type = "";
        if(check(TokenType.UINT_LITERAL)){
            type = "int";
            Token token = next();
            instructions.add(new Instruction(OperationType.push,(long)token.getValue()));
            return "int";
        }
        else if(check(TokenType.DOUBLE_LITERAL)){
            type = "double";
            Token token = next();
            instructions.add(new Instruction(OperationType.push,changeDouble(token.getValue())));
        }
        else{
            type = "String";
            Token token = next();
            String str = (String)token.getValue();
            int len = str.length();
            globalTable.add(new GlobalEntry(true,len-2,str.substring(1,len-1)));
            instructions.add(new Instruction(OperationType.push,(long) globalNum));
            globalNum++;
        }
        return type;
    }
    
    private long changeDouble(Object value) {
        String binary = Long.toBinaryString(Double.doubleToRawLongBits((Double) value));
        long aws = 0;
        long xi = 1;
        for(int i=binary.length()-1; i>=0; i--){
            if(binary.charAt(i) == '1')
                aws += xi;
            xi *=2;
        }
        return aws;
    }

    /**
     * ident_expr -> IDENT
     * 
     * @return
     * @throws CompileError
     */
    public String analyseIdent_expr(SymbolEntry symbol) throws CompileError{
        //System.out.println(symbol.getParamId());
        if(symbol.paramId!=-1){
            String type = find(symbol.getFunctionName()).getReturnType();
            if(type.equals("void"))
                instructions.add(new Instruction(OperationType.arga,symbol.getParamId()-1));
            else
                instructions.add(new Instruction(OperationType.arga,symbol.getParamId()));
        }
        else if(symbol.floor !=1)
            instructions.add(new Instruction(OperationType.loca,symbol.getLocalId()));
        else
            instructions.add(new Instruction(OperationType.globa,symbol.getGlobalId()));
        instructions.add(new Instruction(OperationType.load_64,-1));
        return symbol.getType();
    }
    /**
     * call_param_list -> expr (',' expr)*
       call_expr -> IDENT '(' call_param_list? ')'
     
     * @param symbol
     * @return
     * @throws CompileError
     */
    public String analyseCall_expr(Token token,SymbolEntry symbol,SymbolEntry library) throws CompileError {
        String type="";
        String name = (String)token.getValue();
        Instruction instruction;
        expect(TokenType.L_PAREN);
        if(library!=null){
            instruction = new Instruction(OperationType.callname,globalNum);
            //globalNum++
            type = library.returnType;
        }
        else{
            if(!symbol.getType().equals("void")){
                throw new AnalyzeError(ErrorCode.Break, token.getStartPos());
            }
            int id = FunctionTable.get(name).getFunctionNum();
            System.out.println("call"+name+id);
            instruction = new Instruction(OperationType.call,id);
            type = FunctionTable.get(name).getReturnType();
        }
        op.push(TokenType.L_PAREN);
        if(FunctionTable.get(name)!=null&&(FunctionTable.get(name).getReturnType().equals("int")
                || FunctionTable.get(name).getReturnType().equals("double")))
            instructions.add(new Instruction(OperationType.stackalloc,1));
        else if(name.equals("getint")||name.equals("getdouble")||name.equals("getchar"))
            instructions.add(new Instruction(OperationType.stackalloc,1));
        else 
            instructions.add(new Instruction(OperationType.stackalloc,0));
        if(!check(TokenType.R_PAREN)){
            if(symbol==null)
                symbol=library;
            analyseCall_param_list(symbol);
        }
        expect(TokenType.R_PAREN);
        if(!op.empty())
            op.pop();

        if(library!=null){
            globalTable.add(new GlobalEntry(true, name.length(),name));
            instruction.setX(globalNum);
            globalNum++;
        }
        instructions.add(instruction);
        return type;
    }
    /**
     * @return
     * @throws CompileError
     */
    public void analyseCall_param_list(SymbolEntry symbol) throws CompileError{
        int num = 1;
        HashMap<String,SymbolEntry> params = symbol.getParams();
        int paramNum =params.size();
        String type = analyseExpr();
        //System.out.println(type);
        while(!op.empty()&&op.peek()!=TokenType.L_PAREN)
            Operation.OperationInstruction(op.pop(),instructions,type);
        System.out.println(params.get(num+""));
        if(!type.equals(params.get(num+"").getType())){
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        }
        num++;
        while(check(TokenType.COMMA)){
            next();
            type = analyseExpr();
            while(!op.empty()&&op.peek()!=TokenType.L_PAREN)
                Operation.OperationInstruction(op.pop(),instructions,type);
            if(!type.equals(params.get(num+"").getType())){
                throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
            }
            num++;
        }
        if((num-1)!=paramNum)
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        }
    /**
     * group_expr -> '(' expr ')'
     * 
     * @return
     * @throws CompileError
     */
    public String analyseGroup_expr() throws CompileError{
        expect(TokenType.L_PAREN);
        op.push(TokenType.L_PAREN);
        String type = analyseExpr();
        //System.out.println(type);
        expect(TokenType.R_PAREN);

        while(!op.peek().equals(TokenType.L_PAREN)&&!op.empty())
            Operation.OperationInstruction(op.pop(),instructions,type);
        if(!op.empty())
            op.pop();
        return type;
    }
    /**
     * 
     *  expr -> ('as' ty)*
     * @throws AnalyseError
     */
    public String analyseAs_expr(String type) throws CompileError{
        String newtype = "";
        expect(TokenType.AS_KW);
        Token token = next();
        newtype = (String)token.getValue();
        System.out.println(newtype);
        if(newtype.equals("int")&&type.equals("double"))
            instructions.add(new Instruction(OperationType.ftoi,-1));
        else if(newtype.equals("double")&&type.equals("int"))
            instructions.add(new Instruction(OperationType.itof,-1));
        else if(newtype.equals(type))
            return newtype;
        else
            throw new AnalyzeError(ErrorCode.Break,peekedToken.getStartPos());
        return newtype;
    }
    
    /**
     * expr -> (binary_operator expr)*
     * 
     * @param type
     * @return
     * @throws CompileError
     */
    public String analyseOperator_expr(String type) throws CompileError {
        Token token = next();
        if(!op.empty()&&!op.peek().equals(TokenType.L_PAREN)){
            int in = Operation.getOrder(op.peek());
            int out = Operation.getOrder(token.getTokenType());
            if (Operation.priority[in][out] > 0){
                Operation.OperationInstruction(op.pop(), instructions, type);
            }
        }
        op.push(token.getTokenType());
        String newtype = analyseExpr();
        System.out.println(token.getTokenType());
        if(newtype.equals(type)&&(newtype.equals("int")||newtype.equals("double")))
            return newtype;
        else 
            throw new AnalyzeError(ErrorCode.Break, peekedToken.getStartPos());
    }

    public FunctionEntry get_start() {
    	return this._start;
    }
    public void set_start(FunctionEntry _start) {
    	this._start = _start;
    }
    public ArrayList<GlobalEntry> getGlobalTable() {
    	return this.globalTable;
    }
    public LinkedHashMap<String,FunctionEntry> getFunctionTable() {
    	return this.FunctionTable;
    }
}

