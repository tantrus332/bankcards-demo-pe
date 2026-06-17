package com.tantrus332.bankcards.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetricsFilter implements Filter {

  private final MeterRegistry meterRegistry;
  private final Counter httpRequestCounter;
  private final Timer httpRequestTimer;

  public MetricsFilter(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    this.httpRequestCounter = Counter.builder("http_requests_total")
        .description("Total HTTP requests")
        .tag("application", "bankcards-api")
        .register(meterRegistry);

    this.httpRequestTimer = Timer.builder("http_request_duration_seconds")
        .description("HTTP request duration in seconds")
        .tag("application", "bankcards-api")
        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
        .publishPercentileHistogram()
        .register(meterRegistry);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;

    String method = httpServletRequest.getMethod();
    String uri = httpServletRequest.getRequestURI();

    long startTime = System.nanoTime();
    try {
      chain.doFilter(request, response);
    } finally {
      long duration = System.nanoTime() - startTime;
      int status = httpServletResponse.getStatus();

      String endpoint = normalizeEndpoint(uri);
      String statusClass = String.valueOf(status / 100) + "xx";

      Counter.builder("http_requests_total")
          .description("Total HTTP requests")
          .tag("application", "bankcards-api")
          .tag("method", method)
          .tag("endpoint", endpoint)
          .tag("status", String.valueOf(status))
          .register(meterRegistry)
          .increment();

      Timer.builder("http_request_duration_seconds")
          .description("HTTP request duration in seconds")
          .tag("application", "bankcards-api")
          .tag("method", method)
          .tag("endpoint", endpoint)
          .tag("status", String.valueOf(status))
          .publishPercentiles(0.5, 0.75, 0.95, 0.99)
          .publishPercentileHistogram()
          .register(meterRegistry)
          .record(duration, TimeUnit.NANOSECONDS);
    }
  }

  private String normalizeEndpoint(String uri) {
    if (uri.startsWith("/api/v1/bank-cards")) {
      if (uri.contains("/transfer")) return "/api/v1/bank-cards/transfer";
      if (uri.matches(".*/\\d+/request-block.*")) return "/api/v1/bank-cards/{id}/request-block";
      if (uri.matches(".*/\\d+/details.*")) return "/api/v1/bank-cards/{id}/details";
      if (uri.matches(".*/\\d+.*")) return "/api/v1/bank-cards/{id}";
      return "/api/v1/bank-cards";
    }
    if (uri.startsWith("/api/v1/admin")) {
      if (uri.contains("/users")) {
        if (uri.matches(".*/users/\\d+.*")) return "/api/v1/admin/users/{id}";
        return "/api/v1/admin/users";
      }
      if (uri.contains("/bank-cards")) {
        if (uri.contains("/activate")) return "/api/v1/admin/bank-cards/activate/{id}";
        if (uri.contains("/confirm-block")) return "/api/v1/admin/bank-cards/confirm-block/{id}";
        if (uri.contains("/with-block-request")) return "/api/v1/admin/bank-cards/with-block-request";
        if (uri.matches(".*/bank-cards/\\d+.*")) return "/api/v1/admin/bank-cards/{id}";
        return "/api/v1/admin/bank-cards";
      }
    }
    if (uri.startsWith("/actuator")) return "/actuator";
    return uri;
  }
}
