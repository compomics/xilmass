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
import com.compomics.util.protein.Protein;
import crossLinker.CrossLinkerName;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 *
 * @author Lennart adapted from DBToolKit
 * @author Sule to integrate for crossLinkedPeptides
 *
 */
public class CreateDatabase {

    private String inputFileName,
            outputFileName,
            crossLinker,
            enzymeFilename = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\resources\\enzymes.txt",
            enzymeName = "Trypsin",
            miscl = "2",
            filter,
            lowMass = "600",
            highMass = "4000",
            filterParam = "",
            crossLinked_protein_types = "Both"; //"Both"-"Inter" or "Intra"
    private File input,
            output,
            crossLinkedDB;
    private int minLen = 4,
            maxLen_for_combined = 40;
    private boolean is_inverted = false;

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String lowMass, String highMass, String filterParam, String crossLinked_protein_types,
            int minLen, int maxLen_for_combined) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.lowMass = lowMass;
        this.highMass = highMass;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String lowMass, String highMass, String filterParam, String crossLinked_protein_types) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.lowMass = lowMass;
        this.highMass = highMass;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename,
            String crossLinked_protein_types, int minLen, int maxLen_for_combined) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker,
            String crossLinked_protein_types, int minLen, int maxLen_for_combined) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String filterParam, String crossLinked_protein_types) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String lowMass, String highMass, String filterParam, String crossLinked_protein_types,
            int minLen, int maxLen_for_combined, boolean is_inverted) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.lowMass = lowMass;
        this.highMass = highMass;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
        this.is_inverted = is_inverted;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String lowMass, String highMass, String filterParam, String crossLinked_protein_types, boolean is_inverted) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.lowMass = lowMass;
        this.highMass = highMass;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.is_inverted = is_inverted;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename,
            String crossLinked_protein_types, int minLen, int maxLen_for_combined, boolean is_inverted) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
        this.is_inverted = is_inverted;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker,
            String crossLinked_protein_types, int minLen, int maxLen_for_combined, boolean is_inverted) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.minLen = minLen;
        this.maxLen_for_combined = maxLen_for_combined;
        this.is_inverted = is_inverted;
    }

    public CreateDatabase(String inputFileName, String outputFileName, String crossLinker, String enzymeFilename, String enzymeName,
            String miscl, String filter, String filterParam, String crossLinked_protein_types, boolean is_inverted) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        this.crossLinker = crossLinker;
        this.enzymeFilename = enzymeFilename;
        this.enzymeName = enzymeName;
        this.miscl = miscl;
        this.filter = filter;
        this.filterParam = filterParam;
        this.crossLinked_protein_types = crossLinked_protein_types;
        this.is_inverted = is_inverted;
    }

    // getter and setter methods    
    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getCrossLinker() {
        return crossLinker;
    }

    public void setCrossLinker(String crossLinker) {
        this.crossLinker = crossLinker;
    }

    public String getEnzymeFilename() {
        return enzymeFilename;
    }

    public void setEnzymeFilename(String enzymeFilename) {
        this.enzymeFilename = enzymeFilename;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public String getFilterParam() {
        return filterParam;
    }

    public void setFilterParam(String filterParam) {
        this.filterParam = filterParam;
    }

    public String getCrossLinked_protein_types() {
        return crossLinked_protein_types;
    }

    public void setCrossLinked_protein_types(String crossLinked_protein_types) {
        this.crossLinked_protein_types = crossLinked_protein_types;
    }

    public File getCrossLinkedDB() {
        return crossLinkedDB;
    }

    public void setCrossLinkedDB(File crossLinkedDB) {
        this.crossLinkedDB = crossLinkedDB;
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
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

    public void construct() throws UnknownDBFormatException, IOException {
        digest_insilico();
        // read output file and modify it.
        create_crossLinkedPeptides();
    }

    public void digest_insilico() throws UnknownDBFormatException, IOException {
        // See if all of this is correct.
        if (inputFileName == null) {
            flagError("You did not specify the '--input <input_file_name>' parameter!\n\nRun program without parameters for help.");
        } else if (outputFileName == null) {
            flagError("You did not specify an outputfile!\n\nRun program without parameters for help.");
        } else {
            // Parameters were all found. Let's see if we can access all files that should be accessed.
            // Note that an existing output_file will result in clean and silent overwrite of the file!
            File enzymeFile = null;
            if (enzymeFilename != null) {
                enzymeFile = new File(enzymeFilename);
                if (!enzymeFile.exists()) {
                    flagError("The enzyme definitions file you specified (" + enzymeFile + ") could not be found!\nExiting...");
                }
            }
            input = new File(inputFileName);
            output = new File(outputFileName);
            if (!output.exists()) {
                try {
                    output.createNewFile();
                } catch (IOException ioe) {
                    flagError("Could not create outputfile (" + outputFileName + "): " + ioe.getMessage());
                }
            }
            if (!input.exists()) {
                flagError("The input file you specified (" + inputFileName + ") could not be found!\nExiting...");
            } else {
                // The stuff we've received as input seems to be OK.
                // Get the props for the AutoDBLoader...
                Properties p = null;
                try {
                    InputStream is = EnzymeDigest.class
                            .getClassLoader().getResourceAsStream("DBLoaders.properties");
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
                    loader = adb.getLoaderForFile(input.getAbsolutePath());
                } catch (IOException ioe) {
                } catch (UnknownDBFormatException udfe) {
                }
                if (loader == null) {
                    flagError("Unable to determine database type for your inputfile (" + inputFileName + "), exiting...");
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

                ProcessThread pt = ProcessThread.getSubsetTask(loader, output, null, f, enzyme, massLimits, minMass, maxMass, (ProteinFilter) null);

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
                System.out.println("Finished after " + ((end - start) / 1000) + " seconds.");
            }
        }
    }

    public void create_crossLinkedPeptides() throws IOException {
        DBLoader loader = DBLoaderLoader.loadDB(output),
                loader_next = null;
        Protein start_protein = null,
                next_protein = null;
        CrossLinkerName linker = CrossLinkerName.DSS;
        crossLinkedDB = new File(output.getName() + ".fastacp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(crossLinkedDB));
        if (crossLinker.equals("EDC")) {
            linker = CrossLinkerName.EDC;
        }
        while ((start_protein = loader.nextProtein()) != null) {
            String startCoreHeader = start_protein.getHeader().getCoreHeader(),
                    startHeader = start_protein.getHeader().getAccession(),
                    startSequence = start_protein.getSequence().getSequence(),
                    startProteinDescription = start_protein.getHeader().getDescription();
            // check the first condition
            if (startSequence.length() >= minLen) {
                // find if there is a possible linker locations.
                HashMap<String, ArrayList<Integer>> possible_indices = Find_LinkerPosition.find_possibly_linker_locations(startSequence, linker);
                for (String possible_linked_aa : possible_indices.keySet()) {
                    ArrayList<Integer> indices = possible_indices.get(possible_linked_aa);
                    for (int index : indices) {
                        String mod_startSeq = startSequence.substring(0, index + 1) + "*" + startSequence.substring(index + 1);
                        // find for each possible match on the other part
                        loader_next = DBLoaderLoader.loadDB(output);
                        while ((next_protein = loader_next.nextProtein()) != null) {
                            String nextHeader = next_protein.getHeader().getAccession();
                            if (nextHeader.equals(startHeader)) {
                                // put a control to find either inter or intra proteins
                                if (crossLinked_protein_types.equals("Inter") || crossLinked_protein_types.equals("Both")) {
                                    generate_peptide_combinations(next_protein, linker, startProteinDescription, startCoreHeader, mod_startSeq, bw, possible_linked_aa, index);
                                }
                            } else {
                                if (crossLinked_protein_types.equals("Intra") || crossLinked_protein_types.equals("Both")) {
                                    generate_peptide_combinations(next_protein, linker, startProteinDescription, startCoreHeader, mod_startSeq, bw, possible_linked_aa, index);
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("A database with cross linked peptide combinations is constructed! The file name is " + outputFileName);
        bw.close();
    }

    public HashMap<String, String> generate_peptide_combinations(Protein next_protein, CrossLinkerName linker, String startProteinDescriptionName, String startCoreHeader, String mod_startSeq,
            BufferedWriter bw, String possible_linked_aa, int index_linked) throws IOException {
        HashMap<String, String> newHeader_newSequence = new HashMap<String, String>();

        String nextCoreHeader = next_protein.getHeader().getCoreHeader(),
                nextSequence = next_protein.getSequence().getSequence(),
                info_for_inversion = "",
                nextProteinName = next_protein.getHeader().getDescription();
        if (is_inverted) {
            nextSequence = new StringBuilder(nextSequence).reverse().toString();
            info_for_inversion = "_inverted";
        }
        // check the condition
        if (nextSequence.length() >= minLen) {
            HashMap<String, ArrayList<Integer>> next_possible_indices = Find_LinkerPosition.find_possibly_linker_locations(nextSequence, linker);
            if (linker.equals(CrossLinkerName.DSS)) {
                for (String next_possible_linker : next_possible_indices.keySet()) {
                    ArrayList<Integer> next_indices = next_possible_indices.get(next_possible_linker);
                    for (int next_index : next_indices) {
                        String mod_nextSeq = nextSequence.substring(0, next_index + 1) + "*" + nextSequence.substring(next_index + 1);
                        String newHeader = ">" + startCoreHeader.substring(startCoreHeader.indexOf("|") + 1) + "_" + (index_linked + 1) + "_" + nextCoreHeader.substring(nextCoreHeader.indexOf("|") + 1) + "_" + (next_index + 1) + info_for_inversion;
                        newHeader = newHeader.replace(" ", "");
                        newHeader += "|" + startProteinDescriptionName + "_" + nextProteinName;
                        String newSequence = mod_startSeq + "|" + mod_nextSeq;
                        if (newSequence.length() < maxLen_for_combined + 3) {
                            bw.write(newHeader + "\n" + newSequence + "\n");
//                            System.out.println(newHeader + "\n" + newSequence + "\n");
                            newHeader_newSequence.put(newHeader.substring(1), newSequence);
                        }
                    }
                }
            } else if (linker.equals(CrossLinkerName.EDC)) {
                if (possible_linked_aa.equals("K")) {
                    // the rest should be D or E
                    for (String next_possible_linker : next_possible_indices.keySet()) {
                        if (next_possible_linker.equals("D") || next_possible_linker.equals("S")) {
                            ArrayList<Integer> next_indices = next_possible_indices.get(next_possible_linker);
                            for (int next_index : next_indices) {
                                String mod_nextSeq = nextSequence.substring(0, next_index + 1) + "*" + nextSequence.substring(next_index + 1);
                                String newHeader = ">" + startCoreHeader.substring(startCoreHeader.indexOf("|") + 1) + "_" + (index_linked + 1) + "_" + nextCoreHeader.substring(nextCoreHeader.indexOf("|") + 1) + "_" + (next_index + 1) + info_for_inversion;
                                newHeader = newHeader.replace(" ", "");
                                newHeader += "|" + startProteinDescriptionName + "_" + nextProteinName;
                                String newSequence = mod_startSeq + "|" + mod_nextSeq;
                                if (newSequence.length() < maxLen_for_combined + 3) {
//                                    System.out.println(newHeader + "\n" + newSequence + "\n");
                                    bw.write(newHeader + "\n" + newSequence + "\n");
                                    newHeader_newSequence.put(newHeader.substring(1), newSequence);
                                }
                            }
                        }
                    }
                } else {
                    // the rest should be K
                    for (String next_possible_linker : next_possible_indices.keySet()) {
                        if (next_possible_linker.equals("K")) {
                            ArrayList<Integer> next_indices = next_possible_indices.get(next_possible_linker);
                            for (int next_index : next_indices) {
                                String mod_nextSeq = nextSequence.substring(0, next_index + 1) + "*" + nextSequence.substring(next_index + 1);
                                String newHeader = ">" + startCoreHeader.substring(startCoreHeader.indexOf("|") + 1) + "_" + (index_linked + 1) + "_" + nextCoreHeader.substring(nextCoreHeader.indexOf("|") + 1) + "_" + (next_index + 1) + info_for_inversion;
                                newHeader = newHeader.replace(" ", "");
                                newHeader += "|" + startProteinDescriptionName + "_" + nextProteinName;
                                String newSequence = mod_startSeq + "|" + mod_nextSeq;
                                if (newSequence.length() < maxLen_for_combined + 3) {
//                                    System.out.println(newHeader + "\n" + newSequence + "\n");
                                    bw.write(newHeader + "\n" + newSequence + "\n");
                                    newHeader_newSequence.put(newHeader.substring(1), newSequence);
                                }
                            }
                        }
                    }
                }
            }
        }
        return newHeader_newSequence;
    }

    /**
     * This method prints the specified error message to standard out, after
     * prepending and appending two blank lines each. It then exits the JVM!
     *
     * @param aMessage String with the error message to display.
     */
    private static void flagError(String aMessage) {
        System.err.println("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }
}
