package UI;

import Labs.MPA.PDA;
import Labs.MPA.RulePDA;
import Labs.MPA.TransitionPDA;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class PDAFrame extends JFrame {

    private final JTextField statesField = new JTextField();
    private final JTextField startStateField = new JTextField();
    private final JTextField endStateField = new JTextField();
    private final JTextField alphabetField = new JTextField();
    private final JTextField stackAlphabetField = new JTextField();
    private final JTextField startStringField = new JTextField();
    private final JTextField chain = new JTextField();
    private final JPanel innerVerticalPanel = new JPanel();

    public PDAFrame() {

        setTitle("Лабораторная работа 3");

        setSize(550, 600);

        int spacing = 3;

        this.setVisible(true);
        this.setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton addRuleButton = new JButton("Добавить правило:");
        JButton startAction = new JButton("Выполнить");
        JButton load = new JButton("Загрузить граматику");

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(3, 4, spacing, spacing));
        gridPanel.setMaximumSize(new Dimension(200, 40));

        gridPanel.add(new JLabel("Состояния"));
        gridPanel.add(statesField);
        gridPanel.add(new JLabel("Начальное состояние"));
        gridPanel.add(startStateField);
        gridPanel.add(new JLabel("Коенченое состояние"));
        gridPanel.add(endStateField);
        gridPanel.add(new JLabel("Алфавит языка"));
        gridPanel.add(alphabetField);
        gridPanel.add(new JLabel("Алфавит магазина"));
        gridPanel.add(stackAlphabetField);
        gridPanel.add(new JLabel("Начальный символ"));
        gridPanel.add(startStringField);


        JPanel verticalChainPanel = new JPanel();
        verticalChainPanel.setLayout(new BoxLayout(verticalChainPanel, BoxLayout.Y_AXIS));
        verticalChainPanel.add(new JLabel("Введите цепочку символов для провеки:"));
        verticalChainPanel.add(chain);

        JPanel horizontalButtonPanel = new JPanel();
        horizontalButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        horizontalButtonPanel.add(startAction);
        horizontalButtonPanel.add(load);
        horizontalButtonPanel.add(addRuleButton);


        JTextArea outData = new JTextArea(10, 30);
        outData.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outData);
        JPanel verticalPanel = new JPanel();

        innerVerticalPanel.setLayout(new BoxLayout(innerVerticalPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPaneVertical = new JScrollPane(innerVerticalPanel);
        scrollPaneVertical.setPreferredSize(new Dimension(this.getWidth() - 25, 200));
        scrollPaneVertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneVertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.add(verticalChainPanel);
        verticalPanel.add(horizontalButtonPanel);
        verticalPanel.add(scrollPaneVertical);
        verticalPanel.add(scrollPane);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(gridPanel);
        panel.add(verticalPanel);
        add(panel);

        load.addActionListener(e -> {
            Frame parentFrame = new Frame();
            FileDialog fileDialog = new FileDialog(parentFrame, "Выберите файл", FileDialog.LOAD);
            fileDialog.setVisible(true);

            String directory = fileDialog.getDirectory();
            String fileName = fileDialog.getFile();

            if (directory != null && fileName != null) {
                String filePath = directory + fileName;

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    String line;

                    String states = "";
                    String startState = "";
                    String endState = "";
                    String alphabet = "";
                    String stackAlphabet = "";
                    String startString = "";

                    Map<String, String> PDARules = new TreeMap<>();

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("// states")) {
                            states = reader.readLine();
                        } else if (line.startsWith("// startState")) {
                            startState = reader.readLine();
                        } else if (line.startsWith("// endState")) {
                            endState = reader.readLine();
                        } else if (line.startsWith("// alphabet")) {
                            alphabet = reader.readLine();
                        } else if (line.startsWith("// stackAlphabet")) {
                            stackAlphabet = reader.readLine();
                        } else if (line.startsWith("// startString")) {
                            startString = reader.readLine();
                        } else if (line.startsWith("// rules")) {
                            String rulesLine;
                            while ((rulesLine = reader.readLine()) != null) {
                                String[] ruleParts = rulesLine.split("=");
                                if (ruleParts.length == 2) {
                                    String transitionString = ruleParts[0].trim();
                                    String rules = ruleParts[1].trim();
                                    PDARules.put(transitionString, rules);
                                }
                            }
                        }
                    }

                    reader.close();

                    statesField.setText(states);
                    startStateField.setText(startState);
                    endStateField.setText(endState);
                    alphabetField.setText(alphabet);
                    stackAlphabetField.setText(stackAlphabet);
                    startStringField.setText(startString);

                    innerVerticalPanel.removeAll();

                    for (Map.Entry<String, String> entry : PDARules.entrySet()) {
                        JPanel horizontalPanel = new JPanel();
                        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
                        horizontalPanel.setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
                        horizontalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        horizontalPanel.setAlignmentY(Component.TOP_ALIGNMENT);
                        JTextField textField1 = new JTextField(entry.getKey());
                        textField1.setInputVerifier(new InputVerifier() {
                            @Override
                            public boolean verify(JComponent input) {
                                JTextField field = (JTextField) input;
                                String text = field.getText();
                                if (text.length() == 1) {
                                    return true;
                                } else {
                                    JOptionPane.showMessageDialog(null, "Длина не терминала должна быть один символ", "Ошибка", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                            }
                        });
                        JTextField textField2 = new JTextField(entry.getValue());
                        textField1.setMaximumSize(new Dimension(200, 25));
                        textField2.setMaximumSize(new Dimension(200, 25));
                        JButton deleteButton = new JButton("Удалить");
                        deleteButton.setMaximumSize(new Dimension(90, 25));
                        horizontalPanel.add(textField1);
                        horizontalPanel.add(textField2);
                        horizontalPanel.add(deleteButton);
                        deleteButton.addActionListener(e1 -> {
                            innerVerticalPanel.remove(horizontalPanel);
                            innerVerticalPanel.revalidate();
                            innerVerticalPanel.repaint();
                        });
                        innerVerticalPanel.add(horizontalPanel);
                    }

                    innerVerticalPanel.revalidate();
                    innerVerticalPanel.repaint();
                } catch (IOException ex) {
                    System.out.println("Не удалось считать файл");
                    ex.printStackTrace();
                }
            }
        });

        addRuleButton.addActionListener(e -> {
            JPanel horizontalPanel = new JPanel();
            horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
            horizontalPanel.setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
            horizontalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            horizontalPanel.setAlignmentY(Component.TOP_ALIGNMENT);

            JTextField textField1 = new JTextField();
            textField1.setInputVerifier(new InputVerifier() {
                @Override
                public boolean verify(JComponent input) {
                    JTextField field = (JTextField) input;
                    String text = field.getText();
                    if (text.length() == 1) {
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Длина не терминала должна быть один символ", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            });
            JTextField textField2 = new JTextField();
            textField1.setMaximumSize(new Dimension(200, 25));
            textField2.setMaximumSize(new Dimension(200, 25));
            JButton deleteButton = new JButton("Удалить");
            deleteButton.setMaximumSize(new Dimension(90, 25));

            horizontalPanel.add(textField1);
            horizontalPanel.add(textField2);
            horizontalPanel.add(deleteButton);

            deleteButton.addActionListener(e1 -> {

                innerVerticalPanel.remove(horizontalPanel);
                innerVerticalPanel.revalidate();
                innerVerticalPanel.repaint();

            });

            innerVerticalPanel.add(horizontalPanel);

            innerVerticalPanel.revalidate();
            innerVerticalPanel.repaint();
        });

        startAction.addActionListener(e -> {
            var productionRules = collectProductionRules();
            if (productionRules != null && isValidInput()) {

                PDA pda = new PDA(
                        convertStringToList(statesField.getText()),
                        stringToListCharacter(alphabetField.getText()),
                        stringToListCharacter(stackAlphabetField.getText()),
                        startStateField.getText(),
                        startStringField.getText().charAt(0),
                        convertStringToList(endStateField.getText()),
                        productionRules
                );
                outData.setText("");

                List<String> list = pda.isAccepted(chain.getText());
                for (String generatedString : list) {
                    outData.append(generatedString + "\n");
                }
            }
        });
    }

    private TreeMap<TransitionPDA, RulePDA> collectProductionRules() {
        TreeMap<TransitionPDA, RulePDA> transitionRules = new TreeMap<>();

        boolean atLeastOneFieldCreated = false;
        boolean isCorrect = false;
        for (Component component : innerVerticalPanel.getComponents()) {
            if (component instanceof JPanel horizontalPanel) {
                Component[] components = horizontalPanel.getComponents();

                if (components.length == 3 && components[0] instanceof JTextField textField1 && components[1] instanceof JTextField textField2 && components[2] instanceof JButton) {
                    atLeastOneFieldCreated = true;

                    if (!textField1.getText().trim().isEmpty() && !textField2.getText().trim().isEmpty()) {
                        isCorrect = true;
                        TransitionPDA transition = getTransitionsFromTextField(textField1.getText());
                        RulePDA rules = getRuleFromTextField(textField2.getText());
                        if (isValidValue(transition, rules)) {
                            transitionRules.put(transition, rules);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        if (!atLeastOneFieldCreated) {
            JOptionPane.showMessageDialog(this, "Не создано ни одного правила.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!isCorrect) {
            JOptionPane.showMessageDialog(this, "Правила заданы не корректно.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return transitionRules;
    }

    private boolean isValidValue(TransitionPDA transition, RulePDA rules) {
        return transition != null && transition.currentState != null && transition.stackTopSymbol != null && transition.inputSymbol != null &&
                rules != null && rules.rule != null && rules.currentState != null;
    }

    private TransitionPDA getTransitionsFromTextField(String text) {
        String[] parts = text.split(" ");

        if (parts.length == 3 && parts[0].length() >= 1 && parts[1].length() >= 1 && parts[2].length() >= 1) {
            String state = parts[0];
            char readSymbol = parts[1].charAt(0);
            char stackSymbol = parts[2].charAt(0);

            return new TransitionPDA(state, readSymbol, stackSymbol);
        } else {
            JOptionPane.showMessageDialog(this, "Неверный формат перевода!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private List<String> convertStringToList(String string) {

        List<String> list = new ArrayList<>();
        if (!string.isEmpty()) {

            String[] words = string.split("\\s+");

            Set<String> uniqueWords = new HashSet<>();

            for (String word : words) {
                if (!word.isEmpty()) {
                    uniqueWords.add(word);
                }
            }

            list.addAll(uniqueWords);
        }
        return list;
    }

    public List<Character> stringToListCharacter(String input) {
        List<Character> charList = new ArrayList<>();

        String[] parts = input.split(" ");
        for (String part : parts) {
            if (!part.isEmpty()) {
                charList.add(part.charAt(0));
            }
        }

        return charList;
    }

    public RulePDA getRuleFromTextField(String ruleText) {
        String[] parts = ruleText.split(" ");

        if (parts.length == 2 && parts[0].length() >= 1 && parts[1].length() >= 1) {
            String currentState = parts[0];
            String rule = parts[1];

            return new RulePDA(currentState, rule);
        } else {
            JOptionPane.showMessageDialog(this, "Неверный формат правила!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private boolean isValidInput() {
        String statesValue = statesField.getText();
        String alphabetValue = alphabetField.getText();
        String stackAlphabetValue = stackAlphabetField.getText();
        String startStateValue = startStateField.getText();
        String startStringValue = startStringField.getText();
        String endStateValue = endStateField.getText();
        String chainValue = chain.getText();

        if (statesValue.isEmpty() || alphabetValue.isEmpty() || stackAlphabetValue.isEmpty() ||
                startStateValue.isEmpty() || startStringValue.isEmpty() || endStateValue.isEmpty() || chainValue.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Не все поля заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}