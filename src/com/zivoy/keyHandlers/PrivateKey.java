package com.zivoy.keyHandlers;

public class PrivateKey extends Key {
    private int publicPart2;

    public PrivateKey() {
        PrivateKey t = makePrivateKey();
        this.part1 = t.part1;
        this.part2 = t.part2;
        this.publicPart2 = t.publicPart2;
        this.checkSumPart1 = checksum(part1);
        this.checkSumPart2 = checksum(part2);
    }

    public PrivateKey(int d, int n, int e) {
        this.part1 = n;
        this.part2 = d;
        this.publicPart2 = e;
        this.checkSumPart1 = checksum(part1);
        this.checkSumPart2 = checksum(part2);
    }

    public PrivateKey(int keyPart1, int keyPart2, int checkSumPart1, int checkSumPart2) {
        this.part1 = keyPart1;
        this.part2 = keyPart2;
        this.publicPart2 = -1;
        this.checkSumPart1 = checkSumPart1;
        this.checkSumPart2 = checkSumPart2;
    }

    private static String reverse(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);

        stringBuilder.reverse();

        return stringBuilder.toString();
    }

    private static long reverse(long number) {
        return Long.parseLong(reverse(String.valueOf(number)));
    }

    public static PrivateKey fromString(String key) {
        int checkSumP1 = key.charAt(0);
        int checkSumP2 = key.charAt(key.length()-1);
        key = key.substring(1,key.length()-1);
        String deKey = reverse(String.valueOf(toNum(key)));
        int[] parts = splitKey(deKey);
        return new PrivateKey(parts[0], parts[1], checkSumP1, checkSumP2);
    }

    public PrivateKey makePrivateKey() {
        long p = randPrime(minPrime, 1423);
        long q;
        do {
            q = randPrime(minPrime, 7027);
        } while (p == q);
        long n = p * q;
        long yn = (q - 1) * (p - 1);                            //Totient
        long e;
        do {
            e = getRandom(1000000, yn);
        } while (!areCoprime(e, yn));
        long d = modInverse(e, yn);                           //Modular Inverse

        int in = (int)n;
        int id = (int)d;
        int ie = (int)e;

        if (!Key.validateKeyPair(new PrivateKey(in, id, checksum(in), checksum(id)),
                new PublicKey(in, ie, checksum(in), checksum(ie))))
            return makePrivateKey();
        return new PrivateKey(id, in, ie);
    }

    @Override
    public String getKey() {
        String key = super.getKey();
        char p1 = key.charAt(0);
        char p2 = key.charAt(key.length()-1);
        key = key.substring(1, key.length()-1);
        key = toEnc(reverse(toNum(key)));
        return p1+key+p2;
    }

    @Override
    public String toString() {
        return this.getKey();
    }

    public String decode(String message, int n) {
        StringBuilder messageBuilder = new StringBuilder(message);
        while (messageBuilder.length() % 3 != 0) {
            messageBuilder.insert(0, " ");
        }
        String output = messageBuilder.toString();
        for (int i = 0; i < n; i++) {
            output = processNums(crypt(processText(output), true));
        }
        return toText(processText(output));
    }

    public String decode(String message) {
        return decode(message, 1);
    }

    public PublicKey makePublic() {
        return new PublicKey(this.part1, this.publicPart2, this.checkSumPart1, checksum(this.publicPart2));
    }
}
