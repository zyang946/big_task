

import java.util.ArrayList;
import java.util.HashMap;



public class FunctionEntry {
    int id;//序号

    //如果返回值是int，则为1；是double，则为2；是void，则为0
    int retSlots;

    //参数列表大小
    int paramSlots;

    //局部变量个数
    int locSlots;

    //返回类型
    String returnType;

    //内部指令
    ArrayList<Instruction> innerInstructions;




    public FunctionEntry(int id,int retSlots,int paramSloct,int locSlots,String returnString, ArrayList<Instruction> innerInstructions){
        this.id = id;
        this.retSlots = retSlots;
        this.locSlots = locSlots;
        this.returnType = returnString;
        this.innerInstructions = innerInstructions;
    }
    public int getId() {
    	return this.id;
    }
    public void setId(int id) {
    	this.id = id;//序号
    }
    public int getRetSlots() {
    	return this.retSlots;
    }
    public void setRetSlots(int retSlots) {
    	this.retSlots = retSlots;
    }
    public int getParamSlots() {
    	return this.paramSlots;
    }
    public void setParamSlots(int paramSlots) {
    	this.paramSlots = paramSlots;
    }
    public int getLocSlots() {
    	return this.locSlots;
    }
    public void setLocSlots(int locSlots) {
    	this.locSlots = locSlots;
    }    
    public String getReturnType() {
    	return this.returnType;
    }
    public void setReturnType(String returnType) {
    	this.returnType = returnType;
    }
    public ArrayList<Instruction> getinnerInstructions() {
    	return this.innerInstructions;
    }
    public void setinnerInstructions(ArrayList<Instruction> innerInstructions) {
    	this.innerInstructions = innerInstructions;
    }

    @Override
    public String toString(){
        return "Function{" +
        "id=" + this.id +
        ", \nlocSlots=" + this.locSlots +
        ", \nparamSlots=" + this.paramSlots +
        ", \nbody=" + this.innerInstructions +
        ", \nretSlots=" + this.retSlots +
        ", \nreturnType=" + this.returnType +
        '}';
    }
}
