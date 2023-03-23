import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class PushdownAutomaton {

    ContextFreeGrammarLoader cfgLoader;
    LinkedList<PDARuleProcessor> activeWorkers = new LinkedList<>();

    public PushdownAutomaton(ContextFreeGrammarLoader loader)
    {
        this.cfgLoader = loader;
        System.out.println("Pushdown Automaton constructed for " + cfgLoader.getPath());
    }

    public void addWorker(PDARuleProcessor rp)
    {
        activeWorkers.add(rp);
    }

    public boolean accept(String inString)
    {
        // Confirm that our input string is all terminals.  We will not verify language here, that's just going
        // to have to be right or we can add that later if we have a way to feed the alphabet
        for(int i = 0; i < inString.length(); i++)
        {
            char aChar = inString.charAt(i);
            if(Character.isAlphabetic(aChar) && Character.isUpperCase(aChar))
            {
                System.out.println("Input string is not valid - capital letters.");
                return false;
            }
        }

        // Create stack and push our start rule onto the stack with the $
        Stack<CFGSymbol> startingStack = new Stack<>();
        startingStack.push(new CFGSymbol('$'));
        startingStack.push(new CFGSymbol(cfgLoader.getStartRule().id.charAt(0)));

        System.out.println("Initial stack (" + startingStack + ")");

        // Create a PDA Rule Processor and tell it to run
        addWorker(new PDARuleProcessor(this, inString,0,cfgLoader.getGrammarDictionary(),startingStack));
        execute();

        return true;  // For now
    }

    private void execute()
    {
        while(activeWorkers.size() > 0)
        {
            PDARuleProcessor aWorker = activeWorkers.remove();
            aWorker.run();
        }
    }
}
