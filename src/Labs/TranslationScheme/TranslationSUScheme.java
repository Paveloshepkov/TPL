package Labs.TranslationScheme;
import java.util.*;
import java.util.Map.Entry;

public class TranslationSUScheme {
    private final List<String> nonTerminal;
    private final List<String> inputAlphabet;
    private final List<String> outputAlphabet;
    private final TreeMap<String, List<String>> rules;
    private final List<String> result = new ArrayList<>();
    private final String startSymbol;
    private int index = 0;
    private int length = 0;
    private int status = 0;
    private final String string;
    private String string1;
    private String string2 = "";

    public TranslationSUScheme(List<String> _nonTerminal, List<String> _inputAlphabet, List<String> _outputAlphabet, TreeMap<String, List<String>> _rules, String _startSymbol, String _string) {
        this.nonTerminal = _nonTerminal;
        this.inputAlphabet = _inputAlphabet;
        this.outputAlphabet = _outputAlphabet;
        this.rules = _rules;
        this.startSymbol = _startSymbol;
        this.string = _string;
        string1 = "";
    }

    public List<String> getResult() {
        findStartSymbolIndex();
        this.length = this.string.length();
        this.string1 = nonTerminal.get(index);
        this.string2 = nonTerminal.get(index);
        int i = ruleApply();
        resultMessage(i);

        return result;
    }

    private void findStartSymbolIndex() {
        index = -1;
        int currentIndex = 0;

        for (Entry<String, List<String>> entry : rules.entrySet()) {
            String key = entry.getKey();
            if (key.equals(startSymbol)) {
                index = currentIndex;
                break;
            }
            currentIndex++;
        }
    }

    private void resultMessage(int status) {
        switch (status) {
            case MEM_ALLOC_ERR -> result.add("Memory allocation error.");
            case UNDEFINED_SYMBOL -> result.add("Undefined symbol.");
            case GEN_END -> result.add("String cannot be constructed according to the input grammar rules.");
            case RULE_ERROR -> result.add("Error in defining rules.");
            case BAD_STRING -> result.add("String does not belong to the source language.");
            case SUCCESS -> result.add("Translation completed successfully!");
        }
    }
    String pNonTerm1 = "";
    String pNonTerm2 ="";
    String pRest1 = "", pRest2 = "";
    public int ruleApply() {
        int i1, i2;
        if (string1.length() > length) {
            return MAX_LEN_EXCESS;
        }

        i1 = nonTerminal1Search(string1);
        if (i1 == UNDEFINED_SYMBOL) {
            return i1;
        }
        i2 = nonTerminal2Search(string2);
        if (i2 == UNDEFINED_SYMBOL) {
            return i2;
        }
        if (i1 != i2) {
            return RULE_ERROR;
        }

        if (i1 == index) {
            if (string.equals(string1)) {
                result.add(string1 + ", " + string2);
                return SUCCESS;
            } else {
                return GEN_END;
            }
        } else {
            String buf1 = string1;
            String buf2 = string2;
            List<String> currentRules = rules.get(nonTerminal.get(i1));

            for (String pRule : currentRules) {
                if (pRule.equals("lam,lam")) {
                    pRule = ",";
                }
                String[] pRuleParts = pRule.split(",");
                pNonTerm1 = pNonTerm1.replaceFirst(",", pRuleParts[0]);
                pNonTerm1 = pNonTerm1 + pRest1;

                pNonTerm2 = pNonTerm2.replaceFirst(",", pRuleParts[1]);
                string2 = string2 + pRest2;

                status = ruleApply();
                if (status == UNDEFINED_SYMBOL || status == MEM_ALLOC_ERR || status == RULE_ERROR) {
                    return status;
                }

                string1 = buf1;
                string2 = buf2;

                if (status == SUCCESS) {
                    result.add(string1 + ", " + string2);
                    break;
                }
            }
        }
        return status;
    }

    int symbolIdentify1(String str) {
        int ind = UNDEFINED_SYMBOL;
        for (int i = 0; i < str.length(); i++) {
            String symbol = str.substring(0, i + 1);
            if (inputAlphabet.contains(symbol)) {
                ind = i;
                pNonTerm1 = str.substring(i + 1);
            }
        }
        return ind;
    }

    int symbolIdentify2(String str) {
        int ind = UNDEFINED_SYMBOL;
        for (int i = 0; i < str.length(); i++) {
            String symbol = str.substring(0, i + 1);
            if (outputAlphabet.contains(symbol)) {
                ind = i;
                pNonTerm2 = str.substring(i + 1);
            }
        }
        return ind;
    }

    int nonTerminal1Search(String str) {
        int index = UNDEFINED_SYMBOL;
        String pSymbolNext = str;
        pNonTerm1 = pSymbolNext;
        pRest1 = "";
        while (!pSymbolNext.isEmpty()) {
            int symbolIndex = symbolIdentify1(pSymbolNext);
            if (symbolIndex != UNDEFINED_SYMBOL) {
                index = symbolIndex;
                pNonTerm1 = pSymbolNext.substring(symbolIndex + 1);
                pRest1 = pSymbolNext.substring(0, symbolIndex);
            } else if (index == UNDEFINED_SYMBOL) {
                return UNDEFINED_SYMBOL;
            } else {
                break;
            }
            pSymbolNext = pNonTerm1;
        }
        return index;
    }

    int nonTerminal2Search(String str) {
        int index = UNDEFINED_SYMBOL;
        String pSymbolNext = str;
        pNonTerm2 = pSymbolNext;
        pRest2 = "";
        while (!pSymbolNext.isEmpty()) {
            int symbolIndex = symbolIdentify2(pSymbolNext);
            if (symbolIndex != UNDEFINED_SYMBOL) {
                index = symbolIndex;
                pNonTerm2 = pSymbolNext.substring(symbolIndex + 1);
                pRest2 = pSymbolNext.substring(0, symbolIndex);
            } else if (index == UNDEFINED_SYMBOL) {
                return UNDEFINED_SYMBOL;
            } else {
                break;
            }
            pSymbolNext = pNonTerm2;
        }
        return index;
    }

    private static final int UNDEFINED_SYMBOL = -1;
    private static final int MEM_ALLOC_ERR = -4;
    private static final int GEN_END = -5;
    private static final int RULE_ERROR = -6;
    private static final int BAD_STRING = -7;
    private static final int SUCCESS = 1;
    private static final int MAX_LEN_EXCESS = -2;
}

