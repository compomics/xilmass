/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.outcome;

import java.util.Comparator;

/**
 * This class holds information XilmassResults from a tab-limited text document
 *
 * @author Sule
 */
public class XilmassResult extends Outcome {

    private String proteinA, // a protein derived from peptideA
            modA, // PTMs found on peptideA
            proteinB, // a protein derived from peptideB
            modB, // PTMs found on peptideB
            type, // linking type
            scoringFunctionName, // name of a scoring function
            expMatchedPeakList, // a list of matched peaks
            theoMatchedPeakList, // a list of theoretical matched peaks 
            cPeptidePattern = "",
            ionWeight = ""; // identified ion weights
    private int scanNr, // scan number
            charge, // precursor charge
            linkPeptideA, // linked index on peptideA
            linkPeptideB; // linked index on peptideB
    private double retentionTime, // retention time
            observedMass, // observed mass
            ms1Err, // precursor tolerance
            absMS1err, // absolute value of difference in calculated and observed mass in ppm
            score, // Xilmass XPSM score
            lnNumSp;// Natural logarithm of #peptide in database within precursor tolerance for MS/MS spectrum

    public XilmassResult(String line, boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights) {
        String[] sp = line.split("\t");
        super.spectrumFileName = sp[0];
        super.spectrumTitle = sp[1];
        scanNr = Integer.parseInt(sp[2]);
        retentionTime = Double.parseDouble(sp[3]);
        observedMass = Double.parseDouble(sp[4]);
        charge = Integer.parseInt(sp[5]);
        ms1Err = Double.parseDouble(sp[6]);
        absMS1err = Double.parseDouble(sp[7]);
        super.peptideA = sp[8];
        proteinA = sp[9];
        modA = sp[10];
        super.peptideB = sp[11];
        proteinB = sp[12];
        super.accProteinA = proteinA.substring(0, proteinA.indexOf("("));
        super.accProteinB = proteinB.substring(0, proteinB.indexOf("("));
        super.target_decoy = "";
        super.trueCrossLinking = "";
        modB = sp[13];
        linkPeptideA = Integer.parseInt(sp[14]);
        linkPeptideB = Integer.parseInt(sp[15]);
        super.crossLinkedSitePro1 = Integer.parseInt(sp[16]);
        super.crossLinkedSitePro2 = Integer.parseInt(sp[17]);
        type = sp[18];
        score = Double.parseDouble(sp[19]);
        scoringFunctionName = sp[20];
        lnNumSp = Double.parseDouble(sp[21]);
        expMatchedPeakList = sp[28];
        theoMatchedPeakList = sp[29];
        super.label = sp[30];
        if (doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = sp[31];
            ionWeight = sp[32];
        } else if (doesKeepCPeptideFragmPattern && !doesKeepWeights) {
            cPeptidePattern = sp[31];
            ionWeight = "";
        } else if (!doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = "";
            ionWeight = sp[31];
        }
    }

    public String getProteinA() {
        return proteinA;
    }

    public void setProteinA(String proteinA) {
        this.proteinA = proteinA;
    }

    public String getModA() {
        return modA;
    }

    public void setModA(String modA) {
        this.modA = modA;
    }

    public String getProteinB() {
        return proteinB;
    }

    public void setProteinB(String proteinB) {
        this.proteinB = proteinB;
    }

    public String getModB() {
        return modB;
    }

    public void setModB(String modB) {
        this.modB = modB;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScoringFunctionName() {
        return scoringFunctionName;
    }

    public void setScoringFunctionName(String scoringFunctionName) {
        this.scoringFunctionName = scoringFunctionName;
    }

    public String getExpMatchedPeakList() {
        return expMatchedPeakList;
    }

    public void setExpMatchedPeakList(String expMatchedPeakList) {
        this.expMatchedPeakList = expMatchedPeakList;
    }

    public String getTheoMatchedPeakList() {
        return theoMatchedPeakList;
    }

    public void setTheoMatchedPeakList(String theoMatchedPeakList) {
        this.theoMatchedPeakList = theoMatchedPeakList;
    }

    public String getcPeptidePattern() {
        return cPeptidePattern;
    }

    public void setcPeptidePattern(String cPeptidePattern) {
        this.cPeptidePattern = cPeptidePattern;
    }

    public int getScanNr() {
        return scanNr;
    }

    public void setScanNr(int scanNr) {
        this.scanNr = scanNr;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getLinkPeptideA() {
        return linkPeptideA;
    }

    public void setLinkPeptideA(int linkPeptideA) {
        this.linkPeptideA = linkPeptideA;
    }

    public int getLinkPeptideB() {
        return linkPeptideB;
    }

    public void setLinkPeptideB(int linkPeptideB) {
        this.linkPeptideB = linkPeptideB;
    }
    
    public double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public double getObservedMass() {
        return observedMass;
    }

    public void setObservedMass(double observedMass) {
        this.observedMass = observedMass;
    }

    public double getMs1Err() {
        return ms1Err;
    }

    public void setMs1Err(double ms1Err) {
        this.ms1Err = ms1Err;
    }

    public double getAbsMS1err() {
        return absMS1err;
    }

    public void setAbsMS1err(double absMS1err) {
        this.absMS1err = absMS1err;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getLnNumSp() {
        return lnNumSp;
    }

    public void setLnNumSp(double lnNumSp) {
        this.lnNumSp = lnNumSp;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.spectrumFileName != null ? this.spectrumFileName.hashCode() : 0);
        hash = 79 * hash + (this.spectrumTitle != null ? this.spectrumTitle.hashCode() : 0);
        hash = 79 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 79 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 79 * hash + (this.modA != null ? this.modA.hashCode() : 0);
        hash = 79 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 79 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 79 * hash + (this.modB != null ? this.modB.hashCode() : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.scoringFunctionName != null ? this.scoringFunctionName.hashCode() : 0);
        hash = 79 * hash + (this.expMatchedPeakList != null ? this.expMatchedPeakList.hashCode() : 0);
        hash = 79 * hash + (this.theoMatchedPeakList != null ? this.theoMatchedPeakList.hashCode() : 0);
        hash = 79 * hash + (this.cPeptidePattern != null ? this.cPeptidePattern.hashCode() : 0);
        hash = 79 * hash + this.scanNr;
        hash = 79 * hash + this.charge;
        hash = 79 * hash + this.linkPeptideA;
        hash = 79 * hash + this.linkPeptideB;
        hash = 79 * hash + super.crossLinkedSitePro1;
        hash = 79 * hash + super.crossLinkedSitePro2;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.retentionTime) ^ (Double.doubleToLongBits(this.retentionTime) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.observedMass) ^ (Double.doubleToLongBits(this.observedMass) >>> 32));
        return hash;
    }

    /**
     * Return a String object to write down the validated PSMs for Xilmass
     *
     * @return
     */
    public String toPrint() {

        String toPrint = spectrumFileName + "\t" + spectrumTitle + "\t"
                + observedMass + "\t" + charge + "\t" + retentionTime + "\t" + scanNr + "\t"
                + ms1Err + "\t" + absMS1err + "\t"
                + peptideA + "\t" + proteinA + "\t" + modA + "\t"
                + peptideB + "\t" + proteinB + "\t" + modB + "\t"
                + type + "\t"
                + linkPeptideA + "\t" + linkPeptideB + "\t" + super.crossLinkedSitePro1 + "\t" +super.crossLinkedSitePro2 + "\t"
                + scoringFunctionName + "\t" + score + "\t"
                + expMatchedPeakList + "\t" + theoMatchedPeakList + "\t"
                + lnNumSp + "\t"
                + target_decoy + "\t"
                + super.label + "\t"
                + trueCrossLinking;
        if (!cPeptidePattern.isEmpty()) {
            toPrint += "\t" + cPeptidePattern;
        }
        if (!ionWeight.isEmpty()) {
            toPrint += "\t" + ionWeight;
        }
        return toPrint;
    }

    /**
     * String object for XilmassResults
     *
     * @return
     */
    @Override
    public String toString() {
        return "XilmassResult{" + "spectrumFile=" + spectrumFileName + ", spectrumTitle=" + spectrumTitle
                + ", peptideA=" + peptideA + ", proteinA=" + proteinA + ", modA=" + modA
                + ", peptideB=" + peptideB + ", proteinB=" + proteinB + ", modB=" + modB
                + ", type=" + type + ", scoringFunctionName=" + scoringFunctionName
                + ", expMatchedPeakList=" + expMatchedPeakList + ", theoMatchedPeakList=" + theoMatchedPeakList
                + ", cPeptidePattern=" + cPeptidePattern + ", labeling=" + label
                + ", scanNr=" + scanNr + ", charge=" + charge
                + ", linkPeptideA=" + linkPeptideA + ", linkPeptideB=" + linkPeptideB + ", linkProteinA=" + super.crossLinkedSitePro1 + ", linkProteinB=" + super.crossLinkedSitePro2
                + ", retentionTime=" + retentionTime
                + ", observedMass=" + observedMass + ", ms1Err=" + ms1Err + ", absMS1err=" + absMS1err + ", score=" + score + ", lnNumSp=" + lnNumSp + '}';
    }

    public static final Comparator<XilmassResult> ScoreASC
            = new Comparator<XilmassResult>() {
                @Override
                public int compare(XilmassResult o1, XilmassResult o2) {
                    return o1.getScore() < o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            };

    public static final Comparator<XilmassResult> ScoreDSCBasedTDs
            = new Comparator<XilmassResult>() {
                @Override
                public int compare(XilmassResult o1, XilmassResult o2) {
                    if (o1.getScore() > o2.getScore()) {
                        return -1;
                    } else if (o1.getScore() < o2.getScore()) {
                        return 1;
                    } else {
                        if (o1.getTarget_decoy().equals("TT")) {
                            if (o2.getTarget_decoy().equals("TT")) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } else if (o1.getTarget_decoy().equals("DD")) {
                            if (o2.getTarget_decoy().equals("DD")) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else if (o2.getTarget_decoy().equals("TD")) {
                            if (o2.getTarget_decoy().equals("TT")) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    }
                    return 0;
                }
            };

    public static final Comparator<XilmassResult> ScoreDSC
            = new Comparator<XilmassResult>() {
                @Override
                public int compare(XilmassResult o1, XilmassResult o2) {
                    return o1.getScore() > o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            };

}