package MipsProcessor;

import Operational.Assembler;
import javafx.util.Pair;
import java.io.PrintStream;
import java.util.*;

import static Operational.Functions.getSignedBin;

/**This class represents both data and instructions memory*/
public class Memory {

    private HashMap<Integer, byte[]> DataLocations;
    private HashMap<Integer, String> InstrctLocations;
    private int ReadData; // memory output

    public Memory() {

        InstrctLocations = new HashMap<>();
        DataLocations = new HashMap<>();
    }


    /**Loads both data and instructions to the memory and saves them*/
    public void InitializeMemory(String PROGRAM, int PC, int DataLOC, PrintStream memPS){

        // Loads the instructions memory with the assembled instructions and fill data memory if initial data was given
        String INSTRUCTIONS = Assembler.LoadInstructions(PROGRAM);
        Pair<HashMap<String,Integer>, String[]> DATA = Assembler.LoadData(PROGRAM, DataLOC);
        String[] BinaryInstructions;

        if(DATA!= null) {

            BinaryInstructions = Assembler.getBinInstructions(INSTRUCTIONS.split("\n"), PC, DATA.getKey());
            if(DATA.getValue()!=null) {

                for(int i=0;i<DATA.getValue().length;i++) {
                    for (String word : DATA.getValue()[i].split("\\s*,\\s*")) {

                        WriteToDataMEM(Integer.parseInt(word), DataLOC, true, true, memPS);
                        DataLOC += 4;
                    }
                }
            }
        }

        else BinaryInstructions = Assembler.getBinInstructions(INSTRUCTIONS.split("\n"), PC, null);


        for(int i=0; i < BinaryInstructions.length; i++){

            if (BinaryInstructions[i]!=null) {
                InstrctLocations.put(PC, BinaryInstructions[i]);
                PC += 4;
            }
        }
    }

    public String FetchInstruction(int PC){

        String INSTRUCTION = InstrctLocations.get(PC);
        if(INSTRUCTION!=null) PrintInstructionMEMOutputs(INSTRUCTION);

        return INSTRUCTION;
    }

    private void PrintInstructionMEMOutputs(String INSTRUCTION){

        System.out.println("Pseudo-relative jump address field of instruction : " +Long.parseLong(INSTRUCTION.substring(6),2));
        System.out.println("Op Code field of instruction: " +Integer.parseInt(INSTRUCTION.substring(0,6),2));
        System.out.println("Rs field of instruction: " +Integer.parseInt(INSTRUCTION.substring(6,11), 2));
        System.out.println("Rt field of instruction: " +Integer.parseInt(INSTRUCTION.substring(11,16), 2));
        System.out.println("Rd field of instruction: " +Integer.parseInt(INSTRUCTION.substring(16,21),2));
        System.out.println("Offset/Constant field of instruction: " +getSignedBin(INSTRUCTION.substring(16))+" (in 2's Complement)");
        System.out.println("Shift amount field of instruction: " +Integer.parseInt(INSTRUCTION.substring(21,26),2));
        System.out.println("Function code of instruction: " +Integer.parseInt(INSTRUCTION.substring(26),2));
    }

    private void PrintDataMEMContents(PrintStream memPS){

        memPS.println("Current memory contents:");
        ArrayList<Integer> LOCS = new ArrayList<>(DataLocations.keySet());
        Collections.sort(LOCS);
        for(int LOC : LOCS){
            int out=0;
            for (int i = 0; i < 4; i++) out += (DataLocations.get(LOC)[i] & 0xFF) << (24 - 8 * i);
            memPS.println("Word Address ("+LOC+ ") has the value "+out);
        }
        memPS.println();
        for(int i=0;i<65;i++) memPS.print("-");
        memPS.println();
    }


    /**Writes data into the data memory based on passed control signals**/
    public void WriteToDataMEM(int val, int LOC, boolean MemWrite, boolean SWord, PrintStream memPS){

        if (MemWrite) {

            if (SWord & LOC%4!=0 || LOC < 0) {
                throw new IllegalArgumentException("Error: Invalid writing address!");
            }
            byte[] WriteData;
            if(SWord){

                WriteData = new byte[4];
                for(int i=3;i>=0;i--) WriteData[i]= (byte) ((val >> 8*(3-i)) & 0xFF);
                DataLocations.put(LOC, WriteData);
                memPS.println("Word Address ("+LOC+ ") now has the value "+val);
                memPS.println();
            }

            else {

                if (DataLocations.get(LOC-(LOC%4))==null) WriteData = new byte[4];
                else WriteData = DataLocations.get(LOC-(LOC%4));
                WriteData[LOC%4] = (byte) val;
                DataLocations.put(LOC-(LOC%4),WriteData);
                memPS.println("Byte Address ("+LOC+ ") now has the value "+val);
                memPS.println();
            }

        }

        PrintDataMEMContents(memPS);
    }

    /**Returns the output data from the data memory*/
    public int getReadData(){

        return ReadData;
    }

    /**Reads data from data memory based on passed control signals*/
    public int ReadFromDataMEM(int LOC, boolean MemRead, boolean LWord, boolean Signed) {

        if (MemRead) {
            if (LWord & LOC % 4 != 0 || LOC < 0) {
                throw new IllegalArgumentException("Error: Invalid reading address!");
            }

            if (LWord) {
                ReadData =0;
                if (!(DataLocations.get(LOC) == null))
                    for (int i = 0; i < 4; i++) ReadData += (DataLocations.get(LOC)[i] & 0xFF) << (24 - 8 * i);
            }

            else {

                if (DataLocations.get(LOC - (LOC % 4)) == null) ReadData = 0;
                else {

                    if (Signed) ReadData = DataLocations.get(LOC - (LOC % 4))[LOC % 4];
                    else ReadData = DataLocations.get(LOC - (LOC % 4))[LOC % 4] & 0xFF;
                }
            }

            PrintDataMEMOutputs();
        }
        else System.out.println("Data memory output: Unknown");
        return ReadData;
    }

    private void PrintDataMEMOutputs(){

        System.out.println("Data memory output: "+ ReadData);
    }
}
