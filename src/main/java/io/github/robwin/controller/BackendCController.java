package io.github.robwin.controller;

import io.github.robwin.service.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/backendC")
public class BackendCController {

    private final Service businessCService;

    public BackendCController(@Qualifier("backendCService")Service businessCService){
        this.businessCService = businessCService;
    }

    @GetMapping("failure")
    public String failure(){
        return businessCService.failure();
    }

    @GetMapping("success")
    public String success(){
        return businessCService.success();
    }

    @GetMapping("successException")
    public String successException(){
        return businessCService.successException();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessCService.ignoreException();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return businessCService.monoSuccess();
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure(){
        return businessCService.monoFailure();
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return businessCService.fluxSuccess();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessCService.fluxFailure();
    }

    @GetMapping("monoTimeout")
    public Mono<String> monoTimeout(){
        return businessCService.monoTimeout();
    }

    @GetMapping("fluxTimeout")
    public Flux<String> fluxTimeout(){
        return businessCService.fluxTimeout();
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return businessCService.futureFailure();
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return businessCService.futureSuccess();
    }

    @GetMapping("futureTimeout")
    public CompletableFuture<String> futureTimeout(){
        return businessCService.futureTimeout();
    }

    @GetMapping("fallback")
    public String failureWithFallback(){
        return businessCService.failureWithFallback();
    }
}
