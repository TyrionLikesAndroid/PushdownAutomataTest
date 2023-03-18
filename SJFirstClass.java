public class SJFirstClass {

    public static void main(String[] args) {

        String cfg = (""), inString = ("");
        if(args.length > 0)
            cfg = args[0];
        if(args.length > 1)
            inString = args[1];

        System.out.println("Creating Pushdown Automata Test with CFG=" + cfg + " inString=" + inString);

        ContextFreeGrammarLoader cfgLoader = new ContextFreeGrammarLoader(cfg);
        if(! cfgLoader.load())
            System.out.println("Failure to load CFG= " + cfg);

        PushdownAutomaton pda = new PushdownAutomaton(cfgLoader);
        boolean output = pda.accept(inString);
        System.out.println("PDA accept for: " + inString + " returns: " + ((output) ? "true":"false"));
    }
}
