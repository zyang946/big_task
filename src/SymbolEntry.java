

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SymbolEntry {


    //是否是常数
    boolean isConstant;

    public boolean isConstant() {
    	return this.isConstant;
    }
    public void setConstant(boolean isConstant) {
    	this.isConstant = isConstant;
    }

    //类型(int ,void ,double)
    String type;

    public String getType() {
    	return this.type;
    }
    public void setType(String type) {
    	this.type = type;
    }

    //是否初始化
    boolean isInitialized;

    public boolean isInitialized() {
    	return this.isInitialized;
    }
    public void setInitialized(boolean isInitialized) {
    	this.isInitialized = isInitialized;
    }

    //偏移
    int stackOffset;

    public int getStackOffset() {
    	return this.stackOffset;
    }
    public void setStackOffset(int stackOffset) {
    	this.stackOffset = stackOffset;
    }


    //如果是函数的话具有下面
    boolean isFuction;

    public boolean isFuction() {
    	return this.isFuction;
    }
    public void setFuction(boolean isFuction) {
    	this.isFuction = isFuction;
    }

    //参数个数
    HashMap<String,SymbolEntry> params = new HashMap<>();

    public HashMap<String,SymbolEntry> getParams() {
    	return this.params;
    }
    public void setParams(HashMap<String,SymbolEntry> params) {
    	this.params = params;
    }

    //参数返回类型
    String returnType;

    public String getReturnType() {
    	return this.returnType;
    }
    public void setReturnType(String returnType) {
    	this.returnType = returnType;
    }
    //代码块层数
    int floor;

    public int getFloor() {
        return this.floor;
    }
    public void setFloor(int floor) {
        this.floor = floor;
    }
    int globalId;

    public int getGlobalId() {
    	return this.globalId;
    }
    public void setGlobalId(int globalId) {
    	this.globalId = globalId;
    }

    int localId;

    public int getLocalId() {
    	return this.localId;
    }
    public void setLocalId(int localId) {
    	this.localId = localId;
    }
    


    public SymbolEntry(boolean isConstant,String type,boolean isInitialized,int stackOffset,boolean isFuction,HashMap<String,SymbolEntry> params,String returnType,int floor,int globalId,int localId){
        this.isConstant = isConstant;
        this.type = type;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.isFuction = isFuction;
        this.params = params;
        this.returnType = returnType;
        this.floor = floor;
        this.localId = localId;
        this.globalId = globalId;
    }
    public SymbolEntry(boolean isConstant,String type,boolean isInitialized,int stackOffset,boolean isFuction,HashMap<String,SymbolEntry> params,String returnType){
        this.isConstant = isConstant;
        this.type = type;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.isFuction = isFuction;
        this.params = params;
        this.returnType = returnType;
    }
    public SymbolEntry() {
        
    }
    public static HashMap<String, SymbolEntry> getLibraryTable(){
        HashMap<String, SymbolEntry> libraryTable = new HashMap<>();
        HashMap<String,SymbolEntry> params = new HashMap<>();
        libraryTable.put("getint",new SymbolEntry(false,"void",true,0,true,params,"int"));
        libraryTable.put("getdouble",new SymbolEntry(false,"void",true,0,true,params,"double"));
        libraryTable.put("getchar",new SymbolEntry(false,"void",true,0,true,params,"int"));
        libraryTable.put("putln",new SymbolEntry(false,"void",true,0,true,params,"void"));
        SymbolEntry param1 = new SymbolEntry();
        param1.setType("int");
        params.put("1",param1);
        libraryTable.put("putint",new SymbolEntry(false,"void",true,0,true,params,"void"));
        libraryTable.put("putchar",new SymbolEntry(false,"void",true,0,true,params,"void"));
        libraryTable.put("putstr",new SymbolEntry(false,"void",true,0,true,params,"void"));
        params.remove("1");
        SymbolEntry param2 = new SymbolEntry();
        param2.setType("double");
        params.put("1",param2);
        libraryTable.put("getchar",new SymbolEntry(false,"void",true,0,true,params,"void"));
        
        return libraryTable;
    } 
}
