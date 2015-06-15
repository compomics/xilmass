/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

/**
 *
 * @author Sule
 */
public class XilmassResult extends Outcome {

    private String spectrumFile,
            spectrumTitle,
            scoringFunction,// name of a scoring function
            peptideA, // peptideA sequence
            peptideB, // peptideB sequence
            modificationA, // modification on peptideA
            modificationB, // modiication on peptideB
            expMatchedPeakList, // a list of matched peaks
            theoMatchedPeakList, // a list of theoretical matched peaks 
            cPeptideFragmentPatternName,
            label;

    private double precursorMass, // experimental precursor mass
            theoreticalMass, // theoretical precursor mass
            precursorMZ,// precursor m/z
            ms1err, // precursor mass tolerance 
            score, // XPSM score
            weight;

    private int charge, // precursor charge
            expMatchedPeaks, // all matched experimental peaks 
            theoMatchedPeaks, // all matched theoretical peaks
            theoreticalAs,// number of theoretical peaks from peptideA
            theoreticalPs; // number of theoretical peaks from peptideB

    public XilmassResult(String spectrumFile, String spectrumTitle, String scoringFunction, String proteinA, String proteinB, String peptideA, String peptideB, String modificationA, String modificationB, String expMatchedPeakList, String theoMatchedPeakList, String cPeptideFragmentPatternName, String label, double precursorMass, double theoreticalMass, double precursorMZ, double ms1err, double score, double weight, int charge, int linkerPosA, int linkerPosB, int expMatchedPeaks, int theoMatchedPeaks, int theoreticalAs, int theoreticalPs) {
        this.spectrumFile = spectrumFile;
        this.spectrumTitle = spectrumTitle;
        this.scoringFunction = scoringFunction;
        super.accessProteinA = proteinA;
        super.accessProteinB = proteinB;
        this.peptideA = peptideA;
        this.peptideB = peptideB;
        this.modificationA = modificationA;
        this.modificationB = modificationB;
        this.expMatchedPeakList = expMatchedPeakList;
        this.theoMatchedPeakList = theoMatchedPeakList;
        this.cPeptideFragmentPatternName = cPeptideFragmentPatternName;
        this.label = label;
        this.precursorMass = precursorMass;
        this.theoreticalMass = theoreticalMass;
        this.precursorMZ = precursorMZ;
        this.ms1err = ms1err;
        this.score = score;
        this.weight = weight;
        this.charge = charge;
        super.crossLinkedSitePro1 = linkerPosA;
        super.crossLinkedSitePro2 = linkerPosB;
        this.expMatchedPeaks = expMatchedPeaks;
        this.theoMatchedPeaks = theoMatchedPeaks;
        this.theoreticalAs = theoreticalAs;
        this.theoreticalPs = theoreticalPs;
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

    public String getScoringFunction() {
        return scoringFunction;
    }

    public void setScoringFunction(String scoringFunction) {
        this.scoringFunction = scoringFunction;
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

    public String getcPeptideFragmentPatternName() {
        return cPeptideFragmentPatternName;
    }

    public void setcPeptideFragmentPatternName(String cPeptideFragmentPatternName) {
        this.cPeptideFragmentPatternName = cPeptideFragmentPatternName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPrecursorMass() {
        return precursorMass;
    }

    public void setPrecursorMass(double precursorMass) {
        this.precursorMass = precursorMass;
    }

    public double getTheoreticalMass() {
        return theoreticalMass;
    }

    public void setTheoreticalMass(double theoreticalMass) {
        this.theoreticalMass = theoreticalMass;
    }

    public double getPrecursorMZ() {
        return precursorMZ;
    }

    public void setPrecursorMZ(double precursorMZ) {
        this.precursorMZ = precursorMZ;
    }

    public double getMs1err() {
        return ms1err;
    }

    public void setMs1err(double ms1err) {
        this.ms1err = ms1err;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getExpMatchedPeaks() {
        return expMatchedPeaks;
    }

    public void setExpMatchedPeaks(int expMatchedPeaks) {
        this.expMatchedPeaks = expMatchedPeaks;
    }

    public int getTheoMatchedPeaks() {
        return theoMatchedPeaks;
    }

    public void setTheoMatchedPeaks(int theoMatchedPeaks) {
        this.theoMatchedPeaks = theoMatchedPeaks;
    }

    public int getTheoreticalAs() {
        return theoreticalAs;
    }

    public void setTheoreticalAs(int theoreticalAs) {
        this.theoreticalAs = theoreticalAs;
    }

    public int getTheoreticalPs() {
        return theoreticalPs;
    }

    public void setTheoreticalPs(int theoreticalPs) {
        this.theoreticalPs = theoreticalPs;
    }

}
