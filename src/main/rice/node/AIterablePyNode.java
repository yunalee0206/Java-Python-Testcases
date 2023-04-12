package main.rice.node;

import main.rice.obj.AIterablePyObj;
import main.rice.obj.APyObj;
import java.util.*;

/**
 * An abstract class whose instances represent generators for iterable Python objects
 * (list, tuple, set, or string). Includes common functionality for generating
 * permutations, though some of the individual sub-classes need to do their own
 * post-processing.
 *
 * @param <OuterType> the outer type of object generated by this node; if we invoke
 *                    this.genExVals(), it will return a Set<OuterType<InnerType>>
 * @param <InnerType> the type of object generated by the leftChild node, which represents
 *                    the type of elements in this iterable object; if we invoke
 *                    this.leftChild.genExVals(), it will return a Set<InnerType>
 */
public abstract class AIterablePyNode<OuterType extends AIterablePyObj<InnerType>,
        InnerType extends APyObj<?>> extends APyNode<OuterType> {

    /**
     * Overrides the leftChild in the superclass (APyNode) with a more specific type: a
     * generator of InnerType objects.
     */
    protected APyNode<InnerType> leftChild;

    /**
     * Returns the left child node.
     *
     * @return the left child node
     */
    @Override
    public APyNode<InnerType> getLeftChild() {
        return this.leftChild;
    }

    /**
     * Generates all valid OuterType objects within the exhaustive domain; begins by
     * generating all valid elements, and then finds all permutations of them of valid
     * length (as constrained by the exhaustive domain).
     *
     * @return a set of OuterTypes comprising the exhaustive domain
     */
    public Set<OuterType> genExVals() {
        Set<InnerType> innerVals = this.leftChild.genExVals();
        return this.genPerms(innerVals);
    }

    /**
     * Generates a single valid OuterType object within the random domain; begins by
     * randomly generating a valid length (as constrained by the random domain), and then
     * randomly generates valid elements.
     *
     * @return a single OuterType object selected from the random domain
     */
    public OuterType genRandVal() {
        // Randomly select the size, n
        int length = this.ranDomainChoice().intValue();

        // Randomly select n inner values
        List<InnerType> list = new ArrayList<>();
        for (int idx = 0; idx < length; idx++) {
            InnerType childVal = this.genRandInnerVal();
            list.add(childVal);
        }
        return this.genObj(list);
    }

    /**
     * Helper function for generating one random InnerType object.
     *
     * @return a randomly-generated object of type InnerType
     */
    protected InnerType genRandInnerVal() {
        return this.leftChild.genRandVal();
    }

    /**
     * Helper function for generating an object of the correct OuterType; will be
     * overridden in the subclasses, where the OuterType is known.
     *
     * @param innerVals the elements to be contained by the generated object
     * @return an OuterType object encapsulating the innerVals
     */
    protected abstract OuterType genObj(List<InnerType> innerVals);

    /**
     * Generates all permutations within the exhaustive domain.
     *
     * @param innerVals the set of values that can be contained within the iterable being
     *                  generated
     * @return all permutations of the elements in innerVals of length up to and including
     * the max value in this.exDomain
     */
    protected Set<OuterType> genPerms(Set<InnerType> innerVals) {
        Set<OuterType> perms;

        // Try to gauge which approach will be more efficient, and go with that
        boolean exDomainIsContiguous = this.exDomainIsContiguous();
        if (exDomainIsContiguous) {
            // Make a single call that generates everything from the min length to the max length
            perms = this.genPermsHelper(this.exDomainMax(), this.exDomainMin(), innerVals, true);
        } else {
            // Make a bunch of individual calls that only generate the max length
            perms = new HashSet<>();
            for (Number length : this.exDomain) {
                perms.addAll(this.genPermsHelper(length.intValue(), 0, innerVals, false));
            }
        }

        return perms;
    }

    /**
     * Helper function for genListPerms; generates all permutations of the specified
     * innerVals of length up to and including the input size.
     *
     * @param currSize  the max size permutation to be generated
     * @param minSize   the min size permutation to keep
     * @param innerVals the set of values that can be contained within the iterable being
     *                  randomly-generated
     * @param isContig  true if we're generating all sizes from minSize to currSize; false
     *                  if we're only generating currSize
     * @return all permutations of innerVals, according to the input specifications
     */
    private Set<OuterType> genPermsHelper(int currSize, int minSize,
                                          Set<InnerType> innerVals, boolean isContig) {
        // BASE CASE: size 0 -> return a set containing only the empty list
        if (currSize == 0) {
            Set<OuterType> perms = new HashSet<>();
            perms.add(this.genObj(new ArrayList<>()));
            return perms;
        }

        // RECURSIVE CASE:
        // Get all permutations that are of length size - 1
        Set<OuterType> oneShorter = this.genPermsHelper(currSize - 1, minSize, innerVals, isContig);

        // Iterate over each shorter list, adding each possible single element to it
        Set<OuterType> perms = new HashSet<>();
        for (OuterType listObj : oneShorter) {
            // Extract the internal ArrayList representation
            List<InnerType> list = new ArrayList<>(listObj.getValue());
            int listSize = list.size();

            // If we're finding a contiguous range, we'll need to selectively keep some of
            // the smaller permutations.
            if (isContig && listSize >= minSize) {
                // Only keep around lists that are within the domain
                perms.add(listObj);
            }

            // For efficiency, let's avoid generating duplicates by only adding to the largest
            // elements of shorterPerms
            if (listSize < currSize - 1) {
                continue;
            }

            // Construct all new lists that result from adding val (a possible inner val)
            // to list
            for (InnerType val : innerVals) {
                // Make sure to clone list so as not to mutate the original
                List<InnerType> listClone = new ArrayList<>(list);
                listClone.add(val);
                OuterType obj = this.genObj(listClone);
                if (obj.getValue().size() == currSize || (isContig && obj.getValue().size() > minSize)) {
                    perms.add(obj);
                }
            }
        }
        return perms;
    }
}