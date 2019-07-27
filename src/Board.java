import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

class Board implements ActionListener{

    private static ArrayList<Square> boardSquares = new ArrayList<Square>();
    private static ImageIcon blackSquareIcon;

    private ArrayList<Square> buttonPair = new ArrayList<Square>();
    private State currentTurn;
    private ImageIcon whitePieceIcon;
    private ImageIcon whiteKingIcon;
    private ImageIcon blackPieceIcon;
    private ImageIcon blackKingIcon;
    private ImageIcon whitePieceMovableIcon;
    private ImageIcon whiteKingMovableIcon;
    private ImageIcon blackPieceMovableIcon;
    private ImageIcon blackKingMovableIcon;
    private ImageIcon yellowSquareIcon;
    private JFrame frame;

    static ImageIcon whiteSquareIcon;
    static Boolean jumpPerformed;

    Board(){
        currentTurn = State.WHITE_PIECE;
        jumpPerformed = false;
        setIcons();
        buildBoard();
        showMovablePieces();
    }

    public void actionPerformed(ActionEvent e){
        Square clickedSquare = ((Square) e.getSource());
        buttonPair.add(clickedSquare);

        if (buttonPair.size() == 1) {
            resolvePieceSelection();
        } else if (buttonPair.size() == 2) {
            resolvePieceDestination();
        }
    }

    private void resolvePieceSelection(){
        if (buttonPair.get(0).state == currentTurn) {
            buttonPair.get(0).active = true;
            for (Square square : boardSquares) {
                if (buttonPair.get(0).canMoveTo(square)) {
                    square.colour = Colour.YELLOW;
                    updateBoard();
                }
            }
        } else {
            buttonPair.clear();
            playInvalidMoveSound();
            clearMoveToSquares();
        }
    }

    private void resolvePieceDestination(){
        if (buttonPair.get(1).colour == Colour.YELLOW) {
            buttonPair.get(0).active = false;
            buttonPair.get(0).moveTo(buttonPair.get(1));
            buttonPair.get(0).setIcon(whiteSquareIcon);
            clearMoveToSquares();
            updateBoard();

            if (jumpPerformed) {
                resolveDoubleJump();
                showMovablePieces();
            } else {
                changePlayerTurn();
                buttonPair.clear();
                showMovablePieces();
            }

        } else {
            buttonPair.get(1).active = true;
            buttonPair.remove(buttonPair.size() - 1);
            playInvalidMoveSound();
        }
    }

    private void resolveDoubleJump(){
        Square leftJump = getJumpDestination(buttonPair.get(1), "left");
        Square rightJump = getJumpDestination(buttonPair.get(1), "right");
        if (buttonPair.get(1).canMoveTo(leftJump) || buttonPair.get(1).canMoveTo(rightJump)) {
            if (buttonPair.get(1).canMoveTo(leftJump)) {
                leftJump.colour = Colour.YELLOW;
            }
            if (buttonPair.get(1).canMoveTo(rightJump)) {
                rightJump.colour = Colour.YELLOW;
            }
            buttonPair.get(1).active = true;
            buttonPair.set(0, buttonPair.get(1));
            buttonPair.remove(buttonPair.size() - 1);
            updateBoard();
        } else {
            changePlayerTurn();
            buttonPair.clear();
        }
    }

    static Square getMiddleSquare(Square origin, Square destination){
        Square middleSquare = null;
        int middleSquareRow = (origin.getRow() + destination.getRow()) / 2;
        int middleSquareColumn = (origin.getColumn() + destination.getColumn()) / 2;

        middleSquare = getSquare(middleSquareColumn, middleSquareRow);

        return middleSquare;
    }

    private Square getJumpDestination(Square origin, String direction){
        Square jumpSquare = null;
        int jumpSquareColumn = 0;
        int jumpSquareRow = 0;

        if (direction == "left") {
            if (origin.state == State.WHITE_PIECE) {
                jumpSquareColumn = origin.getColumn() - 2;
                jumpSquareRow = origin.getRow() - 2;
            } else if (origin.state == State.BLACK_PIECE) {
                jumpSquareColumn = origin.getColumn() - 2;
                jumpSquareRow = origin.getRow() + 2;
            }
        } else if (direction == "right") {
            if (origin.state == State.WHITE_PIECE) {
                jumpSquareColumn = origin.getColumn() + 2;
                jumpSquareRow = origin.getRow() - 2;
            } else if (origin.state == State.BLACK_PIECE) {
                jumpSquareColumn = origin.getColumn() + 2;
                jumpSquareRow = origin.getRow() + 2;
            }
        }

        jumpSquare = getSquare(jumpSquareColumn, jumpSquareRow);

        return jumpSquare;
    }

    private void showMovablePieces(){
        for (Square originSquare : boardSquares) {
            if (currentTurn == State.WHITE_PIECE) {
                if (originSquare.state == State.WHITE_PIECE) {
                    if (isMovable(originSquare)) {
                        originSquare.setIcon(whitePieceMovableIcon);
                    }
                } else if (originSquare.state == State.WHITE_KING){
                    if (isMovable(originSquare)) {
                        originSquare.setIcon(whiteKingMovableIcon);
                    }
                }
            } else if (currentTurn == State.BLACK_PIECE) {
                if (originSquare.state == State.BLACK_PIECE) {
                    if (isMovable(originSquare)) {
                        originSquare.setIcon(blackPieceMovableIcon);
                    }
                } else if (originSquare.state == State.BLACK_KING){
                    if (isMovable(originSquare)) {
                        originSquare.setIcon(blackKingMovableIcon);
                    }
                }
            }
        }
    }

    private static Square getSquare(int column, int row){
        Square returnSquare = null;

        for (Square square : boardSquares) {
            if (square.getColumn() == column && square.getRow() == row) {
                returnSquare = square;
            }
        }
        return returnSquare;
    }

    private Boolean isMovable(Square currentSquare){
        boolean isMovable = false;

        for (Square destinationSquare : boardSquares) {
            if (currentSquare.canMoveTo(destinationSquare)) {
                isMovable = true;
                break;
            }
        }
        return isMovable;
    }

    private void updateBoard(){
        frame.getContentPane().removeAll();
        frame.getContentPane().repaint();

        for (Square square : boardSquares) {
            if (square.state == State.WHITE_PIECE) {
                if (square.getRow() == 1) {
                    square.state = State.WHITE_KING;
                    square.setIcon(whiteKingIcon);
                } else if (square.active) {
                    square.setIcon(whitePieceMovableIcon);
                } else {
                    square.setIcon(whitePieceIcon);
                }
            } else if (square.state == State.BLACK_PIECE) {
                if (square.getRow() == 8) {
                    square.state = State.BLACK_KING;
                    square.setIcon(blackKingIcon);
                } else if (square.active) {
                    square.setIcon(blackPieceMovableIcon);
                } else {
                    square.setIcon(blackPieceIcon);
                }
            } else if (square.state == State.EMPTY && square.getColour() == Colour.WHITE) {
                square.setIcon(whiteSquareIcon);
            } else if (square.state == State.EMPTY && square.getColour() == Colour.YELLOW) {
                square.setIcon(yellowSquareIcon);
            }
            frame.add(square);
        }


    }

    private void clearMoveToSquares(){
        for (Square square : boardSquares) {
            if (square.colour == Colour.YELLOW) {
                square.colour = Colour.WHITE;
            }
        }
    }

    private void playInvalidMoveSound(){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("assets/sounds/invalidMove.wav")));
            clip.start();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    private void changePlayerTurn(){
        if (currentTurn == State.WHITE_PIECE) {
            currentTurn = State.BLACK_PIECE;
        } else if (currentTurn == State.BLACK_PIECE) {
            currentTurn = State.WHITE_PIECE;
        }
    }

    private void setIcons(){
        blackSquareIcon = new ImageIcon("assets/pieces/blackSquare.png");
        whiteSquareIcon = new ImageIcon("assets/pieces/whiteSquare.png");
        yellowSquareIcon = new ImageIcon("assets/pieces/canMoveTo.png");

        whitePieceIcon = new ImageIcon("assets/pieces/white.png");
        whiteKingIcon = new ImageIcon("assets/pieces/whiteKing.png");
        blackPieceIcon = new ImageIcon("assets/pieces/black.png");
        blackKingIcon = new ImageIcon("assets/pieces/blackKing.png");

        whitePieceMovableIcon = new ImageIcon("assets/pieces/whiteActive.png");
        whiteKingMovableIcon = new ImageIcon("assets/pieces/whiteKingActive.png");
        blackPieceMovableIcon = new ImageIcon("assets/pieces/blackActive.png");
        blackKingMovableIcon = new ImageIcon("assets/pieces/blackKingActive.png");
    }

    private void buildBoard(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("Checkers");
        frame.setSize(612, 612);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(screenSize.width / 2 - frame.getSize().width / 2, screenSize.height / 2 - frame.getSize().height / 2);
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                Square boardSquare = new Square(i, j);

                if ((i + j) % 2 == 0) {

                    boardSquare.setIcon(blackSquareIcon);
                    boardSquare.setColour(Colour.BLACK);
                } else {
                    if (i > 5) {
                        boardSquare.setIcon(whitePieceIcon);
                        boardSquare.state = State.WHITE_PIECE;
                        boardSquare.setColour(Colour.WHITE);
                    } else if (i < 4) {
                        boardSquare.setIcon(blackPieceIcon);
                        boardSquare.state = State.BLACK_PIECE;
                        boardSquare.setColour(Colour.WHITE);
                    } else {
                        boardSquare.setIcon(whiteSquareIcon);
                        boardSquare.setColour(Colour.WHITE);
                    }
                }
                boardSquares.add(boardSquare);
                boardSquare.setActionCommand("click");
                boardSquare.addActionListener(this);
                frame.add(boardSquare);
            }
        }
        frame.setLayout(new GridLayout(8, 8));
        frame.setVisible(true);
    }
}
