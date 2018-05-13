package MipsProcessor;

/**This class represents a register object*/
public class Register {

    private int value; // Value saved in register

    /**Sets the value in the register*/
    public void SetValue(int value){

        this.value = value;
    }

    /**Returns the value saved in the register**/
    public int getValue(){

        return value;
    }
}
