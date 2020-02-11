package io.github.robwin.annotation;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.lang.annotation.*;

@Decorators
@CircuitBreaker(name = "")
@Retry(name = "")
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface ComposedDecorator {
    String name();
}
