package Operational;

public class MUX {

    private int Selection;
    private String Source;

    public MUX(String Source){

        this.Source = Source;
    }

    public void Select(int[] Values, int SelectionSignal){

        Selection = Values[SelectionSignal];
        ExecuteMUX();
    }

    private void ExecuteMUX(){

        System.out.println(Source+" output: "+Selection);

    }
    public int getSelection() {

        return Selection;
    }
}
