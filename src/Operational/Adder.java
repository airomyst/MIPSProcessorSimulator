package Operational;

/**a class that represents an adder component**/
public class Adder {

    private int Result; // Adder output
    private String Source; // Name of the component instance

    /**Instantiates a named adder**/
    public Adder(String Source){

        this.Source = Source;
    }

    /**Performs an addition operation on the two inputs, then prints the component output value**/
    public void Add(int firOp, int secOp){

        Result = firOp + secOp;
        PrintOutputs();
    }

    private void PrintOutputs(){

        System.out.println(Source+" output: "+Result);
    }

    /**Returns the adder output value**/
    public int getResult() {

        return Result;
    }
}
