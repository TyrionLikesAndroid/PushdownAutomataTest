public class PDATestMain {

    public static void main(String[] args) {

        String cfg = "";
        String inString = "";

        if(args.length > 0)
            cfg = args[0];
        if(args.length > 1)
            inString = args[1];

        System.out.println("Creating Pushdown Automata Test with CFG=" + cfg + " inString=" + inString);

        // Try to load a grammar from the config file if we got a config string
        if(! cfg.isEmpty()) {
            ContextFreeGrammarLoader cfgLoader = new ContextFreeGrammarLoader(cfg);
            if (!cfgLoader.load())
                System.out.println("Failure to load CFG= " + cfg);

            // Try and parse the input string if we got one
            if(! inString.isEmpty()) {
                PushdownAutomaton pda = new PushdownAutomaton(cfgLoader);
                boolean output = pda.accept(inString);
                System.out.println("PDA accept for: " + inString + " returns: " + ((output) ? "true" : "false"));
            }
        }
    }
}
