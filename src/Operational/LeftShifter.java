package Operational;

/**a class that represents a left shifter component**/
public class LeftShifter {

    private int Result; // Left shifter output
    private String Source; // Name of the component instance

    /**Instantiates a named adder**/
    public LeftShifter(String Source){

        this.Source = Source;
    }

    /**Performs a shifting operation on the passed value with shifting amount specified, then prints the component output value**/
    public void LeftShift(int val, int shamt){
        Result = val << shamt;
        PrintOutputs();
    }

    private void PrintOutputs(){
        System.out.println(Source+" output: "+Result);
    }

    /**Returns the left shifter output result**/
    public int getResult() {

        return Result;
    }
}
