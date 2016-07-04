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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class reads a given Xilmass output file (which is set on StartDialog)
 * Then, it fills a table with Xilmass outcomes. An experimental spectrum with
 * all annotated peaks are firstly shown on spectrum panel (for the first
 * selected row on the output file). Then, any other row can be selected to
 * visually see annotation of spectra based on Xilmass results..
 *
 * @author Sule
 */
public final class Visualize extends javax.swing.JFrame {

    private StartDialog startDialog;
    private MSnSpectrum original_spec = new MSnSpectrum();
    private String specsFolder = "";
    private SpectrumPanel spectrumPanel;
    private String[] columnNames = null;
    private File resultFile;
    private boolean isOpenFileMenu = false;
    private SpectrumFactory spFct = SpectrumFactory.getInstance();
    private int indexOfAnnotatedPeaks = 30;
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
        if (!startDialog.getSpecFolder().isEmpty()) {
            initComponents();
            indexOfAnnotatedPeaks = startDialog.getIndexOfAnnotatedPeaks() + 1;
            setSpecsFolder(startDialog.getSpecFolder());
            start_visualization();
            this.setVisible(true);
        } else {
            System.exit(0);
        }
    }

    public String getSpecsFolder() {
        return specsFolder;
    }

    public void setSpecsFolder(String specsFolder) {
        this.specsFolder = specsFolder;
    }

    /**
     * This method reads a given Xilmass result file to put into
     * ArrayList<String[]>
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<String[]> parseXilmassOutput(File file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
        String line = null;
        int control = 0;
        ArrayList<String[]> dataStrArr = new ArrayList<String[]>();
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty() && !line.startsWith("Xilmass")) {
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
                    dataStrArr.add(split);
                }
                control++;
            }
        }
        return dataStrArr;
    }

    /**
     * This method reads a given Xilmass output then prepares the table with all
     * Xilmass results
     *
     * @param dataStrArr
     * @param sorter
     */
    private void prepareResultFileJTable(ArrayList<String[]> dataStrArr, TableRowSorter sorter) {
        // change the table header as bold and slightly bigger
        resultFilejTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        // enable scrolling
        resultFilejScrollPane.setAutoscrolls(true);
        // fill values from dataStrArr to a resultFileJTable..
        fillResultFileJTable(dataStrArr);
        // now adjust table columns
        resultFilejTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int totalWidth = 0; // to set a size of a scrol pane
        for (int column = 0; column < resultFilejTable.getColumnCount(); column++) {
            TableColumn tableColumn = resultFilejTable.getColumnModel().getColumn(column);
            int maxWidth = tableColumn.getMaxWidth();
            // get the actual fit header on the table.
            int headerWidth = getHeaderWidth(tableColumn, column);
            // set a header as preferedWidth which will be later set based on the values on the table
            // this setting will allow finding the most appropriate width for a given column
            int preferedWidth = headerWidth;
            for (int row = 0; row < resultFilejTable.getRowCount(); row++) {
                TableCellRenderer cellRenderer = resultFilejTable.getCellRenderer(row, column);
                Component c = resultFilejTable.prepareRenderer(cellRenderer, row, column);
                // intercellspacing is bit bigger to allow slightly more space on the cell
                int width = c.getPreferredSize().width + (10 * resultFilejTable.getIntercellSpacing().width);
                preferedWidth = Math.max(preferedWidth, width);
                // some cells are super big, setting to maximum 800 will make the table virtually nicer.
                if (preferedWidth >= maxWidth && preferedWidth < 800) {
                    preferedWidth = maxWidth;
                    break;
                } else if (preferedWidth > 800) {
                    preferedWidth = 800;
                    break;
                }
            }
            // update column widht
            tableColumn.setPreferredWidth(preferedWidth);
            totalWidth += preferedWidth;
        }
        resultFilejTable.setRowSorter(sorter);
        resultFilejTable.setAutoCreateColumnsFromModel(true);
        // select an entire row by clicking
        resultFilejTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        resultFilejTable.setRowSelectionAllowed(true);
        resultFilejTable.setColumnSelectionAllowed(false);
        // select the first row..
        resultFilejTable.setRowSelectionInterval(0, 0);
        // update size to enable seeing a scrollbar..
        Dimension size = new Dimension(totalWidth, resultFilejTable.getRowCount() * resultFilejTable.getRowHeight());
        resultFilejTable.setPreferredSize(size);
        resultFilejTable.setPreferredScrollableViewportSize(size);
    }

    /**
     * To fill resultFileJTable from a given dataStrArr, which were parsed from
     * Xilmass output
     *
     * @param dataStrArr
     */
    private void fillResultFileJTable(ArrayList<String[]> dataStrArr) {
        // Fill information on data.
        int number = 0;
        for (int arr = 0; arr < dataStrArr.size(); arr++) {
            String[] strArr = dataStrArr.get(arr);
            if (!strArr[0].equals(strArr[1])) {
                number++;
                resultFilejTable.setValueAt(number, arr, 0);
                for (int i = 0; i < strArr.length; i++) {
                    String tmp = strArr[i];
                    try {
                        Double d = Double.parseDouble(tmp);
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formatted = df.format(d);
                        resultFilejTable.setValueAt(formatted, arr, i + 1);
                    } catch (NumberFormatException e) {
                        resultFilejTable.setValueAt(tmp, arr, i + 1);
                    }
                }
            }
        }
    }

    /**
     * This method returns a width of a header of given table column
     *
     * @param tableColumn
     * @param column
     * @return
     */
    private int getHeaderWidth(TableColumn tableColumn, int column) {
        // arrange header..
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = resultFilejTable.getTableHeader().getDefaultRenderer();
        }
        Component component = renderer.getTableCellRendererComponent(resultFilejTable,
                tableColumn.getHeaderValue(), false, false, -1, column);
        int header_width = component.getPreferredSize().width + (10 * (resultFilejTable.getIntercellSpacing().width));
        return header_width;
    }

    /**
     * This method starts visualization with preparing Xilmass output on a table
     * and then shows the first spectrum with annotated peask
     *
     *
     * @throws MzMLUnmarshallerException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     */
    public void start_visualization() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        if (!isOpenFileMenu) {
            resultFile = new File(startDialog.getPathToScoreFilejTextField().getText());
        }
        try {
            // parse Xilmass output
            ArrayList<String[]> dataStrArr = parseXilmassOutput(resultFile);
            int row_number = dataStrArr.size();
            // construct a 2D array for TableModel
            Object[][] data = new Object[row_number][columnNames.length];
            // Prepare similarity table model
            SimilarityTableModel similarityTableModel = new SimilarityTableModel(columnNames, data);
            resultFilejTable.setModel(similarityTableModel);
            // prepare a tableRowSorter
            TableRowSorter sorter = prepareTableRowSorter();
            prepareResultFileJTable(dataStrArr, sorter);
            LOGGER.log(Level.INFO, "Plotted spectra {0}\t{1}", new Object[]{resultFilejTable.getValueAt(1, 1), resultFilejTable.getValueAt(1, 2)});
            String spectrumFileName = resultFilejTable.getValueAt(1, 1).toString(),
                    spectrumTitle = resultFilejTable.getValueAt(1, 2).toString();
            setOriginalSpectrumForPlotting(spectrumFileName, spectrumTitle);
            annotateSpectrum();
        } catch (IOException ex) {
            LOGGER.info("Something went wrong while start up. Check either your spectrum folders or your score file!");
        }
    }

    /**
     * This sorter enables sorting a selected column. If values are double, sort
     * double values; otherwise it sorts them as String
     *
     * @return
     */
    private TableRowSorter prepareTableRowSorter() {
        TableRowSorter sorter = new TableRowSorter(resultFilejTable.getModel()) {
            @Override
            public Comparator getComparator(int column) {
                Comparator<String> comparator = new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        try {
                            Double o1_integer = Double.parseDouble(o1.toString()),
                                    o2_integer = Double.parseDouble(o2.toString());
                            return (o1_integer.compareTo(o2_integer));
                        } catch (NumberFormatException e) {
                            return o1.compareTo(o2);
                        }
                    }
                };
                return comparator;
            }
        };
        return sorter;
    }

    /**
     * This method prepares a spectrum for plotting from a folder containing
     * spectra.
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
        LOGGER.log(Level.INFO, "SpectrumFileName={0}" + "\t" + "SpectrumTitle={1}", new Object[]{spectrumFileName, spectrumTitle});
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
     * This method finds a spectrum according to a title of a spectrum and a
     * name of a spectrum file while searching in the given folder containing
     * all spectum files
     *
     * @param spectraFolder folder containing all spectrum files
     * @param spectrumFileName
     * @param spectrumTitle
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
        jMenuBar = new javax.swing.JMenuBar();
        jMenu = new javax.swing.JMenu();
        saveImagejMenuItem = new javax.swing.JMenuItem();
        exitjMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cross-linked peptide visualization");

        visualizeSpectrumjPanel.setBackground(new java.awt.Color(255, 255, 255));
        java.awt.GridBagLayout visualizeSpectrumjPanelLayout = new java.awt.GridBagLayout();
        visualizeSpectrumjPanelLayout.columnWidths = new int[] {200};
        visualizeSpectrumjPanelLayout.rowHeights = new int[] {200};
        visualizeSpectrumjPanelLayout.columnWeights = new double[] {200.0};
        visualizeSpectrumjPanelLayout.rowWeights = new double[] {2000.0};
        visualizeSpectrumjPanel.setLayout(visualizeSpectrumjPanelLayout);

        resultFilejScrollPane.setMaximumSize(new java.awt.Dimension(3, 3));
        resultFilejScrollPane.setMinimumSize(new java.awt.Dimension(2, 2));
        resultFilejScrollPane.setPreferredSize(new java.awt.Dimension(10, 10));

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
        resultFilejTable.setPreferredSize(new java.awt.Dimension(300, 200));
        resultFilejTable.getTableHeader().setReorderingAllowed(false);
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

        jMenuBar.setBorder(null);

        jMenu.setText("Menu");
        jMenu.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        saveImagejMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        saveImagejMenuItem.setText("Save image");
        saveImagejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImagejMenuItemActionPerformed(evt);
            }
        });
        jMenu.add(saveImagejMenuItem);

        exitjMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
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
                    .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                    .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualizeSpectrumjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(resultFilejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
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

    private void exitjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitjMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitjMenuItemActionPerformed

    private void saveImagejMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImagejMenuItemActionPerformed
        // Now save image
        JFileChooser savePlaylistDialog = new JFileChooser();
        int status = savePlaylistDialog.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File savePlaylist = savePlaylistDialog.getSelectedFile();
            BufferedImage bImg = new BufferedImage(visualizeSpectrumjPanel.getWidth(), visualizeSpectrumjPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D cg = bImg.createGraphics();
            visualizeSpectrumjPanel.paintAll(cg);
            try {
                if (ImageIO.write(bImg, "png", new File(savePlaylist + ".png"))) {
                    LOGGER.info("-- saved");
                }
            } catch (IOException e) {
                LOGGER.info(e.toString());
            }
        }
    }//GEN-LAST:event_saveImagejMenuItemActionPerformed

    private void resultFilejTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_resultFilejTableKeyReleased
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
    }//GEN-LAST:event_resultFilejTableKeyReleased

    /**
     * This method annotates theoretical peaks onto a given experimental
     * spectrum and then onto a visualizeSpectrumjPanel
     *
     */
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
        LOGGER.info("Annotated peaks=" + peakAnnotation.size());
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitjMenuItem;
    private javax.swing.JMenu jMenu;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JScrollPane resultFilejScrollPane;
    private javax.swing.JTable resultFilejTable;
    private javax.swing.JMenuItem saveImagejMenuItem;
    private javax.swing.JPanel visualizeSpectrumjPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * This method returns a list of spectrum annotation from a selected row on
     * the result file
     *
     * @return
     */
    private List<SpectrumAnnotation> getAnnotatedPeaks() {
        List<SpectrumAnnotation> annotations = new ArrayList<SpectrumAnnotation>();
        int selectedRow = resultFilejTable.getSelectedRow();
        String annotatedPeaksStr = (String) resultFilejTable.getValueAt(selectedRow, indexOfAnnotatedPeaks);
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
            System.out.println(splittedAnnotatedPeak);
            if (!splittedAnnotatedPeak.isEmpty()) {
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
