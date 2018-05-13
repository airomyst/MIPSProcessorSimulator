package MipsProcessor;

/**This class represents the ALU control*/
public class ALUControl {

    // ALU control outputs
    private int ALUControlSignal, shift, jr;

    /**Sets all the ALU control outputs*/
    private void setALUControlSignals(int ALUControlSignal, int shift,int jr){

        this.ALUControlSignal = ALUControlSignal;
        this.shift = shift;
        this.jr = jr;
    }

    /**Determines the output values of the ALU control based on the passed inputs*/
    public void ExecuteALUControl(int ALUOp, int funct) {

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

        PrintOutputs();
    }

    private void PrintOutputs(){

        System.out.println("ALU control code signal: "+ ALUControlSignal);
        System.out.println("ALU control shift signal: "+ shift);
        System.out.println("ALU control jump register signal: "+ jr);
    }

    /**Returns the control signal that will passed to the ALU*/
    public int getAluSignal(){

        return ALUControlSignal;
    }

    /**Returns a shift control signal for shift instructions*/
    public int getShiftSignal(){

        return shift;
    }

    /**Returns a Jump register control signal for jr instruction*/
    public int getJrSignal(){

        return jr;
    }
}