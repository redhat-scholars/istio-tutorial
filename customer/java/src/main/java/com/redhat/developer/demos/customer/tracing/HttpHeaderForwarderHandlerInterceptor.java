package com.redhat.developer.demos.customer.tracing;

import com.google.common.collect.ImmutableSet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpHeaderForwarderHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final ThreadLocal<Map<String, List<String>>> HEADERS_THREAD_LOCAL = new ThreadLocal<>();

    private static final Set<String> FORWARDED_HEADER_NAMES = ImmutableSet.of(
            "x-request-id",
            "x-b3-traceid",
            "x-b3-spanid",
            "x-b3-parentspanid",
            "x-b3-sampled",
            "x-b3-flags",
            "x-ot-span-context",
            "user-agent"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, List<String>> headerMap = Collections.list(request.getHeaderNames()).stream()
                .map(String::toLowerCase)
                .filter(FORWARDED_HEADER_NAMES::contains)
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h))
                ));
        HEADERS_THREAD_LOCAL.set(headerMap);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HEADERS_THREAD_LOCAL.remove();
    }

    static Map<String, List<String>> getHeaders() {
        return HEADERS_THREAD_LOCAL.get();
    }

}
