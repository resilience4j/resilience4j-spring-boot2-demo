package io.github.robwin.controller;

import io.github.robwin.service.BusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/backendA")
public class BackendAController {

    private final BusinessService businessAService;

    public BackendAController(@Qualifier("businessAService") BusinessService businessAService){
        this.businessAService = businessAService;
    }

    @GetMapping("failure")
    public String failure(){
        return businessAService.failure();
    }

    @GetMapping("success")
    public String success(){
        return businessAService.success();
    }

    @GetMapping("successException")
    public String successException(){
        return businessAService.successException();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessAService.ignore();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return businessAService.monoSuccess();
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure(){
        return businessAService.monoFailure();
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return businessAService.fluxSuccess();
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return businessAService.futureFailure();
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return businessAService.futureSuccess();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessAService.fluxFailure();
    }

    @GetMapping("fallback")
    public String failureWithFallback(){
        return businessAService.failureWithFallback();
    }
}
