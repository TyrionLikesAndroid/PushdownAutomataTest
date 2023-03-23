import java.util.*;

public class PDARuleProcessor {

    String inputString;
    int inputPosition;
    Map<String, LinkedList<CFGWord>> grammarDictionary;
    Stack<CFGSymbol> cfgStack;
    PushdownAutomaton parent;
    static private int startId = 0;
    public int instanceId;

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

        // Evaluate the top of the stack and see if we can make any progress on our input string
        boolean evalResult = evaluate();
        while (evalResult) { evalResult = evaluate(); }

        // If we are done evaluating, the top of the stack is either a rule variable
        // or something we can't match. See if we have any rules that match this variable
        CFGSymbol aSym = cfgStack.peek();
        //System.out.println("run() peek=" + aSym.print());

        // Check all the exit conditions and bail out if needed
        if(aSym.isEndOfString() && (inputPosition == inputString.length()))
        {
            System.out.println("Bailing out of run() because string was ACCEPTED and stack is empty");
            return;
        }
        if(inputPosition >= inputString.length())
        {
            System.out.println("Bailing out of run() because input string is exhausted");
            return;
        }
        if(aSym.isTerminal())
        {
            System.out.println("Bailing from run() due to unhandled terminal [" + aSym.print() + "] nextInput[" + inputString.charAt(inputPosition) + "]");
            return;
        }

        // If we get here, the peek identified a variable
        LinkedList<CFGWord> rules = grammarDictionary.get(aSym.print());
        int numRules = rules.size();

        if(numRules == 0)
        {
            // Nothing to do, this RP is done, or we have a problem in the CFG
            System.out.println("No rules associated with " + aSym);
        } else
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
        }
    }

    private void pushStackRule(Stack<CFGSymbol> aStack, CFGWord cfgWord)
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

    private boolean evaluate()
    {
        //Look at the top of the stack
        boolean out = false;
        CFGSymbol aSym = cfgStack.peek();
        //System.out.println("evaluate() peek=" + aSym.print());

        // Check and see if the string is empty, which means we have failed and have to bail
        if(aSym.isEndOfString() && (inputPosition == inputString.length()))
        {
            System.out.println("**ACCEPTED**");
        }
        else if(inputPosition >= inputString.length())
        {
            System.out.println("**FAILURE** Input string has been exhausted");
        }
        else if(aSym.isEpsilon())
        {
            System.out.println("TO DO: Need to write epsilon case");
        } else if(aSym.isTerminal())
        {
            // Compare our terminal to the top of the string since this is right-handed validation
            if(aSym.symbol == inputString.charAt(inputPosition))
            {
                System.out.println("MATCH stack top=" + aSym.symbol + " input char=" + inputString.charAt(inputPosition));

                // Pop the stack and move the position of the string up one
                CFGSymbol outSym = cfgStack.pop();
                //System.out.println("eval() Popped " + outSym.print());
                inputPosition++;
                out = true;
            }
        }

        if(! out)
            System.out.println("Evaluating input, nothing able to progress");

        return out;
    }
}
