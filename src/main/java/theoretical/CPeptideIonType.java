/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package theoretical;

/**
 * It shows which ion type is on a crossLinkedPeptide
 
 Example: RLGK*PGS|AI*KN
 
 PeptideFragmentIons - RLGKPGS and RGWAKAM 
 Linker - DSS/EDC orBS3..
 * LinkedPeptideBackbone_PepA - AI*KN would yield ions of cK, IK, AIK, KN, IKN, AIKN
 * 
 * @author Sule
 */
public enum CPeptideIonType {    
    Backbone,
    Backbone_PepA,
    Backbone_PepB,
    NeutralLoss,
    NeutralLoss_Backbone_PepA,
    NeutralLoss_Backbone_PepB,
    Linker,
    LinkedPeptideBackbone_PepA,
    LinkedPeptideBackbone_PepB
}
