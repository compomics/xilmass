/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import playground.EnzymeDigest;
import com.compomics.dbtoolkit.gui.workerthreads.ProcessThread;
import com.compomics.dbtoolkit.gui.workerthreads.ShuffleDBThread;
import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.EnzymeLoader;
import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.dbtoolkit.io.implementations.AutoDBLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.dbtoolkit.io.interfaces.Filter;
import com.compomics.dbtoolkit.io.interfaces.ProteinFilter;
import com.compomics.util.io.MascotEnzymeReader;
import com.compomics.util.protein.Enzyme;
import com.compomics.util.protein.Protein;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerName;
import crossLinker.CrossLinkerType;
import crossLinker.GetCrossLinker;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import start.ShuffledDecoy;

/**
 *
 * @author Lennart adapted from DBToolKit for in silico digestion of database
 * from DBToolKit
 * @author Sule to integrate for crossLinkedPeptides
 *
 */
public class CreateDatabase {

    private String inputProteinFileName, // input fasta file
            inSilicoPeptideDBName, // in silico digested fasta file
            crossLinkerName, // a cross linker name 
            crossLinkedProteinTypes = "both", // "intra"(Different proteins), "inter" (Same proteins) "both" (Same and different proteins)           
            enzymeName = "Trypsin",
            enzymeFileName, // an enzyme file from DBToolKit
            miscl = "2", // miscleaveged number - Important to create combination, but if it is higher, search space is drastically increased
            filter,
            lowMass = "0", // Default, I do not give any permission to remove any short peptides before cross linking
            highMass = "40000", // Default, I do not give any permission to remove any long peptides before cross linking
            filterParam = "";
    private int minLen = 0,
            maxLen_for_combined = 50; // How many amino acids are on one cross linked peptides
    private File inputProteinFile,
            inSilicoPeptideDB;
    private boolean does_a_peptide_link_to_itself = false, // Is it possible to have the same peptide from the same protein matched to the same peptide from the same protein?
            has_shuffled_decoy_as_concatenated,
            isInvertedPeptides;
    private CrossLinker linker;
    private HashMap<String, String> header_sequence = new HashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(CreateDatabase.class);
    private HashMap<String, String> proteinaccessionAndshuffled = new HashMap<String, String>();

    public CreateDatabase(String givenDBName,
            String inSilicoPeptideDBName,
            String cxDBName, // db related parameters
            String crossLinkerName, // crossLinkerName
            String crossLinkedProteinTypes, // crossLinking strategy
            String enzymeName, String enzymeFileName, String misclevaged, // enzyme related parameters
            String lowMass, String highMass, // filtering of in silico peptides on peptide masses
            int minLen,
            int maxLen_for_combined,// filtering of in silico peptides on peptide lenghts 
            boolean does_link_to_itself,
            boolean isLabeled, // T: heavy labeled protein, F:no labeled
            boolean has_shuffled_decoy_as_concatenated,
            boolean isInvertedPeptides
    ) throws Exception {
        // db related parameters
        inputProteinFileName = givenDBName;
        this.inSilicoPeptideDBName = inSilicoPeptideDBName;
        // crossLinkerName related parameters
        this.crossLinkerName = crossLinkerName;
        this.crossLinkedProteinTypes = crossLinkedProteinTypes;
        // enzyme related
        this.enzymeName = enzymeName;
        this.enzymeFileName = enzymeFileName;
        miscl = misclevaged;
        // filtering parameters
        this.lowMass = lowMass;
        this.highMass = highMass;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
        this.does_a_peptide_link_to_itself = does_link_to_itself;
        this.isInvertedPeptides = isInvertedPeptides;
        linker = GetCrossLinker.getCrossLinker(this.crossLinkerName, isLabeled);
        this.has_shuffled_decoy_as_concatenated = has_shuffled_decoy_as_concatenated;
    }

    // getter and setter methods    
    public String getFastaFileName() {
        return inputProteinFileName;
    }

    public String getInSilicoPeptideDBName() {
        return inSilicoPeptideDBName;
    }

    public String getCrossLinker() {
        return crossLinkerName;
    }

    public void setCrossLinker(String crossLinker) {
        this.crossLinkerName = crossLinker;
    }

    public String getCrossLinked_protein_types() {
        return crossLinkedProteinTypes;
    }

    public void setCrossLinked_protein_types(String crossLinked_protein_types) {
        this.crossLinkedProteinTypes = crossLinked_protein_types;
    }

    public String getEnzymeName() {
        return enzymeName;
    }

    public void setEnzymeName(String enzymeName) {
        this.enzymeName = enzymeName;
    }

    public String getMiscl() {
        return miscl;
    }

    public void setMiscl(String miscl) {
        this.miscl = miscl;
    }

    public String getLowMass() {
        return lowMass;
    }

    public void setLowMass(String lowMass) {
        this.lowMass = lowMass;
    }

    public String getHighMass() {
        return highMass;
    }

    public void setHighMass(String highMass) {
        this.highMass = highMass;
    }

    public int getMinLen() {
        return minLen;
    }

    public void setMinLen(int minLen) {
        this.minLen = minLen;
    }

    public int getMaxLen_for_combined() {
        return maxLen_for_combined;
    }

    public void setMaxLen_for_combined(int maxLen_for_combined) {
        this.maxLen_for_combined = maxLen_for_combined;
    }

    public CrossLinker getLinker() {
        return linker;
    }

    public void setLinker(CrossLinker linker) {
        this.linker = linker;
    }

    /**
     * This method returns a hashmap containing header and cx sequences If it is
     * not already generated, it calls construct() method
     *
     * @return
     * @throws UnknownDBFormatException
     * @throws IOException
     * @throws Exception
     */
    public HashMap<String, String> getHeadersAndSequences() throws UnknownDBFormatException, IOException, Exception {
        if (header_sequence.isEmpty()) {
            construct();
        }
        return header_sequence;
    }

    /**
     * This method does in silico peptide digestion, then create cross linked
     * peptide combinations
     *
     * @throws UnknownDBFormatException
     * @throws IOException
     * @throws Exception
     */
    public void construct() throws UnknownDBFormatException, IOException, Exception {
        digest_insilico();
        // read in silico digested pepti file and generate cross linked peptides
        // first generate shuffles
        if (has_shuffled_decoy_as_concatenated) {
            shuffle();
        }
        // now generate cross linked ones..
        create_crossLinkedPeptides();
    }

    /**
     * This method does in silico enyzme digestion. This comes from DBToolKit.
     * In the end, an output file with in silico digested peptides on is
     * generated.
     *
     * @throws UnknownDBFormatException
     * @throws IOException
     */
    private void digest_insilico() throws UnknownDBFormatException, IOException {
        // See if all of this is correct.
        if (inputProteinFileName == null) {
            flagError("You did not specify the '--input <input_file_name>' parameter!\n\nRun program without parameters for help.");
        } else if (inSilicoPeptideDBName == null) {
            flagError("You did not specify an outputfile!\n\nRun program without parameters for help.");
        } else {
            // Parameters were all found. Let's see if we can access all files that should be accessed.
            // Note that an existing output_file will result in clean and silent overwrite of the file!
            File enzymeFile = null;
            if (enzymeFileName != null) {
                enzymeFile = new File(enzymeFileName);
                if (!enzymeFile.exists()) {
                    flagError("The enzyme definitions file you specified (" + enzymeFile + ") could not be found!\nExiting...");
                }
            }
            inputProteinFile = new File(inputProteinFileName);
            inSilicoPeptideDB = new File(inSilicoPeptideDBName);
            if (!inSilicoPeptideDB.exists()) {
                try {
                    inSilicoPeptideDB.createNewFile();
                } catch (IOException ioe) {
                    flagError("Could not create outputfile (" + inSilicoPeptideDBName + "): " + ioe.getMessage());
                }
            }
            if (!inputProteinFile.exists()) {
                flagError("The input file you specified (" + inputProteinFileName + ") could not be found!\nExiting...");
            } else {
                // The stuff we've received as input seems to be OK.
                // Get the props for the AutoDBLoader...
                Properties p = null;
                try {
                    InputStream is = EnzymeDigest.class.getClassLoader().getResourceAsStream("DBLoaders.properties");
                    p = new Properties();
                    if (is != null) {
                        p.load(is);
                        is.close();
                    }
                } catch (IOException ioe) {
                }
                // See if we managed to load the 'DBLoader.properties' file, else default to built-in types.
                if (p == null || p.size() == 0) {
                    System.out.println("\t - Unable to find 'DBLoaders.properties' file, defaulting to built-in types (SwissProt & FASTA only!)...");
                    p = new Properties();
                    p.put("1", "com.compomics.dbtoolkit.io.implementations.SwissProtDBLoader");
                    p.put("2", "com.compomics.dbtoolkit.io.implementations.FASTADBLoader");
                }
                String[] classNames = new String[p.size()];
                Iterator it = p.values().iterator();
                int counter = 0;
                while (it.hasNext()) {
                    classNames[counter] = (String) it.next();
                    counter++;
                }
                AutoDBLoader adb = new AutoDBLoader(classNames);
                DBLoader loader = null;
                try {
                    loader = adb.getLoaderForFile(inputProteinFile.getAbsolutePath());
                } catch (IOException ioe) {
                } catch (UnknownDBFormatException udfe) {
                }
                if (loader == null) {
                    flagError("Unable to determine database type for your inputfile (" + inputProteinFileName + "), exiting...");
                }
                // Parse the enzyme stuff and masses etc.
                double minMass = -1;
                if (lowMass != null) {
                    try {
                        minMass = Double.parseDouble(lowMass);
                    } catch (Exception e) {
                        flagError("You need to specify a (decimal) number for the lower mass treshold!");
                    }
                }
                double maxMass = -1;
                if (highMass != null) {
                    try {
                        maxMass = Double.parseDouble(highMass);
                    } catch (Exception e) {
                        flagError("You need to specify a (decimal) number for the higher mass treshold!");
                    }
                }
                // Try to load the mascot enzymefile.
                Enzyme enzyme = null;
                try {
                    if (enzymeFile != null) {
                        MascotEnzymeReader enzReader = new MascotEnzymeReader(enzymeFile.getAbsolutePath());
                        enzyme = enzReader.getEnzyme(enzymeName);
                        if (enzyme == null) {
                            flagError("The enzyme '" + enzymeName + "' was not found in the enzyme input file '" + enzymeFile.getAbsolutePath() + "'!");
                        }
                        if (miscl != null) {
                            try {
                                int i = Integer.parseInt(miscl);
                                if (i < 0) {
                                    throw new NumberFormatException();
                                }
                                enzyme.setMiscleavages(i);
                            } catch (NumberFormatException nfe) {
                                flagError("The number of allowed missed cleavages must be a positive whole number! You specified '" + miscl + "' instead!");
                            }
                        }
                    } else {
                        enzyme = EnzymeLoader.loadEnzyme(enzymeName, miscl);
                    }
                } catch (IOException ioe) {
                    flagError("You specified enzyme '" + enzymeName + "' for cleavage, but there was a problem loading it: " + ioe.getMessage());
                }

                // Try to load the filter (if any).
                Filter f = null;
                if (filter != null) {
                    try {
                        Properties props = new Properties();
                        InputStream in = EnzymeDigest.class.getClassLoader().getResourceAsStream("filters.properties");
                        if (in == null) {
                            throw new IOException("File 'filters.properties' not found in current classpath!");
                        }

                        props.load(in);
                        String filterParams = props.getProperty(filter);
                        if (filterParams == null) {
                            flagError("The filter you specified (" + filter + ") is not found in the 'filters.properties' file!");
                        }
                        StringTokenizer st = new StringTokenizer(filterParams, ",");
                        String filterClass = st.nextToken().trim();
                        String filterDB = st.nextToken().trim();

                        if (!filterDB.equals(loader.getDBName())) {
                            flagError("The filter you specified (" + filter + ") is not available for a '" + loader.getDBName() + "' database but for a '" + filterDB + "' database!");
                        } else {
                            try {
                                Constructor constr = null;
                                int type = 0;
                                Class lClass = Class.forName(filterClass);
                                if (lClass == null) {
                                    flagError("The class '" + filterClass + "' for your filter '" + filter + "' could not be found! Check your clasppath setting!");
                                }
                                if (filterParam == null) {
                                    try {
                                        constr = lClass.getConstructor(new Class[]{});
                                    } catch (Exception exc) {
                                    }
                                    type = 1;
                                } else if (filterParam.startsWith("!")) {
                                    try {
                                        constr = lClass.getConstructor(new Class[]{String.class, boolean.class});
                                    } catch (Exception exc) {
                                    }
                                    type = 2;
                                } else {
                                    try {
                                        constr = lClass.getConstructor(new Class[]{String.class});
                                    } catch (Exception exc) {
                                    }
                                    type = 3;
                                }
                                if (constr == null) {
                                    flagError("The '" + filter + "' filter does not support the " + ((filterParam != null) ? "presence" : "absence") + " of a" + (((filterParam != null) && (filterParam.startsWith("!"))) ? "n inverted " : " ") + "parameter!");
                                } else {
                                    if (type == 1) {
                                        f = (Filter) constr.newInstance(new Object[]{});
                                    } else if (type == 2) {
                                        f = (Filter) constr.newInstance(new Object[]{filterParam.substring(1), new Boolean(true)});
                                    } else {
                                        f = (Filter) constr.newInstance(new Object[]{filterParam});
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                flagError("Unable to load class '" + filterClass + "' for your filter '" + filter + "': " + e.getMessage());
                            }
                        }
                    } catch (IOException ioe) {
                        flagError("You specified a filter (" + filter + "), but the filter configuration file was not found: " + ioe.getMessage());
                    }
                }

                // Construct the Thread and run it!
                boolean massLimits = false;
                if ((minMass >= 0) && (maxMass >= 0)) {
                    massLimits = true;
                }

                ProcessThread pt = ProcessThread.getSubsetTask(loader, inSilicoPeptideDB, null, f, enzyme, massLimits, minMass, maxMass, (ProteinFilter) null);

                StringBuffer filterSettings = new StringBuffer();
                if (f == null) {
                    filterSettings.append("no filter specified.");
                } else {
                    filterSettings.append("filter '" + filter + "' chosen");
                    if (filterParam != null) {
                        filterSettings.append(" with " + ((filterParam.startsWith("!")) ? "inverted" : "") + " parameter '" + ((filterParam.startsWith("!")) ? filterParam.substring(1) : filterParam) + "'.");
                    } else {
                        filterSettings.append(" without parameters.");
                    }
                }

                long start = System.currentTimeMillis();
                pt.run();
                long end = System.currentTimeMillis();
                LOGGER.info("In silico digestion is finished after " + ((end - start) / 1000) + " seconds.");
            }
        }
    }

    /**
     * After doing in silico digestion, this method generates all possible
     * cross-linked peptides based on given criteria
     *
     * Make sure that this part was actually created via
     *
     * @throws IOException
     * @throws Exception
     */
    private void shuffle() throws IOException, Exception {
        DBLoader loader = DBLoaderLoader.loadDB(inSilicoPeptideDB);
        // Use randomizing class from DBToolKit!
        ShuffleDBThread sdt = new ShuffleDBThread(inSilicoPeptideDB);
        sdt.shuffle();
        
        Protein protein = null;
        ShuffledDecoy r = null;
        proteinaccessionAndshuffled = new HashMap<String, String>();
        // get a crossLinkerName object        
        while ((protein = loader.nextProtein()) != null) {
            String sequence = protein.getSequence().getSequence();
            while ((protein = loader.nextProtein()) != null) {
                String tmpStartAccession = protein.getHeader().getAccession(),
                        startSequence = protein.getSequence().getSequence();
                int iStart_StartProtein = protein.getHeader().getStartLocation(),
                        iEnd_StartProtein = protein.getHeader().getEndLocation();
                String header_to_put = tmpStartAccession.replace(" ", "") + "(" + iStart_StartProtein + "_" + iEnd_StartProtein + ")";

                // check the first condition
                if (proteinaccessionAndshuffled.isEmpty()) {
                    r = new ShuffledDecoy(sequence);
                    proteinaccessionAndshuffled.put(header_to_put, r.getShuffled().toString());
                } else {
                    r.setTarget(sequence);
                    r.getShuffled();
                    proteinaccessionAndshuffled.put(header_to_put, r.getShuffled().toString());
                }
            }
        }
        LOGGER.info("Shuffles for each sequence were generated!" + proteinaccessionAndshuffled.size());
    }

    /**
     * After doing in silico digestion, this method generates all possible
     * cross-linked peptides based on given criteria
     *
     * @throws IOException
     * @throws Exception
     */
    private void create_crossLinkedPeptides() throws IOException, Exception {
        DBLoader loader = DBLoaderLoader.loadDB(inSilicoPeptideDB),
                loader_next = null;
        Protein startProtein = null,
                nextProtein = null;
        // get a crossLinkerName object        
        while ((startProtein = loader.nextProtein()) != null) {
            String tmpStartAccession = startProtein.getHeader().getAccession(),
                    startHeader = startProtein.getHeader().getAccession(),
                    startSequence = startProtein.getSequence().getSequence();
            // check if a header comes from a generic! 
            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
                tmpStartAccession = startHeader.substring(0, startHeader.indexOf("("));
            }
            // check the first condition
            if (startSequence.length() >= minLen) {
                // find if there is a possible linker locations.
                HashMap<String, ArrayList<Integer>> possible_indices = Find_LinkerPosition.find_possibly_linker_locations(startProtein, linker);
                for (String possible_linked_aa_startSeq : possible_indices.keySet()) {
                    ArrayList<Integer> indices = possible_indices.get(possible_linked_aa_startSeq);
                    for (int index : indices) {
                        // find for each possible match on the other part
                        loader_next = DBLoaderLoader.loadDB(inSilicoPeptideDB);
                        while ((nextProtein = loader_next.nextProtein()) != null) {
                            // now start building a cross linked peptides...
                            String nextHeader = nextProtein.getHeader().getAccession(),
                                    tmpNextAccession = nextProtein.getHeader().getAccession();
                            if (nextHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
                                tmpNextAccession = nextHeader.substring(0, nextHeader.indexOf("("));
                            }
                            if (tmpNextAccession.equals(tmpStartAccession)) {
                                // put a control to find either inter or intra proteins
                                if (crossLinkedProteinTypes.equals("intra") || crossLinkedProteinTypes.equals("both")) {
                                    // header and sequence
                                    generate_peptide_combinations(startProtein, false, nextProtein, possible_linked_aa_startSeq, index);
                                }
                            } else {
                                if (crossLinkedProteinTypes.equals("inter") || crossLinkedProteinTypes.equals("both")) {
                                    generate_peptide_combinations(startProtein, false, nextProtein, possible_linked_aa_startSeq, index);
                                }
                            }
                        }
                    }
                }
            }
        }
        LOGGER.info("Cross linked peptide combinations are constructed!");
    }

    /**
     * Note that this class is not generating any concatenation for shuffled
     * decoys! TODO: Apply this if necessary!
     *
     * @param startProtein
     * @param is_start_sequence_reversed
     * @param nextProtein
     * @param possible_linked_aa_startSeq
     * @param index_linked_aa_startSeq
     * @throws IOException
     */
    public void generate_peptide_combinations(Protein startProtein, boolean is_start_sequence_reversed,
            Protein nextProtein, String possible_linked_aa_startSeq, int index_linked_aa_startSeq) throws IOException {
        String startSequence = startProtein.getSequence().getSequence(),
                nextSequence = nextProtein.getSequence().getSequence();
        // target-target
        generate_xlinked_ones(startSequence, nextSequence, nextProtein, startProtein, index_linked_aa_startSeq, is_start_sequence_reversed, possible_linked_aa_startSeq);
    }

    private void generate_xlinked_ones(String startSequence, String nextSequence, Protein nextProtein, Protein startProtein, int index_linked_aa_startSeq, boolean is_start_sequence_reversed, String possible_linked_aa_startSeq) throws IOException {
        int totalLen = startSequence.length() + nextSequence.length();
        // check the condition - next sequence needs be to larger than minLen and also a cross linked peptide needs to be shorter than maxLen
        if (nextSequence.length() >= minLen && totalLen <= maxLen_for_combined) {
            if ((does_a_peptide_link_to_itself && nextSequence.equals(startSequence)) || (!nextSequence.equals(startSequence))) {
                HashMap<String, ArrayList<Integer>> next_liked_aas_and_indices = Find_LinkerPosition.find_possibly_linker_locations(nextProtein, linker);
                if (linker.getType().equals(CrossLinkerType.homobifunctional)) { // either DSSd0, DSSd12, BS3 or BS3d4.. So K-K
                    for (String next_linked_aa : next_liked_aas_and_indices.keySet()) {
                        ArrayList<Integer> next_indices_liked_aas = next_liked_aas_and_indices.get(next_linked_aa);
                        for (Integer next_index : next_indices_liked_aas) {
                            generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, false, is_start_sequence_reversed);
//InvertedPeptides            generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, true, is_start_sequence_reversed);

                        }
                    }//                      
                } else if (linker.getType().equals(CrossLinkerName.EDC)) {
                    if (possible_linked_aa_startSeq.equals("K")) {
                        // the rest should be D or E
                        for (String next_linked_aa : next_liked_aas_and_indices.keySet()) {
                            if (next_linked_aa.equals("D") || next_linked_aa.equals("S")) {
                                ArrayList<Integer> next_indices_liked_aas = next_liked_aas_and_indices.get(next_linked_aa);
                                for (Integer next_index : next_indices_liked_aas) {
                                    generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, false, is_start_sequence_reversed);
//InvertedPeptides                    generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, true, is_start_sequence_reversed);
                                }
                            }
                        }
                    } else {
                        // the rest should be K, So either D or S link to K
                        if (possible_linked_aa_startSeq.equals("D") || possible_linked_aa_startSeq.equals("S")) {
                            // the rest should be D or E
                            for (String next_linked_aa : next_liked_aas_and_indices.keySet()) {
                                if (next_linked_aa.equals("K")) {
                                    ArrayList<Integer> next_indices_liked_aas = next_liked_aas_and_indices.get(next_linked_aa);
                                    for (Integer next_index : next_indices_liked_aas) {
                                        generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, false, is_start_sequence_reversed);
//InvertedPeptides                        generate_header_and_sequence(startSequence, nextSequence, startProtein, nextProtein, index_linked_aa_startSeq, next_index, true, is_start_sequence_reversed);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method prints the specified error message to standard out, after
     * prepending and appending two blank lines each. It then exits the JVM!
     *
     * @param aMessage String with the error message to display.
     */
    private static void flagError(String aMessage) {
        LOGGER.info("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }

    private void generate_header_and_sequence(String startSequence, String nextSequence,
            Protein startProtein, Protein nextProtein,
            int index_linked_aa_startSeq, int next_index,
            boolean is_inverted, boolean is_start_sequence_reversed) throws IOException {
        String mod_startSeq = startSequence.substring(0, index_linked_aa_startSeq + 1) + "*" + startSequence.substring(index_linked_aa_startSeq + 1),
                info_if_nextSeq_reversed = "",
                info_if_startSeq_reversed = "",
                mod_nextSeq = "";
        int iStart_StartProtein = startProtein.getHeader().getStartLocation(),
                iEnd_StartProtein = startProtein.getHeader().getEndLocation(),
                iStart_NextProtein = nextProtein.getHeader().getStartLocation(),
                iEnd_NextProtein = nextProtein.getHeader().getEndLocation();
        if (is_inverted) {
            nextSequence = new StringBuilder(nextSequence).reverse().toString();
            next_index = nextSequence.length() - next_index - 1;
            info_if_nextSeq_reversed = "_inverted";
        }
        if (is_start_sequence_reversed) {
            info_if_startSeq_reversed = "_inverted";
        }
        // Make sure that a linked amino acid on an inverted sequence is not at the last index
        if (next_index != nextSequence.length() - 1) {
            mod_nextSeq = nextSequence.substring(0, next_index + 1) + "*" + nextSequence.substring(next_index + 1);
            String tmp_linked_sequence = mod_startSeq + "|" + mod_nextSeq;
            String tmp_header = startProtein.getHeader().getAccession().replace(" ", "") + "(" + iStart_StartProtein + "-" + iEnd_StartProtein + ")"
                    + info_if_startSeq_reversed + "_" + (index_linked_aa_startSeq + 1) + "_"
                    + nextProtein.getHeader().getAccession().replace(" ", "") + "(" + iStart_NextProtein + "-" + iEnd_NextProtein + ")"
                    + info_if_nextSeq_reversed + "_" + (next_index + 1);

            String prev_tmp_header = nextProtein.getHeader().getAccession().replace(" ", "") + "(" + iStart_NextProtein + "-" + iEnd_NextProtein + ")"
                    + info_if_nextSeq_reversed + "_" + (next_index + 1) + "_"
                    + startProtein.getHeader().getAccession().replace(" ", "") + "(" + iStart_StartProtein + "-" + iEnd_StartProtein + ")"
                    + info_if_startSeq_reversed + "_" + (index_linked_aa_startSeq + 1);
            if (!header_sequence.containsKey(prev_tmp_header)) {
                header_sequence.put(tmp_header, tmp_linked_sequence);
            }
        }
    }

}
