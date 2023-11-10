package util;

import javax.swing.text.DefaultFormatter;
import java.text.ParseException;

public class NumericRangeFormatter extends DefaultFormatter {
    private final int minValue;
    private final int maxValue;

    public NumericRangeFormatter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (text == null || text.isEmpty()) {
            return minValue;
        }

        try {
            int value = Integer.parseInt(text);
            if (value >= minValue && value <= maxValue) {
                return value;
            }
        } catch (NumberFormatException ignored) {

        }
        throw new ParseException("Недопустимое значение", 0);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value instanceof Integer) {
            return value.toString();
        }
        throw new ParseException("Недопустимое значение", 0);
    }
}
