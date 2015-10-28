/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualize;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Sule
 */
public class SimilarityTableModel extends AbstractTableModel {    
       
    private String[] columnNames;
    private Object[][] data;
        

    public SimilarityTableModel(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }


    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
  
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
 
    public int getLeadSelectionIndex() {
        return -1;
    }

}
