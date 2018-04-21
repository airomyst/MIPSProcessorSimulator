package Operational;

public class Adder {

    private int Result;
    private String Source;

    public Adder(String Source){

        this.Source = Source;
    }

    public void Add(int firOp, int secOp){

        Result = firOp + secOp;
        ExecuteAdder();
    }

    private void ExecuteAdder(){

        System.out.println(Source+" output: "+Result);
    }
    public int getResult() {

        return Result;
    }
}
