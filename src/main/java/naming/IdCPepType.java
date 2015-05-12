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
public enum IdCPepType {

    INTACT, // both peptides were linked and so there are ions from 4 arms of a cross linked peptides...
    
    LINEAR_PEPA, // Only ion series from PeptideA 
    LINEAR_PEPB, // Only ion series from PeptideB

    MONOLINKED_PEPA, // Ion series from one PeptideA linked but not attached to a peptideB-monolink
    MONOLINKED_PEPB, // Ion series from one PeptideB linked but not attached to a peptideA-monolink

    LEFT_U, // Ions series coming from only ions containing N-termini 
    RIGHT_U, // Ions series coming from only ions containing C-termini

    LEFT_CHAIR_PEPA, // Ion series containing N-termini from both peptides + ion series containing C-termini on PeptideA
    LEFT_CHAIR_PEPB, // Ion series containing N-termini from both peptides + ion series containing C-termini on PeptideB

    RIGHT_CHAIR_PEPA, // Ion series containing C-termini from both peptides + ion series containing N-termini on one peptide  on PeptideA
    RIGHT_CHAIR_PEPB, // Ion series containing C-termini from both peptides  + ion series containing N-termini on one peptide  on PeptideB

    LINEAR_NPEPA_CPEPB, // Ion series containing N-termini from PeptideA + C-termini from PeptideB
    LINEAR_NPEPB_CPEPA  // Ion series containing N-termini from PeptideB + C-termini from PeptideA
}
