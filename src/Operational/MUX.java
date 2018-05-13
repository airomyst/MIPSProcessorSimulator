package Operational;

/**a class that represents a MUX component**/
public class MUX {

    private int Selection; // MUX output
    private String Source; // Name of the component instance

    /**Instantiates a named adder**/
    public MUX(String Source){

        this.Source = Source;
    }

    /**Select from the passed array of values based on the passed control signal*/
    public void Select(int[] Values, int SelectionSignal){

        Selection = Values[SelectionSignal];
        PrintOutputs();
    }

    private void PrintOutputs(){

        System.out.println(Source+" output: "+Selection);

    }

    /**Returns the MUX selection result**/
    public int getSelection() {

        return Selection;
    }
}
