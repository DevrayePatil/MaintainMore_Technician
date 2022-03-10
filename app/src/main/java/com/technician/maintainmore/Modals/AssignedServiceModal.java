package com.technician.maintainmore.Modals;

public class AssignedServiceModal {

    String whoBookedService, userName, userEmail, userPhoneNumber,
            bookingID, serviceIcon, serviceName, totalServices, servicePrice, totalServicesPrice,
            bookingDate, bookingTime, visitingDate, visitingTime;

    public AssignedServiceModal(String whoBookedService, String userName, String userEmail, String userPhoneNumber, String bookingID, String serviceIcon, String serviceName, String totalServices, String servicePrice, String totalServicesPrice, String bookingDate, String bookingTime, String visitingDate, String visitingTime) {
        this.whoBookedService = whoBookedService;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.bookingID = bookingID;
        this.serviceIcon = serviceIcon;
        this.serviceName = serviceName;
        this.totalServices = totalServices;
        this.servicePrice = servicePrice;
        this.totalServicesPrice = totalServicesPrice;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.visitingDate = visitingDate;
        this.visitingTime = visitingTime;
    }

    public String getWhoBookedService() {
        return whoBookedService;
    }

    public void setWhoBookedService(String whoBookedService) {
        this.whoBookedService = whoBookedService;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(String totalServices) {
        this.totalServices = totalServices;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getTotalServicesPrice() {
        return totalServicesPrice;
    }

    public void setTotalServicesPrice(String totalServicesPrice) {
        this.totalServicesPrice = totalServicesPrice;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getVisitingDate() {
        return visitingDate;
    }

    public void setVisitingDate(String visitingDate) {
        this.visitingDate = visitingDate;
    }

    public String getVisitingTime() {
        return visitingTime;
    }

    public void setVisitingTime(String visitingTime) {
        this.visitingTime = visitingTime;
    }
}
