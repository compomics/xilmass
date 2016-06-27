package gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * The GUI main frame.
 *
 * @author Niels Hulstaert
 */
public class MainFrame extends javax.swing.JFrame {

    private final JFileChooser fastaDbChooser = new JFileChooser();
    private final JFileChooser searchDbChooser = new JFileChooser();
    private final JFileChooser directoryChooser = new JFileChooser();
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Constructor.
     */
    public MainFrame() {
        initComponents();
    }

    public JFileChooser getFastaDbChooser() {
        return fastaDbChooser;
    }

    public JFileChooser getSearchDbChooser() {
        return searchDbChooser;
    }

    public JFileChooser getDirectoryChooser() {
        return directoryChooser;
    }

    public JFileChooser getFileChooser() {
        return fileChooser;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JCheckBox getCommonPeptideMassToleranceCheckBox() {
        return commonPeptideMassToleranceCheckBox;
    }

    public JTextField getCommonPeptideMassToleranceWindowTextField() {
        return commonPeptideMassToleranceWindowTextField;
    }

    public JComboBox<String> getCommonPeptideMassToleranceWindowUnitComboBox() {
        return commonPeptideMassToleranceWindowUnitComboBox;
    }

    public JButton getContaminantsFastaDbBrowseButton() {
        return contaminantsFastaDbBrowseButton;
    }

    public JTextField getContaminantsFastaDbPathTextField() {
        return contaminantsFastaDbPathTextField;
    }

    public JComboBox<String> getCrossLinkerComboBox() {
        return crossLinkerComboBox;
    }

    public JComboBox<String> getCrosslinkingTypeComboBox() {
        return crosslinkingTypeComboBox;
    }

    public JTextField getDeconvulatePrecisionTextField() {
        return deconvulatePrecisionTextField;
    }

    public JTextField getDeisotopePrecisionTextField() {
        return deisotopePrecisionTextField;
    }

    public JComboBox<String> getEnzymeComboBox() {
        return enzymeComboBox;
    }

    public JButton getFastaDbBrowseButton() {
        return fastaDbBrowseButton;
    }

    public JTextField getFastaDbPathTextField() {
        return fastaDbPathTextField;
    }

    public JComboBox<String> getFdrCalcalationComboBox() {
        return fdrCalcalationComboBox;
    }

    public JTextField getFirstPeptideMassToleranceWindowBaseTextField() {
        return firstPeptideMassToleranceWindowBaseTextField;
    }

    public JTextField getFirstPeptideMassToleranceWindowTextField() {
        return firstPeptideMassToleranceWindowTextField;
    }

    public JComboBox<String> getFirstPeptideMassToleranceWindowUnitComboBox() {
        return firstPeptideMassToleranceWindowUnitComboBox;
    }

    public DualList getFixedModificationsDualList() {
        return fixedModificationsDualList;
    }

    public JTextField getFourthPeptideMassToleranceWindowBaseTextField() {
        return fourthPeptideMassToleranceWindowBaseTextField;
    }

    public JTextField getFourthPeptideMassToleranceWindowTextField() {
        return fourthPeptideMassToleranceWindowTextField;
    }

    public JComboBox<String> getFourthPeptideMassToleranceWindowUnitComboBox() {
        return fourthPeptideMassToleranceWindowUnitComboBox;
    }

    public JComboBox<String> getFragmentationModeComboBox() {
        return fragmentationModeComboBox;
    }

    public JTextField getGlobalFdrValueTextField() {
        return globalFdrValueTextField;
    }

    public JTextField getInterProteinFdrValueTextField() {
        return interProteinFdrValueTextField;
    }

    public JCheckBox getIntraLinkingCheckBox() {
        return intraLinkingCheckBox;
    }

    public JTextField getIntraProteinFdrValueTextField() {
        return intraProteinFdrValueTextField;
    }

    public JComboBox<String> getLabelingComboBox() {
        return labelingComboBox;
    }

    public JTextField getLowerPrecursorMassBoundTextField() {
        return lowerPrecursorMassBoundTextField;
    }

    public JTextField getMaxModPeptideTextField() {
        return maxModPeptideTextField;
    }

    public JTextField getMaximumNumberOfPeaksTextField() {
        return maximumNumberOfPeaksTextField;
    }

    public JTextField getMaximumPeptideLengthTextField() {
        return maximumPeptideLengthTextField;
    }

    public JTextField getMaximumPeptideMassTextField() {
        return maximumPeptideMassTextField;
    }

    public JTextField getMinNumberOfPeaksTextField() {
        return minNumberOfPeaksTextField;
    }

    public JTextField getMinimumNumberOfPeaksTextField() {
        return minimumNumberOfPeaksTextField;
    }

    public JTextField getMinimumPeptideLengthTextField() {
        return minimumPeptideLengthTextField;
    }

    public JTextField getMinimumPeptideMassTextField() {
        return minimumPeptideMassTextField;
    }

    public JTextField getMissedCleavagesTextField() {
        return missedCleavagesTextField;
    }

    public JTextField getSearchDbPathTextField() {
        return searchDbPathTextField1;
    }

    public JCheckBox getMonoLinkingCheckBox() {
        return monoLinkingCheckBox;
    }

    public JComboBox<String> getNeutralLossesComboBox() {
        return neutralLossesComboBox;
    }

    public JTextField getNumberOfThreadsTextField() {
        return numberOfThreadsTextField;
    }

    public JButton getOutputDirectoryBrowseButton() {
        return outputDirectoryBrowseButton;
    }

    public JTextField getOutputDirectoryPathTextField() {
        return outputDirectoryPathTextField;
    }

    public JComboBox<String> getPeakMatchingComboBox() {
        return peakMatchingComboBox;
    }

    public JSpinner getPeptideToleranceSpinner() {
        return peptideToleranceSpinner;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public JButton getSearchDbBrowseButton() {
        return searchDbBrowseButton;
    }

    public JTextField getSecondPeptideMassToleranceWindowBaseTextField() {
        return secondPeptideMassToleranceWindowBaseTextField;
    }

    public JTextField getSecondPeptideMassToleranceWindowTextField() {
        return secondPeptideMassToleranceWindowTextField;
    }

    public JComboBox<String> getSecondPeptideMassToleranceWindowUnitComboBox() {
        return secondPeptideMassToleranceWindowUnitComboBox;
    }

    public JCheckBox getSerineCheckBox() {
        return serineCheckBox;
    }

    public JTextField getSpectrumMassWindowValueTextField() {
        return spectrumMassWindowValueTextField;
    }

    public JTextField getThirdPeptideMassToleranceWindowBaseTextField() {
        return thirdPeptideMassToleranceWindowBaseTextField;
    }

    public JTextField getThirdPeptideMassToleranceWindowTextField() {
        return thirdPeptideMassToleranceWindowTextField;
    }

    public JComboBox<String> getThirdPeptideMassToleranceWindowUnitComboBox() {
        return thirdPeptideMassToleranceWindowUnitComboBox;
    }

    public JCheckBox getThreonineCheckBox() {
        return threonineCheckBox;
    }

    public JCheckBox getTyrosineCheckBox() {
        return tyrosineCheckBox;
    }

    public JButton getValidatedTargetHitsBrowseButton() {
        return validatedTargetHitsBrowseButton;
    }

    public JTextField getValidatedTargetHitsPathTextField() {
        return validatedTargetHitsPathTextField;
    }

    public DualList getVariableModificationsDualList() {
        return variableModificationsDualList;
    }

    public JComboBox<String> getMs1ReportingComboBox() {
        return ms1ReportingComboBox;
    }

    public JCheckBox getWritePercolatorInputFilesCheckBox() {
        return writePercolatorInputFilesCheckBox;
    }

    public JButton getXpsmsBrowseButton() {
        return xpsmsBrowseButton;
    }

    public JTextField getXpsmsPathTextField() {
        return xpsmsPathTextField;
    }

    public JButton getMgfDirectoryBrowseButton() {
        return searchDbBrowseButton1;
    }

    public JTextField getMgfDirectoryPathTextField() {
        return searchDbPathTextField2;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jMenu1 = new javax.swing.JMenu();
        mainPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        inputOutputPanel = new javax.swing.JPanel();
        outputDirectoryPathTextField = new javax.swing.JTextField();
        fastaDbPathLabel = new javax.swing.JLabel();
        contaminantsFastaDbPathTextField = new javax.swing.JTextField();
        contaminantsFastaDbBrowseButton = new javax.swing.JButton();
        contaminantsFastaDbFilePathLabel = new javax.swing.JLabel();
        fastaDbPathTextField = new javax.swing.JTextField();
        fastaDbBrowseButton = new javax.swing.JButton();
        searchDbPathLabel = new javax.swing.JLabel();
        searchDbBrowseButton = new javax.swing.JButton();
        outputDirectoryPathLabel = new javax.swing.JLabel();
        searchDbPathTextField1 = new javax.swing.JTextField();
        outputDirectoryBrowseButton = new javax.swing.JButton();
        validatedTargetHitsPathLabel = new javax.swing.JLabel();
        validatedTargetHitsPathTextField = new javax.swing.JTextField();
        validatedTargetHitsBrowseButton = new javax.swing.JButton();
        xpsmsPathLabel = new javax.swing.JLabel();
        xpsmsPathTextField = new javax.swing.JTextField();
        xpsmsBrowseButton = new javax.swing.JButton();
        searchDbPathLabel1 = new javax.swing.JLabel();
        searchDbPathTextField2 = new javax.swing.JTextField();
        searchDbBrowseButton1 = new javax.swing.JButton();
        crossLinkingPanel = new javax.swing.JPanel();
        crossLinkerLabel = new javax.swing.JLabel();
        crossLinkerComboBox = new javax.swing.JComboBox<>();
        crossLinkerLabel1 = new javax.swing.JLabel();
        labelingComboBox = new javax.swing.JComboBox<>();
        sideReactionsLabel = new javax.swing.JLabel();
        serineCheckBox = new javax.swing.JCheckBox();
        threonineCheckBox = new javax.swing.JCheckBox();
        tyrosineCheckBox = new javax.swing.JCheckBox();
        crossLinkingTypeLabel = new javax.swing.JLabel();
        crosslinkingTypeComboBox = new javax.swing.JComboBox<>();
        monolinkingLabel = new javax.swing.JLabel();
        monoLinkingCheckBox = new javax.swing.JCheckBox();
        peptideLengthsLabel = new javax.swing.JLabel();
        minimumPeptideLengthLabel = new javax.swing.JLabel();
        maximumPeptideLengthLabel = new javax.swing.JLabel();
        minimumPeptideLengthTextField = new javax.swing.JTextField();
        maximumPeptideLengthTextField = new javax.swing.JTextField();
        intralinkingLabel = new javax.swing.JLabel();
        intraLinkingCheckBox = new javax.swing.JCheckBox();
        inSilicoDigestionPanel = new javax.swing.JPanel();
        enzymeComboBox = new javax.swing.JComboBox<>();
        missedCleavagesLabel = new javax.swing.JLabel();
        missedCleavagesTextField = new javax.swing.JTextField();
        minimumPeptideMassLabel = new javax.swing.JLabel();
        minimumPeptideMassTextField = new javax.swing.JTextField();
        maximumPeptideMassLabel = new javax.swing.JLabel();
        maximumPeptideMassTextField = new javax.swing.JTextField();
        enzymeLabel = new javax.swing.JLabel();
        modificationsPanel = new javax.swing.JPanel();
        fixedModificationsPanel = new javax.swing.JPanel();
        fixedModificationsDualList = new gui.DualList();
        fixedModificationsLabel = new javax.swing.JLabel();
        variableModificationsPanel = new javax.swing.JPanel();
        variableModificationsDualList = new gui.DualList();
        variableModificationsLabel = new javax.swing.JLabel();
        otherModSettingsPanel = new javax.swing.JPanel();
        maxModPeptideLabel = new javax.swing.JLabel();
        maxModPeptideTextField = new javax.swing.JTextField();
        scoringPanel = new javax.swing.JPanel();
        neutralLossesLabel = new javax.swing.JLabel();
        neutralLossesComboBox = new javax.swing.JComboBox<>();
        fragmentationLabel = new javax.swing.JLabel();
        fragmentationModeComboBox = new javax.swing.JComboBox<>();
        peptideMassToleranceWindowsPanel = new javax.swing.JPanel();
        firstPeptideMassToleranceWindowLabel = new javax.swing.JLabel();
        firstPeptideMassToleranceWindowBaseTextField = new javax.swing.JTextField();
        firstPeptideMassToleranceWindowUnitLabel = new javax.swing.JLabel();
        firstPeptideMassToleranceWindowUnitComboBox = new javax.swing.JComboBox<>();
        secondPeptideMassToleranceWindowLabel = new javax.swing.JLabel();
        secondPeptideMassToleranceWindowBaseTextField = new javax.swing.JTextField();
        secondPeptideMassToleranceWindowUnitLabel = new javax.swing.JLabel();
        secondPeptideMassToleranceWindowUnitComboBox = new javax.swing.JComboBox<>();
        thirdPeptideMassToleranceWindowLabel = new javax.swing.JLabel();
        thirdPeptideMassToleranceWindowBaseTextField = new javax.swing.JTextField();
        thirdPeptideMassToleranceWindowUnitLabel = new javax.swing.JLabel();
        thirdPeptideMassToleranceWindowUnitComboBox = new javax.swing.JComboBox<>();
        fourthPeptideMassToleranceWindowLabel = new javax.swing.JLabel();
        fourthPeptideMassToleranceWindowBaseTextField = new javax.swing.JTextField();
        fourthPeptideMassToleranceWindowUnitLabel = new javax.swing.JLabel();
        fourthPeptideMassToleranceWindowUnitComboBox = new javax.swing.JComboBox<>();
        commonPeptideMassToleranceCheckBox = new javax.swing.JCheckBox();
        peptideToleranceSpinner = new javax.swing.JSpinner();
        peptideToleranceLabel = new javax.swing.JLabel();
        commonPeptideMassToleranceWindowLabel = new javax.swing.JLabel();
        commonPeptideMassToleranceWindowTextField = new javax.swing.JTextField();
        commonPeptideMassToleranceWindowUnitLabel = new javax.swing.JLabel();
        commonPeptideMassToleranceWindowUnitComboBox = new javax.swing.JComboBox<>();
        firstPeptideMassToleranceWindowBaseLabel = new javax.swing.JLabel();
        secondPeptideMassToleranceWindowBaseLabel = new javax.swing.JLabel();
        thirdPeptideMassToleranceWindowBaseLabel = new javax.swing.JLabel();
        fourthPeptideMassToleranceWindowBaseLabel = new javax.swing.JLabel();
        secondPeptideMassToleranceWindowTextField = new javax.swing.JTextField();
        firstPeptideMassToleranceWindowTextField = new javax.swing.JTextField();
        thirdPeptideMassToleranceWindowTextField = new javax.swing.JTextField();
        fourthPeptideMassToleranceWindowTextField = new javax.swing.JTextField();
        firstPeptideMassToleranceWindowValueLabel = new javax.swing.JLabel();
        secondPeptideMassToleranceWindowValueLabel = new javax.swing.JLabel();
        thirdPeptideMassToleranceWindowValueLabel = new javax.swing.JLabel();
        fourthPeptideMassToleranceWindowValueLabel = new javax.swing.JLabel();
        minNumberOfPeaksLabel = new javax.swing.JLabel();
        minNumberOfPeaksTextField = new javax.swing.JTextField();
        peakMatchingLabel = new javax.swing.JLabel();
        peakMatchingComboBox = new javax.swing.JComboBox<>();
        ms1ReportingLabel = new javax.swing.JLabel();
        ms1ReportingComboBox = new javax.swing.JComboBox<>();
        spectrumPreprocessingPanel = new javax.swing.JPanel();
        spectrumMassWindowValueLabel = new javax.swing.JLabel();
        spectrumMassWindowValueTextField = new javax.swing.JTextField();
        minimumNumberOfPeaksLabel = new javax.swing.JLabel();
        minimumNumberOfPeaksTextField = new javax.swing.JTextField();
        maximumNumberOfPeaksLabel = new javax.swing.JLabel();
        maximumNumberOfPeaksTextField = new javax.swing.JTextField();
        lowerPrecursorMassBoundLabel = new javax.swing.JLabel();
        lowerPrecursorMassBoundTextField = new javax.swing.JTextField();
        deisotopeLabel = new javax.swing.JLabel();
        deisotopePrecisionTextField = new javax.swing.JTextField();
        deconvulatePrecisionLabel = new javax.swing.JLabel();
        deconvulatePrecisionTextField = new javax.swing.JTextField();
        multiThreadingAndValidationPanel = new javax.swing.JPanel();
        numberOfThreadsLabel = new javax.swing.JLabel();
        numberOfThreadsTextField = new javax.swing.JTextField();
        fdrCalculationLabel = new javax.swing.JLabel();
        fdrCalcalationComboBox = new javax.swing.JComboBox<>();
        globalFdrValueLabel = new javax.swing.JLabel();
        globalFdrValueTextField = new javax.swing.JTextField();
        interProteinFdrValueLabel = new javax.swing.JLabel();
        interProteinFdrValueTextField = new javax.swing.JTextField();
        intraProteinFdrValueLabel = new javax.swing.JLabel();
        intraProteinFdrValueTextField = new javax.swing.JTextField();
        writePercolatorInputFilesCheckBox = new javax.swing.JCheckBox();
        bottomPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(10, 10));

        inputOutputPanel.setOpaque(false);

        fastaDbPathLabel.setText("Select the FASTA database (target-decoy, decoys must plit by \"_\")*:");
        fastaDbPathLabel.setToolTipText("give a folder that contains spectra");

        contaminantsFastaDbBrowseButton.setText("browse...");

        contaminantsFastaDbFilePathLabel.setText("Select an optional contaminants FASTA database:");
        contaminantsFastaDbFilePathLabel.setToolTipText("give a folder that contains spectra");

        fastaDbBrowseButton.setText("browse...");

        searchDbPathLabel.setText("Select the cross-linked and mono-linked peptides search database*:");
        searchDbPathLabel.setToolTipText("give a folder that contains spectra");

        searchDbBrowseButton.setText("browse...");

        outputDirectoryPathLabel.setText("Select the output files directory*:");
        outputDirectoryPathLabel.setToolTipText("give a folder that contains spectra");

        outputDirectoryBrowseButton.setText("browse...");

        validatedTargetHitsPathLabel.setText("Select the validated list of target hits*:");
        validatedTargetHitsPathLabel.setToolTipText("give a folder that contains spectra");

        validatedTargetHitsBrowseButton.setText("browse...");

        xpsmsPathLabel.setText("Select the merged XPSMs*:");
        xpsmsPathLabel.setToolTipText("give a folder that contains spectra");

        xpsmsBrowseButton.setText("browse...");

        searchDbPathLabel1.setText("Select the cross-linked and mono-linked peptides search database*:");
        searchDbPathLabel1.setToolTipText("give a folder that contains spectra");

        searchDbBrowseButton1.setText("browse...");

        javax.swing.GroupLayout inputOutputPanelLayout = new javax.swing.GroupLayout(inputOutputPanel);
        inputOutputPanel.setLayout(inputOutputPanelLayout);
        inputOutputPanelLayout.setHorizontalGroup(
            inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputOutputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(fastaDbPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fastaDbBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(contaminantsFastaDbPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contaminantsFastaDbBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(searchDbPathTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchDbBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(searchDbPathTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchDbBrowseButton1))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(outputDirectoryPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputDirectoryBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(validatedTargetHitsPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(validatedTargetHitsBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addComponent(xpsmsPathTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xpsmsBrowseButton))
                    .addGroup(inputOutputPanelLayout.createSequentialGroup()
                        .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fastaDbPathLabel)
                            .addComponent(contaminantsFastaDbFilePathLabel)
                            .addComponent(searchDbPathLabel)
                            .addComponent(searchDbPathLabel1)
                            .addComponent(outputDirectoryPathLabel)
                            .addComponent(validatedTargetHitsPathLabel)
                            .addComponent(xpsmsPathLabel))
                        .addGap(0, 513, Short.MAX_VALUE)))
                .addContainerGap())
        );
        inputOutputPanelLayout.setVerticalGroup(
            inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputOutputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fastaDbPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fastaDbPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fastaDbBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contaminantsFastaDbFilePathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contaminantsFastaDbPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contaminantsFastaDbBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchDbPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchDbBrowseButton)
                    .addComponent(searchDbPathTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchDbPathLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchDbBrowseButton1)
                    .addComponent(searchDbPathTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outputDirectoryPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputDirectoryPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputDirectoryBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(validatedTargetHitsPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(validatedTargetHitsPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(validatedTargetHitsBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xpsmsPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xpsmsPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xpsmsBrowseButton))
                .addContainerGap(91, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Input/Output", inputOutputPanel);

        crossLinkingPanel.setOpaque(false);

        crossLinkerLabel.setText("Select the cross-linker*:");

        crossLinkerComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "DSS", "BS3", "EDC", "GA" }));

        crossLinkerLabel1.setText("Select the labeling*:");

        labelingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "light", "heavy", "both" }));

        sideReactionsLabel.setText("Consider side reactions (only for N-hydroxysuccinimide cross-linkers, such as DSS and BS3) for:");

        serineCheckBox.setText("serine");

        threonineCheckBox.setText("threonine");

        tyrosineCheckBox.setText("tyrosine");

        crossLinkingTypeLabel.setText("Select the labeling*:");

        crosslinkingTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "intra (within same protein)", "inter (between different proteins)", "both" }));

        monolinkingLabel.setText("Mono-linking*:");

        monoLinkingCheckBox.setText("search for monolinked peptides");

        peptideLengthsLabel.setText("Cross-linked peptide lengths:");

        minimumPeptideLengthLabel.setText("Minimum peptide length*:");

        maximumPeptideLengthLabel.setText("Maximum peptide length*:");

        minimumPeptideLengthTextField.setMinimumSize(new java.awt.Dimension(62, 27));
        minimumPeptideLengthTextField.setPreferredSize(new java.awt.Dimension(62, 27));

        maximumPeptideLengthTextField.setMinimumSize(new java.awt.Dimension(62, 27));
        maximumPeptideLengthTextField.setPreferredSize(new java.awt.Dimension(62, 27));

        intralinkingLabel.setText("Intra-linking*:");

        intraLinkingCheckBox.setText("allow linkage of a peptide to itself");

        javax.swing.GroupLayout crossLinkingPanelLayout = new javax.swing.GroupLayout(crossLinkingPanel);
        crossLinkingPanel.setLayout(crossLinkingPanelLayout);
        crossLinkingPanelLayout.setHorizontalGroup(
            crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(crossLinkingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(crossLinkerLabel)
                    .addComponent(crossLinkerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(crossLinkerLabel1)
                    .addComponent(labelingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(crossLinkingTypeLabel)
                    .addComponent(crosslinkingTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monolinkingLabel)
                    .addComponent(monoLinkingCheckBox)
                    .addComponent(sideReactionsLabel)
                    .addGroup(crossLinkingPanelLayout.createSequentialGroup()
                        .addComponent(serineCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(threonineCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tyrosineCheckBox))
                    .addComponent(intralinkingLabel)
                    .addComponent(peptideLengthsLabel)
                    .addGroup(crossLinkingPanelLayout.createSequentialGroup()
                        .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(minimumPeptideLengthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maximumPeptideLengthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minimumPeptideLengthTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maximumPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(intraLinkingCheckBox))
                .addContainerGap(355, Short.MAX_VALUE))
        );
        crossLinkingPanelLayout.setVerticalGroup(
            crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(crossLinkingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(crossLinkerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crossLinkerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(crossLinkerLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sideReactionsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serineCheckBox)
                    .addComponent(threonineCheckBox)
                    .addComponent(tyrosineCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(crossLinkingTypeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crosslinkingTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(monolinkingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monoLinkingCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(peptideLengthsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minimumPeptideLengthLabel)
                    .addComponent(minimumPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(crossLinkingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maximumPeptideLengthLabel)
                    .addComponent(maximumPeptideLengthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(intralinkingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(intraLinkingCheckBox)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Cross-linking", crossLinkingPanel);

        inSilicoDigestionPanel.setOpaque(false);

        enzymeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Trypsin", "Trypsin_Mod", "Lys-C", "Lys-N", "Lys-C/P", "Arg-C", "Asp-N", "V8-E", "V8-DE", "Chymotrypsin", "Trypsin/P", "TrypChymo", "None", "NoCleavage", "dualArgC_Cathep", "dualArgC_Cathep/P", "Arg-C/P", " ", " " }));

        missedCleavagesLabel.setText("Allowed number of miscleavages*:");

        missedCleavagesTextField.setMinimumSize(new java.awt.Dimension(62, 27));
        missedCleavagesTextField.setPreferredSize(new java.awt.Dimension(62, 27));

        minimumPeptideMassLabel.setText("Minimum peptide mass considered for cross-linked combinations*:");

        minimumPeptideMassTextField.setMinimumSize(new java.awt.Dimension(62, 27));
        minimumPeptideMassTextField.setPreferredSize(new java.awt.Dimension(62, 27));

        maximumPeptideMassLabel.setText("Maximum peptide mass considered for cross-linked combinations*:");

        maximumPeptideMassTextField.setMinimumSize(new java.awt.Dimension(62, 27));
        maximumPeptideMassTextField.setPreferredSize(new java.awt.Dimension(62, 27));

        enzymeLabel.setText("Select the enyme used for the in-silico digestion*:");

        javax.swing.GroupLayout inSilicoDigestionPanelLayout = new javax.swing.GroupLayout(inSilicoDigestionPanel);
        inSilicoDigestionPanel.setLayout(inSilicoDigestionPanelLayout);
        inSilicoDigestionPanelLayout.setHorizontalGroup(
            inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inSilicoDigestionPanelLayout.createSequentialGroup()
                .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enzymeLabel)
                    .addComponent(enzymeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(inSilicoDigestionPanelLayout.createSequentialGroup()
                        .addComponent(missedCleavagesLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(missedCleavagesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(inSilicoDigestionPanelLayout.createSequentialGroup()
                        .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(minimumPeptideMassLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maximumPeptideMassLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(minimumPeptideMassTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maximumPeptideMassTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(470, Short.MAX_VALUE))
        );
        inSilicoDigestionPanelLayout.setVerticalGroup(
            inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inSilicoDigestionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enzymeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enzymeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(missedCleavagesLabel)
                    .addComponent(missedCleavagesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minimumPeptideMassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minimumPeptideMassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(inSilicoDigestionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maximumPeptideMassLabel)
                    .addComponent(maximumPeptideMassTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(336, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("In-silico digestion", inSilicoDigestionPanel);

        modificationsPanel.setOpaque(false);
        modificationsPanel.setLayout(new java.awt.GridBagLayout());

        fixedModificationsPanel.setOpaque(false);

        fixedModificationsLabel.setText("Select the fixed modifications*:");

        javax.swing.GroupLayout fixedModificationsPanelLayout = new javax.swing.GroupLayout(fixedModificationsPanel);
        fixedModificationsPanel.setLayout(fixedModificationsPanelLayout);
        fixedModificationsPanelLayout.setHorizontalGroup(
            fixedModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fixedModificationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fixedModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fixedModificationsDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addGroup(fixedModificationsPanelLayout.createSequentialGroup()
                        .addComponent(fixedModificationsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        fixedModificationsPanelLayout.setVerticalGroup(
            fixedModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fixedModificationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fixedModificationsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixedModificationsDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        modificationsPanel.add(fixedModificationsPanel, gridBagConstraints);

        variableModificationsPanel.setOpaque(false);

        variableModificationsLabel.setText("Select the variable modifications*:");

        javax.swing.GroupLayout variableModificationsPanelLayout = new javax.swing.GroupLayout(variableModificationsPanel);
        variableModificationsPanel.setLayout(variableModificationsPanelLayout);
        variableModificationsPanelLayout.setHorizontalGroup(
            variableModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(variableModificationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(variableModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(variableModificationsDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addGroup(variableModificationsPanelLayout.createSequentialGroup()
                        .addComponent(variableModificationsLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        variableModificationsPanelLayout.setVerticalGroup(
            variableModificationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, variableModificationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(variableModificationsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(variableModificationsDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        modificationsPanel.add(variableModificationsPanel, gridBagConstraints);

        otherModSettingsPanel.setOpaque(false);

        maxModPeptideLabel.setText("Set the maximum number of modifications per peptide*:");

        javax.swing.GroupLayout otherModSettingsPanelLayout = new javax.swing.GroupLayout(otherModSettingsPanel);
        otherModSettingsPanel.setLayout(otherModSettingsPanelLayout);
        otherModSettingsPanelLayout.setHorizontalGroup(
            otherModSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherModSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxModPeptideLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxModPeptideTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(544, Short.MAX_VALUE))
        );
        otherModSettingsPanelLayout.setVerticalGroup(
            otherModSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherModSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(otherModSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxModPeptideLabel)
                    .addComponent(maxModPeptideTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        modificationsPanel.add(otherModSettingsPanel, gridBagConstraints);

        mainTabbedPane.addTab("Modifications", modificationsPanel);

        scoringPanel.setOpaque(false);

        neutralLossesLabel.setText("Select the neutral losses to consider*:");

        neutralLossesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "no neutral losses are taken into account", "water losses for D/E/S/T and ammonia losses for K/N/Q/R (only singly charged)", "all water losses are considered (including doubly charged)" }));

        fragmentationLabel.setText("Select the fragmentation mode*:");

        fragmentationModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HCD (b and y ions also a2)", "CID (b and y ions)", "ETD (c and z ions)" }));

        peptideMassToleranceWindowsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Peptide mass tolerance windows"));
        peptideMassToleranceWindowsPanel.setOpaque(false);

        firstPeptideMassToleranceWindowLabel.setText("First peptide mass tolerance window:");

        firstPeptideMassToleranceWindowUnitLabel.setText("Unit*:");

        firstPeptideMassToleranceWindowUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "Da" }));

        secondPeptideMassToleranceWindowLabel.setText("Second mass peptide tolerance window:");

        secondPeptideMassToleranceWindowUnitLabel.setText("Unit*:");

        secondPeptideMassToleranceWindowUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "Da" }));

        thirdPeptideMassToleranceWindowLabel.setText("Third peptide mass tolerance window:");

        thirdPeptideMassToleranceWindowUnitLabel.setText("Unit*:");

        thirdPeptideMassToleranceWindowUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "Da" }));

        fourthPeptideMassToleranceWindowLabel.setText("Fourth peptide mass tolerance window:");

        fourthPeptideMassToleranceWindowUnitLabel.setText("Unit*:");

        fourthPeptideMassToleranceWindowUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "Da" }));

        commonPeptideMassToleranceCheckBox.setText("Use one peptide mass tolerance size for all windows");

        peptideToleranceSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));

        peptideToleranceLabel.setText("Select the number of peptide tolerance windows*:");

        commonPeptideMassToleranceWindowLabel.setText("Value*:");

        commonPeptideMassToleranceWindowUnitLabel.setText("Unit*:");

        commonPeptideMassToleranceWindowUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "Da" }));

        firstPeptideMassToleranceWindowBaseLabel.setText("Base*:");

        secondPeptideMassToleranceWindowBaseLabel.setText("Base*:");

        thirdPeptideMassToleranceWindowBaseLabel.setText("Base*:");

        fourthPeptideMassToleranceWindowBaseLabel.setText("Base*:");

        firstPeptideMassToleranceWindowValueLabel.setText("Value*:");

        secondPeptideMassToleranceWindowValueLabel.setText("Value*:");

        thirdPeptideMassToleranceWindowValueLabel.setText("Value*:");

        fourthPeptideMassToleranceWindowValueLabel.setText("Value*:");

        javax.swing.GroupLayout peptideMassToleranceWindowsPanelLayout = new javax.swing.GroupLayout(peptideMassToleranceWindowsPanel);
        peptideMassToleranceWindowsPanel.setLayout(peptideMassToleranceWindowsPanelLayout);
        peptideMassToleranceWindowsPanelLayout.setHorizontalGroup(
            peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                        .addComponent(peptideToleranceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(peptideToleranceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                        .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(fourthPeptideMassToleranceWindowLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(firstPeptideMassToleranceWindowLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(secondPeptideMassToleranceWindowLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                                .addComponent(thirdPeptideMassToleranceWindowLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(commonPeptideMassToleranceCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                        .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                                .addComponent(commonPeptideMassToleranceWindowLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(commonPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(commonPeptideMassToleranceWindowUnitLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                                .addComponent(thirdPeptideMassToleranceWindowValueLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thirdPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(thirdPeptideMassToleranceWindowBaseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(thirdPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(thirdPeptideMassToleranceWindowUnitLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                                .addComponent(fourthPeptideMassToleranceWindowValueLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fourthPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fourthPeptideMassToleranceWindowBaseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fourthPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fourthPeptideMassToleranceWindowUnitLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                                .addComponent(secondPeptideMassToleranceWindowValueLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(secondPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(secondPeptideMassToleranceWindowBaseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(secondPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(secondPeptideMassToleranceWindowUnitLabel))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                                .addComponent(firstPeptideMassToleranceWindowValueLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(firstPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(firstPeptideMassToleranceWindowBaseLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(firstPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(firstPeptideMassToleranceWindowUnitLabel)))
                        .addGap(6, 6, 6)))
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(firstPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirdPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fourthPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commonPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        peptideMassToleranceWindowsPanelLayout.setVerticalGroup(
            peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideMassToleranceWindowsPanelLayout.createSequentialGroup()
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peptideToleranceLabel)
                    .addComponent(peptideToleranceSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(commonPeptideMassToleranceWindowUnitLabel)
                    .addComponent(commonPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commonPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commonPeptideMassToleranceWindowLabel)
                    .addComponent(commonPeptideMassToleranceCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstPeptideMassToleranceWindowLabel)
                    .addComponent(firstPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstPeptideMassToleranceWindowUnitLabel)
                    .addComponent(firstPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstPeptideMassToleranceWindowBaseLabel)
                    .addComponent(firstPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstPeptideMassToleranceWindowValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secondPeptideMassToleranceWindowLabel)
                    .addComponent(secondPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondPeptideMassToleranceWindowUnitLabel)
                    .addComponent(secondPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondPeptideMassToleranceWindowBaseLabel)
                    .addComponent(secondPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondPeptideMassToleranceWindowValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thirdPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirdPeptideMassToleranceWindowUnitLabel)
                    .addComponent(thirdPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirdPeptideMassToleranceWindowLabel)
                    .addComponent(thirdPeptideMassToleranceWindowBaseLabel)
                    .addComponent(thirdPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(thirdPeptideMassToleranceWindowValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fourthPeptideMassToleranceWindowUnitComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(peptideMassToleranceWindowsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fourthPeptideMassToleranceWindowLabel)
                        .addComponent(fourthPeptideMassToleranceWindowBaseTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fourthPeptideMassToleranceWindowUnitLabel)
                        .addComponent(fourthPeptideMassToleranceWindowBaseLabel)
                        .addComponent(fourthPeptideMassToleranceWindowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(fourthPeptideMassToleranceWindowValueLabel)))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        minNumberOfPeaksLabel.setText("Minimum number matched peaks for cross-linked peptides*:");

        peakMatchingLabel.setText("Peak matching*:");

        peakMatchingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "find all matched theoretical peaks within a tolerance", "find only the closest theoretical peak within a tolerance" }));

        ms1ReportingLabel.setText("MS1 mass differences reporting unit*:");

        ms1ReportingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PPM", "DA" }));
        ms1ReportingComboBox.setMaximumSize(new java.awt.Dimension(56, 25));

        javax.swing.GroupLayout scoringPanelLayout = new javax.swing.GroupLayout(scoringPanel);
        scoringPanel.setLayout(scoringPanelLayout);
        scoringPanelLayout.setHorizontalGroup(
            scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scoringPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scoringPanelLayout.createSequentialGroup()
                        .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(neutralLossesLabel)
                            .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(fragmentationModeComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fragmentationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(neutralLossesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(peptideMassToleranceWindowsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(scoringPanelLayout.createSequentialGroup()
                        .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(scoringPanelLayout.createSequentialGroup()
                                .addComponent(minNumberOfPeaksLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(scoringPanelLayout.createSequentialGroup()
                                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(peakMatchingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(ms1ReportingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(145, 145, 145)
                                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(peakMatchingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ms1ReportingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 186, Short.MAX_VALUE))))
        );
        scoringPanelLayout.setVerticalGroup(
            scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scoringPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(neutralLossesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(neutralLossesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fragmentationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fragmentationModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(peptideMassToleranceWindowsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minNumberOfPeaksLabel)
                    .addComponent(minNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peakMatchingLabel)
                    .addComponent(peakMatchingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(scoringPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ms1ReportingLabel)
                    .addComponent(ms1ReportingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Scoring", scoringPanel);

        spectrumPreprocessingPanel.setOpaque(false);

        spectrumMassWindowValueLabel.setText("Spectrum scoring mass window value*:");

        minimumNumberOfPeaksLabel.setText("Minumum number of filtered peaks per window*:");

        maximumNumberOfPeaksLabel.setText("Maximum number of filtered peaks per window*:");

        lowerPrecursorMassBoundLabel.setText("Lower precursor mass bound for selecting the C13 peak over the C12 peak (in Da)*:");

        deisotopeLabel.setText("Deisotope precision (in Da)*:");

        deisotopePrecisionTextField.setToolTipText("Allowed tolerance between the C12 peak and the C12 with one C13 fragment peak .");

        deconvulatePrecisionLabel.setText("Deconvolute precision (in Da)*:");

        deconvulatePrecisionTextField.setToolTipText("Precision to select if a singly charged and its deconvoluted peak exist within this precision value.");

        javax.swing.GroupLayout spectrumPreprocessingPanelLayout = new javax.swing.GroupLayout(spectrumPreprocessingPanel);
        spectrumPreprocessingPanel.setLayout(spectrumPreprocessingPanelLayout);
        spectrumPreprocessingPanelLayout.setHorizontalGroup(
            spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                            .addComponent(maximumNumberOfPeaksLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(maximumNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                            .addComponent(minimumNumberOfPeaksLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(minimumNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                            .addComponent(spectrumMassWindowValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spectrumMassWindowValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                            .addComponent(lowerPrecursorMassBoundLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lowerPrecursorMassBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, spectrumPreprocessingPanelLayout.createSequentialGroup()
                            .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(deisotopeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deconvulatePrecisionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(deconvulatePrecisionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(deisotopePrecisionTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(347, Short.MAX_VALUE))
        );
        spectrumPreprocessingPanelLayout.setVerticalGroup(
            spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumPreprocessingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spectrumMassWindowValueLabel)
                    .addComponent(spectrumMassWindowValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minimumNumberOfPeaksLabel)
                    .addComponent(minimumNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maximumNumberOfPeaksLabel)
                    .addComponent(maximumNumberOfPeaksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lowerPrecursorMassBoundLabel)
                    .addComponent(lowerPrecursorMassBoundTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deisotopeLabel)
                    .addComponent(deisotopePrecisionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(spectrumPreprocessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deconvulatePrecisionLabel)
                    .addComponent(deconvulatePrecisionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(277, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Spectrum preprocessing", spectrumPreprocessingPanel);

        multiThreadingAndValidationPanel.setOpaque(false);

        numberOfThreadsLabel.setText("Number of threads*:");

        fdrCalculationLabel.setText("<html>FDR calculation.<br>Improved means seperate XPSMs lists (T) into inter- and intra-protein XL sites to compute FDR for each subset.*:</html>");

        fdrCalcalationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "improved", "global" }));

        globalFdrValueLabel.setText("Global FDR value*:");

        interProteinFdrValueLabel.setText("Inter-protein improved FDR value*:");

        intraProteinFdrValueLabel.setText("Intra-protein improved FDR value*:");

        writePercolatorInputFilesCheckBox.setText("Write separate Percolator input files*");

        javax.swing.GroupLayout multiThreadingAndValidationPanelLayout = new javax.swing.GroupLayout(multiThreadingAndValidationPanel);
        multiThreadingAndValidationPanel.setLayout(multiThreadingAndValidationPanelLayout);
        multiThreadingAndValidationPanelLayout.setHorizontalGroup(
            multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(multiThreadingAndValidationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fdrCalculationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(multiThreadingAndValidationPanelLayout.createSequentialGroup()
                        .addComponent(intraProteinFdrValueLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(intraProteinFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(multiThreadingAndValidationPanelLayout.createSequentialGroup()
                        .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(globalFdrValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(interProteinFdrValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(globalFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(interProteinFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(multiThreadingAndValidationPanelLayout.createSequentialGroup()
                        .addComponent(numberOfThreadsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberOfThreadsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fdrCalcalationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(writePercolatorInputFilesCheckBox))
                .addContainerGap(245, Short.MAX_VALUE))
        );
        multiThreadingAndValidationPanelLayout.setVerticalGroup(
            multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(multiThreadingAndValidationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfThreadsLabel)
                    .addComponent(numberOfThreadsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(writePercolatorInputFilesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fdrCalculationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fdrCalcalationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(globalFdrValueLabel)
                    .addComponent(globalFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(interProteinFdrValueLabel)
                    .addComponent(interProteinFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(multiThreadingAndValidationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intraProteinFdrValueLabel)
                    .addComponent(intraProteinFdrValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(252, Short.MAX_VALUE))
        );

        mainTabbedPane.addTab("Multithreading and validation", multiThreadingAndValidationPanel);

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPane)
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bottomPanel.setOpaque(false);

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(85, 27));
        closeButton.setMinimumSize(new java.awt.Dimension(85, 27));
        closeButton.setPreferredSize(new java.awt.Dimension(85, 27));

        runButton.setText("run");
        runButton.setPreferredSize(new java.awt.Dimension(85, 27));

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JCheckBox commonPeptideMassToleranceCheckBox;
    private javax.swing.JLabel commonPeptideMassToleranceWindowLabel;
    private javax.swing.JTextField commonPeptideMassToleranceWindowTextField;
    private javax.swing.JComboBox<String> commonPeptideMassToleranceWindowUnitComboBox;
    private javax.swing.JLabel commonPeptideMassToleranceWindowUnitLabel;
    private javax.swing.JButton contaminantsFastaDbBrowseButton;
    private javax.swing.JLabel contaminantsFastaDbFilePathLabel;
    private javax.swing.JTextField contaminantsFastaDbPathTextField;
    private javax.swing.JComboBox<String> crossLinkerComboBox;
    private javax.swing.JLabel crossLinkerLabel;
    private javax.swing.JLabel crossLinkerLabel1;
    private javax.swing.JPanel crossLinkingPanel;
    private javax.swing.JLabel crossLinkingTypeLabel;
    private javax.swing.JComboBox<String> crosslinkingTypeComboBox;
    private javax.swing.JLabel deconvulatePrecisionLabel;
    private javax.swing.JTextField deconvulatePrecisionTextField;
    private javax.swing.JLabel deisotopeLabel;
    private javax.swing.JTextField deisotopePrecisionTextField;
    private javax.swing.JComboBox<String> enzymeComboBox;
    private javax.swing.JLabel enzymeLabel;
    private javax.swing.JButton fastaDbBrowseButton;
    private javax.swing.JLabel fastaDbPathLabel;
    private javax.swing.JTextField fastaDbPathTextField;
    private javax.swing.JComboBox<String> fdrCalcalationComboBox;
    private javax.swing.JLabel fdrCalculationLabel;
    private javax.swing.JLabel firstPeptideMassToleranceWindowBaseLabel;
    private javax.swing.JTextField firstPeptideMassToleranceWindowBaseTextField;
    private javax.swing.JLabel firstPeptideMassToleranceWindowLabel;
    private javax.swing.JTextField firstPeptideMassToleranceWindowTextField;
    private javax.swing.JComboBox<String> firstPeptideMassToleranceWindowUnitComboBox;
    private javax.swing.JLabel firstPeptideMassToleranceWindowUnitLabel;
    private javax.swing.JLabel firstPeptideMassToleranceWindowValueLabel;
    private gui.DualList fixedModificationsDualList;
    private javax.swing.JLabel fixedModificationsLabel;
    private javax.swing.JPanel fixedModificationsPanel;
    private javax.swing.JLabel fourthPeptideMassToleranceWindowBaseLabel;
    private javax.swing.JTextField fourthPeptideMassToleranceWindowBaseTextField;
    private javax.swing.JLabel fourthPeptideMassToleranceWindowLabel;
    private javax.swing.JTextField fourthPeptideMassToleranceWindowTextField;
    private javax.swing.JComboBox<String> fourthPeptideMassToleranceWindowUnitComboBox;
    private javax.swing.JLabel fourthPeptideMassToleranceWindowUnitLabel;
    private javax.swing.JLabel fourthPeptideMassToleranceWindowValueLabel;
    private javax.swing.JLabel fragmentationLabel;
    private javax.swing.JComboBox<String> fragmentationModeComboBox;
    private javax.swing.JLabel globalFdrValueLabel;
    private javax.swing.JTextField globalFdrValueTextField;
    private javax.swing.JPanel inSilicoDigestionPanel;
    private javax.swing.JPanel inputOutputPanel;
    private javax.swing.JLabel interProteinFdrValueLabel;
    private javax.swing.JTextField interProteinFdrValueTextField;
    private javax.swing.JCheckBox intraLinkingCheckBox;
    private javax.swing.JLabel intraProteinFdrValueLabel;
    private javax.swing.JTextField intraProteinFdrValueTextField;
    private javax.swing.JLabel intralinkingLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JComboBox<String> labelingComboBox;
    private javax.swing.JLabel lowerPrecursorMassBoundLabel;
    private javax.swing.JTextField lowerPrecursorMassBoundTextField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JLabel maxModPeptideLabel;
    private javax.swing.JTextField maxModPeptideTextField;
    private javax.swing.JLabel maximumNumberOfPeaksLabel;
    private javax.swing.JTextField maximumNumberOfPeaksTextField;
    private javax.swing.JLabel maximumPeptideLengthLabel;
    private javax.swing.JTextField maximumPeptideLengthTextField;
    private javax.swing.JLabel maximumPeptideMassLabel;
    private javax.swing.JTextField maximumPeptideMassTextField;
    private javax.swing.JLabel minNumberOfPeaksLabel;
    private javax.swing.JTextField minNumberOfPeaksTextField;
    private javax.swing.JLabel minimumNumberOfPeaksLabel;
    private javax.swing.JTextField minimumNumberOfPeaksTextField;
    private javax.swing.JLabel minimumPeptideLengthLabel;
    private javax.swing.JTextField minimumPeptideLengthTextField;
    private javax.swing.JLabel minimumPeptideMassLabel;
    private javax.swing.JTextField minimumPeptideMassTextField;
    private javax.swing.JLabel missedCleavagesLabel;
    private javax.swing.JTextField missedCleavagesTextField;
    private javax.swing.JPanel modificationsPanel;
    private javax.swing.JCheckBox monoLinkingCheckBox;
    private javax.swing.JLabel monolinkingLabel;
    private javax.swing.JComboBox<String> ms1ReportingComboBox;
    private javax.swing.JLabel ms1ReportingLabel;
    private javax.swing.JPanel multiThreadingAndValidationPanel;
    private javax.swing.JComboBox<String> neutralLossesComboBox;
    private javax.swing.JLabel neutralLossesLabel;
    private javax.swing.JLabel numberOfThreadsLabel;
    private javax.swing.JTextField numberOfThreadsTextField;
    private javax.swing.JPanel otherModSettingsPanel;
    private javax.swing.JButton outputDirectoryBrowseButton;
    private javax.swing.JLabel outputDirectoryPathLabel;
    private javax.swing.JTextField outputDirectoryPathTextField;
    private javax.swing.JComboBox<String> peakMatchingComboBox;
    private javax.swing.JLabel peakMatchingLabel;
    private javax.swing.JLabel peptideLengthsLabel;
    private javax.swing.JPanel peptideMassToleranceWindowsPanel;
    private javax.swing.JLabel peptideToleranceLabel;
    private javax.swing.JSpinner peptideToleranceSpinner;
    private javax.swing.JButton runButton;
    private javax.swing.JPanel scoringPanel;
    private javax.swing.JButton searchDbBrowseButton;
    private javax.swing.JButton searchDbBrowseButton1;
    private javax.swing.JLabel searchDbPathLabel;
    private javax.swing.JLabel searchDbPathLabel1;
    private javax.swing.JTextField searchDbPathTextField1;
    private javax.swing.JTextField searchDbPathTextField2;
    private javax.swing.JLabel secondPeptideMassToleranceWindowBaseLabel;
    private javax.swing.JTextField secondPeptideMassToleranceWindowBaseTextField;
    private javax.swing.JLabel secondPeptideMassToleranceWindowLabel;
    private javax.swing.JTextField secondPeptideMassToleranceWindowTextField;
    private javax.swing.JComboBox<String> secondPeptideMassToleranceWindowUnitComboBox;
    private javax.swing.JLabel secondPeptideMassToleranceWindowUnitLabel;
    private javax.swing.JLabel secondPeptideMassToleranceWindowValueLabel;
    private javax.swing.JCheckBox serineCheckBox;
    private javax.swing.JLabel sideReactionsLabel;
    private javax.swing.JLabel spectrumMassWindowValueLabel;
    private javax.swing.JTextField spectrumMassWindowValueTextField;
    private javax.swing.JPanel spectrumPreprocessingPanel;
    private javax.swing.JLabel thirdPeptideMassToleranceWindowBaseLabel;
    private javax.swing.JTextField thirdPeptideMassToleranceWindowBaseTextField;
    private javax.swing.JLabel thirdPeptideMassToleranceWindowLabel;
    private javax.swing.JTextField thirdPeptideMassToleranceWindowTextField;
    private javax.swing.JComboBox<String> thirdPeptideMassToleranceWindowUnitComboBox;
    private javax.swing.JLabel thirdPeptideMassToleranceWindowUnitLabel;
    private javax.swing.JLabel thirdPeptideMassToleranceWindowValueLabel;
    private javax.swing.JCheckBox threonineCheckBox;
    private javax.swing.JPanel topPanel;
    private javax.swing.JCheckBox tyrosineCheckBox;
    private javax.swing.JButton validatedTargetHitsBrowseButton;
    private javax.swing.JLabel validatedTargetHitsPathLabel;
    private javax.swing.JTextField validatedTargetHitsPathTextField;
    private gui.DualList variableModificationsDualList;
    private javax.swing.JLabel variableModificationsLabel;
    private javax.swing.JPanel variableModificationsPanel;
    private javax.swing.JCheckBox writePercolatorInputFilesCheckBox;
    private javax.swing.JButton xpsmsBrowseButton;
    private javax.swing.JLabel xpsmsPathLabel;
    private javax.swing.JTextField xpsmsPathTextField;
    // End of variables declaration//GEN-END:variables
}
