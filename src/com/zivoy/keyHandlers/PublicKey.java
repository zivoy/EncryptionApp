package com.zivoy.keyHandlers;

public class PublicKey extends Key {
    public PublicKey(int part1, int part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    public static PublicKey fromString(String key) {
        String deKey = String.valueOf(toNum(key));
        int[] parts = splitKey(deKey);
        return new PublicKey(parts[0], parts[1]);
    }

    public String encode(String message, int n) {
        String output = processNums(toAscii(message));
        for (int i = 0; i < n; i++) {
            output = processNums(crypt(processText(output)));
        }
        return strip(output);
    }

    public String encode(String message) {
        return encode(message, 1);
    }

    @Override
    public String toString() {
        return this.getKey();
    }

    public boolean validate() {
        String output;
        try {
            output = this.encode("Hello, World!");
        } catch (Exception e) {
            return false;
        }
        for (char i : output.toCharArray()) {
            if (!((i <= 'z' && i >= 'a') || (i <= 'Z' && i >= 'A') || i == ' '))
                return false;
        }
        return true;
    }
}
