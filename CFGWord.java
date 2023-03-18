
import java.util.*;

public class CFGWord {

    private final String word;
    private final LinkedList<CFGSymbol> symbols = new LinkedList<>();

    public CFGWord(String word)
    {
        //System.out.println("New CFGWord for " + word);
        this.word = word;

        // Chop the inbound rule and turn it into symbols that we can use for the PDA
        for(int i = 0; i < word.length(); i++)
            symbols.add(new CFGSymbol(word.charAt(i)));
    }

    public LinkedList<CFGSymbol> getSymbols() {
        return symbols;
    }
    public String print() {
        return word;
    }

}