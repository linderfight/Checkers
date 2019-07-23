import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Board implements ActionListener{
    Board(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Checkers");
        frame.setSize(612,612);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        for (int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ImageIcon blackSquare = new ImageIcon("pieces/blackSquare.png");
                ImageIcon whiteSquare = new ImageIcon("pieces/whiteSquare.png");
                ImageIcon whitePiece = new ImageIcon("pieces/white.png");

                Square boardSquare = new Square(i, j);

                if ((i + j) % 2 == 0){
                    boardSquare.setIcon(blackSquare);
                } else {
                    if (i > 5){
                        boardSquare.setIcon(whitePiece);
                        boardSquare.state = State.WHITE;
                    } else {
                        boardSquare.setIcon(whiteSquare);
                    }
                }

                //  use action listener

                frame.add(boardSquare);
            }
        }

        frame.setLayout(new GridLayout(8,8));
        frame.setVisible(true);
    }

}
