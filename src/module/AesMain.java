package module;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AesMain {

    private byte[] key;
    private byte[] plainText;
    private byte[] cypherText;

    private final int numberOfBytes = 4;   //Number of chars(32bit)
    private final int numberOfRounds = 10; //Number of round for AES
    private byte[][] mainKey;              //Matrix for key

    //GETTERS & SETTERS
    public byte[] getKey() {
        return key;
    }
    public void setKey(byte[] key) {
        this.key = key;
    }
    public byte[] getPlainText() {
        return plainText;
    }
    public void setPlainText(byte[] plainText) {
        this.plainText = plainText;
    }
    public byte[] getCypherText() {
        return cypherText;
    }
    public void setCypherText(byte[] cypherText) {
        this.cypherText = cypherText;
    }

    //GENERATE 128bit KEY FOR AES
    public byte[] generateKey() {
        KeyGenerator gen = null;
        try{
            gen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert gen != null;
        gen.init(128);
        SecretKey secret = gen.generateKey();
        key = secret.getEncoded();
        return key;
    }

    //KEY TESTING
    public void testKey () throws AESException {
        if (key == null || key.length == 0) {
            throw new AESException("Key is too short!");
        }
        int len = key.length;
        if (len < 16) {
            throw new AESException("Key is too short!");
        }
        if (len > 16) {
            throw new AESException("Key is too long!");
        }
    }

    //THIS METHOD IS INVOKE FROM encryptOnClick method from AesController
    public void encryption() throws AESException {
        if(plainText == null || plainText.length == 0) {
            throw new AESException("PlainText can't be empty!");
        }
        cypherText = encode(plainText, key);

    }

    //THIS METHOD IS INVOKE FROM decryptOnClick method from AesController
    public void decryption() throws AESException {
        if(cypherText == null || cypherText.length == 0) {
            throw new AESException("CypherText can't be empty!");
        }
        plainText = decode(cypherText, key);
    }

    //ENCRYPT BOX
    private byte[] encrypt(byte[] in) {
        byte[] out = new byte[in.length];
        byte[][] box = new byte[4][4];

        //INPUT INTO BOX
        int k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                box[i][j] = in[k++];
            }
        }

        //FIRST ROUND
        Box.addRoundKey(box, mainKey, 0);

        //1-9 ROUNDS
        for (int actualRound = 1; actualRound < numberOfRounds; actualRound++) {
            Box.subBytes(box);
            Box.shiftRows(box);
            Box.mixColumns(box);
            Box.addRoundKey(box, mainKey, actualRound);
        }

        //LAST ROUND
        Box.subBytes(box);
        Box.shiftRows(box);
        Box.addRoundKey(box, mainKey, numberOfRounds);

        //READ OUTPUT FROM BOX
        k = 0;
        for (byte[] bytes : box) {
            for (int j = 0; j < box.length; j++) {
                out[k++] = bytes[j];
            }
        }
        return out;
    }

    //DECRYPTE BOX
    private byte[] decrypt(byte[] in) {
        byte[] out = new byte[in.length];
        byte[][] box = new byte[4][4];

        //INPUT INTO BOX
        int k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                box[i][j] = in[k++];
            }
        }

        //LAST ROUND
        Box.addRoundKey(box, mainKey, numberOfRounds);

        //9-1 ROUNDS
        for (int actualRound = numberOfRounds - 1; actualRound >= 1; actualRound--) {
            Box.invSubBytes(box);
            Box.invShiftRows(box);
            Box.addRoundKey(box, mainKey, actualRound);
            Box.invMixColumns(box);
        }

        //FIRST ROUND
        Box.invSubBytes(box);
        Box.invShiftRows(box);
        Box.addRoundKey(box, mainKey, 0);

        //READ OUTPUT FROM BOX
        k = 0;
        for (byte[] bytes : box) {
            for (int j = 0; j < box.length; j++) {
                out[k++] = bytes[j];
            }
        }
        return out;
    }

    private byte[][] generateKey(byte[] key) {
        byte[][] temp = new byte[numberOfBytes * (numberOfRounds + 1)][4];
        int i = 0;
        int j = 0;
        while (i < 4)
        {
            temp[i][0] = key[j];
            temp[i][1] = key[j++];
            temp[i][2] = key[j++];
            temp[i][3] = key[j++];
            i++;
            j++;
        }
        return temp;
    }

    private void mixColumns(byte[][] s) {
        int[] sp = new int[4];
        byte b02 = (byte)0x02;
        byte b03 = (byte)0x03;

        for (int c = 0; c < 4; c++)
        {
            sp[0] = fMul(b02, s[0][c]) ^ fMul(b03, s[1][c]) ^ s[2][c]  ^ s[3][c];
            sp[1] = s[0][c]  ^ fMul(b02, s[1][c]) ^ fMul(b03, s[2][c]) ^ s[3][c];
            sp[2] = s[0][c]  ^ s[1][c]  ^ fMul(b02, s[2][c]) ^ fMul(b03, s[3][c]);
            sp[3] = fMul(b03, s[0][c]) ^ s[1][c]  ^ s[2][c]  ^ fMul(b02, s[3][c]);
            for (int i = 0; i < 4; i++) s[i][c] = (byte)(sp[i]);
        }
    }

    public  byte fMul(byte a, byte b) {
        byte aa = a, bb = b, r = 0, t;
        while (aa != 0)
        {
            if ((aa & 1) != 0)
                r = (byte) (r ^ bb);
            t = (byte) (bb & 0x20);
            bb = (byte) (bb << 1);
            if (t != 0)
                bb = (byte) (bb ^ 0x1b);
            aa = (byte) ((aa & 0xff) >> 1);
        }
        return r;
    }



    private byte[] encode(byte[] message, byte[] key) {
        int blockSize = 16;
        int messageSize = blockSize;
        int blocks = message.length / blockSize;

        //messageSize must be multiple of 16
        if ((message.length % blockSize) != 0) {
            messageSize = (blocks + 1) * blockSize;
        }
        else {
            messageSize = blocks * blockSize;
        }
        byte[] cypherText = new byte[messageSize];
        byte[] temp = new byte[messageSize];
        byte[] block = new byte[blockSize];
        mainKey = generateKey(key);
        for (int i = 0; i < messageSize; i++) {
            if(i < message.length) {
                temp[i]=message[i];
            }
            else {
                temp[i] = 0;
            }
        }

        //DIVIDE MESSAGE INTO BLOCKS AND ENCRYPT THEM
        int pos = 0;
        for (int i = 0; i < temp.length;) {
            for (int j = 0; j < blockSize; j++) {
                block[j] = temp[i++];
            }
            block = encrypt(block);
            System.arraycopy(block, 0, cypherText,i - blockSize, block.length);
        }
        return cypherText;
    }

    private  byte[] decode(byte[] cypherText, byte[] key) {
        int blockSize = 16;
        byte[] temp = new byte[cypherText.length];
        byte[] block = new byte[blockSize];
        mainKey = generateKey(key);

        //DIVIDE MESSAGE INTO BLOCKS AND DECRYPT THEM
        for (int i = 0; i < cypherText.length;) {
            for (int j = 0; j < blockSize; j++) {
                block[j]=cypherText[i++];
            }
            block = decrypt(block);
            System.arraycopy(block, 0, temp,i-blockSize, block.length);
        }

        int counter = 0;
        for (int i = 1; i < 17; i += 2)
        {
            if (temp[temp.length - i] == 0 && temp[temp.length - i - 1] == 0) {
                counter += 2;
            }
            else {
                break;
            }
        }
        byte[] plainText = new byte[temp.length - counter];
        for (int i = 0; i < temp.length - counter; i++) {
            plainText[i] = temp[i];
        }
        return plainText;
    }

}