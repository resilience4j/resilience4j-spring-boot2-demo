package io.github.robwin.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.robwin.service.BusinessService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/backendB")
public class BackendBController {

    private final BusinessService businessBService;

    public BackendBController(@Qualifier("businessBService")BusinessService businessBService){
        this.businessBService = businessBService;
    }

    @GetMapping("failure")
    public String backendBFailure(){
        return businessBService.failure();
    }

    @GetMapping("success")
    public String backendBSuccess(){
        return businessBService.success();
    }

    @GetMapping("ignore")
    public String ignore(){
        return businessBService.ignore();
    }

    @GetMapping("fluxFailure")
    public Flux<String> fluxFailure(){
        return businessBService.fluxFailure();
    }
}
