/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import com.compomics.util.protein.Protein;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import multithread.score.Result;
import multithread.score.Score;
import naming.DefineIdCPeptideFragmentationPattern;
import org.apache.log4j.Logger;
import scoringFunction.ScoreName;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinkedPeptides;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;
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
        HashSet<File> indexFiles = new HashSet<File>();
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta",
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameIndexFile = cxDBName + ".index", // An index file from already generated cross linked protein database
                monoLinkFile = cxDBName + "_monoLink.index",
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
                fragModeName = ConfigHolder.getInstance().getString("fragMode"),
                scoring = ConfigHolder.getInstance().getString("scoring");
        // get Fragmenteation enum...
        FragmentationMode fragMode = null;
        if (fragModeName.equals("CID")) {
            fragMode = FragmentationMode.CID;
        } else if (fragModeName.equals("HCD")) {
            fragMode = FragmentationMode.HCD;
        } else if (fragModeName.equals("ETD")) {
            fragMode = FragmentationMode.ETD;
        } else if (fragModeName.equals("HCD_all")) {
            fragMode = FragmentationMode.HCD_all;
        }
        // get ScoreName enum..
        ScoreName scoreName = ScoreName.AndromedaD;
        if (scoring.equals("MSAmandaDerived")) {
            scoreName = ScoreName.MSAmandaD;
        } else if (scoring.equals("TheoMSAmandaDerived")) {
            scoreName = ScoreName.TheoMSAmandaD;
        } else if (scoring.equals("AndromedaWeightedDerived")) {
            scoreName = ScoreName.AndromedaDWeighted;
        } else if (scoring.equals("TheoMSAmandaWeightedDerived")) {
            scoreName = ScoreName.TheoMSAmandaDWeighted;
        }
        // Get fixed modification and variable modification names...
        ArrayList<String> fixedModifications = getModificationsName(fixedModificationNames),
                variableModifications = getModificationsName(variableModificationNames);
        // Importing PTMs, so getting a PTMFactory object 
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(new File(modsFileName), false);
        int minLen = ConfigHolder.getInstance().getInt("minLen"),
                maxLen_for_combined = ConfigHolder.getInstance().getInt("maxLenCombined"),
                intensity_option = ConfigHolder.getInstance().getInt("intensityOptionMSAmanda"),
                minFPeakNumPerWindow = ConfigHolder.getInstance().getInt("minimumFiltedPeaksNumberForEachWindow"),
                maxFPeakNumPerWindow = ConfigHolder.getInstance().getInt("maximumFiltedPeaksNumberForEachWindow"),
                threadNum = ConfigHolder.getInstance().getInt("threadNumbers"),
                peakRequiredForImprovedSearch = ConfigHolder.getInstance().getInt("peakRequiredForImprovedSearch");

        // more cross linking option..;
        boolean does_link_to_itself = ConfigHolder.getInstance().getBoolean("doesLinkToItself_InterPeptide"),
                isLabeled = ConfigHolder.getInstance().getBoolean("isLabeled"),
                isBranching = ConfigHolder.getInstance().getBoolean("isBranching"),
                doesRecordZeroes = ConfigHolder.getInstance().getBoolean("recordZeroScore"),
                isPPM = ConfigHolder.getInstance().getBoolean("isMS1PPM"), // Relative or absolute precursor tolerance 
                doesKeepCPeptideFragmPattern = ConfigHolder.getInstance().getBoolean("keepCPeptideFragmPattern"),
                searcForAlsoMonoLink = ConfigHolder.getInstance().getBoolean("searcForAlsoMonoLink"),
                has_decoy = ConfigHolder.getInstance().getBoolean("decoy"),
                isInvertedPeptides = ConfigHolder.getInstance().getBoolean("isInverted"),
                doesKeepWeights = true;

        // A CrossLinker object, required for constructing theoretical spectra
        CrossLinker linker = GetCrossLinker.getCrossLinker(crossLinkerName, isLabeled);
        // Parameters for searching against experimental spectrum 
        double ms1Err = ConfigHolder.getInstance().getDouble("ms1Err"), // Precursor tolerance - ppm (isPPM needs to be true) or Da 
                ms2Err = ConfigHolder.getInstance().getDouble("ms2Err"), //Fragment tolerance - mz diff               
                massWindow = ConfigHolder.getInstance().getDouble("massWindow");    // mass window to make window on a given MSnSpectrum for FILTERING 

        // This part is due to Checking decoy strategy..
        // args[0] - labeled:True, or else noLabeled:False
        // args[1] - full path of fasta file
        // args[2] - full path of a folder containing a given fasta file
        // args[3] - full path of a result file.
        if (args.length == 4) {
            String isHeavyLabeled = args[0];
            if (isHeavyLabeled.equals("labeled")) {
                isLabeled = true;
            } else {
                isLabeled = false;
            }
            // update ConfigHolder property for isLabeled...
            ConfigHolder.getInstance().clearProperty("isLabeled");
            ConfigHolder.getInstance().setProperty("isLabeled", isLabeled);

            // Change database settings
            // Start with database...
            String fasta = args[1];
            givenDBName = fasta;
            inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta";
            // update ConfigHolder property for fasta file
            ConfigHolder.getInstance().clearProperty("givenDBName");
            ConfigHolder.getInstance().setProperty("givenDBName", givenDBName);

            // now name XLinked DB such as cam_plectin_equal_2Pfus_cxm_both
            cxDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_cxm_both";
            ConfigHolder.getInstance().clearProperty("cxDBName");
            ConfigHolder.getInstance().setProperty("cxDBName", cxDBName);

            // Now set an index file...            
            cxDBNameIndexFile = cxDBName + ".index";
            monoLinkFile = cxDBName + "_monoLink.index";

            // Check dbFolder now....
            dbFolder = args[2];
            ConfigHolder.getInstance().clearProperty("folder");
            ConfigHolder.getInstance().setProperty("folder", dbFolder);

            // change a result file now...
            resultFile = args[3];
            ConfigHolder.getInstance().clearProperty("resultFile");
            ConfigHolder.getInstance().setProperty("resultFile", resultFile);
        }

        LOGGER.info("Parameters are ready!");
        LOGGER.info("CX database is checking!");

        // This part of the code makes sure that an already generated CXDB is not constructed again..
        File settings = new File("settings.txt"),
                cxDB = new File(cxDBName + ".fastacp"),
                indexFile = new File(cxDBNameIndexFile),
                indexMonoLinkFile = new File(monoLinkFile);
        boolean isSame = isSameDBSetting(settings), // either the same/different/empty
                doesCXDBExist = false,
                doesIndexFileExist = false;
        // Seems the database setting is the same, so check if there is now constructed crosslinked peptide database exists...
        if (isSame) {
            for (File f : new File(dbFolder).listFiles()) {
                if (f.getName().equals(cxDB.getName())) {
                    LOGGER.info("A constrcuted fastacp file is found! The name=" + f.getName());
                    doesCXDBExist = true;
                }
            }
        }
        if (isSame && doesCXDBExist) {
            for (File f : new File(dbFolder).listFiles()) {
                if (f.getName().equals(indexFile.getName())) {
                    LOGGER.info("An index file for fastacp file is also found! The name=" + f.getName());
                    doesIndexFileExist = true;
                    indexFiles.add(indexFile);

                }
            }
        }
        // Either the same settings but no CXDB found or not the same settings at all..
        HashMap<String, String> headers_sequences = new HashMap<String, String>();
        if ((isSame && !doesCXDBExist) || !isSame) {
            // Construct a cross linked peptide database and write an index file with masses...
            LOGGER.info("A CXDB IS NOT found or DIFFERENT DATABASE SETTINGS! A CXDB is going to be constructed..");
            CreateDatabase instanceToCreateDB = new CreateDatabase(givenDBName,
                    inSilicoPeptideDBName,
                    cxDBName, // db related parameters
                    crossLinkerName, // crossLinker name
                    crossLinkedProteinTypes, // crossLinking type: Both/Inter/Intra
                    enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                    lowMass, highMass, // filtering of in silico peptides on peptide masses
                    minLen, // minimum length for each in silico digested peptide
                    maxLen_for_combined, // maximum lenght for a length for cross linked peptide (maxLen<len(A)+len(B)
                    does_link_to_itself, // if a peptide itself links to itself..
                    isLabeled,
                    has_decoy,
                    isInvertedPeptides); //
            headers_sequences = instanceToCreateDB.getHeadersAndSequences();
            // in silico digested contaminant database
            if (!contaminantDBName.isEmpty()) {
                EnzymeDigest o = new EnzymeDigest();                
                String cdb = contaminantDBName + "_insilico";
                o.main(cdb, enzymeFileName, enzymeName, contaminantDBName, null, lowMass, highMass, null, misclevaged);
                addHeaders(headers_sequences, cdb);
            }
            // first write down a cross-linked peptide database
            WriteCXDB.writeCXDB(headers_sequences, cxDBName);
            LOGGER.info("A CX database is now ready!");
            // now write a settings file
            writeSettings(settings);
        }
        if (searcForAlsoMonoLink && !headers_sequences.isEmpty()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    linker, fragMode, isBranching);
            bw.close();
            indexFiles.add(indexMonoLinkFile);
            LOGGER.info("An index (peptide-mass index) file for monolinks bas been created!");

        } else if (searcForAlsoMonoLink && headers_sequences.isEmpty()) {
            LOGGER.info("A header and sequence object is empty to build index file for monolink index file! Therefore, a CXDB is going to be constructed..");
            CreateDatabase instanceToCreateDB = new CreateDatabase(givenDBName,
                    inSilicoPeptideDBName,
                    cxDBName, // db related parameters
                    crossLinkerName, // crossLinker name
                    crossLinkedProteinTypes, // crossLinking type: Both/Inter/Intra
                    enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                    lowMass, highMass, // filtering of in silico peptides on peptide masses
                    minLen, // minimum length for each in silico digested peptide
                    maxLen_for_combined, // maximum lenght for a length for cross linked peptide (maxLen<len(A)+len(B)
                    does_link_to_itself, // if a peptide itself links to itself..
                    isLabeled,
                    has_decoy,
                    isInvertedPeptides); //
            headers_sequences = instanceToCreateDB.getHeadersAndSequences();
            BufferedWriter bw = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    linker, fragMode, isBranching);
            bw.close();
            indexFiles.add(indexMonoLinkFile);

            LOGGER.info("An index (peptide-mass index) file for monolinks bas been created!");
        }
        // Make sure that an index file also exists...
        if (!doesIndexFileExist) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));
            FASTACPDBLoader.generate_peptide_mass_index(bw,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    linker, fragMode, isBranching);
            bw.close();
            LOGGER.info("An index (peptide-mass index) file bas been created!");
            indexFiles.add(indexFile);
        }

        // delete in silico DB
        File f = new File(inSilicoPeptideDBName);
        f.delete();

        // STEP 2: CONSTRUCT CPEPTIDE OBJECTS
        // STEP 3: MATCH AGAINST THEORETICAL SPECTRUM
        // Get all MSnSpectrum! (all MS2 spectra)
        LOGGER.info("Getting experimental spectra and calculating PCXMs");
        BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));
        StringBuilder fileTitle = new StringBuilder("SpectrumIndex" + "\t" + "SpectrumFile" + "\t" + "MSnSpectrumTitle" + "\t"
                + "PrecursorMZ" + "\t" + "PrecursorCharge" + "\t" + "PrecursorMass" + "\t" + "TheoreticalMass" + "\t" + "MS1Err(PPM)" + "\t"
                + "ScoringFunction" + "\t" + "Score" + "\t"
                + "ProteinA" + "\t" + "ProteinB" + "\t" + "PeptideSequenceA" + "\t" + "PeptideSequenceB" + "\t"
                + "ModificationPeptideA" + "\t" + "ModificationPeptideB" + "\t"
                + "LinkerPositionOnPeptideA" + "\t" + "LinkerPositionOnPeptideB" + "\t"
                + "#MatchedPeaks" + "\t" + "#MatchedTheoreticalPeaks" + "\t"
                + "MatchedPeakList" + "\t" + "TheoreticalPeakList" + "\t"
                + "numTheoAs" + "\t" + "numTheoBs");
        if (doesKeepCPeptideFragmPattern) {
            fileTitle.append("\t").append("CPeptideFragPatternName");
        }
        if (doesKeepWeights) {
            fileTitle.append("\t").append("Weight");
        }
        bw.write(fileTitle.toString());
        bw.newLine();

        // List required for multithreading...
        ExecutorService excService = Executors.newFixedThreadPool(threadNum);

        for (File tmpIndexFile : indexFiles) {
            List<Future<ArrayList<Result>>> futureList = fillFutureList(mgfs, tmpIndexFile, ms1Err, isPPM, scoreName, ptmFactory, linker, fragMode, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow,
                    massWindow, isBranching, excService);
            // now multithreading calculation starts...
            int counting = 0;
            for (Future<ArrayList<Result>> future : futureList) {
                try {
                    counting++;
                    if (counting % 25000 == 0) {
                        LOGGER.info("Number of scored spectra=" + counting);
                    }
                    ArrayList<Result> results = future.get();
                    for (Result res : results) {
                        double tmpScore = res.getScore();
                        if ((tmpScore > 0 && !doesRecordZeroes) || doesRecordZeroes) {
                            CrossLinkedPeptides crossLinkedPeptides = res.getCp();
                            if (crossLinkedPeptides instanceof CPeptides) {
                                CPeptides tmpCpeptide = (CPeptides) res.getCp();
                                String modificationA = getModificationInfo(tmpCpeptide.getPeptideA()),
                                        modificationB = getModificationInfo(tmpCpeptide.getPeptideB());
                                // get linker positions to write them down..
                                int linkerPositionOnPeptideA = tmpCpeptide.getLinker_position_on_peptideA() + 1,
                                        linkerPositionOnPeptideB = tmpCpeptide.getLinker_position_on_peptideB() + 1;
                                // analyze matchedPeaks!
                                HashSet<Peak> matchedPeaks = res.getMatchedPeaks();
                                HashSet<CPeptidePeak> matchedCTheoPeaks = res.getMatchedCTheoPeaks();
                                // Sort them to write down on a result file
                                ArrayList<Peak> matchedPLists = new ArrayList<Peak>(matchedPeaks);
                                Collections.sort(matchedPLists, Peak.ASC_mz_order);
                                ArrayList<CPeptidePeak> matchedCTheoPLists = new ArrayList<CPeptidePeak>(matchedCTheoPeaks);
                                Collections.sort(matchedCTheoPLists, CPeptidePeak.Peak_ASC_mz_order);
                                boolean hasEnoughPeaks = false;
                                if (peakRequiredForImprovedSearch > 0) {
                                    hasEnoughPeaks = hasEnoughPeaks(matchedCTheoPLists, peakRequiredForImprovedSearch);
                                }
                                if ((peakRequiredForImprovedSearch == 0) || hasEnoughPeaks) {
                                    // select precursor mass, theoretical mass and MS1 error
                                    ArrayList<Charge> possibleCharges = res.getMsms().getPrecursor().getPossibleCharges();
                                    Charge charge = possibleCharges.get(possibleCharges.size() - 1);
                                    double precMass = CalculatePrecursorMass.getPrecursorMass(res.getMsms()),
                                            theoMass = res.getCp().getTheoretical_xlinked_mass(),
                                            tmpMS1Err = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass),
                                            precMZ = res.getMsms().getPrecursor().getMz();
                                    // Result line..
                                    String specInfo = counting + "\t" + res.getMsms().getFileName() + "\t" + res.getMsms().getSpectrumTitle() + "\t" + precMZ + "\t";
                                    String runningInfo = charge + "\t" + precMass + "\t" + theoMass + "\t" + tmpMS1Err + "\t"
                                            + scoreName + "\t" + res.getScore() + "\t"
                                            + tmpCpeptide.getProteinA() + "\t" + tmpCpeptide.getProteinB() + "\t"
                                            + tmpCpeptide.getPeptideA().getSequence() + "\t" + tmpCpeptide.getPeptideB().getSequence() + "\t"
                                            + modificationA + "\t" + modificationB + "\t"
                                            + linkerPositionOnPeptideA + "\t" + linkerPositionOnPeptideB + "\t"
                                            + matchedPeaks.size() + "\t" + matchedCTheoPeaks.size() + "\t";
                                    // write a result line..
                                    bw.write(specInfo + runningInfo);
                                    // now write all matched peaks..
                                    if (!matchedPLists.isEmpty()) {
                                        for (Peak p : matchedPLists) {
                                            bw.write(p.mz + " ");
                                        }
                                        bw.write("\t");
                                        // now write all matched theoretical peaks...                                        
                                        int numTheoPepAs = 0,
                                                numTheoPepBs = 0;
                                        for (CPeptidePeak tmpCPeak : matchedCTheoPLists) {
                                            bw.write(tmpCPeak.toString() + " ");
                                            int[] vals = check(tmpCPeak.toString(), numTheoPepAs, numTheoPepBs);
                                            numTheoPepAs = vals[0];
                                            numTheoPepBs = vals[1];
                                        }
                                        bw.write("\t" + numTheoPepAs + "\t" + numTheoPepBs);
                                        // if necessary, write fragmentation pattern for each found CPeptides object..
                                        if (doesKeepCPeptideFragmPattern) {
                                            DefineIdCPeptideFragmentationPattern p = new DefineIdCPeptideFragmentationPattern(matchedCTheoPLists,
                                                    linkerPositionOnPeptideA, linkerPositionOnPeptideB,
                                                    tmpCpeptide.getPeptideA().getSequence().length(), tmpCpeptide.getPeptideB().getSequence().length());
                                            p.getName();
                                            bw.write("\t" + p.getName());
                                        }
                                        if (doesKeepWeights) {
                                            bw.write("\t" + res.getWeight());
                                        }
                                    } else {
                                        bw.write("-" + "\t" + "-" + "\t" + "-" + "\t" + "-");
                                        if (doesKeepCPeptideFragmPattern) {
                                            bw.write("\t" + "-");
                                        }
                                        if (doesKeepWeights) {
                                            bw.write("\t" + "-");
                                        }
                                    }
                                    bw.newLine();
                                }
                            } else if (res.getCp() instanceof MonoLinkedPeptides) {
                                // MONOLINKED FOUND...
                                MonoLinkedPeptides tmpCpeptide = (MonoLinkedPeptides) res.getCp();
                                String modification = getModificationInfo(tmpCpeptide.getPeptide());
                                // get linker positions to write them down..
                                int linkerPositionOnPeptide = tmpCpeptide.getLinker_position() + 1;
                                // analyze matchedPeaks!
                                HashSet<Peak> matchedPeaks = res.getMatchedPeaks();
                                HashSet<CPeptidePeak> matchedCTheoPeaks = res.getMatchedCTheoPeaks();
                                // Sort them to write down on a result file
                                ArrayList<Peak> matchedPLists = new ArrayList<Peak>(matchedPeaks);
                                Collections.sort(matchedPLists, Peak.ASC_mz_order);
                                ArrayList<CPeptidePeak> matchedCTheoPLists = new ArrayList<CPeptidePeak>(matchedCTheoPeaks);
                                Collections.sort(matchedCTheoPLists, CPeptidePeak.Peak_ASC_mz_order);
                                boolean hasEnoughPeaks = false;
                                if (peakRequiredForImprovedSearch > 0) {
                                    hasEnoughPeaks = hasEnoughPeaks(matchedCTheoPLists, peakRequiredForImprovedSearch);
                                }
                                if ((peakRequiredForImprovedSearch == 0) || hasEnoughPeaks) {
                                    // select precursor mass, theoretical mass and MS1 error
                                    ArrayList<Charge> possibleCharges = res.getMsms().getPrecursor().getPossibleCharges();
                                    Charge charge = possibleCharges.get(possibleCharges.size() - 1);
                                    double precMass = CalculatePrecursorMass.getPrecursorMass(res.getMsms()),
                                            theoMass = res.getCp().getTheoretical_xlinked_mass(),
                                            tmpMS1Err = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass),
                                            precMZ = res.getMsms().getPrecursor().getMz();
                                    // Result line..
                                    String specInfo = counting + "\t" + res.getMsms().getFileName() + "\t" + res.getMsms().getSpectrumTitle() + "\t" + precMZ + "\t";
                                    String runningInfo = charge + "\t" + precMass + "\t" + theoMass + "\t" + tmpMS1Err + "\t"
                                            + scoreName + "\t" + res.getScore() + "\t"
                                            + tmpCpeptide.getProtein() + "\t" + "-" + "\t"
                                            + tmpCpeptide.getPeptide().getSequence() + "\t" + "-" + "\t"
                                            + modification + "\t" + "-" + "\t"
                                            + linkerPositionOnPeptide + "\t" + "-" + "\t"
                                            + matchedPeaks.size() + "\t" + matchedCTheoPeaks.size() + "\t";
                                    // write a result line..
                                    bw.write(specInfo + runningInfo);
                                    // now write all matched peaks..
                                    for (Peak p : matchedPLists) {
                                        bw.write(p.mz + " ");
                                    }
                                    bw.write("\t");
                                    // now write all matched theoretical peaks...
                                    for (CPeptidePeak tmpCPeak : matchedCTheoPLists) {
                                        bw.write(tmpCPeak.toString() + " ");
                                    }
                                    // if necessary, write fragmentation pattern for each found CPeptides object..
                                    if (doesKeepCPeptideFragmPattern) {
                                        bw.write("\t" + "MONOLINK_FOUND");
                                    }
                                    if (doesKeepWeights) {
                                        bw.write("\t" + res.getWeight());
                                    }
                                    bw.newLine();
                                }
                            } else if (res.getCp() instanceof Contaminant) {
                                // CONTAMINANT FOUND...
                                Contaminant tmpCpeptide = (Contaminant) res.getCp();
                                String modification = getModificationInfo(tmpCpeptide.getPeptide());
                                // analyze matchedPeaks!
                                HashSet<Peak> matchedPeaks = res.getMatchedPeaks();
                                HashSet<CPeptidePeak> matchedCTheoPeaks = res.getMatchedCTheoPeaks();
                                // Sort them to write down on a result file
                                ArrayList<Peak> matchedPLists = new ArrayList<Peak>(matchedPeaks);
                                Collections.sort(matchedPLists, Peak.ASC_mz_order);
                                ArrayList<CPeptidePeak> matchedCTheoPLists = new ArrayList<CPeptidePeak>(matchedCTheoPeaks);
                                Collections.sort(matchedCTheoPLists, CPeptidePeak.Peak_ASC_mz_order);
                                boolean hasEnoughPeaks = false;
                                if (peakRequiredForImprovedSearch > 0) {
                                    hasEnoughPeaks = hasEnoughPeaks(matchedCTheoPLists, peakRequiredForImprovedSearch);
                                }
                                if ((peakRequiredForImprovedSearch == 0) || hasEnoughPeaks) {
                                    // select precursor mass, theoretical mass and MS1 error
                                    ArrayList<Charge> possibleCharges = res.getMsms().getPrecursor().getPossibleCharges();
                                    Charge charge = possibleCharges.get(possibleCharges.size() - 1);
                                    double precMass = CalculatePrecursorMass.getPrecursorMass(res.getMsms()),
                                            theoMass = res.getCp().getTheoretical_xlinked_mass(),
                                            tmpMS1Err = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass),
                                            precMZ = res.getMsms().getPrecursor().getMz();
                                    // Result line..
                                    String specInfo = counting + "\t" + res.getMsms().getFileName() + "\t" + res.getMsms().getSpectrumTitle() + "\t" + precMZ + "\t";
                                    String runningInfo = charge + "\t" + precMass + "\t" + theoMass + "\t" + tmpMS1Err + "\t"
                                            + scoreName + "\t" + res.getScore() + "\t"
                                            + tmpCpeptide.getProtein() + "\t" + "-" + "\t"
                                            + tmpCpeptide.getPeptide().getSequence() + "\t" + "-" + "\t"
                                            + modification + "\t" + "-" + "\t"
                                            + "-" + "\t" + "-" + "\t"
                                            + matchedPeaks.size() + "\t" + matchedCTheoPeaks.size() + "\t";
                                    // write a result line..
                                    bw.write(specInfo + runningInfo);
                                    if (matchedPLists.isEmpty()) {
                                        bw.write("-");
                                    }
                                    // now write all matched peaks..
                                    for (Peak p : matchedPLists) {
                                        bw.write(p.mz + " ");
                                    }
                                    bw.write("\t");

                                    if (matchedCTheoPLists.isEmpty()) {
                                        bw.write("-");
                                    }
                                    // now write all matched theoretical peaks...
                                    for (CPeptidePeak tmpCPeak : matchedCTheoPLists) {
                                        bw.write(tmpCPeak.toString() + " ");
                                    }
                                    bw.write("\t" + "-" + "\t" + "-");
                                    // if necessary, write fragmentation pattern for each found CPeptides object..
                                    if (doesKeepCPeptideFragmPattern) {
                                        bw.write("\t" + "CONTAMINANT");
                                    }
                                    if (doesKeepWeights) {
                                        bw.write("\t" + "-");
                                    }
                                    bw.newLine();
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LOGGER.error(e);
                }
            }
        }
        LOGGER.info("Cross linked database search is done!");
        bw.close();
        excService.shutdown();
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

    //cxDBNameIndexFile
    private static List<Future<ArrayList<Result>>> fillFutureList(String mgfs,
            File indexFile, double ms1Err, boolean isPPM,
            ScoreName scoreName,
            PTMFactory ptmFactory,
            CrossLinker linker,
            FragmentationMode fragMode,
            double ms2Err,
            int intensity_option,
            int minFPeakNumPerWindow,
            int maxFPeakNumPerWindow,
            double massWindow,
            boolean isBranching,
            ExecutorService excService) throws IOException, MzMLUnmarshallerException {
        List<Future<ArrayList<Result>>> futureList = new ArrayList<Future<ArrayList<Result>>>();

        // now check all spectra to collect all required calculations...
        int specNum = 0;
        File ms2spectra = new File(mgfs);
        ArrayList<SpectrumInfo> specAndInfo = new ArrayList<SpectrumInfo>();
        SpectrumFactory fct = SpectrumFactory.getInstance();
        for (File mgf : ms2spectra.listFiles()) {
            if (mgf.getName().endsWith("mgf")) {
                fct.addSpectra(mgf, new WaitingHandlerCLIImpl());
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    specNum++;
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    double precMass = CalculatePrecursorMass.getPrecursorMass(ms);
                    SpectrumInfo i = new SpectrumInfo(ms, precMass);
                    specAndInfo.add(i);
                }
                LOGGER.info("Spectra from " + mgf.getName() + " are loaded. Total spectra=" + specNum);
            }
        }
        LOGGER.info("Number of all spectra are stored in memory=" + specNum);
        // Now sort all spectra based on their precursor mass for scoring against CPeptides object.
        Collections.sort(specAndInfo, SpectrumInfo.Precursor_ASC_roundDown_mz_order);
        ArrayList<Integer> precs = new ArrayList<Integer>();
        for (SpectrumInfo info : specAndInfo) {
            precs.add(info.getRoundDownPrecMass());
        }

        // Now read all CPeptides from an index file and put them into futureList for multithreading.
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        int numLine = 0;
        while ((line = br.readLine()) != null) {
            numLine++;
            if (numLine % 25000 == 0) {
                LOGGER.info("Number of spectra getting ready for cross linked peptide searching=" + numLine);
            }
            String[] split = line.split("\t");
            double theoMass = Double.parseDouble(split[10]);
            int downTheoMass = (int) theoMass;
            downTheoMass = downTheoMass - 10;
            //Select spectra if fits to given MS1Err diff
            ArrayList<MSnSpectrum> selectedMSnSpectra = new ArrayList<MSnSpectrum>();
            int pos = Collections.binarySearch(precs, downTheoMass);
            if (pos >= 0) {
                for (int i = pos; i < specAndInfo.size(); i++) {
                    double precMass = specAndInfo.get(i).getPrecursorMass(),
                            tmpDiff = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass);
                    if (tmpDiff <= ms1Err) {
                        selectedMSnSpectra.add(specAndInfo.get(i).getMS());
                        // Leave the loop because precursor mass is much far from precursor mass tolerance
                    } else if (theoMass < precMass && (tmpDiff >= (5 * ms1Err))) {
                        i = specAndInfo.size();
                        if (!selectedMSnSpectra.isEmpty()) {
                            Score score = new Score(selectedMSnSpectra, line, scoreName, ptmFactory, linker, fragMode, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow, massWindow, isBranching);
                            Future future = excService.submit(score);
                            futureList.add(future);
                        }
                    }
                }
            }
//            if (!selectedMSnSpectra.isEmpty()) {
//                Score score = new Score(selectedMSnSpectra, line, scoreName, ptmFactory, linker, fragMode, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow, massWindow, isBranching);
//                Future future = excService.submit(score);
//                futureList.add(future);
//            }

            // regular approach
//            for (int i = 0; i < specAndInfo.size(); i++) {
//                double precMass = specAndInfo.get(i).getPrecursorMass(),
//                        tmpDiff = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass);
//                if (tmpDiff <= ms1Err) {
//                    selectedMSnSpectra.add(specAndInfo.get(i).getMS());
//                    // Leave the loop because precursor mass is much far from precursor mass tolerance
//                } else if (theoMass < precMass && (tmpDiff >= (5 * ms1Err))) {
//                    i = specAndInfo.size();
//                }
//            }
        }
        LOGGER.info("Scoring starts now!");
        return futureList;
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
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameIndexFile = cxDBName + "header_seq_mass_cxms.index", // An index file from already generated cross linked protein database
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
                fragModeName = ConfigHolder.getInstance().getString("fragMode"),
                minLen = ConfigHolder.getInstance().getString("minLen"),
                isBranching = ConfigHolder.getInstance().getString("isBranching"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                hasInterPeptide = ConfigHolder.getInstance().getString("hasInterPeptide");
        boolean isLabeled = ConfigHolder.getInstance().getBoolean("isLabeled");

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("Settings file" + "\n");
        bw.write("Running date=" + new Date().toString() + "\n");
        bw.write("givenDBName" + "\t" + givenDBName + "\n");
        bw.write("cxDBName" + "\t" + cxDBName + "\n");
        bw.write("cxDBNameIndexFile" + "\t" + cxDBNameIndexFile + "\n");
        bw.write("dbFolder" + "\t" + dbFolder + "\n");
        bw.write("crossLinkerName" + "\t" + crossLinkerName + "\n");
        bw.write("isLabeled" + "\t" + isLabeled + "\n");
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
        bw.write("minLen" + "\t" + minLen + "\n");
        bw.write("maxLenCombined" + "\t" + maxLenCombined + "\n");
        bw.write("isBranching" + "\t" + isBranching + "\n");
        bw.write("hasInterPeptide" + "\t" + hasInterPeptide + "\n");
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
    public static boolean isSameDBSetting(File paramFile) throws IOException {
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                dbFolder = ConfigHolder.getInstance().getString("folder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowMass = ConfigHolder.getInstance().getString("lowerMass"),
                highMass = ConfigHolder.getInstance().getString("higherMass"),
                variableModification = ConfigHolder.getInstance().getString("variableModification"),
                fixedModification = ConfigHolder.getInstance().getString("fixedModification"),
                isBranching = ConfigHolder.getInstance().getString("isBranching"),
                minLen = ConfigHolder.getInstance().getString("minLen"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                hasInterPeptide = ConfigHolder.getInstance().getString("hasInterPeptide");
        int control = 0;
        boolean isSame = false,
                isLabeled = ConfigHolder.getInstance().getBoolean("isLabeled");

        BufferedReader br = new BufferedReader(new FileReader(paramFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            if ((line.startsWith("givenDBName")) && (line.split("\t")[1].equals(givenDBName))) {
                control++;
            } else if ((line.startsWith("dbFolder")) && (line.split("\t")[1].equals(dbFolder))) {
                control++;
            } else if ((line.startsWith("crossLinkerName")) && (line.split("\t")[1].equals(crossLinkerName))) {
                control++;
            } else if ((line.startsWith("crossLinkedProteinTypes")) && (line.split("\t")[1].equals(crossLinkedProteinTypes))) {
                control++;
            } else if ((line.startsWith("enzymeName")) && (line.split("\t")[1].equals(enzymeName))) {
                control++;
            } else if ((line.startsWith("misclevaged")) && (line.split("\t")[1].equals(misclevaged))) {
                control++;
            } else if ((line.startsWith("lowMass")) && (line.split("\t")[1].equals(lowMass))) {
                control++;
            } else if ((line.startsWith("highMass")) && (line.split("\t")[1].equals(highMass))) {
                control++;
            } else if ((line.startsWith("fixedModification")) && (line.split("\t")[1].equals(fixedModification))) {
                control++;
            } else if ((line.startsWith("variableModification")) && line.split("\t").length > 1 && (line.split("\t")[1].equals(variableModification))) {
                control++;
            } else if ((line.startsWith("variableModification")) && line.split("\t").length == 1) {
                control++;
            } else if ((line.startsWith("isLabeled")) && (line.split("\t")[1].equals(isLabeled))) {
                control++;
            } else if ((line.startsWith("minLen")) && (line.split("\t")[1].equals(minLen))) {
                control++;
            } else if ((line.startsWith("maxLenCombined")) && (line.split("\t")[1].equals(maxLenCombined))) {
                control++;
            } else if ((line.startsWith("isBranching")) && (line.split("\t")[1].equals(isBranching))) {
                control++;
            } else if ((line.startsWith("hasInterPeptide")) && (line.split("\t")[1].equals(hasInterPeptide))) {
                control++;
            }
        }
        if (control == 15) {
            isSame = true;
        }
        return isSame;
    }

    /**
     * This method returns modification info derived from a given peptide. It
     * only returns variable modifications
     *
     * @param peptide
     * @return
     */
    private static String getModificationInfo(Peptide peptide) {
        ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
        String info = "";
        for (ModificationMatch m : modificationMatches) {
            if (m.isVariable()) {
                String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                info += tmp + ";";
            }
        }
        return info;
    }

    /**
     * This method returns all required information to construct a CPeptides
     * object and also to write them on an index file with their masses
     *
     * @param cPeptide
     * @return
     */
    public static StringBuilder getInfo(CPeptides cPeptide) {
        String proteinA = cPeptide.getProteinA(),
                proteinB = cPeptide.getProteinB(),
                peptideA = cPeptide.getPeptideA().getSequence(),
                peptideB = cPeptide.getPeptideB().getSequence(),
                fixedModPepA = getPTMName(cPeptide.getPeptideA().getModificationMatches(), false),
                fixedModPepB = getPTMName(cPeptide.getPeptideB().getModificationMatches(), false),
                varModPepA = getPTMName(cPeptide.getPeptideA().getModificationMatches(), true),
                varModPepB = getPTMName(cPeptide.getPeptideB().getModificationMatches(), true);
        int linkerPosPepA = cPeptide.getLinker_position_on_peptideA(),
                linkerPosPepB = cPeptide.getLinker_position_on_peptideB();
        double mass = cPeptide.getTheoretical_xlinked_mass();
        StringBuilder sb = new StringBuilder(proteinA + "\t" + proteinB + "\t" + peptideA + "\t" + peptideB + "\t"
                + linkerPosPepA + "\t" + linkerPosPepB + "\t" + fixedModPepA + "\t" + fixedModPepB + "\t" + varModPepA + "\t" + varModPepB + "\t" + mass);
        return sb;
    }

    /**
     * This method returns a PTM name from a given ModificationMatches from a
     * peptide
     *
     * @param ptms_peptide a list of Modification matches
     * @param isVariable true:Variable/false:Fixed
     * @return
     */
    private static String getPTMName(ArrayList<ModificationMatch> ptms_peptide, boolean isVariable) {
        String tmp_ptm = "";
        for (ModificationMatch m : ptms_peptide) {
            if (m.isVariable() == isVariable) {
                String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                tmp_ptm += tmp;
            } else {
                if (m.isVariable() == isVariable) {
                    String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                    tmp_ptm += tmp;
                }
            }
        }
        return tmp_ptm;
    }

    /**
     * This method makes sure that there are enough peaks from each peptide
     * (containing number of requiredPeaks for each peptide)
     *
     * @param matchedCTheoPLists a list of matched CPeptide Theoretical peaks
     * @param requiredPeaks minimum number of required peaks for each
     * theoretical CPeptide fragment ions
     * @return true/has enough peaks; false/not enough peaks
     */
    private static boolean hasEnoughPeaks(ArrayList<CPeptidePeak> matchedCTheoPLists, int requiredPeaks) {
        boolean hasEnoughPeaks = false;
        int theoPepA = 0,
                theoPepB = 0;
        for (CPeptidePeak cpP : matchedCTheoPLists) {
            if (cpP.getName().contains("pepA") || cpP.getName().contains("lepA")) {
                theoPepA++;
            } else if (cpP.getName().contains("pepB") || cpP.getName().contains("lepB")) {
                theoPepB++;
            }
        }
        if (theoPepA >= requiredPeaks && theoPepB >= requiredPeaks) {
            hasEnoughPeaks = true;
        }
        return hasEnoughPeaks;
    }

    public static int[] check(String tmpName, int theoreticalPepA, int theoreticalPepB) {
        String split[] = tmpName.split("_");
        int[] vals = new int[2];
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("pepA")) {
                theoreticalPepA++;
            } else if (split[i].equals("pepB")) {
                theoreticalPepB++;
            } else if (split[i].equals("lepA") && !split[i + 1].startsWith("mono")) {
                theoreticalPepA++;
            } else if (split[i].equals("lepB") && !split[i + 1].startsWith("mono")) {
                theoreticalPepB++;
            }

        }
        vals[0] = theoreticalPepA;
        vals[1] = theoreticalPepB;
        return vals;
    }

    private static void addHeaders(HashMap<String, String> headers_sequences, String cdb) throws IOException {
       File contaminant = new File(cdb);
        DBLoader loader = DBLoaderLoader.loadDB(contaminant);
        Protein startProtein = null;
        // get a crossLinkerName object        
        while ((startProtein = loader.nextProtein()) != null) {
            String startHeader = startProtein.getHeader().getAccession(),
                    startSequence = startProtein.getSequence().getSequence(),
                    tmpStartAccession = "";
            // check if a header comes from a generic! 
            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
                tmpStartAccession = "contaminant_" + startHeader.substring(0, startHeader.indexOf("("));
            }
            headers_sequences.put(tmpStartAccession, startSequence);
        }
        contaminant.deleteOnExit();        
    }
}
