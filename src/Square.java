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
        if (!(destination == null)) {
            if (origin.state == State.WHITE_KING || origin.state == State.BLACK_KING) {
                canMoveTo = this.canMakeSingleMoveKing(destination);
            } else {
                canMoveTo = this.canMove(destination);
            }
        }
        return canMoveTo;
    }

    boolean canJumpTo(Square destination){

        boolean canJumpTo = false;
        Square middleSquare;
        Square origin = this;

        try {
            middleSquare = Board.getMiddleSquare(this, destination);
        } catch (Exception e) {
            return false;
        }

        if (destination.state == State.EMPTY && destination.colour == Colour.WHITE) {
            if (middleIsOpposite(origin.state, middleSquare.state) && !(middleSquare.state == State.EMPTY)) {
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

    boolean canJumpToKing(Square destination){
        boolean canJumpTo = false;
        Square middleSquare;
        Square origin = this;

        try {
            middleSquare = Board.getMiddleSquare(this, destination);
        } catch (Exception e) {
            return false;
        }

        if (destination.state == State.EMPTY && destination.colour == Colour.WHITE) {
            if (middleIsOpposite(origin.state, middleSquare.state) && !(middleSquare.state == State.EMPTY)) {
                if (Math.abs(origin.getRow() - destination.getRow()) == 2 && Math.abs(origin.getColumn() - destination.getColumn()) == 2) {
                    canJumpTo = true;
                }
            }
        }

        return canJumpTo;
    }

    private boolean canMove(Square destination){
        boolean canMakeSingleMove = false;
        Square origin = this;

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
        return canMakeSingleMove;
    }

    private boolean canMakeSingleMoveKing(Square destination){
        boolean canMakeSingleMoveKing = false;
        Square origin = this;

        if (destination.state == State.EMPTY && destination.colour == Colour.WHITE) {
            if (Math.abs(origin.getRow() - destination.getRow()) == 1 && Math.abs(origin.getColumn() - destination.getColumn()) == 1) {
                canMakeSingleMoveKing = true;
            }
        }
        return canMakeSingleMoveKing;
    }

    private boolean middleIsOpposite(State origin, State middle){
        boolean middleIsOpposite;

        String[] originStateParts = origin.toString().split("_");
        String[] middleStateParts = middle.toString().split("_");

        middleIsOpposite = !(originStateParts[0].equals(middleStateParts[0]));

        return middleIsOpposite;
    }

    private void playJumpSound(){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("jump.wav")));
            clip.start();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void playMoveSound(){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("movePiece.wav")));
            clip.start();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    void moveTo(Square destination){
        Square middleSquare = null;

        if (this.state == State.BLACK_PIECE) {
            destination.state = State.BLACK_PIECE;
        } else if (this.state == State.WHITE_PIECE) {
            destination.state = State.WHITE_PIECE;
        } else if (this.state == State.BLACK_KING){
            destination.state = State.BLACK_KING;
        } else if (this.state == State.WHITE_KING){
            destination.state = State.WHITE_KING;
        }
        this.state = State.EMPTY;

        if (Math.abs(this.getColumn() - destination.getColumn()) == 2) {
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

    int getColumn(){
        return COLUMN;
    }

    int getRow(){
        return ROW;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        this.state = state;
    }

    Colour getColour(){
        return colour;
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
