package feta.operations;

import feta.network.Network;
import feta.objectmodels.ObjectModel;

public class Clique extends Operation {

    String[] members_;

    public Clique(int cliqueSize_) {
        members_= new String[cliqueSize_];
    }

    public void build(Network net) {
        System.err.println("You haven't written this part yet!");
    }

    public void fill(Network net, ObjectModel om) {};
}