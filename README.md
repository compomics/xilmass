# Xilmass
 * [Tool Description](#tool-description)
 * [Project Description](#project-description)
 * [Citation](#citation)
 * [Downloads](#downloads)
 * [Usage](#usage)
  
---
##Tool description

Xilmass is a novel algorithm to identify cross-linked peptides. Xilmass uses a new way to compose the search database and then scores each experimental spectrum against complete theoretical spectra. Xilmass can handle for both labeled and unlabeled cross-linkers.

[Go to top of page](#xilmass)

----
## Project description

Chemical cross-linking coupled with mass spectrometry (XL-MS) plays an important role in unravelling protein interactions and supports the determination of protein structure. This technique enables the study of proteins that are non-crystallizable or have a protein size above 50 kDa. The identification of the resulting MS/MS spectra from XL-MS experimennt is a computationally challenging task. First, the search space is tremendously increased due to considering all peptide-to-peptide combinations. Second, these experimental MS/MS spectra are more complex than a MS/MS spectrum derived from a single peptide; because these spectra contain fragment ions from two peptides. Third, the peptide mixture contains only few cross-linked peptides compared to single peptides in the mixture. In addition, there is a high dynamic range of cross-linked products due to the fact that cross-linking can occur between residues of the same protein (intra-protein cross-linked peptides), between residues of different proteins (inter-protein cross-linked peptides), or even between two cross-linked peptides (higher order cross-liked peptides). Although several computational approaches are available for the identification of spectra from cross-linked peptides, there still remains a room for improvement. Some of the available approaches are based on the linearization of cross-linked peptide-pairs or the usage of peptide labeling to facilitate cross-linked peptide identification. Here, we introduce Xilmass, a novel algorithm to identify cross-linked peptides that uses a new way to construct the search database and scores each experimental spectrum against each of the complete theoretical spectra. 

Xilmass provides a user-friendly a graphical user interface (GUI) to run cross-linked peptides identifications. Additionally, it also offers the command line (CLI) version for the automation.


If you have any question or suggestion about Xilmass, please contact us immediately!


[Go to top of page](#xilmass)

----

# Citation

We are happy to annouce that Xilmass has been recently accepted! 

[Yılmaz et al.: Analytical Chemistry, 2016, 88 (20), pp 9949–9957 (DOI: 10.1021/acs.analchem.6b01585)](http://pubs.acs.org/doi/abs/10.1021/acs.analchem.6b01585)

The original data set that was used for the Xilmass publication is avaliable at PRIDE with the accession number of [PXD003880](https://www.ebi.ac.uk/pride/archive/projects/PXD003880).



If you perform your search to identify cross-linked peptides by Xilmass, please include the reference above. 

[Go to top of page](#xilmass)

----
## Downloads

Download the latest version of Xilmass algorithm <a href="http://genesis.ugent.be/maven2/com/compomics/xilmass/1.1.0/xilmass-1.1.0.zip" onclick="trackOutboundLink('usage','download','xilmass','http://genesis.ugent.be/maven2/com/compomics/xilmass/1.1.0/xilmass-1.1.0.zip'); return false;">here</a>.  

You can run Xilmass as CLI or our user-friendly GUI. 

----

## Usage
See the [wiki](https://github.com/compomics/xilmass/wiki).

----

| Java | Maven | Netbeans | Lucene |
|:--:|:--:|:--:|:--:|
|[![java](http://genesis.ugent.be/public_data/image/java.png)](http://java.com/en/) | [![maven](http://genesis.ugent.be/public_data/image/maven.png)](http://maven.apache.org/) | [![netbeans](https://netbeans.org/images_www/visual-guidelines/NB-logo-single.jpg)](https://netbeans.org/) | [![lucene](https://lucene.apache.org/images/lucene_logo_green_300.png)](https://lucene.apache.org/) |


[Go to top of page](#xilmass)
