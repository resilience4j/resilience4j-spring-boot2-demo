package io.github.robwin.controller;

import io.github.robwin.service.BusinessService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/backendC")
public class BackendCController {

    private final BusinessService businessCService;

    public BackendCController(@Qualifier("businessCService")BusinessService businessCService){
        this.businessCService = businessCService;
    }

    @GetMapping("failure")
    public String backendCFailure(){
        return businessCService.failure();
    }

    @GetMapping("success")
    public String backendCSuccess(){
        return businessCService.success();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessCService.ignore();
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
}
