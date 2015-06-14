/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.protein.Protein;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class holds all written information on Kojak result file
 *
 * @author Sule
 */
public class KojakResult extends Outcome {

    private int charge;

    private double obsMass,
            psms_mass,
            ppmErr,
            score,
            dScore,
            linkerMass,
            pepDiff;
    private String scanNumber,
            spectrumFileName,
            peptide1,
            peptide2,
            modPeptide1,
            modPeptide2;
    private File fasta = new File("C:/Users/Sule/Documents/PhD/XLinked/databases/cam_plectin_equal_2Pfus.fasta");
    private ArrayList<Protein> proteins = new ArrayList<Protein>();

    public KojakResult(String scanNumber, double obsMass, int charge, double psm_mass, double ppmErr, double score, double dScore, double pepDiff,
            String peptide1, int link1, String protein1, String peptide2, int link2, String protein2, double linkerMass, String[] protein_names, boolean hasTraditionalDecoy) throws IOException {
        this.scanNumber = scanNumber;
        this.obsMass = obsMass;
        this.charge = charge;
        this.psms_mass = psm_mass;
        this.ppmErr = ppmErr;
        this.score = score;
        this.dScore = dScore;
        this.pepDiff = pepDiff;
        this.modPeptide1 = peptide1;
        super.crossLinkedSitePro1 = link1;
        this.accessProteinA = protein1;
        this.modPeptide2 = peptide2;
        super.crossLinkedSitePro2 = link2;
        super.accessProteinB = protein2;
        this.linkerMass = linkerMass;
        this.target_proteins = protein_names;
        label = "light";
        if (Math.abs(linkerMass - 150.143404) < 0.01 || Math.abs(linkerMass - 168.15393) < 0.01) {
            label = "heavy";
        }
        DBLoader loader = DBLoaderLoader.loadDB(fasta);
        Protein protein = null;
        // get a crossLinkerName object        
        while ((protein = loader.nextProtein()) != null) {
            proteins.add(protein);
        }
        // make sure about proteinAccessions...
        this.peptide1 = getSeqNoMod(peptide1);
        this.peptide2 = getSeqNoMod(peptide2);

        for (Protein p : proteins) {
            if (p.getHeader().getAccession().equals(accessProteinA)) {
                crossLinkedSitePro1 += p.getSequence().getSequence().indexOf(this.peptide1);
            }
            if (p.getHeader().getAccession().equals(accessProteinB)) {
                crossLinkedSitePro2 += p.getSequence().getSequence().indexOf(this.peptide2);
            }
        }
        this.hasTraditionalDecoy = hasTraditionalDecoy;
    }

    public String getModPeptide1() {
        return modPeptide1;
    }

    public void setModPeptide1(String modPeptide1) {
        this.modPeptide1 = modPeptide1;
    }

    public String getModPeptide2() {
        return modPeptide2;
    }

    public void setModPeptide2(String modPeptide2) {
        this.modPeptide2 = modPeptide2;
    }

    /**
     * Returns a peptide sequence without modification on a string itself
     *
     * @param pep
     * @return
     */
    public String getSeqNoMod(String pep) {
        String tmpPep = "";
        boolean control = false;
        for (int i = 0; i < pep.length(); i++) {
            char tmpCh = pep.charAt(i);
            if (tmpCh == '[') {
                control = true;
            }
            if (!control) {
                tmpPep += tmpCh;
            } else if (tmpCh == ']') {
                control = false;
            }
        }
        return tmpPep;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public double getObsMass() {
        return obsMass;
    }

    public void setObsMass(double obsMass) {
        this.obsMass = obsMass;
    }

    public double getPsms_mass() {
        return psms_mass;
    }

    public void setPsms_mass(double psms_mass) {
        this.psms_mass = psms_mass;
    }

    public double getPpmErr() {
        return ppmErr;
    }

    public void setPpmErr(double ppmErr) {
        this.ppmErr = ppmErr;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getdScore() {
        return dScore;
    }

    public void setdScore(double dScore) {
        this.dScore = dScore;
    }

    public double getLinkerMass() {
        return linkerMass;
    }

    public void setLinkerMass(double linkerMass) {
        this.linkerMass = linkerMass;
    }

    public double getPepDiff() {
        return pepDiff;
    }

    public void setPepDiff(double pepDiff) {
        this.pepDiff = pepDiff;
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

    public String getPeptide1() {
        return peptide1;
    }

    public void setPeptide1(String peptide1) {
        this.peptide1 = peptide1;
    }

    public String getPeptide2() {
        return peptide2;
    }

    public void setPeptide2(String peptide2) {
        this.peptide2 = peptide2;
    }

    public File getFasta() {
        return fasta;
    }

    public void setFasta(File fasta) {
        this.fasta = fasta;
    }

    public ArrayList<Protein> getProteins() {
        return proteins;
    }

    public void setProteins(ArrayList<Protein> proteins) {
        this.proteins = proteins;
    }

}
