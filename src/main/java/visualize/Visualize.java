/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualize;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import gui.SimilarityTableCellRenderer;
import gui.SimilarityTableModel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.TableRowSorter;
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

    /**
     * Creates new form Visualize
     */
    public Visualize() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        startDialog = new StartDialog(this, true);
        initComponents();
        setSpecsFolder(startDialog.getSpecFolder());
        prepareTable();
        setLocation(200, WIDTH);
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
                        columnNames = new String[4];
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
            resultFilejTable.setFillsViewportHeight(true);
            
            // Prepare a sorter
            TableRowSorter sorter = new TableRowSorter(resultFilejTable.getModel()) {
                @Override
                public Comparator getComparator(int column) {
                    Comparator<String> comparator = new Comparator<String>() {
                        public int compare(String o1, String o2) {
                            Double o1_integer = Double.parseDouble(o1),
                                    o2_integer = Double.parseDouble(o2);
                            return (o1_integer.compareTo(o2_integer));
                        }
                    };
                    return comparator;
                }
            };

            // Fill information on data.
            int number = 0;
            for (int arr = 0; arr < dataStrArr.size(); arr++) {
                String[] strArr = dataStrArr.get(arr);
                if (!strArr[0].equals(strArr[1])) {
                    data[arr][0] = number;
                    number++;
                    resultFilejTable.setValueAt(number, arr, 0);
                    for (int i = 0; i < strArr.length; i++) {
                        data[arr][i + 1] = strArr[i];
                        resultFilejTable.setValueAt(strArr[i], arr, i + 1);
                    }
                }
            }

            // set some variables on a table         
            SimilarityTableCellRenderer renderer = new SimilarityTableCellRenderer();
            resultFilejTable.setDefaultRenderer(Object.class, renderer);
            resultFilejTable.setRowSorter(sorter);
            resultFilejTable.setAutoCreateColumnsFromModel(true);
            resultFilejTable.setPreferredScrollableViewportSize(resultFilejTable.getPreferredSize());
            // select an entire row by clicking
            resultFilejTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            resultFilejTable.setRowSelectionAllowed(true);
            resultFilejTable.setColumnSelectionAllowed(false);
            resultFilejTable.setAutoResizeMode(5);
            resultFilejTable.setRowSelectionInterval(0, 0);

            // select min/max/preferred widths
            resultFilejTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            resultFilejTable.getColumnModel().getColumn(0).setMaxWidth(100);
            resultFilejTable.getColumnModel().getColumn(0).setMinWidth(30);

            String specName = (String) resultFilejTable.getValueAt(0, 1);
            setOriginalSpectrumForPlotting(specName);
            annotateSpectrum();
            
        } catch (IOException ex) {
            System.out.println("Something went wrong while start up. Check either your spectrum folders or your score file!");
        }
    }

    /**
     * To prepare spectrum for plotting from a spectra folder.
     *
     * @param specName is MSnSpectrum object 
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    private void setOriginalSpectrumForPlotting(String specName) throws IOException, MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        boolean isSpecfound = false;
        // find spectrum...
        original_spec = findMSnSpectrum(specsFolder, specName);
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
     * @param specFolder
     * @param specName
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws MzMLUnmarshallerException
     */
    private MSnSpectrum findMSnSpectrum(String specFolder, String specName) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        MSnSpectrum msms = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();
        for (File mgf : new File(specFolder).listFiles()) {
            if (mgf.getName().endsWith(".mgf")) {
                fct.addSpectra(mgf);
                for (String title : spFct.getSpectrumTitles(mgf.getName())) {
                    if (title.equals(specName)) {
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

        visualizeSpectrumjPanel.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout visualizeSpectrumjPanelLayout = new java.awt.GridBagLayout();
        visualizeSpectrumjPanelLayout.columnWidths = new int[] {200};
        visualizeSpectrumjPanelLayout.rowHeights = new int[] {200};
        visualizeSpectrumjPanelLayout.columnWeights = new double[] {200.0};
        visualizeSpectrumjPanelLayout.rowWeights = new double[] {2000.0};
        visualizeSpectrumjPanel.setLayout(visualizeSpectrumjPanelLayout);

        resultFilejScrollPane.setAutoscrolls(true);

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
        resultFilejTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultFilejTableMouseClicked(evt);
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
                    .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultFilejTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultFilejTableMouseClicked
        int selectedRow = resultFilejTable.getSelectedRow();
        String specName = resultFilejTable.getValueAt(selectedRow, 1).toString();
        try {
            original_spec = findMSnSpectrum(specsFolder, specName);
            annotateSpectrum();
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resultFilejTableMouseClicked

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
        List<SpectrumAnnotation> peakAnnotation = new ArrayList();
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
        spectrumPanel.setiXAxisMax(220);
        spectrumPanel.setiYAxisMax(12200);
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
}
