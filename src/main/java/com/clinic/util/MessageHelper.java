package com.clinic.util;

import com.clinic.entity.Appointment.Appointment;
import java.time.format.DateTimeFormatter;

public class MessageHelper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public String adminBookingMessage(Appointment appointment) {
        return String.format(
                "New Appointment Booked:\nDoctor: %s\nPatient: %s\nDate: %s\nTime: %s - %s\nStatus: %s",
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getPatient().getFirstname(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER),
                appointment.getStatus()
        );
    }

    public String doctorBookingMessage(Appointment appointment) {
        return String.format(
                "You have a new appointment:\nPatient: %s\nDate: %s\nTime: %s - %s",
                appointment.getPatient().getFirstname(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER)
        );
    }

    public String patientBookingMessage(Appointment appointment) {
        return String.format(
                "Your appointment is confirmed:\nDoctor: %s\nDate: %s\nTime: %s - %s",
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER)
        );
    }

    public String adminCancelMessage(Appointment appointment) {
        return String.format(
                "Appointment Cancelled:\nDoctor: %s\nPatient: %s\nDate: %s\nTime: %s - %s\nCancelled By: %s\nReason: %s",
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getPatient().getFirstname(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER),
                appointment.getCancelledBy(),
                appointment.getCancellationReason()
        );
    }

    public String doctorCancelMessage(Appointment appointment) {
        return String.format(
                "Appointment Cancelled by %s:\nPatient: %s\nDate: %s\nTime: %s - %s\nReason: %s",
                appointment.getCancelledBy(),
                appointment.getPatient().getFirstname(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER),
                appointment.getCancellationReason()
        );
    }

    public String patientCancelMessage(Appointment appointment) {
        return String.format(
                "Your appointment has been cancelled by %s:\nDoctor: %s\nDate: %s\nTime: %s - %s\nReason: %s",
                appointment.getCancelledBy(),
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getAppointmentDate().format(DATE_FORMATTER),
                appointment.getStartTime().format(TIME_FORMATTER),
                appointment.getEndTime().format(TIME_FORMATTER),
                appointment.getCancellationReason()
        );
    }

    public String adminRescheduleMessage(Appointment appointment) {
        return String.format(
                "Appointment Rescheduled:\nDoctor: %s\nPatient: %s\nOld Date & Time: %s %s\nNew Date & Time: %s %s\nRescheduled By: %s\nReason: %s",
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getPatient().getFirstname(),
                appointment.getRescheduledFrom().toLocalDate().format(DATE_FORMATTER),
                appointment.getRescheduledFrom().toLocalTime().format(TIME_FORMATTER),
                appointment.getRescheduledTo().toLocalDate().format(DATE_FORMATTER),
                appointment.getRescheduledTo().toLocalTime().format(TIME_FORMATTER),
                appointment.getRescheduledBy(),
                appointment.getRescheduleReason()
        );
    }

    public String doctorRescheduleMessage(Appointment appointment) {
        return String.format(
                "Appointment Rescheduled by %s:\nPatient: %s\nNew Date & Time: %s %s\nReason: %s",
                appointment.getRescheduledBy(),
                appointment.getPatient().getFirstname(),
                appointment.getRescheduledTo().toLocalDate().format(DATE_FORMATTER),
                appointment.getRescheduledTo().toLocalTime().format(TIME_FORMATTER),
                appointment.getRescheduleReason()
        );
    }

    public String patientRescheduleMessage(Appointment appointment) {
        return String.format(
                "Your appointment has been rescheduled by %s:\nDoctor: %s\nNew Date & Time: %s %s\nReason: %s",
                appointment.getRescheduledBy(),
                appointment.getDoctor().getUser().getUserProfile().getFirstName(),
                appointment.getRescheduledTo().toLocalDate().format(DATE_FORMATTER),
                appointment.getRescheduledTo().toLocalTime().format(TIME_FORMATTER),
                appointment.getRescheduleReason()
        );
    }
}
