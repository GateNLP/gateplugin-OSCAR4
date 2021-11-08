# Chemical Named Entity Recognizer

GATE PR that uses [OSCAR4](https://github.com/BlueObelisk/oscar4) to annotate documents

More details on OSCAR4, including a number of example texts, can be found in [this publication](http://dx.doi.org/10.1186/1758-2946-3-41). One simple example, from that paper would be _"The quick brown ethyl acetate jumps over the lazy bromine"_ which results in two annotations over _ethyl acetate_ and _bromine_.</p>

The tagger produces annotations of a single type, namely `ChemicalNE`. These annotations have the following features:

- *type:* the type of NE. Usually `compound`
- *Std_InChI:* The standard [International Chemical Identifier](https://en.wikipedia.org/wiki/International_Chemical_Identifier) for the recognised NE.
- *Std_InChiKey:* A hashed version of the Std_InChi key useful for web searches etc.
- *SMILES:* The named entity described using the [simplified molecular-input line-entry system (SMILES)](https://en.wikipedia.org/wiki/Simplified_molecular-input_line-entry_system)
