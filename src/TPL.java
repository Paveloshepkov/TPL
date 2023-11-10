import UI.CursWorkFrame;
import UI.TranslationSchemeFrame;
import UI.GLCFrame;
import UI.PDAFrame;

import javax.swing.*;
import java.awt.*;

public class TPL extends JFrame {
    public TPL() {
        this.setTitle("Теория языков программирования");
        this.setSize(380, 200);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JButton button1 = new JButton("Генерация цепочек языка");
        JButton button2 = new JButton("Моделирование работы МПА");
        JButton button3 = new JButton("Перевод с помощью СУ-схемы");
        JButton button4 = new JButton("Курсовая работа Вариант 13");
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);
        this.add(panel);
        button1.addActionListener((e) -> new GLCFrame());
        button2.addActionListener((e) -> new PDAFrame());
        button3.addActionListener((e) -> new TranslationSchemeFrame());
        button4.addActionListener((e) -> new CursWorkFrame());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TPL form = new TPL();
            form.setVisible(true);
            form.setResizable(false);
        });
    }
}
