package MipsProcessor;

public class ALUControl {

    private int ALUControlSignal, shift, jr;

    private void setALUControlSignals(int ALUControlSignal, int shift,int jr){

        this.ALUControlSignal = ALUControlSignal;
        this.shift = shift;
        this.jr = jr;
    }
    public void EvaluateALUControl(int ALUOp, int funct) {

        switch (ALUOp) {
            case 0:
                setALUControlSignals(2,0, 0);
                break;
            case 1:
                setALUControlSignals(6,0, 0);
                break;
            case 2:
                switch (funct) {
                    case 32: //add
                        setALUControlSignals(2,0, 0);
                        break;
                    case 8: //jr
                        setALUControlSignals(ALUControlSignal,shift, 1);
                        break;
                    case 42: //slt
                        setALUControlSignals(7,0, 0);
                        break;
                    case 39: //nor
                        setALUControlSignals(12,0, 0);
                        break;
                    case 0: //sll
                        setALUControlSignals(4,1, 0);
                        break;
                }
                break;
            case 3: //slti
                setALUControlSignals(7,0, 0);
                break;
            case 4: //ori
                setALUControlSignals(1,0, 0);
                break;
        }

        ExecuteALUControl();
    }

    private void ExecuteALUControl(){

        System.out.println("ALU control code signal: "+ ALUControlSignal);
        System.out.println("ALU control shift signal: "+ shift);
        System.out.println("ALU control jump register signal: "+ jr);
    }

    public int getAluSignal(){

        return ALUControlSignal;
    }

    public int getShiftSignal(){

        return shift;
    }

    public int getJrSignal(){

        return jr;
    }
}