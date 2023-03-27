import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class PushdownAutomaton {

    ContextFreeGrammarLoader cfgLoader;
    LinkedList<PDARuleProcessor> activeWorkers = new LinkedList<>();
    LinkedList<PDAWorkerResult> finalResults = new LinkedList<>();

    public class PDAWorkerResult {
        public int id;
        public boolean result;
        LinkedList<String> derivationTree;

        public PDAWorkerResult(int id, boolean result, LinkedList<String> derivation)
        { this.id=id; this.result=result; this.derivationTree = derivation; }
    };

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

        // Iterate through the start rules and initialize a worker based on each one
        Iterator<ContextFreeGrammarLoader.CFGRule> startIter = cfgLoader.getStartRules().iterator();
        while(startIter.hasNext()){

            ContextFreeGrammarLoader.CFGRule aRule = startIter.next();

            // Create stack and push our start rule onto the stack with the $
            Stack<CFGSymbol> startingStack = new Stack<>();
            startingStack.push(new CFGSymbol('$'));
            PDARuleProcessor.pushStackRule(startingStack, aRule.word);
            System.out.println("Initial stack (" + startingStack + ")");

            // Start the derivation tree based on these first two steps
            LinkedList<String> derivation = new LinkedList<>();
            derivation.add(aRule.id);
            derivation.add(aRule.word.print());

            // Create a PDA Rule Processor and tell it to run
            addWorker(new PDARuleProcessor(this, inString, 0, cfgLoader.getGrammarDictionary(),
                    startingStack, derivation, ""));
        }

        // Start the execute loop
        execute();

        // Execution is complete, so review the results and return the summary answer
        return evaluateResults();
    }

    private void execute()
    {
        // Create a safety net.  Since this is a brute force PDA program, it can definitely run
        // in a loop for a number of languages.  Cap the worker execution at 2000 iterations for now.
        int safetyNet = 2000;
        int safetyCounter = 1;

        // Run loop for all of the active workers
        while(activeWorkers.size() > 0)
        {
            PDARuleProcessor aWorker = activeWorkers.remove();
            aWorker.run();
            safetyCounter++;

            if(safetyCounter > safetyNet)
            {
                System.out.println("Exiting due to safety net");
                return;
            }
        }
    }

    protected void recordResult(boolean acceptResult, int id, LinkedList<String> derivation)
    {
        finalResults.add(new PDAWorkerResult(id, acceptResult, derivation));
    }

    private boolean evaluateResults()
    {
        boolean output = false;

        // Review the results and see if any of them were successful
        Iterator<PDAWorkerResult> it = finalResults.iterator();
        while(it.hasNext())
        {
            PDAWorkerResult workerResult = it.next();
            //System.out.println("PDARuleProcessor[" + workerResult.id + "] result=" + (workerResult.result?"T":"F"));

            if(workerResult.result)
            {
                System.out.println("Successful Derivation from PDARuleProcessor[" + workerResult.id + "]");
                printDerivationTree(workerResult.derivationTree);

                if(output)
                    System.out.println("This is an ambiguous grammar since there are multiple valid derivations");
                else
                    output = true;
            }
        }
        return output;
    }

    private void printDerivationTree(LinkedList<String> dTree)
    {
        Iterator<String> deriveIter = dTree.iterator();
        while(deriveIter.hasNext()) {
            System.out.println("Derivation Step = " + deriveIter.next());
        }
    }
}
