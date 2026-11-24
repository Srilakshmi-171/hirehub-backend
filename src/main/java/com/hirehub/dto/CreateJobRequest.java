
package com.hirehub.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 150, message = "Job title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Company name is required")
    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    private String companyName;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    @NotBlank(message = "Required skills are required")
    @Size(max = 1000, message = "Required skills cannot exceed 1000 characters")
    private String requiredSkills;

    @Positive(message = "Vacancies must be greater than zero")
    private int vacancies;

    @Positive(message = "Salary must be greater than zero")
    private BigDecimal salary;

    @NotBlank(message = "Experience level is required")
    @Pattern(
        regexp = "^(FRESHER|0-1|1-2|2-3|3-5|5\\+)$",
        message = "Experience level must be FRESHER, 0-1, 1-2, 2-3, 3-5, or 5+"
    )
    private String experienceLevel;

    public CreateJobRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getVacancies() {
        return vacancies;
    }

    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
