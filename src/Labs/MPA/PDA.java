package Labs.MPA;

import java.util.*;

public class PDA {
    private final List<String> states;
    private final List<Character> alphabet;
    private final List<Character> stackAlphabet;
    private final String startState;
    private final Character startStack;
    private final List<String> finalStates;
    private final TreeMap<TransitionPDA, RulePDA> transitions;

    public PDA(List<String> states, List<Character> alphabet, List<Character> stackAlphabet,
               String startState, Character startStack, List<String> finalStates,
               TreeMap<TransitionPDA, RulePDA> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.stackAlphabet = stackAlphabet;
        this.startState = startState;
        this.startStack = startStack;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    public List<String> isAccepted(String inputString) {
        List<String> steps = new ArrayList<>();
        Stack<Character> stack = new Stack<>();
        stack.push(startStack);
        String currentState = startState;
        String inputRemaining = inputString;

        for (String finalState : finalStates) {
            if (!states.contains(finalState)) {
                steps.add("Конечное состояние " + finalState + " отсутствует в списке состояний.");
                return steps;
            }
        }

        if (!states.contains(startState)) {
            steps.add("Начальное состояние " + startState + " отсутствует в списке состояний.");
            return steps;
        }

        while (true) {

            if (!inputRemaining.isEmpty() && !alphabet.contains(inputRemaining.charAt(0))) {
                steps.add("Цепочка не принадлежит языку, причина: символ на входе не принадлежит алфавиту языка.");
                return steps;
            }

            if (!stack.isEmpty() && !stackAlphabet.contains(stack.peek())) {
                steps.add("Цепочка не принадлежит языку, причина: символ в магазине не принадлежит алфавиту магазина.");
                return steps;
            }

            if (!states.contains(currentState)) {
                steps.add("Цепочка не принадлежит языку, причина: текущее состояние не принадлежит списку допустимых состояний.");
                return steps;
            }

            String configuration = String.format("(%s, %s, [%s]) %s", currentState, inputRemaining, stack.peek(), stack);
            steps.add(configuration);

            if (finalStates.contains(currentState) && inputRemaining.isEmpty() && stack.peek() == startStack) {
                steps.add("Цепочка принадлежит языку");
                return steps;
            }

            char inputSymbol = inputRemaining.isEmpty() ? 'ε' : inputRemaining.charAt(0);
            TransitionPDA transition = new TransitionPDA(currentState, inputSymbol, stack.peek());

            if (transitions.containsKey(transition)) {
                RulePDA rule = transitions.get(transition);

                currentState = rule.currentState;
                if (inputSymbol != 'ε') {
                    stack.pop();
                }

                String stackReplacement = rule.rule;
                if (!stackReplacement.equals("ε")) {
                    for (int i = stackReplacement.length() - 1; i >= 0; i--) {
                        stack.push(stackReplacement.charAt(i));
                    }
                }

                if (!inputRemaining.isEmpty()) {
                    inputRemaining = inputRemaining.substring(1);
                }
            } else {
                String errorMessage = String.format("Цепочка не принадлежит языку, причина: \n" +
                        "Не определено значение для функции перехода (%s, %s, %s)", currentState, inputSymbol, stack.peek());
                steps.add(errorMessage);
                return steps;
            }
        }
    }

}