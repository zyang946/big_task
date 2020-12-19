

import java.util.Objects;

public class Instruction {
    private OperationType opt;
    long x;
    Object value;

    public Object getValue() {
    	return this.value;
    }
    public void setValue(Object value) {
    	this.value = value;
    }



    public Instruction(OperationType opt) {
        this.opt = opt;
        this.x = 0;
        value = null;
    }

    public Instruction(OperationType opt, long x) {
        this.opt = opt;
        this.x = x;
        value = null;
    }
    public Instruction(OperationType opt, long x,Object value){
        this.opt = opt;
        this.x=x;
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public OperationType getOpt() {
        return opt;
    }

    public void setOpt(OperationType opt) {
        this.opt = opt;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }
}
