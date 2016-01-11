package com.madarasz.netrunnerstats.helper.dialect;

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
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new CardElementProcessor());
        processors.add(new ImageElementProcessor());
        processors.add(new TooltipElementProcessor());
        return processors;
    }
}
