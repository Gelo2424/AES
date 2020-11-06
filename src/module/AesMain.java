package module;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AesMain {

    private byte[] key;
    private byte[] plainText;
    private byte[] cypherText;

    private int Nb = 4;
    private int Nk;
    private int Nr;
    private byte[][] mainKey;

    private final int[] sBox = { 0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F,
            0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76, 0xCA, 0x82,
            0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C,
            0xA4, 0x72, 0xC0, 0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC,
            0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15, 0x04, 0xC7, 0x23,
            0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27,
            0xB2, 0x75, 0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52,
            0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84, 0x53, 0xD1, 0x00, 0xED,
            0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58,
            0xCF, 0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9,
            0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8, 0x51, 0xA3, 0x40, 0x8F, 0x92,
            0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
            0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E,
            0x3D, 0x64, 0x5D, 0x19, 0x73, 0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A,
            0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB, 0xE0,
            0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62,
            0x91, 0x95, 0xE4, 0x79, 0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E,
            0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08, 0xBA, 0x78,
            0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B,
            0xBD, 0x8B, 0x8A, 0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E,
            0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E, 0xE1, 0xF8, 0x98,
            0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55,
            0x28, 0xDF, 0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41,
            0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16 };

    private final int[] invSBox = { 0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5,
            0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB, 0x7C, 0xE3,
            0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4,
            0xDE, 0xE9, 0xCB, 0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D,
            0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E, 0x08, 0x2E, 0xA1,
            0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B,
            0xD1, 0x25, 0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4,
            0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92, 0x6C, 0x70, 0x48, 0x50,
            0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D,
            0x84, 0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4,
            0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06, 0xD0, 0x2C, 0x1E, 0x8F, 0xCA,
            0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
            0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF,
            0xCE, 0xF0, 0xB4, 0xE6, 0x73, 0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD,
            0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E, 0x47,
            0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E,
            0xAA, 0x18, 0xBE, 0x1B, 0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79,
            0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4, 0x1F, 0xDD,
            0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27,
            0x80, 0xEC, 0x5F, 0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D,
            0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF, 0xA0, 0xE0, 0x3B,
            0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53,
            0x99, 0x61, 0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1,
            0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D };

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
        try {
            cypherText = encode(plainText, key);
        } catch(AESException e) {
            e.printStackTrace();
        }
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
        byte[][] temp = new byte[Nb * (Nr+1)][4];
        int i = 0;
        int j = 0;
        while (i < Nk)
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


    private byte[][] addRoundKey(byte[][] state, byte[][] w, int round) {
        byte[][] tmp = new byte[state.length][state[0].length];
        for (int c = 0; c < Nb; c++)
        {
            for (int l = 0; l < 4; l++)
                tmp[l][c] = (byte) (state[l][c] ^ w[round * Nb + c][l]);
        }
        return tmp;
    }

    private byte[][] subBytes(byte[][] state) {
        byte[][] temp = new byte[state.length][state[0].length];
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < Nb; col++)
                temp[row][col] = (byte) (sBox[(state[row][col] & 0xff)]);
        return temp;
    }

    private byte[][] invSubBytes(byte[][] state) {
        for (int row = 0; row < 4; row++)
            for (int col = 0; col < Nb; col++)
                state[row][col] = (byte)(invSBox[(state[row][col] & 0xff)]);
        return state;
    }

    private byte[][] shiftRows(byte[][] state) {
        byte[] t = new byte[4];
        for (int r = 1; r < 4; r++)
        {
            for (int c = 0; c < Nb; c++)
                t[c] = state[r][(c + r) % Nb];
            for (int c = 0; c < Nb; c++)
                state[r][c] = t[c];
        }
        return state;
    }

    private byte[][] invShiftRows(byte[][] state) {
        byte[] t = new byte[4];
        for (int r = 1; r < 4; r++)
        {
            for (int c = 0; c < Nb; c++)
                t[(c + r)%Nb] = state[r][c];
            for (int c = 0; c < Nb; c++)
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

    private  byte[][] mixColumns(byte[][] s) {
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
        return s;
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


    public  byte[] encrypt(byte[] in) {
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[4][4];
        for (int i = 0; i < in.length; i++)
            state[i / 4][i % 4] = in[i];
        state = addRoundKey(state, mainKey, 0);
        for (int round = 1; round < Nr; round++)
        {
            state = subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state);
            state = addRoundKey(state, mainKey, round);
        }
        state = subBytes(state);
        state = shiftRows(state);
        state = addRoundKey(state, mainKey, Nr);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i%4];
        return tmp;
    }


    public  byte[] decrypt(byte[] in) {
        byte[] tmp = new byte[in.length];
        byte[][] state = new byte[4][4]; //4 Nb
        for (int i = 0; i < in.length; i++)
            state[i/4][i % 4] = in[i];
        state = addRoundKey(state, mainKey, Nr);
        for (int round = Nr-1; round >=1; round--)
        {
            state = invSubBytes(state);
            state = invShiftRows(state);
            state = addRoundKey(state, mainKey, round);
            state = invMixColumns(state);
        }
        state = invSubBytes(state);
        state = invShiftRows(state);
        state = addRoundKey(state, mainKey, 0);
        for (int i = 0; i < tmp.length; i++)
            tmp[i] = state[i / 4][i%4];
        return tmp;
    }


    public  byte[] encode(byte[] message, byte[] key) throws AESException {
        Nk = key.length/4;
        Nr = Nk + 6;
        if(message.length==0)throw new AESException("Podaj dane do szyfrowania");
        int len;
        int pom=message.length/16;
        if (pom==0) len=16;
        else if ((message.length % 16) != 0)
            len=(pom+1)*16;
        else len=pom*16;
        byte[] result = new byte[len];
        byte[] temp = new byte[len];
        byte[] blok = new byte[16];
        mainKey = generateKey(key);
        for (int i = 0; i < len;i++)
        { if(i<message.length) temp[i]=message[i];
        else temp[i]=0;
        }
        for (int k = 0; k < temp.length;)
        {
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
        Nk = key.length/4;
        Nr = Nk + 6;
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