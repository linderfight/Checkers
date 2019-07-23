import javax.swing.*;

public class Square extends JButton{

    public int row;
    public int column;
    public State state;

    Square(int row, int column){
        this.row = row;
        this.column = column;
        state = State.EMPTY;
    }

    public void moveTo(Square square){

    }

    public int getRow(){
        return row;
    }

    public void setRow(int row){
        this.row = row;
    }

    public int getColumn(){
        return column;
    }

    public void setColumn(int column){
        this.column = column;
    }
}
