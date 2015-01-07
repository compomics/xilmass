/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

/**
 * Disuccinimidylsuberate
 * C16H20N2O8
 * 
 * @author Sule
 */
public class DSS extends CrossLinker {

    public DSS() {
        this.name = CrossLinkerName.DSS;
        this.type = CrossLinkerType.homobifunctional;
        super.massShift_Type0 = 253;
        super.massShift_Type2 = 138.0681;
    }

}
