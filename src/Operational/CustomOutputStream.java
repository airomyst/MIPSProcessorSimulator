package Operational;

import javax.swing.*;
import java.io.OutputStream;

/**This classes represents an output stream that points to a JtextArea**/
public class CustomOutputStream extends OutputStream {

    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b){

        textArea.append(String.valueOf((char)b)); // Redirects data to the text area
        textArea.setCaretPosition(textArea.getDocument().getLength()); // Scrolls the text area to the end of data
    }
}