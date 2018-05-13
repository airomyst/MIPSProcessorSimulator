package MipsProcessor;

/**Thi class represents the control unit of the processor*/
public class ControlUnit {

    // Control unit control signals
    private boolean Branch, MemRead, MemWrite, RegWrite, isWord, Signed;
    private int ALUOP, RegDst, MemToReg, ALUSrc, Jump;

    // Sets the Control unit signals
    private void setControlSignal(int RegDst, boolean Branch, boolean MemRead, boolean MemWrite, int MemToReg, int ALUOP,
                                  int ALUSrc, boolean RegWrite, int Jump, boolean isWord, boolean Signed) {

        this.RegDst = RegDst;
        this.Branch = Branch;
        this.MemRead = MemRead;
        this.MemWrite = MemWrite;
        this.MemToReg = MemToReg;
        this.ALUOP = ALUOP;
        this.ALUSrc = ALUSrc;
        this.RegWrite = RegWrite;
        this.Jump = Jump;
        this.isWord = isWord;
        this.Signed = Signed;
    }

    /**Evaluates the values of the control unit signals according to the passed OP code*/
    public void ExecuteControlSignal(int opcode) {

        switch (opcode) {

            case 0: //R-Type
                setControlSignal(1, false, false, false, 0, 2, 0, true,
			0, isWord, Signed);
                break;

            case 8: //addi
                setControlSignal(0, false, false, false, 0, 0, 1, true,
			0, isWord, Signed);
                break;

            case 43: //sw
                setControlSignal(RegDst, false, false, true,  MemToReg, 0, 1,
                        false, 0, true, Signed);
                break;

            case 35: //lw
                setControlSignal(0, false, true, false, 1, 0, 1, true,
			0, true, Signed);
                break;

            case 32: //lb
                setControlSignal(0, false, true, false, 1, 0, 1, true,
			0, false, true);
                break;

            case 36: //lbu
                setControlSignal(0, false, true, false, 1, 0, 1, true,
			0, false, false);
                break;

            case 40: //sb
                setControlSignal(RegDst, false, false, true,  MemToReg, 0, 1,
                        false, 0, false, Signed);
                break;

            case 4: //beq
                setControlSignal(RegDst, true, false, false, MemToReg, 1, 0,
                        false, 0, isWord, Signed);
                break;

            case 2: //j
                setControlSignal(RegDst, Branch, false, false, MemToReg, ALUOP,
                        ALUSrc, false, 1, isWord, Signed);
                break;

            case 3: //jal
                setControlSignal(2, Branch, false, false, 3, ALUOP, ALUSrc,
                        true, 1, isWord, Signed);
                break;

            case 10: //slti
                setControlSignal(0, false, false, false, 0, 3, 1, true,
			0, isWord, Signed);
                break;

            case 15: //lui
                setControlSignal(0, false, false, false, 2, ALUOP, ALUSrc,
			true, 0, isWord, Signed);
                break;

            case 13: //ori
                setControlSignal(0, false, false, false, 0, 4, 1, true,
			0, isWord, Signed);
                break;
        }
        
        PrintOutputs();
    }

    private void PrintOutputs() {

        System.out.println("Control signal: RegDst: "+ RegDst);
        System.out.println("Control signal: Branch: "+ Boolean.compare(Branch,false));
        System.out.println("Control signal: MemRead: "+ Boolean.compare(MemRead,false));
        System.out.println("Control signal: MemWrite: "+ Boolean.compare(MemWrite,false));
        System.out.println("Control signal: MemToReg: "+ MemToReg);
        System.out.println("Control signal: ALUOP: "+ ALUOP);
        System.out.println("Control signal: ALUSrc: "+ ALUSrc);
        System.out.println("Control signal: RegWrite: "+ Boolean.compare(RegWrite,false));
        System.out.println("Control signal: Jump: "+ Jump);
        System.out.println("Control signal: isWord: "+ Boolean.compare(isWord,false));
        System.out.println("Control signal: Signed: "+ Boolean.compare(Signed,false));

    }

    /**Returns register destination signal*/
    public int getRegDst() {

        return RegDst;
    }

    /**Returns branching signal*/
    public boolean getBranch() {

        return Branch;
    }

    /**Returns memory read signal*/
    public boolean getMemRead() {

        return MemRead;
    }

    /**Returns ALUOP signal*/
    public int getALUOP() {

        return ALUOP;
    }

    /**Returns register write signal*/
    public boolean getRegWrite() {

        return RegWrite;
    }

    /**Returns jumping signal*/
    public int getJump() {

        return Jump;
    }

    /**Returns memory write signal*/
    public boolean getMemWrite() {

        return MemWrite;
    }

    /**Returns the signal to select data to be written to the register*/
    public int getMemToReg() {

        return MemToReg;
    }

    /**Returns word reading/writing signal*/
    public boolean getIsWord() {

        return isWord;
    }

    /**Returns signed bit reading/writing signal*/
    public boolean getSigned() {

        return Signed;
    }

    /**Returns ALU source signal*/
    public int getALUSrc() {

        return ALUSrc;
    }
}
