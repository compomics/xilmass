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
import com.compomics.util.protein.Protein;
import config.ConfigHolder;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import database.CreateDatabase;
import database.FASTACPDBLoader;
import database.WriteCXDB;
import multithread.score.Result;
import multithread.score.ScorePSM;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.xmlpull.v1.XmlPullParserException;
import scoringFunction.ScoreName;
import start.lucene.IndexAndSearch;
import theoretical.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;
import util.ResourceUtils;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

        long startTime = System.currentTimeMillis();
        Date startDate = new Date();

        // STEP 1: DATABASE GENERATIONS!
        String version = ConfigHolder.getInstance().getString("xilmass.version");
        LOGGER.info("Xilmass version:" + version + " starts!");
        String givenDBName = ConfigHolder.getInstance().getString("givenDBName"),
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                inSilicoPeptideDBName = givenDBName.substring(0, givenDBName.indexOf(".fasta")) + "_in_silico.fasta",
                insilicoContaminantDBName = "",
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                cxDBNameIndexFile = cxDBName + ".index", // An index file from already generated cross linked protein database
                monoLinkFile = cxDBName + "_monoLink.index",
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes").toLowerCase(),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
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
        // load enzyme and modification files from a resource folder
        Resource resourceByRelativePath = ResourceUtils.getResourceByRelativePath("mods.xml");
        File modFile = resourceByRelativePath.getFile();
        resourceByRelativePath = ResourceUtils.getResourceByRelativePath("enzymes.txt");
        File enzymeFile = resourceByRelativePath.getFile();
        String enzymeFileName = enzymeFile.toString();

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
//        System.out.println(modFile.getName());
        ptmFactory.importModifications(modFile, false);
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
                doesKeepIonWeights = false,
                // a setting when I tried to merged different fragment ion types but it must be off by setting as false
                // isContrastLinkedAttachmentOn = ConfigHolder.getInstance().getBoolean("isDifferentIonTypesMayTogether"),
                isContrastLinkedAttachmentOn = false,
                //  settings to count if there an experimental peak is matched to the same theoretical peak and counting these experimental peaks separately
                //doesFindAllMatchedPeaks=T
                doesFindAllMatchedPeaks = true,
                isSettingRunBefore = true,
                isPercolatorAsked = ConfigHolder.getInstance().getBoolean("isPercolatorAsked"),
                // A parameter introduced to check if percolator input will have a feature on ion-ratio (did not improve the results)
                hasIonWeights = false;
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
        LOGGER.info("The settings are ready to perform the search!");
        LOGGER.info("Checking if a previously constructed CX database exists for the same search settings!");
        // This part of the code makes sure that an already generated CXDB is not constructed again..
        File cxDB = new File(cxDBName + ".fastacp"),
                settings = new File(cxDB.getAbsoluteFile().getParent() + File.separator + "settings.txt"),
                indexFile = new File(cxDBNameIndexFile),
                indexMonoLinkFile = new File(monoLinkFile);
        boolean isSame = false,
                doesCXDBExist = false;
        HashMap<String, Integer> acc_and_length = CreateDatabase.getAccession_and_length(givenDBName),
                contaminant_acc_lenght = new HashMap<String, Integer>();
        if(!contaminantDBName.isEmpty()){
            contaminant_acc_lenght = CreateDatabase.getAccession_and_length(contaminantDBName);
        }
        if (settings.exists()) {
            isSame = isSameDBSetting(settings); // either the same/different/empty
            // Seems the database setting is the same, so check if there is now constructed crosslinked peptide database exists...
            if (isSame) {
                for (File f : cxDB.getParentFile().listFiles()) {
                    if (f.getName().equals(cxDB.getName())) {
                        LOGGER.info("A previously constructed CX database file is found! The name is " + f.getName());
                        doesCXDBExist = true;
                    }
                }
            }
        }
        // here load db entries to memory for Lucene indexing search
        File folder = new File(cxDB.getParentFile().getPath() + File.separator + "index");
        if (!folder.exists()) {
            folder.mkdir();
        }
        // Either the same settings but no CXDB found or not the same settings at all..
        HashMap<String, String> headers_sequences = new HashMap<String, String>();
        if ((isSame && !doesCXDBExist) || !isSame || (folder.listFiles().length == 0)) {
            // clean the index folder..
            deleteDirectory(folder);
            folder.mkdir();
            // Construct a cross linked peptide database and write an index file with masses...
            LOGGER.info("Either a CX database is not found or the settings are different! A CX database is going to be constructed..");
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
            isSettingRunBefore = false;
        }
        // TODO: Check for mono linked searching...
        if (searcForAlsoMonoLink && !headers_sequences.isEmpty()) {
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexMonoLinkFile));
            for (CrossLinker linker : linkers) {
                FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, maxModsPerPeptide, acc_and_length);
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
                        linker, fragMode, maxModsPerPeptide, acc_and_length);
            }
            bw2.close();
        }
        // here load db entries to memory for Lucene indexing search
        HashSet<StringBuilder> all_headers = new HashSet<StringBuilder>(),
                tmp_headers = new HashSet<StringBuilder>();
        // if this folder is empty...
        if (folder.listFiles().length == 0 || !isSame) {
            // Make sure that an index file also exists...
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(indexFile));
            tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_for_contaminants(bw2,
                    headers_sequences, ptmFactory,
                    fixedModifications,
                    variableModifications,
                    fragMode, isContrastLinkedAttachmentOn, maxModsPerPeptide, contaminant_acc_lenght);
            all_headers.addAll(tmp_headers);
            // accession numbers with protein lengths from a given fasta-protein database
            
            for (CrossLinker linker : linkers) {
                tmp_headers = FASTACPDBLoader.generate_peptide_mass_index(bw2,
                        headers_sequences, ptmFactory,
                        fixedModifications,
                        variableModifications,
                        linker, fragMode, isContrastLinkedAttachmentOn, maxModsPerPeptide, acc_and_length);
                all_headers.addAll(tmp_headers);
                if (searcForAlsoMonoLink) {
                    tmp_headers = FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw2,
                            headers_sequences, ptmFactory,
                            fixedModifications,
                            variableModifications,
                            linker, fragMode, maxModsPerPeptide, acc_and_length);
                    all_headers.addAll(tmp_headers);
                }
            }
            bw2.close();
            // uncomment to store all index files here..           
            indexFile.delete();
            LOGGER.info("An index file (including peptides and masses) bas been created!");
            // delete in silico DBs
            File f = new File(inSilicoPeptideDBName),
                    cF = new File(insilicoContaminantDBName);
            f.delete();
            cF.delete();
        }
        // enable searching on indexed xlinked proteins...
        IndexAndSearch search = new IndexAndSearch(all_headers, folder, ptmFactory, fragMode, crossLinkerName);
        // STEP 2: CONSTRUCT CPEPTIDE OBJECTS
        // STEP 3: MATCH AGAINST THEORETICAL SPECTRUM
        // Get all MSnSpectrum! (all MS2 spectra)
        LOGGER.info("The identification starts and XPSMs are calculated!");
        long start = System.currentTimeMillis();
        // Title for percolator-input
        StringBuilder percolatorInputTitle = writePercolatorTitle();
        SpectrumFactory fct = SpectrumFactory.getInstance();
        for (File mgf : new File(mgfs).listFiles()) {
            if (mgf.getName().endsWith(".mgf")) {
                LOGGER.info("The MS/MS spectra currently searched are from " + mgf.getName());
                // prepare percolator inputs
                HashSet<StringBuilder> ids = new HashSet<StringBuilder>(); // to give every time unique ids for each entry on percolator input
                LOGGER.debug(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt");
                File percolatorIntra = new File(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_intra_percolator" + ".txt"),
                        percolatorInter = new File(resultFile + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass_inter_percolator" + ".txt");
                BufferedWriter bw_intra = new BufferedWriter(new FileWriter(percolatorIntra)),
                        bw_inter = new BufferedWriter(new FileWriter(percolatorInter));
                bw_intra.write(percolatorInputTitle + "\n");
                bw_inter.write(percolatorInputTitle + "\n");

                // write results on output file for each mgf
                BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile + "" + mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_xilmass" + ".txt"));
                // write the version number
                bw.write("Xilmass version " + version);
                bw.newLine();
                StringBuilder titleToWrite = prepareTitle(isPPM, doesKeepCPeptideFragmPattern, doesKeepIonWeights);
                bw.write(titleToWrite + "\n");
                // now check all spectra to collect all required calculations...
//                int msNum = 0; 
                List<Future<ArrayList<Result>>> futureList = null;
                if (mgf.getName().endsWith("mgf")) {
                    fct.addSpectra(mgf);
                    ArrayList<Result> results = new ArrayList<Result>(),
                            info = new ArrayList<Result>(),
                            percolatorInfoResults = new ArrayList<Result>();
                    ArrayList<StringBuilder> percolatorInfo = new ArrayList<StringBuilder>();
                    for (String title : fct.getSpectrumTitles(mgf.getName())) {
                        MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
//                        msNum++;
//                        LOGGER.info(msNum+ "\t Spectrum name is " + ms.getSpectrumTitle());
                        futureList = fillFutures(ms, ms1Err, isPPM, scoreName, ms2Err, intensity_option, minFPeakNumPerWindow, maxFPeakNumPerWindow,
                                massWindow, doesFindAllMatchedPeaks, doesKeepCPeptideFragmPattern, doesKeepIonWeights, excService, search);
                        for (Future<ArrayList<Result>> future : futureList) {
                            try {
                                // Write each result on an output file...
                                results = future.get();
                                info = new ArrayList<Result>();
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
                    fct.clearFactory();
                }
                bw.close();
                bw_intra.close();
                bw_inter.close();
            }
        }
        writeSettings(settings, startDate, isSettingRunBefore, ("Xilmass version " + version));
        LOGGER.info("The settings file is ready.");
        long end = System.currentTimeMillis();
        LOGGER.info("The cross linked peptide database search lasted in " + +((end - startTime) / 1000) + " seconds.");
        excService.shutdown();
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
                append("ObservedMass(Da)").append("\t").append("PrecCharge").append("\t").append(ms1Err).append("\t").append(absMS1Err).append("\t").
                append("PeptideA").append("\t").append("ProteinA").append("\t").append("ModA").append("\t").
                append("PeptideB").append("\t").append("ProteinB").append("\t").append("ModB").append("\t").
                append("LinkPeptideA").append("\t").append("LinkPeptideB").append("\t").
                append("LinkProteinA").append("\t").append("LinkProteinB").append("\t").
                append("LinkingType").append("\t").
                append("Score").append("\t").
                append("ln(NumSp)").append("\t").
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
            IndexAndSearch search) throws IOException, MzMLUnmarshallerException, XmlPullParserException, Exception {
        List<Future<ArrayList<Result>>> futureList = new ArrayList<Future<ArrayList<Result>>>();
        // now check all spectra to collect all required calculations...
        // now get query range..
        double precMass = CalculatePrecursorMass.getPrecursorMass(ms);
        double[] from_to = getRange(precMass, precTol, isPPM);
        double from = from_to[0],
                to = from_to[1];
        ArrayList<CrossLinking> selectedCPeptides = search.getCPeptidesFromGivenMassRange(from, to);
        if (!selectedCPeptides.isEmpty()) {
            for (CrossLinking c : selectedCPeptides) {
//                LOGGER.info(c.toPrint()+"\t"+c.getTheoretical_xlinked_mass());
            }
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
    private static void writeSettings(File file, Date startTime, boolean isSettingRunBefore, String versionInfo) throws IOException {
        StringBuilder givenDBName = new StringBuilder(ConfigHolder.getInstance().getString("givenDBName")),
                cxDBName = new StringBuilder(ConfigHolder.getInstance().getString("cxDBName")),
                contaminantDBName = new StringBuilder(ConfigHolder.getInstance().getString("contaminantDBName")),
                crossLinkerName = new StringBuilder(ConfigHolder.getInstance().getString("crossLinkerName")),
                crossLinkedProteinTypes = new StringBuilder(ConfigHolder.getInstance().getString("crossLinkedProteinTypes")),
                enzymeName = new StringBuilder(ConfigHolder.getInstance().getString("enzymeName")),
                miscleavaged = new StringBuilder(ConfigHolder.getInstance().getString("miscleavaged")),
                lowerMass = new StringBuilder(ConfigHolder.getInstance().getString("lowerMass")),
                higherMass = new StringBuilder(ConfigHolder.getInstance().getString("higherMass")),
                mgfs = new StringBuilder(ConfigHolder.getInstance().getString("mgfs")),
                resultFolder = new StringBuilder(ConfigHolder.getInstance().getString("resultFolder")),
                fixedModificationNames = new StringBuilder(ConfigHolder.getInstance().getString("fixedModification")), // must be sepeared by semicolumn, lowercase, no space
                variableModificationNames = new StringBuilder(ConfigHolder.getInstance().getString("variableModification")),
                fragModeName = new StringBuilder(ConfigHolder.getInstance().getString("fragMode")),
                minLen = new StringBuilder(ConfigHolder.getInstance().getString("minLen")),
                maxLenCombined = new StringBuilder(ConfigHolder.getInstance().getString("maxLenCombined")),
                allowIntraPeptide = new StringBuilder(ConfigHolder.getInstance().getString("allowIntraPeptide")),
                isLabeled = new StringBuilder(ConfigHolder.getInstance().getString("isLabeled")),
                keepCPeptideFragmPattern = new StringBuilder(ConfigHolder.getInstance().getString("keepCPeptideFragmPattern")),
                searcForAlsoMonoLink = new StringBuilder(ConfigHolder.getInstance().getString("searcForAlsoMonoLink")),
                maxModsPerPeptide = new StringBuilder(ConfigHolder.getInstance().getString("maxModsPerPeptide")),
                ms1Err = new StringBuilder(ConfigHolder.getInstance().getString("ms1Err")),
                isMS1PPM = new StringBuilder(ConfigHolder.getInstance().getString("isMS1PPM")),
                ms2Err = new StringBuilder(ConfigHolder.getInstance().getString("ms2Err")),
                minimumFiltedPeaksNumberForEachWindow = new StringBuilder(ConfigHolder.getInstance().getString("minimumFiltedPeaksNumberForEachWindow")),
                maximumFiltedPeaksNumberForEachWindow = new StringBuilder(ConfigHolder.getInstance().getString("maximumFiltedPeaksNumberForEachWindow")),
                massWindow = new StringBuilder(ConfigHolder.getInstance().getString("massWindow")),
                threadNumbers = new StringBuilder(ConfigHolder.getInstance().getString("threadNumbers")),
                isPercolatorAsked = new StringBuilder(ConfigHolder.getInstance().getString("isPercolatorAsked"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write("Settings-file for " + versionInfo + "\n");
        bw.write("" + "\n");
        bw.write("Running started=" + startTime.toString() + "\n");
        bw.write("Running ended=" + new Date().toString() + "\n");
        if (isSettingRunBefore) {
            bw.write("A cross-linked peptide database has been already constructed!" + "\n");
        } else {
            bw.write("A cross-linked peptide database was constructed for the first time!" + "\n");
        }
        bw.write("givenDBName=" + givenDBName + "\n");
        bw.write("contaminantDBName=" + contaminantDBName + "\n");
        bw.write("cxDBName=" + cxDBName + "\n");
        bw.write("mgfs=" + mgfs + "\n");
        bw.write("resultFolder=" + resultFolder + "\n");
        bw.write("crossLinkerName=" + crossLinkerName + "\n");
        bw.write("isLabeled=" + isLabeled + "\n");
        bw.write("crossLinkedProteinTypes=" + crossLinkedProteinTypes + "\n");
        bw.write("searcForAlsoMonoLink=" + searcForAlsoMonoLink + "\n");
        bw.write("minLen=" + minLen + "\n");
        bw.write("maxLenCombined=" + maxLenCombined + "\n");
        bw.write("allowIntraPeptide=" + allowIntraPeptide + "\n");
        bw.write("keepCPeptideFragmPattern=" + keepCPeptideFragmPattern + "\n");
        bw.write("enzymeName=" + enzymeName + "\n");
        bw.write("miscleavaged=" + miscleavaged + "\n");
        bw.write("lowerMass=" + lowerMass + "\n");
        bw.write("higherMass=" + higherMass + "\n");
        bw.write("fixedModification=" + fixedModificationNames + "\n");
        bw.write("variableModification=" + variableModificationNames + "\n");
        bw.write("maxModsPerPeptide=" + maxModsPerPeptide + "\n");
        bw.write("fragModeName=" + fragModeName + "\n");
        bw.write("ms1Err=" + ms1Err + "\n");
        bw.write("isMS1PPM=" + isMS1PPM + "\n");
        bw.write("ms2Err=" + ms2Err + "\n");
        bw.write("minimumFiltedPeaksNumberForEachWindow=" + minimumFiltedPeaksNumberForEachWindow + "\n");
        bw.write("maximumFiltedPeaksNumberForEachWindow=" + maximumFiltedPeaksNumberForEachWindow + "\n");
        bw.write("massWindow=" + massWindow + "\n");
        bw.write("threadNumbers=" + threadNumbers + "\n");
        bw.write("isPercolatorAsked=" + isPercolatorAsked);
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
                contaminantDBName = ConfigHolder.getInstance().getString("contaminantDBName"),
                cxDBName = ConfigHolder.getInstance().getString("cxDBName"),
                indexFolder = ConfigHolder.getInstance().getString("indexFolder"),
                crossLinkerName = ConfigHolder.getInstance().getString("crossLinkerName"),
                isLabeled = ConfigHolder.getInstance().getString("isLabeled"),
                crossLinkedProteinTypes = ConfigHolder.getInstance().getString("crossLinkedProteinTypes"),
                searcForAlsoMonoLink = ConfigHolder.getInstance().getString("searcForAlsoMonoLink"),
                minLen = ConfigHolder.getInstance().getString("minLen"),
                maxLenCombined = ConfigHolder.getInstance().getString("maxLenCombined"),
                allowIntraPeptide = ConfigHolder.getInstance().getString("allowIntraPeptide"),
                enzymeName = ConfigHolder.getInstance().getString("enzymeName"),
                misclevaged = ConfigHolder.getInstance().getString("miscleavaged"),
                lowerMass = ConfigHolder.getInstance().getString("lowerMass"),
                higherMass = ConfigHolder.getInstance().getString("higherMass"),
                fixedModification = ConfigHolder.getInstance().getString("fixedModification"),
                variableModification = ConfigHolder.getInstance().getString("variableModification"),
                maxModsPerPeptide = ConfigHolder.getInstance().getString("maxModsPerPeptide");
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
            } else if ((line.startsWith("indexFolder")) && (line.split("=")[1].equals(indexFolder))) {
                control++;
            } else if ((line.startsWith("crossLinkerName")) && (line.split("=")[1].equals(crossLinkerName))) {
                control++;
            } else if ((line.startsWith("isLabeled")) && (line.split("=")[1].equals(isLabeled))) {
                control++;
            } else if ((line.startsWith("crossLinkedProteinTypes")) && (line.split("=")[1].equals(crossLinkedProteinTypes))) {
                control++;
            } else if ((line.startsWith("searcForAlsoMonoLink")) && (line.split("=")[1].equals(searcForAlsoMonoLink))) {
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
        if (control == 17) {
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
    //TODO: STRINGBUILDER!
    private static void addContaminants(HashMap<String, String> headers_sequences, String contaminantDB, int minLen, int maxLen) throws IOException {
        File contaminant = new File(contaminantDB);
        DBLoader loader = DBLoaderLoader.loadDB(contaminant);
        Protein startProtein = null;
        // get a crossLinkerName object        
        while ((startProtein = loader.nextProtein()) != null) {
            String startHeader = startProtein.getHeader().getAccession(),
                    startSequence = startProtein.getSequence().getSequence();
            StringBuilder tmpStartAccession = null;
            // check if a header comes from a generic! 
//            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
//            if (startHeader.startsWith("contaminant")) {
            if (startSequence.length() > minLen && startSequence.length() <= maxLen) {
                tmpStartAccession.append("contaminant_").append(startHeader.substring(0, startHeader.indexOf("(")));
                headers_sequences.put(tmpStartAccession.toString(), startSequence);
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
                append("charge").append("\t").append("observedMass_Da").append("\t").
                append("CrossLinkerLabeling").append("\t").
                append("lenPepA").append("\t").append("lenPepB").append("\t").append("sumLen").append("\t").
                append("lnNumSp").append("\t").append("Peptide").append("\t").append("Protein");
        return (title);
    }

    /**
     * This method writes down each Result for percolator-inputs
     */
    private static StringBuilder getPercolatorInfo(Result res, CPeptides c, HashSet<StringBuilder> ids, PTMFactory ptmFactory) throws IOException {
        StringBuilder id = new StringBuilder(""),
                scn = new StringBuilder(res.getScanNum()),
                target = new StringBuilder(""),
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
            target = new StringBuilder("T-");
        } else {
            target = new StringBuilder("D-");
        }
        id = target.append(scn);
        if (ids.contains(id)) {
            int i = 2;
            while (ids.contains(id)) {
                id = target.append(scn).append("-").append(i);
                i++;
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
                .append(res.getLnNumSpec()).append("\t")
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
                .append(res.getLnNumSpec()).append("\t")
                .append("-.").append(cp.getSequenceWithPtms(cp.getPeptideA(), ptmFactory)).append("(").append(cp.getLinker_position_on_peptideA()).append(")").append("--") // PeptideA Sequence
                .append(cp.getSequenceWithPtms(cp.getPeptideB(), ptmFactory)).append("(").append(cp.getLinker_position_on_peptideB()).append(")").append(".-") // PeptideBSequence part              
                .append("\t").append(cp.getProteinA()).append("-").append(cp.getProteinB());
        return input;
    }

    /**
     * This method allows writing down results for either intra-proteins or
     * inter-proteins
     */
    private static void write(Result res, BufferedWriter bw_inter, BufferedWriter bw_intra, HashSet<StringBuilder> ids, PTMFactory ptmFactory) throws IOException {
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

}
