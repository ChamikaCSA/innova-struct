package com.example.backend.service;

import com.example.backend.dto.PortfolioDTO;
import com.example.backend.model.Company;
import com.example.backend.model.Project;
import com.example.backend.model.Certification;
import com.example.backend.repository.CompanyRepository;
import com.example.backend.repository.CertificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Company createPortfolio(PortfolioDTO portfolioDTO, List<MultipartFile> projectImages, List<MultipartFile> certificateImages) {
        // Find the company
        Optional<Company> companyOptional = companyRepository.findById(portfolioDTO.getCompanyId());
        if (!companyOptional.isPresent()) {
            throw new RuntimeException("Company not found with ID: " + portfolioDTO.getCompanyId());
        }

        Company company = companyOptional.get();

        // Update basic company information
        company.setName(portfolioDTO.getCompanyName());
        company.setLicense(portfolioDTO.getLicenseNumber());
        company.setShortDescription(portfolioDTO.getShortDescription());
        company.setEstablished(portfolioDTO.getEstablishedYear());
        company.setLocation(portfolioDTO.getLocation());
        company.setEmployees(portfolioDTO.getEmployeeCount());
        company.setServices(portfolioDTO.getServices());

        // Handle projects
        List<Project> projects = new ArrayList<>();
        int projectImageIndex = 0;
        for (PortfolioDTO.ProjectDTO projectDTO : portfolioDTO.getProjects()) {
            Project project = new Project();
            project.setTitle(projectDTO.getName());
            project.setDescription(projectDTO.getDescription());
            project.setYear(Integer.parseInt(projectDTO.getCompletionYear()));

            // Handle project images
            if (projectDTO.getImages() != null && !projectDTO.getImages().isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (String imageName : projectDTO.getImages()) {
                    if (projectImageIndex < projectImages.size()) {
                        String imageUrl = fileStorageService.storeFile(projectImages.get(projectImageIndex));
                        imageUrls.add(imageUrl);
                        projectImageIndex++;
                    }
                }
                project.setImage(String.join(",", imageUrls));
            }

            projects.add(project);
        }
        company.setProjects(projects);

        // Save company first to ensure it has an ID
        company = companyRepository.save(company);

        // Handle certifications
        if (portfolioDTO.getCertifications() != null) {
            List<Certification> certifications = new ArrayList<>();
            int certImageIndex = 0;
            for (PortfolioDTO.CertificationDTO certDTO : portfolioDTO.getCertifications()) {
                Certification certification = new Certification();
                certification.setName(certDTO.getName());
                certification.setOrganization(certDTO.getOrganization());
                certification.setIssueDate(certDTO.getIssueDate());
                certification.setExpiryDate(certDTO.getExpiryDate());
                certification.setCompany(company); // Set the company reference

                // Handle certificate image
                if (certImageIndex < certificateImages.size()) {
                    String imageUrl = fileStorageService.storeFile(certificateImages.get(certImageIndex));
                    certification.setImageUrl(imageUrl);
                    certImageIndex++;
                }

                // Save each certification
                certification = certificationRepository.save(certification);
                certifications.add(certification);
            }
            company.setCertifications(certifications);
        }

        // Handle contact information
        if (portfolioDTO.getContactInformation() != null) {
            company.setEmail(portfolioDTO.getContactInformation().get("email"));
            company.setPhoneNumber(portfolioDTO.getContactInformation().get("phoneNumber"));
            company.setWebsite(portfolioDTO.getContactInformation().get("website"));
        }

        // Handle financial information
        company.setAnnualRevenue(portfolioDTO.getAnnualRevenue());
        company.setFundingSources(portfolioDTO.getFundingSources());

        // Update timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        company.setUpdatedAt(LocalDateTime.now().format(formatter));
        if (company.getCreatedAt() == null) {
            company.setCreatedAt(LocalDateTime.now().format(formatter));
        }

        // Save the final updated company
        return companyRepository.save(company);
    }
}