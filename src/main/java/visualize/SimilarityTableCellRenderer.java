/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualize;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Sule
 */
public class SimilarityTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus,
                row, column);   
        c.setBackground(Color.WHITE);
        c.setForeground(Color.BLACK);
        // if a cell is selected, highlight a row 
        if (isSelected) {
            c.setBackground(Color.GRAY);
            c.setForeground(Color.WHITE);
        }
        return c;
    }
}
