/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

/**
 *
 * @author Sule
 */
public enum CrossLinkingType {

    MONOLINK, //Type0 - one peptide was only linked to linker(dead-end/monoderivatization) - reflect accessibility/ peptides with hydrolyzed cross linker
    LOOPLINK, //Type1 - a linker was bound to both side on one peptide (self-loop/cyclic/intra-peptide)/internally bridged- information on local structure as alpha-helical regions
    CROSSLINK //Type2 - both peptides were linked together
}
