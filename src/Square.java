import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.File;

public class Square extends JButton{

    public final int COLUMN;
    public final int ROW;
    public State state;
    public Colour colour;
    public Boolean active;

    Square(int row, int column){
        this.ROW = row;
        this.COLUMN = column;
        this.state = State.EMPTY;
        active = false;
    }

    boolean canMoveTo(Square destination){
        Square origin = this;
        boolean canMoveTo = false;

        if (origin.state == State.WHITE_KING || origin.state == State.BLACK_KING){
            canMoveTo = isKingMoveLegal(origin, destination);
        } else {
            if (Math.abs(origin.getRow() - destination.getRow()) == 1) {
                canMoveTo = canMakeSingleMove(origin, destination);
            } else if (Math.abs(origin.getRow() - destination.getRow()) == 2) {
                canMoveTo = canJumpTo(origin, destination);
            }
        }
        return canMoveTo;
    }

    private boolean canMakeSingleMove(Square origin, Square destination){
        boolean canMakeSingleMove = false;

        if (destination.state == State.EMPTY && destination.colour == Colour.WHITE) {
            if (origin.state == State.WHITE_PIECE) {
                if ((origin.getRow() - destination.getRow() == 1) && Math.abs(origin.getColumn() - destination.getColumn()) == 1) {
                    canMakeSingleMove = true;
                }
            } else if (origin.state == State.BLACK_PIECE) {
                if ((destination.getRow() - origin.getRow() == 1) && Math.abs(origin.getColumn() - destination.getColumn()) == 1) {
                    canMakeSingleMove = true;
                }
            }
        }
        return  canMakeSingleMove;
    }

    private boolean canJumpTo(Square origin, Square destination){

        boolean canJumpTo = false;
        Square middleSquare;

        try {
            middleSquare = Board.getMiddleSquare(this, destination);
        } catch (Exception e){
            return  false;
        }

        if (destination.state == State.EMPTY && destination.colour == Colour.WHITE) {
            if (!(origin.state == middleSquare.state) && !(middleSquare.state == State.EMPTY)) {
                if (origin.state == State.WHITE_PIECE) {
                    if ((origin.getRow() - destination.getRow() == 2) && Math.abs(origin.getColumn() - destination.getColumn()) == 2) {
                        canJumpTo = true;
                    }
                } else if (origin.state == State.BLACK_PIECE) {
                    if ((destination.getRow() - origin.getRow() == 2) && Math.abs(origin.getColumn() - destination.getColumn()) == 2) {
                        canJumpTo = true;
                    }
                }
            }
        }

        return canJumpTo;
    }

    private boolean isKingMoveLegal(Square origin, Square destination){
        boolean kingMoveLegal = false;

        // How is the King moving?










        return kingMoveLegal;
    }

    void moveTo(Square destination){
        Square middleSquare = null;

        if (this.state == State.BLACK_PIECE){
            destination.state = State.BLACK_PIECE;
        } else if (this.state == State.WHITE_PIECE){
            destination.state = State.WHITE_PIECE;
        }
        this.state = State.EMPTY;

        if (Math.abs(this.getColumn() - destination.getColumn()) == 2){
            middleSquare = Board.getMiddleSquare(this, destination);
            middleSquare.state = State.EMPTY;
            middleSquare.setIcon(Board.whiteSquareIcon);
            Board.jumpPerformed = true;
            playJumpSound();
        } else {
            Board.jumpPerformed = false;
            playMoveSound();
        }
    }

    private void playJumpSound(){
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("assets/sounds/jump.wav")));
            clip.start();
        }
        catch (Exception exc)
        {
            exc.printStackTrace(System.out);
        }
    }

    private void playMoveSound(){
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("assets/sounds/movePiece.wav")));
            clip.start();
        }
        catch (Exception exc)
        {
            exc.printStackTrace(System.out);
        }
    }

    int getColumn(){
        return COLUMN;
    }

    int getRow(){
        return ROW;
    }

    public State getState(){
        return state;
    }

    Colour getColour(){
        return colour;
    }

    public void setState(State state){
        this.state = state;
    }

    void setColour(Colour colour){
        this.colour = colour;
    }

    @Override
    public String toString(){
        return "Square{" +
                "COLUMN=" + COLUMN +
                ", ROW=" + ROW +
                ", state=" + state +
                ", colour=" + colour +
                ", active=" + active +
                '}';
    }
}
