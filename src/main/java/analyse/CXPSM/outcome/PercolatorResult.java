/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.outcome;

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * This class holds information from Percolator outputs.
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
    private boolean isXLinkingPossible = true; // Since Kojak predicted some wrong cross-linking site, this control will check if validated Percolator Result is possible or not
    private boolean checkLysine;
    private static final Logger LOGGER = Logger.getLogger(PercolatorResult.class);

    public PercolatorResult(String mgfName, String psmID, String peptides, String proteinIDs, String type,
            double score, double qvalue, double posterior_error,
            HashMap<String, String> accs, boolean isXilmass, boolean checkLysine) {
        this.mgfName = mgfName;
        this.type = type;
        this.checkLysine = checkLysine;
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
        // remove modifications...
        String newPepA = "";
        boolean isMod = false;
        for (int i = 0; i < peptideA.length(); i++) {
            if (peptideA.charAt(i) == '[') {
                isMod = true;
            }
            if (!isMod) {
                newPepA += peptideA.charAt(i);
            }
            if (peptideA.charAt(i) == ']') {
                isMod = false;
            }
        }
        String newPepB = "";
        isMod = false;
        for (int i = 0; i < peptideB.length(); i++) {
            if (peptideB.charAt(i) == '[') {
                isMod = true;
            }
            if (!isMod) {
                newPepB += peptideB.charAt(i);
            }
            if (peptideB.charAt(i) == ']') {
                isMod = false;
            }
        }
        String proteinSeqA = accs.get(proteinA),
                proteinSeqB = accs.get(proteinB);

        linkA = Integer.parseInt(pepApart.substring(pepApart.indexOf("(") + 1, pepApart.indexOf(")")));
        linkB = Integer.parseInt(pepBpart.substring(pepBpart.indexOf("(") + 1, pepBpart.indexOf(")")));

        pepApart = newPepA;
        pepBpart = newPepB;

        peptideA = newPepA;
        peptideB = newPepB;

        // this control checks if there is a reverse order on protein listing..
        int tmpindexA = proteinSeqA.indexOf(newPepA),
                tmpindexB = proteinSeqB.indexOf(newPepB);
        // so given protein order must be other way around        
        if (tmpindexA == -1 && tmpindexB == -1) {
            tmpindexB = proteinSeqA.indexOf(newPepB);
            tmpindexA = proteinSeqB.indexOf(newPepA);
            String tmpProteinA = proteinB,
                    tmpProteinB = proteinA;
            proteinA = tmpProteinA;
            proteinB = tmpProteinB;
        }

        // now check if this cross-linked peptide is possible   
        int toCheckLinkA = linkA - 1,
                toCheckLinkB = linkB - 1;
        isXLinkingPossible = checkPossibility(peptideA, toCheckLinkA, peptideB, toCheckLinkB, checkLysine);
        LOGGER.info(new StringBuilder(peptideA).append(linkA).append(peptideB).append(linkB).append(isXLinkingPossible));

        linkA += tmpindexA;
        linkB += tmpindexB;
        this.score = score;
        this.qvalue = qvalue;
        this.posterior_error = posterior_error;
    }

    public static boolean checkPossibility(String peptideA, int linkA, String peptideB, int linkB, boolean checkLysine) {
        boolean isPossible = true;
        if (!checkLysine) {
            LOGGER.info(" A cross-linker is not specific to only Lysine residues! This implementation is not written yet!");
        }
        char linkedA = peptideA.charAt(linkA),
                linkedB = peptideB.charAt(linkB);
        if (linkedA != 'K' || linkedB != 'K') {
            isPossible = false;
        }
        return isPossible;
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

    public boolean isIsXLinkingPossible() {
        return isXLinkingPossible;
    }

    public void setIsXLinkingPossible(boolean isXLinkingPossible) {
        this.isXLinkingPossible = isXLinkingPossible;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.mgfName != null ? this.mgfName.hashCode() : 0);
        hash = 41 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 41 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 41 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 41 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.qvalue) ^ (Double.doubleToLongBits(this.qvalue) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.posterior_error) ^ (Double.doubleToLongBits(this.posterior_error) >>> 32));
        hash = 41 * hash + this.scan;
        hash = 41 * hash + this.linkA;
        hash = 41 * hash + this.linkB;
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
        final PercolatorResult other = (PercolatorResult) obj;
        if ((this.mgfName == null) ? (other.mgfName != null) : !this.mgfName.equals(other.mgfName)) {
            return false;
        }
        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
            return false;
        }
        if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
            return false;
        }
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (Double.doubleToLongBits(this.qvalue) != Double.doubleToLongBits(other.qvalue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.posterior_error) != Double.doubleToLongBits(other.posterior_error)) {
            return false;
        }
        if (this.scan != other.scan) {
            return false;
        }
        if (this.linkA != other.linkA) {
            return false;
        }
        if (this.linkB != other.linkB) {
            return false;
        }
        return true;
    }

}
