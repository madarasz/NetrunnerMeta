package com.madarasz.netrunnerstats.helper.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/2/16.
 *
 */
public class TooltipElementProcessor extends AbstractMarkupSubstitutionElementProcessor{

    public TooltipElementProcessor() {
        super("tooltip");
    }

    @Override
    protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
        Element container;
        container = new Element("a");
        container.setAttribute("class", "my-popover");
        container.setAttribute("data-placement", element.getAttributeValue("place"));
        container.setAttribute("data-toggle", "popover");
        container.setAttribute("data-content", element.getAttributeValue("text"));
        container.setAttribute("title", element.getAttributeValue("title"));
        Element sup = new Element("sup");
        Element ie = new Element("i");
        ie.setAttribute("class", "fa fa-info-circle");
        sup.addChild(ie);
        container.addChild(sup);

        List<Node> nodes = new ArrayList<>();
        nodes.add(container);
        return nodes;
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }
}
