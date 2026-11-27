package com.coffeeshop.helpers;

public class DataOfFields {
    private final String userId = "userId";
    private final String id = "id";
    private final String description = "description";
    private final String status = "status";
    private final String createdAt = "createdAt";
    private final String error = "error";
    private final String message = "message";
    private final String details = "details";
    private final String statusNew = "NEW";
    private final String statusInProgress = "IN_PROGRESS";
    private final String statusCompleted = "COMPLETED";
    private final String badRequest = "Bad Request";
    private final String notFound = "Not Found";
    private final String validationFailed = "Validation failed";
    private final String statusMustBe = "Status must be NEW, IN_PROGRESS or COMPLETED";
    private final String name = "name";
    private final String email = "email";
    private final String asdEmail = "asd@asd.asd";
    private final String testName = "Test";
    private final String asdDescription = "asd";
    private final String sizeForBody = "size()";


    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public String getStatusNew() {
        return statusNew;
    }

    public String getStatusInProgress() {
        return statusInProgress;
    }

    public String getStatusCompleted() {
        return statusCompleted;
    }

    public String getBadRequest() {
        return badRequest;
    }

    public String getNotFound() {
        return notFound;
    }

    public String getValidationFailed() {
        return validationFailed;
    }

    public String getStatusMustBe() {
        return statusMustBe;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAsdEmail() {
        return asdEmail;
    }

    public String userNotFoundMessage(int id) {
        return "User with id " + id + " not found";
    }

    public String orderNotFoundMessage(int id) {
        return "Order with id " + id + " not found";
    }

    public String userAlreadyExistsMessage(String email) {
        return "User with email " + email + " already exists";
    }

    public String getTestName() {
        return testName;
    }

    public String getAsdDescription() {
        return asdDescription;
    }

    public String getSizeForBody() {
        return sizeForBody;
    }
}
