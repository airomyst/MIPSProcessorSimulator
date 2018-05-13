package Operational;

import MipsProcessor.Processor;

import javax.swing.*;
import java.io.PrintStream;

/**This class represents the thread that is executed in the GUI of the program**/
public class ExecutionThread extends Thread {

    private int PC; //PC register value
    private int DATALoc; // Specified data memory address to insert user provided data elements
    private String path; // Path of the passed file
    private JTextField[] RegVals; // The GUI text fields that contains the registers values
    private PrintStream memPS; // special print stream
    private boolean loop;

    /**Initializes the thread that will be executed with all data needed to simulate the assembly program provided by the user**/
    public ExecutionThread(int PC, int DATALoc, String path, JTextField[] RegVals, PrintStream memPS, boolean loop){

        this.DATALoc=DATALoc;
        this.PC=PC;
        this.path=path;
        this.RegVals=RegVals;
        this.memPS=memPS;
        this.loop=loop;
    }

    /**Starts the processor simulation**/
    public void run(){

        Processor.runProcessor(PC, DATALoc, path, RegVals, memPS, loop);
    }
}
