/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import java.util.Comparator;

/**
 * This class holds information XilmassResults from a tab-limited text document
 *
 * @author Sule
 */
public class XilmassResult extends Outcome {

    private String spectrumFile,
            spectrumTitle,
            peptideA, // a sequence of a linked peptideA
            proteinA, // a protein derived from peptideA
            modA, // PTMs found on peptideA
            peptideB, // a sequence of a linked peptideB
            proteinB, // a protein derived from peptideB
            modB, // PTMs found on peptideB
            type, // linking type
            scoringFunctionName, // name of a scoring function
            expMatchedPeakList, // a list of matched peaks
            theoMatchedPeakList, // a list of theoretical matched peaks 
            cPeptidePattern = "",
            labeling = "", // heavy labeled or light labeled of a cross linker
            trueCrossLinking = "", // info for cross-linking info (decision/euclidean distances)
            target_decoy = "", // TT/TD or DD
            ionWeight = ""; // identified ion weights

    private int scanNr, // scan number
            charge, // precursor charge
            linkPeptideA, // linked index on peptideA
            linkPeptideB, // linked index on peptideB
            linkProteinA, // linked index on proteinA
            linkProteinB; // linked index on proteinB

    private double retentionTime, // retention time
            observedMass, // observed mass
            ms1Err, // precursor tolerance
            absMS1err, // absolute value of difference in calculated and observed mass in ppm
            score, // Xilmass XPSM score
            lnNumSp;// Natural logarithm of #peptide in database within precursor tolerance for MS/MS spectrum

    public XilmassResult(String line, boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights) {
        String[] sp = line.split("\t");
        spectrumFile = sp[0];
        spectrumTitle = sp[1];
        scanNr = Integer.parseInt(sp[2]);
        retentionTime = Double.parseDouble(sp[3]);
        observedMass = Double.parseDouble(sp[4]);
        charge = Integer.parseInt(sp[5]);
        ms1Err = Double.parseDouble(sp[6]);
        absMS1err = Double.parseDouble(sp[7]);
        peptideA = sp[8];
        proteinA = sp[9];
        modA = sp[10];
        peptideB = sp[11];
        proteinB = sp[12];
        modB = sp[13];
        linkPeptideA = Integer.parseInt(sp[14]);
        linkPeptideB = Integer.parseInt(sp[15]);
        linkProteinA = Integer.parseInt(sp[16]);
        linkProteinB = Integer.parseInt(sp[17]);
        type = sp[18];
        score = Double.parseDouble(sp[19]);
        scoringFunctionName = sp[20];
        lnNumSp = Double.parseDouble(sp[21]);
        expMatchedPeakList = sp[28];
        theoMatchedPeakList = sp[29];
        labeling = sp[30];

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

    public String getTarget_decoy() {
        return target_decoy;
    }

    public void setTarget_decoy(String target_decoy) {
        this.target_decoy = target_decoy;
    }

    public String getTrueCrossLinking() {
        return trueCrossLinking;
    }

    public void setTrueCrossLinking(String trueCrossLinking) {
        this.trueCrossLinking = trueCrossLinking;
    }

    public String getSpectrumFile() {
        return spectrumFile;
    }

    public void setSpectrumFile(String spectrumFile) {
        this.spectrumFile = spectrumFile;
    }

    public String getSpectrumTitle() {
        return spectrumTitle;
    }

    public void setSpectrumTitle(String spectrumTitle) {
        this.spectrumTitle = spectrumTitle;
    }

    public String getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(String peptideA) {
        this.peptideA = peptideA;
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

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
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

    public String getLabeling() {
        return labeling;
    }

    public void setLabeling(String labeling) {
        this.labeling = labeling;
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

    public int getLinkProteinA() {
        return linkProteinA;
    }

    public void setLinkProteinA(int linkProteinA) {
        this.linkProteinA = linkProteinA;
    }

    public int getLinkProteinB() {
        return linkProteinB;
    }

    public void setLinkProteinB(int linkProteinB) {
        this.linkProteinB = linkProteinB;
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
        hash = 79 * hash + (this.spectrumFile != null ? this.spectrumFile.hashCode() : 0);
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
        hash = 79 * hash + (this.labeling != null ? this.labeling.hashCode() : 0);
        hash = 79 * hash + this.scanNr;
        hash = 79 * hash + this.charge;
        hash = 79 * hash + this.linkPeptideA;
        hash = 79 * hash + this.linkPeptideB;
        hash = 79 * hash + this.linkProteinA;
        hash = 79 * hash + this.linkProteinB;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.retentionTime) ^ (Double.doubleToLongBits(this.retentionTime) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.observedMass) ^ (Double.doubleToLongBits(this.observedMass) >>> 32));
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
        final XilmassResult other = (XilmassResult) obj;
        if ((this.spectrumFile == null) ? (other.spectrumFile != null) : !this.spectrumFile.equals(other.spectrumFile)) {
            return false;
        }
        if ((this.spectrumTitle == null) ? (other.spectrumTitle != null) : !this.spectrumTitle.equals(other.spectrumTitle)) {
            return false;
        }
        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
            return false;
        }
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
        if ((this.modA == null) ? (other.modA != null) : !this.modA.equals(other.modA)) {
            return false;
        }
        if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
        if ((this.modB == null) ? (other.modB != null) : !this.modB.equals(other.modB)) {
            return false;
        }
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.scoringFunctionName == null) ? (other.scoringFunctionName != null) : !this.scoringFunctionName.equals(other.scoringFunctionName)) {
            return false;
        }
        if ((this.expMatchedPeakList == null) ? (other.expMatchedPeakList != null) : !this.expMatchedPeakList.equals(other.expMatchedPeakList)) {
            return false;
        }
        if ((this.theoMatchedPeakList == null) ? (other.theoMatchedPeakList != null) : !this.theoMatchedPeakList.equals(other.theoMatchedPeakList)) {
            return false;
        }
        if ((this.cPeptidePattern == null) ? (other.cPeptidePattern != null) : !this.cPeptidePattern.equals(other.cPeptidePattern)) {
            return false;
        }
        if ((this.labeling == null) ? (other.labeling != null) : !this.labeling.equals(other.labeling)) {
            return false;
        }
        if (this.scanNr != other.scanNr) {
            return false;
        }
        if (this.charge != other.charge) {
            return false;
        }
        if (this.linkPeptideA != other.linkPeptideA) {
            return false;
        }
        if (this.linkPeptideB != other.linkPeptideB) {
            return false;
        }
        if (this.linkProteinA != other.linkProteinA) {
            return false;
        }
        if (this.linkProteinB != other.linkProteinB) {
            return false;
        }
        if (Double.doubleToLongBits(this.retentionTime) != Double.doubleToLongBits(other.retentionTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.observedMass) != Double.doubleToLongBits(other.observedMass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ms1Err) != Double.doubleToLongBits(other.ms1Err)) {
            return false;
        }
        if (Double.doubleToLongBits(this.absMS1err) != Double.doubleToLongBits(other.absMS1err)) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lnNumSp) != Double.doubleToLongBits(other.lnNumSp)) {
            return false;
        }
        return true;
    }

    /**
     * Return a String object to write down the validated PSMs for Xilmass
     *
     * @return
     */
    public String toPrint() {

        String toPrint = spectrumFile + "\t" + spectrumTitle + "\t"
                + observedMass + "\t" + charge + "\t" + retentionTime + "\t" + scanNr + "\t"
                + ms1Err + "\t" + absMS1err + "\t"
                + peptideA + "\t" + proteinA + "\t" + modA + "\t"
                + peptideB + "\t" + proteinB + "\t" + modB + "\t"
                + type + "\t"
                + linkPeptideA + "\t" + linkPeptideB + "\t" + linkProteinA + "\t" + linkProteinB + "\t"
                + scoringFunctionName + "\t" + score + "\t"
                + expMatchedPeakList + "\t" + theoMatchedPeakList + "\t"
                + lnNumSp + "\t"
                + target_decoy + "\t"
                + labeling + "\t"
                + trueCrossLinking + "\t";
        if (!cPeptidePattern.isEmpty()) {
            toPrint += cPeptidePattern + "\t";
        }
        if (!ionWeight.isEmpty()) {
            toPrint += ionWeight + "\t";
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
        return "XilmassResult{" + "spectrumFile=" + spectrumFile + ", spectrumTitle=" + spectrumTitle
                + ", peptideA=" + peptideA + ", proteinA=" + proteinA + ", modA=" + modA
                + ", peptideB=" + peptideB + ", proteinB=" + proteinB + ", modB=" + modB
                + ", type=" + type + ", scoringFunctionName=" + scoringFunctionName
                + ", expMatchedPeakList=" + expMatchedPeakList + ", theoMatchedPeakList=" + theoMatchedPeakList
                + ", cPeptidePattern=" + cPeptidePattern + ", labeling=" + labeling
                + ", scanNr=" + scanNr + ", charge=" + charge
                + ", linkPeptideA=" + linkPeptideA + ", linkPeptideB=" + linkPeptideB + ", linkProteinA=" + linkProteinA + ", linkProteinB=" + linkProteinB
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

    public static final Comparator<XilmassResult> ScoreDSC
            = new Comparator<XilmassResult>() {
                @Override
                public int compare(XilmassResult o1, XilmassResult o2) {
                    return o1.getScore() > o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            };

}
