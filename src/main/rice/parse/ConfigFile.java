package main.rice.parse;

import main.rice.node.APyNode;

import java.util.List;

/**
 * Represents a configuration file with a function name, a list of APyNodes, and a number of random values
 */
public class ConfigFile {
    private String funcName;
    private List<APyNode<?>> nodes;
    private int numRand;

    /**
     * Constructor for ConfigFile
     *
     * @param funcName The name of the function under test
     * @param nodes The list of PyNodes that will be used to generate TestCases for the function under test
     * @param numRand The number of random test cases to be generated
     */
    public ConfigFile(String funcName, List<APyNode<?>> nodes, int numRand) {
        this.funcName = funcName;
        this.nodes = nodes;
        this.numRand = numRand;
    }

    /**
     * Returns the name of the function under test
     @return The function name
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * Returns the List of PyNodes that will be used to generate TestCases for the function under test
     * @return The list of APyNodes
     */
    public List<APyNode<?>> getNodes() {
        return nodes;
    }

    /**
     * Returns the number of random test cases to be generated.
     * @return The number of random test cases
     */
    public int getNumRand() {
        return numRand;
    }
}
