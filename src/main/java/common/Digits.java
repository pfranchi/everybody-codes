package common;

import common.count.IntCount;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public final class Digits {

    private Digits() {
    }

    /*
        Convert number to char array
     */

    public static char[] toCharArray(int n) {
        return Integer.toString(n).toCharArray();
    }

    public static char[] toCharArray(long n) {
        return Long.toString(n).toCharArray();
    }

    /*
        Get digits as int array
     */

    public static int[] toIntArray(char[] chars) {
        int l = chars.length;
        int[] intArray = new int[l];
        for (int i = 0; i < l; i++) {
            intArray[i] = Integer.parseInt(String.valueOf(Character.valueOf(chars[i])));
        }
        return intArray;
    }

    public static int[] toIntArray(String s) {
        return toIntArray(s.toCharArray());
    }

    public static int[] toIntArray(int n) {
        return toIntArray(Integer.toString(Math.abs(n)));
    }

    public static int[] toIntArray(long n) {
        return toIntArray(Long.toString(Math.abs(n)));
    }

    /*
        From digits to int
     */

    public static int toInt(char[] digits) {
        final boolean isNegative = digits[0] == '-';
        int startIndex = 0;
        if (digits[0] == '-' || digits[0] == '+') {
            digits = Arrays.copyOfRange(digits, 1, digits.length);
        }
        StringBuilder sb = new StringBuilder();
        for (char c : digits) {
            sb.append(c);
        }
        int result = Integer.parseInt(sb.toString());
        return isNegative ? -result : result;
    }

    public static int toInt(int[] digits) {
        int res = 0;
        for (int digit : digits) {
            res = res * 10 + digit;
        }
        return res;
    }

    public static int toInt(Collection<Integer> digits) {
        int res = 0;
        for (int digit : digits) {
            res = res * 10 + digit;
        }
        return res;
    }

    /*
        From digits to long
     */

    // TODO

    /*
        Check properties regarding digits
     */

    public static boolean inNonDecreasingOrder(int n) {
        return allDigitsSatisfy(n, (i, j) -> i <= j, i -> true);
    }

    public static boolean inStrictAscendingOrder(int n) {
        return allDigitsSatisfy(n, (i, j) -> i < j, i -> true);
    }

    public static boolean inNonIncreasingOrder(int n) {
        return allDigitsSatisfy(n, (i, j) -> i >= j, i -> true);
    }

    public static boolean inStrictDescendingOrder(int n) {
        return allDigitsSatisfy(n, (i, j) -> i > j, i -> true);
    }

    public static boolean allDigitsSatisfy(int n, BiPredicate<Integer, Integer> consecutiveDigitsCondition, IntPredicate singleDigitCondition) {
        int[] digitArray = toIntArray(n);
        int l = digitArray.length;
        if (l == 1) {
            return singleDigitCondition.test(digitArray[0]);
        }
        return IntStream.range(0, l - 1)
                .allMatch(i -> consecutiveDigitsCondition.test(digitArray[i], digitArray[i + 1]));
    }

    public static boolean hasPairOfEqualConsecutiveDigits(int n) {
        return anyDigitSatisfy(n, Integer::equals, i -> false);
    }

    public static boolean anyDigitSatisfy(int n, BiPredicate<Integer, Integer> consecutiveDigitsCondition, IntPredicate singleDigitCondition) {
        int[] digitArray = toIntArray(n);
        int l = digitArray.length;
        if (l == 1) {
            return singleDigitCondition.test(digitArray[0]);
        }
        return IntStream.range(0, l - 1)
                .anyMatch(i -> consecutiveDigitsCondition.test(digitArray[i], digitArray[i + 1]));
    }

    public static boolean isPalindromic(int n) {
        char[] charArray = toCharArray(n);
        int l = charArray.length;
        if (l == 1) {
            return true;
        }
        for (int i = 0, j = l - i - 1; i < j; i++) {
            if (charArray[i] != charArray[j]) {
                return false;
            }
        }
        return true;
    }

    /*
        Compute operations related to digits
     */

    public static int reverseDigits(int n) {
        final boolean isNegative = n < 0;
        int[] digits = toIntArray(n);
        ArrayUtils.reverse(digits);
        return isNegative ? -toInt(digits) : toInt(digits);
    }

    public static int sumOfDigits(int n) {
        return Arrays.stream(toIntArray(n)).sum();
    }

    public static List<IntCount> getRunOfDigits(int n) {
        return getRunOfDigits(toIntArray(n));
    }

    public static List<IntCount> getRunOfDigits(String s) {
        return getRunOfDigits(toIntArray(s));
    }

    public static List<IntCount> getRunOfDigits(int[] digits) {

        if (digits.length == 0) {
            return Collections.emptyList();
        }

        List<IntCount> res = new ArrayList<>();

        boolean isStart = true;
        int lastSeenDigit = -1;
        int countOfLastSeenDigit = 0;

        for (int digit : digits) {
            if (digit == lastSeenDigit) {
                countOfLastSeenDigit++;
            } else {

                if (isStart) {
                    // Do not add the first digit count
                    isStart = false;
                } else {
                    res.add(new IntCount(lastSeenDigit, countOfLastSeenDigit));
                }

                // Reset the values for new run of character
                lastSeenDigit = digit;
                countOfLastSeenDigit = 1;
            }
        }

        res.add(new IntCount(lastSeenDigit, countOfLastSeenDigit));

        return res;
    }

}
