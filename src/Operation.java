
import java.util.ArrayList;
import java.util.HashMap;


public class Operation{

    public static boolean getPriority(TokenType t1,TokenType t2) throws CompileError{
        int o1 = getOrder(t1);
        int o2 = getOrder(t2);
        if(o1<o2)
            return true;
        return false;
    }
    public static int getOrder(TokenType t){
        if(t.equals(TokenType.L_PAREN))
            return 1;
        else if(t.equals(TokenType.NEG))
            return 2;
        else if(t.equals(TokenType.AS))
            return 3;
        else if(t.equals(TokenType.MINUS)||t.equals(TokenType.DIV))
            return 4;
        else if(t.equals(TokenType.PLUS)||t.equals(TokenType.MINUS))
            return 5;
        else if(t.equals(TokenType.EQ)||t.equals(TokenType.NEQ)||t.equals(TokenType.LT)||t.equals(TokenType.GT)||t.equals(TokenType.LE)||t.equals(TokenType.GE))
            return 6;
        else if(t.equals(TokenType.ASSIGN))
            return 7;
        else if(t.equals(TokenType.L_PAREN))
            return 8;
        return 0;
    }
    public static void OperationInstruction(TokenType t,ArrayList<Instruction> instructions,String type)throws CompileError{
        if(t.equals(TokenType.NEG)){
            //“- neg”
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.neg_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.neg_f,-1));
            else
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.PLUS)){
            //"+"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.add_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.add_f,-1));
            else
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.MINUS)){
            //"- minus"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.sub_i));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.sub_f,-1));
            else
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.MUL)){
            //"*"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.mul_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.mul_f,-1));
            else
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.DIV)){
            //"/"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.div_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.div_f,-1));
            else
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.EQ)){
            //"=="
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
            instructions.add(new Instruction(OperationType.not,-1));
        }
        else if(t.equals(TokenType.NEQ)){
            //"!="
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
        }
        else if(t.equals(TokenType.LT)){
            //"<"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
            instructions.add(new Instruction(OperationType.set_lt,-1));
        }
        else if(t.equals(TokenType.GT)){
            //">"
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
            instructions.add(new Instruction(OperationType.set_gt,-1));
        }
        else if(t.equals(TokenType.GE)){
            //">="
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
            instructions.add(new Instruction(OperationType.set_lt,-1));
            instructions.add(new Instruction(OperationType.not,-1));
        }
        else if(t.equals(TokenType.LE)){
            //"<="
            if(type.equals("int"))
                instructions.add(new Instruction(OperationType.cmp_i,-1));
            else if(type.equals("double"))
                instructions.add(new Instruction(OperationType.cmp_f,-1));
            else 
                throw new AnalyzeError(ErrorCode.InvalidInput,null);
            instructions.add(new Instruction(OperationType.set_gt,-1));
            instructions.add(new Instruction(OperationType.not,-1));
        }

    }

    public static HashMap<OperationType, Integer> getOperations(){
        HashMap<OperationType, Integer> operations = new HashMap<>();
        operations.put(OperationType.nop, 0x00);
        operations.put(OperationType.push, 0x01);
        operations.put(OperationType.pop, 0x02);
        operations.put(OperationType.popn, 0x03);
        operations.put(OperationType.dup, 0x04);
        operations.put(OperationType.loca, 0x0a);
        operations.put(OperationType.arga, 0x0b);
        operations.put(OperationType.globa, 0x0c);
        operations.put(OperationType.load_8, 0x10);
        operations.put(OperationType.load_16, 0x11);
        operations.put(OperationType.load_32, 0x12);
        operations.put(OperationType.load_64, 0x13);
        operations.put(OperationType.store_8, 0x14);
        operations.put(OperationType.store_16, 0x15);
        operations.put(OperationType.store_32, 0x16);
        operations.put(OperationType.store_64, 0x17);
        operations.put(OperationType.alloc, 0x18);
        operations.put(OperationType.free, 0x19);
        operations.put(OperationType.stackalloc, 0x1a);
        operations.put(OperationType.add_i, 0x20);
        operations.put(OperationType.sub_i, 0x21);
        operations.put(OperationType.mul_i, 0x22);
        operations.put(OperationType.div_i, 0x23);
        operations.put(OperationType.add_f, 0x24);
        operations.put(OperationType.sub_f, 0x25);
        operations.put(OperationType.mul_f, 0x26);
        operations.put(OperationType.div_f, 0x27);
        operations.put(OperationType.div_u, 0x28);
        operations.put(OperationType.shl, 0x29);
        operations.put(OperationType.shr, 0x2a);
        operations.put(OperationType.and, 0x2b);
        operations.put(OperationType.or, 0x2c);
        operations.put(OperationType.xor, 0x2d);
        operations.put(OperationType.not, 0x2e);
        operations.put(OperationType.cmp_i, 0x30);
        operations.put(OperationType.cmp_u, 0x31);
        operations.put(OperationType.cmp_f, 0x32);
        operations.put(OperationType.neg_i, 0x34);
        operations.put(OperationType.neg_f, 0x35);
        operations.put(OperationType.itof, 0x36);
        operations.put(OperationType.ftoi, 0x37);
        operations.put(OperationType.shrl, 0x38);
        operations.put(OperationType.set_lt, 0x39);
        operations.put(OperationType.set_gt, 0x3a);
        operations.put(OperationType.br, 0x41);
        operations.put(OperationType.br_false, 0x42);
        operations.put(OperationType.br_true, 0x43);
        operations.put(OperationType.call, 0x48);
        operations.put(OperationType.ret, 0x49);
        operations.put(OperationType.callname, 0x4a);
        operations.put(OperationType.scan_i, 0x50);
        operations.put(OperationType.scan_c, 0x51);
        operations.put(OperationType.scan_f, 0x52);
        operations.put(OperationType.print_i, 0x54);
        operations.put(OperationType.print_c, 0x55);
        operations.put(OperationType.print_f, 0x56);
        operations.put(OperationType.print_s, 0x57);
        operations.put(OperationType.println, 0x58);
        operations.put(OperationType.panic, 0xfe);
        return operations;
    }
}
