package gui;

import com.compomics.util.experiment.biology.PTMFactory;
import config.ConfigHolder;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
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
    private static final String VALIDATED_TARGETS_PATH = "tdfile";
    private static final String XPSMS_PATH = "allXPSMoutput";
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
    private static final String COMMON_PEPTIDE_MASS_WINDOW = "msms_tol";
    private static final String COMMON_PEPTIDE_MASS_WINDOW_UNIT = "report_in_ppm";
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
     * A mapping for the mass unit (key: ComboBox index; value: property value).
     */
    private final Map<Integer, String> unitGuiToPropertiesMapping = new HashMap();
    /**
     * A mapping for the mass unit (key: property value; value: ComboBox index).
     */
    private final Map<String, Integer> unitPropertiesToGuiMapping = new HashMap();
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
        unitGuiToPropertiesMapping.put(0, "T");
        unitGuiToPropertiesMapping.put(1, "F");
        unitPropertiesToGuiMapping.put("T", 0);
        unitPropertiesToGuiMapping.put("F", 0);
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

        //disable the necessary text fields
//        mainFrame.getFileNameSliceIndexTextField().setEnabled(false);
//        mainFrame.getNumberOfPeaksCutoffTextField().setEnabled(false);
//        mainFrame.getPeakIntensityCutoffTextField().setEnabled(false);
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

        //add action listeners
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

        mainFrame.getValidatedTargetHitsBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getFileChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getValidatedTargetHitsPathTextField().setText(mainFrame.getFileChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getXpsmsBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getFileChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show path in text field
                    mainFrame.getXpsmsPathTextField().setText(mainFrame.getFileChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getCommonPeptideMassToleranceCheckBox().addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean notSelected = !mainFrame.getCommonPeptideMassToleranceCheckBox().isSelected();

                mainFrame.getCommonPeptideMassToleranceWindowTextField().setEnabled(!notSelected);
                mainFrame.getCommonPeptideMassToleranceWindowUnitComboBox().setEnabled(!notSelected);

                mainFrame.getFirstPeptideMassToleranceWindowTextField().setEnabled(notSelected);
                mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().setEnabled(notSelected);
                mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().setEnabled(notSelected);
                mainFrame.getSecondPeptideMassToleranceWindowTextField().setEnabled(notSelected);
                mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().setEnabled(notSelected);
                mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().setEnabled(notSelected);
                mainFrame.getThirdPeptideMassToleranceWindowTextField().setEnabled(notSelected);
                mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setEnabled(notSelected);
                mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setEnabled(notSelected);
                mainFrame.getFourthPeptideMassToleranceWindowTextField().setEnabled(notSelected);
                mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setEnabled(notSelected);
                mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setEnabled(notSelected);
            }
        });

        mainFrame.getFdrCalcalationComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean globalSelected = mainFrame.getFdrCalcalationComboBox().getSelectedIndex() == 0;
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
        mainFrame.getValidatedTargetHitsPathTextField().setText(ConfigHolder.getInstance().getString(VALIDATED_TARGETS_PATH));
        mainFrame.getXpsmsPathTextField().setText(ConfigHolder.getInstance().getString(XPSMS_PATH));
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
        mainFrame.getFirstPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW));
        mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW_BASE));
        String firstToleranceWindowUnit = ConfigHolder.getInstance().getString(FIRST_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(firstToleranceWindowUnit));
        mainFrame.getSecondPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW));
        mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW_BASE));
        String secondToleranceWindowUnit = ConfigHolder.getInstance().getString(SECOND_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(secondToleranceWindowUnit));
        mainFrame.getThirdPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW));
        mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW_BASE));
        String thirdToleranceWindowUnit = ConfigHolder.getInstance().getString(THIRD_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(thirdToleranceWindowUnit));
        mainFrame.getFourthPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW));
        mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().setText(ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW_BASE));
        String fourthToleranceWindowUnit = ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(fourthToleranceWindowUnit));
        mainFrame.getCommonPeptideMassToleranceWindowTextField().setText(ConfigHolder.getInstance().getString(FOURTH_PEPTIDE_MASS_WINDOW));
        String commonToleranceWindowUnit = ConfigHolder.getInstance().getString(COMMON_PEPTIDE_MASS_WINDOW_UNIT);
        mainFrame.getCommonPeptideMassToleranceWindowUnitComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(commonToleranceWindowUnit));
        mainFrame.getMinNumberOfPeaksTextField().setText(ConfigHolder.getInstance().getString(MIN_NUMBER_OF_PEAKS));
        boolean peakMatch = ConfigHolder.getInstance().getBoolean(PEAK_MATCHING);
        if (peakMatch) {
            mainFrame.getPeakMatchingComboBox().setSelectedIndex(0);
        } else {
            mainFrame.getPeakMatchingComboBox().setSelectedIndex(1);
        }
        String ms1ReportingUnit = ConfigHolder.getInstance().getString(MS1_REPORTING);
        mainFrame.getMs1ReportingComboBox().setSelectedIndex(unitPropertiesToGuiMapping.get(ms1ReportingUnit));
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
        boolean improvedFdr = ConfigHolder.getInstance().getBoolean(IMPROVED_FDR);
        if (improvedFdr) {
            mainFrame.getFdrCalcalationComboBox().setSelectedIndex(1);
        } else {
            mainFrame.getFdrCalcalationComboBox().setSelectedIndex(0);
        }
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
        ConfigHolder.getInstance().setProperty(VALIDATED_TARGETS_PATH, mainFrame.getValidatedTargetHitsPathTextField().getText());
        ConfigHolder.getInstance().setProperty(XPSMS_PATH, mainFrame.getXpsmsPathTextField().getText());
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
        if (serine) {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_SERINE, "T");
        } else {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_SERINE, "F");
        }
        boolean threonine = mainFrame.getThreonineCheckBox().isSelected();
        if (threonine) {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_THREONINE, "T");
        } else {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_THREONINE, "F");
        }
        boolean tyrosine = mainFrame.getTyrosineCheckBox().isSelected();
        if (tyrosine) {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_TYROSINE, "T");
        } else {
            ConfigHolder.getInstance().setProperty(SIDE_REACTION_TYROSINE, "F");
        }
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
        if (doMonoLinkSearch) {
            ConfigHolder.getInstance().setProperty(SEARCH_MONOLINK, "T");
        } else {
            ConfigHolder.getInstance().setProperty(SEARCH_MONOLINK, "F");
        }
        ConfigHolder.getInstance().setProperty(MIN_PEPTIDE_LENGTH, mainFrame.getMinimumPeptideLengthTextField().getText());
        ConfigHolder.getInstance().setProperty(MAX_PEPTIDE_LENGTH, mainFrame.getMaximumPeptideLengthTextField().getText());
        boolean intraLinking = mainFrame.getIntraLinkingCheckBox().isSelected();
        if (intraLinking) {
            ConfigHolder.getInstance().setProperty(INTRA_LINKING, "T");
        } else {
            ConfigHolder.getInstance().setProperty(INTRA_LINKING, "F");
        }
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
        boolean useCommonMassToleranceWindow = mainFrame.getCommonPeptideMassToleranceCheckBox().isSelected();
        if (useCommonMassToleranceWindow) {
            ConfigHolder.getInstance().setProperty(COMMON_PEPTIDE_MASS_WINDOW, mainFrame.getCommonPeptideMassToleranceWindowTextField().getText());
            int commonToleranceWindowUnit = mainFrame.getCommonPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
            ConfigHolder.getInstance().setProperty(COMMON_PEPTIDE_MASS_WINDOW_UNIT, commonToleranceWindowUnit);
        } else {
            ConfigHolder.getInstance().setProperty(FIRST_PEPTIDE_MASS_WINDOW, mainFrame.getFirstPeptideMassToleranceWindowTextField().getText());
            ConfigHolder.getInstance().setProperty(FIRST_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getFirstPeptideMassToleranceWindowBaseTextField().getText());
            int firstToleranceWindowUnit = mainFrame.getFirstPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
            ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, firstToleranceWindowUnit);
            ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW, mainFrame.getSecondPeptideMassToleranceWindowTextField().getText());
            ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getSecondPeptideMassToleranceWindowBaseTextField().getText());
            int secondToleranceWindowUnit = mainFrame.getSecondPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
            ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, secondToleranceWindowUnit);
            ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW, mainFrame.getThirdPeptideMassToleranceWindowTextField().getText());
            ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getThirdPeptideMassToleranceWindowBaseTextField().getText());
            int thirdToleranceWindowUnit = mainFrame.getThirdPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
            ConfigHolder.getInstance().setProperty(THIRD_PEPTIDE_MASS_WINDOW_UNIT, thirdToleranceWindowUnit);
            ConfigHolder.getInstance().setProperty(FOURTH_PEPTIDE_MASS_WINDOW, mainFrame.getFourthPeptideMassToleranceWindowTextField().getText());
            ConfigHolder.getInstance().setProperty(FOURTH_PEPTIDE_MASS_WINDOW_BASE, mainFrame.getFourthPeptideMassToleranceWindowBaseTextField().getText());
            int fourthToleranceWindowUnit = mainFrame.getFourthPeptideMassToleranceWindowUnitComboBox().getSelectedIndex();
            ConfigHolder.getInstance().setProperty(SECOND_PEPTIDE_MASS_WINDOW_UNIT, fourthToleranceWindowUnit);
        }
        ConfigHolder.getInstance().setProperty(MIN_NUMBER_OF_PEAKS, mainFrame.getMinNumberOfPeaksTextField());
        int peakMatch = mainFrame.getPeakMatchingComboBox().getSelectedIndex();
        if (peakMatch == 0) {
            ConfigHolder.getInstance().setProperty(PEAK_MATCHING, "T");
        } else {
            ConfigHolder.getInstance().setProperty(PEAK_MATCHING, "F");
        }
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
        if (writePrecolatorInput) {
            ConfigHolder.getInstance().setProperty(WRITE_PERCOLATOR, "T");
        } else {
            ConfigHolder.getInstance().setProperty(WRITE_PERCOLATOR, "F");
        }
        int improvedFdr = mainFrame.getFdrCalcalationComboBox().getSelectedIndex();
        if (improvedFdr == 0) {
            ConfigHolder.getInstance().setProperty(IMPROVED_FDR, "T");
            ConfigHolder.getInstance().setProperty(INTER_PROTEIN_FDR, mainFrame.getInterProteinFdrValueTextField().getText());
            ConfigHolder.getInstance().setProperty(INTRA_PROTEIN_FDR, mainFrame.getIntraProteinFdrValueTextField().getText());
        } else {
            ConfigHolder.getInstance().setProperty(IMPROVED_FDR, "F");
            ConfigHolder.getInstance().setProperty(GLOBAL_FDR, mainFrame.getGlobalFdrValueTextField().getText());
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

//        if (mainFrame.getSpectraDirectoryTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide a spectra input directory.");
//        }
//        if (mainFrame.getComparisonSpectraDirectoryTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide a comparison spectra input directory.");
//        }
//        if (mainFrame.getOutputDirectoryTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide an output directory.");
//        }
//        if (mainFrame.getPrecursorToleranceTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide a precursor tolerance value.");
//        } else {
//            try {
//                Double tolerance = Double.valueOf(mainFrame.getPrecursorToleranceTextField().getText());
//                if (tolerance < 0.0) {
//                    validationMessages.add("Please provide a positive precursor tolerance value.");
//                }
//            } catch (NumberFormatException nfe) {
//                validationMessages.add("Please provide a numeric precursor tolerance value.");
//            }
//        }
//        if (mainFrame.getFragmentToleranceTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide a fragment tolerance value.");
//        } else {
//            try {
//                Double tolerance = Double.valueOf(mainFrame.getFragmentToleranceTextField().getText());
//                if (tolerance < 0.0) {
//                    validationMessages.add("Please provide a positive fragment tolerance value.");
//                }
//            } catch (NumberFormatException nfe) {
//                validationMessages.add("Please provide a numeric fragment tolerance value.");
//            }
//        }
//
//        if (mainFrame.getNeighbourSlicesOnlyCheckBox().isSelected()) {
//            if (mainFrame.getFileNameSliceIndexTextField().getText().isEmpty()) {
//                validationMessages.add("Please provide a file name slice index value.");
//            } else {
//                try {
//                    Integer index = Integer.valueOf(mainFrame.getFileNameSliceIndexTextField().getText());
//                    if (index < 0) {
//                        validationMessages.add("Please provide a positive file name slice index value.");
//                    }
//                } catch (NumberFormatException nfe) {
//                    validationMessages.add("Please provide a numeric file name slice index value.");
//                }
//            }
//        }
//        if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 2) {
//            if (mainFrame.getNumberOfPeaksCutoffTextField().getText().isEmpty()) {
//                validationMessages.add("Please a provide peak cutoff number when choosing the TopN intense peak selection filter.");
//            } else {
//                try {
//                    Integer number = Integer.valueOf(mainFrame.getNumberOfPeaksCutoffTextField().getText());
//                    if (number < 0) {
//                        validationMessages.add("Please provide a positive peak cutoff number value.");
//                    }
//                } catch (NumberFormatException nfe) {
//                    validationMessages.add("Please provide a numeric peak cutoff number value.");
//                }
//            }
//        } else if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 3) {
//            if (mainFrame.getPeakIntensityCutoffTextField().getText().isEmpty()) {
//                validationMessages.add("Please provide peak cutoff percentage when choosing the Discard peaks with less than x% of precursor-intensity filter.");
//            } else {
//                try {
//                    Double percentage = Double.valueOf(mainFrame.getPeakIntensityCutoffTextField().getText());
//                    if (percentage < 0.0) {
//                        validationMessages.add("Please provide a positive peak cutoff percentage value.");
//                    }
//                } catch (NumberFormatException nfe) {
//                    validationMessages.add("Please provide a numeric peak cutoff percentage value.");
//                }
//            }
//        }
//        if (mainFrame.getNumberOfThreadsTextField().getText().isEmpty()) {
//            validationMessages.add("Please provide a number of threads.");
//        } else {
//            try {
//                Integer numberOfThreads = Integer.valueOf(mainFrame.getNumberOfThreadsTextField().getText());
//                if (numberOfThreads < 0) {
//                    validationMessages.add("Please provide a positive number of threads.");
//                }
//            } catch (NumberFormatException nfe) {
//                validationMessages.add("Please provide a numeric number of threads.");
//            }
//        }
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
            LOGGER.info("starting spectrum similarity score pipeline");
//            ScorePipeline.run(true);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                LOGGER.info("finished spectrum similarity score pipeline");
                JOptionPane.showMessageDialog(runDialog, "The score pipeline has finished.");
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
                showMessageDialog("Unexpected error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOGGER.info("the spectrum similarity score pipeline run was cancelled");
            } finally {

            }
        }
    }

}
