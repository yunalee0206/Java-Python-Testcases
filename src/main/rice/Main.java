package main.rice;

import main.rice.basegen.BaseSetGenerator;
import main.rice.concisegen.ConciseSetGenerator;
import main.rice.parse.ConfigFile;
import main.rice.parse.ConfigFileParser;
import main.rice.parse.InvalidConfigException;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import main.rice.test.Tester;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Main class that contains the entry point for the test case generation process
 * Reads input configuration files, generates test cases, and
 * prints a concise set of test cases
 */
public class Main {

    /**
     * Main method that generates test cases using input arguments;
     * Prints the concise test set
     *
     * @param args string paths to files
     * @throws IOException            If there is an error reading the input files
     * @throws InvalidConfigException If the configuration file is invalid
     * @throws InterruptedException   If the execution is interrupted
     */
    public static void main(String[] args) throws IOException, InvalidConfigException, InterruptedException {
        Set<TestCase> conciseTestCases = generateTests(args);
        int index = 1;
        for (TestCase testCase : conciseTestCases) {
            System.out.println("Testcase " + index + " :");
            System.out.println(testCase.getArgs());
            index++;
        }
    }

    /**
     * Generates concise test set; helper function for main();
     * performs end-to-end test case generation
     *
     * @param args arguments
     * @return A concise test set
     * @throws IOException            If there is an error reading the input files.
     * @throws InvalidConfigException If the configuration file is invalid.
     * @throws InterruptedException   If the execution is interrupted.
     */
    public static Set<TestCase> generateTests(String[] args) throws IOException, InvalidConfigException, InterruptedException {
        String configFilePath = args[0];
        String buggyFilePath = args[2];
        String referenceFilePath = args[1];


        ConfigFile config = ConfigFileParser.parse(ConfigFileParser.readFile(configFilePath));

        BaseSetGenerator baseGenerator = new BaseSetGenerator(config.getNodes(), config.getNumRand());
        Tester testerObj = new Tester(config.getFuncName(), referenceFilePath, buggyFilePath, baseGenerator.genBaseSet());

        testerObj.computeExpectedResults();
        return ConciseSetGenerator.setCover(testerObj.runTests());
    }
}
