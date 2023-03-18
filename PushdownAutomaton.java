import java.util.LinkedList;
import java.util.Map;

public class PushdownAutomaton {

    ContextFreeGrammarLoader cfgLoader;

    public PushdownAutomaton(ContextFreeGrammarLoader loader)
    {
        this.cfgLoader = loader;
        System.out.println("Pushdown Automaton constructed for " + cfgLoader.getPath());
    }

    public boolean accept(String inString)
    {
        // For now just return true if the string is not empty
        return ! (inString.isEmpty());
    }
}
