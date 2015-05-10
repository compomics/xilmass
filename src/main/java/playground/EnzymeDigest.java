/*
 * Copyright (C) Lennart Martens
 * 
 * Contact: lennart.martens AT UGent.be (' AT ' to be replaced with '@')
 */

/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-okt-02
 * Time: 19:41:43
 */
package playground;

import crossLinker.CrossLinkerName;
import com.compomics.dbtoolkit.gui.workerthreads.ProcessThread;
import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.EnzymeLoader;
import com.compomics.dbtoolkit.io.UnknownDBFormatException;
import com.compomics.dbtoolkit.io.implementations.AutoDBLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.dbtoolkit.io.interfaces.Filter;
import com.compomics.dbtoolkit.io.interfaces.ProteinFilter;
import com.compomics.util.general.CommandLineParser;
import com.compomics.util.io.MascotEnzymeReader;
import com.compomics.util.protein.Enzyme;
import com.compomics.util.protein.Protein;
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

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2008/11/20 15:25:02 $
 */
/**
 * This class implements a command-line way of calling the sequence-based subset
 * option with a 'null' argument on the query in the ProcessThread class, albeit
 * it is NOT called in threading mode.
 *
 * @author Lennart Martens
 * @see com.compomics.dbtoolkit.gui.workerthreads.ProcessThread
 */
public class EnzymeDigest {

    /**
     * Default constructor.
     */
    public EnzymeDigest() {
    }

    /**
     * The main method takes the start-up parameters and processes the specified
     * DB accordingly.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        // First see if we should output anything useful.
        if (args == null || args.length == 0) {
            flagError("Usage:\n\tEnzymeDigest [--enzymeFile <custom_enzyme_file>] [--enzyme <enzymeName> [--mc <number_of_missed_cleavages>]] [--filter <filter_name> [--filterParam \"<filter_parameter>\"]] [--lowMass <lower_mass_treshold> --highMass <higher_mass_treshold>] --input <input_db_name> <output_db_name>\n\n\tNote that an existing output file will be silently overwritten!");
        }
        CommandLineParser clp = new CommandLineParser(args, new String[]{"enzymeFile", "enzyme", "minLen", "maxLen", "crossLinker", "crossLinkerFileName", "mc", "filter", "filterParam", "lowMass", "highMass", "input"});
        String inputFile = clp.getOptionParameter("input");
        String[] temp = clp.getParameters();
        if ((temp == null) || (temp.length == 0)) {
            flagError("You need to specify an output file!\n\nRun program without parameters for help.");
        }
        String outputFile = temp[0];
        String crossLinker = clp.getOptionParameter("crossLinker"),
                crossLinkerFileName = clp.getOptionParameter("crossLinkerFileName");
        String enzymeFilename = clp.getOptionParameter("enzymeFile");
        String enzymeName = clp.getOptionParameter("enzyme");
        String miscl = clp.getOptionParameter("mc");
        String query = clp.getOptionParameter("query");
        String filter = clp.getOptionParameter("filter");
        String lowMass = clp.getOptionParameter("lowMass");
        String highMass = clp.getOptionParameter("highMass");
        String filterParam = clp.getOptionParameter("filterParam");
        String crossLinked_protein_types = clp.getOptionParameter("crossLinkedProteinTypes");
        int minLen = Integer.parseInt(clp.getOptionParameter("minLen")),
                maxLen = Integer.parseInt(clp.getOptionParameter("maxLen"));

        // See if all of this is correct.
        if (inputFile == null) {
            flagError("You did not specify the '--input <input_file_name>' parameter!\n\nRun program without parameters for help.");
        } else if (outputFile == null) {
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
            File input = new File(inputFile);
            File output = new File(outputFile);

            if (!output.exists()) {
                try {
                    output.createNewFile();
                } catch (IOException ioe) {
                    flagError("Could not create outputfile (" + outputFile + "): " + ioe.getMessage());
                }
            }
            if (!input.exists()) {
                flagError("The input file you specified (" + inputFile + ") could not be found!\nExiting...");
            } else {
                // The stuff we've received as input seems to be OK.
                // Get the props for the AutoDBLoader...
                Properties p = null;

                try {
                    InputStream is = EnzymeDigest.class
                            .getClassLoader().getResourceAsStream("DBLoaders.properties");
                    p = new Properties();
                    if (is
                            != null) {
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
                    flagError("Unable to determine database type for your inputfile (" + inputFile + "), exiting...");
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
                        InputStream in = EnzymeDigest.class
                                .getClassLoader().getResourceAsStream("filters.properties");
                        if (in
                                == null) {
                            throw new IOException("File 'filters.properties' not found in current classpath!");
                        }

                        props.load(in);
                        String filterParams = props.getProperty(filter);
                        if (filterParams
                                == null) {
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
                // read output file and modify it.
//                create_crossLinkedPeptides(output, crossLinker, minLen, maxLen);
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
        System.err.println("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }
}
