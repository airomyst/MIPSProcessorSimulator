package MipsProcessor;

import javax.swing.*;

/**Thi class represents the register file component in the processor*/
public class RegisterFile {

    // Attributes for the register file outputs and Registers in it
    private Register[] Registers;
    private int WriteRegister; // Number of the register that is written to
    private int frstReadData;
    private int scndReadData;

    public RegisterFile(JTextField[] RegVals){

        Registers = new Register[32];
        for(int i=0; i<32; i++) Registers[i] = new Register();
        Registers[29].SetValue(1000);//set inital $sp value
        for(int i=0; i<32; i++) RegVals[i].setText(""+Registers[i].getValue());
    }

    /**Reads the values stored in the specified registers*/
    public void ReadFromRegisters(int frstRegister, int scndRegister){

        frstReadData = Registers[frstRegister].getValue();
        scndReadData = Registers[scndRegister].getValue();
        PritnOutputs();
    }

    /**Returns the first read data output of the register file*/
    public int getFrstReadData(){

        return frstReadData;

    }

    /**Returns the second read data output of the register file*/
    public int getScndReadData(){

        return scndReadData;
    }

    private void PritnOutputs(){

        System.out.println("Read data 1: "+ frstReadData);
        System.out.println("Read data 2: "+ scndReadData);
    }

    /**Write the passed value in the register that will be written to*/
    public void WriteToRegister(int Writedata, boolean RegWrite, JTextField[] RegVals){

        if(RegWrite && !(WriteRegister==0)){

            Registers[WriteRegister].SetValue(Writedata);
            RegVals[WriteRegister].setText(""+Writedata);
        }
    }

    /**Sets the number of the register to be written to*/
    public void setWriteRegister(int regNum){

        WriteRegister = regNum;
    }
}
