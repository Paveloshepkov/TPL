package Labs.GLC;

import java.util.ArrayList;
import java.util.List;

public class Generator {
    private final int minLength;
    private final int maxLength;
    private final boolean isLeftInference;
    private ContextFreeGrammar grammar;
    List<String> generatedStrings = new ArrayList<>();

    public Generator(int minLength, int maxLength, boolean isLeftInference) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.isLeftInference = isLeftInference;
    }

    public boolean isLeftInference() {
        return isLeftInference;
    }

    public List<String> generateStrings(ContextFreeGrammar _grammar) {
        grammar = _grammar;
        String startSymbol = grammar.getStartSymbol();
        generatedStrings.clear();
        if (isLeftInference()) {
            generateStringRecursiveLeft(startSymbol, startSymbol);
        } else {
            generateStringRecursiveRight(startSymbol, startSymbol);
        }
        return generatedStrings;
    }

    private void generateStringRecursiveLeft(String symbol, String currentString) {
        if (currentString.length() >= minLength && currentString.length() <= maxLength) {
            if (containsJustTerminals(currentString) && !generatedStrings.contains(currentString)) {
                generatedStrings.add(currentString);
            }
        }

        if (currentString.length() > maxLength) {
            return;
        }

        if (grammar.isNonTerminal(symbol)) {
            List<String> productionRules = grammar.getProductionRules(symbol);
            boolean replacementMade = false;
            for (String rule : productionRules) {
                if (rule.equals(ContextFreeGrammar.EPSILON)) {
                    String newString = currentString.replaceFirst(symbol, "");
                    generateStringRecursiveLeft(symbol, newString);
                } else {
                    String newString = currentString.replaceFirst(symbol, rule);

                    if (!newString.equals(currentString)) {
                        generateStringRecursiveLeft(symbol, newString);
                        replacementMade = true;
                    }
                }
            }

            if (!replacementMade) {
                String nextNonTerminal = grammar.findNextNonTerminal(currentString);
                if (nextNonTerminal != null) {
                    generateStringRecursiveLeft(nextNonTerminal, currentString);
                }
            }
        }
    }

    private void generateStringRecursiveRight(String symbol, String currentString) {
        if (currentString.length() >= minLength && currentString.length() <= maxLength) {
            if (containsJustTerminals(currentString) && !generatedStrings.contains(currentString)) {
                generatedStrings.add(currentString);
            }
        }

        if (currentString.length() > maxLength) {
            return;
        }

        if (grammar.isNonTerminal(symbol)) {
            List<String> productionRules = grammar.getProductionRules(symbol);
            boolean replacementMade = false;

            int lastIndex = currentString.lastIndexOf(symbol);

            if (lastIndex >= 0) {
                for (String rule : productionRules) {
                    String newString;
                    if (rule.equals(ContextFreeGrammar.EPSILON)) {
                        newString = currentString.substring(0, lastIndex) + currentString.substring(lastIndex + 1);

                    } else {
                        newString = currentString.substring(0, lastIndex) + rule + currentString.substring(lastIndex + 1);

                    }
                    if (!newString.equals(currentString)) {
                        generateStringRecursiveRight(symbol, newString);
                        replacementMade = true;
                    }
                }
            }

            if (!replacementMade) {
                String nextNonTerminal = grammar.findNextNonTerminal(currentString);
                if (nextNonTerminal != null) {
                    generateStringRecursiveRight(nextNonTerminal, currentString);
                }
            }
        }
    }

    private boolean containsJustTerminals(String currentString) {
        for (int i = 0; i < currentString.length(); i++) {
            char symbol = currentString.charAt(i);
            if (!grammar.isTerminal(String.valueOf(symbol))) {
                return false;
            }
        }
        return true;
    }

}