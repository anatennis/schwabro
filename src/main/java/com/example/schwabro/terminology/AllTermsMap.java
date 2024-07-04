package com.example.schwabro.terminology;

import com.example.schwabro.util.TermsUtil;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AllTermsMap {
    public static Map<String, TermEntity> ALL_TERMS_MAP = new TreeMap<>();

    public static String toHTML() {
        String str = String.valueOf(ALL_TERMS_MAP.values()
                        .stream()
                        .map(TermEntity::toHtml)
                        .collect(Collectors.toList()))
                .replace("[", "")
                .replace("]","")
                .replace("<br>,","<tr><br></tr>");

        return "<html><table>" + str + "</table></html>";
    }

    public static void firstUploadGlossary() {
        ALL_TERMS_MAP = TermsUtil.readAllTerms();
    }

    public static void addNewTerm(TermEntity term) {
        ALL_TERMS_MAP.put(term.getTerm(), term);

    }

    public static void removeMyTerms() {
        ALL_TERMS_MAP.entrySet().removeIf(entry-> entry.getValue().isMyTerm());
    }
}