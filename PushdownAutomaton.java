public class PushdownAutomaton {

    public PushdownAutomaton(String cfg)
    {
        System.out.println("Pushdown Automaton constructed for " + cfg);
    }

    public boolean accept(String inString)
    {
        // For now just return true if the string is not empty
        return ! (inString.isEmpty());
    }
}
