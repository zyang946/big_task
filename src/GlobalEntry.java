

public class GlobalEntry {
    boolean isConst;
    int length;
    String item;




    GlobalEntry(boolean isConst,int length,String item){
        this.isConst = isConst;
        this.length = length;
        this.item = item;
    }
    GlobalEntry(boolean isConst){
        this.isConst = isConst;
        this.length = 0;
        this.item = "";
    }
    public boolean getIsConst() {
    	return this.isConst;
    }
    public void setIsConst(boolean isConst) {
        this.isConst = isConst;
        this.length = 0;
    }


    public int getLength() {
    	return this.length;
    }
    public void setLength(int length) {
    	this.length = length;
    }
    public String getItem() {
    	return this.item;
    }
    public void setItem(String item) {
    	this.item = item;
    }
    @Override
    public String toString() {
        return "Global{" +
                "isConst=" + isConst +
                ", count=" + length +
                ", item =" + item +
                '}';
    }
}
