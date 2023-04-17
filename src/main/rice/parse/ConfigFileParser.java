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
        JSONObject jsonObject;
        try {jsonObject = new JSONObject(contents);
        }
        catch (JSONException c) {
            throw new InvalidConfigException("jsonObject is not a valid JSONObject");
        }

        Set<String> keySet = new HashSet<>(Set.of("fname", "num random", "types", "exhaustive domain", "random domain"));
        if (!(jsonObject.keySet().equals(keySet))) {
            throw new InvalidConfigException("jsonObject does not have valid keys");
        }

        String funcName;
        try {funcName = jsonObject.getString("fname");}
        catch (JSONException c) {
            throw new InvalidConfigException("fname is not a string");
        }

        int numRand;
        try {numRand = jsonObject.getInt("num random");}
        catch (JSONException c) {
            throw new InvalidConfigException("numRand is not an Integer");
        };

//        JSONArray nodesArray = jsonObject.getJSONArray("nodes");


        JSONArray types = genJSONArray("types", jsonObject);
        JSONArray exDomain = genJSONArray("exhaustive domain", jsonObject);
        JSONArray ranDomain = genJSONArray("random domain", jsonObject);

        if (!(types.length() == exDomain.length() && exDomain.length() == ranDomain.length() && types.length() == ranDomain.length())) {
            throw new InvalidConfigException("The length should be the same");
        }
        List<APyNode<?>> nodes = new ArrayList<>();
        for (int i = 0; i < types.length(); i++) {
            String typeIndex = (String) types.get(i);
            String exDom = (String) exDomain.get(i);
            String ranDom = (String) ranDomain.get(i);

            nodes.add(createNode(typeIndex.strip(), exDom.strip(), ranDom.strip()));
        }

        return new ConfigFile(funcName, nodes, numRand);
    }


    /**
     * Generates a JSONArray from a JSONObject using the specified key
     *
     * @param key The key for the JSONArray
     * @param jObj The JSONObject from which to generate the JSONArray
     * @return The generated JSONArray
     * @throws InvalidConfigException If there is an error generating the JSONArray
     */
    private static JSONArray genJSONArray(String key, JSONObject jObj) throws InvalidConfigException{
        try {
            JSONArray jArray = (JSONArray) jObj.get(key);
            return jArray;
        }
        catch (ClassCastException c) {
            throw new InvalidConfigException("Unable to convert to JSONArray");
        }
    }

//    /**
//     * Converts a JSONArray of strings into a List of strings
//     *
//     * @param jsonArray The JSONArray to be converted
//     * @return The List of strings
//     */
//    private static List<String> stringArrayToList(JSONArray jsonArray) {
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            list.add(jsonArray.getString(i));
//        }
//        return list;
//    }

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
    private static List<Integer> parseIntDomain(String domain) throws InvalidConfigException{
        try {
            if (domain.contains("~")) {
                int n = domain.indexOf("~");
                int startNum = Integer.parseInt(domain.substring(0, n).strip());
                int endNum = Integer.parseInt(domain.substring(n+1).strip());
                if (startNum > endNum ){
                    throw new InvalidConfigException("Invalid Domain");
                }
                List<Integer> range = new ArrayList<>();
                for (int i = startNum; i <= endNum; i++) {
                    range.add(i);
                }
                return range;
            }

        } catch (NumberFormatException e) {
            throw new InvalidConfigException("Failed to parse; invalid range");
        };

        String[] parts = removeWhiteSpace(domain).split(",");
        List<Integer> result = new ArrayList<>();
        try {
            for (String part : parts) {
                result.add(Integer.parseInt(part));
            }
        } catch (NumberFormatException n) {
            throw new InvalidConfigException("Not an Integer");
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
    private static List<Number> parseFloatDomain(String domain) throws InvalidConfigException {
        try {
            if (domain.contains("~")) {
                int n = domain.indexOf("~");
                int startNum = Integer.parseInt(domain.substring(0, n).strip());
                int endNum = Integer.parseInt(domain.substring(n+1).strip());
                if (startNum > endNum ){
                    throw new InvalidConfigException("Invalid Domain");
                }
                List<Integer> range = new ArrayList<>();
                for (int i = startNum; i <= endNum; i++) {
                    range.add(i);
                }

                List<Number> fltRange = new ArrayList<>();
                for (Number num: range) {
                    fltRange.add(num.doubleValue());
                }

                return fltRange;
            }

        } catch (NumberFormatException e) {
            throw new InvalidConfigException("Failed to parse; invalid range");
        };


        String[] parts = removeWhiteSpace(domain).split(",");
        List<Number> result = new ArrayList<>();
        try {
            for (String part : parts) {
                result.add(Float.parseFloat(part));
            }
            return result;
        } catch (NumberFormatException n) {
            throw new InvalidConfigException("Not a Float");
        }
    }

    /**
     * Parses the domain string for iterable domain values.
     *
     * @param domain The domain string to parse
     * @return A list of iterable domain values
     * @throws InvalidConfigException If the domain string is invalid
     */
    private static List<Integer> parseIterableDomain(String domain) throws InvalidConfigException {
        List<Integer> result = parseIntDomain(domain);
        for (Integer part : result) {
            if (part < 0) {
                throw new InvalidConfigException("Invalid Iterable Objects; input domain contains negative integers");
            }
        }
        return result;
    }

    /**
     * Creates an APyNode object based on the given inputs
     *
     * @param types The type of APyNode to create
     * @param exDomain The exhaustive domain
     * @param ranDomain The random domain
     * @return The created APyNode object
     */
    private static APyNode<?> createNode(String types, String exDomain, String ranDomain) throws InvalidConfigException{
        APyNode<?> node;

        switch (types.strip()) {
            case "int":
                node = new PyIntNode();
                node.setExDomain(parseIntDomain(exDomain.toString()));
                node.setRanDomain(parseIntDomain(ranDomain));
                parenthesisCheckSimple(types, exDomain, ranDomain);
                break;
            case "bool":
                node = new PyBoolNode();
                node.setExDomain(parseBoolDomain(exDomain));
                node.setRanDomain(parseBoolDomain(ranDomain));
                parenthesisCheckSimple(types, exDomain, ranDomain);
                break;

            case "float":
                node = new PyFloatNode();
                node.setExDomain(parseFloatDomain(exDomain));
                node.setRanDomain(parseFloatDomain(ranDomain));
                parenthesisCheckSimple(types, exDomain, ranDomain);
                break;
            default:  return createIterableNode(types, exDomain, ranDomain);
        }
        return node;
    }

    /**
     * Performs parenthesis and colon checks for simple types of Nodes
     *
     * @param types The simple type of APyNode
     * @param exDomain The exhaustive domain
     * @param ranDomain The random domain
     * @throws InvalidConfigException If Node contains invalid characters
     */
    private static void parenthesisCheckSimple(String types, String exDomain, String ranDomain) throws InvalidConfigException{
        if (types.contains("(") || exDomain.contains("(") || ranDomain.contains("(")) {
            throw new InvalidConfigException("Simple Nodes should not contain opening parenthesis");
        }
        if (types.contains(":") || exDomain.contains(":") || ranDomain.contains(":")) {
            throw new InvalidConfigException("Simple Nodes should not contain colon");
        }
    }

    /**
     * Creates an iterable APyNode object based on the given inputs.
     *
     * @param types The type of iterable APyNode to create
     * @param exDomain The exhaustive domain
     * @param ranDomain The random domain
     * @return The created iterable APyNode object
     * @throws InvalidConfigException If the inputs are invalid
     */
    private static APyNode<?> createIterableNode(String types, String exDomain, String ranDomain)
            throws InvalidConfigException {
        APyNode<?> node;
        APyNode<?> child = createNode(types.substring(types.indexOf("(")+1).strip(),
                exDomain.substring(exDomain.indexOf("(")+1).strip(),
                ranDomain.substring(ranDomain.indexOf("(")+1).strip());


        if (types.startsWith("list")) {
            node = new PyListNode<>(child);
            parenthesisCheckIterable(types, exDomain, ranDomain);

        }
        else if (types.startsWith("set")) {
            node = new PySetNode<>(child);
            parenthesisCheckIterable(types, exDomain, ranDomain);

        }
        else if (types.startsWith("tuple")) {
            node = new PyTupleNode<>(child);
            parenthesisCheckIterable(types, exDomain, ranDomain);

        }
        else if (types.startsWith("dict")) {
            if (!(types.contains("(") || exDomain.contains("(") || ranDomain.contains("("))) {
                throw new InvalidConfigException("Dict nodes should contain opening parenthesis");
            }
            if (!(types.contains(":") || exDomain.contains(":") || ranDomain.contains(":")) ){
                throw new InvalidConfigException("Dict Nodes should contain colon");
            }
            APyNode<?> leftChild = createNode(types.substring(types.indexOf("(")+1, types.indexOf(":")).strip(),
                    exDomain.substring(exDomain.indexOf("(")+1, types.indexOf(":")).strip(),
                    ranDomain.substring(ranDomain.indexOf("(")+1, types.indexOf(":")).strip());
            APyNode<?> rightChild = createNode(types.substring(types.indexOf(":")+1).strip(),
                    exDomain.substring(exDomain.indexOf(":")+1).strip(),
                    ranDomain.substring(ranDomain.indexOf(":")+1).strip());
            node = new PyDictNode<>(leftChild, rightChild);
        }
        else if (types.startsWith("str")) {
            node = new PyStringNode(Set.of());
            node.setExDomain(parseIterableDomain(exDomain));
            node.setRanDomain(parseIterableDomain(ranDomain));
            parenthesisCheckSimple(types, exDomain, ranDomain);
        }

        else {throw new InvalidConfigException("Invalid Iterable Node");}

        node.setExDomain(parseIntDomain(exDomain.substring(0, exDomain.indexOf("("))));
        node.setRanDomain(parseIntDomain(ranDomain.substring(0, ranDomain.indexOf("("))));
        return node;
    }

    /**
     * Performs parenthesis and colon checks for iterable types of Nodes
     *
     * @param types The type of iterable APyNode
     * @param exDomain The exhaustive domain
     * @param ranDomain The random domain
     * @throws InvalidConfigException If Node contains invalid characters
     */
    private static void parenthesisCheckIterable(String types, String exDomain, String ranDomain) throws InvalidConfigException{
        if (!(types.contains("(") || exDomain.contains("(") || ranDomain.contains("("))) {
            throw new InvalidConfigException("Iterable Nodes should contain opening parenthesis");
        }
        if (types.contains(":") || exDomain.contains(":") || ranDomain.contains(":")) {
            throw new InvalidConfigException("Iterable Nodes (not dict) should not contain colon");
        }
    }

}
