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
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
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
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
    private boolean isOpenFileMenu = false,
            isPercolatorOutput = false;
    private SpectrumFactory spFct = SpectrumFactory.getInstance();
    private static final Logger LOGGER = Logger.getLogger(Visualize.class.toString());

    /**
     * Creates new form Visualize
     *
     * @throws MzMLUnmarshallerException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public Visualize() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        startDialog = new StartDialog(this, true);
        initComponents();
        setSpecsFolder(startDialog.getSpecFolder());
        isPercolatorOutput = startDialog.isIsPercolatorOutput();
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

    public ArrayList<String[]> readResultFile(File file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String line = null;
        int control = 0;
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
                    }
                }
                control++;
            }
        }
        return dataStrArr;
    }

    private void readPercolatorOutputFileJTable(ArrayList<String[]> dataStrArr, Object[][] data, TableRowSorter sorter) {
        resultFilejScrollPane.setAutoscrolls(true);
        resultFilejTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // set widths.
//        resultFilejTable.getColumnModel().getColumn(0).setPreferredWidth(80);    // index            
//        resultFilejTable.getColumnModel().getColumn(1).setPreferredWidth(100);   // spectrum file
//        resultFilejTable.getColumnModel().getColumn(2).setPreferredWidth(100);   // scan          
//        resultFilejTable.getColumnModel().getColumn(3).setPreferredWidth(1000);   // qvalue          
//        resultFilejTable.getColumnModel().getColumn(4).setPreferredWidth(1000);   // posterior_error_prob
//        resultFilejTable.getColumnModel().getColumn(5).setPreferredWidth(5800);   // proteinA            
//        resultFilejTable.getColumnModel().getColumn(6).setPreferredWidth(5800);   // proteinB    
//        resultFilejTable.getColumnModel().getColumn(7).setPreferredWidth(5800);   // peptideA
//        resultFilejTable.getColumnModel().getColumn(8).setPreferredWidth(5800);     // peptideB
//        resultFilejTable.getColumnModel().getColumn(9).setPreferredWidth(2500);   // XLType
//        resultFilejTable.getColumnModel().getColumn(10).setPreferredWidth(880);   // linkA
//        resultFilejTable.getColumnModel().getColumn(11).setPreferredWidth(880);   // linkB 
//        resultFilejTable.getColumnModel().getColumn(12).setPreferredWidth(9400);   // Predicted
//        resultFilejTable.getColumnModel().getColumn(13).setPreferredWidth(9400);   // Euclidean_CarbonBeta
//        resultFilejTable.getColumnModel().getColumn(14).setPreferredWidth(6000);   // Euclidean_CarbonAlpha
        // Fill information on data.
        int number = 0;
        for (int arr = 0; arr < dataStrArr.size(); arr++) {
            String[] strArr = dataStrArr.get(arr);
            if (!strArr[0].equals(strArr[1])) {
                data[arr][0] = number;
                number++;
                resultFilejTable.setValueAt(number, arr, 0);
                for (int i = 0; i < strArr.length; i++) {
                    if (i == 1 || i == 10 || i == 11) {
                        int value_to_show = new Integer(strArr[i]);
                        resultFilejTable.setValueAt(value_to_show, arr, i + 1);
                    } else if (i == 2 || i == 3 | i == 4) {
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
        Dimension size = new Dimension(resultFilejTable.getColumnModel().getTotalColumnWidth() + 2700, resultFilejTable.getRowCount() * resultFilejTable.getRowHeight());
        resultFilejTable.setPreferredSize(size);
        resultFilejTable.setPreferredScrollableViewportSize(size);
        resultFilejScrollPane.setPreferredSize(size);
    }

    private void readResultFileJTable(ArrayList<String[]> dataStrArr, Object[][] data, TableRowSorter sorter) {
        resultFilejScrollPane.setAutoscrolls(true);
//        resultFilejTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//         set widths.
//        resultFilejTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // index 
//        resultFilejTable.getColumnModel().getColumn(1).setPreferredWidth(500); // spectrum file         
//        resultFilejTable.getColumnModel().getColumn(2).setPreferredWidth(500); // spectrum title     
//        resultFilejTable.getColumnModel().getColumn(3).setPreferredWidth(120); // observed mass            
//        resultFilejTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // precursor charge 
//        resultFilejTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // scannr
//        resultFilejTable.getColumnModel().getColumn(16).setPreferredWidth(80);  // linkPeptideA
//        resultFilejTable.getColumnModel().getColumn(17).setPreferredWidth(80);  // linkPeptideB
//        resultFilejTable.getColumnModel().getColumn(18).setPreferredWidth(80);  // linkProteinA
//        resultFilejTable.getColumnModel().getColumn(19).setPreferredWidth(80);  // linkProteinB
//        resultFilejTable.getColumnModel().getColumn(25).setPreferredWidth(80);  // TT-TD-DD
//        resultFilejTable.getColumnModel().getColumn(28).setPreferredWidth(80);  // carbonbeta-carbonbeta
//        resultFilejTable.getColumnModel().getColumn(29).setPreferredWidth(80);  // carbonalpha-carbonalpha

//        resultFilejTable.getColumnModel().getColumn(1).setPreferredWidth(7000); // spectrum file
//        resultFilejTable.getColumnModel().getColumn(2).setPreferredWidth(1500); // spectrum title           
//        resultFilejTable.getColumnModel().getColumn(3).setPreferredWidth(2000); // observed mass            
//        resultFilejTable.getColumnModel().getColumn(4).setPreferredWidth(1500); // precursor charge
//            resultFilejTable.getColumnModel().getColumn(5).setPreferredWidth(5800);   // retention time             
//            resultFilejTable.getColumnModel().getColumn(6).setPreferredWidth(5800);   // scan number    
//            resultFilejTable.getColumnModel().getColumn(5).setPreferredWidth(2000);   // MS1mass error
//            resultFilejTable.getColumnModel().getColumn(8).setPreferredWidth(30);     // AbsMS1Err 
//            resultFilejTable.getColumnModel().getColumn(6).setPreferredWidth(2500);   // PeptideA
//            resultFilejTable.getColumnModel().getColumn(7).setPreferredWidth(3500);   // ProteinA
//            resultFilejTable.getColumnModel().getColumn(8).setPreferredWidth(3500);   // modification peptide seq A
//            resultFilejTable.getColumnModel().getColumn(9).setPreferredWidth(9400);   // PeptideB
//            resultFilejTable.getColumnModel().getColumn(10).setPreferredWidth(9400);  // ProteinB
//            resultFilejTable.getColumnModel().getColumn(11).setPreferredWidth(6000);  // modification peptide seq B
//            resultFilejTable.getColumnModel().getColumn(12).setPreferredWidth(6000);  // LinkingType
//            resultFilejTable.getColumnModel().getColumn(13).setPreferredWidth(900);   // linker position peptideA
//            resultFilejTable.getColumnModel().getColumn(14).setPreferredWidth(900);   // linker position peptideB
//            resultFilejTable.getColumnModel().getColumn(15).setPreferredWidth(4000);  // linker position proteinA
//            resultFilejTable.getColumnModel().getColumn(16).setPreferredWidth(4000);  // linker position proteinB
//            resultFilejTable.getColumnModel().getColumn(17).setPreferredWidth(64000); // ScoringFunction
//            resultFilejTable.getColumnModel().getColumn(18).setPreferredWidth(2000);  // Score
//            resultFilejTable.getColumnModel().getColumn(19).setPreferredWidth(2000);  // Matched peaks
//            resultFilejTable.getColumnModel().getColumn(20).setPreferredWidth(6000);  // Matched theoretical peaks
//            resultFilejTable.getColumnModel().getColumn(21).setPreferredWidth(2500);  // Ln(NumSpec)
//            resultFilejTable.getColumnModel().getColumn(22).setPreferredWidth(4500);  // Target/Decoy
//            resultFilejTable.getColumnModel().getColumn(23).setPreferredWidth(6000);  // LinkerLabeling
//            resultFilejTable.getColumnModel().getColumn(24).setPreferredWidth(6000);  // Predicted
//            resultFilejTable.getColumnModel().getColumn(25).setPreferredWidth(6000);  // Euclidean Beta Distance
//            resultFilejTable.getColumnModel().getColumn(26).setPreferredWidth(6000);  // Euclidean Alpha Distance
        // Fill information on data.
        int number = 0;
        for (int arr = 0; arr < dataStrArr.size(); arr++) {
            String[] strArr = dataStrArr.get(arr);
            if (!strArr[0].equals(strArr[1])) {
                data[arr][0] = number;
                number++;
                resultFilejTable.setValueAt(number, arr, 0);
                for (int i = 0; i < strArr.length; i++) {
                    if (i == 2 || i == 4 || i == 6 || i == 7 || i == 20 || i == 23 || i == 28) {
                        if (!strArr[i].equals("-")) {
                            double value_to_show = Math.floor(new Double(strArr[i]) * 10000) / 10000;
                            resultFilejTable.setValueAt(value_to_show, arr, i + 1);
                        }
                    } else if (i == 5) {
                        int value_to_show = new Integer(strArr[i]);
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
        Dimension size = new Dimension(resultFilejTable.getColumnModel().getTotalColumnWidth() + 2700, resultFilejTable.getRowCount() * resultFilejTable.getRowHeight());
        resultFilejTable.setPreferredSize(size);
        resultFilejTable.setPreferredScrollableViewportSize(size);
//        resultFilejTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

//        resultFilejScrollPane.setPreferredSize(size);
    }

    public void prepareTable() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        if (!isOpenFileMenu) {
            resultFile = new File(startDialog.getPathToScoreFilejTextField().getText());
        }
        try {
            // read the file
            ArrayList<String[]> dataStrArr = readResultFile(resultFile);
            int row_number = dataStrArr.size();
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
            if (!isPercolatorOutput) {
                readResultFileJTable(dataStrArr, data, sorter);
            } else {
                readPercolatorOutputFileJTable(dataStrArr, data, sorter);
            }
            System.out.println(resultFilejTable.getValueAt(1, 1) + "\t" + resultFilejTable.getValueAt(1, 2));
            String spectrumFileName = resultFilejTable.getValueAt(1, 1).toString(),
                    spectrumTitle = resultFilejTable.getValueAt(1, 2).toString();
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
                if (!isPercolatorOutput) {
                    for (String title : spFct.getSpectrumTitles(mgf.getName())) {
                        if (title.equals(spectrumTitle)) {
                            msms = (MSnSpectrum) spFct.getSpectrum(mgf.getName(), title);
                        }
                    }
                } else {
                    for (String title : spFct.getSpectrumTitles(mgf.getName())) {
                        MSnSpectrum tmpMSMS = (MSnSpectrum) spFct.getSpectrum(mgf.getName(), title);
                        String tmpScanNum = tmpMSMS.getScanNumber();
                        if (tmpScanNum.equals(spectrumTitle) || title.contains("scan=" + spectrumTitle + "\"")) {
                            msms = tmpMSMS;
                        }
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
        jMenuBar = new javax.swing.JMenuBar();
        jMenu = new javax.swing.JMenu();
        saveImagejMenuItem = new javax.swing.JMenuItem();
        exitjMenuItem = new javax.swing.JMenuItem();

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

        jMenu.setText("Menu");

        saveImagejMenuItem.setText("Save image");
        saveImagejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImagejMenuItemActionPerformed(evt);
            }
        });
        jMenu.add(saveImagejMenuItem);

        exitjMenuItem.setText("Exit");
        exitjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitjMenuItemActionPerformed(evt);
            }
        });
        jMenu.add(exitjMenuItem);

        jMenuBar.add(jMenu);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                    .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resultFilejTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultFilejTableMouseClicked
        int selectedRow = resultFilejTable.getSelectedRow();
        String spectrumFileName = (String) resultFilejTable.getValueAt(selectedRow, 1),
                spectrumTitle = (String) resultFilejTable.getValueAt(selectedRow, 2);
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

    private void setImage(java.awt.event.ActionEvent evt) {
        JFileChooser savePlaylistDialog = new JFileChooser();
        int status = savePlaylistDialog.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File savePlaylist = savePlaylistDialog.getSelectedFile();
            //BufferedImage bi = new BufferedImage(spectrumPanel.getSize().width, spectrumPanel.getSize().height, BufferedImage.TYPE_INT_ARGB);

            BufferedImage bi = new BufferedImage(visualizeSpectrumjPanel.getSize().width + 30, visualizeSpectrumjPanel.getSize().height + 30, BufferedImage.TYPE_INT_ARGB);

            Graphics g = bi.createGraphics();

            this.paint(g);  //this == JComponent
            g.dispose();
            try {
                ImageIO.write(bi, "png", new File(savePlaylist + ".png"));
            } catch (Exception e) {
            }
        }
    }
    private void saveImagejMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImagejMenuItemActionPerformed
        // Now save image 

        JFileChooser savePlaylistDialog = new JFileChooser();
        int status = savePlaylistDialog.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File savePlaylist = savePlaylistDialog.getSelectedFile();
            //BufferedImage bi = new BufferedImage(spectrumPanel.getSize().width, spectrumPanel.getSize().height, BufferedImage.TYPE_INT_ARGB);

            BufferedImage bi = new BufferedImage(visualizeSpectrumjPanel.getSize().width + 100, visualizeSpectrumjPanel.getSize().height + 100, BufferedImage.TYPE_INT_ARGB);

            Graphics g = bi.createGraphics();

            this.paint(g);  //this == JComponent
            g.dispose();
            try {
                ImageIO.write(bi, "png", new File(savePlaylist + ".png"));
            } catch (Exception e) {
            }
        }
//        final String formatName = "png";
//
//        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
//            ImageWriter writer = iw.next();
//            ImageWriteParam writeParam = writer.getDefaultWriteParam();
//            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
//            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
//            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
//                continue;
//            }
//
//            setDPI(metadata);
//
//            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
//            try {
//                writer.setOutput(stream);
//                writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
//            } finally {
//                stream.close();
//            }
//            break;
//        }   
    }//GEN-LAST:event_saveImagejMenuItemActionPerformed

    private void exitjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitjMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitjMenuItemActionPerformed

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

        // set up the peak annotations!!!
        List<SpectrumAnnotation> peakAnnotation = getAnnotatedPeaks();
        System.out.println("Annotated peaks=" + peakAnnotation.size());
        // add the annotations to the spectrum
        spectrumPanel.setAnnotations(peakAnnotation);

        // add the spectrum panel to the parent frame or dialog
        visualizeSpectrumjPanel.add(spectrumPanel);
        spectrumPanel.setBackground(Color.getHSBColor(0, 0, 1));
        visualizeSpectrumjPanel.removeAll();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        visualizeSpectrumjPanel.add(spectrumPanel, gridBagConstraints);
        visualizeSpectrumjPanel.revalidate();
        visualizeSpectrumjPanel.repaint();

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
                if ("System".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Visualize.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Visualize().setVisible(true);

                } catch (MzMLUnmarshallerException ex) {
                    Logger.getLogger(Visualize.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Visualize.class
                            .getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Visualize.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitjMenuItem;
    private javax.swing.JMenu jMenu;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JScrollPane resultFilejScrollPane;
    private javax.swing.JTable resultFilejTable;
    private javax.swing.JMenuItem saveImagejMenuItem;
    private javax.swing.JPanel visualizeSpectrumjPanel;
    // End of variables declaration//GEN-END:variables

    private List<SpectrumAnnotation> getAnnotatedPeaks() {
        List<SpectrumAnnotation> annotations = new ArrayList<SpectrumAnnotation>();
        int selectedRow = resultFilejTable.getSelectedRow();
        System.out.println(resultFilejTable.getValueAt(selectedRow, 23));
        String annotatedPeaksStr = (String) resultFilejTable.getValueAt(selectedRow, 23);
        String[] splittedAnnotatedPeaksStr = annotatedPeaksStr.split(" ");
        Color lightBlue = Color.getHSBColor(0.56f, 0.3f, 1f),
                lightPink = Color.getHSBColor(0.92f, 0.3f, 1f),
                lightYellow = Color.getHSBColor(0.16f, 0.4f, 1f),
                purple = Color.getHSBColor(0.76f, 0.4f, 1f),
                prussian_blue = new Color(0, 51, 102),
                navy_blue = new Color(0, 0, 102),
                midnight_blue = new Color(0, 0, 51),
                burnt_sienna = new Color(102, 0, 0),
                burnt_umber = new Color(51, 0, 0),
                kashmir_green = new Color(0, 51, 0),
                forest_green = new Color(0, 102, 0),
                selectedColor = null;
        for (String splittedAnnotatedPeak : splittedAnnotatedPeaksStr) {
            if (!splittedAnnotatedPeak.isEmpty()) {
                System.out.println(splittedAnnotatedPeak);
                String[] annotationInfo = splittedAnnotatedPeak.split("_");
                String chargeState = annotationInfo[0],
                        ionNameAndIndex = splittedAnnotatedPeak.substring(splittedAnnotatedPeak.indexOf("_") + 1, splittedAnnotatedPeak.lastIndexOf("_")),
                        mz = ((splittedAnnotatedPeak.substring(splittedAnnotatedPeak.lastIndexOf("_")).split("="))[1]).replace(",", "").replace("[", "").replace("]", "");
                String chInfo = "1+";
                if (chargeState.equals("doublyCharged")) {
                    chInfo = "2+";
                }
                ionNameAndIndex = "(" + ionNameAndIndex + ")" + chInfo;
                if (ionNameAndIndex.contains("lepA")) {
                    selectedColor = navy_blue;
                } else if (ionNameAndIndex.contains("lepB")) {
                    selectedColor = forest_green;
                } else if (ionNameAndIndex.contains("pepA")) {
                    selectedColor = midnight_blue;
                } else if (ionNameAndIndex.contains("pepB")) {
                    selectedColor = kashmir_green;
                }
                DefaultSpectrumAnnotation defaultSpecAn = new DefaultSpectrumAnnotation(
                        new Double(mz), // the mz value to annotate
                        0.5, // the mz error margin
                        selectedColor, // the annotation color
                        ionNameAndIndex); // the annotation label-like y1+
                annotations.add(defaultSpecAn);
            }
        }
        return annotations;
    }

}
