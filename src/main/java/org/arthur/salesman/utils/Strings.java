package org.arthur.salesman.utils;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.07
 */
public class Strings {

    public static String join(String separator, Object... words) {
        String joined = "";
        for (Object word : words) {
            joined += (word.toString() + separator);
        }
        return joined.substring(0, joined.length() - separator.length());
    }

}
