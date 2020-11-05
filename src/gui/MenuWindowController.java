package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MenuWindowController {

    @FXML
    private MainWindowController mainWindowController;

    @FXML
    public void openAES(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/aesWindow.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        AesController aesController = loader.getController();
        aesController.setMainWindowController(mainWindowController);
        mainWindowController.setScreen(pane);
    }

    @FXML
    public void openAuthors(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/authorsWindow.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        AuthorsController authorsController = loader.getController();
        authorsController.setMainWindowController(mainWindowController);
        mainWindowController.setScreen(pane);
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
}
