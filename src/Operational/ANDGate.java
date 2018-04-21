package Operational;

public class ANDGate {

    private int Signal;
    private String Source;

    public ANDGate(String Source){

        this.Source = Source;
    }

    public void ANDing(boolean firSig, boolean secSig){

        boolean Sig = firSig & secSig;
        Signal = Boolean.compare(Sig, false);
        ExecuteANDGate();
    }

    private void ExecuteANDGate(){

        System.out.println(Source+" output: "+Signal);
    }
    public int getSignal() {

        return Signal;
    }
}
