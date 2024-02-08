package com.fakebank.account.balances.configs.tracing;

import io.prometheus.client.exemplars.tracer.common.SpanContextSupplier;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class TracingRequestFilter implements Filter {
    public static final String X_W3C_TRACE_ID = "X-W3C-TraceId";
    private final SpanContextSupplier spanContextSupplier;

    public TracingRequestFilter(SpanContextSupplier spanContextSupplier) {
        this.spanContextSupplier = spanContextSupplier;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader(
                X_W3C_TRACE_ID, spanContextSupplier.getTraceId());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
