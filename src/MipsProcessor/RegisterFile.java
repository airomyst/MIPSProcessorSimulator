package MipsProcessor;

import javax.swing.*;

public class RegisterFile {

    private Register[] Registers;
    private int WriteRegister;
    private int frstReadData;
    private int scndReadData;

    public RegisterFile(){

        Registers = new Register[32];
        for(int i=0; i<32; i++) Registers[i] = new Register();
    }

    public void ReadFromRegisters(int frstRegister, int scndRegister){

        frstReadData = Registers[frstRegister].getValue();
        scndReadData = Registers[scndRegister].getValue();
        ExecuteRegisterFile();
    }

    public int getFrstReadData(){

        return frstReadData;

    }

    public int getScndReadData(){

        return scndReadData;

    }

    public void ExecuteRegisterFile(){

        System.out.println("Read data 1: "+ frstReadData);
        System.out.println("Read data 2: "+ scndReadData);
    }

    public void WriteToRegister(int Writedata, boolean RegWrite, JTextField[] RegVals){

        if(RegWrite && WriteRegister==0){
            throw new IllegalArgumentException("Error: Cannot change value in register $0");
        }
        if(RegWrite){

            Registers[WriteRegister].SetValue(Writedata);
            RegVals[WriteRegister].setText(""+Writedata);
        }
    }

    public void setWriteRegister(int regNum){

        WriteRegister = regNum;
    }
}
