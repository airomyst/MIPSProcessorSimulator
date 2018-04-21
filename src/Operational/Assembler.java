package Operational;

import javafx.util.Pair;

import java.util.*;

public class Assembler {

    private static final HashMap<String, String> OPFcodes;

    static {
        OPFcodes = new HashMap<>();
        OPFcodes.put("add", "100000");
        OPFcodes.put("sll", "000000");
        OPFcodes.put("nor", "100111");
        OPFcodes.put("jr", "001000");
        OPFcodes.put("slt", "101010");
        OPFcodes.put("addi", "001000");
        OPFcodes.put("sw", "101011");
        OPFcodes.put("lw", "100011");
        OPFcodes.put("lb", "100000");
        OPFcodes.put("lbu", "100100");
        OPFcodes.put("sb", "101000");
        OPFcodes.put("beq", "000100");
        OPFcodes.put("j", "000010");
        OPFcodes.put("jal", "000011");
        OPFcodes.put("slti", "001010");
        OPFcodes.put("lui", "001111");
        OPFcodes.put("ori", "001101");
    }

    //gets the register no. as a 5-bit binary number
    private static String getRegisterBin(String regNAME) {

        if (regNAME.charAt(1) == 's') return getBinStr(16 + regNAME.charAt(2) - '0', 5);

        else if (regNAME.charAt(1) == 't') {
            if (regNAME.charAt(2) == '8') return getBinStr(24, 5);
            else if (regNAME.charAt(2) == '9') return getBinStr(25, 5);
            else return getBinStr(8 + regNAME.charAt(2) - '0', 5);
        }
        else if (regNAME.charAt(1) == 'a' && !regNAME.equals("$at")) return getBinStr(4 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.charAt(1) == 'v') return getBinStr(2 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.charAt(1) == 'k') return getBinStr(26 + regNAME.charAt(2) - '0', 5);
        else if (regNAME.equals("$gp")) return getBinStr(28, 5);
        else if (regNAME.equals("$sp")) return getBinStr(29, 5);
        else if (regNAME.equals("$fp")) return getBinStr(30, 5);
        else if (regNAME.equals("$ra")) return getBinStr(31, 5);
        else if (regNAME.equals("$at")) return getBinStr(1, 5);
        else if (regNAME.equals("$0")) return getBinStr(0, 5);
        else if (Character.isDigit(regNAME.charAt(1)))
            return getBinStr(Integer.parseInt(regNAME.split("\\$")[1]), 5);

        throw new IllegalArgumentException("Invalid register name");
    }

    private static String getBinStr(int num, int digits) {

        if(digits==16) return  String.format("%16s", Integer.toBinaryString(0xFFFF & num)).replace(" ", "0");

        else return String.format("%" + Integer.toString(digits) + "s", Integer.toBinaryString(num)).replace(" ", "0");

    }

    public static String toRType(String rsn, String rtn, String rdn, int shamt, String funct) {

        if (funct == null) throw new IllegalArgumentException("Error: Invalid instruction name");
        String rs = getRegisterBin(rsn);
        String rt = getRegisterBin(rtn);
        String rd = getRegisterBin(rdn);

        return "000000" + rs + rt + rd + getBinStr(shamt, 5) + funct;
    }

    public static String toJType(String opcode, int cons) {

        if (opcode == null) throw new IllegalArgumentException("Error: Invalid instruction name");
        return opcode + getBinStr(cons, 32).substring(4, 30);
    }

    public static String toIType(String opcode, String rsn, String rtn, int cons) {

        if (opcode == null) throw new IllegalArgumentException("Error: Invalid instruction name");
        String rs = getRegisterBin(rsn);
        String rt = getRegisterBin(rtn);

        return opcode + rs + rt + getBinStr(cons, 16);
    }

    //takes an array of instructions each is e.g."add $s1, $s2 , $s3" and slices it properly to get information to do the binary conversion
    public static String[] getBinInstructions(String[] INSTRUCTIONS, int PC, HashMap<String, Integer> MapData) {

        //create a dictionary that maps each label to its line number in the array of instructions
        HashMap<String, Integer> MapLabels = new HashMap<>();
        int linesCounter = 0, extras = 0;

        /*
        first loop to prepare for parsing instructions and handling label addressing instructions
        checks for psuedo instructions
        checks for and saves labels in the hashmap
        removes labels from each instruction in the instructions array after saving it
        */
        for (int i = 0; i < INSTRUCTIONS.length; i++, linesCounter++) {

            //seperates labels from the instruction name
            String[] sepLabel = INSTRUCTIONS[i].split("\\s*:\\s*");
            String instrctNAME;

            //remove labels and get instruction name from the string
            if (INSTRUCTIONS[i].contains(":")) {

                if(sepLabel.length > 1){

                    instrctNAME = sepLabel[1].split(" ")[0];
                    INSTRUCTIONS[i] = sepLabel[1];
                }
                else { // in case there is a label at the last line
                    instrctNAME = "";
                    INSTRUCTIONS = Arrays.copyOf(INSTRUCTIONS, INSTRUCTIONS.length-1);
                }

                MapLabels.put(sepLabel[0], linesCounter);
            }

            else instrctNAME = sepLabel[0].split(" ")[0];
            if (instrctNAME.equals("blt") | instrctNAME.equals("la")) extras++;
        }

        linesCounter = 0;
        String[] BinaryInstructions = new String[INSTRUCTIONS.length + extras];

        //translates every instruction to binary correspondingly
        for (int i = 0,j=0; i < BinaryInstructions.length; i++, linesCounter++, j++) {

            //split the instruction into recognizable divisions
            String[] div = INSTRUCTIONS[j].split("\\s+|(\\s*,+\\s*)");
            if (div.length == 4) {

                if (div[3].charAt(0) == '$' | div[0].equals("sll")) {

                    if (div[0].equals("sll"))
                        BinaryInstructions[i] = toRType("$0", div[2], div[1], Integer.parseInt(div[3]),
                                OPFcodes.get(div[0]));

                    else
                        BinaryInstructions[i] = toRType(div[2], div[3], div[1], 0, OPFcodes.get(div[0]));
                }

                else if (div[0].equals("blt")) {

                    BinaryInstructions[i] = toRType(div[3], div[2], "$at", 0, OPFcodes.get("slt"));
                    i++;
                    if (Character.isLetter(div[3].charAt(0))) {

                        BinaryInstructions[i] = toIType(OPFcodes.get("beq"), "$at", "$0",
                                (MapLabels.get(div[3]) - linesCounter - 1));
                    }
                    else BinaryInstructions[i] = toIType(OPFcodes.get("beq"), "$at", "$0", Integer.parseInt(div[3]));
                }

                else if (Character.isLetter(div[3].charAt(0))) {
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), div[2], div[1],
                            (MapLabels.get(div[3]) - linesCounter - 1));
                }
                else
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), div[2], div[1], Integer.parseInt(div[3]));

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

                    throw new IllegalArgumentException("Error: Address doesn't exist!");

                }

                else if(div[0].equals("lui"))
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]),"$0", div[1], Integer.parseInt(div[2]));
                else {
                    //splits offset and register rs
                    String[] _temp = div[2].split("[()]");
                    BinaryInstructions[i] = toIType(OPFcodes.get(div[0]), _temp[1], div[1], Integer.parseInt(_temp[0]));
                }
            }

            else if (div.length == 2) {

                if (div[0].equals("jr"))
                    BinaryInstructions[i] = toRType(div[1], "$0", "$0", 0, OPFcodes.get(div[0]));

                else BinaryInstructions[i] = toJType(OPFcodes.get(div[0]), MapLabels.get(div[1]) * 4 + PC);
            }

            else throw new IllegalArgumentException("Error: Unsupported instructions!");
        }

        return BinaryInstructions;
    }

    public static String LoadInstructions(String PROGRAM){

        String INSTRUCTIONS;

        //remove extra space between labels and instructions
        if(PROGRAM.replaceAll("\\s*", "").endsWith(":")) { //check if the last line is a label
            PROGRAM = String.join(": ", PROGRAM.split("\\s*:\\s*"));
            PROGRAM +=":";
        }
        else PROGRAM = String.join(": ", PROGRAM.split("\\s*:\\s*"));

        //searches for beginning of instructions and put all the instructions as a string result
        if(PROGRAM.contains(".data")){

            if(!PROGRAM.contains(".text")) throw new IllegalArgumentException("Error: You have to specify beginning of instructions.");
            INSTRUCTIONS = PROGRAM.split("\\s*.text\\s*")[1];
        }   

        else {

            PROGRAM = PROGRAM.replaceAll("\\s*.text\\s*","");
            INSTRUCTIONS = PROGRAM;

        }
        return INSTRUCTIONS;
    }

    //put all the data mapped with their corresponding addresses in a Pair object
    public static Pair<HashMap<String,Integer>, String[]> LoadData(String PROGRAM, int DATALoc)  {

        //remove extra space between labels and instructions
        PROGRAM = String.join(": ", PROGRAM.split("\\s*:\\s+"));

        //searches for beginning of data elements and put them in an array of string
        if(PROGRAM.contains(".data")) {

            String[] DATA = PROGRAM.split("\\s*.text\\s*")[0].replaceAll("\\s*.data\\s*","").split("\n");
            HashMap<String,Integer> Addrs = new HashMap<>(); //map data variable to its memory address

            for (int i=0;i<DATA.length;i++) {

                if (DATA[i].contains(".word")) {

                    Addrs.put(DATA[i].split(":*\\s+")[0], DATALoc);
                    DATA[i] = DATA[i].split(":*\\s+")[2];
                    DATALoc += DATA[i].split(",").length * 4; // increase data location by number of data elements entered
                }

                else throw new IllegalArgumentException("Error: Unsupported data input.");
            }

            return new Pair<>(Addrs, DATA);
        }

        else return null;
    }
}