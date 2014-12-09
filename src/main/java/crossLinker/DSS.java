/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

/**
 *
 * @author Sule
 */
public class DSS extends CrossLinker {

    public DSS() {
        this.name = CrossLinkerName.DSS;
        this.massDift = 138.0681;
//        this.massDift = 156.0786;
        this.type = CrossLinkerType.homobifunctional;
    }

}
