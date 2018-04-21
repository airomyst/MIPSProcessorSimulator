package Operational;

public class LeftShifter {

    private int Result;
    private String Source;

    public LeftShifter(String Source){

        this.Source = Source;
    }

    public void LeftShift(int val, int shamt){

        Result = val << shamt;
        ExecuteLeftShifter();
    }

    private void ExecuteLeftShifter(){

        System.out.println(Source+" output: "+Result);
    }
    public int getResult() {

        return Result;
    }
}
