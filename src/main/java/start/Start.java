/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import config.ConfigHolder;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import database.CreateDatabase;
import database.FASTACPDBLoader;
import database.WriteCXDB;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import matching.MatchAndScore;
import org.apache.log4j.Logger;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.FragmentationMode;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class Start {

    private static final Logger LOGGER = Logger.getLogger(Start.class);

    /**
     * @param args the command line arguments
     * @throws com.compomics.dbtoolkit.io.UnknownDBFormatException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws UnknownDBFormatException, IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException, Exception {

        LOGGER.info("Program starts! " + "\t");
        // STEP 1: DATABASE GENERATIONS! 
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                inSilicoPeptideDBName = ConfigHolder.getInstance().getString("inSilicoPeptideDBName"),
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameCache = cxDBName + "header_seq_mass_cxms.cache", // A cache file from already generated cross linked protein database
                dbFolder = ConfigHolder.getInstance().getString("folder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                enzymeFileName = ConfigHolder.getInstance().getString("enzymeFileName"),
                modsFileName = ConfigHolder.getInstance().getString("modsFileName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowMass = ConfigHolder.getInstance().getString("lowerMass"),
                highMass = ConfigHolder.getInstance().getString("higherMass"),
                mgfs = ConfigHolder.getInstance().getString("mgfs"),
                resultFile = ConfigHolder.getInstance().getString("resultFile"),
                fixedModificationNames = ConfigHolder.getInstance().getString("fixedModification"), // must be sepeared by semicolumn, lowercase, no space
                variableModificationNames = ConfigHolder.getInstance().getString("variableModification"),
                fragModeName = ConfigHolder.getInstance().getString("fragMode");
        ArrayList<String> fixedModifications = getModificationsName(fixedModificationNames),
                variableModifications = getModificationsName(variableModificationNames);
        FragmentationMode fragMode = null;

        if (fragModeName.equals("CID")) {
            fragMode = FragmentationMode.CID;
        } else if (fragModeName.equals("HCD")) {
            fragMode = FragmentationMode.HCD;
        } else if (fragModeName.equals("ETD")) {
            fragMode = FragmentationMode.ETD;
        }
        // Importing PTMs, so getting a PTMFactory object 
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(new File(modsFileName), false);
        int minLen = ConfigHolder.getInstance().getInt("minLen"),
                maxLen_for_combined = ConfigHolder.getInstance().getInt("maxLenCombined"),
                scoring = ConfigHolder.getInstance().getInt("scoring"),
                intensity_option = ConfigHolder.getInstance().getInt("intensityOptionMSAmanda"),
                minFPeakNum = ConfigHolder.getInstance().getInt("minimumFiltedPeaksNumber"),
                maxFPeakNum = ConfigHolder.getInstance().getInt("maximumFiltedPeaksNumber");
        boolean does_link_to_itself = false,
                isLabeled = ConfigHolder.getInstance().getBoolean("isLabeled"),
                isBranching = ConfigHolder.getInstance().getBoolean("isBranching");
        // Parameters for searching against experimental spectrum 
        double ms2Err = ConfigHolder.getInstance().getDouble("ms2Err"), //Fragment tolerance - mz diff
                ms1Err = ConfigHolder.getInstance().getDouble("ms1Err"), // Precursor tolerance - ppm error 
                massWindow = ConfigHolder.getInstance().getDouble("massWindow");    // mass window to make window on a given MSnSpectrum for FILTERING 
        boolean isPPM = ConfigHolder.getInstance().getBoolean("isMS1PPM"); // Relative or absolute precursor tolerance 
        CrossLinker linker = GetCrossLinker.getCrossLinker(crossLinkerName, isLabeled); // Required for constructing theoretical spectra

        LOGGER.info("CX database is checking!");

        // This part of the code makes sure that an already generated CXDB is not constructed again..
        HashMap<String, String> header_sequence = new HashMap<String, String>();
        HashMap<CPeptides, Double> cPeptide_TheoreticalMass = null;
        File settings = new File("settings.txt");
        boolean isSame = areTheSameDBSettings(settings); // either the same/different or absent;

        if (isSame) {
            for (File f : new File(dbFolder).listFiles()) {
                if (f.getName().endsWith(".fastacp")) {
                    File cxDBFile = new File(cxDBName + ".fastacp");
                    LOGGER.info("An already constrcuted fastacp file is found! The name=" + new File(cxDBName + ".fastacp").getName());
                    // Read a file 
                    header_sequence = getHeaderSequence(cxDBFile);
                    cPeptide_TheoreticalMass = FASTACPDBLoader.getCPeptide_TheoreticalMass(new File(cxDBNameCache), ptmFactory, fixedModifications, variableModifications, linker, fragMode, isBranching);
                }
            }
        } else {
            // So, file is empty.. 
            // first write a settings file
            writeSettings(settings);
            LOGGER.info("A constructed fastacp file is NOT found! It is going to be constructed..");
            CreateDatabase instance = new CreateDatabase(givenDBName, inSilicoPeptideDBName, cxDBName, // db related parameters
                    crossLinkerName, crossLinkedProteinTypes, // crossLinker related parameters
                    enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                    lowMass, highMass, // filtering of in silico peptides on peptide masses
                    minLen, maxLen_for_combined, does_link_to_itself, isLabeled);
            header_sequence = instance.getHeader_sequence();
            cPeptide_TheoreticalMass = FASTACPDBLoader.getCPeptide_TheoreticalMass(new File(cxDBNameCache), header_sequence, ptmFactory, fixedModifications, variableModifications, linker, fragMode, isBranching);
            // If necessary, and crossLinked database is not constructed..
            WriteCXDB.writeCXDB(header_sequence, cxDBName);
        }

        LOGGER.info("CX database is ready! ");
        LOGGER.info("Header and sequence object is ready! Total size is" + header_sequence.size());

        // STEP 2: CONSTRUCT CPEPTIDE OBJECTS
        ArrayList<Double> theoretical_masses = new ArrayList<Double>();
        ArrayList<CPeptides> cpeptides = new ArrayList<CPeptides>();
        for (CPeptides cPeptide : cPeptide_TheoreticalMass.keySet()) {
            double theoreticalMass = cPeptide_TheoreticalMass.get(cPeptide);
            theoretical_masses.add(theoreticalMass);
            cpeptides.add(cPeptide);
        }

        // STEP 3: MATCH AGAINST THEORETICAL SPECTRUM
        // Get all MSnSpectrum! MS2 spectra
        LOGGER.info("Getting experimental spectra and calculating PCXMs");
        BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
        String fileTitle = "SpectrumIndex" + "\t" + "SpectrumFile" + "\t" + "MSnSpectrumTitle" + "\t"
                + "PrecursorMZ" + "\t" + "PrecursorCharge" + "\t" + "PrecursorMass" + "\t" + "TheoreticalMass" + "\t" + "MS1Err(PPM)" + "\t"
                + "ScoringFunction" + "\t" + "Score" + "\t"
                + "ProteinA" + "\t" + "ProteinB" + "\t" + "PeptideSequenceA" + "\t" + "PeptideSequenceB" + "\t"
                + "ModificationPeptideA" + "\t" + "ModificationPeptideB" + "\t"
                + "linkerPositionOnPeptideA" + "\t" + "linkerPositionOnPeptideB" + "\t" + "#MatchedPeaks" + "\t" + "#MatchedTheoreticalPeaks" + "\t" + "MatchedPeakList" + "\t" + "TheoreticalPeakList" + "\n";
        bw.write(fileTitle);

        File ms2spectra = new File(mgfs);
        int num = 0;
        // Maybe MSnSpectrum with PMs
        HashMap<Double, MSnSpectrum> precursorMass_MSnSpectrum = new HashMap<Double, MSnSpectrum>();
        // Or maybe just arraylist sorted by precursor mass values        
        for (File mgf : ms2spectra.listFiles()) {
            if (mgf.getName().endsWith("mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    num++;
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    precursorMass_MSnSpectrum.put(ms.getPrecursor().getMass(1), ms);
                    // PREPARE MSnSPECTRUM object no<w 
                    ArrayList<Charge> possibleCharges = ms.getPrecursor().getPossibleCharges();
                    Charge charge = possibleCharges.get(possibleCharges.size() - 1);
                    String specInfo = (num + "\t" + mgf.getName() + "\t" + ms.getSpectrumTitle() + "\t" + ms.getPrecursor().getMz() + "\t" + ms.getPrecursor().getPossibleChargesAsString() + "\t");
                    int charge_value = charge.value;
                    double precursor_mass = ms.getPrecursor().getMass(charge_value);
                    String peptideA,
                            peptideB,
                            proteinA,
                            proteinB ,
                            modificationA ,
                            modificationB;
                    CPeptides tmpCpeptide = null;
//                    MatchAndScore f = new MatchAndScore(ms, scoring, tmpCpeptide, ms2Err, intensity_option); // MSRobin -0, Andromeda-1, TheMSRobin-2

                    for (int i = 0; i < theoretical_masses.size(); i++) {
                        Double theoretical_mass = theoretical_masses.get(i);
                        double tmpMS1Err = CalculateMS1Err.getMS1Err(isPPM, theoretical_mass, precursor_mass);
                        if (tmpMS1Err <= ms1Err) {
                            // set a temporary xlinked peptide object
                            tmpCpeptide = cpeptides.get(i);
                            MatchAndScore f = new MatchAndScore(ms, scoring, tmpCpeptide, ms2Err, intensity_option, minFPeakNum, maxFPeakNum, massWindow); // MSAmandad -0, Andromedad-1, TheoMSAmandad-2

                            double psmscore = f.getCXPSMScore();

                            peptideA = tmpCpeptide.getPeptideA().getSequence();
                            peptideB = tmpCpeptide.getPeptideB().getSequence();
                            proteinA = tmpCpeptide.getProteinA();
                            proteinB = tmpCpeptide.getProteinB();
                            modificationA = getModificationInfo(tmpCpeptide.getPeptideA());
                            modificationB = getModificationInfo(tmpCpeptide.getPeptideB());

                            int linkerPositionOnPeptideA = tmpCpeptide.getLinker_position_on_peptideA() + 1,
                                    linkerPositionOnPeptideB = tmpCpeptide.getLinker_position_on_peptideB() + 1;

                            // analyze matchedPeaks!
                            HashSet<Peak> matchedPeaks = f.getMatchedPeaks();
                            HashSet<CPeptidePeak> matchedCTheoPeaks = f.getMatchedTheoreticalCPeaks();

                            ArrayList<Peak> matchedPLists = new ArrayList<Peak>(matchedPeaks);
                            Collections.sort(matchedPLists, Peak.ASC_mz_order);
                            ArrayList<CPeptidePeak> matchedCTheoPLists = new ArrayList<CPeptidePeak>(matchedCTheoPeaks);
                            Collections.sort(matchedCTheoPLists, CPeptidePeak.Peak_ASC_mz_order);

                            String runningInfo = (precursor_mass + "\t" + theoretical_mass + "\t" + tmpMS1Err + "\t"
                                    + getScoringStr(scoring) + "\t" + psmscore + "\t"
                                    + proteinA + "\t" + proteinB + "\t" + peptideA + "\t" + peptideB + "\t"
                                    + modificationA + "\t" + modificationB + "\t"
                                    + linkerPositionOnPeptideA + "\t" + linkerPositionOnPeptideB + "\t"
                                    + matchedPeaks.size() + "\t" + matchedCTheoPeaks.size() + "\t");

                            bw.write(specInfo + runningInfo);

                            for (Peak p : matchedPLists) {
                                bw.write(p.mz + " ");
                            }
                            bw.write("\t");

                            for (CPeptidePeak tmpCPeak : matchedCTheoPLists) {
                                bw.write(tmpCPeak.toString() + " ");
                            }
                            bw.write("\t");
                            bw.newLine();
                        }
                    }
                }
            }
        }
        LOGGER.info("Cross linked database search is done!");
        bw.close();
    }

    /**
     * This method reads a given cross linked database to generate
     * header/sequence pairs
     *
     * @param cxDBFile a constructed cross-linked database
     *
     * @return
     * @throws IOException
     */
    private static HashMap<String, String> getHeaderSequence(File cxDBFile) throws IOException {
        HashMap<String, String> headerAndSequence = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new FileReader(cxDBFile));
        String line = "",
                header = "",
                sequence = "";
        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                // so this is a header
                header = line.substring(1);
                sequence = "";
            } else {
                sequence = line;
                headerAndSequence.put(header, sequence);
            }
        }
        return headerAndSequence;
    }

    /**
     * This method returns a String name for a selected scoring function to
     * write on an output file
     *
     * @param scoring
     * @return
     */
    private static String getScoringStr(int scoring) {
        String scoreName = "MSAmandaD";
        if (scoring == 1) {
            scoreName = "AndromedaD";
        } else if (scoring == 2) {
            scoreName = "TheoMSAmandaD";
        }
        return scoreName;
    }

    /**
     * This method converts a given list of modification names to a an arraylist
     * (Note that all given PTMs are considered being separated by semicolumn)
     *
     * @param ptmNames
     * @return
     */
    private static ArrayList<String> getModificationsName(String ptmNames) {
        ArrayList<String> mods = new ArrayList<String>();
        if (!ptmNames.isEmpty()) {
            String[] fixed_modifications_name_split = ptmNames.split(";");
            for (String fixed_modification_name : fixed_modifications_name_split) {
                mods.add(fixed_modification_name);
            }
        }
        return mods;
    }

    /**
     * This method write a settings containing input/output information on a
     * "settings.txt" file
     *
     * @param file
     * @throws IOException
     */
    private static void writeSettings(File file) throws IOException {
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                inSilicoPeptideDBName = ConfigHolder.getInstance().getString("inSilicoPeptideDBName"),
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameCache = cxDBName + "header_seq_mass_cxms.cache", // A cache file from already generated cross linked protein database
                dbFolder = ConfigHolder.getInstance().getString("folder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                enzymeFileName = ConfigHolder.getInstance().getString("enzymeFileName"),
                modsFileName = ConfigHolder.getInstance().getString("modsFileName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowMass = ConfigHolder.getInstance().getString("lowerMass"),
                highMass = ConfigHolder.getInstance().getString("higherMass"),
                mgfs = ConfigHolder.getInstance().getString("mgfs"),
                resultFile = ConfigHolder.getInstance().getString("resultFile"),
                fixedModificationNames = ConfigHolder.getInstance().getString("fixedModification"), // must be sepeared by semicolumn, lowercase, no space
                variableModificationNames = ConfigHolder.getInstance().getString("variableModification"),
                fragModeName = ConfigHolder.getInstance().getString("fragMode");
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("Settings file" + "\n");
        bw.write("Running date=" + new Date().toString() + "\n");
        bw.write("givenDBName" + "\t" + givenDBName + "\n");
        bw.write("inSilicoPeptideDBName" + "\t" + inSilicoPeptideDBName + "\n");
        bw.write("cxDBName" + "\t" + cxDBName + "\n");
        bw.write("cxDBNameCache" + "\t" + cxDBNameCache + "\n");
        bw.write("dbFolder" + "\t" + dbFolder + "\n");
        bw.write("crossLinkerName" + "\t" + crossLinkerName + "\n");
        bw.write("crossLinkedProteinTypes" + "\t" + crossLinkedProteinTypes + "\n");
        bw.write("enzymeName" + "\t" + enzymeName + "\n");
        bw.write("enzymeFileName" + "\t" + enzymeFileName + "\n");
        bw.write("modsFileName" + "\t" + modsFileName + "\n");
        bw.write("misclevaged" + "\t" + misclevaged + "\n");
        bw.write("lowMass" + "\t" + lowMass + "\n");
        bw.write("highMass" + "\t" + highMass + "\n");
        bw.write("mgfs" + "\t" + mgfs + "\n");
        bw.write("resultFile" + resultFile + "\t" + "\n");
        bw.write("fixedModificationNames" + "\t" + fixedModificationNames + "\n");
        bw.write("variableModificationNames" + "\t" + variableModificationNames + "\n");
        bw.write("fragModeName" + "\t" + fragModeName + "\n");
        bw.close();
    }

    /**
     * This method checks if a database construction is the same as before.
     *
     * @param paramFile a parameter/setting file which contains all input and
     * output information
     * @return
     * @throws IOException
     */
    public static boolean areTheSameDBSettings(File paramFile) throws IOException {
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                dbFolder = ConfigHolder.getInstance().getString("folder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowMass = ConfigHolder.getInstance().getString("lowerMass"),
                highMass = ConfigHolder.getInstance().getString("higherMass");
        boolean isSame = true;
        BufferedReader br = new BufferedReader(new FileReader(paramFile));
        String line = "";
        int lineNumber = 0;
        while ((line = br.readLine()) != null) {
            lineNumber++;
            if ((line.startsWith("givenDBName")) && (!line.split("\t")[1].equals(givenDBName))) {
                return false;
            } else if ((line.startsWith("dbFolder")) && (!line.split("\t")[1].equals(dbFolder))) {
                return false;
            } else if ((line.startsWith("crossLinkerName")) && (!line.split("\t")[1].equals(crossLinkerName))) {
                return false;
            } else if ((line.startsWith("crossLinkedProteinTypes")) && (!line.split("\t")[1].equals(crossLinkedProteinTypes))) {
                return false;
            } else if ((line.startsWith("enzymeName")) && (!line.split("\t")[1].equals(enzymeName))) {
                return false;
            } else if ((line.startsWith("misclevaged")) && (!line.split("\t")[1].equals(misclevaged))) {
                return false;
            } else if ((line.startsWith("lowMass")) && (!line.split("\t")[1].equals(lowMass))) {
                return false;
            } else if ((line.startsWith("highMass")) && (!line.split("\t")[1].equals(highMass))) {
                return false;
            }
        }
        if (lineNumber == 0) {
            isSame = false;
        }
        return isSame;
    }

    /**
     * This method returns modification info derived from a given peptide
     *
     * @param peptide
     * @return
     */
    private static String getModificationInfo(Peptide peptide) {
        ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
        String info = "";
        for (int i = 0; i < modificationMatches.size(); i++) {
            ModificationMatch m = modificationMatches.get(i);
            String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
            info += tmp + ";";
        }
        return info;
    }

}
