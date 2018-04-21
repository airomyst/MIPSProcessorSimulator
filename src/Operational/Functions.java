package Operational;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.Math.pow;

public class Functions {

    public static int getSignedBin(String BinaryInteger){

        int res = Integer.parseInt(BinaryInteger, 2);
        if (BinaryInteger.charAt(0)=='1') res -= 2 * pow(2,BinaryInteger.length()-1);

        return res;
    }


    public static String getRegName(int regNum){

        if(regNum >= 16 && regNum <= 23) return "$s"+(regNum-16);
        else if(regNum >= 8 && regNum <= 15) return "$t"+(regNum-8);
        else if(regNum==24) return "$t8";
        else if(regNum==25) return "$t9";
        else if(regNum >= 4 && regNum <= 7) return "$a"+(regNum-4);
        else if(regNum == 2 || regNum == 3) return "$v"+(regNum-2);
        else if(regNum==28) return "$gp";
        else if(regNum==29) return "$sp";
        else if(regNum==30) return "$fp";
        else if(regNum==31) return "$ra";
        else if(regNum==0) return "$0";
        else if(regNum==1) return "$at";
        else if(regNum == 26 || regNum == 27) return "$k"+(regNum-26);
        else {

            throw new IllegalArgumentException("Invalid register number!");
        }
    }

    public static String readFile(String path, Charset encoding){

        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            System.out.println("file doesn't exist");
            Thread.currentThread().interrupt();

        }
        return new String(encoded, encoding);
    }
}
