package com.zivoy.keyHandlers;

public class PublicKey extends Key {
    public PublicKey(int part1, int part2, int checkSumPart1, int checkSumPart2) {
        this.part1 = part1;
        this.part2 = part2;
        this.checkSumPart1 = checkSumPart1;
        this.checkSumPart2 = checkSumPart2;
    }

    public static PublicKey fromString(String key) {
        int checkSumP1 = key.charAt(0);
        int checkSumP2 = key.charAt(key.length()-1);
        key = key.substring(1,key.length()-1);
        String deKey = String.valueOf(toNum(key));
        int[] parts = splitKey(deKey);
        return new PublicKey(parts[0], parts[1], checkSumP1, checkSumP2);
    }

    public String encode(String message, int n) {
        String output = processNums(toAscii(message));
        for (int i = 0; i < n; i++) {
            output = processNums(crypt(processText(output), false));
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
}
