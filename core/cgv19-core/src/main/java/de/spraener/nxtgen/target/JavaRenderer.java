package de.spraener.nxtgen.target;

public class JavaRenderer implements CodeTargetRenderer {
    private boolean withMarkers = false;
    private String commentPrefix = "//";

    public JavaRenderer() {
    }

    @Override
    public String render(CodeTarget target) {
        CodeTargetToCodeConverter converter = new CodeTargetToCodeConverter(target)
                .withMarkers(withMarkers)
                .withSingleLineCommentPrefix(commentPrefix);
        return converter.toString();
    }

    public JavaRenderer withMarkers() {
        this.withMarkers = true;
        return this;
    }

    public JavaRenderer withCommentPrefix(String prefix) {
        this.commentPrefix = prefix;
        return this;
    }
}
