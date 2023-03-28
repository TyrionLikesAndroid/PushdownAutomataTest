 Example of a context free grammar (CFG) parser and a pushdown automaton (PDA).  Run the main class with
 varArgs to input the grammar file name and the test string for the PDA.  Output will indicate if the test
 string is accepted, print the derivation tree, and indicate whether the grammar is ambiguous.

 Test examples that are provided:
 - "G1.cfg" "00000#11111"  -> single derivation, easy grammar
 - "G2.cfg" "(a+a+a)"      -> single derivation, more complex grammar
 - "G3.cfg" "a+axa"        -> multiple derivations, ambiguous grammar
 - "G4.cfg" "1010110101"   -> single derivation, uses epsilon
 - "G5.cfg" "a+axa"        -> same as G3 but in Chomsky Normal Form