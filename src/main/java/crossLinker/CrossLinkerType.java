/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

/**
 * This enum class hold a cross linker type.
 *
 * AMINE_TO_AMINE is a homobifunctional including NHS-ester crosslinkers such as DSS, BS3 and also imidoester. * 
 * Target groups: primary amine (lysine (K) and n-termini) 
 * 
 * AMINE_TO_SULFHYDRYL is hetereobifunctional including NHS-Haloacetyl Crosslinkers such as SID, NHS-Maleimide and NHS-Pyridyldithiol Crosslinkers
 * Target groups: primary amine (lysine (K)) and sulfhydryl (cysteine (C)) groups in proteins and other molecules.
 * 
 * CARBOXYL_TO_AMINE is another hetereobifunctional including EDC, DCC and NHS.
 * Target groups: carboxyl groups (glutamate (E), aspartate (D), C-termini) to primary amines (lysine, N-termini).
 * 
 * SULFHYDRYL_TO_CARBOHYDRATE is also hetereobifunctional including BMPH, EMCH, MPBH.
 * Target groups: sulfhydryl (cysteine (C)) and aldehyde (oxidized glycoprotein carbohydrate) groups
 * 
 * SULFHYDRYL_TO_SULFHYDRYL is again hetereobifunctional including BMPEGn cross-linkers
 * Target groups: protein and peptide thiols (reduced cysteines)
 *
 * @author Sule
 */
public enum CrossLinkerType {
    AMINE_TO_AMINE,
    AMINE_TO_SULFHYDRYL,    
    CARBOXYL_TO_AMINE,
    SULFHYDRYL_TO_CARBOHYDRATE,
    SULFHYDRYL_TO_SULFHYDRYL    
}
