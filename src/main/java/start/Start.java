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
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
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
import start.lucene.LuceneIndexSearch;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.CrossLinking;
import theoretical.CrossLinkingType;
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
        double version = 0.0;
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta",
                insilicoContaminantDBName = "",
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
                resultFile = ConfigHolder.getInstance().getString("resultFolder"),
                fixedModificationNames = ConfigHolder.getInstance().getString("fixedModification"), // must be sepeared by semicolumn, lowercase, no space
                variableModificationNames = ConfigHolder.getInstance().getString("variableModification"),
                fragModeName = ConfigHolder.getInstance().getString("fragMode"),
                //scoring = ConfigHolder.getInstance().getString("scoring"),
                scoring = "TheoMSAmandaDerived",
                labeledOption = ConfigHolder.getInstance().getString("isLabeled");
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
        ptmFactory.importModifications(new File(modsFileName), false);
        int minLen = ConfigHolder.getInstance().getInt("minLen"),
                maxLen_for_combined = ConfigHolder.getInstance().getInt("maxLenCombined"),
                //IntensityPart for MSAmanda derived is 0 for SQRT(IP), 1 for IP, 2 is for original explained intensity function..
                //intensity_option = ConfigHolder.getInstance().getInt("intensityOptionMSAmanda"),
                intensity_option = 1,
                minFPeakNumPerWindow = ConfigHolder.getInstance().getInt("minimumFiltedPeaksNumberForEachWindow"),
                maxFPeakNumPerWindow = ConfigHolder.getInstance().getInt("maximumFiltedPeaksNumberForEachWindow"),
                threadNum = ConfigHolder.getInstance().getInt("threadNumbers"),
                //# Meaning that there is a restriction that there must be at least x theoretical peaks from both peptides to be assigned (0:None, 1:1 for each) ---MP
                //peakRequiredForImprovedSearch=0                
                peakRequiredForImprovedSearch = 0,
                maxModsPerPeptide = ConfigHolder.getInstance().getInt("maxModsPerPeptide");

        // multithreading 
        ExecutorService excService = Executors.newFixedThreadPool(threadNum);
        // more cross linking option..;
        boolean does_link_to_itself = ConfigHolder.getInstance().getBoolean("allowIntraPeptide"),
                //# Keep scores equal to zero (either probability of matched peak is zero or none matched peaks)
                //recordZeroScore=F
                doesRecordZeroes = false,
                isPPM = ConfigHolder.getInstance().getBoolean("isMS1PPM"), // Relative or absolute precursor tolerance 
                doesKeepCPeptideFragmPattern = ConfigHolder.getInstance().getBoolean("keepCPeptideFragmPattern"),
                searcForAlsoMonoLink = ConfigHolder.getInstance().getBoolean("searcForAlsoMonoLink"),
                doesKeepIonWeights = true,
                // a setting when I tried to merged different fragment ion types but it must be off by setting as false
                // isContrastLinkedAttachmentOn = ConfigHolder.getInstance().getBoolean("isDifferentIonTypesMayTogether"),
                isContrastLinkedAttachmentOn = false,
                //  settings to count if there an experimental peak is matched to the same theoretical peak and counting these experimental peaks separately
                //doesFindAllMatchedPeaks=T
                doesFindAllMatchedPeaks = true,
                isPercolatorAsked = ConfigHolder.getInstance().getBoolean("isPercolatorAsked"),
                // A parameter introduced to check if percolator input will have a feature on ion-ratio (did not improve the results)
                hasIonWeights = true;
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
                doesCXDBExist = false;
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
                }
            }
        }
        // here load db entries to memory for Lucene indexing search
        File folder = new File(indexFile.getParentFile().getPath() + File.separator + "index");
        if (!folder.exists()) {
            folder.mkdir();
        }
        // Either the same settings but no CXDB found or not the same settings at all..
        HashMap<String, String> headers_sequences = new HashMap<String, String>();
        if ((isSame && !doesCXDBExist) || !isSame || (folder.listFiles().length == 0)) {
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
                    isLabeled); //
            LOGGER.info("Construction of header_sequence..");
            headers_sequences = instanceToCreateDB.getHeadersAndSequences();
            // in silico digested contaminant database
            if (!contaminantDBName.isEmpty()) {
                EnzymeDigest o = new EnzymeDigest();
                o.main(insilicoContaminantDBName, enzymeFileName, enzymeName, contaminantDBName, null, lowMass, highMass, null, misclevaged);
                addContaminants(headers_sequences, insilicoContaminantDBName, 4, 25);
                // delete the contaminant file
                LOGGER.debug(insilicoContaminantDBName);
                File insilicoContaminantDB = new File(insilicoContaminantDBName);
                insilicoContaminantDB.delete();
            }
            // first write down a cross-linked peptide database
            WriteCXDB.writeCXDB(headers_sequences, cxDBName);
            LOGGER.info("A CX database is now ready!");
            // now write a settings file
            writeSettings(settings);
        }
        // TODO: Check for mono linked searching...
        if (searcForAlsoMonoLink && !headers_sequences.isEmpty()) {
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, maxModsPerPeptide);
            }
            bw2.close();
            LOGGER.info("An index (peptide-mass index) file for monolinks bas been created!");
        } else if (searcForAlsoMonoLink && headers_sequences.isEmpty() && folder.listFiles().length == 0) {
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
                    isLabeled); //
            headers_sequences = instanceToCreateDB.getHeadersAndSequences();
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, maxModsPerPeptide);
            }
            bw2.close();
        }
        // here load db entries to memory for Lucene indexing search
        HashSet<StringBuilder> all_headers = new HashSet<StringBuilder>(),
                tmp_headers = new HashSet<StringBuilder>();
        // if this folder is empty...
        if (folder.listFiles().length == 0) {
            // Make sure that an index file also exists...
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexFile));
            tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_for_contaminants(bw2,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    fragMode, isContrastLinkedAttachmentOn, maxModsPerPeptide);
            all_headers.addAll(tmp_headers);
            for (CrossLinker linker : linkers) {
                tmp_headers = FASTACPDBLoader.generate_peptide_mass_index(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, isContrastLinkedAttachmentOn, maxModsPerPeptide);
                all_headers.addAll(tmp_headers);
                if (searcForAlsoMonoLink) {
                    tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                            headers_sequences, ptmFactory,
                            fixedModifications,
                            variableModifications,
                            linker, fragMode, maxModsPerPeptide);
                    all_headers.addAll(tmp_headers);
                }
            }
            bw2.close();
            // uncomment to store all index files here..           
            indexFile.delete();
            LOGGER.info("An index (peptide-mass index) file bas been created!");
            // delete in silico DBs
            File f = new File(inSilicoPeptideDBName),
                    cF = new File(insilicoContaminantDBName);
            f.delete();
            cF.delete();
        }
        // enable searching on indexed xlinked proteins...
        LuceneIndexSearch search = new LuceneIndexSearch(all_headers, folder, ptmFactory, fragMode, isContrastLinkedAttachmentOn, crossLinkerName);
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
                LOGGER.debug(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt");
                File percolatorIntra = new File(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt"),
                        percolatorInter = new File(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_inter_percolator" + ".txt");
                BufferedWriter bw_intra = new BufferedWriter(new FileWriter(percolatorIntra)),
                        bw_inter = new BufferedWriter(new FileWriter(percolatorInter));
                bw_intra.write(percolatorInputTitle + "\n");
                bw_inter.write(percolatorInputTitle + "\n");

                // write results on output file for each mgf
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile + "" + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmas" + ".txt"));
                // write the version number
                bw.write("Xilmass version " + version);
                bw.newLine();
                StringBuilder titleToWrite = prepareTitle(isPPM, doesKeepCPeptideFragmPattern, doesKeepIonWeights);
                bw.write(titleToWrite + "\n");
                // now check all spectra to collect all required calculations...
                SpectrumFactory fct = SpectrumFactory.getInstance();
                if (mgf.getName().endsWith("mgf")) {
                    LOGGER.info("Scoring starts now!" + " Spectra file=" + mgf.getName());
                    fct.addSpectra(mgf, new WaitingHandlerCLIImpl());
                    for (String title : fct.getSpectrumTitles(mgf.getName())) {
                        MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                        List<Future<ArrayList<Result>>> futureList = fillFutures(ms, ms1Err, isPPM, scoreName, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow,
                                massWindow, doesFindAllMatchedPeaks, doesKeepCPeptideFragmPattern, doesKeepIonWeights, excService, search);
                        for (Future<ArrayList<Result>> future : futureList) {
                            try {
                                // Write each result on an output file...
                                ArrayList<Result> results = future.get();
                                ArrayList<Result> info = new ArrayList<Result>();
                                for (Result res : results) {
                                    double tmpScore = res.getScore();
                                    // making sure that only crosslinked ones are written, neither monolinks nor contaminants.
                                    if (((tmpScore > 0 && !doesRecordZeroes) || doesRecordZeroes)
                                            && (res.getCp().getLinkingType().equals(CrossLinkingType.CROSSLINK) || res.getCp().getLinkingType().equals(CrossLinkingType.CONTAMINANT))) {
                                        if (peakRequiredForImprovedSearch > 0) {
                                            boolean hasEnoughPeaks = hasEnoughPeaks(new ArrayList<CPeptidePeak>(res.getMatchedCTheoPeaks()), peakRequiredForImprovedSearch);
                                            if (hasEnoughPeaks) {
                                                info.add(res);
                                                bw.write(res.toPrint());
                                                bw.newLine();
                                            }
                                        }
                                        if (peakRequiredForImprovedSearch == 0) {
                                            info.add(res);
                                            bw.write(res.toPrint());
                                            bw.newLine();
                                        }
                                    }
                                }
                                if (isPercolatorAsked) {
                                    // write all res and also percolator input
                                    ArrayList<String> percolatorInfo = new ArrayList<String>();
                                    ArrayList<Result> percolatorInfoResults = new ArrayList<Result>();
                                    for (Result r : info) {
                                        CrossLinkingType linkingType = r.getCp().getLinkingType();
                                        if (linkingType.equals(CrossLinkingType.CROSSLINK)) {
                                            String i = getPercolatorInfoNoIDs(r, (CPeptides) r.getCp(), ptmFactory);
                                            if (!percolatorInfo.contains(i)) {
                                                percolatorInfo.add(i);
                                                percolatorInfoResults.add(r);
                                            }
                                        }
                                    }
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
                bw.close();
                bw_intra.close();
                bw_inter.close();
            }
        }
        LOGGER.info("Cross linked database search is done!");
        excService.shutdown();
    }

    private static StringBuilder prepareTitle(boolean isMS1PPM, boolean doesKeepCPeptideFragmPattern, boolean doesKeepWeights) throws IOException {
        String ms1Err = "MS1Err(PPM)",
                absMS1Err = "AbsMS1Err(PPM)";
        if (!isMS1PPM) {
            ms1Err = "MS1Err(Da)";
            absMS1Err = "AbsMS1Err(Da)";
        }
        StringBuilder fileTitle = new StringBuilder(
                "File" + "\t" + "SpectrumTitle" + "\t" + "ScanNumber" + "\t" + "RetentionTime(Seconds)" + "\t"
                + "ObservedMass(Da)" + "\t" + "PrecCharge" + "\t" + ms1Err + "\t" + absMS1Err + "\t"
                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t"
                + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
                + "LinkingType" + "\t"
                + "Score" + "\t"
                + "ln(NumSp)" + "\t"
                + "#MatchedPeaks" + "\t" + "#MatchedTheoPeaks" + "\t"
                + "MatchedPeakList" + "\t" + "MatchedTheoPeakList" + "\t"
                + "Labeling");
        if (doesKeepCPeptideFragmPattern) {
            fileTitle.append("\t").append("CPeptideFragPatternName");
        }
        if (doesKeepWeights) {
            fileTitle.append("\t").append("IntroducedIonWeight");
        }
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
            // convert to all lower case            
            for (String fixed_modification_name : fixed_modifications_name_split) {
                mods.add(fixed_modification_name.toLowerCase());
            }
        }
        return mods;
    }

    /**
     * This method generates used for multithreading..
     *
     */
    private static List<Future<ArrayList<Result>>> fillFutures(MSnSpectrum ms,
            double precTol, boolean isPPM,
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
            LuceneIndexSearch search) throws IOException, MzMLUnmarshallerException, XmlPullParserException, Exception {
        List<Future<ArrayList<Result>>> futureList = new ArrayList<Future<ArrayList<Result>>>();
        // now check all spectra to collect all required calculations...
        // now get query range..
        double precMass = CalculatePrecursorMass.getPrecursorMass(ms);
        double[] from_to = getRange(precMass, precTol, isPPM);
        double from = from_to[0],
                to = from_to[1];
        ArrayList<CrossLinking> selectedCPeptides = search.getQuery(from, to);
        if (!selectedCPeptides.isEmpty()) {
            ScorePSM score = new ScorePSM(selectedCPeptides, ms, scoreName, fragTol, massWindow,
                    intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow, doesFindAllMatchedPeaks,
                    isPPM, doesKeepCPeptideFragmPattern, doesKeepWeight);
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
                resultFolder = ConfigHolder.getInstance().getString("resultFolder"),
                fixedModificationNames = ConfigHolder.getInstance().getString("fixedModification"), // must be sepeared by semicolumn, lowercase, no space
                variableModificationNames = ConfigHolder.getInstance().getString("variableModification"),
                fragModeName = ConfigHolder.getInstance().getString("fragMode"),
                minLen = ConfigHolder.getInstance().getString("minLen"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                allowIntraPeptide = ConfigHolder.getInstance().getString("allowIntraPeptide"),
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
        bw.write("resultFile" + resultFolder + "\t" + "\n");
        bw.write("fixedModificationNames" + "\t" + fixedModificationNames + "\n");
        bw.write("variableModificationNames" + "\t" + variableModificationNames + "\n");
        bw.write("fragModeName" + "\t" + fragModeName + "\n");
        bw.write("minLen" + "\t" + minLen + "\n");
        bw.write("maxLenCombined" + "\t" + maxLenCombined + "\n");
        bw.write("allowIntraPeptide" + "\t" + allowIntraPeptide);
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
                minLen = ConfigHolder.getInstance().getString("minLen"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                allowIntraPeptide = ConfigHolder.getInstance().getString("allowIntraPeptide"),
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
            } else if ((line.startsWith("variableModification")) && line.split("\t").length > 1 && line.split("\t")[1].equals(variableModification)) {
                control++;
            } else if (line.split("\t").length == 1) {
                control++;
            } else if ((line.startsWith("isLabeled")) && (line.split("\t")[1].equals(isLabeled))) {
                control++;
            } else if ((line.startsWith("minLen")) && (line.split("\t")[1].equals(minLen))) {
                control++;
            } else if ((line.startsWith("maxLenCombined")) && (line.split("\t")[1].equals(maxLenCombined))) {
                control++;
            } else if ((line.startsWith("allowIntraPeptide")) && (line.split("\t")[1].equals(allowIntraPeptide))) {
                control++;
            }
        }
        if (control == 13) {
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
        contaminant.delete();
    }

    /**
     * This method prepare title for each percolator inputs
     *
     * @return
     * @throws IOException
     */
    private static String writePercolatorTitle() throws IOException {
        String title = "SpecID" + "\t" + "Label" + "\t" + "scannr" + "\t"
                + "massDelta_ppm" + "\t"
                + "score" + "\t"
                + "charge" + "\t" + "observedMass_Da" + "\t"
                + "CrossLinkerLabeling" + "\t"
                + "lenPepA" + "\t" + "lenPepB" + "\t" + "sumLen" + "\t"
                + "lnNumSp" + "\t"
                + "Peptide" + "\t"
                + "Protein";
        return (title);
    }

    /**
     * This method writes down each Result for percolator-inputs
     */
    private static String getPercolatorInfo(Result res, CPeptides c, HashSet<String> ids, PTMFactory ptmFactory) throws IOException {
        String id = "",
                scn = res.getScanNum(),
                target = "",
                labelInfo = "0";
        int label = -1,
                pepALen = c.getPeptideA().getSequence().length(),
                pepBLen = c.getPeptideB().getSequence().length(),
                sumLen = pepALen + pepBLen;
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false,
                isLabeled = c.getLinker().isIsLabeled();
        if (isLabeled) {
            labelInfo = "1";
        }
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
        // because only cross linked peptides are selected for scoring!
        CPeptides cp = (CPeptides) res.getCp();
        int linkerA = cp.getLinker_position_on_peptideA() + 1,
                linkerB = cp.getLinker_position_on_peptideB() + 1;
        String input = id + "\t" + label + "\t" + scn + "\t"
                + res.getDeltaMass() + "\t"
                + res.getScore() + "\t"
                + res.getCharge() + "\t"
                + res.getObservedMass() + "\t"
                + labelInfo + "\t"
                + pepALen + "\t" + pepBLen + "\t" + sumLen + "\t"
                + res.getLnNumSpec() + "\t"
                + "-." + cp.getSequenceWithPtms(cp.getPeptideA(), ptmFactory) + "(" + linkerA + ")" + "--" // PeptideA Sequence
                + cp.getSequenceWithPtms(cp.getPeptideB(), ptmFactory) + "(" + linkerB + ")" + ".-" // PeptideBSequence part              
                + "\t"
                + cp.getProteinA() + "-" + cp.getProteinB();
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
    private static String getPercolatorInfoNoIDs(Result res, CPeptides c, PTMFactory ptmFactory) throws IOException {
        String scn = res.getScanNum(),
                labelInfo = "0";
        int label = -1,
                pepALen = c.getPeptideA().getSequence().length(),
                pepBLen = c.getPeptideB().getSequence().length(),
                sumLen = pepALen + pepBLen;
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false,
                isLabeled = c.getLinker().isIsLabeled();
        if (isLabeled) {
            labelInfo = "1";
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
        String input = label + "\t" + scn + "\t"
                + res.getDeltaMass() + "\t"
                + res.getScore() + "\t"
                + res.getCharge() + "\t"
                + res.getObservedMass() + "\t"
                + labelInfo + "\t"
                + pepALen + "\t" + pepBLen + "\t" + sumLen + "\t"
                + res.getLnNumSpec() + "\t"
                + "-." + cp.getSequenceWithPtms(cp.getPeptideA(), ptmFactory) + "(" + cp.getLinker_position_on_peptideA() + ")" + "--" // PeptideA Sequence
                + cp.getSequenceWithPtms(cp.getPeptideB(), ptmFactory) + "(" + cp.getLinker_position_on_peptideB() + ")" + ".-" // PeptideBSequence part              
                + "\t"
                + cp.getProteinA() + "-" + cp.getProteinB();
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
            } else {
                // write to bw_intra
                bw_intra.write(getPercolatorInfo(res, c, ids, ptmFactory) + "\n");
            }
        }
    }

    /**
     * This method getRange of theoretical masses for a given precursorTolerance
     */
    /**
     * This method getRange of theoretical masses for a given precursorTolerance
     *
     * @param precMass observed precursor mass
     * @param isPPM true: ms1Err is on ppm, false: ms1Err is Da
     * @param precTol precursor tolerance
     * @return an array of lower (O.value) and upper (1.value) mass range
     */
    public static double[] getRange(double precMass, double precTol, boolean isPPM) {
        double[] from_to = new double[2];
        double from = precMass - precTol,
                to = precMass + precTol;
        if (isPPM) {
            double mass = (precMass * 1000000),
                    shiftDown = (1000000 + precTol),
                    shiftUp = (1000000 - precTol);
            from = mass / shiftDown;
            to = mass / shiftUp;
        }
        from_to[0] = from;
        from_to[1] = to;
        return from_to;
    }

}
