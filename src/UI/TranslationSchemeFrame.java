package UI;

import Labs.TranslationScheme.TranslationSUScheme;

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

public class TranslationSchemeFrame extends JFrame {
    private final JTextField nonTerminalField = new JTextField();
    private final JTextField inputAlphabetField = new JTextField();
    private final JTextField outputAlphabetField = new JTextField();
    private final JTextField chainTransitionField = new JTextField();
    private final JComboBox<String> StartSymbolComboBox = new JComboBox<>();
    private final JPanel innerVerticalPanel = new JPanel();

    public TranslationSchemeFrame() {

        setTitle("Лабораторная работа 4");
        setSize(600, 600);

        int spacing = 3;

        this.setVisible(true);
        this.setResizable(false);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(13, 1, spacing, spacing));
        gridPanel.setMaximumSize(new Dimension(200, 40));
        JButton load = new JButton("Загрузить граматику");
        gridPanel.add(load);
        JButton startAction = new JButton("Выполнить");
        gridPanel.add(startAction);
        gridPanel.add(new JLabel("Входной алфавит"));
        gridPanel.add(inputAlphabetField);
        gridPanel.add(new JLabel("Нетерминальные символы"));
        gridPanel.add(nonTerminalField);
        gridPanel.add(new JLabel("Целевой символ"));
        gridPanel.add(StartSymbolComboBox);
        gridPanel.add(new JLabel("Выходной алфавит"));
        gridPanel.add(outputAlphabetField);
        gridPanel.add(new JLabel("Цепочка для перевода"));
        gridPanel.add(chainTransitionField);
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
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Отступы от краев формы

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
                    String inputAlphabet = "";
                    String outputAlphabet = "";
                    String startSymbol = "";

                    Map<String, String> grammarRules = new HashMap<>();

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("// множество нетерминальных символов")) {
                            nonTerminals = reader.readLine();
                        } else if (line.startsWith("// алфавит входного языка")) {
                            inputAlphabet = reader.readLine();
                        } else if (line.startsWith("// алфавит выходного языка")) {
                            outputAlphabet = reader.readLine();
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
                    inputAlphabetField.setText(inputAlphabet);
                    outputAlphabetField.setText(outputAlphabet);
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
                TranslationSUScheme translationSUSchemeMain = new TranslationSUScheme(
                        convertStringToList(nonTerminalField.getText()),
                        convertStringToList(inputAlphabetField.getText()),
                        convertStringToList(outputAlphabetField.getText()),
                        collectProductionRules(),
                        (String) StartSymbolComboBox.getSelectedItem(),
                        chainTransitionField.getText()
                );
                outData.setText("");
                List<String> list = translationSUSchemeMain.getResult();
                for (String generatedString : list) {
                    outData.append(generatedString + "\n");
                }

            }
        });
    }

    private TreeMap<String, List<String>> collectProductionRules() {
        TreeMap<String, List<String>> productionRules = new TreeMap<>();
        boolean atLeastOneFieldCreated = false;

        for (Component component : innerVerticalPanel.getComponents()) {
            if (component instanceof JPanel horizontalPanel) {
                Component[] components = horizontalPanel.getComponents();

                if (components.length == 3 && components[0] instanceof JTextField textField1 && components[1] instanceof JTextField textField2 && components[2] instanceof JButton) {
                    atLeastOneFieldCreated = true;
                    String nonTerminal = textField1.getText();
                    String productionRulesText = textField2.getText();
                    List<String> rules = Arrays.asList(productionRulesText.split(","));
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
        String inputAlphabetValue = inputAlphabetField.getText();
        String startSymbolValue = (String) StartSymbolComboBox.getSelectedItem();
        String outputAlphabetValue = outputAlphabetField.getText();
        String chainTransitionValue = chainTransitionField.getText();

        if (noTerminalValue.isEmpty() || inputAlphabetValue.isEmpty() || (startSymbolValue != null && startSymbolValue.isEmpty()) ||
                outputAlphabetValue.isEmpty() || chainTransitionValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля и выберите тип вывода.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}