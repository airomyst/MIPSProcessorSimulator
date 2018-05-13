package MipsProcessor;

/**A class that represents the Arithmetic logic unit in the processor*/
public class ALU {

    //ALU OUTPUT SIGNALS
    private int ALUres;
    private boolean zeroFlag;

    /**Sets all the ALU outputs**/
    private void setALUOutput(int ALUres){

        this.ALUres = ALUres;
        if(ALUres==0) zeroFlag = true;
        else zeroFlag = false;
    }

    /**Performs an arithmetic operation on the two passed inputs based on the input signal, then prints all the output signals**/
    public void ExecuteALU(int firOp, int secOp, int ControlSignal) {

        switch (ControlSignal) {
            case 6:
                setALUOutput(firOp - secOp);
                break;
            case 2:
                setALUOutput(firOp + secOp);
                break;
            case 1:
                setALUOutput(firOp | secOp);
                break;
            case 12:
                setALUOutput(~(firOp | secOp));
                break;
            case 7:
                setALUOutput((firOp < secOp)? 1 : 0);
                break;
            case 4:
                setALUOutput(secOp << firOp);
                break;
        }

        PrintOutputs();
    }

    /**Returns the result of the performed operation by the ALU**/
    public int getALUres() {
        return ALUres;
    }

    /**Returns the zero flag signal of the ALU**/
    public boolean getZeroFlag() {
        return zeroFlag;
    }

    private void PrintOutputs() {

        System.out.println("ALU output: " + ALUres);
        System.out.println("Zero flag: " + Boolean.compare(zeroFlag, false));
    }
}