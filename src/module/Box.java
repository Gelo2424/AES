package module;

public class Box {

    private static final int numberOfBytes = 4; //Number of chars(32bit)

    /*
    byte[][] matrix = { {2, 3, 1, 1},
                        {1, 2, 3, 1},
                        {1, 1, 2, 3},
                        {3, 1, 1, 2} };

    byte[][] invMatrix = { {0x0e, 0x0b, 0x0d, 0x09},
                           {0x09, 0x0e, 0x0b, 0x0d},
                           {0x0d, 0x09, 0x0e, 0x0b},
                           {0x0b, 0x0d, 0x09, 0x0e} };
    */

    //Combined key with box
    public static void addRoundKey(byte[][] box, byte[][] key, int round) {
        for (int i = 0; i < numberOfBytes; i++) {
            for (int j = 0; j < 4; j++) {
                box[j][i] ^= key[round * numberOfBytes + i][j];
            }
        }
    }

    //Replaces elements in arr with values from SBox
    public static void subBytes(byte[][] box) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = Sbox.replace_byte(box[row][column], true);
            }
        }
    }

    public static void invSubBytes(byte[][] box) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = Sbox.replace_byte(box[row][column], false);
            }
        }
    }

    public static void shiftRows(byte[][] box) {
        byte[][] temp = copyBox(box);
        //row is also a number that defines how many bytes have to be shifted in each row
        for (int row = 1; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = temp[row][(column + row) % 4];
            }
        }
    }

    public static void invShiftRows(byte[][] box) {
        byte[][] temp = copyBox(box);
        //row is also a number that defines how many bytes have to be shifted in each row
        for (int row = 1; row < 4; row++) {
            for (int column = 3; column >= 0; column--) {
                if(column - row < 0) {
                    box[row][column] = temp[row][(column - row + 4) % 4];
                }
                else{
                    box[row][column] = temp[row][(column - row) % 4];
                }
            }
        }
    }

    public static void mixColumns(byte[][] box) {
        byte[][] temp = Box.copyBox(box);
        byte b02 = (byte) 0x02;
        byte b03 = (byte) 0x03;
        for (int column = 0; column < 4; column++) {
            box[0][column] = (byte)( fMul(temp[0][column], b02) ^ fMul(temp[1][column], b03) ^ temp[2][column] ^ temp[3][column] );
            box[1][column] = (byte)( temp[0][column] ^ fMul(temp[1][column], b02) ^ fMul(temp[2][column], b03) ^ temp[3][column] );
            box[2][column] = (byte)( temp[0][column] ^ temp[1][column] ^ fMul(temp[2][column], b02) ^ fMul(temp[3][column], b03) );
            box[3][column] = (byte)( fMul(temp[0][column], b03) ^ temp[1][column] ^ temp[2][column] ^ fMul(temp[3][column], b02) );
        }
    }

    public static void invMixColumns(byte[][] box) {
        byte[][] temp = Box.copyBox(box);
        byte b01 = (byte) 0x0e;
        byte b02 = (byte) 0x0b;
        byte b03 = (byte) 0x0d;
        byte b04 = (byte) 0x09;

        for (int column = 0; column < 4; column++) {
            box[0][column] = (byte)( fMul(temp[0][column], b01) ^ fMul(temp[1][column], b02) ^ fMul(temp[2][column], b03) ^ fMul(temp[3][column], b04) );
            box[1][column] = (byte)( fMul(temp[0][column], b04) ^ fMul(temp[1][column], b01) ^ fMul(temp[2][column], b02) ^ fMul(temp[3][column], b03) );
            box[2][column] = (byte)( fMul(temp[0][column], b03) ^ fMul(temp[1][column], b04) ^ fMul(temp[2][column], b01) ^ fMul(temp[3][column], b02) );
            box[3][column] = (byte)( fMul(temp[0][column], b02) ^ fMul(temp[1][column], b03) ^ fMul(temp[2][column], b04) ^ fMul(temp[3][column], b01) );
        }
    }

    /* NASZ KOD
    private static byte fMul(byte state, int mulNum) {
        // mulNum powinien być tylko 2 lub 3
        int result = state;
        if (result < 0) {
            result = result << 24;
            result = result >>> 24;
        }
        result = result * 2;
        if (result > 0xff) {
            result = result ^ 0x1b;
        }
        if (mulNum == 2) {
            return (byte)result;
        } else {
            result = result ^ state;
            return (byte)result;
        }
    }*/
    /* WIKIPEDIA KOD
    private static byte fMul( byte b, byte a) {
        byte p = 0;
        for (int counter = 0; counter < 8; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }

            boolean hi_bit_set = (a & 0x80) != 0;
            a <<= 1;
            if (hi_bit_set) {
                a ^= 0x1B; // x^8 + x^4 + x^3 + x + 1
            }
            b >>= 1;
        }
        return p;
    }
    */
    public static byte fMul(byte state, byte mulNum) {
        byte out = 0;
        byte t;
        while (mulNum != 0)
        {
            if ((mulNum & 1) != 0) {
                out = (byte) (out ^ state);
            }
            t = (byte) (state & 0x20);
            state = (byte) (state << 1);
            if (t != 0) {
                state = (byte) (state ^ 0x1b);
            }
            mulNum = (byte) ((mulNum & 0xff) >> 1);
        }
        return out;
    }

    private static byte[][] copyBox(byte[][] box) {
        byte[][] temp = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                temp[row][column] = box[row][column];
            }
        }
        return temp;
    }

}
