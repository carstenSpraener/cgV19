package de.spraener.nxtgen.target;

/**
 * Renders a CodeTarget into Java source code format.
 */
public class JavaRenderer implements CodeTargetRenderer {

    private static final String INDENT_UNIT = "    ";

    private boolean withMarkers = false;

    /**
     * Enable section markers (comments) in the output.
     */
    public JavaRenderer withMarkers() {
        this.withMarkers = true;
        return this;
    }

    @Override
    public String render(CodeTarget codeTarget) {
        StringBuilder sb = new StringBuilder();
        for (CodeSection section : codeTarget.getSectionsOrdered()) {
            renderSection(sb, section, 0);
        }
        return sb.toString();
    }

    private void renderSection(StringBuilder sb, CodeSection section, int indentLevel) {
        String pad = INDENT_UNIT.repeat(indentLevel);
        if (withMarkers) {
            sb.append(pad).append("<<section id=").append(section.getId()).append(">>\n");
        }

        if (section.rendersChildrenInline()) {
            for (CodeSnippet snippet : section.getSnippetsOrdered()) {
                renderSnippet(sb, snippet, pad);
            }
        } else {
            for (CodeSnippet snippet : section.getOwnSnippets()) {
                renderSnippet(sb, snippet, pad);
            }
            for (CodeSection child : section.getChildren()) {
                renderSection(sb, child, indentLevel + 1);
            }
        }

        if (withMarkers) {
            sb.append(pad).append("<</section:").append(section.getId()).append(">>\n");
        }
    }

    private void renderSnippet(StringBuilder sb, CodeSnippet snippet, String pad) {
        StringBuilder snippetSb = new StringBuilder();
        snippet.evaluate(snippetSb);
        String line = snippetSb.toString().trim();
        if (!line.isEmpty()) {
            sb.append(pad).append(line).append("\n");
        }
    }
}
