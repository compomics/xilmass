/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import analyse.CXPSM.NameTargetDecoy;
import config.ConfigHolder;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class identify and then validate Xilmass results..
 *
 * @author Sule
 */
public class RunAndValidate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // First, identify a cross-linking sites
            Start.main(args);
            // Then, validate identified cross-linking sites
            // validateArgs[0] is the analysis option and needs to be 0 for Xilmass! 
            
            String[] validateArgs = {"0", 
                ConfigHolder.getInstance().getString("resultFolder"),
                ConfigHolder.getInstance().getString("scoringFunctionName"),
                ConfigHolder.getInstance().getString("tdfile"),
                ConfigHolder.getInstance().getString("allXPSMoutput")};
            NameTargetDecoy.main(validateArgs);

        } catch (IOException ex) {
            Logger.getLogger(RunAndValidate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RunAndValidate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(RunAndValidate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RunAndValidate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
