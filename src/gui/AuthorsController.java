package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AuthorsController {
    @FXML
    private MainWindowController mainWindowController;

    @FXML
    public void previous(ActionEvent actionEvent) {
        mainWindowController.loadMenuScreen();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
}
