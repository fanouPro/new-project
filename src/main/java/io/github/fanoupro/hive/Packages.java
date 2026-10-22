package io.github.fanoupro.hive;

import java.util.regex.Pattern;

final class Packages {

    private static final Pattern PACKAGE = Pattern.compile("[A-Za-z_$][\\w$]*(\\.[A-Za-z_$][\\w$]*)*");

    private Packages() {
    }

    static String requireValidPackage(String packageName, String what) {
        if (packageName == null || !PACKAGE.matcher(packageName).matches()) {
            throw new IllegalArgumentException("Invalid " + what + ": '" + packageName + "'");
        }
        return packageName;
    }

    static boolean isWithin(String packageName, String ancestor) {
        return packageName.equals(ancestor) || packageName.startsWith(ancestor + ".");
    }

    static boolean isStrictlyWithin(String packageName, String ancestor) {
        return packageName.startsWith(ancestor + ".");
    }

    static String relativeTo(String packageName, String ancestor) {
        return packageName.equals(ancestor) ? "" : packageName.substring(ancestor.length() + 1);
    }

    static String lastSegment(String packageName) {
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }
}
