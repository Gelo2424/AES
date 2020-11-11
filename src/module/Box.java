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

    public static int[][] rCon = {
            {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}
    };

    public static void addRoundKey(byte[][] box, byte[][] scheduledKey, int round) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                //Runda zaczyna się od zerowej
                box[row][column] ^= scheduledKey[row][round * 4 + column];
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

    // WIKIPEDIA KOD
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

    public static byte[][] keySchedule(byte[] key) {
        byte[][] temp = new byte[4][44];
        for (int column = 0; column < 4; column++) {
            for (int row = 0; row < 4; row++) {
                temp[row][column] = key[column * 4 + row];
            }
        }
        int column_counter = 3;

        for (int round = 0; round < 10; round++) {
            byte[] temp_column = {temp[1][column_counter], temp[2][column_counter], temp[3][column_counter], temp[0][column_counter]};
            for(int i = 0; i < 4; i++) {
                temp_column[i] = Sbox.replace_byte(temp_column[i],true);
            }
            for(int i = 0; i < 4; i++) {
                //możliwe, że te zmienne do xora muszą być najpierw zmienione na inty
                temp[i][column_counter + 1] = (byte) (temp[i][column_counter - 3] ^ temp_column[i] ^ rCon[i][round]);
            }
            for(int column = column_counter + 2; column <= column_counter + 4; column++) {
                for(int row = 0; row < 4; row++) {
                    temp[row][column] = (byte) (temp[row][column - 4] ^ temp[row][column - 1]);
                }
            }
            column_counter = column_counter + 4;
        }
        return temp;
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
