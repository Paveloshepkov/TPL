package Labs.GLC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContextFreeGrammar {
    private final List<String> nonTerminals;
    private final List<String> terminals;
    public Map<String, List<String>> productions;
    private final String startSymbol;
    public static final String EPSILON = "ε";

    public ContextFreeGrammar(List<String> terminals, List<String> nonTerminals, String startSymbol, Map<String, List<String>> productions) {
        this.terminals = terminals;
        this.nonTerminals =  nonTerminals;
        this.startSymbol = startSymbol;
        this.productions = productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    public boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    public List<String> getProductionRules(String nonTerminal) {
        if (productions.containsKey(nonTerminal)) {
            List<String> rules = productions.get(nonTerminal);
            if (rules.contains(EPSILON)) {
                rules = new ArrayList<>(rules);
                rules.remove(EPSILON);
                rules.add("");
            }
            return rules;
        }
        return Collections.emptyList();
    }

    public String findNextNonTerminal(String inputString) {
        for (int i = 0; i < inputString.length(); i++) {
            String symbol = inputString.substring(i, i + 1);
            if (isNonTerminal(symbol)) {
                return symbol;
            }
        }
        return null;
    }
}
