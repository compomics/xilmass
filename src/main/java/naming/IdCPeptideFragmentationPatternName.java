/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naming;

/**
 * This enum class contains names for a cross linked peptide
 *
 * @author Sule
 */
public enum IdCPeptideFragmentationPatternName {

    ALLOVER, // both peptides were linked and so there are ions from 4 arms of a cross linked peptides...
    
    LEFT_LINEAR_PEPA, // Only ion series from PeptideA on the left
    LEFT_LINEAR_PEPB, // Only ion series from PeptideB on the left
    RIGHT_LINEAR_PEPA, // Only ion series from PeptideA on the right
    RIGHT_LINEAR_PEPB, // Only ion series from PeptideB on the right
    
    LINEAR_PEPA, // Only ion series from PeptideA from both
    LINEAR_PEPB, // Only ion series from PeptideB from both
    
    ATTACHEDTOPEPA_FROM_NODEPEPB,// node from peptide B is linked to peptideA
    ATTACHEDTOPEPB_FROM_NODEPEPA,// node from peptide A is linked to peptideB
    
//    LINEAR_PEPA, // Only ion series from PeptideA 
//    LINEAR_PEPB, // Only ion series from PeptideB
    
    MONOLINKED_PEPA, // Ion series from one PeptideA linked but not attached to a peptideB-monolink
    MONOLINKED_PEPB, // Ion series from one PeptideB linked but not attached to a peptideA-monolink

    LEFT_U, // Ions series coming from only ions containing N-termini 
    RIGHT_U, // Ions series coming from only ions containing C-termini

    LEFT_CHAIR_PEPA, // Ion series containing N-termini from both peptides + ion series containing C-termini on PeptideA
    LEFT_CHAIR_PEPB, // Ion series containing N-termini from both peptides + ion series containing C-termini on PeptideB

    RIGHT_CHAIR_PEPA, // Ion series containing C-termini from both peptides + ion series containing N-termini on one peptide  on PeptideA
    RIGHT_CHAIR_PEPB, // Ion series containing C-termini from both peptides  + ion series containing N-termini on one peptide  on PeptideB

    LINEAR_NPEPA_CPEPB, // Ion series containing N-termini from PeptideA + C-termini from PeptideB
    LINEAR_NPEPB_CPEPA , // Ion series containing N-termini from PeptideB + C-termini from PeptideA
    
    LINK, // Ion containing only link between two linked positions
    SINGLE, // Only one ion found
    
    NODE_PEPA, // Ions derived from only linked amino acid(s) on PeptideA
    NODE_PEPB  // Ions derived from only linked amino acid(s) on PeptideB 
    
}
