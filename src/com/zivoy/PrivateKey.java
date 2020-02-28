package com.zivoy;

public class PrivateKey extends Key {
    private int publicPart2;

    public PrivateKey() {
        PrivateKey t = makePrivateKey();
        this.part1 = t.part1;
        this.part2 = t.part2;
        this.publicPart2 = t.publicPart2;
    }

    public PrivateKey(int d, int n, int e) {
        this.part1 = n;
        this.part2 = d;
        this.publicPart2 = e;
    }

    public PrivateKey(int keyPart1, int keyPart2) {
        this.part1 = keyPart1;
        this.part2 = keyPart2;
        this.publicPart2 = -1;
    }

    private static String reverse(String string) {
        StringBuilder stringBuilder = new StringBuilder(string);

        stringBuilder.reverse();

        return stringBuilder.toString();
    }

    public static PrivateKey fromString(String key) {
        String deKey = reverse(String.valueOf(toNum(key)));
        int[] parts = splitKey(deKey);
        return new PrivateKey(parts[0], parts[1]);
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

        String test = "Hello, World!";
        if (!new PrivateKey(n, d).decode((new PublicKey(n, e).encode(test))).equals(test))
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
        String n = String.format("%07d", this.part1);
        String d = String.format("%07d", this.part2);

        return toEnc(Long.parseLong(reverse(n + d)));
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
        return new PublicKey(this.part1, this.publicPart2);
    }
}
