package feta;

import java.io.*;

public class Network {

    /** Network properties */
    public int noNodes_ = 0;
    public int noLinks_ = 0;
    public int[] inDegreeDistrib_;
    public int[] outDegreeDistrib_;
    // Size of degree distribution array
    public int degArraySize_ = 1000;

    /** Network statistics */
    private int[] triCount_;
    private int singletonInCount_;
    private int doubletonInCount_;
    private int singletonOutCount_;
    private int doubletonOutCount_;
    private int maxInDegree_;
    private int maxOutDegree;
    private double clusterCoeff_;
    private boolean measureDegDist_;
    private boolean trackDegreeDistrib_;

    /** Network options */
    // Multiple links a->b allowed
    public boolean complexNetwork_;
    // Is network directed?
    public boolean directedNetwork_;
    // Get rid of duplicated links?
    public boolean ignoreDuplicates_;
    // Ignore tadpoles?
    public boolean ignoreSelfLinks_;
    // Interval between measurements
    private int interval_;

    public Network(FetaOptions opt) {
        complexNetwork_= opt.complexNetwork_;
        directedNetwork_= opt.directedNetwork_;
        ignoreDuplicates_= opt.ignoreDuplicates_;
        ignoreSelfLinks_= opt.ignoreSelfLinks_;
        measureDegDist_= opt.measureDegDist_;
    }

    private void init() {
        noNodes_= 0;
        noLinks_= 0;
        trackDegreeDistrib_= false;
    }

}