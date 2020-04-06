package feta.network;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class DirectedNetwork extends Network {

    /** Hashmaps containing info about node inlinks and outlinks */
    private TreeMap<Integer, ArrayList<Integer>> inLinks_;
    public TreeMap<Integer, ArrayList<Integer>> outLinks_;

    private int[] inDegreeDist_;
    private int[] outDegreeDist_;
    private int inDegArraySize_ = 1000;
    private int outDegArraySize_ = 1000;
    private int maxNodeNo_=1000;

    /** Doubles relating to measurements */
    private double avgInDeg_;
    private double avgOutDeg_;
    private double meanInDegSq_;
    private double meanOutDegSq_;
    private double InInAssort_;
    private double InOutAssort_;
    private double OutInAssort_;
    private double OutOutAssort_;
    public int maxInDeg_;
    public int maxOutDeg_;
    private BufferedWriter brIn_=null;
    private BufferedWriter brOut_=null;

    public DirectedNetwork() {
        inLinks_ = new TreeMap<Integer, ArrayList<Integer>>();
        outLinks_ = new TreeMap<Integer, ArrayList<Integer>>();

        inDegreeDist_= new int[inDegArraySize_];
        outDegreeDist_= new int[outDegArraySize_];

        avgInDeg_= 0.0;
        avgOutDeg_= 0.0;
        meanInDegSq_= 0.0;
        meanOutDegSq_= 0.0;
        InInAssort_=0.0;
        InOutAssort_=0.0;
        OutInAssort_=0.0;
        OutOutAssort_=0.0;
    }

    public void addNode(int nodeno) {
        inLinks_.put(nodeno, new ArrayList<Integer>());
        outLinks_.put(nodeno, new ArrayList<Integer>());
        incrementInDegDist(0);
        incrementOutDegDist(0);
    }

    public void addLink(int src, int dst){
        if (isLink(src,dst) && !allowDuplicates_)
            return;
        inLinks_.get(dst).add(src);
        outLinks_.get(src).add(dst);
        incrementInDegDist(getInDegree(dst));
        reduceInDegDist(getInDegree(dst)-1);
        incrementOutDegDist(getOutDegree(src));
        reduceOutDegDist(getOutDegree(src)-1);
    }

    public boolean isLink(int a, int b) {
        if (outLinks_.get(a).contains(b)) {
            return true;
        }
        return false;
    }

    public void setUpDegDistWriters(String fname) {
        int dot = fname.lastIndexOf('.');
        String inDegFile = fname.substring(0,dot)+"InDeg"+fname.substring(dot);
        String outDegFile = fname.substring(0,dot)+"OutDeg"+fname.substring(dot);

        try {
            FileWriter fwIn = new FileWriter(inDegFile);
            brIn_ = new BufferedWriter(fwIn);
            FileWriter fwOut = new FileWriter(outDegFile);
            brOut_= new BufferedWriter(fwOut);
        } catch (IOException ioe) {
            System.err.println("Could not set up degree distribution writer.");
            ioe.printStackTrace();
        }
    }

    public void writeDegDist() {
        String inString = "";
        String outString = "";
        for (int i = 0; i < inDegArraySize_; i++) {
            inString +=inDegreeDist_[i]+" ";
        }
        for (int j = 0; j < outDegArraySize_; j++) {
            outString +=outDegreeDist_[j]+" ";
        }
        try {
            brIn_.write(inString+"\n");
            brOut_.write(outString+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeWriters() throws IOException {
        brIn_.close();
        brOut_.close();
    }

    public int getInDegree(int node) {
        return inLinks_.get(node).size();
    }

    public int getOutDegree(int node) {
        return outLinks_.get(node).size();
    }

    public int getTotDegree(int node) {
        return getInDegree(node) + getOutDegree(node);
    }

    /** Increments degree distribution at appropriate index corresponding to new link */
    public void incrementInDegDist(int degree) {
        if (degree > maxInDeg_)
            maxInDeg_=degree;
        if (degree < inDegArraySize_) {
            inDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[inDegArraySize_*2];
            System.arraycopy(inDegreeDist_, 0, newDegDist, 0, inDegArraySize_);
            Arrays.fill(newDegDist, inDegArraySize_, inDegArraySize_*2, 0);
            inDegreeDist_=newDegDist;
            inDegArraySize_*=2;
            incrementInDegDist(degree);
        }
    }

    /** Increments degree distribution at appropriate index corresponding to new link */
    public void incrementOutDegDist(int degree) {
        if (degree > maxOutDeg_)
            maxOutDeg_=degree;
        if (degree < outDegArraySize_) {
            outDegreeDist_[degree]++;
        } else {
            int[] newDegDist = new int[outDegArraySize_*2];
            System.arraycopy(outDegreeDist_, 0, newDegDist, 0, outDegArraySize_);
            Arrays.fill(newDegDist, outDegArraySize_, outDegArraySize_*2, 0);
            outDegreeDist_=newDegDist;
            outDegArraySize_*=2;
            incrementOutDegDist(degree);
        }
    }

    public void reduceInDegDist(int degree) {
        inDegreeDist_[degree]--;
    }

    public void reduceOutDegDist(int degree) {
        outDegreeDist_[degree]--;
    }



    public void removeLinks(String nodename) {
        // Removes all links from or to a node. WHY WOULD YOU DO THIS???
    }

    public String measureToString() {
        return noNodes_+" "+noLinks_+" "+avgInDeg_+" "+avgOutDeg_+" "+maxInDeg_+" "+maxOutDeg_+" "+meanInDegSq_+" "+
                meanOutDegSq_+" "+InInAssort_+" "+InOutAssort_+" "+OutInAssort_+" "+OutOutAssort_;
    }

    public String degreeVectorToString() {
        String degs = "";
        for (int i = 0; i < noNodes_; i++) {
            degs +=getInDegree(i)+" ";
        }
        return degs+"\n";
    }

    public void addNewLink(String src, String dst, long time) {
        linksToBuild_.add(new DirectedLink(src, dst, time));
    }

}
