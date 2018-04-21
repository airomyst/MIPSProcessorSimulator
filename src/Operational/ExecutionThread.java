package Operational;

import MipsProcessor.Processor;

import javax.swing.*;
import java.io.PrintStream;

public class ExecutionThread extends Thread {

    private int PC;
    private int DATALoc;
    private String path;
    private JTextField[] RegVals;
    private PrintStream memPS;

    public ExecutionThread(int PC, int DATALoc, String path, JTextField[] RegVals, PrintStream memPS){

        this.DATALoc=DATALoc;
        this.PC=PC;
        this.path=path;
        this.RegVals=RegVals;
        this.memPS=memPS;
    }
    public void run(){

        Processor.runProcessor(PC, DATALoc, path, RegVals, memPS);
    }
}
