package org.arthur.salesman;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.runner.CoauthorsCalculator;
import org.arthur.salesman.runner.CoauthorsTimeContextCalculator;
import org.arthur.salesman.runner.EvaluationCalculator;
import org.arthur.salesman.runner.EvaluationThreeCalculator;
import org.arthur.salesman.runner.EvaluationTwoCalculator;
import org.arthur.salesman.runner.MergeCalculator;
import org.arthur.salesman.runner.SRSRunner;
import org.arthur.salesman.runner.SRSStarRunner;
import org.arthur.salesman.runner.TSRSRunner;
import org.arthur.salesman.runner.TrustNetworkCalculator;
import org.arthur.salesman.runner.UsefulnessCalculator;
import org.arthur.salesman.runner.UserBasedCalculator;

import java.io.FileReader;
import java.util.Properties;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.07
 */
public class App {

    private static Properties props = new Properties();
    private static Logger LOG = LogManager.getLogger(App.class);

    public static void main(String... args) throws Exception {
        long start = System.currentTimeMillis();
        try {

            if (args.length > 1 && "--run".equals(args[0])) {
                props.load(new FileReader(args[1]));
                execute();
            } else {
                printHelp();
            }

        } catch (Exception e) {
            LOG.error("An error occurred, please check the log file", e);
        }
        long end = System.currentTimeMillis();

        System.out.println("Took " + (end - start) + "ms to run the program");
        LOG.info("Took " + (end - start) + "ms to run the program");
    }

    private static void execute() throws Exception {
        String app = props.getProperty("app");

        if (app == null) {
            System.err.println("You must specify the app on your config file");
        } else {
            int coreThreads = Integer.parseInt(props.getProperty("core.size", "5"));
            int maxThreads = Integer.parseInt(props.getProperty("max.size", "30"));

            if ("usercf".equals(app)) {
                UserBasedCalculator.getCalculator(props, isDebug()).execute();
            } else if ("evaluation".equals(app)) {
                EvaluationCalculator.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("coauthors".equals(app)) {
                CoauthorsCalculator.getCalculator(props).execute();
            }  else if ("time_context_coauthors".equals(app)) {
                CoauthorsTimeContextCalculator.getCalculator(props).execute();
            } else if ("trustnet".equals(app)) {
                TrustNetworkCalculator.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("evaluation_2".equals(app)) {
                EvaluationTwoCalculator.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("merge".equals(app)) {
                MergeCalculator.getCalculator(props).execute();
            } else if ("label_recommendations".equals(app)) {
                UsefulnessCalculator.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("evaluation_3".equals(app)) {
                EvaluationThreeCalculator.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("srs".equals(app)) {
                SRSRunner.getCalculator(props, true).execute(coreThreads, maxThreads);
            } else if ("srs_star".equals(app)) {
                SRSStarRunner.getCalculator(props).execute(coreThreads, maxThreads);
            } else if ("tsrs".equals(app)) {
                TSRSRunner.getCalculator(props).execute(coreThreads, maxThreads);
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
