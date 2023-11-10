package UI;

import CursWork.ChainsGeneration;
import util.CustomChecker;
import util.CustomConverter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CursWorkFrame extends JFrame {

    private final JTextField nonTerminalField = new JTextField();
    private final JTextField terminalField = new JTextField();
    private final JRadioButton leftInference = new JRadioButton("Леволинейная");
    private final JRadioButton rightInference = new JRadioButton("Праволинейная");
    private final JComboBox<String> startSymbolComboBox = new JComboBox<>();
    private final JTextField checkChainField = new JTextField();
    private final JTextArea outChains;
    private final JSpinner minLength;
    private final JSpinner maxLength;
    private final JTextArea outStates;
    private final JLabel label;
    private final JButton buildDKA;
    DefaultTableModel mainGrammarModel = new DefaultTableModel();
    DefaultTableModel transformedGrammarModel = new DefaultTableModel();
    DefaultTableModel DKAModel = new DefaultTableModel();
    ChainsGeneration chainsGeneration = new ChainsGeneration();
    CustomConverter customConverter = new CustomConverter();
    CustomChecker customChecker = new CustomChecker();
    private final Map<String, String> bufferForTransform = new HashMap<>();
    private final Map<String, String[]> transformedMap = new HashMap<>();
    private final LinkedHashMap<String, Integer> resultMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> statesMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> symbolsMap = new LinkedHashMap<>();
    private int checkInference;
    private String finalStates = "";
    private String beginLeftState = "";
    private String[][] transitions;
    private String[][] newTransitions;
    private final char[] nonTerms = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private boolean needToTransform;
    boolean isStop = false;

    public CursWorkFrame() {
        setTitle("Курсовая работа вариант 13");
        setSize(1200, 800);
        this.setVisible(true);
        this.setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int spacing = 3;

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(14, 1, spacing, spacing));

        JButton load = new JButton("Загрузить граматику");
        JButton transform = new JButton("Преобразовать");
        buildDKA = new JButton("Построить ДКА");
        JButton checkChain = new JButton("Проверить цепочку");
        minLength = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        maxLength = new JSpinner(new SpinnerNumberModel(10, 0, 10, 1));

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(leftInference);
        radioButtonGroup.add(rightInference);

        gridPanel.add(load);
        gridPanel.add(new JLabel("Терминальные символы"));
        gridPanel.add(terminalField);
        gridPanel.add(new JLabel("Нетерминальные символы"));
        gridPanel.add(nonTerminalField);
        gridPanel.add(new JLabel("Целевой символ"));
        gridPanel.add(startSymbolComboBox);
        gridPanel.add(new JLabel("Минимальная длина"));
        gridPanel.add(minLength);
        gridPanel.add(new JLabel("Максимальная длина"));
        gridPanel.add(maxLength);
        gridPanel.add(new JLabel("Тип вывода:"));
        gridPanel.add(leftInference);
        gridPanel.add(rightInference);
        JButton addRuleButton = new JButton("Добавить правило");
        gridPanel.add(addRuleButton);

        JPanel verticalPanel1 = new JPanel();
        verticalPanel1.setLayout(new BoxLayout(verticalPanel1, BoxLayout.Y_AXIS));
        verticalPanel1.setBorder(new EmptyBorder(5, 5, 5, 5));

        outChains = new JTextArea(10, 1);

        outChains.setEditable(false);
        JScrollPane scrollChains = new JScrollPane(outChains);
        scrollChains.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollChains.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        verticalPanel1.add(gridPanel);
        verticalPanel1.add(scrollChains);

        String[] columnNames = {"Не терминал", "Правило"};

        mainGrammarModel.setColumnIdentifiers(columnNames);
        mainGrammarModel.setRowCount(0);
        JTable mainGrammarTable = new JTable();
        mainGrammarTable.setModel(mainGrammarModel);
        mainGrammarTable.setBounds(10, 10, 200, 150);

        JScrollPane scrollMainGrammarTable = new JScrollPane(mainGrammarTable);
        scrollMainGrammarTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMainGrammarTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMainGrammarTable.setPreferredSize(new Dimension(400, 300));

        transformedGrammarModel.setColumnIdentifiers(columnNames);
        transformedGrammarModel.setRowCount(0);
        JTable transformedGrammarTable = new JTable();
        transformedGrammarTable.setModel(transformedGrammarModel);
        transformedGrammarTable.setBounds(10, 10, 200, 150);
        JScrollPane scrollTransformedGrammarTable = new JScrollPane(transformedGrammarTable);
        scrollTransformedGrammarTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollTransformedGrammarTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTransformedGrammarTable.setPreferredSize(new Dimension(400, 300));

        JPanel verticalPanel2 = new JPanel();
        verticalPanel2.setLayout(new BoxLayout(verticalPanel2, BoxLayout.Y_AXIS));
        verticalPanel2.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel mainGrammarTableLabel = new JPanel();
        mainGrammarTableLabel.setLayout(new GridLayout(1, 1, spacing, spacing));
        mainGrammarTableLabel.add(new JLabel("Исходная грамматика"));
        mainGrammarTableLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel2.add(mainGrammarTableLabel);
        verticalPanel2.add(scrollMainGrammarTable);

        JPanel horizontalButtonsPanel = new JPanel();
        horizontalButtonsPanel.setLayout(new GridLayout(1, 2, spacing, spacing));
        horizontalButtonsPanel.add(addRuleButton);
        horizontalButtonsPanel.add(transform);
        horizontalButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel2.add(horizontalButtonsPanel);
        JPanel transformedLabel = new JPanel();
        transformedLabel.setLayout(new GridLayout(1, 1, spacing, spacing));
        transformedLabel.add(new JLabel("Преобразованная грамматика"));
        transformedLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel2.add(transformedLabel);
        verticalPanel2.add(scrollTransformedGrammarTable);

        JPanel verticalPanel3 = new JPanel();
        verticalPanel3.setLayout(new BoxLayout(verticalPanel3, BoxLayout.Y_AXIS));
        verticalPanel3.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel dkaLabel = new JPanel();
        dkaLabel.setLayout(new GridLayout(1, 1, spacing, spacing));
        dkaLabel.add(new JLabel("Таблица переходов ДКА"));
        dkaLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel3.add(dkaLabel);

        DKAModel.setRowCount(0);
        JTable DKATable = new JTable();
        DKATable.setModel(DKAModel);
        DKATable.setBounds(10, 10, 200, 150);
        JScrollPane scrollDKATable = new JScrollPane(DKATable);
        scrollDKATable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollDKATable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDKATable.setPreferredSize(new Dimension(400, 300));
        verticalPanel3.add(scrollDKATable);

        JPanel buildDKAButton = new JPanel();
        buildDKAButton.setLayout(new GridLayout(1, 1, spacing, spacing));
        buildDKAButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        buildDKAButton.add(buildDKA);
        verticalPanel3.add(buildDKAButton);

        JPanel configLabel = new JPanel();
        configLabel.setLayout(new GridLayout(1, 1, spacing, spacing));
        label = new JLabel("Последовательность конфигураций");
        configLabel.add(label);
        configLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel3.add(configLabel);

        outStates = new JTextArea(10, 1);
        JScrollPane scrollStates = new JScrollPane(outStates);
        scrollStates.setPreferredSize(new Dimension(400, 260));
        scrollStates.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollStates.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        verticalPanel3.add(scrollStates);

        JPanel checkBuildHelperPanel = new JPanel();
        checkBuildHelperPanel.setLayout(new GridLayout(1, 2, spacing, spacing));
        checkBuildHelperPanel.setPreferredSize(new Dimension(400, 30));
        checkBuildHelperPanel.add(checkChainField);
        checkBuildHelperPanel.add(checkChain);
        checkBuildHelperPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        verticalPanel3.add(checkBuildHelperPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.add(verticalPanel1);
        mainPanel.add(verticalPanel2);
        mainPanel.add(verticalPanel3);
        add(mainPanel);

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

                    String nonTerminals = "";
                    String terminals = "";
                    String startSymbol = "";

                    LinkedHashMap<String, String> grammarRules = new LinkedHashMap<>();

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("// множество нетерминальных символов")) {
                            nonTerminals = reader.readLine();
                        } else if (line.startsWith("// множество терминальных символов")) {
                            terminals = reader.readLine();
                        } else if (line.startsWith("// целевой символ")) {
                            startSymbol = reader.readLine();
                        } else if (line.startsWith("// правила для символа")) {
                            String symbol = line.substring(line.lastIndexOf(" ") + 1);
                            String rules = reader.readLine();
                            grammarRules.put(symbol, rules);
                        }
                    }

                    reader.close();

                    nonTerminalField.setText(nonTerminals);
                    terminalField.setText(terminals);
                    updateStartSymbolComboBox();
                    startSymbolComboBox.setSelectedItem(startSymbol);


                    mainGrammarModel.setRowCount(0);

                    for (Map.Entry<String, String> entry : grammarRules.entrySet()) {
                        mainGrammarModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                    }

                    mainGrammarTable.setModel(mainGrammarModel);


                } catch (IOException ex) {
                    System.out.println("Не удалость считать граматику");
                    ex.printStackTrace();
                }
            }
        });

        addRuleButton.addActionListener(e -> mainGrammarModel.addRow(new Object[]{" ", " "}));

        transform.addActionListener(e -> {

            chainsGeneration.setTerm(terminalField.getText().replaceAll("\\s", ""));
            chainsGeneration.setNonTerm(nonTerminalField.getText().replaceAll("\\s", ""));
            chainsGeneration.setStartNonTerm((String) startSymbolComboBox.getSelectedItem());
            chainsGeneration.setMinChainLength((int) minLength.getValue());
            chainsGeneration.setMaxChainLength((int) maxLength.getValue());
            transformGrammar();
        });

        buildDKA.addActionListener(e -> createDKA());

        checkChain.addActionListener(e -> checkDKA(checkChainField.getText()));
    }

    private void transformGrammar() {

        String key;
        boolean isTerm;
        boolean isGrammar;
        boolean isCorrect = true;
        String failMessage = "";

        finalStates = "";
        beginLeftState = "";

        chainsGeneration.removeAllRules();

        if (leftInference.isSelected()) {
            checkInference = 1;
        }
        if (rightInference.isSelected()) {
            checkInference = 0;
        }

        for (int i = 0; i < mainGrammarModel.getRowCount(); i++) {
            String value = "";
            key = mainGrammarModel.getValueAt(i, 0).toString();
            if (mainGrammarModel.getValueAt(i, 1) != null) {
                value = mainGrammarModel.getValueAt(i, 1).toString();
            }
            for (int er = 0; er < value.length(); er++) {
                isTerm = false;
                for (int k = 0; k < terminalField.getText().replace(" ", "").length(); k++) {
                    if (value.charAt(er) == terminalField.getText().replace(" ", "").charAt(k)) {
                        isTerm = true;
                    }
                }
                if (!isTerm) {
                    if (!(value.charAt(er) == 'A' || value.charAt(er) == 'B'
                            || value.charAt(er) == 'C' || value.charAt(er) == 'D' || value.charAt(er) == 'E' || value.charAt(er) == 'F' || value.charAt(er) == 'G'
                            || value.charAt(er) == 'H' || value.charAt(er) == 'I' || value.charAt(er) == 'J' || value.charAt(er) == 'K' || value.charAt(er) == 'L'
                            || value.charAt(er) == 'M' || value.charAt(er) == 'N' || value.charAt(er) == 'O' || value.charAt(er) == 'P' || value.charAt(er) == 'Q'
                            || value.charAt(er) == 'R' || value.charAt(er) == 'S' || value.charAt(er) == 'T' || value.charAt(er) == 'U' || value.charAt(er) == 'V'
                            || value.charAt(er) == 'W' || value.charAt(er) == 'X' || value.charAt(er) == 'Y' || value.charAt(er) == 'Z' || value.charAt(er) == '|' || value.charAt(er) == '~')) {
                        isCorrect = false;
                        failMessage = "ошибка!\n" + value.charAt(er) + " не нашло в алфавите";
                        break;
                    }
                }
            }
            for (int j = 0; j < value.length(); j++) {
                if (!value.contains(value.substring(j, j + 1)) && (value.charAt(j) == 'A' || value.charAt(j) == 'B'
                        || value.charAt(j) == 'C' || value.charAt(j) == 'D' || value.charAt(j) == 'E' || value.charAt(j) == 'F' || value.charAt(j) == 'G'
                        || value.charAt(j) == 'H' || value.charAt(j) == 'I' || value.charAt(j) == 'J' || value.charAt(j) == 'K' || value.charAt(j) == 'L'
                        || value.charAt(j) == 'M' || value.charAt(j) == 'N' || value.charAt(j) == 'O' || value.charAt(j) == 'P' || value.charAt(j) == 'Q'
                        || value.charAt(j) == 'R' || value.charAt(j) == 'S' || value.charAt(j) == 'T' || value.charAt(j) == 'U' || value.charAt(j) == 'V'
                        || value.charAt(j) == 'W' || value.charAt(j) == 'X' || value.charAt(j) == 'Y' || value.charAt(j) == 'Z')) {
                    isCorrect = false;
                    failMessage = "ошибка!\n" + value.charAt(j) + " не нашло в алфавите";
                    break;
                }
            }
            if (mainGrammarModel.getValueAt(i, 1) != null) {
                chainsGeneration.addRuleDescription(key, mainGrammarModel.getValueAt(i, 1).toString());
            } else {
                chainsGeneration.addRuleDescription(key, "");
            }
        }

        isGrammar = customChecker.checkRegularGrammar(chainsGeneration, checkInference);

        if (isCorrect && isGrammar) {
            chainsGeneration.generateChains();
            if (leftInference.isSelected()) {
                transformLeftGrammar();
            }
            if (rightInference.isSelected()) {
                transformRightGrammar();
            }
            buildDKA.setEnabled(true);
        } else {
            if (failMessage.isEmpty()) {
                failMessage = "Ошибка при проверке регулярной грамматики";
            }
            JOptionPane.showMessageDialog(this, failMessage);
        }
    }

    public void transformLeftGrammar() {
        int length;
        int newNonTerms;

        String key;
        String bufRule;
        StringBuilder resultRules = new StringBuilder();

        boolean isLastTerm = false;

        finalStates = "";
        bufferForTransform.clear();

        for (int i = 0; i < chainsGeneration.getRulesDictionary().keySet().size(); i++) {
            key = (String) chainsGeneration.getRulesDictionary().keySet().toArray()[i];

            for (String isKey : chainsGeneration.getRulesDictionary().get(key)) {
                length = isKey.length() - 1;

                if (isKey.equals("")) {
                    resultRules.append("|");
                    beginLeftState += key;
                    continue;
                }

                if (isKey.length() == 2 && (customChecker.checkNonTerm(String.valueOf(isKey.charAt(0)), chainsGeneration))) {
                    resultRules.append(isKey).append("|");
                    continue;
                }

                if (isKey.length() >= 2) {
                    if (customChecker.checkNonTerm(String.valueOf(isKey.charAt(0)), chainsGeneration)) {
                        newNonTerms = length - 1;
                        isLastTerm = true;
                    } else {
                        newNonTerms = length + 1;
                    }

                    if (!customChecker.checkNonTerm(String.valueOf(isKey.charAt(0)), chainsGeneration) && isKey.length() == 2) {
                        newNonTerms = 2;
                    }

                    String newKey = addNewKey();
                    resultRules.append(newKey).append(isKey.charAt(length)).append("|");
                    bufRule = isKey.substring(0, length);
                    newNonTerms--;

                    while (newNonTerms >= 0) {
                        String bufNewKey;

                        if (isLastTerm) {
                            while (bufRule.length() > 2) {
                                String newRule = newKey + bufRule.charAt(bufRule.length() - 1);
                                bufferForTransform.put(newKey, newRule);
                                bufNewKey = addNewKey();
                                bufferForTransform.remove(newKey);
                                newRule = bufNewKey + bufRule.charAt(bufRule.length() - 1);
                                bufferForTransform.put(newKey, newRule);
                                newNonTerms--;
                                newKey = bufNewKey;
                                bufRule = bufRule.substring(0, bufRule.length() - 1);
                            }

                        } else {
                            while (bufRule.length() > 1) {
                                String newRule = newKey + bufRule.charAt(bufRule.length() - 1);
                                bufferForTransform.put(newKey, newRule);
                                bufNewKey = addNewKey();
                                bufferForTransform.remove(newKey);
                                newRule = bufNewKey + bufRule.charAt(bufRule.length() - 1);
                                bufferForTransform.put(newKey, newRule);
                                newNonTerms--;
                                newKey = bufNewKey;
                                bufRule = bufRule.substring(0, bufRule.length() - 1);
                            }

                        }
                        bufferForTransform.put(newKey, bufRule);
                        newNonTerms--;
                    }

                    isLastTerm = false;
                } else {
                    resultRules.append(isKey).append("|");
                }
            }

            resultRules = new StringBuilder(resultRules.substring(0, resultRules.length() - 1));
            transformedGrammarModel.addRow(new Object[]{key, resultRules.toString()});
            transformedMap.put(key, resultRules.toString().split("\\|"));
            resultRules = new StringBuilder();
        }

        for (int i = 0; i < bufferForTransform.keySet().size(); i++) {
            String bufferKey = (String) bufferForTransform.keySet().toArray()[i];
            String bufferValue = bufferForTransform.get(bufferKey);
            transformedGrammarModel.addRow(new Object[]{bufferKey, bufferValue});
            transformedMap.put(bufferKey, bufferForTransform.get(bufferKey).split("\\|"));
        }

    }

    private void transformRightGrammar() {
        int length;
        int newNonTerms;

        String key;
        String bufRule;
        StringBuilder resultRules = new StringBuilder();

        boolean isLastTerm = false;
        finalStates = "";

        bufferForTransform.clear();
        transformedGrammarModel.setRowCount(0);

        for (int i = 0; i < chainsGeneration.getRulesDictionary().keySet().size(); i++) {
            key = (String) chainsGeneration.getRulesDictionary().keySet().toArray()[i];

            for (String isKey : chainsGeneration.getRulesDictionary().get(key)) {
                length = isKey.length() - 1;

                if (isKey.equals("")) {
                    finalStates += key + ",";
                    resultRules.append("|");
                    continue;
                }

                if (isKey.length() == 2 && (customChecker.checkNonTerm(String.valueOf(isKey.charAt(length)), chainsGeneration))) {
                    resultRules.append(isKey).append("|");
                    continue;
                }

                if (isKey.length() >= 2) {
                    if (customChecker.checkNonTerm(String.valueOf(isKey.charAt(length)), chainsGeneration)) {
                        newNonTerms = length - 1;
                        isLastTerm = true;
                    } else {
                        newNonTerms = length;
                    }

                    if (!customChecker.checkNonTerm(String.valueOf(isKey.charAt(length)), chainsGeneration) && isKey.length() == 2) {
                        newNonTerms = 1;
                    }

                    String newKey = addNewKey();
                    resultRules.append(isKey.charAt(0)).append(newKey).append("|");
                    bufRule = isKey.substring(1);
                    newNonTerms--;

                    while (newNonTerms >= 0) {
                        String bufNewKey;

                        if (isLastTerm) {
                            while (bufRule.length() > 2) {
                                String newRule = bufRule.charAt(0) + newKey;
                                bufferForTransform.put(newKey, newRule);
                                bufNewKey = addNewKey();
                                bufferForTransform.remove(newKey);
                                newRule = bufRule.charAt(0) + bufNewKey;
                                bufferForTransform.put(newKey, newRule);
                                newNonTerms--;
                                newKey = bufNewKey;
                                bufRule = bufRule.substring(1);
                            }

                        } else {
                            while (bufRule.length() > 1) {
                                String newRule = bufRule.charAt(0) + newKey;
                                bufferForTransform.put(newKey, newRule);
                                bufNewKey = addNewKey();
                                bufferForTransform.remove(newKey);
                                newRule = bufRule.charAt(0) + bufNewKey;
                                bufferForTransform.put(newKey, newRule);
                                newNonTerms--;
                                newKey = bufNewKey;
                                bufRule = bufRule.substring(1);
                            }

                        }
                        bufferForTransform.put(newKey, bufRule);
                        newNonTerms--;
                    }

                    isLastTerm = false;
                } else {
                    resultRules.append(isKey).append("|");
                }
            }

            resultRules = new StringBuilder(resultRules.substring(0, resultRules.length() - 1));
            transformedGrammarModel.addRow(new Object[]{key, resultRules.toString()});
            resultRules = new StringBuilder();
        }

        for (int i = 0; i < bufferForTransform.keySet().size(); i++) {
            String bufferKey = (String) bufferForTransform.keySet().toArray()[i];
            String bufferValue = bufferForTransform.get(bufferKey);
            transformedGrammarModel.addRow(new Object[]{bufferKey, bufferValue});
        }

    }

    private String addNewKey() {
        String key = "";
        for (int i = 0; i < 26; i++) {
            String nonTerm = String.valueOf(nonTerms[i]);
            if (!chainsGeneration.getRulesDictionary().containsKey(nonTerm)
                    && !bufferForTransform.containsKey(nonTerm)) {
                key = nonTerm;
                break;
            }
        }
        return key;
    }

    private void createDKA() {

        needToTransform = false;

        for (int i = 0; i < transformedMap.size(); i++) {
            String key = transformedMap.keySet().toArray()[i].toString();
            statesMap.put(key, i);
        }
        statesMap.put("~", transformedMap.size());

        for (int i = 0; i < chainsGeneration.getTerm().length(); i++) {
            symbolsMap.put(String.valueOf(chainsGeneration.getTerm().charAt(i)), i);
        }

        transitions = new String[transformedMap.size() + 1][];
        for (int i = 0; i < transformedMap.size() + 1; i++) {
            transitions[i] = new String[chainsGeneration.getTerm().length()];
        }
        for (int i = 0; i < transformedMap.size() + 1; i++) {
            Arrays.fill(transitions[i], "");
        }

        if (checkInference == 1) {
            buildLeftSideGrammarToDKA();
        } else {
            buildRightSideGrammarToDKA();
        }

        for (int i = 0; i < transformedMap.size() + 1; i++) {
            if (needToTransform) {
                break;
            }
            for (int j = 0; j < transitions[i].length; j++) {
                if (transitions[i][j].length() >= 2) {
                    needToTransform = true;
                    transformToDKA();
                    break;
                }
            }
        }

        if (!needToTransform) {
            if (checkInference == 1) {
                newTransitions = new String[10000][];
                for (int i = 0; i < 10000; i++) {
                    newTransitions[i] = new String[chainsGeneration.getTerm().length()];
                }
                for (int i = 0; i < transformedMap.size() + 1; i++) {
                    System.arraycopy(transitions[i], 0, newTransitions[i], 0, transitions[i].length);
                }
                if (!addNewStates()) {
                    return;
                }

                needToTransform = true;
                exceptNonEntryStates(newTransitions);
                addNewFinalStates(resultMap);
                printStatesToUser(newTransitions);
            } else {
                exceptNonEntryStates(transitions);
                addNewFinalStates(resultMap);
                printStatesToUser(transitions);
            }
        }
    }

    private void buildLeftSideGrammarToDKA() {
        String start = chainsGeneration.getStartNonTerm();
        assert start != null;
        finalStates += start.charAt(0) + ",";

        for (String key : transformedMap.keySet()) {
            for (String isKey : transformedMap.get(key)) {
                if (isKey.length() == 1 && !customChecker.checkNonTerm(isKey, chainsGeneration)) {
                    beginLeftState += "~";
                    transitions[transformedMap.size()][symbolsMap.get(String.valueOf(isKey.charAt(0)))] += key;
                } else if (!isKey.isEmpty()) {
                    transitions[statesMap.get(String.valueOf(isKey.charAt(0)))][symbolsMap.get(String.valueOf(isKey.charAt(1)))] += key;
                }
            }
        }

        for (String key : statesMap.keySet()) {
            for (int j = 0; j < transitions[statesMap.get(key)].length; j++) {
                if (!transitions[statesMap.get(key)][j].isEmpty()) {
                    transitions[statesMap.get(key)][j] = customChecker.rearrangeInOrder(transitions[statesMap.get(key)][j], chainsGeneration);
                }
            }
        }
        List<String> list = chainsGeneration.getChainsList();
        for (String l : list) {
            outChains.append(l + "\n");
        }
    }

    private void buildRightSideGrammarToDKA() {
        String key;
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < transformedMap.size() + 1; i++) {
            for (int j = 0; j < transitions[i].length; j++) {
                if (i != transformedMap.size()) {
                    key = (String) transformedMap.keySet().toArray()[i];
                    for (String isKey : transformedMap.get(key)) {
                        if (!isKey.isEmpty()) {
                            if (isKey.charAt(0) == chainsGeneration.getTerm().charAt(j) && isKey.length() == 2) {
                                res.append(isKey.charAt(1));
                            } else if (isKey.charAt(0) == chainsGeneration.getTerm().charAt(j) && isKey.length() == 1) {
                                res.append("~");
                                finalStates += "~,";
                            }
                        }
                    }
                    transitions[i][j] = res.toString();
                    transitions[i][j] = customChecker.rearrangeInOrder(transitions[i][j], chainsGeneration);
                    res = new StringBuilder();
                } else {
                    transitions[i][j] = "";
                }
            }
        }
    }

    public void transformToDKA() {
        newTransitions = new String[10000][];
        for (int i = 0; i < 10000; i++) {
            newTransitions[i] = new String[chainsGeneration.getTerm().length()];
        }

        for (int i = 0; i < transformedMap.size() + 1; i++) {
            System.arraycopy(transitions[i], 0, newTransitions[i], 0, transitions[i].length);
        }

        if (!addNewStates()) {
            return;
        }

        // Исключения непригодных состояний
        exceptNonEntryStates(newTransitions);

        // Добавление новых конечных состояний
        addNewFinalStates(resultMap);

        printStatesToUser(newTransitions);
    }

    public boolean addNewStates() {
        int counter = 1;
        String oldState;
        String result = "";

        // Перебор по строкам нового переходного массива
        for (int i = 0; i < transformedMap.size() + 1; i++) {
            // Перебор по столбцам нового переходного массива
            for (int j = 0; j < newTransitions[i].length; j++) {
                // Проверка, что состояние не было добавлено ранее и не пусто
                if (!statesMap.containsKey(newTransitions[i][j]) && !newTransitions[i][j].isEmpty()) {
                    oldState = newTransitions[i][j];
                    // Добавление нового состояния в словарь состояний
                    statesMap.put(oldState, transformedMap.size() + counter);

                    // Обновление нового переходного массива
                    for (int h = 0; h < newTransitions[i].length; h++) {
                        for (int k = 0; k < oldState.length(); k++) {
                            result += newTransitions[statesMap.get(String.valueOf(oldState.charAt(k)))][h];
                        }
                        // Реорганизация результата и обновление нового переходного массива
                        result = customChecker.rearrangeInOrder(result, chainsGeneration);
                        newTransitions[statesMap.get(oldState)][h] = result;
                        result = "";
                    }
                    counter++;
                }
            }
        }

        int isNewState;
        int oldStatesCount = counter - 1;
        boolean isEnteredInPrevious = false;
        int clueNumber = statesMap.size() - oldStatesCount;

        if (checkInference == 1) {
            if (beginLeftState.isEmpty()) {
                // Обработка ошибки, если начальное состояние пусто
                JOptionPane.showMessageDialog(null, "Ошибка построения автомата");
                DKAModel.setRowCount(0);
                DKAModel.setColumnCount(0);
                return false;
            }

            // Реорганизация начального состояния
            beginLeftState = customChecker.rearrangeInOrder(beginLeftState, chainsGeneration);

            // Проверка, было ли начальное состояние в предыдущих состояниях
            for (String key : transformedMap.keySet()) {
                if (key.equals(beginLeftState)) {
                    isEnteredInPrevious = true;
                    break;
                }
            }

            // Добавление начального состояния, если оно не было в предыдущих состояниях
            if (!isEnteredInPrevious && !beginLeftState.equals("~")) {
                statesMap.put(beginLeftState, transformedMap.size() + counter);
                for (int h = 0; h < chainsGeneration.getTerm().length(); h++) {
                    for (int k = 0; k < beginLeftState.length(); k++) {
                        result += newTransitions[statesMap.get(String.valueOf(beginLeftState.charAt(k)))][h];
                    }
                    // Реорганизация результата и обновление нового переходного массива
                    result = customChecker.rearrangeInOrder(result, chainsGeneration);
                    newTransitions[statesMap.get(beginLeftState)][h] = result;
                    result = "";
                }
                counter++;
                isNewState = oldStatesCount + 1;
            } else {
                isNewState = oldStatesCount;
            }
        } else {
            if (finalStates.isEmpty()) {
                // Обработка ошибки, если конечные состояния пусты
                JOptionPane.showMessageDialog(null, "Ошибка построения автомата");
                DKAModel.setRowCount(0);
                DKAModel.setColumnCount(0);
                return false;
            }
            isNewState = oldStatesCount;
        }

        // Добавление новых состояний
        while (isNewState != 0) {
            String key = (String) statesMap.keySet().toArray()[clueNumber];
            for (int j = 0; j < chainsGeneration.getTerm().length(); j++) {
                // Проверка, что состояние не было добавлено ранее и не пусто
                if (!statesMap.containsKey(newTransitions[statesMap.get(key)][j])
                        && !newTransitions[statesMap.get(key)][j].isEmpty()) {
                    isNewState++;
                    oldState = newTransitions[statesMap.get(key)][j];
                    statesMap.put(oldState, transformedMap.size() + counter);
                    for (int h = 0; h < chainsGeneration.getTerm().length(); h++) {
                        for (int k = 0; k < oldState.length(); k++) {
                            result += newTransitions[statesMap.get(String.valueOf(oldState.charAt(k)))][h];
                        }
                        // Реорганизация результата и обновление нового переходного массива
                        result = customChecker.rearrangeInOrder(result, chainsGeneration);
                        newTransitions[statesMap.get(oldState)][h] = result;
                        result = "";
                    }
                    counter++;
                }
            }
            clueNumber++;
            isNewState--;
        }

        return true;
    }

    private void exceptNonEntryStates(String[][] transform) {
        int currentCount;
        int isNewState = 1;
        Map<String, Integer> bufStatesDictionary = new HashMap<>();

        // Инициализация мапы состояний
        for (String key : statesMap.keySet()) {
            bufStatesDictionary.put(key, 0);
        }

        if (checkInference == 1) {
            bufStatesDictionary.put(beginLeftState, 1);
        } else {
            bufStatesDictionary.put(String.valueOf(chainsGeneration.getStartNonTerm().charAt(0)), 1);
        }

        currentCount = isNewState;

        while (true) {
            for (String key : statesMap.keySet()) {
                if (bufStatesDictionary.get(key) == 0) {
                    continue;
                }
                for (int j = 0; j < chainsGeneration.getTerm().length(); j++) {
                    isNewState++;
                    bufStatesDictionary.put(transform[statesMap.get(key)][j], 1);
                }
            }
            if (currentCount == isNewState) {
                break;
            }
            currentCount = isNewState;
            isNewState = 0;
        }

        for (String key : bufStatesDictionary.keySet()) {
            if (bufStatesDictionary.get(key) == 1) {
                resultMap.put(key, bufStatesDictionary.get(key));
            }
        }
    }

    private void addNewFinalStates(Map<String, Integer> resultDictionary) {
        String[] buffer = finalStates.split(",");

        for (String key : resultDictionary.keySet()) {
            if (!key.isEmpty()) {
                for (String string : buffer) {
                    for (int k = 0; k < key.length(); k++) {
                        if (string.equals(String.valueOf(key.charAt(k)))) {
                            finalStates += key + ",";
                        }
                    }
                }
            }
        }
    }

    private void printStatesToUser(String[][] transform) {
        DKAModel.setRowCount(0);

        DKAModel.addColumn("");
        for (int j = 0; j < chainsGeneration.getTerm().length(); j++) {
            DKAModel.addColumn(String.valueOf(chainsGeneration.getTerm().charAt(j)));
        }

        for (int i = 0; i < resultMap.size(); i++) {
            String key = (String) resultMap.keySet().toArray()[i];
            if (!key.isEmpty()) {
                Vector<Object> rowData = new Vector<>();
                rowData.add(key);

                rowData.addAll(Arrays.asList(transform[i]));

                DKAModel.addRow(rowData);
            }
        }
    }

    private void checkDKA(String chainString) {
        isStop = false;
        boolean isFinal = false;

        String newState;
        String currentState;

        outStates.setText("");
        List<String> list = new ArrayList<>();

        if (checkInference == 0) {
            currentState = (String) startSymbolComboBox.getSelectedItem();
        } else {
            currentState = beginLeftState;
        }

        list.add("(" + currentState + "," + chainString + ")");
        for (int i = 0; i < chainString.length(); i++) {
            if (!symbolsMap.containsKey(String.valueOf(chainString.charAt(i)))) {
                label.setText("В цепочке указано неизвестное значение: '" + chainString.charAt(i) + "'!");
                isStop = true;
                break;
            }
            newState = transformFunction(String.valueOf(chainString.charAt(i)), currentState);
            if (isStop) {
                break;
            }
            list.add("(" + newState + "," + chainString.substring(i + 1) + ")");
            currentState = newState;
        }
        if (!isStop) {
            String[] _finalStates = finalStates.split(",");
            for (String finalState : _finalStates) {
                assert currentState != null;
                if (currentState.equals(finalState)) {
                    isFinal = true;
                    label.setText("Разпознавание прошло успешно!");
                }
            }
            if (!isFinal) {
                label.setText("Автомат не перешел в конечное состояние!");
            }
        }
        for (String str : list) {
            outStates.append(str + "\n");
        }
    }

    private String transformFunction(String symbol, String currentState) {
        String newState;
        if (needToTransform) {
            newState = newTransitions[statesMap.get(currentState)][symbolsMap.get(symbol)];
        } else {
            newState = transitions[statesMap.get(currentState)][symbolsMap.get(symbol)];
        }
        if (newState.isEmpty()) {
            label.setText("Нет состояния перехода для: '" + symbol + "' и исходного состояния: '" + currentState + "'!");
            isStop = true;
        }
        return newState;
    }

    private void updateStartSymbolComboBox() {
        List<String> nonTerminal = customConverter.convertStringToList(nonTerminalField.getText());
        startSymbolComboBox.removeAllItems();

        for (String i : nonTerminal) {
            startSymbolComboBox.addItem(i);
        }
    }

}