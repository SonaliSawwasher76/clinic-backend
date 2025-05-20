package com.clinic.repository;

import com.clinic.entity.Appointment.DoctorSchedule;
import com.clinic.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    List<DoctorSchedule> findByDoctor_DoctorId(Long doctorId);

    List<DoctorSchedule> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);


    List<DoctorSchedule> findByDoctor(Doctor doctor);
}

