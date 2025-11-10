import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScrabbleController implements ActionListener{
    private App app;
    private Game game;
    private int selectedTile;

    public ScrabbleController(App app, Game game) {
        super();
        this.app = app;
        this.game = game;
    }

    public void actionPerformed(ActionEvent event) {
        String[] command = event.getActionCommand().split(" ");
        switch (command[0]) {
            case "H":
                selectedTile = Integer.parseInt(command[1]);
                app.disableHand();
                app.enableBoard();
                app.hideTile(selectedTile);
                app.disableDone();
                app.disableExchange();
                game.selectTile(command[2].charAt(0));
                break;
            case "B":
                if (game.placeTile(Integer.parseInt(command[2]), Integer.parseInt(command[3]))) {
                    app.enableHand();
                    app.disableBoard();
                    app.enableDone();
                }
                break;
            case "D":
                if (game.ValidateMove(Boolean.parseBoolean(command[1]))) {
                    game.nextTurn(false);
                }
                else {
                    game.removeViewsPlacedTiles();
                }
                app.enableExchange();
                break;
            case "E":
                game.nextTurn(true);
                break;
            default:
                break;
        }
    }
}
