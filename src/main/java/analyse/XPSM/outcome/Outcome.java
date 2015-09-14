/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;


/**
 * This class hold information from each Xlinking algorithms
 *
 * @author Sule
 */
public abstract class Outcome {

    protected String[] target_proteins;
    
    protected String accProteinA, // accession number of proteinA
            accProteinB, // accession number of proteinB
            
            peptideA,
            peptideB,
            
            label,
            
            target_decoy,
            trueCrossLinking,
            
            scanNumber,
            spectrumFileName,
            spectrumTitle  ;
    
    protected int crossLinkedSitePro1,
            crossLinkedSitePro2;
    
    
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

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
    }

    public String getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(String scanNumber) {
        this.scanNumber = scanNumber;
    }

    public String getSpectrumFileName() {
        return spectrumFileName;
    }

    public void setSpectrumFileName(String spectrumFileName) {
        this.spectrumFileName = spectrumFileName;
    }

    public String getTrueCrossLinking() {
        return trueCrossLinking;
    }

    public void setTrueCrossLinking(String trueCrossLinking) {
        this.trueCrossLinking = trueCrossLinking;
    }

    public String getTarget_decoy() {
        return target_decoy;
    }

    public void setTarget_decoy(String target_decoy) {
        this.target_decoy = target_decoy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String[] getTarget_proteins() {
        return target_proteins;
    }

    public void setTarget_proteins(String[] target_proteins) {
        this.target_proteins = target_proteins;
    }

    public String getAccProteinA() {
        return accProteinA;
    }

    public void setAccProteinA(String accProteinA) {
        this.accProteinA = accProteinA;
    }

    public String getAccProteinB() {
        return accProteinB;
    }

    public void setAccProteinB(String accProteinB) {
        this.accProteinB = accProteinB;
    }

    public int getCrossLinkedSitePro1() {
        return crossLinkedSitePro1;
    }

    public void setCrossLinkedSitePro1(int crossLinkedSitePro1) {
        this.crossLinkedSitePro1 = crossLinkedSitePro1;
    }

    public int getCrossLinkedSitePro2() {
        return crossLinkedSitePro2;
    }

    public void setCrossLinkedSitePro2(int crossLinkedSitePro2) {
        this.crossLinkedSitePro2 = crossLinkedSitePro2;
    }
}
