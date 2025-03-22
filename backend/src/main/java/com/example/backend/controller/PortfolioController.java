package com.example.backend.controller;

import com.example.backend.dto.PortfolioDTO;
import com.example.backend.model.Company;
import com.example.backend.service.PortfolioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/companies/portfolio")
@CrossOrigin(origins = "*")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> createPortfolio(
            @RequestParam("companyData") String companyDataJson,
            @RequestParam(value = "projectImages", required = false) List<MultipartFile> projectImages,
            @RequestParam(value = "certificateImages", required = false) List<MultipartFile> certificateImages) {
        try {
            // Convert JSON string to PortfolioDTO
            PortfolioDTO portfolioDTO = objectMapper.readValue(companyDataJson, PortfolioDTO.class);

            // Handle null lists
            if (projectImages == null) {
                projectImages = new ArrayList<>();
            }
            if (certificateImages == null) {
                certificateImages = new ArrayList<>();
            }

            // Create portfolio
            Company updatedCompany = portfolioService.createPortfolio(portfolioDTO, projectImages, certificateImages);

            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCompany);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating portfolio: " + e.getMessage());
        }
    }
}