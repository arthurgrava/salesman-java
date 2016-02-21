package org.arthur.salesman;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.runner.CoauthorsCalculator;
import org.arthur.salesman.runner.SimilarityComparator;

import java.io.IOException;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Main {

    public static void main(String... args) throws Exception {
        if (args.length > 0) {
            String first = args[0];

            if ("--help".equalsIgnoreCase(first) || "-h".equalsIgnoreCase(first)) {
                System.out.println("java -jar file.jar {application} {parameters}");
            } else if ("similarity".equals(first.toLowerCase())) {
                runSimilarityApp(args);
            } else if ("coauthors".equals(first.toLowerCase())) {
                runCoauthorsApp(args);
            } else {
                System.out.println("java -jar file.jar {application} {parameters}");
            }
        } else {
            System.out.println("java -jar file.jar {application} {parameters}");
        }
    }

    private static void runCoauthorsApp(String... args) throws IOException {
        if (args.length < 5) {
            System.out.println(
                "Missing parameters, to run properly please input:\n-----\n\t" +
                "java -jar file.jar coauthors {publicationsPath} {nAuthors} {nArticles} {coauthorsPath}"
            );
            System.exit(2);
        }

        String publicationsPath = args[1];
        int nAuthors = Integer.parseInt(args[2]);
        int nArticles = Integer.parseInt(args[3]);
        String coauthorsPath = args[4];
        String separator = ",";
        boolean debug = isDebugMode();

        CoauthorsCalculator cc = new CoauthorsCalculator(
                publicationsPath, nAuthors, nArticles, coauthorsPath, separator, debug
        );
        cc.run();
    }

    private static void runSimilarityApp(String... args) throws IOException {
        if (args.length < 6) {
            System.out.println(
                "Missing parameters, to run properly please input:\n-----\n\t" +
                "java -jar file.jar similarity {filePath} {targetPath} {coreSize} {maxSize} {topK}"
            );
            System.exit(2);
        }

        String filePath = args[1];
        String targetPath = args[2];
        int coreSize = Integer.parseInt(args[3]);
        int maxSize = Integer.parseInt(args[4]);
        int topK = Integer.parseInt(args[5]);
        boolean debug = isDebugMode();

        SimilarityComparator sc = new SimilarityComparator(coreSize, maxSize, debug);
        sc.execute(filePath, targetPath, topK);
    }

    private static boolean isDebugMode() {
        String debugging = System.getenv("SALESMAN_DEGUB");

        return StringUtils.isNotBlank(debugging) && debugging.equals("true");
    }

}
