package com.example.schwabro.terminology;

public class TermEntity {
    String term;
    String definition;
    String hyperlink;

    public TermEntity (String term, String definition, String hyperlink) {
        this.term = term;
        this.definition = definition;
        this.hyperlink = hyperlink;
    }

    public String toHtml() {
       return "<tr><td><b>" + term + "</b></td></tr>" +
                "<br><tr><td>" + definition + "</td></tr>" +
                "<tr><td>" +"<a href=\"" + hyperlink + "/\">Learn more...</a>"+ "</td></tr> <br>";
    }
}