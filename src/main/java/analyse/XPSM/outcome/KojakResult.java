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
import java.util.Comparator;

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
            modPeptide2,
            trueCrossLinking = "", // info for cross-linking info (decision/euclidean distances)
            target_decoy = ""; // TT/TD or DD

    private File fasta;

    private ArrayList<Protein> proteins = new ArrayList<Protein>();

    public KojakResult(String spectrumFileName, String scanNumber, double obsMass, int charge, double psm_mass, double ppmErr, double score, double dScore, double pepDiff,
            String peptide1, int link1, String protein1, String peptide2, int link2, String protein2, double linkerMass, String[] protein_names,
            File databaseFile) throws IOException {
        this.spectrumFileName = spectrumFileName;
        this.scanNumber = scanNumber;
        this.obsMass = obsMass;
        this.charge = charge;
        this.psms_mass = psm_mass;
        this.ppmErr = ppmErr;
        this.score = score;
        this.dScore = dScore;
        this.pepDiff = pepDiff;
        this.modPeptide1 = getMod(peptide1);
        super.crossLinkedSitePro1 = link1;
        this.accessProteinA = protein1;
        this.modPeptide2 = getMod(peptide2);
        super.crossLinkedSitePro2 = link2;
        super.accessProteinB = protein2;
        this.linkerMass = linkerMass;
        this.target_proteins = protein_names;
        fasta = databaseFile;
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

        //>sp|Q15149|175-400PlectinABDisoform1a(FromJakeSong)(33); - add 33 to the link..
        for (Protein p : proteins) {
            if (p.getHeader().getAccession().equals(accessProteinA)) {
                crossLinkedSitePro1 += p.getSequence().getSequence().indexOf(this.peptide1);
            }
            if (p.getHeader().getAccession().equals(accessProteinB)) {
                crossLinkedSitePro2 += p.getSequence().getSequence().indexOf(this.peptide2);
            }
        }

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

    /**
     * Returns a peptide sequence without modification on a string itself
     *
     * @param pep
     * @return
     */
    public String getMod(String pep) {
        String tmpPep = "";
        boolean control = false;
        for (int i = 0; i < pep.length(); i++) {
            char tmpCh = pep.charAt(i);
            if (tmpCh == '[') {
                control = true;
            }
            if (control) {
                if (tmpCh == ']') {
                    control = false;
                } else {
                    tmpPep += tmpCh;
                }
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

    public String toPrint() {

        String title = "SpectrumFile" + "\t" + "ScanNr" + "\t"
                + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t"
                + "PSM_Mass" + "\t"
                + "Score" + "\t" + "dScore" + "\t" + "PepDiff" + "\t"
                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t" + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
                + "TargetDecoy" + "\t"
                + "LinkerLabeling" + "\t"
                + "Predicted" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)";

        String res = spectrumFileName + "\t" + scanNumber + "\t"
                + obsMass + "\t" + charge + "\t"
                + psms_mass + "\t"
                + score + "\t" + dScore + "\t" + pepDiff + "\t"
                + peptide1 + "\t" + accessProteinA + "\t" + modPeptide1 + "\t"
                + peptide2 + "\t" + accessProteinB + "\t" + modPeptide2 + "\t"
                + "-" + "\t" + "-" + "\t"
                + super.crossLinkedSitePro1 + "\t" + super.crossLinkedSitePro2 + "\t"
                + target_decoy + "\t"
                + label + "\t"
                + trueCrossLinking;

        return res;

    }

    public static final Comparator<KojakResult> ScoreDSC
            = new Comparator<KojakResult>() {
                @Override
                public int compare(KojakResult o1, KojakResult o2) {
                    return o1.getScore() > o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            };

}
