/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.db.robustness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public class Merger {

    // attibutes
    private int allowed_decoys = 5; // how many decoys are needed to be found 
    private File output = new File("C:\\Users\\Sule\\Desktop\\test100db.txt"), // find overall how many targets are and their union targets
            folder = new File("C:\\Users\\Sule\\Desktop\\db_100");

    public Merger(int allowed_decoys, File output, File folder) {
        this.allowed_decoys = allowed_decoys;
        this.output = output;
        this.folder = folder;
    }

    /**
     * This method puts all targets together to calculate recall It selects
     * targets till #number of decoy is found. Then it puts them all into a list
     *
     * @param folder a folder containing list of td results
     * @param allowed_decoys how many half-decoy or decoy are allowed
     * @return
     */
    private HashSet<SelectInfo> getUnionList(File folder, int allowed_decoys) throws IOException {
        HashSet<SelectInfo> union = new HashSet<SelectInfo>();
        for (File f : folder.listFiles()) {
            if (f.getName().startsWith("result_td_target") && f.getName().endsWith(".fasta.txt")) {
                // target is written as string.. 
                ArrayList<SelectInfo> targets = getTargets(f, allowed_decoys);
                union.addAll(targets);
            }
        }
        return union;
    }

    /**
     * This method get recall value (#targets/#union)
     *
     * @param targets a list of previously selected targets
     * @param unionList a list of all targets above threshold
     * @return
     */
    private double[] getRecallInfo(ArrayList<SelectInfo> targets, HashSet<SelectInfo> unionList) {
        HashSet<SelectInfo> info = new HashSet<SelectInfo>(targets);
        int num = info.size(),
                unionSize = unionList.size();
//        for(String t:targets){
//            if(unionList.contains(t)){                
//                num++;
//            }
//        }

        double recall = (double) num / (double) unionSize;
        double[] t = {recall, num, unionSize};
        return t;
    }

    public ArrayList<SelectInfo> getTargets(File f, int allowed_decoys) throws FileNotFoundException, IOException {
//        HashSet<String> selected_targets = new HashSet<String>();
        ArrayList<SelectInfo> all_targets = new ArrayList<SelectInfo>();
        ArrayList<SelectInfo> selected_targets = new ArrayList<SelectInfo>();
        // read a given file
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spectrum")) {
                String[] split = line.split("\t");
                double score = Double.parseDouble(split[9]);
                String proteinA = split[10],
                        proteinB = split[11],
                        peptideA = split[12],
                        peptideB = split[13],
                        modificationA = split[14],
                        modificationB = split[15],
                        status = split[28];
                SelectInfo obj = new SelectInfo(proteinA, proteinB, peptideA, peptideB, modificationA, modificationB, status, score);
                all_targets.add(obj);
            }
        }
        // now sort them based on their scores and collect till #allowed_decoys is reached
        Collections.sort(all_targets, info_ASC_score_order);
        int found_decoy = 0;
        // select a list till #allowed_decoys are found
        // make an informative name - containing both peptides and modifications
        for (int i = 0; i < all_targets.size(); i++) {
            if (found_decoy < allowed_decoys) {
                SelectInfo o = all_targets.get(i);
                selected_targets.add(o);
                if (!o.getStatus().equals("target")) {
                    found_decoy++;
                }
            } else {
                break;
            }
        }
        return selected_targets;
    }

    public void run() throws IOException {
        HashSet<SelectInfo> unionList = getUnionList(folder, allowed_decoys);
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("FileName" + "\t" + "#Targets" + "\t" + "Recall (%)" + "\t" + "#ValidatedUniqueTargetsPerRun" + "\t" + "#AllValidatedUniqueTargets" + "\n");
        for (File f : folder.listFiles()) {
            if (f.getName().startsWith("result_td_target") && f.getName().endsWith(".fasta.txt")) {
                ArrayList<SelectInfo> targets = getTargets(f, allowed_decoys);
                int targetsNum = targets.size();
                double[] recall = getRecallInfo(targets, unionList);
                bw.write(f.getName() + "\t" + targetsNum + "\t" + recall[0] + "\t" + recall[1] + "\t" + recall[2] + "\n");
            }
        }
        bw.close();
    }

    public class SelectInfo {

        private String proteinA,
                proteinB,
                peptideA,
                peptideB,
                modificationA,
                modificationB,
                status;
        private double score;

        public SelectInfo(String proteinA, String proteinB, String peptideA, String peptideB, String modificationA, String modificationB, String status, double score) {
            this.proteinA = proteinA;
            this.proteinB = proteinB;
            this.peptideA = peptideA;
            this.peptideB = peptideB;
            this.modificationA = modificationA;
            this.modificationB = modificationB;
            this.status = status;
            this.score = score;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getProteinA() {
            return proteinA;
        }

        public void setProteinA(String proteinA) {
            this.proteinA = proteinA;
        }

        public String getProteinB() {
            return proteinB;
        }

        public void setProteinB(String proteinB) {
            this.proteinB = proteinB;
        }

        public String getPeptideA() {
            return peptideA;
        }

        public void setPeptideA(String peptideA) {
            this.peptideA = peptideA;
        }

        public String getPeptideB() {
            return peptideB;
        }

        public void setPeptideB(String peptideB) {
            this.peptideB = peptideB;
        }

        public String getModificationA() {
            return modificationA;
        }

        public void setModificationA(String modificationA) {
            this.modificationA = modificationA;
        }

        public String getModificationB() {
            return modificationB;
        }

        public void setModificationB(String modificationB) {
            this.modificationB = modificationB;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
            hash = 47 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
            hash = 47 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
            hash = 47 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
            hash = 47 * hash + (this.modificationA != null ? this.modificationA.hashCode() : 0);
            hash = 47 * hash + (this.modificationB != null ? this.modificationB.hashCode() : 0);
            hash = 47 * hash + (this.status != null ? this.status.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SelectInfo other = (SelectInfo) obj;
            if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
                return false;
            }
            if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
                return false;
            }
            if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
                return false;
            }
            if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
                return false;
            }
            if ((this.modificationA == null) ? (other.modificationA != null) : !this.modificationA.equals(other.modificationA)) {
                return false;
            }
            if ((this.modificationB == null) ? (other.modificationB != null) : !this.modificationB.equals(other.modificationB)) {
                return false;
            }
            if ((this.status == null) ? (other.status != null) : !this.status.equals(other.status)) {
                return false;
            }
            return true;
        }
    }

    /**
     * To sort selected info in a ascending order of scores
     */
    public final Comparator<SelectInfo> info_ASC_score_order
            = new Comparator<SelectInfo>() {
                @Override
                public int compare(SelectInfo o1, SelectInfo o2) {
                    return o1.getScore() > o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        int allowed_decoys = 5; // how many decoys are needed to be found 
        File output = new File("C:\\Users\\Sule\\Desktop\\test.txt"), // find overall how many targets are and their union targets
                folder = new File("C:\\Users\\Sule\\Desktop\\test_tds");
        output = new File("C:\\Users\\Sule\\Desktop\\test100db.txt"); // find overall how many targets are and their union targets
        folder = new File("C:\\Users\\Sule\\Desktop\\db100");

        Merger m = new Merger(allowed_decoys, output, folder);
        m.run();
    }

}
