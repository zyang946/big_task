

public enum OperationType{
    none,
    nop,
    push,
    pop,
    popn,
    dup,
    loca,
    arga,
    globa,
    load_8,
    load_16,
    load_32,
    load_64,
    store_8,
    store_16,
    store_32,
    store_64,
    alloc,
    free,
    stackalloc,
    add_i,
    sub_i,
    mul_i,
    div_i,
    add_f,
    sub_f,
    mul_f,
    div_f,
    div_u,
    shl,
    shr,
    and,
    or,
    xor,
    not,
    cmp_i,
    cmp_u,
    cmp_f,
    neg_i,
    neg_f,
    itof,
    ftoi,
    shrl,
    set_lt,
    set_gt,
    br,
    br_false,
    br_true,
    call,
    ret,
    callname,
    scan_i,
    scan_c,
    scan_f,
    print_i,
    print_c,
    print_f,
    print_s,
    println,
    panic;
    @Override
    public String toString(){
        switch(this){
            case none:
                return "None";
            case push:
                return "push";
            case pop:
                return "pop";
            case popn:
                return "popn";
            case dup:
                return "dup";
            case loca:
                return "loca";
            case arga:
                return "arga";
            case globa:
                return "globa";
            case load_8:
                return "load.8";
            case load_16:
                return "load.16";
            case load_32:
                return "load.32";
            case load_64:
                return "load.64";
            case store_8:
                return "store.8";
            case store_16:
                return "store.16";
            case store_32:
                return "store.32";
            case store_64:
                return "store.64";
            case alloc:
                return "alloc";
            case free:
                return "free";
            case stackalloc:
                return "stackalloc";
            case add_i:
                return "add.i";
            case add_f:
                return "add.f";
            case sub_i:
                return "sub.i";
            case sub_f:
                return "sub.f";
            case mul_i:
                return "mul.i";
            case mul_f:
                return "mul.f";
            case div_i:
                return "div.i";
            case div_f:
                return "div.f";
            case div_u:
                return "div.u";
            case shl:
                return "shl";
            case shr:
                return "shr";
            case and:
                return "and";
            case or:
                return "or";
            case xor:
                return "xor";
            case not:
                return "not";
            case cmp_i:
                return "cmp.i";
            case cmp_f:
                return "cmp.f";
            case cmp_u:
                return "cmp.u";
            case neg_i:
                return "neg.i";
            case neg_f:
                return "neg.f";
            case itof:
                return "itof";
            case ftoi:
                return "ftoi";
            case shrl:
                return "shrl";
            case set_lt:
                return "set.lt";
            case set_gt:
                return "set.gt";
            case br:
                return "br";
            case br_false:
                return "br.false";
            case br_true:
                return "br.true";
            case call:
                return "call";
            case ret:
                return "ret";
            case callname:
                return "callname";
            case scan_i:
                return "scan.i";
            case scan_f:
                return "scan.f";
            case scan_c:
                return "scan.c";
            case print_i:
                return "print.i";
            case print_f:
                return "print.f";
            case print_c:
                return "print.c";
            case print_s:
                return "print.s";
            case println:
                return "println";
            case panic:
                return "panic";
            default:
                return "InvalidOperator";
        }
    }

}