package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.utils.Strings;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.13
 */
public class EvaluationCalculator {

    private String testRatingsPath;
    private String predictionsPath;
    private String targetPath;

    public EvaluationCalculator(String testRatingsPath, String predictionsPath, String targetPath) {
        this.testRatingsPath = testRatingsPath;
        this.predictionsPath = predictionsPath;
        this.targetPath = targetPath;
    }

    public void execute() {

    }

    public static EvaluationCalculator getCalculator(final Properties props) throws IOException {
        String testRatingsPath = props.getProperty("ratings.path");
        String predictionsPath = props.getProperty("predictions.path");
        String targetPath = props.getProperty("target.path");

        if (StringUtils.isNoneBlank(testRatingsPath, predictionsPath, targetPath)) {
            System.out.println(
                    "Configurations are:\n\t" +
                    Strings.join("\n\t", testRatingsPath, predictionsPath, targetPath)
            );

            return new EvaluationCalculator(testRatingsPath, predictionsPath, targetPath);
        } else {
            System.out.println("Your configuration file must have:");
            System.out.println("  * ratings.path\t--  Testing dataset");
            System.out.println("  * predictions.path\t--  Predicted dataset");
            System.out.println("  * target.path\t\t--  Target of evaluation result\n\n");
            throw new IOException("Missing parameters on the config file");
        }
    }
}
