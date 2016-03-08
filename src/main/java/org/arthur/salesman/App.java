package org.arthur.salesman;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.runner.RecommendationCalculator;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.07
 */
public class App {

    private static Properties props = new Properties();

    public static void main(String... args) throws Exception {
        if (args.length > 1 && "--run".equals(args[0])) {
            props.load(new FileReader(args[1]));
            execute();
        } else {
            printHelp();
        }
    }

    private static void execute() throws Exception {
        String app = props.getProperty("app");

        if (app == null) {
            System.err.println("You must specify the app on your config file");
        } else {
            if ("usercf".equals(app)) {
                runUserCf();
            }
        }
    }

    private static void runUserCf() throws Exception {
        String citations = props.getProperty("citations.path");
        String similarity = props.getProperty("similarity.path");
        String means = props.getProperty("means.path");
        String target = props.getProperty("target.path");

        int coreThreads = Integer.parseInt(props.getProperty("core.size"));
        int maxThreads = Integer.parseInt(props.getProperty("max.size"));

        if (StringUtils.isNoneBlank(citations, similarity, means, target)) {
            System.out.println("Configurations are: " +
                    join("\n\t", citations, similarity, means, target, coreThreads, maxThreads, isDebug())
            );
            RecommendationCalculator rc = new RecommendationCalculator(isDebug(), coreThreads, maxThreads);
            rc.execute(citations, similarity, means, target);
        } else {
            System.err.println("To run usercf you need to specify citations.path, similarity.path, " +
                    "means.path and target.path");
        }
    }

    private static String join(String separator, Object... words) {
        String joined = "";
        for (Object word : words) {
            joined += (word.toString() + separator);
        }
        return joined.substring(0, joined.length() - separator.length());
    }

    private static void printHelp() {
        System.out.println("To run it you must query\n * java -jar salesman.java {option}");
        System.out.println("----------");
        System.out.println("\t\t--help\tto get the same instructions as this");
        System.out.println("\t\t--run\tpath to configuration file");
    }

    private static boolean isDebug() {
        String env = System.getenv("SALESMAN_DEBUG");
        String conf = props.getProperty("debug", "false");

        return StringUtils.isNotBlank(env) ? env.equals("true") : "true".equals(conf);
    }
}
