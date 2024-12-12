package com.TaiNguyen.ProjectManagementSystems.Modal;

public class ProjectDetailsResponseWithPrediction extends ProjectDetailsResponse {

    private String predictedPerformanceBase64;

    public ProjectDetailsResponseWithPrediction(ProjectDetailsResponse projectDetailsResponse, String predictedPerformanceBase64) {
        super(projectDetailsResponse.getId(), projectDetailsResponse.getName(), projectDetailsResponse.getDescription(),
                projectDetailsResponse.getCategory(), projectDetailsResponse.getTags(), projectDetailsResponse.getFileNames(),
                projectDetailsResponse.getGoals(), projectDetailsResponse.getCreatedDate(), projectDetailsResponse.getEndDate(),
                projectDetailsResponse.getStatus(), projectDetailsResponse.getFundingAmount(), projectDetailsResponse.getProfitAmount(),
                projectDetailsResponse.getTeamMembers());
        this.predictedPerformanceBase64 = predictedPerformanceBase64;
    }

    public String getPredictedPerformanceBase64() {
        return predictedPerformanceBase64;
    }

    public void setPredictedPerformanceBase64(String predictedPerformanceBase64) {
        this.predictedPerformanceBase64 = predictedPerformanceBase64;
    }
}
