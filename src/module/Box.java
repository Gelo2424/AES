package module;

public class Box {

    byte[][] matrix = { {2, 3, 1, 1},
            {1, 2, 3, 1},
            {1, 1, 2, 3},
            {3, 1, 1, 2} };


    //Replaces elements in arr with values from SBox
    public static void subBytes(byte[][] box) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                box[row][column] = Sbox.replace_byte(box[row][column]);
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
