package stroom.security.client.presenter;

import stroom.widget.util.client.SafeHtmlUtil;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DescriptionBuilder {

    private final SafeHtmlBuilder sb = new SafeHtmlBuilder();
    private boolean written;

    public void addLine(final String text) {
        addLine(false, false, false, SafeHtmlUtil.getSafeHtml(text));
    }

    public void addLine(final boolean isBold,
                        final boolean isInherited,
                        final String text) {
        addLine(isBold, isInherited, false, SafeHtmlUtil.getSafeHtml(text));
    }

    public void addLine(final boolean isBold,
                        final boolean isInherited,
                        final SafeHtml text) {
        addLine(isBold, isInherited, false, text);
    }

    public void addLine(final boolean isBold,
                        final boolean isInherited,
                        final boolean isDelimiter,
                        final SafeHtml text) {
        final ClassNameBuilder classNameBuilder = new ClassNameBuilder();
        if (isInherited) {
            classNameBuilder.addClassName("inherited");
        }
        if (isBold) {
            classNameBuilder.addClassName("bold");
        }
        if (isDelimiter) {
            classNameBuilder.addClassName("delimiter");
        }

        sb.appendHtmlConstant("<span" + classNameBuilder.buildClassAttribute() + ">");
        sb.append(text);
        sb.appendHtmlConstant("</span>");
        written = true;
    }

    public void addTitle(final String title) {
        addLine(true, false, title);
    }

    public void append(final SafeHtml safeHtml) {
        sb.append(safeHtml);
        written = true;
    }

    public void addNewLine() {
        if (written) {
            sb.appendHtmlConstant("<br/>");
        }
    }

    public SafeHtml toSafeHtml() {
        return sb.toSafeHtml();
    }
}
