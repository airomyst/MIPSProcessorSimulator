package Operational;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;

import static Operational.Functions.getRegName;

public class GraphicalInterface extends JFrame implements ActionListener{

    private JFileChooser fc = new JFileChooser();
    private JButton ClrBtn = new JButton("Roll Back");
    private JTextField PCVal = new JTextField("0");
    private JLabel PClbl = new JLabel("PC Value");
    private JTextField DataLocVal = new JTextField("0");
    private JLabel DataLoclbl = new JLabel("Data Address");
    private JButton StpBtn = new JButton("Step Execution");
    private JPanel btmPnl = new JPanel();
    private JTextArea txtArea = new JTextArea();
    private JScrollPane scrlTxtArea = new JScrollPane(txtArea);
    private JPanel memTxtPnl = new JPanel();
    private JTextArea memTxtArea = new JTextArea();
    private JScrollPane scrlMemTxtArea = new JScrollPane(memTxtArea);
    private JPanel regPnl = new JPanel();
    private JLabel[] regLbls = new JLabel[32];
    private JTextField[] regVals = new JTextField[32];
    private ExecutionThread exThread;
    private JMenuBar mnuBar = new JMenuBar();
    private JMenu Optnmnu = new JMenu("File");
    private JMenuItem opnMni = new JMenuItem("Load...");
    private String DirVal;
    private PrintStream printStream;

    public GraphicalInterface(){

        init();
    }

    private void init(){

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setTitle("Mips Processor Simulator V1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(d);
        this.setMinimumSize(new Dimension(1280, 600));
        this.setJMenuBar(mnuBar);

        mnuBar.add(Optnmnu);

        Optnmnu.add(opnMni);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Mips and Text Files", "mips", "txt");
        fc.setFileFilter(filter);

        regPnl.setLayout(new GridLayout(32, 2, 3, 3));
        regPnl.setPreferredSize(new Dimension(135,0));
        regPnl.setBorder(BorderFactory.createTitledBorder("Registers"));
        for(int i=0;i<32;i++) {
            regLbls[i] = new JLabel(getRegName(i));
            regLbls[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            regVals[i] = new JTextField("0");
            regVals[i].setEditable(false);
            regVals[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            regPnl.add(regLbls[i]);
            regPnl.add(regVals[i]);
        }

        memTxtArea.setEditable(false);

        memTxtPnl.setBorder(BorderFactory.createTitledBorder("Memory"));
        memTxtPnl.setPreferredSize(new Dimension(270,0));
        memTxtPnl.setLayout(new BorderLayout());
        memTxtPnl.add(scrlMemTxtArea);

        printStream = new PrintStream(new CustomOutputStream(memTxtArea));

        PCVal.setPreferredSize(new Dimension(120, 25));
        DataLocVal.setPreferredSize(new Dimension(120, 25));

        btmPnl.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        btmPnl.add(ClrBtn);
        btmPnl.add(PClbl);
        btmPnl.add(PCVal);
        btmPnl.add(DataLoclbl);
        btmPnl.add(DataLocVal);
        btmPnl.add(StpBtn);

        txtArea.setEditable(false);

        StpBtn.addActionListener(this);
        opnMni.addActionListener(this);
        ClrBtn.addActionListener(this);

        this.add(scrlTxtArea, BorderLayout.CENTER);
        this.add(btmPnl, BorderLayout.SOUTH);
        this.add(regPnl, BorderLayout.WEST);
        this.add(memTxtPnl, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent e) {

        Object o = e.getSource();
        if (o == StpBtn) {
            if (exThread == null) {
                if(DirVal!=null) {
                    exThread = new ExecutionThread(Integer.parseInt(PCVal.getText()), Integer.parseInt(DataLocVal.getText()), DirVal, regVals, printStream);
                    exThread.setDefaultUncaughtExceptionHandler((t, er) -> System.err.println(er.getMessage()));
                    exThread.start();
                }
                else System.out.println("you have to specify a file!");
            }
            else if(exThread.getState()==Thread.State.WAITING){
                synchronized (exThread) {
                    exThread.notify();
                }
            }
        }

        else if (o == opnMni){

            memTxtArea.setText("");
            txtArea.setText("");
            for(int i=0; i<32; i++) regVals[i].setText("0");
            int returnVal = fc.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {

                DirVal = fc.getSelectedFile().getAbsolutePath();
                txtArea.setText(txtArea.getText()+"File Loaded Successfully!\n");
            }
            if(exThread!=null) exThread=null;

        }

        else if (o == ClrBtn) {

            for(int i=0; i<32; i++) regVals[i].setText("0");
            txtArea.setText("");
            memTxtArea.setText("");
            if(exThread!=null) exThread=null;
        }
    }
    public JTextArea getTxtArea(){

        return txtArea;
    }
}
