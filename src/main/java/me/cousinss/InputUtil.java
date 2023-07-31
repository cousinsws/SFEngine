package me.cousinss;

public class InputUtil {

    public static int[][] fromTriangular(String... input) {
        int[][] out = new int[input.length+1][input.length+1];
        for(int i = 0; i < input.length; i++) {
            for(int j = 0; j < input.length; j++) {
                out[i][j] = 0;
            }
        }
        for(int i = 0; i < input.length-1; i++) {
            for(int j = 0; j < input[i].length(); j++) {
                out[i][i+j+1] = Integer.parseInt(""+input[i].charAt(j));
            }
        }
        for(int i = 0; i < input.length+1; i++) {
            for(int j = 0; j < i; j++) {
                out[i][j] = out[j][i];
            }
        }
        return out;
    }
}
