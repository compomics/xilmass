/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import java.io.IOException;

/**
 * This class holds cross linking site information from validated pLink results
 *
 * @author Sule
 */
public class PLinkResult extends Outcome {

    private String spectrumTitle,
            spectrumFileName,
            label,
            peptideA,
            alphaProtein,
            modLocationAlpha,
            modNameAlpha,
            peptideB,
            betaProtein,
            modLocationBeta,
            modNameBeta;
    private int charge,
            xlinkPos1,
            xlinkPos2;
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

      
    public PLinkResult(String spectrumTitle, String spectrumFileName, String label, 
            String peptideA, String alphaProtein, String modLocationAlpha, String modNameAlpha,
            String peptideB, String betaProtein, String modLocationBeta, String modNameBeta, 
            int charge, 
            int xlinkPos1, int xlinkPos2, 
            double intensity, double ms1errPPM,             double experimentalMZ, 
            double pLinkScore, double eValue, double alphaEValue, double betaEValue, 
            double matchedIntensity, double unMatchedIntensity, 
            double mass, String []target_names) {
        this.spectrumTitle = spectrumTitle;
        this.spectrumFileName = spectrumFileName;
        super.label = label;
        this.peptideA = peptideA;
        super.accessProteinA = alphaProtein;
        this.modLocationAlpha = modLocationAlpha;
        this.modNameAlpha = modNameAlpha;
        this.peptideB = peptideB;
        super.accessProteinB = betaProtein;
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
    }

    public String getSpectrumTitle() {
        return spectrumTitle;
    }

    public void setSpectrumTitle(String spectrumTitle) {
        this.spectrumTitle = spectrumTitle;
    }

    public String getSpectrumFileName() {
        return spectrumFileName;
    }

    public void setSpectrumFileName(String spectrumFileName) {
        this.spectrumFileName = spectrumFileName;
    }

    public String getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(String peptideA) {
        this.peptideA = peptideA;
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

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
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

    public String getAlphaProtein() {
        return alphaProtein;
    }

    public void setAlphaProtein(String alphaProtein) {
        this.alphaProtein = alphaProtein;
    }

    public String getBetaProtein() {
        return betaProtein;
    }

    public void setBetaProtein(String betaProtein) {
        this.betaProtein = betaProtein;
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

}
