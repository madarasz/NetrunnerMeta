package com.madarasz.netrunnerstats.springMVC.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Created by madarasz on 1/15/16.
 */
@Configuration
public class MyBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(MyBeanPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof RequestMappingHandlerMapping) {
            setRemoveSemicolonContent((RequestMappingHandlerMapping) bean,
                    beanName);
            setUseSuffixPatternMatch((RequestMappingHandlerMapping) bean,
                    beanName);
        }
        return bean;
    }

    private void setRemoveSemicolonContent(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            String beanName) {
        logger.info(
                "Setting 'RemoveSemicolonContent' on 'RequestMappingHandlerMapping'-bean to false. Bean name: {}",
                beanName);
        requestMappingHandlerMapping.setRemoveSemicolonContent(false);
    }

    private void setUseSuffixPatternMatch(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            String beanName) {
        logger.info(
                "Setting 'UseSuffixPatternMatch' on 'RequestMappingHandlerMapping'-bean to false. Bean name: {}",
                beanName);
        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
    }
}
