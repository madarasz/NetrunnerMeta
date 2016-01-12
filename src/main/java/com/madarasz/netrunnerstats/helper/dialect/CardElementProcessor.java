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
public class CardElementProcessor extends AbstractMarkupSubstitutionElementProcessor{

    public CardElementProcessor() {
        super("card");
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
            container.setAttribute("href", "/Cards/" + card.getTitle());
            container.setAttribute("class", "link-card");
            container.addChild(new Text(title));
        } else {
            container = new Element("span");
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
