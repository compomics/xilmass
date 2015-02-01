/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

import crossLinker.type.BS3;
import crossLinker.type.DSS;
import crossLinker.type.EDC;
import crossLinker.type.GA;

/**
 *
 * @author Sule
 */
public class GetCrossLinker {

    public static CrossLinker getCrossLinker(String crossLinkerName, boolean isLabeled) throws Exception {
        CrossLinker linker = null;
        if (crossLinkerName.equals("EDC")) {
            linker = new EDC();
            if (isLabeled) {
                throw (new Exception("Using leabeled EDC is not supported, yet!"));
            }
        } else if (crossLinkerName.equals("BS3")) {
            linker = new BS3(isLabeled);
        } else if (crossLinkerName.equals("GA")) {
            linker = new GA();
            if (isLabeled) {
                throw (new Exception("Using leabeled EDC is not supported, yet!"));
            }
        } else if (crossLinkerName.equals("DSS")) {
            linker = new DSS(isLabeled);
        } else if (!crossLinkerName.equals("DSS")) {
            throw new Exception("Not supported cross linker! Choose between DSS/EDC/BS3 or GA! ");
        }
        return linker;
    }

}
