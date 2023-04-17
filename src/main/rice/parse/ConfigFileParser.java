package main.rice.parse;


import main.rice.node.*;
import org.json.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * This is the class for parsing configuration files in JSON format and converting them into ConfigFile objects
 */
public class ConfigFileParser {

    /**
     * Reads and returns the contents of the file located at the input filepath;
     * throws an IOException if the file does not exist or cannot be read
     *
     * @param filePath The file path
     * @return The contents of the file as a String
     * @throws IOException If there is an error reading the file
     */
    public static String readFile(String filePath) throws IOException {
        return Files.readString(Paths.get(filePath));
    }

    /**
     * Parses the contents of a configuration file in JSON format into a ConfigFile object
     *
     * @param contents The contents of the configuration file
     * @return The parsed ConfigFile object
     * @throws InvalidConfigException If there is an error parsing the configuration file
     */
    public static ConfigFile parse(String contents) throws InvalidConfigException {
        try {
            String fileContent = readFile(contents);
            return convertToConfigFile(fileContent);
        } catch (IOException e) {
            throw new InvalidConfigException(e.getMessage());
        }
    }

    /**
     * Converts the contents of a configuration file in JSON format into a ConfigFile object
     *
     * @param jsonString The contents of the configuration file as a JSON string
     * @return The parsed ConfigFile object
     */
    private static ConfigFile convertToConfigFile(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        String funcName = jsonObject.getString("fname");
        JSONArray nodesArray = jsonObject.getJSONArray("nodes");
        int numRand = jsonObject.getInt("num Rand");

        List<APyNode<?>> nodes = new ArrayList<>();

        for (int i = 0; i < nodesArray.length(); i++) {
            JSONObject nodeObject = nodesArray.getJSONObject(i);
            String types = nodeObject.getString("types");

            List<String> exDomain = stringArrayToList(nodeObject.getJSONArray("exhaustive Domain"));
            List<String> ranDomain = stringArrayToList(nodeObject.getJSONArray("random Domain"));

            nodes.add(createSimpleNode(types, exDomain, ranDomain));
        }

        return new ConfigFile(funcName, nodes, numRand);
    }

    /**
     * Converts a JSONArray of strings into a List of strings
     *
     * @param jsonArray The JSONArray to be converted
     * @return The List of strings
     */
    private static List<String> stringArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    /**
     * Removes whitespaces from a string
     *
     * @param input The input string
     * @return The string with whitespaces removed
     */
    private static String removeWhiteSpace(String input) {
        return input.replaceAll("\\s", "");
    }

    /**
     * Parses a string into a List of integers
     *
     * @param domain The string to be parsed
     * @return The List of integers
     */
    private static List<Integer> parseIntDomain(String domain) {
        String[] parts = removeWhiteSpace(domain).split(",");
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }

    /**
     * Parses a string into a List of booleans
     *
     * @param domain The string to be parsed
     * @return The List of booleans
     */
    private static List<Integer> parseBoolDomain(String domain) throws InvalidConfigException {
        List<Integer> result = parseIntDomain(domain);
        for (Integer part : result) {
            if (!part.equals(0) && !part.equals(1)) {
                throw new InvalidConfigException("Invalid Boolean; input domain contains other than 0 or 1");
            }

        }
        return result;
    }

    /**
     * Parses a string into a List of Numbers
     *
     * @param domain The string to be parsed
     * @return The List of Numbers
     */
    private static List<Number> parseNumberDomain(String domain) {
        String[] parts = removeWhiteSpace(domain).split(",");
        List<Number> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Double.parseDouble(part));
        }
        return result;
    }

    /**
     * Parses a string into a List of floats
     *
     * @param domain The string to be parsed
     * @return The List of floats
     */
    private static List<Number> parseFloatDomain(String domain) {
        String[] parts = removeWhiteSpace(domain).split(",");
        List<Number> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Float.parseFloat(part));
        }
        return result;
    }

    private static List<Integer> parseIterableDomain(String domain) throws InvalidConfigException {
        List<Integer> result = parseIntDomain(domain);
        for (Integer part : result) {
            if (part < 0) {
                throw new InvalidConfigException("Invalid Iterable Objects; input domain contains negative integers");
            }
        }
    }
    /**
     * Creates an APyNode object based on the given inputs
     *
     * @param types The type of APyNode to create
     * @param exDomain The exhaustive domain
     * @param ranDomain The random domain
     * @return The created APyNode object
     */
    private static APyNode<?> createSimpleNode(String types, List<String> exDomain, List<String> ranDomain) throws InvalidConfigException{
        APyNode<?> node;

        switch (types) {
            case "int":
                node = new PyIntNode();
                node.setExDomain(parseIntDomain(exDomain.get(0)));
//                        (types, parseIntDomain(exDomain.get(0)), parseIntDomain(ranDomain.get(0)));
                break;
            case "bool":
                node = new PyBoolNode();
                node.setExDomain(parseBoolDomain(exDomain.get(0)));
                break;

            case "float":
                node = new PyFloatNode();
                node.setExDomain(parseFloatDomain(exDomain.get(0)));
                break;
            default:  throw new InvalidConfigException("Not The Simple Types");
        }
        return node;
    }

    private static APyNode<?> createIterableNode(String types, List<String> exDomain, List<String> ranDomain){
        APyNode<?> node;
        switch (types)
    }
}
