/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM;

import analyse.CXPSM.prepareOutcome.*;
import com.compomics.util.experiment.identification.SequenceFactory;
import config.ConfigHolder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;

/**
 * This class selects the best scored XPSM for each spectrum.
 *
 * @author Sule
 */
public class NameTargetDecoy {

    /**
     *
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, IllegalArgumentException, InterruptedException, ClassNotFoundException, ConfigurationException {

        File xilmassResFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("xilmass.results.elite")),
                // Xwalk predicted and manullay curated cross linking sites
                prediction = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("prediction")),
                // The validated PSM list from contaminants
                psms_contamination = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("psms.contaminant.elite")),
                pLinkCombValidatedFile = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.validated.file.elite")),
                pLinkFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.folder.elite")),
                pLinkAllOutput = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLinkalloutput.elite")),
                percolatorKojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.kojak.elite")),
                percolatorXilmassFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.xilmass.elite")),
                kojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("kojak.folder.elite")),
                database = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("database.file"));
        // get every protein entry on database with their accession numbers
        HashMap<String, String> accs = getAccs(database);
        String proteinA = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinA"),
                proteinB = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinB");
        String[] protein_names = {proteinA, proteinB};
        boolean isElite = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("is.analyzing.elite"),
                isPITFDR = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("is.PIT.FDR"),
                isMS1ErrPPM = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("is.ms1Err.ppm"),
                doesContainCPeptidePattern = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("doesContainCPeptidePattern"),
                doesContainIonWeight = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("doesContainIonWeight");
        double qvalue = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("qvalue"),
                fdr_cutoff = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("fdr");
        int analysis = ConfigHolder.getTargetDecoyAnalyzeInstance().getInt("analysis"); // 1-Kojak/2-AllPLink 3-ValPLink 4-Xilmass 5-PercolatorKojak runs! 6-Percolator-Xilmass runs

        if (!isElite) {
            psms_contamination = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("psms.contaminant.qexactive"));
            xilmassResFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("xilmass.results.qexactive"));
            pLinkFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.folder.qexactive"));
            pLinkAllOutput = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLinkalloutput.qexactive"));
            kojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("kojak.folder.qexactive"));
            pLinkCombValidatedFile = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.validated.file.qexactive"));
            percolatorKojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.kojak.qexactive"));
            percolatorXilmassFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.xilmass.qexactive"));
        }

        File output = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("output"));

        // NOW RUN!!!
        AnalyzeOutcomes o = null;
        switch (analysis) {
            case 0:
                o = new AnalyzeXilmass(xilmassResFolder, output, prediction, psms_contamination, protein_names, fdr_cutoff, isPITFDR, isMS1ErrPPM, doesContainCPeptidePattern, doesContainIonWeight);
                break;
            case 1:
                o = new AnalyzeKojak(output, kojakFolder, prediction, psms_contamination, database, protein_names, fdr_cutoff, isPITFDR);
                break;
            case 2:
                o = new AnalyzePLink(pLinkFolder, pLinkAllOutput, output, prediction, psms_contamination, protein_names, fdr_cutoff, isPITFDR);
                break;
            case 3:
                o = new AnalyzePLinkValidatedResult(pLinkCombValidatedFile, output, prediction, psms_contamination, protein_names);
                break;
            case 4:
                o = new AnalyzePercolator(output, percolatorXilmassFolder, prediction, psms_contamination, protein_names, accs, true, qvalue);
                break;
            case 5:
                o = new AnalyzePercolator(output, percolatorKojakFolder, prediction, psms_contamination, protein_names, accs, qvalue);
                break;
        }
        o.run();
    }

    /**
     * This method decides if there is a2/b2 pair on given theoretical peak from
     * either proteinA (isProteinA=true) or proteinB (isProteinA=false)
     *
     * @param theoreticalPeaks
     * @param isProteinA
     * @return
     */
    private static boolean search_pairs(String theoreticalPeaks, boolean isProteinA) {
        boolean hasPairs = false;
        int a2 = 0,
                b2 = 0;
        String[] split = theoreticalPeaks.split(" ");
        for (String st : split) {
            String[] st_split = st.split("_");
            for (int i = 0; i < st_split.length - 1; i++) {
                if (st_split[i].equals("pepA") && st_split[i + 1].equals("a2") && isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepA") && st_split[i + 1].equals("b2") && isProteinA) {
                    b2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("a2") && !isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("b2") && !isProteinA) {
                    b2++;
                }
            }
        }
        if (a2 > 0 && b2 > 0) {
            hasPairs = true;
        }
        return hasPairs;
    }

    public static HashMap<String, String> getAccs(File fasta) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        HashMap<String, String> acc_seq = new HashMap<String, String>();
        SequenceFactory fct = SequenceFactory.getInstance();
        System.out.println("A fasta file is being loaded..");
        fct.loadFastaFile(fasta);
        System.out.println("Accessions are being retrieved..");
        Set<String> accession_original_db = fct.getAccessions();
        for (String acc : accession_original_db) {
            String proSeq = fct.getProtein(acc).getSequence();
            acc_seq.put(acc, proSeq);
        }
        return acc_seq;
    }

}
