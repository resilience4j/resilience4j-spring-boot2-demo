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
@RequestMapping(value = "/backendB")
public class BackendBController {

    private final BusinessService businessBService;

    public BackendBController(@Qualifier("businessBService")BusinessService businessBService){
        this.businessBService = businessBService;
    }

    @GetMapping("failure")
    public String failure(){
        return businessBService.failure();
    }

    @GetMapping("success")
    public String success(){
        return businessBService.success();
    }

    @GetMapping("successException")
    public String successException(){
        return businessBService.successException();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessBService.ignore();
    }

    @GetMapping("monoSuccess")
    public Mono<String> monoSuccess(){
        return businessBService.monoSuccess();
    }

    @GetMapping("monoFailure")
    public Mono<String> monoFailure(){
        return businessBService.monoFailure();
    }

    @GetMapping("fluxSuccess")
    public Flux<String> fluxSuccess(){
        return businessBService.fluxSuccess();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessBService.fluxFailure();
    }

    @GetMapping("futureFailure")
    public CompletableFuture<String> futureFailure(){
        return businessBService.futureFailure();
    }

    @GetMapping("futureSuccess")
    public CompletableFuture<String> futureSuccess(){
        return businessBService.futureSuccess();
    }

    @GetMapping("fallback")
    public String failureWithFallback(){
        return businessBService.failureWithFallback();
    }
}
