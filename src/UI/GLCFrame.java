package UI;

import Labs.GLC.ContextFreeGrammar;
import Labs.GLC.Generator;
import util.NumericRangeFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GLCFrame extends JFrame {
    private final JTextField nonTerminalField = new JTextField();
    private final JTextField terminalField = new JTextField();
    private final JTextField minLengthField = new JFormattedTextField(new NumericRangeFormatter(0, 99));
    private final JTextField maxLengthField = new JFormattedTextField(new NumericRangeFormatter(0, 99));
    private final JRadioButton leftInference = new JRadioButton("Левосторонний");
    private final JRadioButton rightInference = new JRadioButton("Правосторонний");
    private final JComboBox<String> StartSymbolComboBox = new JComboBox<>();
    private final JPanel innerVerticalPanel = new JPanel();

    public GLCFrame() {

        setTitle("Лабораторная работа 1");
        setSize(600, 600);

        int spacing = 3;

        this.setVisible(true);
        this.setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(leftInference);
        radioButtonGroup.add(rightInference);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(16, 1, spacing, spacing));
        gridPanel.setMaximumSize(new Dimension(200, 40));
        JButton load = new JButton("Загрузить граматику");
        gridPanel.add(load);
        JButton startAction = new JButton("Выполнить");
        gridPanel.add(startAction);
        gridPanel.add(new JLabel("Терминальные символы"));
        gridPanel.add(terminalField);
        gridPanel.add(new JLabel("Нетерминальные символы"));
        gridPanel.add(nonTerminalField);
        gridPanel.add(new JLabel("Целевой символ"));
        gridPanel.add(StartSymbolComboBox);
        gridPanel.add(new JLabel("Минимальная длина"));
        gridPanel.add(minLengthField);
        gridPanel.add(new JLabel("Максимальная длина"));
        gridPanel.add(maxLengthField);
        gridPanel.add(new JLabel("Тип вывода:"));
        gridPanel.add(leftInference);
        gridPanel.add(rightInference);
        JButton addRuleButton = new JButton("Добавить правило:");
        gridPanel.add(addRuleButton);

        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        verticalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        verticalPanel.add(gridPanel);

        innerVerticalPanel.setLayout(new BoxLayout(innerVerticalPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPaneVertical = new JScrollPane(innerVerticalPanel);
        scrollPaneVertical.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneVertical.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        verticalPanel.add(scrollPaneVertical);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        mainPanel.add(verticalPanel);

        add(mainPanel);

        JTextArea outData = new JTextArea(10, 30);
        outData.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outData);
        mainPanel.add(scrollPane);

        addRuleButton.addActionListener(e -> {
            JPanel horizontalPanel = new JPanel();
            horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
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
            textField1.setMaximumSize(new Dimension(120, 25));
            textField2.setMaximumSize(new Dimension(120, 25));

            JButton deleteButton = new JButton("Х");
            deleteButton.setMaximumSize(new Dimension(50, 25));

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

        nonTerminalField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateStartSymbolComboBox();
            }
        });

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
                    Map<String, String> grammarRules = new HashMap<>();

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
                    StartSymbolComboBox.setSelectedItem(startSymbol);

                    innerVerticalPanel.removeAll();
                    innerVerticalPanel.revalidate();
                    innerVerticalPanel.repaint();

                    for (Map.Entry<String, String> entry : grammarRules.entrySet()) {

                        JPanel horizontalPanel = new JPanel();
                        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
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
                        textField1.setMaximumSize(new Dimension(120, 25));
                        textField2.setMaximumSize(new Dimension(120, 25));

                        JButton deleteButton = new JButton("Х");
                        deleteButton.setMaximumSize(new Dimension(50, 25));

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
                    System.out.println("Не удалость считать граматику");
                    ex.printStackTrace();
                }
            }
        });

        startAction.addActionListener(e -> {

            if (isValidInput()) {

                ContextFreeGrammar cfg = new ContextFreeGrammar(
                        convertStringToList(terminalField.getText()),
                        convertStringToList(nonTerminalField.getText()),
                        (String) StartSymbolComboBox.getSelectedItem(),
                        collectProductionRules()
                );

                Generator generator = new Generator(
                        Integer.parseInt(minLengthField.getText()),
                        Integer.parseInt(maxLengthField.getText()),
                        leftInference.isSelected()
                );
                outData.setText("");
                List<String> list = generator.generateStrings(cfg);
                for (String generatedString : list) {
                    outData.append(generatedString + "\n");
                }
            }
        });

    }

    private Map<String, List<String>> collectProductionRules() {
        Map<String, List<String>> productionRules = new HashMap<>();
        boolean atLeastOneFieldCreated = false;

        for (Component component : innerVerticalPanel.getComponents()) {
            if (component instanceof JPanel horizontalPanel) {
                Component[] components = horizontalPanel.getComponents();

                if (components.length == 3 && components[0] instanceof JTextField textField1 && components[1] instanceof JTextField textField2 && components[2] instanceof JButton) {
                    atLeastOneFieldCreated = true;
                    String nonTerminal = textField1.getText();
                    String productionRulesText = textField2.getText();
                    List<String> rules = Arrays.asList(productionRulesText.split("\\s*\\|\\s*"));
                    productionRules.put(nonTerminal, rules);
                }
            }
        }

        if (!atLeastOneFieldCreated) {
            JOptionPane.showMessageDialog(this, "Не создано ни одного правила.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return productionRules;
    }

    private void updateStartSymbolComboBox() {
        List<String> nonTerminal = convertStringToList(nonTerminalField.getText());
        StartSymbolComboBox.removeAllItems();

        for (String i : nonTerminal) {
            StartSymbolComboBox.addItem(i);
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

    private boolean isValidInput() {
        String noTerminalValue = nonTerminalField.getText();
        String terminalValue = terminalField.getText();
        String startSymbolValue = (String) StartSymbolComboBox.getSelectedItem();
        String minLengthValue = minLengthField.getText();
        String maxLengthValue = maxLengthField.getText();

        if (noTerminalValue.isEmpty() || terminalValue.isEmpty() || (startSymbolValue != null && startSymbolValue.isEmpty()) ||
                minLengthValue.isEmpty() || maxLengthValue.isEmpty() || (!leftInference.isSelected() && !rightInference.isSelected())) {
            JOptionPane.showMessageDialog(this, "Заполните все поля и выберите тип вывода.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (Integer.parseInt(minLengthValue) > Integer.parseInt(maxLengthValue)) {
            JOptionPane.showMessageDialog(this, "Заполните поля минимальной и максимальной длинны корректно", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}