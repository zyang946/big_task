

public class GlobalEntry {
    boolean isConst;
    int length;

    GlobalEntry(boolean isConst,int length){
        this.isConst = isConst;
        this.length = length;
    }
    GlobalEntry(boolean isConst){
        this.isConst = isConst;
        this.length = 0;
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
    @Override
    public String toString() {
        return "Global{" +
                "isConst=" + isConst +
                ", count=" + length +
                '}';
    }
}
