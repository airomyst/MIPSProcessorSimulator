package MipsProcessor;

public class ALU {

    private int ALUres;
    private boolean zeroFlag;

    private void setALUOutput(int ALUres){

        this.ALUres = ALUres;
        if(ALUres==0) zeroFlag = true;
        else zeroFlag = false;
    }
    public void EvaluateALU(int firOp, int secOp, int ControlSignal) {

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

        ExecuteALU();
    }

    public int getALUres() {
        return ALUres;
    }

    public boolean getZeroFlag() {
        return zeroFlag;
    }

    private void ExecuteALU() {

        System.out.println("ALU output: " + ALUres);
        System.out.println("Zero flag: " + Boolean.compare(zeroFlag, false));
    }
}