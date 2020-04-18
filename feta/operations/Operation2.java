package feta.operations;

import feta.Methods;
import feta.network.Network;
import feta.objectmodels.MixedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public abstract class Operation2 {

    public ArrayList<int[]> nodeChoices_;
    private long time_;
    private boolean orderedData_;

    /** updates network with the new nodes and links that occur in this operation
     alternative is for this to happen in the Network interface */
    public abstract void bufferLinks(Network net);

    /** Implemented in Operation, this is for selecting old nodes when growing network */
    public abstract void chooseNodes(Network net, MixedModel obm);

    /** Extracts operation into a node choices arraylist */
    public abstract void setNodeChoices(boolean orderedData);

    /** Get time */
    public long getTime() {return time_;}

    /** Gets loglikelihood (start to finish) */
    public double calcLogLike(MixedModel obm, Network net) {
        setNodeChoices(orderedData_);
        filterNodeChoices();
        ArrayList<int[]> nodeOrders = generateOrdersFromOperation();
        return logLikeFromList(nodeOrders,obm,net);
    }

    /** Returns the likelihood ratio against random model of an ordered set of nodes nodeSet, given that alreadyChosen
     * have been chosen. These can be added up to get likelihoods for unordered sets of nodes */
    private double likelihoodRatioOrderedSet(Network net, MixedModel obm, int[] nodeSet, int[] alreadyChosen){
        obm.updateNormalisation(net, alreadyChosen);
        if (nodeSet.length == 1) {
            return obm.calcProbability(net,nodeSet[0]) * (net.noNodes_ - alreadyChosen.length);
        }
        else {
            int node = nodeSet[0];
            int [] newNodeSet = new int[nodeSet.length - 1];
            System.arraycopy(nodeSet, 1, newNodeSet,0, newNodeSet.length);
            int [] newAlreadyChosen = new int[alreadyChosen.length + 1];
            System.arraycopy(alreadyChosen,0,newAlreadyChosen,0,alreadyChosen.length);
            newAlreadyChosen[alreadyChosen.length]=node;
            double prob = obm.calcProbability(net,node) * (net.noNodes_-alreadyChosen.length);
            return prob * likelihoodRatioOrderedSet(net,obm,newNodeSet,newAlreadyChosen);
        }
    }

    /** Gets Likelihood of operation from possible node orders */
    private double logLikeFromList (ArrayList<int[]> orders, MixedModel obm, Network net) {
        int noOrders = orders.size();
        double sum = 0.0;
        for (int[] order: orders) {
            sum+= likelihoodRatioOrderedSet(net, obm, order, new int[0]);
        }
        return Math.log(sum) - Math.log(noOrders);
    }

    /** Helper methods */

    /** Concatenates two int arrays */
    public static int[] concatenate(int[] first, int[] second) {
        int[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        return both;
    }

    /** Generates possible ways of concatenaing items from list1 with items from list2 */
    private static ArrayList<int[]> combineOrders(ArrayList<int[]> list1, ArrayList<int[]> list2) {
        ArrayList<int[]> combined = new ArrayList<int[]>();
        for (int [] l1: list1) {
            for (int [] l2: list2) {
                combined.add(concatenate(l1, l2));
            }
        }
        return combined;
    }

    /** Generates all possible choice sequences of nodes from choice arraylist */
    private static ArrayList<int[]> generatePossibleSequences(ArrayList<ArrayList<int[]>> listOfLists) {
        ArrayList<int[]> finalList = new ArrayList<int[]>();
        for (int i = 0; i< listOfLists.size(); i++) {
            finalList = combineOrders(finalList, listOfLists.get(i));
        }
        return finalList;
    }

    private ArrayList<int[]> generateOrdersFromOperation() {
        ArrayList<ArrayList<int[]>> listOfLists = new ArrayList<ArrayList<int[]>>();
        for (int[] arr: nodeChoices_) {
            if (arr.length <= 5) {
                listOfLists.add(Methods.generatePerms(0,arr,new ArrayList<int[]>()));
            }
            else {
                listOfLists.add(Methods.generateRandomShuffles(arr, 50));
            }
        }
        return generatePossibleSequences(listOfLists);
    }

    public void filterNodeChoices() {
        ArrayList<int[]> newChoices = new ArrayList<int[]>();
        for (int[] nodeSet: nodeChoices_) {
            int[] copy = Methods.removeNegativeNumbers(nodeSet);
            newChoices.add(copy);
        }
        nodeChoices_=newChoices;
    }

}
