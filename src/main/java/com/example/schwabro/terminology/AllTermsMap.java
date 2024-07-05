package com.example.schwabro.terminology;

import com.example.schwabro.util.TermsUtil;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AllTermsMap {
    private static final Map<String, TermEntity> ALL_TERMS_MAP = new TreeMap<>();

    public static String toHTML() {
        String str = String.valueOf(ALL_TERMS_MAP.values()
                        .stream()
                        .map(TermEntity::toHtml)
                        .collect(Collectors.toList()))
                .replace("[", "")
                .replace("]", "")
                .replace("<br>,", "<tr><br></tr>");

        return "<html><table>" + str + "</table></html>";
    }

    public static Map<String, TermEntity> firstUploadGlossary() {
        if (ALL_TERMS_MAP.isEmpty())
            ALL_TERMS_MAP.putAll(TermsUtil.readAllTerms());
        return ALL_TERMS_MAP;
    }

    public static void addNewTerm(TermEntity term) {
        ALL_TERMS_MAP.put(term.getTerm(), term);
    }

    public static void removeMyTerms() {
        ALL_TERMS_MAP.entrySet().removeIf(entry -> entry.getValue().isMyTerm());
    }
}