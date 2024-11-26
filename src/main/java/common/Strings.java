package common;

import com.google.common.primitives.Chars;

import java.util.List;

public final class Strings {

    private Strings() {
    }

    public static List<String> splitByRow(String s) {
        return s.lines().toList();
    }

    public static String firstRow(String s) {
        return s.lines().findFirst().orElseThrow();
    }

    public static int countUppercaseCharacters(String s) {
        return (int) Chars.asList(s.toCharArray()).stream().filter(Character::isUpperCase).count();
    }

}
