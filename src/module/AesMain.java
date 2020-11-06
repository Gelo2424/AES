package module;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AesMain {

    private byte[] key;
    private byte[] plainText;
    private byte[] cypherText;

    private final int numberOfBytes = 4; //Number of chars(32bit)
    private final int numberOfRounds = 10; //Number of round for AES
    private byte[][] mainKey;

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

    public void encryption() throws AESException {
        if(plainText == null || plainText.length == 0) {
            throw new AESException("PlainText can't be empty!");
        }
        cypherText = encode(plainText, key);

    }

    public void decryption() throws AESException {
        if(cypherText == null || cypherText.length == 0) {
            throw new AESException("CypherText can't be empty!");
        }
        try {
            plainText = decode(cypherText, key);
        } catch(AESException e) {
            e.printStackTrace();
        }
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

    private byte[][] invSubBytes(byte[][] state) {
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < numberOfBytes; col++)
                state[row][col] = (byte)(Sbox.invSBox[(state[row][col] & 0xff)]);
        return state;
    }

   /* public static void invSubBytes(byte[][] box) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = Sbox.replace_byte(box[row][column], Sbox.invSBox);
            }
        }
    }*/

    private byte[][] invShiftRows(byte[][] state) {
        byte[] t = new byte[4];
        for (int r = 1; r < 4; r++)
        {
            for (int c = 0; c < numberOfBytes; c++)
                t[(c + r)% numberOfBytes] = state[r][c];
            for (int c = 0; c < numberOfBytes; c++)
                state[r][c] = t[c];
        }
        return state;
    }

    private  byte[][] invMixColumns(byte[][] s) {
        int[] sp = new int[4];
        byte b02 = (byte)0x0e, b03 = (byte)0x0b, b04 = (byte)0x0d, b05 = (byte)0x09;
        for (int c = 0; c < 4; c++)
        {
            sp[0] = fMul(b02, s[0][c]) ^ fMul(b03, s[1][c]) ^ fMul(b04,s[2][c])  ^ fMul(b05,s[3][c]);
            sp[1] = fMul(b05, s[0][c]) ^ fMul(b02, s[1][c]) ^ fMul(b03,s[2][c])  ^ fMul(b04,s[3][c]);
            sp[2] = fMul(b04, s[0][c]) ^ fMul(b05, s[1][c]) ^ fMul(b02,s[2][c])  ^ fMul(b03,s[3][c]);
            sp[3] = fMul(b03, s[0][c]) ^ fMul(b04, s[1][c]) ^ fMul(b05,s[2][c])  ^ fMul(b02,s[3][c]);
            for (int i = 0; i < 4; i++) s[i][c] = (byte)(sp[i]);
        }
        return s;
    }

    private void mixColumns(byte[][] s) {
        int[] sp = new int[4];
        byte b02 = (byte)0x02, b03 = (byte)0x03;
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


    public byte[] encrypt(byte[] in) {
        byte[] out = new byte[in.length];
        byte[][] box = new byte[4][4];
        int k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                box[i][j] = in[k++];
            }
        }
        Box.addRoundKey(box, mainKey, 0);
        for (int actualRound = 1; actualRound < numberOfRounds; actualRound++) {
            Box.subBytes(box);
            Box.shiftRows(box);
            mixColumns(box);
            Box.addRoundKey(box, mainKey, actualRound);
        }
        Box.subBytes(box);
        Box.shiftRows(box);
        Box.addRoundKey(box, mainKey, numberOfRounds);
        k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                out[k++] = box[i][j];
            }
        }
        return out;
    }


    public byte[] decrypt(byte[] in) {
        byte[] out = new byte[in.length];
        byte[][] box = new byte[4][4];
        int k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                box[i][j] = in[k++];
            }
        }
        Box.addRoundKey(box, mainKey, numberOfRounds);
        for (int round = numberOfRounds - 1; round >= 1; round--) {
            box = invSubBytes(box);
            box = invShiftRows(box);
            Box.addRoundKey(box, mainKey, round);
            box = invMixColumns(box);
        }
        box = invSubBytes(box);
        box = invShiftRows(box);
        Box.addRoundKey(box, mainKey, 0);
        k = 0;
        for(int i = 0; i < box.length; i++) {
            for(int j = 0; j < box.length; j++) {
                out[k++] = box[i][j];
            }
        }
        return out;
    }


    public  byte[] encode(byte[] message, byte[] key) {
        int len;
        int pom = message.length/16;
        if (pom==0) len = 16;
        else if ((message.length % 16) != 0)
            len = (pom+1)*16;
        else len = pom*16;
        byte[] result = new byte[len];
        byte[] temp = new byte[len];
        byte[] blok = new byte[16];
        mainKey = generateKey(key);
        for (int i = 0; i < len;i++)
        { if(i<message.length) temp[i]=message[i];
        else temp[i]=0;
        }
        for (int k = 0; k < temp.length;) {
            for (int j=0;j<16;j++) blok[j]=temp[k++];
            blok = encrypt(blok);
            System.arraycopy(blok, 0, result,k-16, blok.length);

        }
        return result;
    }

    public  byte[] decode(byte[] encrypted, byte[] key) throws AESException {
        if(encrypted.length==0)throw new AESException("Podaj dane do deszyfrowania");
        byte[] tmpResult = new byte[encrypted.length];
        byte[] blok = new byte[16];
        mainKey = generateKey(key);
        for (int i = 0; i < encrypted.length;)
        {
            for (int j=0;j<16;j++) blok[j]=encrypted[i++];
            blok = decrypt(blok);
            System.arraycopy(blok, 0, tmpResult,i-16, blok.length);
        }
        int cnt = 0;
        for (int i = 1; i < 17; i += 2)
        {
            if (tmpResult[tmpResult.length - i] == 0 && tmpResult[tmpResult.length - i - 1] == 0)
                cnt += 2;
            else  break;
        }
        byte[] result = new byte[tmpResult.length - cnt];
        System.arraycopy(tmpResult, 0, result, 0, tmpResult.length - cnt);
        return result;
    }

}