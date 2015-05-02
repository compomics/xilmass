/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualize;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.CellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.apache.commons.lang.NumberUtils;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public final class Visualize extends javax.swing.JFrame {

    private StartDialog startDialog;
    private MSnSpectrum original_spec = new MSnSpectrum(),
            tmp_spec = new MSnSpectrum();
    private String[] specTitles = new String[2];
    private String specsFolder = "";
    private SpectrumPanel spectrumPanel;
    private String[] columnNames = null;
    private File resultFile;
    private boolean isOpenFileMenu = false;
    private SpectrumFactory spFct = SpectrumFactory.getInstance();
    private static final Logger LOGGER = Logger.getLogger(Visualize.class.toString());

    /**
     * Creates new form Visualize
     */
    public Visualize() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        startDialog = new StartDialog(this, true);
        initComponents();
        setSpecsFolder(startDialog.getSpecFolder());
        prepareTable();
//        setLocation(200, WIDTH);
        this.setVisible(true);
    }

    public String getSpecsFolder() {
        return specsFolder;
    }

    public void setSpecsFolder(String specsFolder) {
        this.specsFolder = specsFolder;
    }

    public void prepareTable() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        if (!isOpenFileMenu) {
            resultFile = new File(startDialog.getPathToScoreFilejTextField().getText());
        }
        try {
            // read the file
            BufferedReader br = new BufferedReader(new FileReader(resultFile.getAbsolutePath()));
            String line = null;
            int row_number = 0,
                    control = 0;
            ArrayList<String[]> dataStrArr = new ArrayList<String[]>();
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] split = line.split("\t");
                    // prepare column names
                    if (control == 0) {
                        columnNames = new String[split.length + 1];
                        // first column - just for indexing
                        columnNames[0] = "index";
                        // write names of other columns
                        for (int i = 0; i < split.length; i++) {
                            columnNames[i + 1] = split[i];

                        }
                    }
                    // prepare data
                    if (control != 0) {
                        if (!split[0].equals(split[1])) {
                            dataStrArr.add(split);
                            row_number++;
                        }
                    }
                    control++;
                }
            }
            // construct a 2D array for TableModel
            Object[][] data = new Object[row_number][columnNames.length];
            // Prepare similarity table
            SimilarityTableModel similarityTableModel = new SimilarityTableModel(columnNames, data);
            resultFilejTable.setModel(similarityTableModel);
//            resultFilejTable.setFillsViewportHeight(true);

            // Prepare a sorter
            TableRowSorter sorter = new TableRowSorter(resultFilejTable.getModel()) {
                @Override
                public Comparator getComparator(int column) {
                    Comparator<String> comparator = new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            boolean isNumber = NumberUtils.isNumber(o1);
                            if (isNumber) {
                                Double o1_integer = Double.parseDouble(o1),
                                        o2_integer = Double.parseDouble(o2);

                                return (o1_integer.compareTo(o2_integer));
                            } else {
                                return o1.compareTo(o2);
                            }
                        }
                    };
                    return comparator;
                }
            };

            resultFilejScrollPane.setAutoscrolls(true);
            resultFilejTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            // set widths.
            resultFilejTable.getColumnModel().getColumn(0).setPreferredWidth(880);  // index            
            resultFilejTable.getColumnModel().getColumn(1).setPreferredWidth(7000); // spectrum index
            resultFilejTable.getColumnModel().getColumn(2).setPreferredWidth(15000); // spectrum file           
            resultFilejTable.getColumnModel().getColumn(3).setPreferredWidth(15000); // spectrum title 
            resultFilejTable.getColumnModel().getColumn(4).setPreferredWidth(5300); // precursor mz
            resultFilejTable.getColumnModel().getColumn(5).setPreferredWidth(1500); // precursor charg
            resultFilejTable.getColumnModel().getColumn(6).setPreferredWidth(5800); // precursor mass            
            resultFilejTable.getColumnModel().getColumn(7).setPreferredWidth(5800); // theoretical mass
            resultFilejTable.getColumnModel().getColumn(8).setPreferredWidth(3000); // MS1mass error
            resultFilejTable.getColumnModel().getColumn(9).setPreferredWidth(3500); // Scoring funcion
            resultFilejTable.getColumnModel().getColumn(10).setPreferredWidth(6600); // Score
            resultFilejTable.getColumnModel().getColumn(11).setPreferredWidth(5500); // proteinA name
            resultFilejTable.getColumnModel().getColumn(12).setPreferredWidth(5500); //proteinB name
            resultFilejTable.getColumnModel().getColumn(13).setPreferredWidth(9400);  // peptide sequence A
            resultFilejTable.getColumnModel().getColumn(14).setPreferredWidth(9400); // peptide sequence B
            resultFilejTable.getColumnModel().getColumn(15).setPreferredWidth(900); // linker position peptideA
            resultFilejTable.getColumnModel().getColumn(16).setPreferredWidth(900); // linker position peptideB
            resultFilejTable.getColumnModel().getColumn(17).setPreferredWidth(2000); // #matched peaks
            resultFilejTable.getColumnModel().getColumn(18).setPreferredWidth(2000); // #matched theoretical peaks
            resultFilejTable.getColumnModel().getColumn(19).setPreferredWidth(60000); // matched peak list
            resultFilejTable.getColumnModel().getColumn(20).setPreferredWidth(135000); // theoretical matched peak list

            // Fill information on data.
            int number = 0;
            for (int arr = 0; arr < dataStrArr.size(); arr++) {
                String[] strArr = dataStrArr.get(arr);
                if (!strArr[0].equals(strArr[1])) {
                    data[arr][0] = number;
                    number++;
                    resultFilejTable.setValueAt(number, arr, 0);
                    for (int i = 0; i < strArr.length; i++) {
                        if (i == 3 || i == 5 || i == 6 || i == 7 || i == 9) {
                            double value_to_show = Math.floor(new Double(strArr[i]) * 10000) / 10000;
                            resultFilejTable.setValueAt(value_to_show, arr, i + 1);
                        } else {
                            data[arr][i + 1] = strArr[i];
                            resultFilejTable.setValueAt(strArr[i], arr, i + 1);
                        }
                    }
                }
            }

            // set some variables on a table         
            SimilarityTableCellRenderer renderer = new SimilarityTableCellRenderer();
            resultFilejTable.setDefaultRenderer(Object.class, renderer);
            resultFilejTable.setRowSorter(sorter);
            resultFilejTable.setAutoCreateColumnsFromModel(true);
            // select an entire row by clicking
            resultFilejTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            resultFilejTable.setRowSelectionAllowed(true);
            resultFilejTable.setColumnSelectionAllowed(false);
            resultFilejTable.setRowSelectionInterval(0, 0);
            Dimension size = new Dimension(resultFilejTable.getColumnModel().getTotalColumnWidth()+2700, resultFilejTable.getRowCount() * resultFilejTable.getRowHeight());
            resultFilejTable.setPreferredSize(size);
            resultFilejTable.setPreferredScrollableViewportSize(size);
            String spectrumFileName = (String) resultFilejTable.getValueAt(1, 2),
                    spectrumTitle = (String) resultFilejTable.getValueAt(1, 3);
            setOriginalSpectrumForPlotting(spectrumFileName, spectrumTitle);
            annotateSpectrum();

        } catch (IOException ex) {
            LOGGER.info("Something went wrong while start up. Check either your spectrum folders or your score file!");
        }
    }

    /**
     * To prepare spectrum for plotting from a spectra folder.
     *
     * @param spectrumName is MSnSpectrum object
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    private void setOriginalSpectrumForPlotting(String spectrumFileName, String spectrumTitle) throws IOException, MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        boolean isSpecfound = false;
        // find spectrum...
        LOGGER.info("SpectrumFileName=" + spectrumFileName + "\t" + "SpectrumTitle=" + spectrumTitle);
        original_spec = findMSnSpectrum(specsFolder, spectrumFileName, spectrumTitle);
        // create copies for the selected spectra
        // check the possible error
        if (original_spec != null) {
            isSpecfound = true;
        }
        if (isSpecfound == false) {
            JOptionPane.showMessageDialog(this, "Spectrum cannot be found on the selected folder!", "Input file error", JOptionPane.ERROR_MESSAGE);
        }
        // clear a SpectrumFactory
        spFct.clearFactory();
    }

    /**
     * To find a spectrum according to spectrum name while searching in all
     * spectra files on a given spectra folder
     *
     * @param spectraFolder
     * @param specName
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws MzMLUnmarshallerException
     */
    private MSnSpectrum findMSnSpectrum(String spectraFolder, String spectrumFileName, String spectrumTitle) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        MSnSpectrum msms = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();
        for (File mgf : new File(spectraFolder).listFiles()) {
            if (mgf.getName().endsWith(".mgf") && mgf.getName().equals(spectrumFileName)) {
                fct.addSpectra(mgf);
                for (String title : spFct.getSpectrumTitles(mgf.getName())) {
                    if (title.equals(spectrumTitle)) {
                        msms = (MSnSpectrum) spFct.getSpectrum(mgf.getName(), title);
                    }
                }
            }
        }
        fct.clearFactory();
        return msms;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visualizeSpectrumjPanel = new javax.swing.JPanel();
        resultFilejScrollPane = new javax.swing.JScrollPane();
        resultFilejTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(2147, 2147));

        visualizeSpectrumjPanel.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout visualizeSpectrumjPanelLayout = new java.awt.GridBagLayout();
        visualizeSpectrumjPanelLayout.columnWidths = new int[] {200};
        visualizeSpectrumjPanelLayout.rowHeights = new int[] {200};
        visualizeSpectrumjPanelLayout.columnWeights = new double[] {200.0};
        visualizeSpectrumjPanelLayout.rowWeights = new double[] {2000.0};
        visualizeSpectrumjPanel.setLayout(visualizeSpectrumjPanelLayout);

        resultFilejScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        resultFilejScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        resultFilejScrollPane.setAutoscrolls(true);
        resultFilejScrollPane.setMaximumSize(new java.awt.Dimension(3, 3));
        resultFilejScrollPane.setMinimumSize(new java.awt.Dimension(2, 2));
        resultFilejScrollPane.setPreferredSize(new java.awt.Dimension(2, 2));

        resultFilejTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        resultFilejTable.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        resultFilejTable.setPreferredSize(new java.awt.Dimension(12000, 6000));
        resultFilejTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultFilejTableMouseClicked(evt);
            }
        });
        resultFilejTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                resultFilejTableKeyReleased(evt);
            }
        });
        resultFilejScrollPane.setViewportView(resultFilejTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultFilejTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultFilejTableMouseClicked
        int selectedRow = resultFilejTable.getSelectedRow();
        String spectrumFileName = (String) resultFilejTable.getValueAt(selectedRow, 2),
                spectrumTitle = (String) resultFilejTable.getValueAt(selectedRow, 3);
        try {
            setOriginalSpectrumForPlotting(spectrumFileName, spectrumTitle);
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            original_spec = findMSnSpectrum(specsFolder, spectrumFileName, spectrumTitle);
            annotateSpectrum();
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resultFilejTableMouseClicked

    private void resultFilejTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resultFilejTableKeyReleased
        int selectedRow = resultFilejTable.getSelectedRow();
        String spectrumFileName = (String) resultFilejTable.getValueAt(selectedRow, 2),
                spectrumTitle = (String) resultFilejTable.getValueAt(selectedRow, 3);
        try {
            setOriginalSpectrumForPlotting(spectrumFileName, spectrumTitle);
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            original_spec = findMSnSpectrum(specsFolder, spectrumFileName, spectrumTitle);
            annotateSpectrum();
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resultFilejTableKeyReleased

    public void annotateSpectrum() {
        // First prepare a spectrum Panel
        double[] mzs = original_spec.getMzValuesAsArray(),
                ints = original_spec.getIntensityValuesAsArray();
        double precursor = original_spec.getPrecursor().getMz();
        String charge = original_spec.getPrecursor().getPossibleChargesAsString(),
                name = original_spec.getSpectrumTitle();
        spectrumPanel = new SpectrumPanel(
                mzs, // double [] of mz values 
                ints, // double [] of intensity values
                precursor, // double with precursor mz 
                charge, // String precursor charge
                name); // String spectrum file name  

        // TODO: Make sure about these annotations!
        // set up the peak annotations!!!
        List<SpectrumAnnotation> peakAnnotation = getAnnotatedPeaks();
        peakAnnotation.add(
                new DefaultSpectrumAnnotation(
                        180, // the mz value to annotate
                        -0.0068229, // the mz error margin
                        new Color(248, 151, 202), // the annotation color
                        "y1+"));  // the annotation label

        // add the annotations to the spectrum
        spectrumPanel.setAnnotations(peakAnnotation);

        // add the spectrum panel to the parent frame or dialog
        visualizeSpectrumjPanel.add(spectrumPanel);
        spectrumPanel.setBackground(Color.getHSBColor(10, 0, 8));
        visualizeSpectrumjPanel.removeAll();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        visualizeSpectrumjPanel.add(spectrumPanel, gridBagConstraints);
        visualizeSpectrumjPanel.repaint();
        visualizeSpectrumjPanel.revalidate();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
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
            java.util.logging.Logger.getLogger(Visualize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Visualize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Visualize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Visualize.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Visualize().setVisible(true);
                } catch (MzMLUnmarshallerException ex) {
                    Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane resultFilejScrollPane;
    private javax.swing.JTable resultFilejTable;
    private javax.swing.JPanel visualizeSpectrumjPanel;
    // End of variables declaration//GEN-END:variables

    private List<SpectrumAnnotation> getAnnotatedPeaks() {
        List<SpectrumAnnotation> annotations = new ArrayList<SpectrumAnnotation>();
        int selectedRow = resultFilejTable.getSelectedRow();
        String annotatedPeaksStr = (String) resultFilejTable.getValueAt(selectedRow, 20);
        String[] splittedAnnotatedPeaksStr = annotatedPeaksStr.split(" ");
        Color lightBlue = Color.getHSBColor(0.56f, 0.3f, 1f),
                lightPink = Color.getHSBColor(0.92f, 0.3f, 1f),
                lightYellow = Color.getHSBColor(0.16f, 0.4f, 1f),
                purple = Color.getHSBColor(0.76f, 0.4f, 1f),
                selectedColor = null;
        for (String splittedAnnotatedPeak : splittedAnnotatedPeaksStr) {
            if (!splittedAnnotatedPeak.isEmpty()) {
                String[] annotationInfo = splittedAnnotatedPeak.split("_");
                String chargeState = annotationInfo[0],
                        ionNameAndIndex = splittedAnnotatedPeak.substring(splittedAnnotatedPeak.indexOf("_") + 1, splittedAnnotatedPeak.lastIndexOf("_")),
                        mz = (splittedAnnotatedPeak.substring(splittedAnnotatedPeak.lastIndexOf("_")).split("="))[1];
                String chInfo = "1+";
                if (chargeState.equals("doublyCharged")) {
                    chInfo = "2+";
                }
                ionNameAndIndex = "(" + ionNameAndIndex + ")" + chInfo;
                if (ionNameAndIndex.contains("lepA")) {
                    selectedColor = purple;
                } else if (ionNameAndIndex.contains("lepB")) {
                    selectedColor = lightBlue;
                } else if (ionNameAndIndex.contains("pepA")) {
                    selectedColor = Color.ORANGE;
                } else if (ionNameAndIndex.contains("pepB")) {
                    selectedColor = Color.BLUE;
                }
                DefaultSpectrumAnnotation defaultSpecAn = new DefaultSpectrumAnnotation(
                        new Double(mz), // the mz value to annotate
                        0.0068229, // the mz error margin
                        selectedColor, // the annotation color
                        ionNameAndIndex); // the annotation label-like y1+
                annotations.add(defaultSpecAn);
            }
        }
        return annotations;
    }
}
