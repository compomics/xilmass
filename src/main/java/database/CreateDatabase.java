/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.dbtoolkit.gui.workerthreads.ProcessThread;
import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.EnzymeLoader;
import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.dbtoolkit.io.implementations.AutoDBLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.dbtoolkit.io.interfaces.Filter;
import com.compomics.dbtoolkit.io.interfaces.ProteinFilter;
import com.compomics.util.io.MascotEnzymeReader;
import com.compomics.util.protein.Enzyme;
import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerType;
import crossLinker.GetCrossLinker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import playground.EnzymeDigest;

/**
 *
 * @author Lennart in silico digestion adapted from DBToolKit
 * 
 * @author Sule combination of cross-linked peptides
 *
 */
public class CreateDatabase {

    private String inputProteinDBName, // input fasta file
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
    private boolean does_a_peptide_link_to_itself = false; // Is it possible to have the same peptide from the same protein matched to the same peptide from the same protein?
    private CrossLinker linker;
    private HashMap<String, StringBuilder> header_sequence = new HashMap<String, StringBuilder>();
    private static final Logger LOGGER = Logger.getLogger(CreateDatabase.class);
    private HashMap<String, Integer> accession_and_length = new HashMap<String, Integer>();

    public CreateDatabase(String proteinDBName,
            String inSilicoPeptideDBName,
            String cxDBName, // db related parameters
            String crossLinkerName, // crossLinkerName
            String crossLinkedProteinTypes, // crossLinking strategy
            String enzymeName, String enzymeFileName, String misclevaged, // enzyme related parameters
            String lowMass, String highMass, // filtering of in silico peptides on peptide masses
            int minLen,
            int maxLen_for_combined,// filtering of in silico peptides on peptide lenghts 
            boolean does_link_to_itself,
            boolean isLabeled // T: heavy labeled protein, F:no labeled
    ) throws Exception {
        // db related parameters
        inputProteinDBName = proteinDBName;
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
        linker = GetCrossLinker.getCrossLinker(this.crossLinkerName, isLabeled);
        // accession and the corresponding protein length of a given input-protein database to construct cross-linked peptides database
        accession_and_length = getAccession_and_length(inputProteinDBName);
    }

    // getter and setter methods    
    public String getFastaFileName() {
        return inputProteinDBName;
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

    public HashMap<String, Integer> getAccession_and_length() {
        return accession_and_length;
    }

    public void setAccession_and_length(HashMap<String, Integer> accession_and_length) {
        this.accession_and_length = accession_and_length;
    }

    public File getInSilicoPeptideDB() {
        return inSilicoPeptideDB;
    }

    public void setInSilicoPeptideDB(File inSilicoPeptideDB) {
        this.inSilicoPeptideDB = inSilicoPeptideDB;
    }
    
    
    /**
     * This method returns a hashmap containing header and a cross-linked
     * sequences. If this hashmap is not constructed, yet, it calls construct()
     * method.
     *
     * @return
     * @throws UnknownDBFormatException
     * @throws IOException
     * @throws Exception
     */
    public HashMap<String, StringBuilder> getHeadersAndSequences() throws UnknownDBFormatException, IOException, Exception {
        if (header_sequence.isEmpty()) {
            construct();
        }
        return header_sequence;
    }

    /**
     * This method performs in silico peptide digestion, then creates
     * cross-linked peptide combinations
     *
     * @throws UnknownDBFormatException
     * @throws IOException
     * @throws Exception
     */
    public void construct() throws UnknownDBFormatException, IOException, Exception {
        LOGGER.info("Performing in silico digestion");
        digest_insilico();
        LOGGER.info("Constructing a cross-linked peptides database");
        create_crossLinkedPeptides();
    }

    /**
     * This method performs in silico enyzme digestion. Only this part comes directly
     * from DBToolKit. In the end, an output file containing these putative peptides  is generated.
     *
     * @throws UnknownDBFormatException
     * @throws IOException
     */
    public void digest_insilico() throws UnknownDBFormatException, IOException {
        // See if all of this is correct.
        if (inputProteinDBName == null) {
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
            inputProteinFile = new File(inputProteinDBName);
            inSilicoPeptideDB = new File(inSilicoPeptideDBName);
            if (!inSilicoPeptideDB.exists()) {
                try {
                    inSilicoPeptideDB.createNewFile();
                } catch (IOException ioe) {
                    flagError("Could not create outputfile (" + inSilicoPeptideDBName + "): " + ioe.getMessage());
                }
            }
            if (!inputProteinFile.exists()) {
                flagError("The input file you specified (" + inputProteinDBName + ") could not be found!\nExiting...");
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
                    flagError("Unable to determine database type for your inputfile (" + inputProteinDBName + "), exiting...");
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
                                    try {
                                        constr = lClass.getConstructor(new Class[]{String.class});
                                    } catch (Exception exc) {
                                    }
                                } else {
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
                LOGGER.info("In silico digestion was finished after " + ((end - start) / 1000) + " seconds.");
            }
        }
    }

    /**
     * After doing in silico digestion, this method generates all possible
     * cross-linked peptides based on given criteria
     *
     * @throws IOException
     * @throws Exception
     */
    public void create_crossLinkedPeptides() throws IOException, Exception {
        DBLoader loader = DBLoaderLoader.loadDB(inSilicoPeptideDB),
                loader_next = null;
        Protein startProtein = null,
                nextProtein = null;
        long start = System.currentTimeMillis();
        // get a crossLinkerName object        
        while ((startProtein = loader.nextProtein()) != null) {
            boolean doesStartProContainProteinNtermini = false,
                    doesStartProContainProteinCtermini = false;
            String startHeader = startProtein.getHeader().getAccession(),
                    tmpStartAccession = startProtein.getHeader().getAccession(),
                    nextHeader = "",
                    tmpNextAccession = "";
            int startLen = startProtein.getSequence().getSequence().length();
            // check if a header comes from a generic! 
            if (startHeader.matches(".*[^0-9].*-.*[^0-9].*")) {
                doesStartProContainProteinNtermini = FASTACPDBLoader.checkProteinContainsProteinTermini(startHeader, true, accession_and_length);
                doesStartProContainProteinCtermini = FASTACPDBLoader.checkProteinContainsProteinTermini(startHeader, false, accession_and_length);
                tmpStartAccession = startHeader.substring(0, startHeader.indexOf("("));
            }
            // a start sequence must be at least #minLen amino acids
            if (startLen >= minLen) {
                // find if there is a possible linker locations.                
                ArrayList<LinkedResidue> linkedStartResiduesOnFirstPart = Find_LinkerPosition.find_cross_linking_sites(startProtein, true, linker, doesStartProContainProteinNtermini, doesStartProContainProteinCtermini),
                        linkedStartResiduesOnSecondPart = Find_LinkerPosition.find_cross_linking_sites(startProtein, false, linker, doesStartProContainProteinNtermini, doesStartProContainProteinCtermini),
                        linkedNextResiduesOnFirstPart = new ArrayList<LinkedResidue>(),
                        linkedNextResiduesOnSecondPart = new ArrayList<LinkedResidue>();
                loader_next = DBLoaderLoader.loadDB(inSilicoPeptideDB);
                while ((nextProtein = loader_next.nextProtein()) != null) {
                    boolean doesNextProContainProteinNtermini = false,
                            doesNextProContainProteinCtermini = false,
                            toConjugate = false;
                    // now start building a cross linked peptides...
                    nextHeader = nextProtein.getHeader().getAccession();
                    tmpNextAccession = nextProtein.getHeader().getAccession();
                    int nextLen = nextProtein.getSequence().getSequence().length(),
                            totalLen = startLen + nextLen;
                    if (nextHeader.matches(".*[^0-9].*-.*[^0-9].*") && nextProtein.getSequence().getSequence().length() >= minLen) {
                        tmpNextAccession = nextHeader.substring(0, nextHeader.indexOf("("));
                        doesNextProContainProteinNtermini = FASTACPDBLoader.checkProteinContainsProteinTermini(nextHeader, true, accession_and_length);
                        doesNextProContainProteinCtermini = FASTACPDBLoader.checkProteinContainsProteinTermini(nextHeader, false, accession_and_length);
                    }
                    if ((tmpNextAccession.equals(tmpStartAccession) && (crossLinkedProteinTypes.toLowerCase().equals("intra") || crossLinkedProteinTypes.toLowerCase().equals("both")))
                            || (!tmpNextAccession.equals(tmpStartAccession) && (crossLinkedProteinTypes.toLowerCase().equals("inter") || crossLinkedProteinTypes.toLowerCase().equals("both")))
                            && (nextLen >= minLen && totalLen <= maxLen_for_combined)
                            && (does_a_peptide_link_to_itself && startProtein.getSequence().equals(nextProtein.getSequence())
                            || (!nextProtein.getSequence().equals(startProtein.getSequence())))) {
                        toConjugate = true;
                        linkedNextResiduesOnFirstPart = Find_LinkerPosition.find_cross_linking_sites(nextProtein, true, linker, doesNextProContainProteinNtermini, doesNextProContainProteinCtermini);
                        linkedNextResiduesOnSecondPart = Find_LinkerPosition.find_cross_linking_sites(nextProtein, false, linker, doesNextProContainProteinNtermini, doesNextProContainProteinCtermini);
                    }
                    if (toConjugate) {
                        get_peptide_combinations(linkedStartResiduesOnFirstPart, linkedNextResiduesOnSecondPart);
                        if (linker.getType().equals(CrossLinkerType.AMINE_TO_SULFHYDRYL)
                                || linker.getType().equals(CrossLinkerType.CARBOXYL_TO_AMINE)
                                || linker.getType().equals(CrossLinkerType.SULFHYDRYL_TO_CARBOHYDRATE)) {
                            //Because the the target group of the second reactive group is different
                            get_peptide_combinations(linkedNextResiduesOnFirstPart, linkedStartResiduesOnSecondPart);
                        }
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info("Cross-linked peptides combinations are generated in " + ((end - start) / 1000) + " seconds.");
    }

    /**
     * This method generates combination of each residue on a
     * linkedStartResidues list against each residue on linkedNextResidues list.
     * If this combination is not previously constructed, then it puts into
     * header_sequence hashmap for further cases
     *
     * @param linkedStartResidues a list of LinkedResiude
     * @param linkedNextResidues
     * @throws IOException
     */
    public void get_peptide_combinations(ArrayList<LinkedResidue> linkedStartResidues, ArrayList<LinkedResidue> linkedNextResidues) throws IOException {
        for (LinkedResidue start : linkedStartResidues) {
            for (LinkedResidue next : linkedNextResidues) {
                StringBuilder[] header_and_sequence = construct_header_and_sequence(start, next);
                StringBuilder rev_header = reverse(header_and_sequence);
                if (!header_sequence.containsKey(rev_header.toString())) {
                    header_sequence.put(header_and_sequence[0].toString(), header_and_sequence[1]);
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

    /**
     * For given for two linkedResidues, headers and sequences are constructed.
     * If a sequence of @param next is longer than a sequence of @param start;
     * then the first written part comes from @param next.
     *
     *
     * @param start
     * @param next
     * @return an array of StringBuilder with first element is a constructed
     * header and the second element is a constructed sequence
     * @throws IOException
     */
    public static StringBuilder[] construct_header_and_sequence(LinkedResidue start, LinkedResidue next) throws IOException {
        String startSeq = start.getSequence(),
                nextSeq = next.getSequence(),
                startHeader = start.getProtein().getHeader().getAccession(),
                nextHeader = next.getProtein().getHeader().getAccession();
        int positionToStartStartSeq = 0,
                positionToStartNextSeq = 0,
                positionLinkedResStartSeq = start.getPosition(),
                positionLinkedResNextSeq = next.getPosition();
        // making sure that a sequence starts from the second residue, right after M! 
        if (start.getResType().equals(LinkedResidueType.NTerminiIncludesM)) {
            startHeader = startHeader.replace("(1-", "(2-");
            startSeq = startSeq.substring(1);
        }
        StringBuilder mod_startSeq = new StringBuilder(startSeq.substring(positionToStartStartSeq, positionLinkedResStartSeq + 1))
                .append("*")
                .append(startSeq.substring(positionLinkedResStartSeq + 1));
        // also making sure that the next sequence starts from the second residue, right after M! 
        if (next.getResType().equals(LinkedResidueType.NTerminiIncludesM)) {
            nextSeq = nextSeq.substring(1);
            nextHeader = nextHeader.replace("(1-", "(2-");
        }
        StringBuilder mod_nextSeq = new StringBuilder(nextSeq.substring(positionToStartNextSeq, positionLinkedResNextSeq + 1))
                .append("*")
                .append(nextSeq.substring(positionLinkedResNextSeq + 1));
        StringBuilder tmp_sequence = new StringBuilder(),
                tmp_header = new StringBuilder();
        StringBuilder[] header_and_sequence = new StringBuilder[2];
        if (mod_startSeq.length() >= mod_nextSeq.length()) {
            tmp_sequence = new StringBuilder(mod_startSeq).append("|").append(mod_nextSeq);
            tmp_header = new StringBuilder(startHeader.replace(" ", ""))
                    .append("_").append(positionLinkedResStartSeq + 1).append("_")
                    .append(nextHeader.replace(" ", ""))
                    .append("_").append(positionLinkedResNextSeq + 1);

        } else {
            tmp_sequence = new StringBuilder(mod_nextSeq).append("|").append(mod_startSeq);
            tmp_header = new StringBuilder(nextHeader.replace(" ", ""))
                    .append("_").append(positionLinkedResNextSeq + 1).append("_")
                    .append(startHeader.replace(" ", ""))
                    .append("_").append(positionLinkedResStartSeq + 1);
        }
        header_and_sequence[0] = tmp_header;
        header_and_sequence[1] = tmp_sequence;
        return header_and_sequence;
    }

    /**
     * This method returns a hashmap with keys as accession numbers and values
     * as length of the sequence with corresponding accession number
     *
     * @param proteinFastaFileName is the name of proteinFastaFileName (the full
     * path)
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static HashMap<String, Integer> getAccession_and_length(String proteinFastaFileName) throws FileNotFoundException, IOException {
        HashMap<String, Integer> acc_and_length = new HashMap<String, Integer>();
        BufferedReader br = new BufferedReader(new FileReader(new File(proteinFastaFileName)));
        String line = "",
                acc = null;
        int len = 0;
        while ((line = br.readLine()) != null) {
            // this line has accession number
            if (line.startsWith(">")) {
                // first check if already sequence is checked
                if (len != 0 && acc != null) {
                    acc_and_length.put(acc, len);
                }
                String[] sp = line.split("\\|");
                acc = sp[1];
                len = 0;
                // this line is only sequence
            } else {
                len += line.length();
            }
        }
        if (len != 0 && acc != null) {
            acc_and_length.put(acc, len);
        }
        return acc_and_length;
    }

    /**
     * This method swaps around the proteinA and proteinB.
     *
     * @param header_and_sequence a header-sequence information that a header is
     * swapped around
     * @return
     */
    public static StringBuilder reverse(StringBuilder[] header_and_sequence) {
        StringBuilder reversedHeader = new StringBuilder();
        String h = header_and_sequence[0].toString();
        String[] sp = h.split("_");
        int posA = 0,
                posB = 2;
        if (sp[posA + 1].contains("REVERSED")
                || sp[posA + 1].contains("SHUFFLED")) {
            posB++;
        }
        for (int i = posB; i < sp.length; i++) {
            reversedHeader.append(sp[i]).append("_");
        }
        for (int i = 0; i < posB; i++) {
            reversedHeader.append(sp[i]);
            if (i != posB - 1) {
                reversedHeader.append("_");
            }
        }
        return reversedHeader;

    }

}
