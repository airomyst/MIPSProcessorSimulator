package Operational;

import javafx.util.Pair;

import java.util.*;

public class Assembler {

    private static final HashMap<String, String> OPFcodes; //A hash map that maps each OPcode/function code to its equivalent

    static {
        OPFcodes = new HashMap<>();
        OPFcodes.put("add", "100000"); //32
        OPFcodes.put("sll", "000000"); //0
        OPFcodes.put("nor", "100111"); //39
        OPFcodes.put("jr", "001000"); //8
        OPFcodes.put("slt", "101010"); //42
        OPFcodes.put("addi", "001000"); //8
        OPFcodes.put("sw", "101011"); //43
        OPFcodes.put("lw", "100011"); //35
        OPFcodes.put("lb", "100000"); //32
        OPFcodes.put("lbu", "100100"); //36
        OPFcodes.put("sb", "101000"); //40
        OPFcodes.put("beq", "000100"); //4
        OPFcodes.put("j", "000010"); //2
        OPFcodes.put("jal", "000011"); //3
        OPFcodes.put("slti", "001010"); //10
        OPFcodes.put("lui", "001111"); //15
        OPFcodes.put("ori", "001101"); //13
    }

    // Returns the binary representation for the register number
    private static String getRegisterBin(String regNAME) {

        if (regNAME.equals("$sp")) return getBinStr(29, 5);
        else if (regNAME.charAt(1) == 's') return getBinStr(16 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.charAt(1) == 't') {
            if (regNAME.charAt(2) == '8') return getBinStr(24, 5);
            else if (regNAME.charAt(2) == '9') return getBinStr(25, 5);
            else return getBinStr(8 + regNAME.charAt(2) - '0', 5);
        }
        else if (regNAME.equals("$at")) return getBinStr(1, 5);
        else if (regNAME.charAt(1) == 'a') return getBinStr(4 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.charAt(1) == 'v') return getBinStr(2 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.charAt(1) == 'k') return getBinStr(26 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.equals("$gp")) return getBinStr(28, 5);
        else if (regNAME.equals("$fp")) return getBinStr(30, 5);
        else if (regNAME.equals("$ra")) return getBinStr(31, 5);
        else if (Character.isDigit(regNAME.charAt(1)))
            return getBinStr(Integer.parseInt(regNAME.split("\\$")[1]), 5);

        throw new IllegalArgumentException("Error: Invalid register name");
    }

    // Returns the input number as a string with length equals to the number of digits (helper function)
    private static String getBinStr(int num, int digits) {

        // Special case to handle conversion for signed numbers for constant and offsets
        if(digits==16)
            return  String.format("%16s", Integer.toBinaryString(0xFFFF & num)).replace(" ", "0");

        else {
            // Checks to see if the desired number of bits is more or less than the actual numbers'
            int tmpLength = Integer.toBinaryString(num).length();

            if(!(tmpLength>digits))
                // Append zeros if desired bits are more than the numbers'
                return String.format("%" + Integer.toString(digits) + "s", Integer.toBinaryString(num)).replace(" ", "0");
            // In case the number's bits are more than required takes least significant bits equal to the desired number of bits
            else return Integer.toBinaryString(num).substring(tmpLength-digits);
        }
    }

    //returns the binary representation of an R-type format instruction
    private static String toRType(String rsn, String rtn, String rdn, int shamt, String funct) {

        if (funct == null) throw new IllegalArgumentException("Error: Invalid instruction name");

        String rs = getRegisterBin(rsn);
        String rt = getRegisterBin(rtn);
        String rd = getRegisterBin(rdn);

        return "000000" + rs + rt + rd + getBinStr(shamt, 5) + funct;
    }

    //returns the binary representation of an J-type format instruction
    private static String toJType(String opcode, int cons) {

        if (opcode == null) throw new IllegalArgumentException("Error: Invalid instruction name");

        return opcode + getBinStr(cons, 32).substring(4, 30);
    }

    //return binary representation of an I-type format instruction
    public static String toIType(String opcode, String rsn, String rtn, int cons) {

        if (opcode == null) throw new IllegalArgumentException("Error: Invalid instruction name");

        String rs = getRegisterBin(rsn);
        String rt = getRegisterBin(rtn);
        return opcode + rs + rt + getBinStr(cons, 16);
    }

    /**takes an array of assembly-written instructions and slices them properly to get info as to doing the binary conversion**/
    public static String[] getBinInstructions(String[] INSTRUCTIONS, int PC, HashMap<String, Integer> MapData) {

        //creates a hash map that maps each label to its line number in the array of instructions
        HashMap<String, Integer> MapLabels = new HashMap<>();
        int linesCounter = 0, extras = 0;

        /*
        First loop:
            Prepares for parsing instructions and handling label addressing instructions.
            Checks for pseudo instructions.
            Checks for and saves labels in the hash map.
            Removes labels from each instruction in the instructions array and save it.
        */
        for (int i = 0; i < INSTRUCTIONS.length; i++, linesCounter++) {

            // Separates labels from the instruction name
            String[] sepLabel = INSTRUCTIONS[i].split(": ");
            String instrctNAME;

            // Removes labels and get instruction name from the string
            if (INSTRUCTIONS[i].contains(":")) {

                if(sepLabel.length > 1){

                    instrctNAME = sepLabel[1].split(" ")[0];
                    INSTRUCTIONS[i] = sepLabel[1];
                }
                else {
                    // In case there is a label at the last line
                    instrctNAME = "";
                    INSTRUCTIONS = Arrays.copyOf(INSTRUCTIONS, INSTRUCTIONS.length-1);
                }

                MapLabels.put(sepLabel[0], linesCounter);
            }

            else instrctNAME = sepLabel[0].split(" ")[0];

            // Checks for pseudo instructions
            if (instrctNAME.equals("la")) extras++;
            else if (instrctNAME.equals("blt")) extras +=2;
        }

        linesCounter = 0;
        String[] BinaryInstructions = new String[INSTRUCTIONS.length + extras];

        // Translates every instruction to binary correspondingly
        for (int i = 0,j=0; i < BinaryInstructions.length; i++, linesCounter++, j++) {
            // Split the instruction into recognizable divisions
            String[] div = INSTRUCTIONS[j].split("(\\s*,+\\s*)|\\s+");
            if (div.length == 4) {

                if (div[3].charAt(0) == '$' | div[0].equals("sll")) {

                    if (div[0].equals("sll"))
                        BinaryInstructions[i] = toRType("$0", div[2], div[1], Integer.parseInt(div[3]),
                                OPFcodes.get(div[0]));

                    else
                        BinaryInstructions[i] = toRType(div[2], div[3], div[1], 0, OPFcodes.get(div[0]));
                }

                else if (div[0].equals("blt")) {

                    if(!MapLabels.containsKey(div[3])) throw new IllegalArgumentException("Error: Addressing label does not exist!");
                    BinaryInstructions[i] = toIType(OPFcodes.get("addi"), div[2],"$at",-1);
                    i++;
                    BinaryInstructions[i] = toRType("$at", div[1], "$at", 0, OPFcodes.get("slt"));
                    i++;
                    if (Character.isLetter(div[3].charAt(0))) {
                        BinaryInstructions[i] = toIType(OPFcodes.get("beq"), "$at", "$0",
                                (MapLabels.get(div[3]) - linesCounter - 1));
                    }

                    else BinaryInstructions[i] = toIType(OPFcodes.get("beq"), "$at", "$0", Integer.parseInt(div[3])-linesCounter-1);
                    linesCounter+=2;
                }

                else if (Character.isLetter(div[3].charAt(0))) {

                    if(!MapLabels.containsKey(div[3])) throw new IllegalArgumentException("Error: Addressing label does not exist!");

                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), div[2], div[1],
                            (MapLabels.get(div[3]) - linesCounter - 1));
                }
                else
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), div[2], div[1], Integer.parseInt(div[3])-linesCounter-1);
            }

            else if (div.length == 3) {

                if (div[0].equals("move"))
                    BinaryInstructions[i] = toRType(div[2], "$0", div[1], 0, OPFcodes.get("add"));

                else if(div[0].equals("la")) {

                    if(MapData.get(div[2])!=null){

                        BinaryInstructions[i] = toIType(OPFcodes.get("lui"), "$0", "$at",
                                Integer.parseInt(getBinStr(MapData.get(div[2]),32).substring(0,16),2));
                        i++;
                        BinaryInstructions[i] = toIType(OPFcodes.get("ori"), "$at", div[1],
                                Integer.parseInt(getBinStr(MapData.get(div[2]),32).substring(16),2));
                    }

                    else throw new IllegalArgumentException("Error: Data Label does not exist!");
                    linesCounter++;
                }

                else if(div[0].equals("lui"))
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]),"$0", div[1], Integer.parseInt(div[2]));

                else {

                    //splits offset and register rs (loading and storing instructions)
                    String[] _temp = div[2].split("[()]");
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), _temp[1], div[1], Integer.parseInt(_temp[0]));
                }
            }

            else if (div.length == 2) {
                if (div[0].equals("jr"))
                    BinaryInstructions[i] = toRType(div[1], "$0", "$0", 0, OPFcodes.get(div[0]));

                // For j and jal instructions
                else {
                    if(!MapLabels.containsKey(div[1])) throw new IllegalArgumentException("Error: Addressing label does not exist!");
                    BinaryInstructions[i] = toJType(OPFcodes.get(div[0]), MapLabels.get(div[1]) * 4 + PC);
                }
            }

            else throw new IllegalArgumentException("Error: Unsupported instructions!");
        }

        return BinaryInstructions;
    }

    /**Prepares instructions to be properly parsed and read by the assembler removing any unnecessary characters**/
    public static String LoadInstructions(String PROGRAM){

        String INSTRUCTIONS;

        // Firstly, extra spaces between labels and instructions are removed

        // Checks if the last line is a label
        if(PROGRAM.replaceAll("\\s*", "").endsWith(":")) {

            INSTRUCTIONS = String.join(": ", PROGRAM.split("\\s*:\\s*"));
            INSTRUCTIONS +=": ";
        }
        else INSTRUCTIONS = String.join(": ", PROGRAM.split("\\s*:\\s*"));
        // Searches for beginning of instructions and put all the instructions as a string result
        if(PROGRAM.contains(".data")){

            if(!PROGRAM.contains(".text")) throw new IllegalArgumentException("Error: You have to specify beginning of instructions.");
            INSTRUCTIONS = INSTRUCTIONS.split("\\s*.text\\s*")[1].replaceAll("((\\r\\n)|\\n|\\r){2,}", "\n");
        }

        else
            INSTRUCTIONS = INSTRUCTIONS.replaceAll("\\s*.text\\s*","").replaceAll("((\\r\\n)|\\n|\\r){2,}", "\n"); //remove empty lines and the .text label

        INSTRUCTIONS = INSTRUCTIONS.replaceAll("\\s*$","");
        return INSTRUCTIONS;
    }


    /**Returns all the data mapped with their corresponding addresses in a Pair object
     *information carried by the return value are the label of the data memory address to be used in the program
     * and a hash map that maps every address in memory and the data in it
    **/
    public static Pair<HashMap<String,Integer>, String[]> LoadData(String PROGRAM, int DATALoc)  {

        // Removes extra space between labels and instructions
        PROGRAM = String.join(": ", PROGRAM.split("\\s*:\\s*"));

        // Searches for beginning of data elements and put them in an array of string
        if(PROGRAM.contains(".data")) {

            String[] DATA =
                    PROGRAM.split("\\s*.text\\s*")[0].replaceAll("\\s*.data\\s*","").replaceAll("((\\r\\n)|\\n|\\r){2,}","\n").split("((\\r\\n)|\\n|\\r)");
            HashMap<String,Integer> Addrs = new HashMap<>(); //map data variable to its memory address

            for (int i=0;i<DATA.length;i++) {

                if (DATA[i].contains(".word")) {

                    Addrs.put(DATA[i].split("\\s*:\\s*")[0], DATALoc);
                    DATA[i] = DATA[i].split("\\s*:\\s*")[1].replaceAll(".word\\s*","");
                    DATALoc += DATA[i].split(",").length * 4; // Increases data location by number of data elements entered
                }

                else throw new IllegalArgumentException("Error: Unsupported data input.");
            }
            return new Pair<>(Addrs, DATA);
        }

        else return null;
    }
}
