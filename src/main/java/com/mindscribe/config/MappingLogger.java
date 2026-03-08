package com.mindscribe.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class MappingLogger {

    private final RequestMappingHandlerMapping handlerMapping;

    public MappingLogger(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logMappings() {
        handlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            System.out.println(
                mapping + " -> " +
                method.getMethod().getDeclaringClass().getSimpleName() +
                "#" + method.getMethod().getName()
            );
        });
    }
}

