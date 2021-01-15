public class BreakAndContinue {
    //内部指令
    Instruction instruction;
    //所处的位置
    int local;
    //
    int whileNum;
    
    public BreakAndContinue(Instruction instruction,int local,int whileNum){
        this.instruction = instruction;
        this.local = local;
        this.whileNum = whileNum;
    }
    public Instruction getInstruction(){
        return this.instruction;
    }
    public void setInstruction(Instruction instruction){
        this.instruction = instruction;
    }
    public int getLocal(){
        return this.local;
    }
    public void setLocal(int local){
        this.local = local;
    }
    public int getWhileNum(){
        return this.whileNum;
    }
    public void setWhileNum(int whileNum){
        this.whileNum = whileNum;
    }
}

