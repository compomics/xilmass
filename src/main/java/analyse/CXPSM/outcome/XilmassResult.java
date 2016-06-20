/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.outcome;

import java.util.Comparator;
import java.util.regex.Pattern;

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
            ionWeight = "", // identified ion weights
            scanNr = "";// because the scan numbers can be merged!
    private int charge, // precursor charge
            linkPeptideA, // linked index on peptideA
            linkPeptideB = -1; // linked index on peptideB
    private double retentionTime, // retention time
            observedMass, // observed mass
            ms1Err, // precursor tolerance
            absMS1err, // absolute value of difference in calculated and observed mass in ppm
            score, // Xilmass XPSM score
            deltaScore,
            lnNumSp,// Natural logarithm of #peptide in database within precursor tolerance for MS/MS spectrum
            lnNumXSp,
            calculatedMass; // calculated theoretical cross-linked peptides

    public XilmassResult(String line, boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights, String ScoringFunctionName) {
        String[] sp = line.split("\t");
        calculatedMass = Double.parseDouble(sp[6]);
        super.spectrumFileName = sp[0];
        super.spectrumTitle = sp[1];
        scanNr = sp[2];
        retentionTime = Double.parseDouble(sp[3]);
        observedMass = Double.parseDouble(sp[4]);
        charge = Integer.parseInt(sp[5]);
        ms1Err = Double.parseDouble(sp[7]);
        absMS1err = Double.parseDouble(sp[8]);
        super.peptideA = sp[9];
        proteinA = sp[10];
        modA = sp[11];
        // check if type is indeed cross-linked...
        type = sp[19];

        super.peptideB = sp[12];
        proteinB = sp[13];
        modB = sp[14];

        super.target_decoy = "";

        linkPeptideA = Integer.parseInt(sp[15]);
        if (!type.equals("MonoLinked")) {
            linkPeptideB = Integer.parseInt(sp[16]);
        }

        super.crossLinkedSitePro1 = Integer.parseInt(sp[17]);
        if (!type.equals("MonoLinked")) {
            super.crossLinkedSitePro2 = Integer.parseInt(sp[18]);
        }
        score = Double.parseDouble(sp[20]);
        scoringFunctionName = ScoringFunctionName;
        deltaScore = Double.parseDouble(sp[21]);
        lnNumSp = Double.parseDouble(sp[22]);
        lnNumXSp = Double.parseDouble(sp[23]);

        expMatchedPeakList = sp[26];
        theoMatchedPeakList = sp[27];
        super.label = sp[28];
        if (doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = sp[29];
            ionWeight = sp[30];
        } else if (doesKeepCPeptideFragmPattern && !doesKeepWeights) {
            cPeptidePattern = sp[29];
            ionWeight = "";
        } else if (!doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = "";
            ionWeight = sp[29];
        }
        // remove the part from "(" on the accession names such as PROTEINACC(START-END)
        String inSilicoInfo = "(()(\\d+)(-)(\\d+)())";
        Pattern p = Pattern.compile(inSilicoInfo);
        if (p.matcher(proteinA).find()) {
            super.accProteinA = proteinA.split("\\(")[0];
        }
        if (p.matcher(proteinB).find()) {
            super.accProteinB = proteinB.split("\\(")[0];
        }
    }

    /**
     * This constructor is used for testing.
     *
     * @param line
     * @param ScoringFunctionName
     */
    public XilmassResult(String line, String ScoringFunctionName) {
        boolean doesKeepCPeptideFragmPattern = false,
                doesKeepWeights = false;
        String[] sp = line.split("\t");
        calculatedMass = -1;
        super.spectrumFileName = sp[0];
        super.spectrumTitle = sp[1];
        scanNr = sp[2];
        retentionTime = Double.parseDouble(sp[3]);
        observedMass = Double.parseDouble(sp[4]);
        charge = Integer.parseInt(sp[5]);
        ms1Err = Double.parseDouble(sp[6]);
        absMS1err = Double.parseDouble(sp[7]);
        super.peptideA = sp[8];
        proteinA = sp[9];
        modA = sp[10];
        // check if type is indeed cross-linked...
        type = sp[18];

        super.peptideB = sp[11];
        proteinB = sp[12];
        modB = sp[13];

        super.target_decoy = "";

        linkPeptideA = Integer.parseInt(sp[14]);
        if (!type.equals("MonoLinked")) {
            linkPeptideB = Integer.parseInt(sp[15]);
        }

        super.crossLinkedSitePro1 = Integer.parseInt(sp[16]);
        if (!type.equals("MonoLinked")) {
            super.crossLinkedSitePro2 = Integer.parseInt(sp[17]);
        }
        score = Double.parseDouble(sp[19]);
        scoringFunctionName = ScoringFunctionName;
        deltaScore = -1;
        lnNumSp = Double.parseDouble(sp[21]);
        lnNumXSp = -1;

        expMatchedPeakList = sp[28];
        theoMatchedPeakList = sp[29];
        super.label = sp[30];
        if (doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = sp[29];
            ionWeight = sp[30];
        } else if (doesKeepCPeptideFragmPattern && !doesKeepWeights) {
            cPeptidePattern = sp[29];
            ionWeight = "";
        } else if (!doesKeepCPeptideFragmPattern && doesKeepWeights) {
            cPeptidePattern = "";
            ionWeight = sp[29];
        }
        // remove the part from "(" on the accession names such as PROTEINACC(START-END)
        String inSilicoInfo = "(()(\\d+)(-)(\\d+)())";
        Pattern p = Pattern.compile(inSilicoInfo);
        if (p.matcher(proteinA).find()) {
            super.accProteinA = proteinA.split("\\(")[0];
        }
        if (p.matcher(proteinB).find()) {
            super.accProteinB = proteinB.split("\\(")[0];
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

    public String getScanNr() {
        return scanNr;
    }

    public void setScanNr(String scanNr) {
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
        hash = 79 * hash + (this.scanNr != null ? this.scanNr.hashCode() : 0);
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
     * @param hasPredictions true: if Xwalk predictions avaliable from
     * customized file, false: no Xwalk prediction
     *
     * @return
     */
    public String toPrint(boolean hasPredictions) {

        String toPrint = spectrumFileName + "\t" + spectrumTitle + "\t"
                + observedMass + "\t" + charge + "\t" + retentionTime + "\t" + scanNr + "\t"
                + ms1Err + "\t" + absMS1err + "\t" + calculatedMass + "\t"
                + peptideA + "\t" + super.accProteinA + "\t" + modA + "\t"
                + peptideB + "\t" + super.accProteinB + "\t" + modB + "\t"
                + type + "\t"
                + linkPeptideA + "\t" + linkPeptideB + "\t" + super.crossLinkedSitePro1 + "\t" + super.crossLinkedSitePro2 + "\t"
                + scoringFunctionName + "\t" + score + "\t" + deltaScore + "\t"
                + lnNumSp + "\t" + lnNumXSp + "\t"
                + expMatchedPeakList + "\t" + theoMatchedPeakList + "\t"
                + target_decoy + "\t"
                + super.label;

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
                + ", Scan=" + scanNr + ", charge=" + charge
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
