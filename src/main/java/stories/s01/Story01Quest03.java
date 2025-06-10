package stories.s01;

import com.google.common.collect.Sets;
import common.AbstractQuest;
import common.support.interfaces.Quest03;
import common.support.interfaces.Story01;
import common.support.params.ExecutionParameters;
import org.apache.commons.math3.primes.Primes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Story01Quest03 extends AbstractQuest implements Story01, Quest03 {

    private static final Pattern PATTERN = Pattern.compile("^x=(?<x>\\d+) y=(?<y>\\d+)$");

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int total = 0;

        for (String line: inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {

                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));

                int ringSum = x + y; // constant along the ring
                int ringLength = ringSum - 1; // the number of positions in the ring
                int newX = (int) ((long) x + 99) % ringLength + 1; // so it is in range [1..(ringLength)] instead of [0..(ringLength-1)]
                int newY = ringSum - newX;

                int value = newX + 100 * newY;

                total += value;

            }
        }

        return Integer.toString(total);
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        List<Integer> mods = new ArrayList<>();
        List<Integer> remainders = new ArrayList<>();

        for (String line: inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.matches()) {

                int x = Integer.parseInt(matcher.group("x"));
                int y = Integer.parseInt(matcher.group("y"));

                int ringLength = x + y - 1;
                mods.add(ringLength);
                remainders.add(y - 1);

            }
        }

        return solveSystemOfModularEquations(mods, remainders).toString();

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {
        return solvePart2(input, inputLines, executionParameters);
    }

    private BigInteger solveSystemOfModularEquations(List<Integer> mods, List<Integer> remainders) {

        //mods = new ArrayList<>(mods);
        remainders = new ArrayList<>(remainders);

        int numberOfEquations = mods.size();
        if (remainders.size() != numberOfEquations) {
            throw new IllegalArgumentException("Incompatible sizes: " + mods.size() + " and " + remainders.size());
        }

        List<Set<Integer>> uniquePrimeFactors = new ArrayList<>();

        for (int mod: mods) {
            List<Integer> factors = Primes.primeFactors(mod);
            uniquePrimeFactors.add(new HashSet<>(factors));
        }

        // Check pairwise coprimality
        for (int index1 = 0; index1 < numberOfEquations; index1++) {
            for (int index2 = index1 + 1; index2 < numberOfEquations; index2++) {

                if (!Sets.intersection( uniquePrimeFactors.get(index1), uniquePrimeFactors.get(index2) ).isEmpty()) {
                    throw new IllegalArgumentException(mods.get(index1) + " and " + mods.get(index2) + " are not coprime");
                }

            }
        }

        // Normalize remainders so that they are in the range [0, mod[
        for (int index = 0; index < numberOfEquations; index++) {

            int oldRemainder = remainders.get(index);
            int mod = mods.get(index);

            int newRemainder = (oldRemainder % mod + mod) % mod;
            remainders.set(index, newRemainder);

        }

        // Compute mcm
        BigInteger mcm = BigInteger.ONE;
        for (int mod: mods) {
            mcm = mcm.multiply(BigInteger.valueOf(mod));
        }

        BigInteger total = BigInteger.ZERO;
        for (int index = 0; index < numberOfEquations; index++) {

            BigInteger mod = BigInteger.valueOf(mods.get(index));
            BigInteger remainder = BigInteger.valueOf(remainders.get(index));

            BigInteger productOfTheOtherMods = mcm.divide( mod );
            BigInteger modInverse = productOfTheOtherMods.modInverse(mod);

            BigInteger x = remainder.multiply(productOfTheOtherMods).multiply(modInverse);
            total = total.add(x);

        }

        return total.mod(mcm);
    }

}
