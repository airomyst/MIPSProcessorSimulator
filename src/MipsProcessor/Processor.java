package MipsProcessor;

import Operational.*;

import javax.swing.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static Operational.Functions.*;

public class Processor {

    private static int AppendPCBits(int val, int PC){

        //appends the Last 4 bits in The PC Register for the Jump instructions
        String binVal = String.format("%" + Integer.toString(26) + "s", Integer.toBinaryString(val)).replace(" ", "0");
        String PCVal = String.format("%" + Integer.toString(32) + "s", Integer.toBinaryString(PC)).replace(" ", "0");
        binVal = PCVal.substring(0,4) + binVal;
        System.out.println("PC bits appended to jump address.");
        return Integer.parseInt(binVal, 2);
    }

    private static int SignExtend(String BinaryInteger){

        int res = getSignedBin(BinaryInteger);

        System.out.println("Sign extender output: "+res);
        return res;
    }

    public static void runProcessor(int PC, int DATALoc, String path, JTextField[] RegVals, PrintStream memPS){

        String PROGRAM = readFile(path, StandardCharsets.UTF_8);
        if(PC%4!=0 || DATALoc %4 !=0) throw new IllegalArgumentException("Error: Address has to be a multiple of 4!");

        String INSTRUCTION;
        int rsRegister, rtRegister, rdRegister, Constant;

        //initialize all components
        Memory mainMEM = new Memory();
        MUX RegDstRegisterMUX = new MUX("RegDestination Mux");
        ControlUnit ControlUnit = new ControlUnit();
        RegisterFile RegisterFile = new RegisterFile();
        ALUControl ALUControl = new ALUControl();
        MUX ALUSrcMUX = new MUX("ALU source Mux (second input)");
        MUX ALUShiftMUX = new MUX("ALU shift Mux (first input)");
        ANDGate BranchAND = new ANDGate("Branch AND Gate");
        ALU ALU = new ALU();
        LeftShifter ImmediateShifter = new LeftShifter("Load Upper Immediate Shift Left");
        Adder PCAdder = new Adder("PC Adder");
        Adder BranchAdder = new Adder("Branch Adder");
        MUX BranchMUX = new MUX("Branch Mux");
        LeftShifter BranchShifter = new LeftShifter("Branch Shifter");
        LeftShifter JumpShifter = new LeftShifter("Jump Shifter");
        MUX JumpMUX = new MUX("Jump Mux");
        MUX JrMUX = new MUX("Jr Mux");
        MUX DataToRegMUX = new MUX("Data To Register Mux");
        ANDGate RegWriteAND = new ANDGate("RegWrite AND Gate");
        mainMEM.InitializeMemory(PROGRAM, PC, DATALoc, memPS);
        //print some output separators
        System.out.println("Successfully assembled the code and loaded both data and instructions memory!");
        for(int i=0;i<220;i++)System.out.print("-");
        System.out.println();
        System.out.println();

        while(true) {

            INSTRUCTION = mainMEM.FetchInstruction(PC);

            if (INSTRUCTION ==null) break;

            System.out.println("PC Output: "+PC);

            ControlUnit.EvaluateControlSignal(Integer.parseInt(INSTRUCTION.substring(0, 6), 2));

            //Load target Registers
            rsRegister = Integer.parseInt(INSTRUCTION.substring(6, 11), 2);
            rtRegister = Integer.parseInt(INSTRUCTION.substring(11, 16), 2);
            rdRegister = Integer.parseInt(INSTRUCTION.substring(16, 21), 2);
            RegDstRegisterMUX.Select(new int[]{rtRegister, rdRegister, 31}, ControlUnit.getRegDst());

            //get Data from registers
            RegisterFile.ReadFromRegisters(rsRegister, rtRegister);
            RegisterFile.setWriteRegister(RegDstRegisterMUX.getSelection());

            //get immediate value
            Constant = SignExtend(INSTRUCTION.substring(16));

            ALUControl.EvaluateALUControl(ControlUnit.getALUOP(), Integer.parseInt(INSTRUCTION.substring(26), 2));

            //generate ALU inputs
            ALUShiftMUX.Select(new int[]{RegisterFile.getFrstReadData(), Integer.parseInt(INSTRUCTION.substring(21, 26), 2)},
                    ALUControl.getShiftSignal());
            ALUSrcMUX.Select(new int[]{RegisterFile.getScndReadData(), Constant}, ControlUnit.getALUSrc());

            ALU.EvaluateALU(ALUShiftMUX.getSelection(), ALUSrcMUX.getSelection(), ALUControl.getAluSignal());

            //write and read from data memory
            mainMEM.WriteToDataMEM(RegisterFile.getScndReadData(), ALU.getALUres(), ControlUnit.getMemWrite(), ControlUnit.getIsWord(), memPS);
            mainMEM.ReadFromDataMEM(ALU.getALUres(), ControlUnit.getMemRead(), ControlUnit.getIsWord(), ControlUnit.getSigned());

            PCAdder.Add(PC, 4); //increment PC

            //get relative address
            BranchShifter.LeftShift(Constant, 2);
            BranchAdder.Add(PCAdder.getResult(), BranchShifter.getResult());
            BranchAND.ANDing(ALU.getZeroFlag(), ControlUnit.getBranch());

            BranchMUX.Select(new int[]{PCAdder.getResult(), BranchAdder.getResult()}, BranchAND.getSignal());

            //get psuedo-relative address
            JumpShifter.LeftShift(Integer.parseInt(INSTRUCTION.substring(6), 2), 2);

            JumpMUX.Select(new int[]{BranchMUX.getSelection(), AppendPCBits(JumpShifter.getResult(), PCAdder.getResult())},
                    ControlUnit.getJump());

            JrMUX.Select(new int[]{JumpMUX.getSelection(), rsRegister}, ALUControl.getJrSignal());

            //get the new PC Register value
            PC = JrMUX.getSelection();

            //get upper immediate value
            ImmediateShifter.LeftShift(Constant, 16);

            //select data to write to the register
            DataToRegMUX.Select(new int[]{ALU.getALUres(), mainMEM.getReadData(), ImmediateShifter.getResult(), PCAdder.getResult()},
                    ControlUnit.getMemToReg());

            //writing to register
            RegWriteAND.ANDing(ControlUnit.getRegWrite(), !(ALUControl.getJrSignal() != 0));
            RegisterFile.WriteToRegister(DataToRegMUX.getSelection(), RegWriteAND.getSignal() != 0, RegVals);

            //Print output seperations
            System.out.println();
            for(int i=0;i<210;i++)System.out.print("-");
            System.out.println();
            System.out.println();

            memPS.println();
            for(int i=0;i<65;i++) memPS.print("-");
            memPS.println();

            //Pause the thread
            synchronized (Thread.currentThread()){
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {

                }
            }
        }
        System.out.println("Program finished successfully!");
    }

    public static void main(String[] args){

        GraphicalInterface gi = new GraphicalInterface();
        PrintStream printStream = new PrintStream(new CustomOutputStream(gi.getTxtArea())); //to redirect output to the graphical interface
        System.setOut(printStream);
        System.setErr(printStream);
        gi.setVisible(true);
    }
}
