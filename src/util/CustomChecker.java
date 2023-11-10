package util;

import CursWork.ChainsGeneration;

public class CustomChecker {
    public boolean checkRegularGrammar(ChainsGeneration chainsGeneration, int _checkInference) {
        String key;
        boolean isNonTerm;
        boolean isFirstTerm;

        for (int i = 0; i < chainsGeneration.getRulesDictionary().keySet().size(); i++) {
            key = (String) chainsGeneration.getRulesDictionary().keySet().toArray()[i];

            for (String isKey : chainsGeneration.getRulesDictionary().get(key)) {
                if (isKey.length() == 1) {
                    if (checkNonTerm(String.valueOf(isKey.charAt(0)),chainsGeneration)) {
                        return false;
                    }
                }

                if (isKey.length() >= 2) {
                    if (checkNonTerm(String.valueOf(isKey.charAt(0)),chainsGeneration)) {
                        isNonTerm = true;
                        isFirstTerm = false;

                        if (_checkInference == 0) {
                            return false;
                        }
                    } else {
                        isNonTerm = false;
                        isFirstTerm = true;
                    }

                    for (int j = 1; j < isKey.length(); j++) {
                        String help = String.valueOf(isKey.charAt(j));

                        if (checkNonTerm(help, chainsGeneration)) {
                            if (isFirstTerm && _checkInference == 1) {
                                return false;
                            }

                            if (isNonTerm) {
                                return false;
                            }

                            isNonTerm = true;
                        } else {
                            if (isNonTerm && isFirstTerm) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean checkNonTerm(String key, ChainsGeneration generation) {
        if (key.equals(String.valueOf(generation.getStartNonTerm().charAt(0)))) {
            return true;
        }
        for (int j = 0; j < generation.getNonTerm().length(); j++) {
            if (key.equals(String.valueOf(generation.getNonTerm().charAt(j)))) {
                return true;
            }
        }
        return false;
    }

    public String rearrangeInOrder(String stringToOrder, ChainsGeneration chainsGeneration) {
        stringToOrder = stringToOrder.chars().distinct().sorted().collect(StringBuilder::new,
                StringBuilder::appendCodePoint, StringBuilder::append).toString();

        if (stringToOrder.contains("~")) {
            stringToOrder = stringToOrder.replace("~", "");
            stringToOrder += "~";
        }
        if (stringToOrder.contains(String.valueOf(chainsGeneration.getNonTerm().charAt(0)))) {
            stringToOrder = stringToOrder.replace(String.valueOf(chainsGeneration.getNonTerm().charAt(0)), "");
            stringToOrder = chainsGeneration.getNonTerm().charAt(0) + stringToOrder;
        }

        return stringToOrder;
    }

}
