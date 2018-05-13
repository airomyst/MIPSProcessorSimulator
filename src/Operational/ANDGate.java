package Operational;

/**a class that represents an AND Gate component**/
public class ANDGate {

    private int Signal; // AND gate output
    private String Source; // Name of the component instance

    /**Instantiates a named AND gate**/
    public ANDGate(String Source){

        this.Source = Source;
    }

    /**Performs an AND logical operation on the two inputs, then prints the component output value**/
    public void ANDing(boolean firSig, boolean secSig){

        boolean Sig = firSig & secSig;
        Signal = Boolean.compare(Sig, false);
        PrintOutputs();
    }

    private void PrintOutputs(){

        System.out.println(Source+" output: "+Signal);
    }

    /**Returns the AND gate output signal**/
    public int getSignal() {

        return Signal;
    }
}
