package module;

public class Box {

    private static final int numberOfBytes = 4; //Number of chars(32bit)
    private static final int numberOfRounds = 10;

    byte[][] matrix = { {2, 3, 1, 1},
                        {1, 2, 3, 1},
                        {1, 1, 2, 3},
                        {3, 1, 1, 2} };

    byte[][] invMatrix = { {0x0e, 0x0b, 0x0d, 0x09},
                           {0x09, 0x0e, 0x0b, 0x0d},
                           {0x0d, 0x09, 0x0e, 0x0b},
                           {0x0b, 0x0d, 0x09, 0x0e} };


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
                box[row][column] = Sbox.replace_byte(box[row][column], Sbox.s_box);
            }
        }
    }

    public static void shiftRows(byte[][] box) {
        byte[][] temp = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                temp[row][column] = box[row][column];
            }
        }
        //row is also a number that defines how many bytes have to be shifted in each row
        for (int row = 1; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = temp[row][(column + row) % 4];
            }
        }
    }

    public static void invShiftRows(byte[][] box) {
        byte[][] temp = new byte[4][4];
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                temp[row][column] = box[row][column];
            }
        }
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

    public void printBox(byte[][] box) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                System.out.print(box[row][column] + ", ");
            }
            System.out.println();
        }
        System.out.println("------------");
    }

}
