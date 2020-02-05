package com.madarasz.netrunnerstats.helper.dialect;

import com.madarasz.netrunnerstats.database.DOs.Card;
import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.context.SpringWebContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madarasz on 1/2/16.
 *
 */
public class ImageElementProcessor extends AbstractMarkupSubstitutionElementProcessor{

    public ImageElementProcessor() {
        super("img");
    }

    @Override
    protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
        final ApplicationContext appCtx = ((SpringWebContext)arguments.getContext()).getApplicationContext();
        final CardRepository cardRepository = appCtx.getBean(CardRepository.class);
        String title = element.getAttributeValue("title");
        Card card = cardRepository.findByTitle(title);
        Element container;
        if (card != null) {
            container = new Element("a");
            container.setAttribute("href", "/Cards/" + card.getTitle() + "/");
            Element image = new Element("img");
            image.setAttribute("src", card.getImageSrc());
            image.setAttribute("alt", "Netrunner " + title);
            image.setAttribute("class", "card-" + element.getAttributeValue("size"));
            container.addChild(image);
        } else {
            container = new Element("u");
            container.setAttribute("class", "link-card-broken");
            container.addChild(new Text(title));
        }

        List<Node> nodes = new ArrayList<>();
        nodes.add(container);
        return nodes;
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

}
