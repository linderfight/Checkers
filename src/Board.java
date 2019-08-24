import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLClientInfoException;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

class Board implements ActionListener{

    private static ArrayList<Square> boardSquares = new ArrayList<Square>();
    private static ImageIcon blackSquareIcon;
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
    Square clickedSquare;
    Square selectedPiece;


    Board(){
        generateNewBoard();
    }

    public void actionPerformed(ActionEvent e){
        clickedSquare = ((Square) e.getSource());
        if (correctPieceSelected()){
            selectedPiece = clickedSquare;
            clickedSquare = null;
            showPossibleMoves();
        } else if (clickedSquare.colour == Colour.YELLOW && clickedSquare.state == State.EMPTY) {
            makeAMove();
        } else {
            playInvalidMoveSound();
        }


    }

    private void makeAMove(){
        selectedPiece.moveTo(clickedSquare);
        clearPossibleMoves();
        selectedPiece.setIcon(whiteSquareIcon);
        selectedPiece.active = false;
        updateBoard();

        if (jumpPerformed) {
            resolveDoubleJump();
        } else {
            changePlayerTurn();
            showMovablePieces();
        }
        checkIfGameOver();
    }

    private void resolveDoubleJump(){

        if (clickedSquare.state == State.WHITE_KING || clickedSquare.state == State.BLACK_KING){
            resloveDoubleJumpKing();
        } else {
            resloveDoubleJumpRegular();
        }

        clickedSquare = null;
    }

    private void resloveDoubleJumpKing(){

        Square leftUp = getJumpDestination(clickedSquare, "leftUp");
        Square leftDown = getJumpDestination(clickedSquare, "leftDown");
        Square rightUp = getJumpDestination(clickedSquare, "rightUp");
        Square rightDown = getJumpDestination(clickedSquare, "rightDown");

        if (clickedSquare.canJumpToKing(leftUp)) {
            leftUp.colour = Colour.YELLOW;
            selectedPiece = clickedSquare;
            selectedPiece.active = true;
            updateBoard();
        } else if (clickedSquare.canJumpToKing(leftDown)){
            leftDown.colour = Colour.YELLOW;
            selectedPiece = clickedSquare;
            selectedPiece.active = true;
            updateBoard();
        } else if (clickedSquare.canJumpToKing(rightUp)){
            rightUp.colour = Colour.YELLOW;
            selectedPiece = clickedSquare;
            selectedPiece.active = true;
            updateBoard();
        } else if (clickedSquare.canJumpToKing(rightDown)){
            rightDown.colour = Colour.YELLOW;
            selectedPiece = clickedSquare;
            selectedPiece.active = true;
            updateBoard();
        } else {
            selectedPiece = null;
            changePlayerTurn();
            showMovablePieces();
        }
    }

    private void resloveDoubleJumpRegular(){
        Square left = getJumpDestination(clickedSquare, "left");
        Square right = getJumpDestination(clickedSquare, "right");

        if (clickedSquare.canJumpTo(left) || clickedSquare.canJumpTo(right)) {
            if (clickedSquare.canJumpTo(left)) {
                left.colour = Colour.YELLOW;
            }
            if (clickedSquare.canJumpTo(right)) {
                right.colour = Colour.YELLOW;
            }
            selectedPiece = clickedSquare;
            selectedPiece.active = true;
            updateBoard();
        } else {
            selectedPiece = null;
            changePlayerTurn();
            showMovablePieces();
        }
    }

    private boolean correctPieceSelected(){
        boolean correctPieceSelected = false;

        String[] pieceStateParts = clickedSquare.state.toString().split("_");
        String[] currentTurnParts = currentTurn.toString().split("_");

        if ((pieceStateParts[0].equals(currentTurnParts[0]) && !(isPieceSelected())) && (isMovable(clickedSquare) || canJump(clickedSquare))){
            correctPieceSelected = true;
        } else {
            correctPieceSelected = false;
        }

        return correctPieceSelected;
    }

    private boolean isPieceSelected(){
        for (Square square : boardSquares) {
            if (square.active == true){
                System.out.println(square.toString());
                return true;
            }

        }
        return false;

    }

    private void generateNewBoard(){
        currentTurn = State.WHITE_PIECE;
        jumpPerformed = false;
        boardSquares.clear();
        setIcons();
        buildBoard();
        showMovablePieces();
    }

    private void showPossibleMoves(){
        Boolean canJump = false;

        selectedPiece.active = true;
        for (Square square : boardSquares) {
            if (selectedPiece.state == State.BLACK_KING || selectedPiece.state == State.WHITE_KING) {
                if (selectedPiece.canJumpToKing(square)) {
                    square.colour = Colour.YELLOW;
                    canJump = true;
                }
            } else {
                if (selectedPiece.canJumpTo(square)) {
                    square.colour = Colour.YELLOW;
                    canJump = true;
                }
            }
        }

        if (canJump == false) {
            for (Square square : boardSquares) {
                if (selectedPiece.canMoveTo(square)) {
                    square.colour = Colour.YELLOW;
                }
            }
        }
        updateBoard();
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
        } else if (direction == "leftUp") {
            jumpSquareColumn = origin.getColumn() - 2;
            jumpSquareRow = origin.getRow() - 2;
        }else if (direction == "leftDown") {
            jumpSquareColumn = origin.getColumn() - 2;
            jumpSquareRow = origin.getRow() + 2;
        }else if (direction == "rightUp") {
            jumpSquareColumn = origin.getColumn() + 2;
            jumpSquareRow = origin.getRow() - 2;
        }else if (direction == "rightDown") {
            jumpSquareColumn = origin.getColumn() + 2;
            jumpSquareRow = origin.getRow() + 2;
        }

        jumpSquare = getSquare(jumpSquareColumn, jumpSquareRow);

        return jumpSquare;
    }

    private boolean isMovable(Square currentSquare){
        boolean isMovable = false;

        for (Square destinationSquare : boardSquares) {
            if (currentSquare.canMoveTo(destinationSquare)) {
                isMovable = true;
                break;
            }
        }
        return isMovable;
    }

    private boolean canJumpKing(Square currentSquare){
        boolean canJumpKing = false;

        for (Square destinationSquare : boardSquares) {
            if (currentSquare.canJumpToKing(destinationSquare)) {
                canJumpKing = true;
                break;
            }
        }

        return canJumpKing;
    }

    private boolean canJump(Square currentSquare){
        boolean canJump = false;

        for (Square destinationSquare : boardSquares) {
            if (currentSquare.state == State.WHITE_KING || currentSquare.state == State.BLACK_KING) {
                if (currentSquare.canJumpToKing(destinationSquare)) {
                    canJump = true;
                    break;
                }
            } else {
                if (currentSquare.canJumpTo(destinationSquare)) {
                    canJump = true;
                    break;
                }
            }
        }

        return canJump;
    }

    private void showMovablePieces(){
        boolean jumpPresent = false;

        for (Square originSquare : boardSquares) {
            if (currentTurn == State.WHITE_PIECE) {
                if (originSquare.state == State.WHITE_PIECE) {
                    if (canJump(originSquare)) {
                        originSquare.setIcon(whitePieceMovableIcon);
                        jumpPresent = true;
                    }
                } else if (originSquare.state == State.WHITE_KING){
                    if (canJumpKing(originSquare)) {
                        originSquare.setIcon(whiteKingMovableIcon);
                        jumpPresent = true;
                    }
                }
            } else if (currentTurn == State.BLACK_PIECE) {
                if (originSquare.state == State.BLACK_PIECE) {
                    if (canJump(originSquare)) {
                        originSquare.setIcon(blackPieceMovableIcon);
                        jumpPresent = true;
                    }
                } else if (originSquare.state == State.BLACK_KING){
                    if (canJumpKing(originSquare)) {
                        originSquare.setIcon(blackKingMovableIcon);
                        jumpPresent = true;
                    }
                }
            }
        }

        if (!(jumpPresent)){
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
    }

    static Square getMiddleSquare(Square origin, Square destination){
        Square middleSquare = null;
        int middleSquareRow = (origin.getRow() + destination.getRow()) / 2;
        int middleSquareColumn = (origin.getColumn() + destination.getColumn()) / 2;

        middleSquare = getSquare(middleSquareColumn, middleSquareRow);

        return middleSquare;
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
            } else if (square.state == State.WHITE_KING) {
                square.setIcon(whiteKingIcon);
            } else if (square.state == State.BLACK_KING) {
                square.setIcon(blackKingIcon);
            } else if (square.state == State.EMPTY && square.getColour() == Colour.WHITE) {
                square.setIcon(whiteSquareIcon);
            } else if (square.state == State.EMPTY && square.getColour() == Colour.YELLOW) {
                square.setIcon(yellowSquareIcon);
            }
            frame.add(square);
        }
    }

    private void checkIfGameOver(){

        boolean gameOver = false;
        boolean whiteCantMove = true;
        boolean blackCantMove = true;
        int blackPiecesCount = 0;
        int whitePiecesCount = 0;

        for (Square square : boardSquares) {
            if (square.state == State.WHITE_KING || square.state == State.WHITE_PIECE) {
                whitePiecesCount++;
                if(square.getIcon() == whiteKingMovableIcon || square.getIcon() == whitePieceMovableIcon){
                    whiteCantMove = false;
                }
            } else if (square.state == State.BLACK_KING || square.state == State.BLACK_PIECE) {
                blackPiecesCount++;
                if(square.getIcon() == blackKingMovableIcon || square.getIcon() == blackPieceMovableIcon){
                    blackCantMove = false;
                }
            }
        }

        if (blackPiecesCount == 0 || whitePiecesCount == 0){
            gameOver = true;
            if (blackPiecesCount == 0 && whitePiecesCount > 0){
                showMessageDialog(null, "The player with WHITE pieces wins. Congratulations!");
            } else if (whitePiecesCount == 0 && blackPiecesCount > 0){
                showMessageDialog(null, "The player with BLACK pieces wins. Congratulations!");
            }
        } else if (whiteCantMove || blackCantMove) {
            if (whiteCantMove && blackCantMove) {
                if (whitePiecesCount > blackPiecesCount) {
                    showMessageDialog(null, "The player with WHITE pieces wins. Congratulations!");
                } else if (blackPiecesCount > whitePiecesCount) {
                    showMessageDialog(null, "The player with BLACK pieces wins. Congratulations!");
                }
            } else if (whiteCantMove) {
                showMessageDialog(null, "The player with BLACK pieces wins. Congratulations!");
            } else if (blackCantMove) {
                showMessageDialog(null, "The player with WHITE pieces wins. Congratulations!");
            }
        }

        if (gameOver) {
            frame.setVisible(false);
            frame.dispose();
            generateNewBoard();
        }
    }

    private void clearPossibleMoves(){
        for (Square square : boardSquares) {
            if (square.colour == Colour.YELLOW) {
                square.colour = Colour.WHITE;
            }
        }
    }

    private void playInvalidMoveSound(){
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemResource("invalidMove.wav")));
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
        blackSquareIcon = new ImageIcon(ClassLoader.getSystemResource("blackSquare.png"));
        whiteSquareIcon = new ImageIcon(ClassLoader.getSystemResource("whiteSquare.png"));
        yellowSquareIcon = new ImageIcon(ClassLoader.getSystemResource("canMoveTo.png"));

        whitePieceIcon = new ImageIcon(ClassLoader.getSystemResource("white.png"));
        whiteKingIcon = new ImageIcon(ClassLoader.getSystemResource("whiteKing.png"));
        blackPieceIcon = new ImageIcon(ClassLoader.getSystemResource("black.png"));
        blackKingIcon = new ImageIcon(ClassLoader.getSystemResource("blackKing.png"));

        whitePieceMovableIcon = new ImageIcon(ClassLoader.getSystemResource("whiteActive.png"));
        whiteKingMovableIcon = new ImageIcon(ClassLoader.getSystemResource("whiteKingActive.png"));
        blackPieceMovableIcon = new ImageIcon(ClassLoader.getSystemResource("blackActive.png"));
        blackKingMovableIcon = new ImageIcon(ClassLoader.getSystemResource("blackKingActive.png"));
    }

    private void buildBoard(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("Momchil's checkers - v1.0");
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
