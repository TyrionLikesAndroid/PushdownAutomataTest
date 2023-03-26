import java.util.*;

public class PDARuleProcessor {

    String inputString;
    int inputPosition;
    Map<String, LinkedList<CFGWord>> grammarDictionary;
    Stack<CFGSymbol> cfgStack;
    PushdownAutomaton parent;
    static private int startId = 0;
    public int instanceId;

    private enum EvalResult
    {
        TERMINAL_SUCCESS,
        TERMINAL_FAILURE,
        CONTINUE_NO_PROGRESS,
        CONTINUE_PROGRESS
    }

    public PDARuleProcessor(PushdownAutomaton parent, String input, int pos, Map<String, LinkedList<CFGWord>> dict, Stack<CFGSymbol> stack)
    {
        this.instanceId = startId++;
        this.parent = parent;
        this.inputString = input;
        this.inputPosition = pos;
        this.grammarDictionary = dict;
        this.cfgStack = stack;

        System.out.println("PDARuleProcessor[" + instanceId + "] constructed");
    }

    public void run()
    {
        System.out.println("PDARuleProcessor[" + instanceId + "] running");

        // Evaluate our current state, which will analyze the top of the stack and see if we
        // can make any progress with the input string.  We will continue as long as things progress
        EvalResult result = evaluate();
        while (result == EvalResult.CONTINUE_PROGRESS) { result = evaluate(); }

        // If we are done evaluating, the top of the stack is either a rule variable, or we have
        // reached an exit condition.  Handle the exit conditions first
        if(result == EvalResult.TERMINAL_SUCCESS)
        {
            System.out.println("Exiting because string was ACCEPTED and stack is empty");
            parent.recordResult(true, instanceId);
            return;
        }
        if(result == EvalResult.TERMINAL_FAILURE)
        {
            System.out.println("Exiting because of terminal failure condition");
            parent.recordResult(false, instanceId);
            return;
        }

        // There is still work to do, so peek the top of the stack and start looking for
        // a rule variable replacement
        CFGSymbol aSym = cfgStack.peek();
        //System.out.println("run() peek=" + aSym.print());

        LinkedList<CFGWord> rules = grammarDictionary.get(aSym.print());
        int numRules = rules.size();
        if(numRules == 0)
        {
            // Nothing to do, this RP is done, or we have a problem in the CFG
            System.out.println("Exiting because of CFG has no rule for " + aSym);
            parent.recordResult(false, instanceId);
        }
        else
        {
            // Pop the top of the stack since we are replacing this topmost rule
            CFGSymbol out = cfgStack.pop();
            //System.out.println("run() Popped " + out.print());

            // Iterate over all the rules and setup new RP classes
            for(int i = 0; i < rules.size(); i++)
            {
                System.out.println("Rules iteration " + i + ", "+ rules.get(i).print());
                // Make a copy of the stack
                Stack<CFGSymbol> newStack = cloneStack(cfgStack);
                pushStackRule(newStack, rules.get(i));

                //Make a new PDARuleProcessor and put it in the working list
                PDARuleProcessor newRp = new PDARuleProcessor(parent,inputString, inputPosition, grammarDictionary, newStack);
                parent.addWorker(newRp);
            }

            // Nothing left to do, this will be an incomplete worker but there is hope
            System.out.println("Exiting with valid child workers still in progress");
            parent.recordResult(false, instanceId);
        }
    }

    static public void pushStackRule(Stack<CFGSymbol> aStack, CFGWord cfgWord)
    {
        LinkedList<CFGSymbol> symbols = cfgWord.getSymbols();

        // Push the symbols on the stack in reverse order since we are doing this right-handed
        Iterator<CFGSymbol> rIter = symbols.descendingIterator();
        while(rIter.hasNext())
        {
            CFGSymbol in = rIter.next();
            aStack.push(in);
            System.out.println("Push Stack(" + aStack + ") Pushed " + in.print());
        }
    }

    private Stack<CFGSymbol> cloneStack(Stack<CFGSymbol> oldStack)
    {
        Stack<CFGSymbol> newStack = new Stack<>();
        System.out.println("Clone Old stack (" + oldStack + ") size = " + oldStack.size());

        for(int i = 0; i < oldStack.size(); i++)
        {
            //System.out.println("Clone oldStack[" + i + "]=" + oldStack.elementAt(i).print());
            CFGSymbol in = oldStack.elementAt(i);
            newStack.push(in);
            System.out.println("Clone Stack(" + newStack + ") Pushed " + in.print());
        }
        return newStack;
    }

    private EvalResult evaluate()
    {
        //Assume that no progress will be made unless indicated below
        EvalResult result = EvalResult.CONTINUE_NO_PROGRESS;

        // Look at the top of the stack
        CFGSymbol aSym = cfgStack.peek();
        //System.out.println("evaluate() peek=" + aSym.print());

        // Check for terminal cases first
        if(aSym.isEndOfString() && (inputPosition == inputString.length()))
        {
            // If we see end of string symbol on the stack AND we are indeed at the end
            // of the input string, we are successful
            System.out.println("**ACCEPTED**");
            result = EvalResult.TERMINAL_SUCCESS;
        }
        else if(inputPosition >= inputString.length())
        {
            // We still have some stuff on the stack, but we are out of input string.
            // This is a hard failure case with no rewind, so this path is done.
            System.out.println("**FAILURE** Input string has been exhausted");
            result = EvalResult.TERMINAL_FAILURE;
        }
        else if(aSym.isEpsilon())
        {
            // This should never happen, so send a failure and bail so we can debug it
            System.out.println("**FAILURE** This should never happen go and debug");
            result = EvalResult.TERMINAL_FAILURE;
        }
        else if(aSym.isTerminal())
        {
            // Compare our stack terminal to the top of the string since this is right-handed validation
            if(aSym.symbol == inputString.charAt(inputPosition))
            {
                System.out.println("MATCH stack top=" + aSym.symbol + " input char=" + inputString.charAt(inputPosition));

                // Pop the stack and move the position of the string up one
                CFGSymbol outSym = cfgStack.pop();
                //System.out.println("eval() Popped " + outSym.print());
                inputPosition++;

                // We made some progress on our input, so report appropriately
                result = EvalResult.CONTINUE_PROGRESS;
            }
            else
            {
                // The input string terminal doesn't match with our stack symbol.  We can't progress any farther
                System.out.println("**FAILURE** Mismatching terminals in stack/input");
                result = EvalResult.TERMINAL_FAILURE;
            }
        }

        return result;
    }
}
