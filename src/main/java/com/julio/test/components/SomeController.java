
package com.julio.test.components;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
public class SomeController {
    private List<String> people = Arrays.asList("Julio", "Hans", "Valerie");
    private final MeterRegistry registry;

    public SomeController(MeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/api/people")
    @Timed(percentiles = {0.5, 0.95, 0.999}, histogram = true)
    @HystrixCommand(fallbackMethod = "fallbackPeople")
    public List<String> allPeople() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return people;
    }

    @GetMapping("/api/peopleAsync")
    public CompletableFuture<Collection<String>> personNamesAsync() {
        return CompletableFuture.supplyAsync(() -> Collections.singletonList("jon"));
    }


    @SuppressWarnings("unused")
    public List<String> fallbackPeople() {
        return Arrays.asList("fallback Hans", "fallback Valerie", "fallback Julio");
    }

    @GetMapping("/api/fail")
    public String fail() {
        throw new RuntimeException("ops");
    }

    @GetMapping("/api/stats")
    public Map<String, Number> stats() {
        return Optional.ofNullable(registry.find("http.server.requests").tags("uri", "/api/people")
            .timer())
            .map(t -> new HashMap<String, Number>() {{
                put("count", t.count());
                put("max", t.max(TimeUnit.MILLISECONDS));
                put("mean", t.mean(TimeUnit.MILLISECONDS));
                put("50.percentile", t.percentile(0.5, TimeUnit.MILLISECONDS));
                put("95.percentile", t.percentile(0.95, TimeUnit.MILLISECONDS));
            }})
            .orElse(null);
    }
}
