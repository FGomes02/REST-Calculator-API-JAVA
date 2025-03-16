package com.fgomes.rest;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@Order(1) // Ensures this filter is invoked early if necessary.
public class MDCFilter implements Filter {

    // Use a header name that's conventional for correlation IDs.
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    // The key used in the MDC
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            try {
                chain.doFilter(request, response);
            } finally {
                MDC.remove(CORRELATION_ID_MDC_KEY);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}