package module;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;


public class AesController {

    //Klasa z algorytmem AES
    AesMain aesMain = new AesMain();

    @FXML
    private MainWindowController mainWindowController;
    private final FileChooser fileChooser = new FileChooser();

    @FXML//KEY
    public TextField keyTextField;
    public Button generateKeyButton;

    public TextField keyFileRead;
    public Button readKeyButton;

    public TextField keyFileWrite;
    public Button writeKeyButton;

    @FXML//PLAINTEXT
    public TextField plaintextFileRead;
    public Button plaintextOpenButton;

    public TextArea plaintextTextBox;

    public TextField plaintextFileWrite;
    public Button cyphertextWriteButton;

    @FXML//CYPHERTEXT
    public TextField cyphertextFileRead;
    public Button cyphertextOpenButton;

    public TextArea cyphertextTextBox;

    public TextField cyphertextFileWrite;
    public Button plaintextWriteButton;

    @FXML//RADIO
    public RadioButton fileRadio;
    public RadioButton textboxRadio;

    @FXML//ENCRYPTE, DECRYPTE
    public Button encryptButton;
    public Button decryptButton;

    @FXML//BACK
    public void previous(ActionEvent actionEvent) {
        mainWindowController.loadMenuScreen();
    }

    @FXML//SET MAIN CONTROLLER
    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    @FXML
    public void initialize() {

    }

    //KONWERSJA BYTE -> HEX | HEX -> BYTE


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    //GENERATE KEY


    public void generateKeyOnClick(ActionEvent actionEvent){
        aesMain.setKey(aesMain.generateKey());
        keyTextField.setText(byteArrayToHexString(aesMain.getKey()));
    }

    //ENCRYPT


    public void encryptOnClick(ActionEvent actionEvent) {

        if(textboxRadio.isSelected()) {
            String plainText = plaintextTextBox.getText();
            aesMain.setPlainText(plainText.getBytes(StandardCharsets.UTF_8));
        }

        aesMain.setKey(hexStringToByteArray(keyTextField.getText()));
        aesMain.encryption();
        cyphertextTextBox.setText(byteArrayToHexString(aesMain.getCypherText()));

    }

    //DECRYPT


    public void decprytOnClick(ActionEvent actionEvent) {

        //Sprawdz poprawnosc klucza

        if(textboxRadio.isSelected()) {
            String cypherText = cyphertextTextBox.getText();
            aesMain.setCypherText(hexStringToByteArray(cypherText));
        }

        aesMain.setKey(hexStringToByteArray(keyTextField.getText()));
        aesMain.decryption();
        //plaintextTextBox.setText(byteArrayToHexString(aesMain.getPlainText()));
        plaintextTextBox.setText(new String(aesMain.getPlainText(), StandardCharsets.UTF_8));
    }


    //READ FROM FILE & WRITE TO FILE


    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose a file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    public void readKeyFile(ActionEvent actionEvent) {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            return;
        }
        aesMain.setKey(bytes);
        keyTextField.setText(new String(bytes));
        keyFileRead.setText(file.toString());
    }

    public void writeKeyFile(ActionEvent actionEvent) {
        fileChooser.setTitle("Write a key file");
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            String key = byteArrayToHexString(aesMain.getKey());
            out.write(key);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyFileWrite.setText(file.toString());
    }


    public void readPlaintext(ActionEvent actionEvent) {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            return;
        }
        plaintextFileRead.setText(file.toString());
        plaintextTextBox.setText(new String(bytes));
        aesMain.setPlainText(bytes);
    }

    public void readCyphertext(ActionEvent actionEvent) {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            return;
        }

        cyphertextFileRead.setText(file.toString());
        cyphertextTextBox.setText(new String(bytes));
        aesMain.setCypherText(hexStringToByteArray(cyphertextTextBox.getText()));
    }

    public void writePlaintext(ActionEvent actionEvent) {
        fileChooser.setTitle("Write a Plaintext file");
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            out.write(plaintextTextBox.getText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plaintextFileWrite.setText(file.toString());
    }

    public void writeCyphertext(ActionEvent actionEvent) {
        fileChooser.setTitle("Write a Cyphertext file");
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));
            out.write(cyphertextTextBox.getText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cyphertextFileWrite.setText(file.toString());
    }
}
