
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

    public boolean isEpsilon() {
        return symbols.peekFirst().isEpsilon();
    }

    public Map<String, Integer> getTerminalCount()
    {
        Map<String, Integer> out = new HashMap<>();
        Iterator<CFGSymbol> iter = symbols.iterator();
        while(iter.hasNext())
        {
            CFGSymbol aSym = iter.next();
            if(aSym.isTerminal() && (! aSym.isEpsilon()))
            {
                int count = 0;
                if(out.containsKey(aSym.print()))
                    count = out.get(aSym.print());

                out.put(aSym.print(), ++count);
            }
        }
        return out;
    }

}