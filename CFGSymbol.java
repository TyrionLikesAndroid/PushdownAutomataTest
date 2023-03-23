
public class CFGSymbol {

    public final char symbol;

    public CFGSymbol(char symbol) {

        //System.out.println("New CFGSymbol for " + symbol);
        this.symbol = symbol;

        //System.out.println(this.print() + " isTerminal(" + (this.isTerminal()?"T":"F") + ")" + " isEpsilon(" + (this.isEpsilon()?"T":"F") + ")");
    }

    public boolean isEpsilon() {

        return String.valueOf(symbol).equals("ε");
    }

    public boolean isEndOfString() {

        return String.valueOf(symbol).equals("$");
    }

    public boolean isTerminal() {

        return ! (Character.isAlphabetic(symbol) && Character.isUpperCase(symbol));
    }

    public String print() {
        return String.valueOf(symbol);
    }

}