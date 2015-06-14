/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This method combines all given files from each run on Kojak For each
 * spectrum, it keeps the highest score one. If there is one spectrum with more
 * than one cross linking site with the highest score, it keeps both of these
 *
 * @author Sule
 */
public class AnalyzeKojak extends AnalyzeOutcomes {

    private File output,
            kojakResultFolder;

    public AnalyzeKojak(File output, File kojakResultFolder, File prediction_file, File psms_contaminant, String[] target_names) {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        this.output = output;
        this.kojakResultFolder = kojakResultFolder;
        super.psms_contaminant = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/1_Cleaning/SGRunsHCD_identification/velos_orbitra_elite/hcd_orbi_elite_crap_10psmFDR_psms_validated.txt");
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, ArrayList<KojakResult>> kojakResults = new HashMap<String, ArrayList<KojakResult>>();
        HashMap<String, Double> kojakScores = new HashMap<String, Double>();
        boolean wasTitleWritten = false;
        HashMap<String, HashSet<String>> contaminant_MSMSMap = super.getContaminant_MSMSMap();
        for (File f : kojakResultFolder.listFiles()) {
            if (f.getName().endsWith(".txt")) {
                System.out.println("My name is " + "\t" + f.getName());
                String mgfFileName = f.getName().substring(0, f.getName().indexOf("_kojak")) + ".mgf";
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("Kojak") && !line.startsWith("Scan")) {
                        String[] sp = line.split("\t");
                        String scanNumber = sp[0],
                                peptide1 = sp[8],
                                protein1 = sp[10],
                                peptide2 = sp[11],
                                protein2 = sp[13];

                        double obsMass = Double.parseDouble(sp[1]),
                                psmMass = Double.parseDouble(sp[3]),
                                ppmErr = Double.parseDouble(sp[4]),
                                score = Double.parseDouble(sp[5]),
                                dScore = Double.parseDouble(sp[6]),
                                pepDiff = Double.parseDouble(sp[7]),
                                linkerMass = Double.parseDouble(sp[14]);
                        // So making sure that it is indeed a cross linked one..
                        if (!peptide1.equals("-") && !peptide2.equals("-")) {
                            int charge = Integer.parseInt(sp[2]),
                                    link1 = Integer.parseInt(sp[9]),
                                    link2 = Integer.parseInt(sp[12]);
                            // just keep cross linked pairs                    
                            if (link1 != -1 && link2 != -1) {
                                protein1 = protein1.split("\\|")[1];
                                protein2 = protein2.split("\\|")[1];
                                boolean isContaminantDerived = false;
                                String index = mgfFileName + "AND" + scanNumber;
                                KojakResult kr = new KojakResult(scanNumber, obsMass, charge, psmMass, ppmErr, score, dScore,
                                        pepDiff, peptide1, link1, protein1, peptide2, link2, protein2, linkerMass, target_names, hasTraditionalDecoy);
                                if (contaminant_MSMSMap.containsKey(mgfFileName)) {
                                    for (String tmpsScans : contaminant_MSMSMap.get(mgfFileName)) {
                                        if (tmpsScans.equals(scanNumber)) {
                                            isContaminantDerived = true;
                                        }
                                    }
                                }
                                if (!isContaminantDerived && kojakScores.containsKey(index)) {
                                    double storedKojakScore = kojakScores.get(index);
                                    // stored one has a smaller dScore, so remove it
                                    if (storedKojakScore < score) {
                                        // remove this last one
                                        kojakResults.remove(index);
                                        ArrayList<KojakResult> tmpKjks = new ArrayList<KojakResult>();
                                        tmpKjks.add(kr);
                                        kojakResults.put(index, tmpKjks);
                                    }
                                    // just add another one, since they have similar score
                                    if (storedKojakScore == score) {
                                        kojakResults.get(index).add(kr);
                                    }
                                } else if (!isContaminantDerived) {
                                    kojakScores.put(index, score);
                                    ArrayList<KojakResult> tmpKjks = new ArrayList<KojakResult>();
                                    tmpKjks.add(kr);
                                    kojakResults.put(index, tmpKjks);
                                }
                            }
                        }
                    } else if (line.startsWith("Scan") && !wasTitleWritten) {
                        wasTitleWritten = true;
                        bw.write("SpectrumFile" + "\t" + line + "\t" + "labeled" + "\t" + "Target_Decoy+" + "\t" + "Predicted" + "\t" + "Euclidean_distance_Alpha(A)" + "\t" + "Euclidean_distance_Beta(A)" + "\n");
                    }
                }
            }
        }
        // now write them down
        for (String index : kojakResults.keySet()) {
            String tmpMGF = index.substring(0, index.indexOf("AND"));
            for (KojakResult kj : kojakResults.get(index)) {
                bw.write(tmpMGF + "\t" + kj.getScanNumber() + "\t" + kj.getObsMass() + "\t" + kj.getCharge() + "\t" + kj.getPsms_mass() + "\t" + kj.getPpmErr() + "\t" + kj.getScore() + "\t"
                        + kj.getdScore() + "\t" + kj.getPepDiff() + "\t" + kj.getPeptide1() + "\t" + kj.getCrossLinkedSitePro1() + "\t" + kj.getAccessProteinA() + "\t"
                        + kj.getPeptide2() + "\t" + kj.getCrossLinkedSitePro2() + "\t" + kj.getAccessProteinB() + "\t" + kj.getLinkerMass() + "\t" + kj.getLabel() + "\t"
                        + kj.getTargetDecoy() + "\t" + assetTrueLinking(kj.accessProteinA, kj.accessProteinB, kj.crossLinkedSitePro1, kj.crossLinkedSitePro2) + "\n");
            }
        }
        bw.close();
    }

}
