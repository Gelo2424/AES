package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import module.AESException;
import module.AesMain;

import java.io.*;
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
    public void previous() {
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


    public static byte[] hexStringToByteArray(String s) throws AESException {
        int len = s.length();
        if(len % 32 != 0) {
            throw new AESException("Given HEX String is not correct");
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            char left = s.charAt(i);
            char right = s.charAt(i+1);
            if( (!(left > 47 && left < 58) && !(left > 64 && left < 71) && !(left > 96 && left < 103)) ||
                    (!(right > 47 && right < 58) && !(right > 64 && right < 71) && !(right > 96 && right < 103))) {
                throw new AESException("Given HEX String is not correct");
            }
            data[i / 2] = (byte) ((Character.digit(left, 16) << 4)
                    + Character.digit(right, 16));
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

    public static String stringToHex(String in) {
        StringBuilder sb = new StringBuilder();
        char[] ch = in.toCharArray();
        for (char c : ch) {
            String hexString = Integer.toHexString(c);
            sb.append(hexString);
        }
        return sb.toString();
    }

    //GENERATE KEY

    public void generateKeyOnClick(){
        aesMain.setKey(aesMain.generateKey());
        keyTextField.setText(byteArrayToHexString(aesMain.getKey()));
    }

    //ENCRYPT

    public void encryptOnClick() {
        try{
            aesMain.setKey(hexStringToByteArray(keyTextField.getText()));
            aesMain.testKey();
        } catch(AESException e) {
            DialogBox.dialogAboutError("Invalid Key! " + e.getMessage());
            return;
        }

        if (textboxRadio.isSelected()) {
            if (plaintextTextBox.getText().isEmpty()) {
                DialogBox.dialogAboutError("Plaintext can't be empty!");
                return;
            }
            String plainText = plaintextTextBox.getText();
            aesMain.setPlainText(plainText.getBytes(StandardCharsets.UTF_8));
        }
        if(fileRadio.isSelected()) {
            if (plaintextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }
        try{
            aesMain.encryption();
        } catch (AESException e) {
            DialogBox.dialogAboutError("PlainText error! " + e.getMessage());
            return;
        }
        cyphertextTextBox.setText(byteArrayToHexString(aesMain.getCypherText()));

    }

    //DECRYPT

    public void decprytOnClick() {

        try{
            aesMain.setKey(hexStringToByteArray(keyTextField.getText()));
            aesMain.testKey();
        } catch(AESException e) {
            DialogBox.dialogAboutError("Invalid Key!" + e.getMessage());
            return;
        }

        if(textboxRadio.isSelected()) {
            if (cyphertextTextBox.getText().isEmpty()) {
                DialogBox.dialogAboutError("CypherText can't be empty!");
                return;
            }
            String cypherText = cyphertextTextBox.getText();
            try {
                aesMain.setCypherText(hexStringToByteArray(cypherText));
            } catch (AESException e) {
                DialogBox.dialogAboutError("CypherText error! " + e.getMessage());
                return;
            }
        }
        if(fileRadio.isSelected()) {
            if (cyphertextFileRead.getText().isEmpty()) {
                DialogBox.dialogAboutError("Choose o file!");
                return;
            }
        }
        try{
            aesMain.decryption();
        } catch (AESException e) {
            DialogBox.dialogAboutError("CypherText error! " + e.getMessage());
            return;
        }

        plaintextTextBox.setText(new String(aesMain.getPlainText(), StandardCharsets.UTF_8));
    }


    //READ FROM FILE & WRITE TO FILE


    private static File configureOpenFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Choose a file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        return fileChooser.showOpenDialog(null);
    }

    private static File configureWriteFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Write a file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        return fileChooser.showSaveDialog(null);
    }

    public void readKeyFile() {
        File file = configureOpenFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch(IOException e) {
            e.printStackTrace();
        }
        if (bytes == null) {
            return;
        }
        aesMain.setKey(bytes);
        keyTextField.setText(new String(bytes));
        keyFileRead.setText(file.toString());
    }

    public void writeKeyFile() {
        File file = configureWriteFileChooser(fileChooser);
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


    public void readPlaintext() {
        File file = configureOpenFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            FileInputStream in = new FileInputStream(file);
            int size = in.available();
            bytes = new byte[size];
            in.read(bytes);
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            DialogBox.dialogAboutError("File is empty!");
        }

        aesMain.setPlainText(bytes);
        plaintextFileRead.setText(file.toString());
        plaintextTextBox.setText(new String(bytes, StandardCharsets.UTF_8));

    }

    public void readCyphertext() {
        File file = configureOpenFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        byte[] bytes = null;
        try {
            FileInputStream in = new FileInputStream(file);
            int size = in.available();
            bytes = new byte[size];
            in.read(bytes);
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        if(bytes == null) {
            DialogBox.dialogAboutError("File is empty!");
        }

        aesMain.setCypherText(bytes);
        cyphertextFileRead.setText(file.toString());
        cyphertextTextBox.setText(byteArrayToHexString(bytes));
    }

    public void writePlaintext() {
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try{
            FileOutputStream out = new FileOutputStream(file);
            out.write(aesMain.getPlainText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plaintextFileWrite.setText(file.toString());
    }

    public void writeCyphertext() {
        File file = configureWriteFileChooser(fileChooser);
        if (file == null) {
            return;
        }
        try{
            FileOutputStream out = new FileOutputStream(file);
            out.write(aesMain.getCypherText());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cyphertextFileWrite.setText(file.toString());
    }

}
