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
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import com.compomics.util.protein.Protein;
import config.ConfigHolder;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerType;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import multithread.score.Result;
import multithread.score.ScorePSM;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;
import scoringFunction.ScoreName;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinkedPeptides;
import theoretical.CrossLinkingType;
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
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta",
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameIndexFile = cxDBName + ".index", // An index file from already generated cross linked protein database
                monoLinkFile = cxDBName + "_monoLink.index",
                dbFolder = ConfigHolder.getInstance().getString("folder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes").toLowerCase(),
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
                scoring = ConfigHolder.getInstance().getString("scoring"),
                labeledOption = ConfigHolder.getInstance().getString("isLabeled");
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
                peakRequiredForImprovedSearch = ConfigHolder.getInstance().getInt("peakRequiredForImprovedSearch"),
                maxModsPerPeptide = ConfigHolder.getInstance().getInt("maxModsPerPeptide");

        // multithreading 
        ExecutorService excService = Executors.newFixedThreadPool(threadNum);
        // more cross linking option..;
        boolean does_link_to_itself = ConfigHolder.getInstance().getBoolean("doesLinkToItself_InterPeptide"),
                isBranching = ConfigHolder.getInstance().getBoolean("isBranching"),
                doesRecordZeroes = ConfigHolder.getInstance().getBoolean("recordZeroScore"),
                isPPM = ConfigHolder.getInstance().getBoolean("isMS1PPM"), // Relative or absolute precursor tolerance 
                doesKeepCPeptideFragmPattern = ConfigHolder.getInstance().getBoolean("keepCPeptideFragmPattern"),
                searcForAlsoMonoLink = ConfigHolder.getInstance().getBoolean("searcForAlsoMonoLink"),
                has_decoy = ConfigHolder.getInstance().getBoolean("decoy"),
                isInvertedPeptides = ConfigHolder.getInstance().getBoolean("isInverted"),
                doesKeepWeights = true,
                isContrastLinkedAttachmentOn = ConfigHolder.getInstance().getBoolean("isDifferentIonTypesMayTogether"),
                doesFindAllMatchedPeaks = ConfigHolder.getInstance().getBoolean("doesFindAllMatchedPeaks"),
                isPercolatorAsked = ConfigHolder.getInstance().getBoolean("isPercolatorAsked");
        // Parameters for searching against experimental spectrum 
        double ms1Err = ConfigHolder.getInstance().getDouble("ms1Err"), // Precursor tolerance - ppm (isPPM needs to be true) or Da 
                ms2Err = ConfigHolder.getInstance().getDouble("ms2Err"), //Fragment tolerance - mz diff               
                massWindow = ConfigHolder.getInstance().getDouble("massWindow");    // mass window to make window on a given MSnSpectrum for FILTERING 

        // A CrossLinker object, required for constructing theoretical spectra - get required cross linkers together
        ArrayList<CrossLinker> linkers = new ArrayList<CrossLinker>();
        boolean isLabeled = true;
        if (labeledOption.equals("B")) {
            linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, true));
            linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, false));
        } else if (labeledOption.equals("F")) {
            linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, false));
        } else if (labeledOption.equals("T")) {
            linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, true));
        }

        // Maybe heavy and light labeled linkers are used
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
                addContaminants(headers_sequences, cdb, 4, 25);
            }

            // first write down a cross-linked peptide database
            WriteCXDB.writeCXDB(headers_sequences, cxDBName);
            LOGGER.info("A CX database is now ready!");
            // now write a settings file
            writeSettings(settings);
        }
        if (searcForAlsoMonoLink && !headers_sequences.isEmpty()) {
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, isBranching, maxModsPerPeptide);
            }
            bw2.close();
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
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, isBranching, maxModsPerPeptide);
            }
            bw2.close();
        }
        // Make sure that an index file also exists...
        if (!doesIndexFileExist) {
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexFile));
            FASTACPDBLoader.generate_peptide_mass_index_for_contaminants(bw2,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    fragMode, isBranching, isContrastLinkedAttachmentOn, maxModsPerPeptide);

            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, isBranching, isContrastLinkedAttachmentOn, maxModsPerPeptide);

                if (searcForAlsoMonoLink) {
                    FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                            headers_sequences, ptmFactory,
                            fixedModifications,
                            variableModifications,
                            linker, fragMode, isBranching, maxModsPerPeptide);
                }
            }
            bw2.close();
            LOGGER.info("An index (peptide-mass index) file bas been created!");
        }

        // delete in silico DB
        File f = new File(inSilicoPeptideDBName);
        f.delete();

        // STEP 2: CONSTRUCT CPEPTIDE OBJECTS
        // STEP 3: MATCH AGAINST THEORETICAL SPECTRUM
        // Get all MSnSpectrum! (all MS2 spectra)
        LOGGER.info("Getting experimental spectra and calculating PCXMs");
        // Title for percolator-input
        String percolatorInputTitle = writePercolatorTitle();
        for (File mgf : new File(mgfs).listFiles()) {
            if (mgf.getName().endsWith(".mgf")) {
                // prepare percolator inputs
                HashSet<String> ids = new HashSet<String>(); // to give every time unique ids for each entry on percolator input
                File percolatorIntra = new File(resultFile + "_" + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_intra_percolator" + ".txt"),
                        percolatorInter = new File(resultFile + "_" + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_inter_percolator" + ".txt");

                BufferedWriter bw_intra = new BufferedWriter(new FileWriter(percolatorIntra)),
                        bw_inter = new BufferedWriter(new FileWriter(percolatorInter));
                bw_intra.write(percolatorInputTitle + "\n");
                bw_inter.write(percolatorInputTitle + "\n");

                // write results on output file for each mgf
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile + "_" + mgf.getName() + ".txt"));
                StringBuilder title = prepareTitle(doesKeepCPeptideFragmPattern, doesKeepWeights);
                bw.write(title + "\n");
                List<Future<ArrayList<Result>>> futureList = fillFutures(mgf, indexFile, ms1Err, isPPM, scoreName, ptmFactory,
                        crossLinkerName, fragMode, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow,
                        massWindow, isBranching, isContrastLinkedAttachmentOn, doesFindAllMatchedPeaks, doesKeepCPeptideFragmPattern, doesKeepWeights, excService);
                System.out.println("FutureList=" + futureList.size());
                for (Future<ArrayList<Result>> future : futureList) {
                    try {
                        // Write each result on an output file...
                        ArrayList<Result> results = future.get();
                        for (Result res : results) {
                            double tmpScore = res.getScore();
                            // making sure that only crosslinked ones are written, neither monolinks nor contaminants.
                            if (((tmpScore > 0 && !doesRecordZeroes) || doesRecordZeroes) && res.getCp().getLinkingType().equals(CrossLinkingType.CROSSLINK)) {
                                boolean hasEnoughPeaks = false;
                                if (peakRequiredForImprovedSearch > 0 && !isPercolatorAsked) {
                                    hasEnoughPeaks = hasEnoughPeaks(new ArrayList<CPeptidePeak>(res.getMatchedCTheoPeaks()), peakRequiredForImprovedSearch);
                                    if (hasEnoughPeaks) {
                                        bw.write(res.toPrint());
                                        bw.newLine();
                                    }
                                }
                                if (peakRequiredForImprovedSearch > 0 && isPercolatorAsked) {
                                    hasEnoughPeaks = hasEnoughPeaks(new ArrayList<CPeptidePeak>(res.getMatchedCTheoPeaks()), peakRequiredForImprovedSearch);
                                    if (hasEnoughPeaks) {
                                        bw.write(res.toPrint());
                                        bw.newLine();
                                        // write also percolator input
                                        write(res, bw_inter, bw_intra, ids);
                                    }
                                }
                                if (peakRequiredForImprovedSearch == 0 && !isPercolatorAsked) {
                                    bw.write(res.toPrint());
                                    bw.newLine();
                                }
                                if (peakRequiredForImprovedSearch == 0 && isPercolatorAsked) {
                                    bw.write(res.toPrint());
                                    bw.newLine();
                                    // write also percolator input
                                    write(res, bw_inter, bw_intra, ids);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        LOGGER.error(e);
                    }
                }
                bw.close();
                bw_intra.close();
                bw_inter.close();
            }
        }
        LOGGER.info("Cross linked database search is done!");
        excService.shutdown();
    }

    private static StringBuilder prepareTitle(boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights) throws IOException {
        StringBuilder fileTitle = new StringBuilder(
                "SpectrumFile" + "\t" + "MSnSpectrumTitle" + "\t" + "scannr" + "\t" + "RetentionTime" + "\t"
                + "ObservedMass" + "\t" + "PrecCharge" + "\t" + "MS1Err" + "\t" + "AbsMS1Err(PPM)" + "\t"
                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t"
                + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
                + "Type" + "\t"
                + "Score" + "\t" + "DeltaScore" + "\t" + "ScoringFunction" + "\t"
                + "ln(NumSp)" + "\t"
                + "#MatchedPeaks" + "\t" + "#MatchedTheoreticalPeaks" + "\t"
                + "#TheoIonA" + "\t" + "#TheoIonB" + "\t"
                + "IonFracA" + "\t" + "IonFracB" + "\t"
                + "MatchedPeakList" + "\t" + "TheoMatchedPeakList" + "\t"
        );
        if (doesKeepCPeptideFragmPattern) {
            fileTitle.append("\t").append("CPeptideFragPatternName");
        }
        if (doesKeepWeights) {
            fileTitle.append("\t").append("Weight");
        }
        fileTitle.append("\t" + "isLabeled");
        return fileTitle;
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
     * This method generates used for multithreading..
     *
     */
    private static List<Future<ArrayList<Result>>> fillFutures(File mgf,
            File indexFile,
            double ms1Err, boolean isPPM,
            ScoreName scoreName,
            PTMFactory ptmFactory,
            String crossLinkerName,
            FragmentationMode fragMode,
            double fragTol,
            int intensity_option,
            int minFPeakNumPerWindow,
            int maxFPeakNumPerWindow,
            double massWindow,
            boolean isBranching,
            boolean isContrastLinkedAttachmentOn,
            boolean doesFindAllMatchedPeaks,
            boolean doesKeepCPeptideFragmPattern,
            boolean doesKeepWeight,
            ExecutorService excService) throws IOException, MzMLUnmarshallerException, XmlPullParserException, Exception {

        List<Future<ArrayList<Result>>> futureList = new ArrayList<Future<ArrayList<Result>>>();

        // now check all spectra to collect all required calculations...
        SpectrumFactory fct = SpectrumFactory.getInstance();
        if (mgf.getName().endsWith("mgf")) {
            fct.addSpectra(mgf, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(mgf.getName())) {
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                double precMass = CalculatePrecursorMass.getPrecursorMass(ms);
                ArrayList<CrossLinkedPeptides> selectedCPeptides = new ArrayList<CrossLinkedPeptides>();
                String line = "";
                BufferedReader br = new BufferedReader(new FileReader(indexFile));
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\t");
                    double theoMass = Double.parseDouble(split[10]);
                    //Select spectra if fits to given MS1Err diff
                    double tmpDiff = CalculateMS1Err.getMS1Err(isPPM, theoMass, precMass);
                    if (Math.abs(tmpDiff) <= ms1Err) {
                        CrossLinker linker = null;
                        if (line.contains("CROSSLINK")) {
                            linker = GetCrossLinker.getCrossLinker(crossLinkerName, Boolean.parseBoolean(split[12]));
                        }
                        CrossLinkedPeptides cp = Start.getCPeptides(line, ptmFactory, linker, fragMode, isBranching, isContrastLinkedAttachmentOn);
                        selectedCPeptides.add(cp);
                    }
                }
                if (!selectedCPeptides.isEmpty()) {
                    ScorePSM score = new ScorePSM(selectedCPeptides, ms, scoreName, fragTol, massWindow, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow, doesFindAllMatchedPeaks, isPPM, doesKeepCPeptideFragmPattern, doesKeepWeight);
                    Future future = excService.submit(score);
                    futureList.add(future);
                }
            }
        }
        LOGGER.info("Scoring starts now!");
        return futureList;
    }

    /**
     * This method writes a file containing parameters (etc. input/output
     * information) as "settings.txt" file
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
                hasInterPeptide = ConfigHolder.getInstance().getString("hasInterPeptide"),
                isLabeled = ConfigHolder.getInstance().getString("isLabeled");

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
        bw.write("hasInterPeptide" + "\t" + hasInterPeptide);
        bw.close();
    }

    /**
     * This method checks if parameters to construct a xlinked database are the
     * same as before.
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
                hasInterPeptide = ConfigHolder.getInstance().getString("hasInterPeptide"),
                isLabeled = ConfigHolder.getInstance().getString("isLabeled");
        int control = 0;
        boolean isSame = false;
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
            } else if ((line.startsWith("fixedModification")) && (line.split("\t")[1].equals(fixedModification)) && line.split("\t").length == 1) {
                control++;
            } else if ((line.startsWith("variableModification")) && (line.split("\t")[1].equals(variableModification))) {
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
        if (control == 14) {
            isSame = true;
        }
        return isSame;
    }

    /**
     * This method assures that there are enough peaks from each peptide
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

    /**
     * This method adds accession and sequence of contaminant to constructed
     * database
     *
     * @param headers_sequences
     * @param contaminantDB
     * @param minLen
     * @param maxLen
     * @throws IOException
     */
    private static void addContaminants(HashMap<String, String> headers_sequences, String contaminantDB, int minLen, int maxLen) throws IOException {
        File contaminant = new File(contaminantDB);
        DBLoader loader = DBLoaderLoader.loadDB(contaminant);
        Protein startProtein = null;
        // get a crossLinkerName object        
        while ((startProtein = loader.nextProtein()) != null) {
            String startHeader = startProtein.getHeader().getAccession(),
                    startSequence = startProtein.getSequence().getSequence(),
                    tmpStartAccession = "";
            // check if a header comes from a generic! 
//            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
//            if (startHeader.startsWith("contaminant")) {
            if (startSequence.length() > minLen && startSequence.length() <= maxLen) {
                tmpStartAccession = "contaminant_" + startHeader.substring(0, startHeader.indexOf("("));
                headers_sequences.put(tmpStartAccession, startSequence);
            }
        }
        contaminant.deleteOnExit();
    }

    /**
     * This method generates a CPeptides object after reading a file
     *
     * @param line
     * @param ptmFactory
     * @param linker
     * @param fragMode
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static CrossLinkedPeptides getCPeptides(String line, PTMFactory ptmFactory, CrossLinker linker, FragmentationMode fragMode,
            boolean isBranching, boolean isContrastLinkedAttachmentOn) throws XmlPullParserException, IOException {
        CrossLinkedPeptides selected = null;
        String[] split = line.split("\t");
        String proteinA = split[0],
                proteinB = split[1],
                peptideAseqFile = split[2],
                peptideBseqFile = split[3],
                fixedModA = split[6],
                fixedModB = split[7],
                variableModA = split[8],
                variableModB = split[9];
        // linker positions...
        // This means a cross linked peptide is here...
        if (!proteinB.equals("-")) {

            linker.setIsLabeled(Boolean.parseBoolean(split[12]));
            Integer linkerPosPeptideA = Integer.parseInt(split[4]),
                    linkerPosPeptideB = Integer.parseInt(split[5]);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModB, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA),
                    ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true),
                    variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModB, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            ptms_peptideB.addAll(variablePTM_peptideB);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA),
                    peptideB = new Peptide(peptideBseqFile, ptms_peptideB);
            if (peptideA.getSequence().length() > peptideB.getSequence().length()) {
                // now generate peptide...
                CPeptides tmpCpeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            } else {
                CPeptides tmpCpeptide = new CPeptides(proteinB, proteinA, peptideB, peptideA, linker, linkerPosPeptideB, linkerPosPeptideA, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            }
        } // This means only monolinked peptide...    
        else if (!proteinA.startsWith("contaminant")) {
            Integer linkerPosPeptideA = Integer.parseInt(split[4]);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA);
            MonoLinkedPeptides mP = new MonoLinkedPeptides(peptideA, proteinA, linkerPosPeptideA, linker, fragMode, isBranching);
            selected = mP;
        } else if (proteinA.startsWith("contaminant")) {
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA);
            Contaminant mP = new Contaminant(peptideA, proteinA, fragMode, isBranching);
            selected = mP;
        }
        return selected;
    }

    /**
     * This method prepare title for each percolator inputs
     *
     * @return
     * @throws IOException
     */
    private static String writePercolatorTitle() throws IOException {
        String title = "SpecID" + "\t" + "Label" + "\t" + "scannr" + "\t"
                + "score" + "\t" + "deltaScore" + "\t"
                + "charge" + "\t" + "observedMass_Da" + "\t" + "massDelta_ppm" + "\t" + "absMassDelta_ppm" + "\t"
                + "retentionTime" + "\t"
                + "lenPepA" + "\t" + "lenPepB" + "\t" + "sumLen" + "\t"
                + "ionFracA" + "\t" + "ionFracB" + "\t"
                + "lnNumSp" + "\t";
        return (title);
    }

    /**
     * This method writes down each Result for percolator-inputs
     */
    private static void write(Result res, CPeptides c, BufferedWriter bw_percolator_input, HashSet<String> ids) throws IOException {
        String id = "",
                scn = res.getScanNum(),
                target = "";
        int label = -1,
                pepALen = c.getPeptideA().getSequence().length(),
                pepBLen = c.getPeptideB().getSequence().length(),
                sumLen = pepALen + pepBLen;
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false;
        if (c.getProteinA().contains("REVERSED") || c.getProteinA().contains("SHUFFLED") || c.getProteinA().contains("DECOY")) {
            isProteinAdecoy = true;
        }
        if (c.getProteinB().contains("REVERSED") || c.getProteinB().contains("SHUFFLED") || c.getProteinB().contains("DECOY")) {
            isProteinBdecoy = true;
        }
        if (!isProteinAdecoy && !isProteinBdecoy) {
            label = 1;
            target = "T-";
        } else {
            target = "D-";
        }
        id = target + scn;
        if (ids.contains(id)) {
            int i = 2;
            while (ids.contains(id)) {
                id = target + scn + "-" + i;
                i++;
            }
        }
        ids.add(id);
        String input = id + "\t" + label + "\t" + scn + "\t"
                + res.getScore() + "\t" + res.getDeltaScore() + "\t"
                + res.getCharge() + "\t" + res.getObservedMass() + "\t" + res.getDeltaMass() + "\t" + res.getAbsDeltaMass() + "\t"
                + res.getMsms().getPrecursor().getRt() + "\t"
                + pepALen + "\t" + pepBLen + "\t" + sumLen + "\t"
                + res.getIonFracA() + "\t" + res.getIonFracB() + "\t"
                + res.getLnNumSpec();
        bw_percolator_input.write(input + "\n");
    }

    /**
     * This method allows writing down results for either intra-proteins or
     * inter-proteins
     */
    private static void write(Result res, BufferedWriter bw_inter, BufferedWriter bw_intra, HashSet<String> ids) throws IOException {
        if (res.getCp() instanceof CPeptides) {
            CPeptides c = (CPeptides) res.getCp();
            if (c.getType().equals("interProtein")) {
                // write to bw_inter
                write(res, c, bw_inter, ids);

            } else {
                // write to bw_intra
                write(res, c, bw_intra, ids);
            }
        }
    }

}
