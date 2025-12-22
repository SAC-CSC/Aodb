package com.industrial.AODB.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StringFunctions {

    public String[] splitString(String str, String separator) {
        return str.split(separator, -1); // -1 keeps trailing empty values
    }

    public List<Integer> findIndexesList(String str, char delimiter) {
        List<Integer> indexList = new ArrayList<>();
        int index = str.indexOf(delimiter);

        while (index >= 0) {
            indexList.add(index);
            index = str.indexOf(delimiter, index + 1);
        }

        return indexList;
    }

    public List<String> getElementList(String str, List<Integer> foundIndexes) {
        List<String> elementList = new ArrayList<>();

        for (int i = 0; i < foundIndexes.size(); i++) {
            int start = foundIndexes.get(i);
            if (i < foundIndexes.size() - 1) {
                int end = foundIndexes.get(i + 1);
                elementList.add(str.substring(start, end));
            } else {
                // Last element trimming last 6 characters (same as C# logic)
                int len = str.length() - start - 6;
                elementList.add(str.substring(start, start + Math.max(0, len)));
            }
        }

        return elementList;
    }

    public String nullifyIfEmpty(String str) {
        return (str != null && str.isEmpty()) ? "null" : str;
    }

    public String getSeparatedElementValue(List<Integer> indexList, int pos, String element) {
        if (pos <= 0 || indexList.size() < pos) return "";

        int startPosition = indexList.get(pos - 1);
        if (indexList.size() > pos) {
            int endPosition = indexList.get(pos) - startPosition;
            return nullifyIfEmpty(element.substring(startPosition + 1, startPosition + endPosition)).trim();
        } else {
            return nullifyIfEmpty(element.substring(startPosition + 1)).trim();
        }
    }

    public String trimTelegram(String telegram) {
        telegram = telegram.trim();
        int pos = telegram.indexOf("ENDBSM");
        return pos != -1 ? telegram.substring(0, pos + 6) : telegram;
    }

    public String checkTelegramType(String telegram) {
        int pos1 = telegram.indexOf("BSM") + 3;
        int pos2 = telegram.indexOf(".", pos1); // ensure pos2 is after pos1

        if (pos1 < 0 || pos2 < 0 || pos2 <= pos1 || pos2 > telegram.length()) {
            return "INS"; // Default fallback
        }

        String type = telegram.substring(pos1, pos2).trim();
        return switch (type) {
            case "DEL" -> "DEL";
            case "CHG" -> "CHG";
            default -> "INS";
        };
    }

    public static int checkInvalidTelegram(String telegram) {
        telegram = telegram.trim();
        if (telegram.length() < 12) return 0;

        String start = telegram.substring(6, 9);
        String end = telegram.substring(telegram.length() - 6, telegram.length() - 3);

        return (start.equals("BSM") && end.equals("END")) ? 1 : 0;
    }
}
