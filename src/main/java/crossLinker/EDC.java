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
public class EDC extends CrossLinker {

    public EDC() {
        super.name = CrossLinkerName.EDC;
        super.type = CrossLinkerType.heterobifunctional;
        super.massDift = 0;
    }

}
