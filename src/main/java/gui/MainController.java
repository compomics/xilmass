package gui;

import com.compomics.util.experiment.biology.PTMFactory;
import config.ConfigHolder;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * This class is main controller for the graphical user interface (GUI).
 *
 * @author Niels Hulstaert
 */
public class MainController {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MainController.class);

    /**
     * The pane names.
     */
    private static final String INPUT_OUTPUT_PANE = "Input/Output: ";
    private static final String CROSS_LINKING_PANE = "Cross-linking: ";
    private static final String IN_SILICO_DIGESTION_PANE = "In-silico digestions: ";
    private static final String MODIFICATIONS_PANE = "Modifications: ";
    private static final String SCORING_PANE = "Scoring: ";
    private static final String SPECTRUM_PRE_PROCESSING_PANE = "Spectrum preprocessing: ";
    private static final String MULTITHREADING_AND_VALIDATION_PANE = "Multithreading and validation: ";

    /**
     * The parameters properties names.
     */
    /**
     * Input/Output parameters.
     */
    private static final String FASTA_DB_PATH = "givenDBName";
    private static final String CONTAMINANTS_DB_PATH = "contaminantDBName";
    private static final String SEARCH_DB_PATH = "cxDBName";
    private static final String MGF_DIRECTORY_PATH = "mgfs";
    private static final String OUTPUT_DIRECTORY_PATH = "resultFolder";
    /**
     * Cross-linking parameters.
     */
    private static final String CROSS_LINKER = "crossLinkerName";
    private static final String LABELING = "isLabeled";
    private static final String SIDE_REACTION_SERINE = "isConsideredSideReactionSerine";
    private static final String SIDE_REACTION_THREONINE = "isConsideredSideReactionThreonine";
    private static final String SIDE_REACTION_TYROSINE = "isConsideredSideReactionTyrosine";
    private static final String CROSS_LINKING_TYPE = "crossLinkedProteinTypes";
    private static final String SEARCH_MONOLINK = "searcForAlsoMonoLink";
    private static final String MIN_PEPTIDE_LENGTH = "minLen";
    private static final String MAX_PEPTIDE_LENGTH = "maxLenCombined";
    private static final String INTRA_LINKING = "allowIntraPeptide";
    /**
     * In-silico digestion.
     */
    private static final String ENZYME = "enzymeName";
    private static final String MISSED_CLEAVAGES = "miscleavaged";
    private static final String MIN_PEPTIDE_MASS = "lowerMass";
    private static final String MAX_PEPTIDE_MASS = "higherMass";
    /**
     * Modifications.
     */
    private static final String FIXED_MODIFICATIONS = "fixedModification";
    private static final String VARIABLE_MODIFICATIONS = "variableModification";
    private static final String MAX_MOD_PEPTIDE = "maxModsPerPeptide";
    /**
     * Scoring.
     */
    private static final String NEUTRAL_LOSSES = "consider_neutrallosses";
    private static final String FRAGMENTATION_MODE = "fragMode";
    private static final String PEP_TOL_WINDOWS = "peptide_tol_total";
    private static final String FRAGMENT_MASS_TOLERANCE = "msms_tol";
    private static final String FRAGMENT_MASS_TOLERANCE_UNIT = "report_in_ppm";
    private static final String FIRST_PEPTIDE_MASS_WINDOW = "peptide_tol1";
    private static final String FIRST_PEPTIDE_MASS_WINDOW_BASE = "peptide_tol1_base";
    private static final String FIRST_PEPTIDE_MASS_WINDOW_UNIT = "is_peptide_tol1_PPM";
    private static final String SECOND_PEPTIDE_MASS_WINDOW = "peptide_tol2";
    private static final String SECOND_PEPTIDE_MASS_WINDOW_BASE = "peptide_tol2_base";
    private static final String SECOND_PEPTIDE_MASS_WINDOW_UNIT = "is_peptide_tol2_PPM";
    private static final String THIRD_PEPTIDE_MASS_WINDOW = "peptide_tol3";
    private static final String THIRD_PEPTIDE_MASS_WINDOW_BASE = "peptide_tol3_base";
    private static final String THIRD_PEPTIDE_MASS_WINDOW_UNIT = "is_peptide_tol3_PPM";
    private static final String FOURTH_PEPTIDE_MASS_WINDOW = "peptide_tol4";
    private static final String FOURTH_PEPTIDE_MASS_WINDOW_BASE = "peptide_tol4_base";
    private static final String FOURTH_PEPTIDE_MASS_WINDOW_UNIT = "is_peptide_tol4_PPM";
    private static final String FIFTH_PEPTIDE_MASS_WINDOW = "peptide_tol5";
    private static final String FIFTH_PEPTIDE_MASS_WINDOW_BASE = "peptide_tol5_base";
    private static final String FIFTH_PEPTIDE_MASS_WINDOW_UNIT = "is_peptide_tol5_PPM";
    private static final String MIN_NUMBER_OF_PEAKS = "minRequiredPeaks";
    private static final String PEAK_MATCHING = "isAllMatchedPeaks";
    private static final String MS1_REPORTING = "report_in_ppm";
    /**
     * Spectrum preprocessing.
     */
    private static final String SPECTRUM_MASS_WINDOW = "massWindow";
    private static final String WINDOW_MIN_NUMBER_OF_PEAKS = "minimumFiltedPeaksNumberForEachWindow";
    private static final String WINDOW_MAX_NUMBER_OF_PEAKS = "maximumFiltedPeaksNumberForEachWindow";
    private static final String LOWER_PREC_MASS_BOUND = "minPrecMassIsotopicPeakSelected";
    private static final String DEISOTOPE_PRECISION = "deconvulatePrecision";
    private static final String DECONVOLUTE_PRECISION = "deconvulatePrecision";
    /**
     * Spectrum preprocessing.
     */
    private static final String MULTI_THREADING = "threadNumbers";
    private static final String WRITE_PERCOLATOR = "isPercolatorAsked";
    private static final String IMPROVED_FDR = "isImprovedFDR";
    private static final String INTER_PROTEIN_FDR = "fdrInterPro";
    private static final String INTRA_PROTEIN_FDR = "fdrIntraPro";
    private static final String GLOBAL_FDR = "fdr";

    /**
     * Model fields.
     */
    private List<String> utilitiesPtms = PTMFactory.getInstance().getPTMs();
    private final Comparator<String> stringComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };
    /**
     * A mapping for boolean properties (key: Boolean value; value: property
     * value).
     */
    private final Map<Boolean, String> booleanGuiToPropertiesMapping = new HashMap();
    /**
     * A mapping for boolean properties (key: property value; value: Boolean
     * value).
     */
    private final Map<String, Boolean> booleanPropertiesToGuiMapping = new HashMap();
    /**
     * A mapping for boolean properties (key: ComboBox index; value: property
     * value).
     */
    private final Map<Integer, String> booleanIndexGuiToPropertiesMapping = new HashMap();
    /**
     * A mapping for boolean properties (key: property value; value: ComboBox
     * index).
     */
    private final Map<String, Integer> booleanIndexPropertiesToGuiMapping = new HashMap();
    /**
     * A map that holds the information message for each pane of the tabbed
     * pane.
     */
    private final Map<Integer, String> paneInformationMessages = new HashMap();
    private XilmassSwingWorker xilmassSwingWorker;

    /**
     * The views of this controller.
     */
    private final MainFrame mainFrame = new MainFrame();
    private RunDialog runDialog;

    /**
     * No-arg constructor.
     */
    public MainController() {
        booleanGuiToPropertiesMapping.put(true, "T");
        booleanGuiToPropertiesMapping.put(false, "F");
        booleanPropertiesToGuiMapping.put("T", true);
        booleanPropertiesToGuiMapping.put("F", false);
        booleanIndexGuiToPropertiesMapping.put(0, "T");
        booleanIndexGuiToPropertiesMapping.put(1, "F");
        booleanIndexPropertiesToGuiMapping.put("T", 0);
        booleanIndexPropertiesToGuiMapping.put("F", 1);
        paneInformationMessages.put(0, "Input and output related parameters.");
        paneInformationMessages.put(1, "Cross-linking related parameters.");
        paneInformationMessages.put(2, "In-silico digestion related parameters.");
        paneInformationMessages.put(3, "Peptide modifications related parameters.");
        paneInformationMessages.put(4, "Scoring algorithm related parameters.");
        paneInformationMessages.put(5, "Spectrum preprocessing related parameters.");
        paneInformationMessages.put(5, "Spectrum preprocessing related parameters.");
        paneInformationMessages.put(6, "Validation related parameters.");
    }

    /**
     * Init the controller.
     */
    public void init() {
        //add gui appender
        LogTextAreaAppender logTextAreaAppender = new LogTextAreaAppender();
        logTextAreaAppender.setThreshold(Level.INFO);
        logTextAreaAppender.setImmediateFlush(true);
        PatternLayout layout = new org.apache.log4j.PatternLayout();
        layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss} - %m%n");
        logTextAreaAppender.setLayout(layout);

        LOGGER.addAppender(logTextAreaAppender);
        LOGGER.setLevel(Level.INFO);

        mainFrame.setTitle("Xilmass run graphical user interface " + ConfigHolder.getInstance().getString("xilmass.version", ""));

        runDialog = new RunDialog(mainFrame, true);
        runDialog.getLogTextArea().setText("..." + System.lineSeparator());

        //get the appender for setting the text area
        logTextAreaAppender.setRunDialog(runDialog);

        //init the modification dual lists
        mainFrame.getFixedModificationsDualList().init(stringComparator);
        mainFrame.getVariableModificationsDualList().init(stringComparator);

        //init file choosers
        //disable select multiple files
        mainFrame.getFastaDbChooser().setMultiSelectionEnabled(false);
        mainFrame.getSearchDbChooser().setMultiSelectionEnabled(false);
        mainFrame.getDirectoryChooser().setMultiSelectionEnabled(false);
        mainFrame.getFileChooser().setMultiSelectionEnabled(false);
        //set select directories only
        mainFrame.getDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //set file filters
        mainFrame.getFastaDbChooser().setFileFilter(new FastaFileFilter());

        //set first pane information message
        mainFrame.getPaneInformationMessageLabel().setText(paneInformationMessages.get(0));

        //add this hack to the JSpinner
        JComponent comp = mainFrame.getPeptideToleranceSpinner().getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);

        //add action listeners
        mainFrame.getMainTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mainFrame.getPaneInformationMessageLabel().setText(paneInformationMessages.get(mainFrame.getMainTabbedPane().getSelectedIndex()));
            }
        });

        mainFrame.getFastaDbBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getFastaDbChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getFastaDbPathTextField().setText(mainFrame.getFastaDbChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getContaminantsFastaDbBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getFastaDbChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getContaminantsFastaDbPathTextField().setText(mainFrame.getFastaDbChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getSearchDbBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getFileChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getSearchDbPathTextField().setText(mainFrame.getFileChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getMgfDirectoryBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getDirectoryChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getMgfDirectoryPathTextField().setText(mainFrame.getDirectoryChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getOutputDirectoryBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getDirectoryChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getOutputDirectoryPathTextField().setText(mainFrame.getDirectoryChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getPeptideToleranceSpinner().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (int) mainFrame.getPeptideToleranceSpinner().getValue();

                switch (value) {
                    case 5:
                        mainFrame.getFifthPeptideMassToleranceWindowTextField().setEnabled(true);
                        mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setEnabled(true);
                        mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setEnabled(true);
                    case 4:
                        if (value == 4) {
                            mainFrame.getFifthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                        }
                        mainFrame.getFourthPeptideMassToleranceWindowTextField().setEnabled(true);
                        mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setEnabled(true);
                        mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setEnabled(true);
                    case 3:
                        if (value == 3) {
                            mainFrame.getFifthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                        }
                        mainFrame.getThirdPeptideMassToleranceWindowTextField().setEnabled(true);
                        mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setEnabled(true);
                        mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setEnabled(true);
                    case 2:
                        if (value == 2) {
                            mainFrame.getFifthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                        }
                        mainFrame.getSecondPeptideMassToleranceWindowTextField().setEnabled(true);
                        mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().setEnabled(true);
                        mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().setEnabled(true);
                    case 1:
                        if (value == 1) {
                            mainFrame.getFifthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                            mainFrame.getSecondPeptideMassToleranceWindowTextField().setEnabled(false);
                            mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().setEnabled(false);
                            mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().setEnabled(false);
                        }
                        mainFrame.getFirstPeptideMassToleranceWindowTextField().setEnabled(true);
                        mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().setEnabled(true);
                        mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().setEnabled(true);
                        break;
                }

            }
        });

        mainFrame.getFdrCalcalationComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean globalSelected = mainFrame.getFdrCalcalationComboBox().getSelectedIndex() == 1;
                mainFrame.getGlobalFdrValueTextField().setEnabled(globalSelected);
                mainFrame.getInterProteinFdrValueTextField().setEnabled(!globalSelected);
                mainFrame.getIntraProteinFdrValueTextField().setEnabled(!globalSelected);
            }
        });

        mainFrame.getRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //validate input
                List<String> validationMessages = validateInput();
                if (!validationMessages.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    for (String validationMessage : validationMessages) {
                        message.append(validationMessage).append(System.lineSeparator());
                    }
                    showMessageDialog("Validation errors", message.toString(), JOptionPane.WARNING_MESSAGE);
                } else {
                    //copy the parameter values to the ConfigHolder
                    copyParameterValues();
                    int reply = JOptionPane.showConfirmDialog(mainFrame, "Save the current settings for future usage?"
                            + System.lineSeparator() + "Otherwise the settings will be used for this run only.", "Save settings", JOptionPane.INFORMATION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        try {
                            ConfigHolder.getInstance().save();
                        } catch (ConfigurationException ce) {
                            showMessageDialog("Save problem", "The settings could not be saved."
                                    + System.lineSeparator()
                                    + ce.getMessage(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    if (reply != JOptionPane.CANCEL_OPTION) {
                        xilmassSwingWorker = new XilmassSwingWorker();
                        xilmassSwingWorker.execute();

                        //show the run dialog
                        centerRunDialog();
                        runDialog.setVisible(true);
                    }
                }
            }
        });

        mainFrame.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });

        runDialog.getClearButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDialog.getLogTextArea().setText("..." + System.lineSeparator());
            }
        });

        runDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //load the parameters from the properties file
        loadParameterValues();
    }

    /**
     * Show the view of this controller.
     */
    public void showView() {
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * Load the parameter values from the properties file and set them in the
     * matching fields.
     */
    private void loadParameterValues() {
        //Input/Output params
        mainFrame.getFastaDbPathTextField().setText(ConfigHolder.getInstance().getString(FASTA_DB_PATH));
        mainFrame.getContaminantsFastaDbPathTextField().setText(ConfigHolder.getInstance().getString(CONTAMINANTS_DB_PATH));
        mainFrame.getSearchDbPathTextField().setText(ConfigHolder.getInstance().getString(SEARCH_DB_PATH));
        mainFrame.getMgfDirectoryPathTextField().setText(ConfigHolder.getInstance().getString(MGF_DIRECTORY_PATH));
        mainFrame.getOutputDirectoryPathTextField().setText(ConfigHolder.getInstance().getString(OUTPUT_DIRECTORY_PATH));
        //Cross-linking params
        String crossLinker = ConfigHolder.getInstance().getString(CROSS_LINKER);
        mainFrame.getCrossLinkerComboBox().getModel().setSelectedItem(crossLinker);
        String isLabeled = ConfigHolder.getInstance().getString(LABELING);
        switch (isLabeled) {
            case "T":
                mainFrame.getLabelingComboBox().setSelectedIndex(1);
                break;
            case "F":
                mainFrame.getLabelingComboBox().setSelectedIndex(0);
                break;
            case "B":
                mainFrame.getLabelingComboBox().setSelectedIndex(2);
                break;
            default:
                throw new IllegalArgumentException("Cross-linker label type not found.");
        }
        boolean serine = ConfigHolder.getInstance().getBoolean(SIDE_REACTION_SERINE);
        mainFrame.getSerineCheckBox().setSelected(serine);
        boolean threonine = ConfigHolder.getInstance().getBoolean(SIDE_REACTION_THREONINE);
        mainFrame.getThreonineCheckBox().setSelected(threonine);
        boolean tyrosine = ConfigHolder.getInstance().getBoolean(SIDE_REACTION_TYROSINE);
        mainFrame.getTyrosineCheckBox().setSelected(tyrosine);
        String linkingType = ConfigHolder.getInstance().getString(CROSS_LINKING_TYPE);
        switch (linkingType) {
            case "intra":
                mainFrame.getCrosslinkingTypeComboBox().setSelectedIndex(0);
                break;
            case "inter":
                mainFrame.getCrosslinkingTypeComboBox().setSelectedIndex(1);
                break;
            case "both":
                mainFrame.getCrosslinkingTypeComboBox().setSelectedIndex(2);
                break;
            default:
                throw new IllegalArgumentException("Cross-linker linking type not found.");
        }
        boolean doMonoLinkSearch = ConfigHolder.getInstance().getBoolean(SEARCH_MONOLINK);
        mainFrame.getMonoLinkingCheckBox().setSelected(doMonoLinkSearch);
        mainFrame.getMinimumPeptideLengthTextField().setText(ConfigHolder.getInstance().getString(MIN_PEPTIDE_LENGTH));
        mainFrame.getMaximumPeptideLengthTextField().setText(ConfigHolder.getInstance().getString(MAX_PEPTIDE_LENGTH));
        boolean intraLinking = ConfigHolder.getInstance().getBoolean(INTRA_LINKING);
        mainFrame.getIntraLinkingCheckBox().setSelected(intraLinking);
        //In-silico digestion params
        mainFrame.getEnzymeComboBox().getModel().setSelectedItem(ConfigHolder.getInstance().getString(ENZYME));
        mainFrame.getMissedCleavagesTextField().setText(ConfigHolder.getInstance().getString(MISSED_CLEAVAGES));
        mainFrame.getMinimumPeptideMassTextField().setText(ConfigHolder.getInstance().getString(MIN_PEPTIDE_MASS));
        mainFrame.getMaximumPeptideMassTextField().setText(ConfigHolder.getInstance().getString(MAX_PEPTIDE_MASS));
        //modification params
        String fixedModifications = ConfigHolder.getInstance().getString(FIXED_MODIFICATIONS);
        String variableModifications = ConfigHolder.getInstance().getString(VARIABLE_MODIFICATIONS);
        List<String> fixedModificationList = Arrays.asList(StringUtils.split(fixedModifications, ';'));
        List<String> variableModificationList = Arrays.asList(StringUtils.split(variableModifications, ';'));
        mainFrame.getFixedModificationsDualList().populateLists(utilitiesPtms, fixedModificationList);
        mainFrame.getVariableModificationsDualList().populateLists(utilitiesPtms, variableModificationList);
        mainFrame.getMaxModPeptideTextField().setText(ConfigHolder.getInstance().getString(MAX_MOD_PEPTIDE));
        //scoring params
        int neutralLosses = ConfigHolder.getInstance().getInt(NEUTRAL_LOSSES);
        mainFrame.getNeutralLossesComboBox().setSelectedIndex(neutralLosses);
        String fragmentationMode = ConfigHolder.getInstance().getString(FRAGMENTATION_MODE);
        switch (fragmentationMode) {
            case "HCD":
                mainFrame.getFragmentationModeComboBox().setSelectedIndex(0);
                break;
            case "CID":
                mainFrame.getFragmentationModeComboBox().setSelectedIndex(1);
                break;
            case "ETD":
                mainFrame.getFragmentationModeComboBox().setSelectedIndex(2);
                break;
        }
        int peptideToleranceWindows = ConfigHolder.getInstance().getInt(PEP_TOL_WINDOWS);
        mainFrame.getPeptideToleranceSpinner().setValue(peptideToleranceWindows);
        mainFrame.getFragmentMassToleranceValueTextField().setText(ConfigHolder.getInstance().getString(FRAGMENT_MASS_TOLERANCE));
        String fragmentMassToleranceUnit = ConfigHolder.getInstance().getString(FRAGMENT_MASS_TOLERANCE_UNIT);
        mainFrame.getFragmentMassToleranceUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(fragmentMassToleranceUnit));
        mainFrame.getFirstPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW));
        mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW_BASE));
        String firstToleranceWindowUnit = ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(firstToleranceWindowUnit));
        mainFrame.getSecondPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW));
        mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW_BASE));
        String secondToleranceWindowUnit = ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(secondToleranceWindowUnit));
        mainFrame.getThirdPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW));
        mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW_BASE));
        String thirdToleranceWindowUnit = ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(thirdToleranceWindowUnit));
        mainFrame.getFourthPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW));
        mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW_BASE));
        String fourthToleranceWindowUnit = ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(fourthToleranceWindowUnit));
        mainFrame.getFifthPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FIFTH_PEPTIDE_MASS_WINDOW));
        mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(FIFTH_PEPTIDE_MASS_WINDOW_BASE));
        String fifthToleranceWindowUnit = ConfigHolder.getInstance().getString(FIFTH_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getFifthPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(fifthToleranceWindowUnit));
        mainFrame.getMinNumberOfPeaksTextField().setText(ConfigHolder.getInstance().getString(MIN_NUMBER_OF_PEAKS));
        String peakMatch = ConfigHolder.getInstance().getString(PEAK_MATCHING);
        mainFrame.getPeakMatchingComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(peakMatch));
        String ms1ReportingUnit = ConfigHolder.getInstance().getString(MS1_REPORTING);
        mainFrame.getMs1ReportingComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(ms1ReportingUnit));
        //Spectrum preprocessing params
        mainFrame.getSpectrumMassWindowValueTextField().setText(ConfigHolder.getInstance().getString(SPECTRUM_MASS_WINDOW));
        mainFrame.getMinimumNumberOfPeaksTextField().setText(ConfigHolder.getInstance().getString(WINDOW_MIN_NUMBER_OF_PEAKS));
        mainFrame.getMaximumNumberOfPeaksTextField().setText(ConfigHolder.getInstance().getString(WINDOW_MAX_NUMBER_OF_PEAKS));
        mainFrame.getLowerPrecursorMassBoundTextField().setText(ConfigHolder.getInstance().getString(LOWER_PREC_MASS_BOUND));
        mainFrame.getDeisotopePrecisionTextField().setText(ConfigHolder.getInstance().getString(DEISOTOPE_PRECISION));
        mainFrame.getDeconvulatePrecisionTextField().setText(ConfigHolder.getInstance().getString(DECONVOLUTE_PRECISION));
        //Multithreading and validation params
        mainFrame.getNumberOfThreadsTextField().setText(ConfigHolder.getInstance().getString(MULTI_THREADING));
        boolean writePrecolatorInput = ConfigHolder.getInstance().getBoolean(WRITE_PERCOLATOR);
        mainFrame.getWritePercolatorInputFilesCheckBox().setSelected(writePrecolatorInput);
        String improvedFdr = ConfigHolder.getInstance().getString(IMPROVED_FDR);
        mainFrame.getFdrCalcalationComboBox().setSelectedIndex(booleanIndexPropertiesToGuiMapping.get(improvedFdr));
        mainFrame.getGlobalFdrValueTextField().setText(ConfigHolder.getInstance().getString(GLOBAL_FDR));
        mainFrame.getInterProteinFdrValueTextField().setText(ConfigHolder.getInstance().getString(INTER_PROTEIN_FDR));
        mainFrame.getIntraProteinFdrValueTextField().setText(ConfigHolder.getInstance().getString(INTRA_PROTEIN_FDR));
    }

    /**
     * Copy the parameter values to the ConfigHolder so that they can be used in
     * the Xilmass run.
     */
    private void copyParameterValues() {
        //Input/Output params
        ConfigHolder.getInstance().setProperty(FASTA_DB_PATH, mainFrame.getFastaDbPathTextField().getText());
        ConfigHolder.getInstance().setProperty(CONTAMINANTS_DB_PATH, mainFrame.getContaminantsFastaDbPathTextField().getText());
        ConfigHolder.getInstance().setProperty(SEARCH_DB_PATH, mainFrame.getSearchDbPathTextField().getText());
        ConfigHolder.getInstance().setProperty(MGF_DIRECTORY_PATH, mainFrame.getMgfDirectoryPathTextField().getText());
        ConfigHolder.getInstance().setProperty(OUTPUT_DIRECTORY_PATH, mainFrame.getOutputDirectoryPathTextField().getText());
        //Cross-linking params
        ConfigHolder.getInstance().setProperty(CROSS_LINKER, mainFrame.getCrossLinkerComboBox().getSelectedItem());
        int isLabeled = mainFrame.getLabelingComboBox().getSelectedIndex();
        switch (isLabeled) {
            case 0:
                ConfigHolder.getInstance().setProperty(LABELING, "F");
                break;
            case 1:
                ConfigHolder.getInstance().setProperty(LABELING, "T");
                break;
            case 2:
                ConfigHolder.getInstance().setProperty(LABELING, "B");
                break;
        }
        boolean serine = mainFrame.getSerineCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(SIDE_REACTION_SERINE, booleanGuiToPropertiesMapping.get(serine));
        boolean threonine = mainFrame.getThreonineCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(SIDE_REACTION_THREONINE, booleanGuiToPropertiesMapping.get(threonine));
        boolean tyrosine = mainFrame.getTyrosineCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(SIDE_REACTION_TYROSINE, booleanGuiToPropertiesMapping.get(tyrosine));
        int linkingType = mainFrame.getCrosslinkingTypeComboBox().getSelectedIndex();
        switch (linkingType) {
            case 0:
                ConfigHolder.getInstance().setProperty(CROSS_LINKING_TYPE, "intra");
                break;
            case 1:
                ConfigHolder.getInstance().setProperty(CROSS_LINKING_TYPE, "inter");
                break;
            case 2:
                ConfigHolder.getInstance().setProperty(CROSS_LINKING_TYPE, "both");
                break;
        }
        boolean doMonoLinkSearch = mainFrame.getMonoLinkingCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(SEARCH_MONOLINK, booleanGuiToPropertiesMapping.get(doMonoLinkSearch));
        ConfigHolder.getInstance().setProperty(MIN_PEPTIDE_LENGTH, mainFrame.getMinimumPeptideLengthTextField().getText());
        ConfigHolder.getInstance().setProperty(MAX_PEPTIDE_LENGTH, mainFrame.getMaximumPeptideLengthTextField().getText());
        boolean intraLinking = mainFrame.getIntraLinkingCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(INTRA_LINKING, booleanGuiToPropertiesMapping.get(intraLinking));
        //In-silico digestion params
        ConfigHolder.getInstance().setProperty(ENZYME, mainFrame.getEnzymeComboBox().getSelectedItem());
        ConfigHolder.getInstance().setProperty(MISSED_CLEAVAGES, mainFrame.getMissedCleavagesTextField().getText());
        ConfigHolder.getInstance().setProperty(MIN_PEPTIDE_MASS, mainFrame.getMinimumPeptideMassTextField().getText());
        ConfigHolder.getInstance().setProperty(MAX_PEPTIDE_MASS, mainFrame.getMaximumPeptideMassTextField().getText());
        //modification params
        List<String> fixedModifications = mainFrame.getFixedModificationsDualList().getAddedItems();
        String joinedFixedModifications = StringUtils.join(fixedModifications, ';');
        ConfigHolder.getInstance().setProperty(FIXED_MODIFICATIONS, joinedFixedModifications);
        List<String> variableModifications = mainFrame.getVariableModificationsDualList().getAddedItems();
        String joinedVariableModifications = StringUtils.join(variableModifications, ';');
        ConfigHolder.getInstance().setProperty(VARIABLE_MODIFICATIONS, joinedVariableModifications);
        ConfigHolder.getInstance().setProperty(MAX_MOD_PEPTIDE, mainFrame.getMaxModPeptideTextField().getText());
        //scoring params
        ConfigHolder.getInstance().setProperty(NEUTRAL_LOSSES, mainFrame.getNeutralLossesComboBox().getSelectedIndex());
        int fragmentationMode = mainFrame.getFragmentationModeComboBox().getSelectedIndex();
        switch (fragmentationMode) {
            case 0:
                ConfigHolder.getInstance().setProperty(FRAGMENTATION_MODE, "HCD");
                break;
            case 1:
                ConfigHolder.getInstance().setProperty(FRAGMENTATION_MODE, "CID");
                break;
            case 2:
                ConfigHolder.getInstance().setProperty(FRAGMENTATION_MODE, "ETD");
                break;
        }
        ConfigHolder.getInstance().setProperty(PEP_TOL_WINDOWS, mainFrame.getPeptideToleranceSpinner().getValue());
        ConfigHolder.getInstance().setProperty(FRAGMENT_MASS_TOLERANCE, mainFrame.getFragmentMassToleranceValueTextField().getText());
        int fragmentMassToleranceUnit = mainFrame.getFragmentMassToleranceUnitComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(FRAGMENT_MASS_TOLERANCE_UNIT, booleanIndexGuiToPropertiesMapping.get(fragmentMassToleranceUnit));
        ConfigHolder.getInstance().setProperty(FIRST_PEPTIDE_MASS_WINDOW, mainFrame.getFirstPeptideMassToleranceWindowTextField().getText());
        ConfigHolder.getInstance().setProperty(FIRST_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().getText());
        int firstToleranceWindowUnit = mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, booleanIndexGuiToPropertiesMapping.get(firstToleranceWindowUnit));
        ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW, mainFrame.getSecondPeptideMassToleranceWindowTextField().getText());
        ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().getText());
        int secondToleranceWindowUnit = mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, booleanIndexGuiToPropertiesMapping.get(secondToleranceWindowUnit));
        ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW, mainFrame.getThirdPeptideMassToleranceWindowTextField().getText());
        ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().getText());
        int thirdToleranceWindowUnit = mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW_UNIT, booleanIndexGuiToPropertiesMapping.get(thirdToleranceWindowUnit));
        ConfigHolder.getInstance().setProperty(FOURTH_PEPTIDE_MASS_WINDOW, mainFrame.getFourthPeptideMassToleranceWindowTextField().getText());
        ConfigHolder.getInstance().setProperty(FOURTH_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().getText());
        int fourthToleranceWindowUnit = mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, booleanIndexGuiToPropertiesMapping.get(fourthToleranceWindowUnit));
        ConfigHolder.getInstance().setProperty(MIN_NUMBER_OF_PEAKS, mainFrame.getMinNumberOfPeaksTextField());
        int peakMatch = mainFrame.getPeakMatchingComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(PEAK_MATCHING, booleanIndexGuiToPropertiesMapping.get(peakMatch));
        int ms1ReportingUnit = mainFrame.getMs1ReportingComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(MS1_REPORTING, booleanIndexGuiToPropertiesMapping.get(ms1ReportingUnit));
        //Spectrum preprocessing params
        ConfigHolder.getInstance().setProperty(SPECTRUM_MASS_WINDOW, mainFrame.getSpectrumMassWindowValueTextField().getText());
        ConfigHolder.getInstance().setProperty(WINDOW_MIN_NUMBER_OF_PEAKS, mainFrame.getMinimumNumberOfPeaksTextField().getText());
        ConfigHolder.getInstance().setProperty(WINDOW_MAX_NUMBER_OF_PEAKS, mainFrame.getMaximumNumberOfPeaksTextField().getText());
        ConfigHolder.getInstance().setProperty(LOWER_PREC_MASS_BOUND, mainFrame.getLowerPrecursorMassBoundTextField().getText());
        ConfigHolder.getInstance().setProperty(DEISOTOPE_PRECISION, mainFrame.getDeisotopePrecisionTextField().getText());
        ConfigHolder.getInstance().setProperty(DEISOTOPE_PRECISION, mainFrame.getDeconvulatePrecisionTextField().getText());
        //Multithreading and validation params
        ConfigHolder.getInstance().setProperty(MULTI_THREADING, mainFrame.getNumberOfThreadsTextField().getText());
        boolean writePrecolatorInput = mainFrame.getWritePercolatorInputFilesCheckBox().isSelected();
        ConfigHolder.getInstance().setProperty(WRITE_PERCOLATOR, booleanGuiToPropertiesMapping.get(writePrecolatorInput));
        int improvedFdr = mainFrame.getFdrCalcalationComboBox().getSelectedIndex();
        ConfigHolder.getInstance().setProperty(IMPROVED_FDR, booleanIndexGuiToPropertiesMapping.get(improvedFdr));
        if (improvedFdr == 0) {
            ConfigHolder.getInstance().setProperty(GLOBAL_FDR, mainFrame.getGlobalFdrValueTextField().getText());
        } else {
            ConfigHolder.getInstance().setProperty(INTER_PROTEIN_FDR, mainFrame.getInterProteinFdrValueTextField().getText());
            ConfigHolder.getInstance().setProperty(INTRA_PROTEIN_FDR, mainFrame.getIntraProteinFdrValueTextField().getText());
        }
    }

    /**
     * Validate the user input and return a list of validation messages if
     * necessary.
     *
     * @return the list of validation messages
     */
    private List<String> validateInput() {
        List<String> validationMessages = new ArrayList<>();

        //Input/Output params
        if (mainFrame.getFastaDbPathTextField().getText().isEmpty()) {
            validationMessages.add(INPUT_OUTPUT_PANE + "Please provide a FASTA database file.");
        }
        if (mainFrame.getSearchDbPathTextField().getText().isEmpty()) {
            validationMessages.add(INPUT_OUTPUT_PANE + "Please provide a search database file.");
        }
        if (mainFrame.getMgfDirectoryPathTextField().getText().isEmpty()) {
            validationMessages.add(INPUT_OUTPUT_PANE + "Please provide a directory with MGF files.");
        }
        if (mainFrame.getOutputDirectoryPathTextField().getText().isEmpty()) {
            validationMessages.add(INPUT_OUTPUT_PANE + "Please provide an output directory.");
        }
        //Cross-linking params
        if (mainFrame.getMinimumPeptideLengthTextField().getText().isEmpty()) {
            validationMessages.add(CROSS_LINKING_PANE + "Please provide the minimun peptide length.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMinimumPeptideLengthTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(CROSS_LINKING_PANE + "Please provide a positive minimun peptide length.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric minimun peptide length.");
            }
        }
        if (mainFrame.getMaximumPeptideLengthTextField().getText().isEmpty()) {
            validationMessages.add(CROSS_LINKING_PANE + "Please provide the maximum peptide length.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMinimumPeptideLengthTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(CROSS_LINKING_PANE + "Please provide a positive maximum peptide length.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(CROSS_LINKING_PANE + "Please provide a numeric maximum peptide length.");
            }
        }
        //In-silico digestion params
        if (mainFrame.getMissedCleavagesTextField().getText().isEmpty()) {
            validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide the number of allowed missed cleavages.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMissedCleavagesTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a positive number of allowed missed cleavages.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a numeric number of allowed missed cleavages.");
            }
        }
        if (mainFrame.getMinimumPeptideMassTextField().getText().isEmpty()) {
            validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a minimum peptide mass.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getMinimumPeptideMassTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a positive minimum peptide mass.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a numeric minimum peptide mass.");
            }
        }
        if (mainFrame.getMaximumPeptideMassTextField().getText().isEmpty()) {
            validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a maximum peptide mass.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getMaximumPeptideMassTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a positive maximum peptide mass.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(IN_SILICO_DIGESTION_PANE + "Please provide a numeric maximum peptide mass.");
            }
        }
        //modification params
        if (mainFrame.getMaxModPeptideTextField().getText().isEmpty()) {
            validationMessages.add(MODIFICATIONS_PANE + "Please provide the number of allowed modifications per peptide.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMaxModPeptideTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(MODIFICATIONS_PANE + "Please provide a positive number of allowed modifications per peptide.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(MODIFICATIONS_PANE + "Please provide a numeric number of allowed modifications per peptide.");
            }
        }
        //scoring params
        int value = (int) mainFrame.getPeptideToleranceSpinner().getValue();

        switch (value) {
            case 5:
                if (mainFrame.getFifthPeptideMassToleranceWindowTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a fifth peptide tolerance mass window value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFifthPeptideMassToleranceWindowTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive fifth peptide tolerance mass window value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric fifth peptide tolerance mass window value.");
                    }
                }
                if (mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a fifth peptide tolerance mass window base value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFifthPeptideMassToleranceWindowBaseTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive fifth peptide tolerance mass window base value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric fifth peptide tolerance mass window base value.");
                    }
                }
            case 4:
                if (mainFrame.getFourthPeptideMassToleranceWindowTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a fourth peptide tolerance mass window value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFourthPeptideMassToleranceWindowTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive fourth peptide tolerance mass window value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric fourth peptide tolerance mass window value.");
                    }
                }
                if (mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a fourth  peptide tolerance mass window base value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive fourth peptide tolerance mass window base value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric fourth peptide tolerance mass window base value.");
                    }
                }
            case 3:
                if (mainFrame.getThirdPeptideMassToleranceWindowTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a third peptide tolerance mass window value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getThirdPeptideMassToleranceWindowTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive third peptide tolerance mass window value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric third peptide tolerance mass window value.");
                    }
                }
                if (mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a third peptide tolerance mass window base value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive third peptide tolerance mass window base value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric third peptide tolerance mass window base value.");
                    }
                }
            case 2:
               if (mainFrame.getSecondPeptideMassToleranceWindowTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a second peptide tolerance mass window value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getSecondPeptideMassToleranceWindowTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive second peptide tolerance mass window value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric second peptide tolerance mass window value.");
                    }
                }
                if (mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a second peptide tolerance mass window base value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive second peptide tolerance mass window base value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric second peptide tolerance mass window base value.");
                    }
                }
            case 1:
                if (mainFrame.getFirstPeptideMassToleranceWindowTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a first peptide tolerance mass window value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFirstPeptideMassToleranceWindowTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive first peptide tolerance mass window value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric first peptide tolerance mass window value.");
                    }
                }
                if (mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().getText().isEmpty()) {
                    validationMessages.add(SCORING_PANE + "Please provide a first peptide tolerance mass window base value.");
                } else {
                    try {
                        Double tolerance = Double.valueOf(mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().getText());
                        if (tolerance < 0.0) {
                            validationMessages.add(SCORING_PANE + "Please provide a positive first peptide tolerance mass window base value.");
                        }
                    } catch (NumberFormatException nfe) {
                        validationMessages.add(SCORING_PANE + "Please provide a numeric first peptide tolerance mass window base value.");
                    }
                }
                break;
        }
        if (mainFrame.getMinNumberOfPeaksTextField().getText().isEmpty()) {
            validationMessages.add(SCORING_PANE + "Please provide the minimum number of matched peaks.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMinimumNumberOfPeaksTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(SCORING_PANE + "Please provide a positive minimum number of matched peaks.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SCORING_PANE + "Please provide a numeric minimum number of matched peaks.");
            }
        }
        //Spectrum preprocessing params
        if (mainFrame.getSpectrumMassWindowValueTextField().getText().isEmpty()) {
            validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a spectrum scoring mass window value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getSpectrumMassWindowValueTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a positive spectrum scoring mass window value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a numeric spectrum scoring mass window value.");
            }
        }
        if (mainFrame.getMinimumNumberOfPeaksTextField().getText().isEmpty()) {
            validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide the minimum number of filtered peaks per window.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMinimumNumberOfPeaksTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a positive minimum number of filtered peaks per window.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a numeric minimum number of filtered peaks per window.");
            }
        }
        if (mainFrame.getMaximumNumberOfPeaksTextField().getText().isEmpty()) {
            validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide the maximum number of filtered peaks per window.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getMaximumNumberOfPeaksTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a positive maximum number of filtered peaks per window.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a numeric maximum number of filtered peaks per window.");
            }
        }
        if (mainFrame.getLowerPrecursorMassBoundTextField().getText().isEmpty()) {
            validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a lower precursor mass bound value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getLowerPrecursorMassBoundTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a positive lower precursor mass bound value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a numeric lower precursor mass bound value.");
            }
        }
        if (mainFrame.getDeisotopePrecisionTextField().getText().isEmpty()) {
            validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a deisotope precision value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getDeisotopePrecisionTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a positive deisotope precision value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(SPECTRUM_PRE_PROCESSING_PANE + "Please provide a numeric deisotope precision value.");
            }
        }
        //Multithreading and validation params
        if (mainFrame.getNumberOfThreadsTextField().getText().isEmpty()) {
            validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide the number of threads.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getNumberOfThreadsTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a positive number threads.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a numeric number of threads.");
            }
        }
        int improvedFdr = mainFrame.getFdrCalcalationComboBox().getSelectedIndex();
        if (improvedFdr == 0) {
            if (mainFrame.getInterProteinFdrValueTextField().getText().isEmpty()) {
                validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide an inter-protein FDR value.");
            } else {
                try {
                    Double tolerance = Double.valueOf(mainFrame.getInterProteinFdrValueTextField().getText());
                    if (tolerance < 0.0) {
                        validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a positive inter-protein FDR value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a numeric inter-protein FDR value.");
                }
            }
            if (mainFrame.getIntraProteinFdrValueTextField().getText().isEmpty()) {
                validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide an intra-protein FDR value.");
            } else {
                try {
                    Double tolerance = Double.valueOf(mainFrame.getIntraProteinFdrValueTextField().getText());
                    if (tolerance < 0.0) {
                        validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a positive intra-protein FDR value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a numeric intra-protein FDR value.");
                }
            }
        } else if (mainFrame.getGlobalFdrValueTextField().getText().isEmpty()) {
            validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a global FDR value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getGlobalFdrValueTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a positive global FDR value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add(MULTITHREADING_AND_VALIDATION_PANE + "Please provide a numeric global FDR value.");
            }
        }

        return validationMessages;
    }

    /**
     * Shows a message dialog.
     *
     * @param title the dialog title
     * @param message the dialog message
     * @param messageType the dialog message type
     */
    private void showMessageDialog(final String title, final String message, final int messageType) {
        //add message to JTextArea
        JTextArea textArea = new JTextArea(message);
        //put JTextArea in JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.getViewport().setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(mainFrame.getContentPane(), scrollPane, title, messageType);
    }

    /**
     * Center the run dialog on the frame.
     */
    private void centerRunDialog() {
        Point topLeft = mainFrame.getLocationOnScreen();
        Dimension parentSize = mainFrame.getSize();

        Dimension dialogSize = runDialog.getSize();

        int x;
        int y;

        if (parentSize.width > dialogSize.width) {
            x = ((parentSize.width - dialogSize.width) / 2) + topLeft.x;
        } else {
            x = topLeft.x;
        }

        if (parentSize.height > dialogSize.height) {
            y = ((parentSize.height - dialogSize.height) / 2) + topLeft.y;
        } else {
            y = topLeft.y;
        }

        runDialog.setLocation(x, y);
    }

    private class XilmassSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            LOGGER.info("starting Xilmass run");

            Thread.sleep(10000);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                LOGGER.info("finished Xilmass run");
                JOptionPane.showMessageDialog(runDialog, "The Xilmass run has finished.");
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
                showMessageDialog("Unexpected error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOGGER.info("the Xilmass run was cancelled");
            } finally {

            }
        }
    }

}
