package org.arthur.salesman;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.runner.UserBasedCalculator;

import java.io.FileReader;
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
                UserBasedCalculator.getCalculator(props, isDebug()).execute();
            }
        }
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
