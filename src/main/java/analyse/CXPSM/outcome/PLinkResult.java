/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.outcome;

import java.util.Comparator;

/**
 * This class holds cross linking site information from validated pLink results
 *
 * @author Sule
 */
public class PLinkResult extends Outcome {

    private String modLocationAlpha,
            modNameAlpha,
            modLocationBeta,
            modNameBeta;
    private int charge,
            xlinkPos1,
            xlinkPos2,
            validCandidate,
            candidateTotal;
    private double intensity,
            ms1errPPM,
            experimentalMZ,
            pLinkScore,
            eValue,
            alphaEValue,
            betaEValue,
            matchedIntensity,
            unMatchedIntensity,
            xlinkedMass;

    public PLinkResult(String spectrumTitle, String spectrumFileName, String scanNum, String label,
            String peptideA, String alphaProtein, String modLocationAlpha, String modNameAlpha,
            String peptideB, String betaProtein, String modLocationBeta, String modNameBeta,
            int charge,
            int xlinkPos1, int xlinkPos2,
            double intensity, double ms1errPPM, double experimentalMZ,
            double pLinkScore, double eValue, double alphaEValue, double betaEValue,
            double matchedIntensity, double unMatchedIntensity,
            double mass, String[] target_names, int validCandidate,int candidateTotal) {
        super.spectrumTitle = spectrumTitle;
        super.spectrumFileName = spectrumFileName;
        super.scanNumber = scanNum;
        super.label = label;
        super.peptideA = peptideA;
        super.accProteinA = alphaProtein;
        this.modLocationAlpha = modLocationAlpha;
        this.modNameAlpha = modNameAlpha;
        super.peptideB = peptideB;
        super.accProteinB = betaProtein;
        this.modLocationBeta = modLocationBeta;
        this.modNameBeta = modNameBeta;
        this.charge = charge;
        super.crossLinkedSitePro1 = xlinkPos1;
        super.crossLinkedSitePro2 = xlinkPos2;
        this.intensity = intensity;
        this.ms1errPPM = ms1errPPM;
        this.experimentalMZ = experimentalMZ;
        this.pLinkScore = pLinkScore;
        this.eValue = eValue;
        this.alphaEValue = alphaEValue;
        this.betaEValue = betaEValue;
        this.matchedIntensity = matchedIntensity;
        this.unMatchedIntensity = unMatchedIntensity;
        this.xlinkedMass = mass;
        super.target_proteins = target_names;
        super.target_decoy = "";
        super.trueCrossLinking = "";
        this.validCandidate = validCandidate;
        this.candidateTotal = candidateTotal;
    }

    public int getCandidateTotal() {
        return candidateTotal;
    }

    public void setCandidateTotal(int candidateTotal) {
        this.candidateTotal = candidateTotal;
    }

    
    public String getModLocationAlpha() {
        return modLocationAlpha;
    }

    public void setModLocationAlpha(String modLocationAlpha) {
        this.modLocationAlpha = modLocationAlpha;
    }

    public String getModNameAlpha() {
        return modNameAlpha;
    }

    public void setModNameAlpha(String modNameAlpha) {
        this.modNameAlpha = modNameAlpha;
    }

    public String getModLocationBeta() {
        return modLocationBeta;
    }

    public void setModLocationBeta(String modLocationBeta) {
        this.modLocationBeta = modLocationBeta;
    }

    public String getModNameBeta() {
        return modNameBeta;
    }

    public void setModNameBeta(String modNameBeta) {
        this.modNameBeta = modNameBeta;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public int getXlinkPos1() {
        return xlinkPos1;
    }

    public void setXlinkPos1(int xlinkPos1) {
        this.xlinkPos1 = xlinkPos1;
    }

    public int getXlinkPos2() {
        return xlinkPos2;
    }

    public void setXlinkPos2(int xlinkPos2) {
        this.xlinkPos2 = xlinkPos2;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getMH() {
        return ms1errPPM;
    }

    public void setMs1errPPM(double ms1errPPM) {
        this.ms1errPPM = ms1errPPM;
    }

    public double getExperimentalMZ() {
        return experimentalMZ;
    }

    public void setExperimentalMZ(double experimentalMZ) {
        this.experimentalMZ = experimentalMZ;
    }

    public double getpLinkScore() {
        return pLinkScore;
    }

    public void setpLinkScore(double pLinkScore) {
        this.pLinkScore = pLinkScore;
    }

    public double geteValue() {
        return eValue;
    }

    public void seteValue(double eValue) {
        this.eValue = eValue;
    }

    public double getAlphaEValue() {
        return alphaEValue;
    }

    public void setAlphaEValue(double alphaEValue) {
        this.alphaEValue = alphaEValue;
    }

    public double getBetaEValue() {
        return betaEValue;
    }

    public void setBetaEValue(double betaEValue) {
        this.betaEValue = betaEValue;
    }

    public double getMatchedIntensity() {
        return matchedIntensity;
    }

    public void setMatchedIntensity(double matchedIntensity) {
        this.matchedIntensity = matchedIntensity;
    }

    public double getUnMatchedIntensity() {
        return unMatchedIntensity;
    }

    public void setUnMatchedIntensity(double unMatchedIntensity) {
        this.unMatchedIntensity = unMatchedIntensity;
    }

    public double getXlinkedMass() {
        return xlinkedMass;
    }

    public void setMass(double mass) {
        this.xlinkedMass = mass;
    }

    public int getValidCandidate() {
        return validCandidate;
    }

    public void setValidCandidate(int validCandidate) {
        this.validCandidate = validCandidate;
    }
   

    public static final Comparator<PLinkResult> eValueASC
            = new Comparator<PLinkResult>() {
                @Override
                public int compare(PLinkResult o1, PLinkResult o2) {
                    return o1.geteValue() < o2.geteValue() ? -1 : o1.geteValue() == o2.geteValue() ? 0 : 1;
                }
            };

    public static final Comparator<PLinkResult> pLinkScoreDSC
            = new Comparator<PLinkResult>() {
                @Override
                public int compare(PLinkResult o1, PLinkResult o2) {
                    return o1.getpLinkScore() > o2.getpLinkScore() ? -1 : o1.getpLinkScore() == o2.getpLinkScore() ? 0 : 1;
                }
            };

    public static final Comparator<PLinkResult> pLinkScanDSC
            = new Comparator<PLinkResult>() {
                @Override
                public int compare(PLinkResult o1, PLinkResult o2) {
                    double scanNumA = Double.parseDouble(o1.getScanNumber()),
                    scanNumB = Double.parseDouble(o2.getScanNumber());

                    return scanNumA > scanNumA ? -1 : scanNumA == scanNumB ? 0 : 1;
                }
            };

    @Override
    public String toString() {
        return "PLinkResult{" + "modLocationAlpha=" + modLocationAlpha + ", modNameAlpha=" + modNameAlpha + ", modLocationBeta=" + modLocationBeta + ", modNameBeta=" + modNameBeta + ", charge=" + charge + ", xlinkPos1=" + xlinkPos1 + ", xlinkPos2=" + xlinkPos2 + ", intensity=" + intensity + ", ms1errPPM=" + ms1errPPM + ", experimentalMZ=" + experimentalMZ + ", pLinkScore=" + pLinkScore + ", eValue=" + eValue + ", alphaEValue=" + alphaEValue + ", betaEValue=" + betaEValue + ", matchedIntensity=" + matchedIntensity + ", unMatchedIntensity=" + unMatchedIntensity + ", xlinkedMass=" + xlinkedMass + '}';
    }

}
