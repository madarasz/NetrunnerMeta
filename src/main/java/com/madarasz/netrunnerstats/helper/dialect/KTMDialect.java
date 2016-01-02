package com.madarasz.netrunnerstats.helper.dialect;

import com.madarasz.netrunnerstats.database.DRs.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by madarasz on 1/2/16.
 * New dialect for Thymeleaf.
 */
public class KTMDialect extends AbstractDialect {

    @Override
    public String getPrefix() {
        return "ktm";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new CardElementProcessor());
        processors.add(new ImageElementProcessor());
        return processors;
    }
}
