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
        int p = randPrime(minPrime, 1423);
        int q;
        do {
            q = randPrime(minPrime, 7027);
        } while (p == q);
        int n = p * q;
        int yn = (q - 1) * (p - 1);                            //Totient
        int e;
        do {
            e = getRandom(1000000, yn);
        } while (!areCoprime(e, yn));
        int d = modInverse(e, yn);                           //Modular Inverse

        if (!Key.validateKeyPair(new PrivateKey(n, d, checksum(n), checksum(d)),
                new PublicKey(n, e, checksum(n), checksum(e))))
            return makePrivateKey();
        return new PrivateKey(d, n, e);
    }

    private int findE(int n, int d) {
        int p, q, e;
        do {
            p = randPrime(minPrime, 1423);
            q = n / p;
        } while (!isPrime(q));
        int yn = (q - 1) * (p - 1);

        for (int i = 1000000; i <= yn; i++) {
            e = getRandom(1000000, yn);
            if (areCoprime(e, yn) && d == modInverse(e, yn))
                return e;
        }
        return -1;
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
            output = processNums(crypt(processText(output)));
        }
        return toText(processText(output));
    }

    public String decode(String message) {
        return decode(message, 1);
    }

    public PublicKey makePublic() {
        if (this.publicPart2 == -1)
            this.publicPart2 = findE(this.part1, this.part2);
        return new PublicKey(this.part1, this.publicPart2, this.checkSumPart1, checksum(this.publicPart2));
    }
}
