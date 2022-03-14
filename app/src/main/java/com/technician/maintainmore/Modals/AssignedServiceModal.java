package com.technician.maintainmore.Modals;

public class AssignedServiceModal {

    String bookingID, whoBookedService,
            serviceIcon, serviceName, serviceDescription,
            totalServices, pricePerService, totalServicesPrice,
            bookingDate, bookingTime, visitingDate, visitingTime;

    public AssignedServiceModal(String bookingID, String whoBookedService,
                                String serviceIcon, String serviceName, String serviceDescription,
                                String totalServices, String pricePerService, String totalServicesPrice,
                                String bookingDate, String bookingTime, String visitingDate, String visitingTime) {
        this.bookingID = bookingID;
        this.whoBookedService = whoBookedService;
        this.serviceIcon = serviceIcon;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.totalServices = totalServices;
        this.pricePerService = pricePerService;
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

    public String getPricePerService() {
        return pricePerService;
    }

    public void setPricePerService(String pricePerService) {
        this.pricePerService = pricePerService;
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
