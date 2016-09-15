/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import analyse.CXPSM.NameTargetDecoy;
import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.protein.Protein;
import config.ConfigHolder;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import database.CreateDatabase;
import database.FASTACPDBLoader;
import database.WriteCXDB;
import gui.MainController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import multithread.score.Result;
import multithread.score.ScorePSM;
import org.apache.log4j.Logger;

import org.xmlpull.v1.XmlPullParserException;
import precursorRemoval.MascotAdaptedPrecursorPeakRemoval;
import scoringFunction.ScoreName;
import specprocessing.DeisotopingAndDeconvoluting;
import start.lucene.IndexAndSearch;
import theoretical.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import util.ResourceUtils;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Painter;
import javax.swing.UIManager;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import visualize.Visualize;

/**
 *
 * @author Sule
 */
public class Start {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(Start.class);
    private static final String HEADER = "[Xilmass - an algorithm to identify cross-linked peptides]\n";
    private static final String USAGE = "java -jar <jar file name>";
    private static Options options;

    /**
     * The startup error message.
     */
    private static final String ERROR_MESSAGE = "An error occured during startup, please try again."
            + System.lineSeparator() + "If the problem persists, contact your administrator or post an issue on the google code page.";

    /**
     * Main executable.
     *
     * @param commandLineArguments Command-line arguments.
     */
    public static void main(String[] commandLineArguments) {
        sendAnalyticsEvent();

        constructOptions();

        displayBlankLines(1, System.out);
        displayHeader(System.out);
        displayBlankLines(2, System.out);
        parse(commandLineArguments);
    }

    /**
     * Run xilmass in command line mode.
     */
    public static void launchCommandLineMode() throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            Date startDate = new Date();

            // STEP 1: GET ALL XILMASS PARAMETERS!!
            String version = ConfigHolder.getInstance().getString("xilmass.version");
            LOGGER.info("Xilmass version:" + version + " starts!");
            String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                    contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                    inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta",
                    insilicoContaminantDBName = "",
                    cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                    //                    output = ConfigHolder.getInstance().getString("tdfile"), // td file
                    //                    allXPSMsName = ConfigHolder.getInstance().getString("allXPSMoutput"), // all XPSMs
                    cxDBNameIndexFile = cxDBName + ".index", // An index file from already generated cross linked protein database
                    crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                    crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes").toLowerCase(),
                    enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                    misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                    lowMass = ConfigHolder.getInstance().getString("lowerMass"),
                    highMass = ConfigHolder.getInstance().getString("higherMass"),
                    mgfs = ConfigHolder.getInstance().getString("mgfs"),
                    resultFolder = ConfigHolder.getInstance().getString("resultFolder"),
                    fixedModificationNames = ConfigHolder.getInstance().getString("fixedModification"), // must be sepeared by semicolumn, lowercase, no space
                    variableModificationNames = ConfigHolder.getInstance().getString("variableModification"),
                    fragModeName = ConfigHolder.getInstance().getString("fragMode"),
                    // scoring = ConfigHolder.getInstance().getString("scoringFunctionName"), was required for testing
                    scoring = "AndromedaD",
                    labeledOption = ConfigHolder.getInstance().getString("isLabeled");
            // load enzyme and modification files from a resource folder
            String enzymeFileName = ResourceUtils.getResourceByRelativePath("enzymes.txt").getFile().toString();

            // checking if paths for given input are avaliable
            File f = null;
            try {
                f = new File(givenDBName);
            } catch (Exception e) {
                LOGGER.error("A given path for givenDBName is not found!");
            }
            try {
                f = new File(contaminantDBName);
            } catch (Exception e) {
                LOGGER.error("A given path for contaminantDBName is not found!");
            }
            try {
                f = new File(inSilicoPeptideDBName);
            } catch (Exception e) {
                LOGGER.error("A given path for inSilicoPeptideDBName is not found!");
            }
            try {
                f = new File(insilicoContaminantDBName);
            } catch (Exception e) {
                LOGGER.error("A given path for insilicoContaminantDBName is not found!");
            }
            try {
                f = new File(cxDBName);
            } catch (Exception e) {
                LOGGER.error("A given path for cxDBName is not found!");
            }
            try {
                f = new File(resultFolder);
            } catch (Exception e) {
                LOGGER.error("A given path for resultFolder is not found!");
            }

            try {
                f = new File(mgfs);
            } catch (Exception e) {
                LOGGER.error("A given folder path for mgfs is not found!");
            }

            // get a contaminant database...
            if (!contaminantDBName.isEmpty()) {
                insilicoContaminantDBName = contaminantDBName.substring(0, contaminantDBName.indexOf(".fasta")) + "_in_silico.fasta";
            }
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
            int minLen = ConfigHolder.getInstance().getInt("minLen"),
                    maxLen_for_combined = ConfigHolder.getInstance().getInt("maxLenCombined"),
                    //IntensityPart for MSAmanda derived is 0 for SQRT(IP), 1 for IP, 2 is for original explained intensity function..
                    //intensity_option = ConfigHolder.getInstance().getInt("intensityOptionMSAmanda"),-was for testing scoring types.
                    intensity_option = 0,
                    minFPeakNumPerWindow = ConfigHolder.getInstance().getInt("minimumFiltedPeaksNumberForEachWindow"),
                    maxFPeakNumPerWindow = ConfigHolder.getInstance().getInt("maximumFiltedPeaksNumberForEachWindow"),
                    threadNum = ConfigHolder.getInstance().getInt("threadNumbers"),
                    //Meaning that there is a restriction that there must be at least x theoretical peaks from both peptides to be assigned (0:None, 1:1 for each) ---MP
                    peakRequiredForImprovedSearch = ConfigHolder.getInstance().getInt("minRequiredPeaks"),
                    maxModsPerPeptide = ConfigHolder.getInstance().getInt("maxModsPerPeptide"),
                    neutralLossesCase = ConfigHolder.getInstance().getInt("consider_neutrallosses");
            // multithreading
            ExecutorService excService = Executors.newFixedThreadPool(threadNum);
            // more cross linking option..;
            boolean does_link_to_itself = ConfigHolder.getInstance().getBoolean("allowIntraPeptide"),
                    //Keep scores equal to zero (either probability of matched peak is zero or none matched peaks)
                    doesRecordZeroes = false,
                    shownInPPM = ConfigHolder.getInstance().getBoolean("report_in_ppm"), // Relative or absolute precursor tolerance
                    //                    doesKeepCPeptideFragmPattern = ConfigHolder.getInstance().getBoolean("keepCPeptideFragmPattern"),
                    doesKeepCPeptideFragmPattern = false,
                    searchForAlsoMonoLink = ConfigHolder.getInstance().getBoolean("searchForAlsoMonoLink"),
                    doesKeepIonWeights = false,
                    // a setting when I tried to merged different fragment ion types but it must be off by setting as false
                    // isContrastLinkedAttachmentOn = ConfigHolder.getInstance().getBoolean("isDifferentIonTypesMayTogether"),
                    isContrastLinkedAttachmentOn = false,
                    // settings to count if there an experimental peak is matched to the same theoretical peak and counting these experimental peaks separately
                    // doesFindAllMatchedPeaks=T
                    doesFindAllMatchedPeaks = ConfigHolder.getInstance().getBoolean("isAllMatchedPeaks"),
                    isSettingRunBefore = true,
                    isPercolatorAsked = ConfigHolder.getInstance().getBoolean("isPercolatorAsked"),
                    // A parameter introduced to check if percolator input will have a feature on ion-ratio (did not improve the results)
                    hasIonWeights = false;
            // Searching against experimental spectrum
            double msms_tol = ConfigHolder.getInstance().getDouble("msms_tol"), //Fragment tolerance - mz diff
                    massWindow = ConfigHolder.getInstance().getDouble("massWindow"), // mass window to make window on a given MSnSpectrum for FILTERING
                    minPrecMassIsotopicPeakSelected = ConfigHolder.getInstance().getDouble("minPrecMassIsotopicPeakSelected"), // min precursor mass that C13 peak is selected over C12 peak on precursor.
                    // values for deisotoping and deconvoluting..
                    deisotopePrecision = ConfigHolder.getInstance().getDouble("deisotopePrecision"),
                    deconvulatePrecision = ConfigHolder.getInstance().getDouble("deisotopePrecision");
            // get all peptide tolerance mass windows
            ArrayList<PeptideTol> pep_tols = getPepTols(ConfigHolder.getInstance());
            // this object will be set for every new spectra in order to clean precursor peaks and deisotoping-deconvoluting..
            MascotAdaptedPrecursorPeakRemoval precursorPeakRemove = new MascotAdaptedPrecursorPeakRemoval(null, msms_tol);
            DeisotopingAndDeconvoluting deisotopeAndDeconvolute = new DeisotopingAndDeconvoluting(null, deisotopePrecision, deconvulatePrecision);
            // A CrossLinker object, required for constructing theoretical spectra - get required cross linkers together
            ArrayList<CrossLinker> linkers = new ArrayList<CrossLinker>();
            boolean isLabeled = true;
            if (labeledOption.equals("B")) { // both isotope labeled and non-labeled ones
                try {
                    linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, true));
                    linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, false));
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            } else if (labeledOption.equals("F")) { // light labeled
                try {
                    linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, false));
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            } else if (labeledOption.equals("T")) { // heavy labeled
                try {
                    linkers.add(GetCrossLinker.getCrossLinker(crossLinkerName, true));
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
            boolean isSideReactionConsidered_S = checkEnablingSideReactionOption(crossLinkerName, 0),
                    isSideReactionConsidered_T = checkEnablingSideReactionOption(crossLinkerName, 1),
                    isSideReactionConsidered_Y = checkEnablingSideReactionOption(crossLinkerName, 2);

            LOGGER.info("The settings are ready to perform the search!");

            // STEP 2: CHECK IF PREVIOUSLY A CXDB WAS CONSTRUCTED!!
            LOGGER.info("Checking if a previously constructed CX database exists for the same search settings!");
            // This part of the code makes sure that an already generated CXDB is not constructed again..
            File cxDB = new File(cxDBName + ".fastacp"),
                    settings = new File(cxDB.getAbsoluteFile().getParent() + File.separator + "settings.txt"),
                    indexFile = new File(cxDBNameIndexFile);

            boolean isSame = false,
                    doesCXDBExist = false;
            HashMap<String, Integer> acc_and_length = CreateDatabase.getAccession_and_length(givenDBName),
                    contaminant_acc_lenght = new HashMap<String, Integer>();
            if (!contaminantDBName.isEmpty()) {
                contaminant_acc_lenght = CreateDatabase.getAccession_and_length(contaminantDBName);
            }
            if (settings.exists()) {
                isSame = isSameDBSetting(settings); // either the same/different/empty
                // Seems the database setting is the same, so check if there is now constructed crosslinked peptide database exists...
                if (isSame) {
                    for (File tmp : cxDB.getParentFile().listFiles()) {
                        if (tmp.getName().equals(cxDB.getName())) {
                            LOGGER.info("A previously constructed CX database file is found! The name is " + tmp.getName());
                            doesCXDBExist = true;
                        }
                    }
                }
            }

            // STEP 3: CREATE A CROSS-LINKED DATABASE!!!
            // Either the same settings but absent CXDB or not the same settings at all..
            HashMap<String, StringBuilder> headers_sequences = new HashMap<String, StringBuilder>();
            if ((isSame && !doesCXDBExist) || !isSame) {
                // Construct a cross linked peptide database and write an index file with masses...
                LOGGER.info("Either a CX database is not found or the settings are different! A CX database is going to be constructed..");
                CreateDatabase instanceToCreateDB = new CreateDatabase(givenDBName, inSilicoPeptideDBName,
                        cxDBName, // db related parameters
                        crossLinkerName, // crossLinker name
                        crossLinkedProteinTypes, // crossLinking type: Both/Inter/Intra
                        enzymeName, enzymeFileName, misclevaged, // enzyme related parameters
                        lowMass, highMass, // filtering of in silico peptides on peptide masses
                        minLen, // minimum length for each in silico digested peptide
                        maxLen_for_combined, // maximum lenght for a length for cross linked peptide (maxLen<len(A)+len(B)
                        does_link_to_itself, // if a peptide itself links to itself..
                        isLabeled, isSideReactionConsidered_S, isSideReactionConsidered_T, isSideReactionConsidered_Y);
                // generate cross-linked peptides database
                headers_sequences = instanceToCreateDB.getHeadersAndSequences();
                // add in silico digested contaminant database
                if (!contaminantDBName.isEmpty()) {
                    EnzymeDigest o = new EnzymeDigest();
                    o.main(insilicoContaminantDBName, enzymeFileName, enzymeName, contaminantDBName, null, lowMass, highMass, null, misclevaged);
                    addContaminants(headers_sequences, insilicoContaminantDBName, 4, 25);
                    // delete the contaminant file
                    LOGGER.debug(insilicoContaminantDBName);
                    File insilicoContaminantDB = new File(insilicoContaminantDBName);
                    insilicoContaminantDB.delete();
                }
                // add mono-linked peptides to a cross-linked database
                if (searchForAlsoMonoLink) {
                    headers_sequences.putAll(instanceToCreateDB.getMonolinkedHeadersAndSequences());
                }
                // first write down a cross-linked peptide database
                WriteCXDB.writeCXDB(headers_sequences, cxDBName);
                LOGGER.info("A CX database is now ready!");
                // now write a settings file
                isSettingRunBefore = false;
            }

            // STEP 4: GENERATE INDEXES!! Load db entries to memory for Lucene indexing search
            File folder = new File(cxDB.getParentFile().getPath() + File.separator + "index");
            // Make sure that an index file also exists...
            if (!folder.exists()) {
                folder.mkdir();
            }
            HashSet<StringBuilder> all_headers = new HashSet<StringBuilder>(),
                    tmp_headers = new HashSet<StringBuilder>();
            if (folder.listFiles().length == 0 || !isSame) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));
                // add contaminants..
                tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_for_contaminants(bw, headers_sequences, ptmFactory,
                        fixedModifications, variableModifications, maxModsPerPeptide,
                        fragMode, isContrastLinkedAttachmentOn, contaminant_acc_lenght);
                all_headers.addAll(tmp_headers);
                // accession numbers with protein lengths from a given fasta-protein database
                for (CrossLinker linker : linkers) {
                    // cross-linked peptides
                    tmp_headers = FASTACPDBLoader.generate_peptide_mass_index(bw, headers_sequences, ptmFactory,
                            fixedModifications, variableModifications, maxModsPerPeptide,
                            linker, fragMode, isContrastLinkedAttachmentOn, acc_and_length);
                    all_headers.addAll(tmp_headers);
                    // mono-linked peptides
                    if (searchForAlsoMonoLink) {
                        tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw, headers_sequences, ptmFactory,
                                fixedModifications, variableModifications, maxModsPerPeptide,
                                linker, fragMode, acc_and_length);
                        all_headers.addAll(tmp_headers);
                    }
                }
                bw.close();

                // delete a file containing all mass indexes for cross-linked peptides with possible modification
                indexFile.delete();
                LOGGER.info("An index file (including peptides and masses) has been created!");
                // delete in silico DBs
                File tmp_f = new File(inSilicoPeptideDBName),
                        tmp_cF = new File(insilicoContaminantDBName);
                tmp_f.delete();
                tmp_cF.delete();
            }

            // STEP 5: PREPARE LUCENCE INDEXING!
            IndexAndSearch search = null;
            try {
                search = new IndexAndSearch(all_headers, folder, ptmFactory, fragMode, crossLinkerName);
            } catch (Exception ex) {
                LOGGER.error(ex);
            }

            // STEP 6: SCORE PEPTIDE-TO-SPECTRUM MATCHES!!!
            LOGGER.info("The identification starts and XPSMs are calculated!");
            // Title for percolator-input
            StringBuilder percolatorInputTitle = writePercolatorTitle();
            SpectrumFactory fct = SpectrumFactory.getInstance();
            File mgf_folder;
            try {
                mgf_folder = new File(mgfs);
                for (File mgf : mgf_folder.listFiles()) {
                    if (mgf.getName().endsWith(".mgf")) {
                        LOGGER.info("The MS/MS spectra currently searched are from " + mgf.getName());
                        LOGGER.debug(resultFolder + File.separator + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt");
                        File percolatorIntra,
                                percolatorInter;
                        BufferedWriter bw_intra = null,
                                bw_inter = null;
                        if (isPercolatorAsked) {
                            percolatorIntra = new File(resultFolder + File.separator + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt");
                            percolatorInter = new File(resultFolder + File.separator + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_inter_percolator" + ".txt");
                            bw_intra = new BufferedWriter(new FileWriter(percolatorIntra));
                            bw_inter = new BufferedWriter(new FileWriter(percolatorInter));
                            bw_intra.write(percolatorInputTitle + "\n");
                            bw_inter.write(percolatorInputTitle + "\n");
                        }

                        // write results on output file for each mgf
                        BufferedWriter bw = new BufferedWriter(new FileWriter(resultFolder + File.separator + "" + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass" + ".txt"));
                        bw.write("Xilmass version " + version);
                        bw.newLine();
                        StringBuilder titleToWrite = prepareTitle(shownInPPM, doesKeepCPeptideFragmPattern, doesKeepIonWeights);
                        bw.write(titleToWrite + "\n");

                        // now check all spectra to collect all required calculations...
                        List<Future<ArrayList<Result>>> futureList = null;
                        if (mgf.getName().endsWith("mgf")) {
                            HashSet<String> ids = new HashSet<String>(); // to give every time unique ids for each entry on percolator input
                            fct.addSpectra(mgf);
                            ArrayList<Result> results = new ArrayList<Result>(),
                                    info = new ArrayList<Result>(),
                                    percolatorInfoResults = new ArrayList<Result>();
                            ArrayList<StringBuilder> percolatorInfo = new ArrayList<StringBuilder>();
                            int total_spectra = fct.getSpectrumTitles(mgf.getName()).size(),
                                    tmp_total_spectra = 0;
                            for (String title : fct.getSpectrumTitles(mgf.getName())) {
                                tmp_total_spectra++;
                                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                                // Xilmass can identify spectra with known charges only. pLink and MSAmanda follows this strategy. 
                                if (ms.getPrecursor().getPossibleCharges().size() == 1 && !ms.getPrecursor().getPossibleChargesAsString().isEmpty()) {
                                    // first remove any isotopic peaks derived from precursor peak.
                                    precursorPeakRemove.setExpMSnSpectrum(ms);
                                    ms = precursorPeakRemove.getPrecursorPeaksRemovesExpMSnSpectrum();
                                    // then deisotoping and harge state deconvolution
                                    deisotopeAndDeconvolute.setExpMSnSpectrum(ms);
                                    ms = deisotopeAndDeconvolute.getDeisotopedDeconvolutedExpMSnSpectrum();
                                    if (tmp_total_spectra % 500 == 0) {
                                        LOGGER.info("Number of total ID spectra is " + tmp_total_spectra + " in total " + total_spectra + " spectra, and currently ID spectrum is " + ms.getSpectrumTitle());
                                    }
                                    // making sure that a spectrum contains peaks after preprocessing..
                                    if (!ms.getPeakList().isEmpty()) {
                                        try {
                                            // here comes to check each mgf several mass windows..
                                            futureList = fillFutures(ms, pep_tols, shownInPPM, scoreName, msms_tol, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow,
                                                    massWindow, doesFindAllMatchedPeaks, doesKeepCPeptideFragmPattern, doesKeepIonWeights,
                                                    excService, search, peakRequiredForImprovedSearch, minPrecMassIsotopicPeakSelected, neutralLossesCase);
                                        } catch (XmlPullParserException ex) {
                                            LOGGER.error(ex);
                                        } catch (Exception ex) {
                                            LOGGER.error(ex);
                                        }
                                        for (Future<ArrayList<Result>> future : futureList) {
                                            try {
                                                results = future.get();
                                                info = new ArrayList<Result>();
                                                for (Result res : results) {
                                                    double tmpScore = res.getScore();
                                                    if ((tmpScore > 0 && !doesRecordZeroes) || doesRecordZeroes) {
                                                        info.add(res);
                                                        bw.write(res.toPrint());
                                                        bw.newLine();
                                                    }
                                                }
                                                if (isPercolatorAsked) {
                                                    // write all res and also percolator input
                                                    percolatorInfo = new ArrayList<StringBuilder>();
                                                    percolatorInfoResults = new ArrayList<Result>();
                                                    for (Result r : info) {
                                                        CrossLinkingType linkingType = r.getCp().getLinkingType();
                                                        if (linkingType.equals(CrossLinkingType.CROSSLINK)) {
                                                            StringBuilder i = getPercolatorInfoNoIDs(r, (CPeptides) r.getCp(), ptmFactory);
                                                            if (!percolatorInfo.contains(i)) {
                                                                percolatorInfo.add(i);
                                                                percolatorInfoResults.add(r);
                                                            }
                                                        }
                                                    }
                                                    ids = new HashSet<String>(); // to give every time unique ids for each entry on percolator input
                                                    for (Result r : percolatorInfoResults) {
                                                        write(r, bw_inter, bw_intra, ids, ptmFactory);
                                                    }
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                LOGGER.error(e);
                                            }
                                        }
                                    }
                                }
                            }
                            fct.clearFactory();
                        }
                        bw.close();
                        if (isPercolatorAsked) {
                            bw_intra.close();
                            bw_inter.close();
                        }
                    }
                }
                writeSettings(settings, startDate, isSettingRunBefore, ("Xilmass version " + version));
                LOGGER.info("The settings-file is ready.");
                long end = System.currentTimeMillis();
                LOGGER.info("The cross linked peptide database search lasted in " + +((end - startTime) / 1000) + " seconds.");
                excService.shutdown();
                // here validate the results!        
                String analysis = "11",
                        xilmassResFolder = resultFolder,
                        scoringFunctionName = "AndromedaDerived",
                        isImprovedFDR = ConfigHolder.getInstance().getString("isImprovedFDR"),
                        fdrInterPro = ConfigHolder.getInstance().getString("fdrInterPro"),
                        fdrIntraPro = ConfigHolder.getInstance().getString("fdrIntraPro"),
                        fdr = ConfigHolder.getInstance().getString("fdr");
                NameTargetDecoy.main(new String[]{analysis, xilmassResFolder, scoringFunctionName, "validatedXPSMs_list.txt", "allXPSMs_list.txt",
                    fdrInterPro, fdrIntraPro, fdr, isImprovedFDR, ConfigHolder.getInstance().getString("report_in_ppm")});
                // write settings on an output folder
                writeSettings(new File(xilmassResFolder + File.separator + "settings.txt"), startDate, isSettingRunBefore, ("Xilmass version " + version));
            } catch (Exception e) {
                LOGGER.error("A given path for mgf folder is not found!");
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
        }
    }

    private static StringBuilder prepareTitle(boolean isMS1PPM, boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights) throws IOException {
        StringBuilder ms1Err = new StringBuilder("MS1Err(PPM)"),
                absMS1Err = new StringBuilder("AbsMS1Err(PPM)");
        if (!isMS1PPM) {
            ms1Err = new StringBuilder("MS1Err(Da)");
            absMS1Err = new StringBuilder("AbsMS1Err(Da)");
        }
        StringBuilder fileTitle = new StringBuilder();
        fileTitle.append("File").append("\t").append("SpectrumTitle").
                append("\t").append("ScanNumber").append("\t").append("RetentionTime(Seconds)").append("\t").
                append("ObservedMass(Da)").append("\t").append("PrecCharge").append("\t").append("CalculatedMass_Da").append("\t").append(ms1Err).append("\t").append(absMS1Err).append("\t").
                append("PeptideA").append("\t").append("ProteinA").append("\t").append("ModA").append("\t").
                append("PeptideB").append("\t").append("ProteinB").append("\t").append("ModB").append("\t").
                append("LinkPeptideA").append("\t").append("LinkPeptideB").append("\t").
                append("LinkProteinA").append("\t").append("LinkProteinB").append("\t").
                append("LinkingType").append("\t").
                append("Score").append("\t").append("DeltaScore").append("\t").
                append("ln(NumSp)").append("\t").append("ln(NumXSpec)").append("\t").
                append("#MatchedPeaks").append("\t").append("#MatchedTheoPeaks").append("\t").
                append("MatchedPeakList").append("\t").append("MatchedTheoPeakList").append("\t").
                append("Labeling");
        if (doesKeepCPeptideFragmPattern) {
            fileTitle.append("\t").append("CPeptideFragPatternName");
        }
        if (doesKeepWeights) {
            fileTitle.append("\t").append("IntroducedIonWeight");
        }
        return fileTitle;
    }

    /**
     * Construct Options.
     */
    private static void constructOptions() {
        options = new Options();

        options.addOption("h", "help", Boolean.FALSE, "Help");
        options.addOption("u", "usage", Boolean.FALSE, "Usage");

        Option commandLineOption = new Option("c", "command_line", false, "Command-line mode");
        commandLineOption.setArgName("command_line");
        Option startupGuiOption = new Option("s", "startup_gui", false, "Startup GUI mode");
        startupGuiOption.setArgName("startup_gui");
        Option resultsGuiOption = new Option("r", "results_gui", false, "Results GUI mode");
        resultsGuiOption.setArgName("results_gui");
        OptionGroup commandLineModeOptionGroup = new OptionGroup();
        commandLineModeOptionGroup.addOption(commandLineOption);
        commandLineModeOptionGroup.addOption(startupGuiOption);
        commandLineModeOptionGroup.addOption(resultsGuiOption);

        options.addOptionGroup(commandLineModeOptionGroup);
    }

    /**
     * Display example application header.
     *
     * @out OutputStream to which header should be written.
     */
    private static void displayHeader(OutputStream out) {
        try {
            out.write(HEADER.getBytes());
        } catch (IOException ioEx) {
            System.out.println(HEADER);
        }
    }

    /**
     * Write the provided number of blank lines to the provided OutputStream.
     *
     * @param numberBlankLines Number of blank lines to write.
     * @param out OutputStream to which to write the blank lines.
     */
    private static void displayBlankLines(
            int numberBlankLines,
            OutputStream out) {
        try {
            for (int i = 0; i < numberBlankLines; ++i) {
                out.write("\n".getBytes());
            }
        } catch (IOException ioEx) {
            for (int i = 0; i < numberBlankLines; ++i) {
                System.out.println();
            }
        }
    }

    /**
     * Print usage information to provided OutputStream.
     *
     * @param applicationName Name of application to list in usage.
     * @param options Command-line options to be part of usage.
     * @param out OutputStream to which to write the usage information.
     */
    private static void printUsage(
            String applicationName,
            Options options,
            OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.flush();
    }

    /**
     * Write "help" to the provided OutputStream.
     */
    private static void printHelp(
            Options options,
            int printedRowWidth,
            String header,
            String footer,
            int spacesBeforeOption,
            int spacesBeforeOptionDescription,
            boolean displayUsage,
            final OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                USAGE,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);
        writer.flush();
    }

    /**
     * Apply Apache Commons CLI parser to command-line arguments.
     *
     * @param commandLineArguments Command-line arguments to be processed.
     */
    private static void parse(String[] commandLineArguments) {
        CommandLineParser cmdLineParser = new BasicParser();
        CommandLine commandLine;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
            if (commandLine.getOptions().length == 0) {
                //launch startup GUI mode
                launchStartupGuiMode();
            }
            if (commandLine.hasOption('h')) {
                printHelp(
                        options, 80, "Help", "End of Help",
                        5, 3, true, System.out);
            }
            if (commandLine.hasOption('u')) {
                printUsage(USAGE, options, System.out);
            }
            if (commandLine.hasOption("c")) {
                try {
                    //launch command line mode
                    launchCommandLineMode();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    printHelp(
                            options, 80, "Help", "End of Help",
                            5, 3, true, System.out);
                }
            }
            if (commandLine.hasOption('s')) {
                //launch startup GUI mode
                launchStartupGuiMode();
            }
            if (commandLine.hasOption('r')) {
                //launch results GUI mode
                launchResultsGui();
            }
        } catch (ParseException parseException) {
            System.out.println("Encountered exception while parsing :\n"
                    + parseException.getMessage());
            printHelp(
                    options, 80, "Help", "End of Help",
                    5, 3, true, System.out);
        }
    }

    /**
     * Run xilmass in startup GUI mode.
     */
    private static void launchStartupGuiMode() {
        try {
            /**
             * Set the Nimbus look and feel.
             */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
             */
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            //set background color for JOptionPane and JPanel instances
            UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
            //set background color for JFileChooser instances
            UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                    (Painter<JFileChooser>) new Painter<JFileChooser>() {
                @Override
                public void paint(Graphics2D g, JFileChooser object, int width, int height) {
                    g.setColor(Color.WHITE);
                    g.draw(object.getBounds());
                }
            });
            //</editor-fold>

            MainController mainController = new MainController();
            mainController.init();
            mainController.showView();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            //add message to JTextArea
            JTextArea textArea = new JTextArea(ERROR_MESSAGE + System.lineSeparator() + System.lineSeparator() + ex.getMessage());
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(null, scrollPane, "Xilmass startup GUI error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Run xilmass in results viewer GUI mode.
     */
    private static void launchResultsGui() {
        /**
         * Set the Nimbus look and feel.
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //set background color for JOptionPane and JPanel instances
        UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
        //set background color for JFileChooser instances
        UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                (Painter<JFileChooser>) new Painter<JFileChooser>() {
            @Override
            public void paint(Graphics2D g, JFileChooser object, int width, int height) {
                g.setColor(Color.WHITE);
                g.draw(object.getBounds());
            }
        });
        //</editor-fold>

        //Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Visualize().setVisible(true);
                } catch (MzMLUnmarshallerException | FileNotFoundException | ClassNotFoundException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        });
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
            String[] currMods = ptmNames.split(";");
            // convert to all lower case
            for (String curModName : currMods) {
                mods.add(curModName);
            }
        }
        return mods;
    }

    /**
     * This method generates used for multithreading..
     *
     */
    private static List<Future<ArrayList<Result>>> fillFutures(MSnSpectrum ms,
            ArrayList<PeptideTol> pepTols,
            boolean shownInPPM,
            ScoreName scoreName,
            double fragTol,
            int intensity_option,
            int minFPeakNumPerWindow,
            int maxFPeakNumPerWindow,
            double massWindow,
            boolean doesFindAllMatchedPeaks,
            boolean doesKeepCPeptideFragmPattern,
            boolean doesKeepWeight,
            ExecutorService excService,
            IndexAndSearch search,
            int peakRequiredForImprovedSearch,
            double minPrecMassIsotopicPeakSelected, int neutralLossesCase) throws IOException, MzMLUnmarshallerException, XmlPullParserException, Exception {
        List<Future<ArrayList<Result>>> futureList = new ArrayList<Future<ArrayList<Result>>>();
        // now check all spectra to collect all required calculations...
        // now get query range..
        ArrayList<CrossLinking> selectedCPeptides = new ArrayList<CrossLinking>();
        double precMass = CalculatePrecursorMass.getPrecursorMass(ms);
        for (PeptideTol pepTol : pepTols) {
            // C13 peaks might be selected over C12 peaks if precursor mass is higher than 2500Da,
            // we start observing C13 peaks more abundant than C12 peaks around 1800-2000Da
            if ((pepTol.getPeptide_tol_base() >= (DeisotopingAndDeconvoluting.getDiffC12C13() - fragTol) && precMass > minPrecMassIsotopicPeakSelected)
                    || pepTol.getPeptide_tol_base() < (DeisotopingAndDeconvoluting.getDiffC12C13() - fragTol)) {
//                precMass = precMass - pepTol.getPeptide_tol_base();
                double[] from_to = getRange(precMass, pepTol);
                double from = from_to[0],
                        to = from_to[1];
                ArrayList<CrossLinking> tmpSelectedCPeptides = search.getCPeptidesFromGivenMassRange(from, to);
                // making sure that a cross-linked peptides are not already selected
                for (int s = 0; s < tmpSelectedCPeptides.size(); s++) {
                    CrossLinking tmpS = tmpSelectedCPeptides.get(s);
                    boolean isSelected = false;
                    for (int i = 0; i < selectedCPeptides.size(); i++) {
                        CrossLinking cp = selectedCPeptides.get(i);
                        if ((cp instanceof CPeptides) && (tmpS instanceof CPeptides)) {
                            if (cp.equals(tmpS)) {
                                isSelected = true;
                            }
                        }
                        // also add Contaminant-derived objects
                        if ((cp instanceof Contaminant) && (tmpS instanceof Contaminant)) {
                            if (cp.equals(tmpS)) {
                                isSelected = true;
                            }
                        }
                        // also add monolinked peptides
                        if ((cp instanceof MonoLinkedPeptides) && (tmpS instanceof MonoLinkedPeptides)) {
                            if (cp.equals(tmpS)) {
                                isSelected = true;
                            }
                        }
                    }
                    if (!isSelected && !selectedCPeptides.contains(tmpS)) {
                        selectedCPeptides.add(tmpS);
                    }
                }
            }
        }
        if (!selectedCPeptides.isEmpty()) {
            ScorePSM score = new ScorePSM(selectedCPeptides, ms, scoreName, fragTol, massWindow,
                    intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow, doesFindAllMatchedPeaks,
                    doesKeepCPeptideFragmPattern, doesKeepWeight, shownInPPM, peakRequiredForImprovedSearch, neutralLossesCase);
            Future future = excService.submit(score);
            futureList.add(future);
        }
        return futureList;
    }

    /**
     * This method writes a file containing parameters (etc. input/output
     * information) as "settings.txt" file
     *
     * @param file
     * @throws IOException
     */
    private static void writeSettings(File file, Date startTime, boolean isSettingRunBefore, String versionInfo) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(new StringBuilder("Search settings of ").append(versionInfo).append("\n").append("\n").toString());
        bw.write(new StringBuilder("Running started=").append(startTime.toString()).append("\n").toString());
        bw.write(new StringBuilder("Running ended=").append(new Date().toString()).append("\n").append("\n").toString());
        if (isSettingRunBefore) {
            bw.write(new StringBuilder("A cross-linked peptide database has been previously constructed!").append("\n\n").toString());
        } else {
            bw.write(new StringBuilder("A cross-linked peptide database was constructed for the first time!").append("\n\n").toString());
        }
        // write down all input file related parameters..
        bw.write(new StringBuilder("##Input file related parameters").append("\n").toString());
        bw.write(new StringBuilder("givenDBName=").append(ConfigHolder.getInstance().getString("givenDBName")).append("\n").toString());
        bw.write(new StringBuilder("contaminantDBName=").append(ConfigHolder.getInstance().getString("contaminantDBName")).append("\n").toString());
        bw.write(new StringBuilder("cxDBName=").append(ConfigHolder.getInstance().getString("cxDBName")).append("\n").toString());
        bw.write(new StringBuilder("mgfs=").append(ConfigHolder.getInstance().getString("mgfs")).append("\n").toString());
        bw.write(new StringBuilder("resultFolder=").append(ConfigHolder.getInstance().getString("resultFolder")).append("\n").toString());
        String  index = new File(ConfigHolder.getInstance().getString("cxDBName")).getParentFile().getAbsolutePath() + (File.separator) + ("index").replace("\\", "/");
        bw.write(new StringBuilder("index=").append(index).append("\n\n").toString());
        // write down all cross-linking related parameters
        bw.write(new StringBuilder("##Cross-linking related parameters").append("\n").toString());
        bw.write(new StringBuilder("crossLinkerName=").append(ConfigHolder.getInstance().getString("crossLinkerName")).append("\n").toString());
        bw.write(new StringBuilder("isLabeled=").append(ConfigHolder.getInstance().getString("isLabeled")).append("\n").toString());
        bw.write(new StringBuilder("isConsideredSideReactionSerine=").append(ConfigHolder.getInstance().getString("isConsideredSideReactionSerine")).append("\n").toString());
        bw.write(new StringBuilder("isConsideredSideReactionThreonine=").append(ConfigHolder.getInstance().getString("isConsideredSideReactionThreonine")).append("\n").toString());
        bw.write(new StringBuilder("isConsideredSideReactionTyrosine=").append(ConfigHolder.getInstance().getString("isConsideredSideReactionTyrosine")).append("\n").toString());
        bw.write(new StringBuilder("crossLinkedProteinTypes=").append(ConfigHolder.getInstance().getString("crossLinkedProteinTypes")).append("\n").toString());
        bw.write(new StringBuilder("searchForAlsoMonoLink=").append(ConfigHolder.getInstance().getString("searchForAlsoMonoLink")).append("\n").toString());
        bw.write(new StringBuilder("minLen=").append(ConfigHolder.getInstance().getString("minLen")).append("\n").toString());
        bw.write(new StringBuilder("maxLenCombined=").append(ConfigHolder.getInstance().getString("maxLenCombined")).append("\n").toString());
        bw.write(new StringBuilder("allowIntraPeptide=").append(ConfigHolder.getInstance().getString("allowIntraPeptide")).append("\n\n").toString());

        // write down all in silico digestion related parameters
        bw.write(new StringBuilder("##In silico digestion related parameters").append("\n").toString());
        bw.write(new StringBuilder("enzymeName=").append(ConfigHolder.getInstance().getString("enzymeName")).append("\n").toString());
        bw.write(new StringBuilder("miscleavaged=").append(ConfigHolder.getInstance().getString("miscleavaged")).append("\n").toString());
        bw.write(new StringBuilder("lowerMass=").append(ConfigHolder.getInstance().getString("lowerMass")).append("\n").toString());
        bw.write(new StringBuilder("higherMass=").append(ConfigHolder.getInstance().getString("higherMass")).append("\n").append("\n").toString());

        // write down all peptide modification related parameters
        bw.write(new StringBuilder("##Peptide modification related parameters").append("\n").toString());
        bw.write(new StringBuilder("fixedModification=").append(ConfigHolder.getInstance().getString("fixedModification")).append("\n").toString());
        bw.write(new StringBuilder("variableModification=").append(ConfigHolder.getInstance().getString("variableModification")).append("\n").toString());
        bw.write(new StringBuilder("maxModsPerPeptide=").append(ConfigHolder.getInstance().getString("maxModsPerPeptide")).append("\n\n").toString());

        // write down all scoring related parameters
        bw.write(new StringBuilder("##Scoring related parameters").append("\n").toString());
        bw.write(new StringBuilder("consider_neutrallosses=").append(ConfigHolder.getInstance().getString("consider_neutrallosses")).append("\n").toString());
        bw.write(new StringBuilder("fragModeName=").append(ConfigHolder.getInstance().getString("fragMode")).append("\n").toString());
        bw.write("\n");
        bw.write(new StringBuilder("peptide_tol_total=").append(ConfigHolder.getInstance().getString("peptide_tol_total")).append("\n").toString());
        // write each peptide-tolerance mass window on given setting-parameters
        int pep_tol_nums = Integer.parseInt(ConfigHolder.getInstance().getString("peptide_tol_total"));
        for (int pep_tol_num = 1; pep_tol_num <= pep_tol_nums; pep_tol_num++) {
            String name = "peptide_tol" + pep_tol_num,
                    is_ppm = "is_peptide_tol" + pep_tol_num + "_PPM",
                    base = "peptide_tol" + pep_tol_num + "_base";
            bw.write(new StringBuilder(name).append("=").append(ConfigHolder.getInstance().getString(name)).append("\n").toString());
            bw.write(new StringBuilder(is_ppm).append("=").append(ConfigHolder.getInstance().getString(is_ppm)).append("\n").toString());
            bw.write(new StringBuilder(base).append("=").append(ConfigHolder.getInstance().getString(base)).append("\n").toString());
        }
        bw.write("\n");

        bw.write(new StringBuilder("msms_tol=").append(ConfigHolder.getInstance().getString("msms_tol")).append("\n").toString());
        bw.write(new StringBuilder("report_in_ppm=").append(ConfigHolder.getInstance().getString("report_in_ppm")).append("\n").toString());
        bw.write(new StringBuilder("minRequiredPeaks=").append(ConfigHolder.getInstance().getString("minRequiredPeaks")).append("\n").toString());
        bw.write(new StringBuilder("isAllMatchedPeaks=").append(ConfigHolder.getInstance().getString("isAllMatchedPeaks")).append("\n\n").toString());

        // write down all spectrum preprocessing-parameters
        bw.write(new StringBuilder("##Spectrum preprocessing related parameters").append("\n").toString());
        bw.write(new StringBuilder("massWindow=").append(ConfigHolder.getInstance().getString("massWindow")).append("\n").toString());
        bw.write(new StringBuilder("minimumFiltedPeaksNumberForEachWindow=").append(ConfigHolder.getInstance().getString("minimumFiltedPeaksNumberForEachWindow")).append("\n").toString());
        bw.write(new StringBuilder("maximumFiltedPeaksNumberForEachWindow=").append(ConfigHolder.getInstance().getString("maximumFiltedPeaksNumberForEachWindow")).append("\n\n").toString());
        bw.write(new StringBuilder("minPrecMassIsotopicPeakSelected=").append(ConfigHolder.getInstance().getString("minPrecMassIsotopicPeakSelected")).append("\n").toString());

        bw.write(new StringBuilder("deisotopePrecision=").append(ConfigHolder.getInstance().getString("deisotopePrecision")).append("\n").toString());
        bw.write(new StringBuilder("deconvulatePrecision=").append(ConfigHolder.getInstance().getString("deconvulatePrecision")).append("\n\n").toString());

        // write each Multithreading and validation related parameters
        bw.write(new StringBuilder("##Multithreading and validation related parameters").append("\n").toString());
        bw.write(new StringBuilder("threadNumbers=").append(ConfigHolder.getInstance().getString("threadNumbers")).append("\n").toString());
        bw.write(new StringBuilder("isPercolatorAsked=").append(ConfigHolder.getInstance().getString("isPercolatorAsked")).append("\n").toString());
        bw.write(new StringBuilder("isImprovedFDR=").append(ConfigHolder.getInstance().getString("isImprovedFDR")).append("\n").toString());
        bw.write(new StringBuilder("fdrInterPro=").append(ConfigHolder.getInstance().getString("fdrInterPro")).append("\n").toString());
        bw.write(new StringBuilder("fdrIntraPro=").append(ConfigHolder.getInstance().getString("fdrIntraPro")).append("\n").toString());
        bw.write(new StringBuilder("fdr=").append(ConfigHolder.getInstance().getString("fdr")).append("\n").append("\n").toString());
        bw.close();
    }

    /**
     * This method checks if parameters to construct a xlinked database are the
     * same as before.
     *
     * @param paramFile a parameter/setting file which contains all input and
     * output information (settings.txt on the output folder)
     * @return
     * @throws IOException
     */
    public static boolean isSameDBSetting(File paramFile) throws IOException {
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                //indexFolder = ConfigHolder.getInstance().getString("indexFolder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                isLabeled = ConfigHolder.getInstance().getString("isLabeled"),
                isConsideredSideReactionSerine = ConfigHolder.getInstance().getString("isConsideredSideReactionSerine"),
                isConsideredSideReactionThreonine = ConfigHolder.getInstance().getString("isConsideredSideReactionThreonine"),
                isConsideredSideReactionTyrosine = ConfigHolder.getInstance().getString("isConsideredSideReactionTyrosine"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                searchForAlsoMonoLink = ConfigHolder.getInstance().getString("searchForAlsoMonoLink"),
                minLen = ConfigHolder.getInstance().getString("minLen"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                allowIntraPeptide = ConfigHolder.getInstance().getString("allowIntraPeptide"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowerMass = ConfigHolder.getInstance().getString("lowerMass"),
                higherMass = ConfigHolder.getInstance().getString("higherMass"),
                fixedModification = ConfigHolder.getInstance().getString("fixedModification"),
                variableModification = ConfigHolder.getInstance().getString("variableModification"),
                maxModsPerPeptide = ConfigHolder.getInstance().getString("maxModsPerPeptide"),
                index = new File(ConfigHolder.getInstance().getString("cxDBName")).getParentFile().getAbsolutePath() + (File.separator) + ("index").replace("\\", "/");
        int control = 0;
        boolean isSame = false;
        BufferedReader br = new BufferedReader(new FileReader(paramFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            if ((line.startsWith("givenDBName")) && (line.split("=")[1].equals(givenDBName))) {
                control++;
            } else if (((line.startsWith("contaminantDBName")) && (line.split("=").length == 2) && (line.split("\t")[1].equals(contaminantDBName)))
                    || (line.startsWith("contaminantDBName")) && (line.split("=").length == 1)) {
                control++;
            } else if ((line.startsWith("cxDBName")) && (line.split("=")[1].equals(cxDBName))) {
                control++;
            } else if ((line.startsWith("index")) && (line.split("=")[1].equals(index))) {
                control++;
            } else if ((line.startsWith("crossLinkerName")) && (line.split("=")[1].equals(crossLinkerName))) {
                control++;
            } else if ((line.startsWith("isLabeled")) && (line.split("=")[1].equals(isLabeled))) {
                control++;
            } else if ((line.startsWith("isConsideredSideReactionSerine")) && (line.split("=")[1].equals(isConsideredSideReactionSerine))) {
                control++;
            } else if ((line.startsWith("isConsideredSideReactionThreonine")) && (line.split("=")[1].equals(isConsideredSideReactionThreonine))) {
                control++;
            } else if ((line.startsWith("isConsideredSideReactionTyrosine")) && (line.split("=")[1].equals(isConsideredSideReactionTyrosine))) {
                control++;
            } else if ((line.startsWith("crossLinkedProteinTypes")) && (line.split("=")[1].equals(crossLinkedProteinTypes))) {
                control++;
            } else if ((line.startsWith("searchForAlsoMonoLink")) && (line.split("=")[1].equals(searchForAlsoMonoLink))) {
                control++;
            } else if ((line.startsWith("minLen")) && (line.split("=")[1].equals(minLen))) {
                control++;
            } else if ((line.startsWith("maxLenCombined")) && (line.split("=")[1].equals(maxLenCombined))) {
                control++;
            } else if ((line.startsWith("allowIntraPeptide")) && (line.split("=")[1].equals(allowIntraPeptide))) {
                control++;
            } else if ((line.startsWith("enzymeName")) && (line.split("=")[1].equals(enzymeName))) {
                control++;
            } else if ((line.startsWith("miscleavaged")) && (line.split("=")[1].equals(misclevaged))) {
                control++;
            } else if ((line.startsWith("lowerMass")) && (line.split("=")[1].equals(lowerMass))) {
                control++;
            } else if ((line.startsWith("higherMass")) && (line.split("=")[1].equals(higherMass))) {
                control++;
            } else if ((line.startsWith("fixedModification")) && line.split("=").length == 2 && (line.split("=")[1].equals(fixedModification))) {
                control++;
            } else if ((line.startsWith("variableModification")) && line.split("=").length == 2 && line.split("=")[1].equals(variableModification)) {
                control++;
            } else if ((line.startsWith("maxModsPerPeptide")) && (line.split("=")[1].equals(maxModsPerPeptide))) {
                control++;
            }
        }
        if (control == 21) {
            isSame = true;
        }
        return isSame;
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
    private static void addContaminants(HashMap<String, StringBuilder> headers_sequences, String contaminantDB, int minLen, int maxLen) throws IOException {
        File contaminant = new File(contaminantDB);
        DBLoader loader = DBLoaderLoader.loadDB(contaminant);
        Protein startProtein = null;
        // get a crossLinkerName object
        while ((startProtein = loader.nextProtein()) != null) {
            String startHeader = startProtein.getHeader().getAccession(),
                    startSequence = startProtein.getSequence().getSequence();
            // check if a header comes from a generic!
//            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
//            if (startHeader.startsWith("contaminant")) {
            if (startSequence.length() > minLen && startSequence.length() <= maxLen) {
                StringBuilder h = new StringBuilder().append("contaminant_").append(startHeader.substring(0, startHeader.indexOf("(")));
                headers_sequences.put(h.toString(), new StringBuilder(startSequence));
            }
        }
        contaminant.delete();
    }

    /**
     * This method prepare title for each percolator inputs
     *
     * @return
     * @throws IOException
     */
    private static StringBuilder writePercolatorTitle() throws IOException {
        StringBuilder title = new StringBuilder();
        title.append("SpecID").append("\t").append("Label").append("\t").append("scannr").append("\t").
                append("massDelta_ppm").append("\t").
                append("score").append("\t").
                append("charge").append("\t").append("observedMass_Da").append("\t").append("calculatedMass_Da").append("\t").
                append("CrossLinkerLabeling").append("\t").
                append("lenPepA").append("\t").append("lenPepB").append("\t").append("sumLen").append("\t").
                append("lnNumSp").append("\t").append("Peptide").append("\t").append("Protein");
        return (title);
    }

    /**
     * This method writes down each Result for percolator-inputs
     */
    private static StringBuilder getPercolatorInfo(Result res, CPeptides c, HashSet<String> ids, PTMFactory ptmFactory) throws IOException {
        StringBuilder scn = new StringBuilder(res.getScanNum()),
                target = new StringBuilder(""),
                labelInfo = new StringBuilder("0");
        String id = "";
        int label = -1,
                pepALen = c.getPeptideA().getSequence().length(),
                pepBLen = c.getPeptideB().getSequence().length(),
                sumLen = pepALen + pepBLen;
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false,
                isLabeled = c.getLinker().isIsLabeled();
        if (isLabeled) {
            labelInfo = new StringBuilder("1");
        }
        if (c.getProteinA().contains("REVERSED") || c.getProteinA().contains("SHUFFLED") || c.getProteinA().contains("DECOY")) {
            isProteinAdecoy = true;
        }
        if (c.getProteinB().contains("REVERSED") || c.getProteinB().contains("SHUFFLED") || c.getProteinB().contains("DECOY")) {
            isProteinBdecoy = true;
        }
        if (!isProteinAdecoy && !isProteinBdecoy) {
            label = 1;
            target = new StringBuilder("T-");
        } else {
            target = new StringBuilder("D-");
        }
        id = target.toString() + scn.toString();

        if (ids.contains(id)) {
            int i = 2;
            id = target.toString() + scn.toString() + "-" + (i);
            while (ids.contains(id)) {
                i++;
                id = target.toString() + scn.toString() + "-" + (i);
            }
        }
        ids.add(id);

        // because only cross linked peptides are selected for scoring!
        CPeptides cp = (CPeptides) res.getCp();
        int linkerA = cp.getLinker_position_on_peptideA() + 1,
                linkerB = cp.getLinker_position_on_peptideB() + 1;
        StringBuilder input = new StringBuilder();
        input.append(id).append("\t").append(label).append("\t").append(scn).append("\t")
                .append(res.getDeltaMass()).append("\t")
                .append(res.getScore()).append("\t")
                .append(res.getCharge()).append("\t")
                .append(res.getObservedMass()).append("\t")
                .append(labelInfo).append("\t")
                .append(pepALen).append("\t").append(pepBLen).append("\t").append(sumLen).append("\t")
                .append(res.getLnNumSpec()).append("\t").append(res.getLnNumXSpec()).append("\t")
                .append("-.").append(cp.getSequenceWithPtms(cp.getPeptideA(), ptmFactory)).append("(").append(linkerA).append(")").append("--") // PeptideA Sequence
                .append(cp.getSequenceWithPtms(cp.getPeptideB(), ptmFactory)).append("(").append(linkerB).append(")").append(".-").append("\t") // PeptideBSequence part
                .append(cp.getProteinA()).append("-").append(cp.getProteinB());
        return input;
    }

    /**
     * This method generates Percolator Input entries without scanIDs
     *
     *
     * @param res
     * @param c
     * @param hasIonWeight
     * @param ptmFactory
     * @return
     * @throws IOException
     */
    private static StringBuilder getPercolatorInfoNoIDs(Result res, CPeptides c, PTMFactory ptmFactory) throws IOException {
        StringBuilder scn = new StringBuilder(res.getScanNum()),
                labelInfo = new StringBuilder("0");
        int label = -1,
                pepALen = c.getPeptideA().getSequence().length(),
                pepBLen = c.getPeptideB().getSequence().length(),
                sumLen = pepALen + pepBLen;
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false,
                isLabeled = c.getLinker().isIsLabeled();
        if (isLabeled) {
            labelInfo = new StringBuilder("1");
        }
        if (c.getProteinA().contains("REVERSED") || c.getProteinA().contains("SHUFFLED") || c.getProteinA().contains("DECOY")) {
            isProteinAdecoy = true;
        }
        if (c.getProteinB().contains("REVERSED") || c.getProteinB().contains("SHUFFLED") || c.getProteinB().contains("DECOY")) {
            isProteinBdecoy = true;
        }
        if (!isProteinAdecoy && !isProteinBdecoy) {
            label = 1;
        }
        // because only cross linked peptides are selected for scoring!
        CPeptides cp = (CPeptides) res.getCp();
        StringBuilder input = new StringBuilder();
        input.append(label).append("\t").append(scn).append("\t")
                .append(res.getDeltaMass()).append("\t")
                .append(res.getScore()).append("\t")
                .append(res.getCharge()).append("\t")
                .append(res.getObservedMass()).append("\t")
                .append(labelInfo).append("\t")
                .append(pepALen).append("\t").append(pepBLen).append("\t").append(sumLen).append("\t")
                .append(res.getLnNumSpec()).append("\t").append(res.getLnNumXSpec()).append("\t")
                .append("-.").append(cp.getSequenceWithPtms(cp.getPeptideA(), ptmFactory)).append("(").append(cp.getLinker_position_on_peptideA()).append(")").append("--") // PeptideA Sequence
                .append(cp.getSequenceWithPtms(cp.getPeptideB(), ptmFactory)).append("(").append(cp.getLinker_position_on_peptideB()).append(")").append(".-") // PeptideBSequence part
                .append("\t").append(cp.getProteinA()).append("-").append(cp.getProteinB());
        return input;
    }

    /**
     * This method allows writing down results for either intra-proteins or
     * inter-proteins
     */
    private static void write(Result res, BufferedWriter bw_inter, BufferedWriter bw_intra, HashSet<String> ids, PTMFactory ptmFactory) throws IOException {
        if (res.getCp() instanceof CPeptides) {
            CPeptides c = (CPeptides) res.getCp();
            if (c.getType().equals("interProtein")) {
                // write to bw_inter
                bw_inter.write(getPercolatorInfo(res, c, ids, ptmFactory) + "\n");
            }
            if (c.getType().equals("intraProtein")) {
                // write to bw_intra
                bw_intra.write(getPercolatorInfo(res, c, ids, ptmFactory) + "\n");
            }
        }
    }

    /**
     * This method getRange of theoretical masses (in Da) for a given
     * precursorTolerance
     *
     * @param precMass observed precursor mass
     * @param pepTol a peptide_tol mass window object
     * @return an array of lower (O.value) and upper (1.value) mass range
     */
    public static double[] getRange(double precMass, PeptideTol pepTol) {
        boolean isPPM = pepTol.isPPM();
        double[] from_to = new double[2];
        double upper_limit = pepTol.getUpper_limit(), // already in Dalton
                lower_limit = pepTol.getLower_limit(),// already in Dalton
                from = precMass + lower_limit,
                to = precMass + upper_limit;
        if (isPPM) {
            double mass = ((precMass - pepTol.getPeptide_tol_base()) * 1000000),
                    shiftDown = (1000000 + pepTol.getPeptide_tol()),
                    shiftUp = (1000000 - pepTol.getPeptide_tol());
            from = (mass / shiftDown);
            to = (mass / shiftUp);

        }
        from_to[0] = from;
        from_to[1] = to;
        return from_to;
    }

    /**
     * This method forces to delete given directory
     *
     * @param folder
     * @return
     */
    static public boolean deleteDirectory(File folder) {
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (folder.delete());
    }

    /**
     * This method retrieves information about the peptide tolerance mass
     * windows on xLink.properties file
     *
     * @param instance is a ConfigHolder instance object holding properties for
     * Xilmass algorithm.
     * @return
     */
    public static ArrayList<PeptideTol> getPepTols(ConfigHolder instance) {
        ArrayList<PeptideTol> pep_tols = new ArrayList<PeptideTol>();
        //the total number of peptide tolerance mass windows
        int num_pep_tols = instance.getInt("peptide_tol_total");
        if (num_pep_tols > 5 && num_pep_tols < 1) {
            LOGGER.error("Xilmass cannot be executed! Invalid peptide_tol_total! Must be between 1 and 5!");
            System.exit(1);
        } else {
            LOGGER.info("There are currently " + num_pep_tols + " mass windows!");
            // now feel all peptide mass tolerance mass windows..
            for (int tmp_pep_tol = 1; tmp_pep_tol <= num_pep_tols; tmp_pep_tol++) {
                String key = "peptide_tol" + tmp_pep_tol;
                // just making sure that actually ConfigHolder contains this key..
                if (instance.containsKey(key)) {
                    boolean is_peptide_tol_ppm = instance.getBoolean("is_" + key + "_PPM");
                    double peptide_tol = instance.getDouble(key),
                            peptide_tol_base = instance.getDouble(key + "_base"); //
                    PeptideTol pep_tol = new PeptideTol(is_peptide_tol_ppm, peptide_tol, peptide_tol_base, key);
                    pep_tols.add(pep_tol);
                }
            }
        }
        return pep_tols;
    }

    private static boolean checkEnablingSideReactionOption(String crossLinkerName, int option) {
        boolean isEnabled = false;
        if (crossLinkerName.equals("DSS") || crossLinkerName.equals("BS3")) {
            if (option == 0) {
                isEnabled = ConfigHolder.getInstance().getBoolean("isConsideredSideReactionSerine");
            } else if (option == 1) {
                isEnabled = ConfigHolder.getInstance().getBoolean("isConsideredSideReactionThreonine");
            } else if (option == 2) {
                isEnabled = ConfigHolder.getInstance().getBoolean("isConsideredSideReactionTyrosine");
            }
        }
        return isEnabled;
    }

    /**
     * Send an event to the google analytics server for tool start monitoring.
     */
    private static void sendAnalyticsEvent() {
        String COLLECT_URL = "http://www.google-analytics.com/collect";
        String POST = "v=1&tid=UA-36198780-12&cid=35119a79-1a05-49d7-b876-bb88420f825b&uid=asuueffeqqss&t=event&ec=usage&ea=toolstart&el=xilmass";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> request = new HttpEntity<String>(POST);
            ResponseEntity<String> postForEntity
                    = restTemplate.postForEntity(COLLECT_URL,
                            request, String.class);

            if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
                LOGGER.info("Successfully sent analytics event.");
            }
        } catch (RestClientException ex) {
            LOGGER.getLogger("Failed to connect to internet.");
        }
    }

}
