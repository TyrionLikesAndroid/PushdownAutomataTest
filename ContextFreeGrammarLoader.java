
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class ContextFreeGrammarLoader {

    private final String path;
    public Map<String,LinkedList<CFGWord>> grammarDictionary = new HashMap<>();
    private final String ruleDelimeter = "->";
    private final String wordDelimeter = ":";

    public ContextFreeGrammarLoader(String path) {
        this.path = path;
    }

    public Map<String,LinkedList<CFGWord>> getGrammarDictionary() {

        return grammarDictionary;
    }

    public String getPath()
    {
        return path;
    }

    public void printCFGRules()
    {
        Set<String> keys = grammarDictionary.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext())
        {
            String key = iter.next();
            System.out.println("Rule Variable=" + key);
            LinkedList<CFGWord> ruleList = grammarDictionary.get(key);
            Iterator<CFGWord> iter2 = ruleList.iterator();
            while(iter2.hasNext())
            {
                CFGWord key2 = iter2.next();
                System.out.println("Rule Word=" + key2.print());
            }
        }
    }

    public boolean load() {
        boolean out = false;
        File cfgFile = new File(path);
        if(cfgFile.exists()) {
            System.out.println("CFG file " + path + " exists");

            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(cfgFile), StandardCharsets.UTF_16));

                String cfgLine;
                while ((cfgLine = bufferedReader.readLine()) != null) {

                    // Parse the rule.  It will have a key value that should be a capitol letter and
                    // one or more rules to follow
                    //System.out.println(cfgLine);
                    int delimeterPos = cfgLine.indexOf(ruleDelimeter);
                    int NOT_FOUND = -1;
                    if(delimeterPos != NOT_FOUND)
                    {
                        String ruleVariable = cfgLine.substring(0,delimeterPos);
                        String ruleSet = cfgLine.substring(delimeterPos+ruleDelimeter.length());
                        //System.out.println("Rule variable=" + ruleVariable + " ruleSet=" + ruleSet);

                        // Create our linked list for the ruleset
                        LinkedList<CFGWord> ruleList = new LinkedList<>();

                        // See if this is a complex ruleset or a simple ruleset
                        int wordDelimeterPos = ruleSet.indexOf(wordDelimeter);
                        if(wordDelimeterPos != NOT_FOUND)
                        {
                            // This is a complex rule, chop it up by delimeter
                            //System.out.println("Complex Rule " + ruleSet);
                            String[] complexRules = ruleSet.split(wordDelimeter);
                            for(int i = 0; i < complexRules.length; i++)
                            {
                                //System.out.println("Delimeter= " + wordDelimeter + " Chunk=" + complexRules[i]);
                                ruleList.add(new CFGWord(complexRules[i]));
                            }
                        }
                        else
                        {
                            // This is a simple rule, so just add it to our linked list
                            ruleList.add(new CFGWord(ruleSet));
                        }

                        // See if we have this rule variable in our map
                        if(grammarDictionary.containsKey(ruleVariable))
                        {
                            // We already have this rule, which is fine because a rule can be spread across
                            // multiple lines.  Just add everything we parsed into this existing linked list
                            //System.out.println("Adding to existing rule " + ruleVariable);
                            grammarDictionary.get(ruleVariable).addAll(ruleList);
                        }
                        else
                        {
                            // This is the first use of this rule variable, so set the key with this
                            // linked list
                            //System.out.println("Adding new rule " + ruleVariable);
                            grammarDictionary.put(ruleVariable, ruleList);
                        }

                        out = true;
                    }
                }

                printCFGRules();
                bufferedReader.close();
            } catch (Exception e)
            {
                System.out.println("Error loading config file");
                e.printStackTrace();
            }
        }
        else
            System.out.println("CFG file " + path + " not found");

        return out;
    }
}