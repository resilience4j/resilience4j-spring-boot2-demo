package io.github.robwin.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.robwin.service.BusinessService;
import reactor.core.publisher.Flux;

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

    @GetMapping("ignore")
    public String ignore(){
        return businessAService.ignore();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessAService.fluxFailure();
    }

    @GetMapping("recover")
    public String methodWithRecovery(){
        return businessAService.methodWithRecovery().get();
    }
}
