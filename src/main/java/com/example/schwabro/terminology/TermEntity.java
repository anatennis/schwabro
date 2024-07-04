package com.example.schwabro.terminology;

public class TermEntity {
    String term;
    String definition;
    String hyperlink;
    boolean isMyTerm = false;

    public TermEntity (String term, String definition, String hyperlink) {
        this.term = term;
        this.definition = definition;
        this.hyperlink = hyperlink;
    }

    public TermEntity (String term) {
        this.term = term;
    }

    public String toHtml() {
       return "<tr><td><b>" + term + "</b></td></tr>" +
                "<br><tr><td>" + definition + "</td></tr>" +
                "<tr><td>" +"<a href=\"" + hyperlink + "/\">Learn more...</a>"+ "</td></tr> <br>";
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public String getHyperlink() {
        return hyperlink;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public boolean isMyTerm() {
        return isMyTerm;
    }

    public void setMyTermTrue() {
        isMyTerm = true;
    }
}