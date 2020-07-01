package group.msg.at.cloud.common.rest.internal.json;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public final class SimpleJsonBuilder {

    private static final String DOUBLE_QUOTE = "\"";
    private static final String VALUE_SEPARATOR = ":";
    private static final String ELEMENT_SEPARATOR = ", ";

    private final StringBuilder document = new StringBuilder();
    private final Deque<Integer> elements = new ArrayDeque<>();
    private int currentElementIndex = 0;

    public SimpleJsonBuilder add(String name, String value) {
        if (value != null && !value.isEmpty()) {
            if (currentElementIndex != 0) {
                document.append(ELEMENT_SEPARATOR);
            }
            currentElementIndex++;
            document.append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(VALUE_SEPARATOR).append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE);
        }
        return this;
    }

    public SimpleJsonBuilder add(String name, int value) {
        if (currentElementIndex != 0) {
            document.append(ELEMENT_SEPARATOR);
        }
        currentElementIndex++;
        document.append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(VALUE_SEPARATOR).append(value);
        return this;
    }

    public SimpleJsonBuilder add(String name, List<String> values) {
        if (values != null && !values.isEmpty()) {
            if (values.size() == 1) {
                add(name, values.get(0));
            } else {
                if (currentElementIndex != 0) {
                    document.append(ELEMENT_SEPARATOR);
                }
                currentElementIndex++;
                document.append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(VALUE_SEPARATOR).append("[");
                int currentValueIndex = 0;
                for (String currentValue : values) {
                    if (currentValueIndex > 0) {
                        document.append(",");
                    }
                    document.append(DOUBLE_QUOTE).append(currentValue).append(DOUBLE_QUOTE);
                    currentValueIndex++;
                }
                document.append("]");
            }
        }
        return this;
    }

    public SimpleJsonBuilder startObject(String name) {
        if (currentElementIndex != 0) {
            document.append(ELEMENT_SEPARATOR);
        }
        elements.push(++currentElementIndex);
        currentElementIndex = 0;
        document.append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(" : { ");
        return this;
    }

    public SimpleJsonBuilder stopObject(String name) {
        document.append(" }");
        currentElementIndex = elements.pop();
        return this;
    }

    public SimpleJsonBuilder startMap(String name) {
        if (currentElementIndex != 0) {
            document.append(ELEMENT_SEPARATOR);
        }
        elements.push(++currentElementIndex);
        currentElementIndex = 0;
        document.append(DOUBLE_QUOTE).append(name).append(DOUBLE_QUOTE).append(" : { ");
        return this;
    }

    public SimpleJsonBuilder stopMap(String name) {
        document.append(" }");
        currentElementIndex = elements.pop();
        return this;
    }

    public String build() {
        return document.toString();
    }
}
