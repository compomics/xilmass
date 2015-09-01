/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import java.util.HashMap;

/**
 * This class holds information from Percolator outputs.
 * 
 * 
 * @author Sule
 */
public class PercolatorResult {

    private String mgfName,
            peptideA,
            peptideB,
            proteinA,
            proteinB,
            type = "none";
    private double score,
            qvalue,
            posterior_error;
    private int scan, // can be duplicated...one spectrum may have multiples identification with the same best score
            linkA,
            linkB;

    public PercolatorResult(String mgfName, String psmID, String peptides, String proteinIDs, String type,
            double score, double qvalue, double posterior_error,
            HashMap<String, String> accs, boolean isXilmass) {
        this.mgfName = mgfName;
        this.type = type;
        scan = Integer.parseInt(psmID.split("-")[1]);
        String pepApart = peptides.split("--")[0],
                pepBpart = peptides.split("--")[1];
        peptideA = pepApart.substring(2, pepApart.indexOf("("));
        peptideB = pepBpart.substring(0, pepBpart.indexOf("("));      
        linkA = Integer.parseInt(pepApart.substring(pepApart.indexOf("(") + 1, pepApart.indexOf(")")));
        linkB = Integer.parseInt(pepBpart.substring(pepBpart.indexOf("(") + 1, pepBpart.indexOf(")")));
        if (!isXilmass) {
            proteinA = proteinIDs.split("\t")[0].split("\\|")[1].replace(" ", "");
            proteinB = proteinIDs.split("\t")[0].split("\\|")[1].replace(" ", "");
            if (proteinIDs.split("\t").length == 2) {
                proteinB = proteinIDs.split("\t")[1].split("\\|")[1].replace(" ", "");
            }
        } else {
            proteinA = proteinIDs.split("-")[0];
            proteinA = proteinA.substring(0, proteinA.indexOf("("));
            proteinB = proteinIDs.split("-")[2];
            proteinB = proteinB.substring(0, proteinB.indexOf("("));
        }
        String proteinSeqA = accs.get(proteinA),
                proteinSeqB = accs.get(proteinB);

        linkA = Integer.parseInt(pepApart.substring(pepApart.indexOf("(") + 1, pepApart.indexOf(")")));
        linkB = Integer.parseInt(pepBpart.substring(pepBpart.indexOf("(") + 1, pepBpart.indexOf(")")));

        int tmpindexA = proteinSeqA.indexOf(peptideA),
                tmpindexB = proteinSeqB.indexOf(peptideB);
        linkA += tmpindexA;
        linkB += tmpindexB;
        this.score = score;
        this.qvalue = qvalue;
        this.posterior_error = posterior_error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
  
    public String getMgfName() {
        return mgfName;
    }

    public void setMgfName(String mgfName) {
        this.mgfName = mgfName;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getQvalue() {
        return qvalue;
    }

    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }

    public double getPosterior_error() {
        return posterior_error;
    }

    public void setPosterior_error(double posterior_error) {
        this.posterior_error = posterior_error;
    }

    public int getScan() {
        return scan;
    }

    public void setScan(int scan) {
        this.scan = scan;
    }

    public int getLinkA() {
        return linkA;
    }

    public void setLinkA(int linkA) {
        this.linkA = linkA;
    }

    public int getLinkB() {
        return linkB;
    }

    public void setLinkB(int linkB) {
        this.linkB = linkB;
    }

}
