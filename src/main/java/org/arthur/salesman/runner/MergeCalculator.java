package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.hybrid.PickerMerge;
import org.arthur.salesman.reader.RatingsReader;
import org.arthur.salesman.utils.Strings;

import java.io.IOException;
import java.util.Properties;

/**
 * Choose the merge mode and returns it to be executed
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.26
 */
public class MergeCalculator {

    private static final String PICKER = "picker";

    private String mode;
    private String algorithmA;
    private String algorithmB;
    private String target;

    private MergeCalculator(String mode, String algorithmA, String algorithmB, String target) {
        this.mode = mode;
        this.algorithmA = algorithmA;
        this.algorithmB = algorithmB;
        this.target = target;
    }

    public static MergeCalculator getCalculator(Properties props) throws IOException {
        String mode = props.getProperty("app.mode");
        String algorithmA = props.getProperty("main.algorithm.path");
        String algorithmB = props.getProperty("secondary.algorithm.path");
        String target = props.getProperty("target.path");

        if (StringUtils.isNoneBlank(mode, algorithmA, algorithmB, target)) {
            System.out.println(Strings.join("\n\t", "Configurations are:", mode, algorithmA, algorithmB, target));

            return new MergeCalculator(mode, algorithmA, algorithmB, target);
        } else {
            System.out.println("Your configuration file must have:");
            System.out.println("  * app.mode\t\t--  Which merge app will be used");
            System.out.println("  * main.algorithm.path\t--  The preferable algorithm");
            System.out.println("  * secondary.algorithm.path\t--  The mixin algorithm");
            System.out.println("  * target.path\t\t--  Target of new recommendations\n\n");

            throw new IOException("Missing properties parameters, check your config file");
        }
    }

    public void execute() throws IOException {
        if (PICKER.equals(mode)) {
            new PickerMerge(
                    RatingsReader.readFile(algorithmA),
                    RatingsReader.readFile(algorithmB),
                    target
            ).doMagic();
            return;
        }

        throw new IOException("The specified {app.mode} does not exists -- " + mode);
    }

}
