/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package theoretical;

/**
 * The enum class for a fragmentation mode.
 * Based on this only selected ion types are retrieved
 * 
 * @author Sule
 */
 public enum FragmentationMode {
    CID,// mostly b and y ions (b1 is rarely observed and b2 holds the highest intensity
    ETD,// mostly c and z ions
    HCD, // b- and (mostly) y- ions, as well as a-ions (fragmented from b ions)
    HCD_all // a- and x- ions and also b- and y- ions
    
}
