package com.boxpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boxpro.service.VeiculoService;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {
    
    @Autowired
    private  VeiculoService veiculoService;
}
