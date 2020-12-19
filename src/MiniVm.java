

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;



public class MiniVm {
    FunctionEntry _start;
    ArrayList<Byte> output;
    LinkedHashMap<String,GlobalEntry> globalTable;
    LinkedHashMap<String,FunctionEntry> functionTable;
    HashMap<OperationType,Integer> operationTable = Operation.getOperations();
    int magic = 0x72303b3e;
    int version = 0x00000001;

    public MiniVm(LinkedHashMap<String,GlobalEntry> globalTable,LinkedHashMap<String,FunctionEntry> functionTable,FunctionEntry _start){
       this.globalTable = globalTable;
       this.functionTable = functionTable;
       this._start = _start;
    }
    public ArrayList<Byte> getOutput(){
        output = new ArrayList<>();
        output_add(magic,4);
        output_add(version,4);
        output_add(globalTable.size(),4);
        for (Iterator<Map.Entry<String, GlobalEntry>> it = globalTable.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, GlobalEntry> item = it.next();
            output_add(booleanToint(item.getValue().getIsConst()), 1);
            if(item.getValue().getLength()==0){
                output_add(8,4);
                output_add(0L,8);
            }
            else{
                output_add(item.getValue().getLength(), 4);
                output_add(item.getKey());
            }
        }
        output_add(functionTable.size()+1, 4);
        output_add(_start.getId(),4);
        output_add(_start.getRetSlots(),4);
        output_add(_start.getParamSlots(),4);
        output_add(_start.getLocSlots(),4);
        output_add(_start.getinnerInstructions().size(),4);
        for(Instruction i : _start.getinnerInstructions()){
            output_add(operationTable.get(i.getOpt()),1);
            if(i.getX()!=-1){
                if(operationTable.get(i.getOpt())==1)
                    output_add(i.getX(),8);
                else
                    output_add((int)i.getX(),4);
            }
        }
        for (Iterator<Map.Entry<String, FunctionEntry>> it = functionTable.entrySet().iterator(); it.hasNext();){
            Map.Entry<String, FunctionEntry> item = it.next();
            output_add(item.getValue().getId(),4);
            output_add(item.getValue().getRetSlots(),4);
            output_add(item.getValue().getParamSlots(),4);
            output_add(item.getValue().getLocSlots(),4);
            output_add(item.getValue().getinnerInstructions().size(),4);
            for(Instruction i : item.getValue().getinnerInstructions()){
                output_add(operationTable.get(i.getOpt()),1);
                if(i.getX()!=-1){
                    if(operationTable.get(i.getOpt())==1)
                        output_add(i.getX(),8);
                    else
                        output_add((int)i.getX(),4);
                }
            }
        }
        return output;
    }

    public void output_add(int num,int len){
        int start = 8 * (len-1);
        for(int i = 0 ; i < len; i++){
            int part = num >> ( start - i * 8 ) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }
    public void output_add(String str){
        for (int i = 0; i < str.length();i++){
            char c = str.charAt(i);
            output.add((byte) c);
        }
    }
    public void output_add(long num,int len){
        int start = 8 * (len-1);
        for(int i = 0 ; i < len; i++){
            long part = num >> ( start - i * 8 ) & 0xFF;
            byte b = (byte) part;
            output.add(b);
        }
    }
    public int booleanToint(boolean x){
        if(x==false)
            return 1;
        return 0;
    }
}
