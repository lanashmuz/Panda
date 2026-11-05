package Controller;

import Model.EasyBoard;
import Model.GameConfiguration;
import View.EasyBoardFrame;
import View.GameSetupFrame;

public class GameController {

    private final GameSetupFrame setupFrame;

    public GameController(GameSetupFrame setupFrame) {
        this.setupFrame = setupFrame;
        setupFrame.setOnStartGame(this::startGame);
    }

    private void startGame(GameConfiguration configuration) {
        EasyBoard board = new EasyBoard(configuration);
        EasyBoardFrame boardFrame = new EasyBoardFrame(board);

        boardFrame.setVisible(true);
        setupFrame.dispose();
    }
}
