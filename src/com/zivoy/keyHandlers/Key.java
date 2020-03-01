package com.zivoy.keyHandlers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

public class Key {
    public final Integer[] cached_primes;
    public int part1;
    public int part2;
    public int checkSumPart1;
    public int checkSumPart2;

    Random random = new Random();
    int minPrime = 1009;
    int maxPrime = 7028;

    {
        Integer[] cached_primes1;
        ArrayList<Integer> cached_primes_temp = new ArrayList<>();
        for (int i = minPrime; i < maxPrime; i++) {
            if (isPrime(i)) cached_primes_temp.add(i);
        }
        cached_primes1 = new Integer[cached_primes_temp.size()];
        cached_primes1 = cached_primes_temp.toArray(cached_primes1);
        cached_primes = cached_primes1;
    }

    public static long gcd(long a, long b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    public static boolean areCoprime(long a, long b) {
        return gcd(a, b) == 1;
    }

    public static long modInverse(long a, long m) {
        long Ta = a % m;
        for (long x = 1; x < m; x++) {
            if ((Ta * x) % m == 1)
                return x;
        }
        return 1;
    }

    public static boolean isPrime(int n) {
        if (n % 2 == 0) return false;
        for (int i = 3; i < ceil(sqrt(n)); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static double getRandom(double min, double max) {
        return (random() * (max + 1 - min)) + min;
    }

    public static long getRandom(long min, long max) {
        return Math.min(max, Math.max(min, round(getRandom((double) min, max))));
    }

    public static int[] splitKey(String key) {
        int part1 = Integer.parseInt(key.substring(0, (int) round(key.length() / 2.)));
        int part2 = Integer.parseInt(key.substring((int) round(key.length() / 2.)));
        return new int[]{part1, part2}; //map(int, [key[:len(key)//2], key[len(key)//2:]])
    }

    private static long[] convertPrimitive(ArrayList<Long> in) {
        long[] out = new long[in.size()];
        for (int i = 0; i < in.size(); i++) {
            out[i] = in.get(i);
        }
        return out;
    }

    private static long[] reverseArrayList(long[] in) {
        ArrayList<Long> revArrayList = new ArrayList<>();
        for (int i = in.length - 1; i >= 0; i--) {
            revArrayList.add(in[i]);
        }
        return convertPrimitive(revArrayList);
    }

    private static long[] reverseArrayList(ArrayList<Long> in) {
        return reverseArrayList(convertPrimitive(in));
    }

    public static long[] convertBase(long n, int base_to) {
        if (n == 0L)
            return new long[]{0L};
        ArrayList<Long> digits = new ArrayList<>();
        while (n > 0) {
            digits.add(n % base_to);
            n = Math.floorDiv(n, base_to);
        }
        return reverseArrayList(digits);
    }

    public static long[] convertBase(long[] n, int base_to, int base_from) {
        long[] nLs = reverseArrayList(n);
        long newnum = getNumFromList(nLs, base_from);
        return convertBase(newnum, base_to);
    }

    private static long getNumFromList(long[] list, int base) {
        long sum = 0;
        for (int i = 0; i < list.length; i++) {
            sum += list[i] * Math.pow(base, i);
        }
        return sum;
    }

    public static long toNum(String inText) {
        ArrayList<Long> output = new ArrayList<>();
        long character;
        for (char i : strip(inText).toCharArray()) {
            character = (long) (i) - 39;
            if (i > 90)
                character = (long) (i) - 97;
            output.add(character);
        }
        long[] out = convertBase(convertPrimitive(output), 10, 52);
        StringBuilder message = new StringBuilder();
        for (long i : out) {
            message.append(i);
        }
        return Long.parseLong(message.toString()) - 2080L;
    }

    public final int randPrime(int start, int end) {
        int rand;
        do {
            rand = cached_primes[random.nextInt(cached_primes.length)];
        } while ((start <= rand) && (rand < end));
        return rand;
    }

    private long enAndDecrypt(long message) {
        BigInteger temp = BigInteger.valueOf(message);
        BigInteger temp1 = BigInteger.valueOf(this.part1);
        BigInteger temp2 = BigInteger.valueOf(this.part2);
        BigInteger temp3 = temp.modPow(temp2, temp1);

        return temp3.intValue();
    }

    public final long[] toAscii(String text) {
        char[] chrs = text.toCharArray();
        Long[] out = new Long[chrs.length];
        for (int i = 0; i < chrs.length; i++) {
            out[i] = (long) chrs[i];
        }
        return convertPrimitive(out);
    }

    public final String toText(long[] chrs) {
        StringBuilder out = new StringBuilder();
        for (long chr : chrs) {
            out.append((char) chr);
        }
        return out.toString();
    }

    public final long[] crypt(long[] inMessage, boolean dropSecond) {
        ArrayList<String> newMessage = new ArrayList<>();

        for (int i = 0; i < inMessage.length; i += 2) {
            if (dropSecond) {
                newMessage.add(String.format("%03d", inMessage[i]) + String.format("%03d", inMessage[i + 1]));
                if ((i + 1) < inMessage.length)
                    newMessage.add(String.format("%03d", inMessage[i]));
            } else {
                String message = String.format("%03d", inMessage[i]);
                if ((i + 1) < inMessage.length)
                    message += String.format("%03d", inMessage[i+1]);
                message += getRandom(0,9);
                newMessage.add(message);
            }
        }

        ArrayList<Long> fMessage = new ArrayList<>();

        if (dropSecond){//todo rewrite this completely
            for (int i = newMessage.size()-1; i>0;i-=2){
                newMessage.remove(i);
            }
        }

        for (String i : newMessage) {
            long tempMessage = enAndDecrypt(Long.parseLong(i));
            if (dropSecond)
                tempMessage = tempMessage / 10;
            fMessage.add(tempMessage / 1000);
            fMessage.add(tempMessage % 1000);
        }

        if (fMessage.get(fMessage.size() - 2) == 0)
            fMessage.remove(fMessage.size() - 2);

        return convertPrimitive(fMessage);
    }

    private long[] convertPrimitive(Long[] in) {
        long[] out = new long[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i];
        }
        return out;
    }

    public final String toEnc(long inNum) {
        StringBuilder output = new StringBuilder();
        inNum += 2080;
        for (Long i : convertBase(inNum, 52)) {
            char charecter = (char) (i + 97);
            if (i + 65 > 90) {
                charecter = (char) (i + 39);
            }
            output.append(charecter);
        }
        return String.format("%1$3s", output.toString());
    }

    public final String[] splitString(String string, int cut, String pad) {
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < string.length(); i += cut) {
            String prt = string.substring(i, i + cut);
            if (!pad.equals("")) {
                prt = String.format("%1$-" + cut + "s", prt);
            }
            output.add(prt);
        }
        String[] out = new String[output.size()];
        out = output.toArray(out);
        return out;
    }

    public final long[] processText(String text) {
        ArrayList<Long> output = new ArrayList<>();
        String[] uInput = splitString(text, 3, " ");
        for (String i : uInput) {
            output.add((toNum(i)));
        }
        return convertPrimitive(output);
    }

    public final String processNums(long[] nums) {
        StringBuilder output = new StringBuilder();
        for (long i : nums) {
            output.append(toEnc(i));
        }
        return output.toString();
    }

    public String getKey() {
        String n = String.format("%07d", this.part1);
        String e = String.format("%07d", this.part2);

        return (char) this.checkSumPart1 + toEnc(Long.parseLong(n + e)) + (char) this.checkSumPart2;
    }

    public static String strip(String input, char character){
        String regex = "(^"+character+"+|"+character+"+$)";
        return input.replaceAll(regex, "");
    }

    public static String strip(String input){
        return strip(input, ' ');
    }

    public final int checksum(int number){
        return toEnc(number % 53).charAt(2);
    }

    public boolean validateKey(){
        return this.checkSumPart1 == checksum(this.part1) && this.checkSumPart2 == checksum(this.part2);
    }

    public static boolean validateKeyPair(PrivateKey privateKey, PublicKey publicKey){
        boolean test1 = privateKey.validateKey() && publicKey.validateKey() &&
                privateKey.checkSumPart1 == publicKey.checkSumPart1;
        if (test1){
            String test = "Hello, World!";
            return privateKey.decode(publicKey.encode(test)).equals(test);
        }
        return false;
    }
}
