package CursWork;

import java.util.*;

public class ChainsGeneration {
    private final List<String> chainsList = new ArrayList<>();
    LinkedHashMap<String, String[]> rulesDictionary = new LinkedHashMap<>();

    private int maxChainLength;
    private int minChainLength;
    private String term = "";
    private String nonTerm = "";
    private String startNonTerm = "";
    private static final int MAX_RECURSION = 20000;


    public ChainsGeneration() {}

    public void removeAllRules() {
        chainsList.clear();
        rulesDictionary.clear();
    }

    public void addRuleDescription(String key, String description) {
        String[] rules = description.endsWith("|") ? description.split("\\|", -1) : description.split("\\|");
        rulesDictionary.put(key, rules);
    }

    public void generateChains() {
        String res;
        int recursion = 0;
        String chainPrefix;
        List<String> nonTermChains = new ArrayList<>();

        for (String ruleRightPart : rulesDictionary.get(startNonTerm)) {
            if (isValid(ruleRightPart) == 0 && ruleRightPart.length() >= minChainLength) {
                chainsList.add(ruleRightPart.isEmpty() ? "" : ruleRightPart);
            } else if (isValid(ruleRightPart) == -2) {
                nonTermChains.add(ruleRightPart);
            }
        }

        while (chainsList.size() < 20 && recursion <= MAX_RECURSION && !nonTermChains.isEmpty()) {
            recursion++;
            List<String> subNonTermChains = new ArrayList<>();
            for (String nonTermChain : nonTermChains) {
                chainPrefix = "";
                for (int i = 0; i < nonTermChain.length(); i++) {
                    if (!rulesDictionary.containsKey(String.valueOf(nonTermChain.charAt(i)))){
                        chainPrefix += nonTermChain.charAt(i);
                    } else {
                        for (String ruleRightPart : rulesDictionary.get(String.valueOf(nonTermChain.charAt(i))) ){
                            res = chainPrefix + ruleRightPart + nonTermChain.substring(i + 1);
                            int isValidResult = isValid(res);
                            if (isValidResult == 0 && res.length() >= minChainLength && !chainsList.contains(res.isEmpty() ? "" : res)) {
                                chainsList.add(res.isEmpty() ? "" : res);
                            }
                            if (isValidResult == -2) {
                                subNonTermChains.add(res);
                            }
                            if (chainsList.size() >= 20) {
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            nonTermChains.clear();
            nonTermChains.addAll(subNonTermChains);
        }
    }




    private int isValid(String line) {
        int termSym = 0;
        int nonTermSym = 0;
        for (char ch : line.toCharArray()) {
            if (!rulesDictionary.containsKey(String.valueOf(ch))) {
                termSym++;
            } else {
                nonTermSym++;
            }

            if (termSym > maxChainLength || (termSym + nonTermSym - 5) > maxChainLength) {
                return -1;
            }
        }

        return (nonTermSym > 0) ? -2 : 0;
    }

    public List<String> getChainsList() {
        return chainsList;
    }

    public Map<String, String[]> getRulesDictionary() {
        return rulesDictionary;
    }

    public String getTerm() {
        return term;
    }

    public String getNonTerm() {
        return nonTerm;
    }

    public String getStartNonTerm() {
        return startNonTerm;
    }

    public void setMaxChainLength(int maxChainLength) {
        this.maxChainLength = maxChainLength;
    }

    public void setMinChainLength(int minChainLength) {
        this.minChainLength = minChainLength;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setNonTerm(String nonTerm) {
        this.nonTerm = nonTerm;
    }

    public void setStartNonTerm(String startNonTerm) {
        this.startNonTerm = startNonTerm;
    }
}
