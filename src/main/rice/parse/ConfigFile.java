package main.rice.parse;

import main.rice.node.APyNode;

import java.util.List;

public class ConfigFile {
    private String funcName;
    private List<APyNode<?>> nodes;
    private int numRand;

    // Constructor
    public ConfigFile(String funcName, List<APyNode<?>> nodes, int numRand) {
        this.funcName = funcName;
        this.nodes = nodes;
        this.numRand = numRand;
    }

    // Getter for funcName
    public String getFuncName() {
        return funcName;
    }

    // Getter for nodes
    public List<APyNode<?>> getNodes() {
        return nodes;
    }

    // Getter for numRand
    public int getNumRand() {
        return numRand;
    }
}
