/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

/**
 * This enum class hold a cross linker type attribute.
 *
 * Either homobifunctional, so both ends of a linker agent are specific to the
 * same residue. Like DSS-K and K.
 *
 * Or hetereobifunctional, ends of a linker agents are specific to different
 * residues. Like EDC-K and D/E.
 *
 * @author Sule
 */
public enum CrossLinkerType {
    homobifunctional,
    heterobifunctional
}
