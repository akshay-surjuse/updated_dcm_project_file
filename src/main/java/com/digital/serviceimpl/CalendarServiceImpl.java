package com.digital.serviceimpl;

import com.digital.dto.*;
import com.digital.entity.*;
import com.digital.exception.InvalidDateException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.*;
import com.digital.servicei.CalendarService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;

    private final AdminRepository adminRepository;

    private final HolidayRepository holidayRepository;

    private final EventRepository eventRepository;

    private final ClassRepository classRepository;

    private final SectionRepository sectionRepository;

    @Override
    public CalendarDto createAcademicCalender(CreateAcademicCalenderRequest request, String username) {

        Admin admin = adminRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("Admin record not found in database"));

        AcademicCalendar academicCalendar = AcademicCalendar.builder()
                .academicYear(request.getAcademicYear())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(admin)
                .build();

        AcademicCalendar savedAcademicCalender = academicCalendarRepository.save(academicCalendar);

        return mapToCalenderDto(savedAcademicCalender);
    }

    @Override
    public HolidayDto addHoliday(Long calendarId, Holiday holiday) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(holiday.getHolidayDate().isAfter(academicCalendar.getEndDate()) |
                holiday.getHolidayDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Holiday date must be within academic calender year");
        }
        holiday.setCalendar(academicCalendar);

        Holiday savedHoliday = holidayRepository.save(holiday);

        return mapToHolidayDto(savedHoliday);
    }

    @Override
    public EventDto addEvent(Long calendarId, Event event) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(event.getEventDate().isAfter(academicCalendar.getEndDate()) |
                event.getEventDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Event date must be within academic year.");
        }

        List<Long> classIds = event.getSchoolClasses().stream().map(SchoolClass::getClassId).toList();
        List<SchoolClass> schoolClasses = classRepository.findAllById(classIds);

        List<Long> sectionIds = event.getSections().stream().map(Section::getSectionId).toList();
        List<Section> sections = sectionRepository.findAllById(sectionIds);

        event.setCalendar(academicCalendar);
        event.setSchoolClasses(schoolClasses);
        event.setSections(sections);

        Event savedEvent = eventRepository.save(event);

        return mapToEventDto(savedEvent);
    }

    @Override
    public List<EventDto> viewCalenderEvents() {

        LocalDate currentDate = LocalDate.now();

        List<Event> events = eventRepository.findAll()
                                .stream()
                                .filter(e -> currentDate.isAfter(e.getCalendar().getStartDate()) &
                                 currentDate.isBefore(e.getCalendar().getEndDate())).toList();

        return events.stream().map(this::mapToEventDto).toList();
    }

    @Override
    public CalendarDto updateAcademicCalender(Long calendarId, UpdateAcademicCalenderRequest request) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Academic calendar record not found in database."));

        academicCalendar.setAcademicYear(request.getAcademicYear());
        academicCalendar.setStartDate(request.getStartDate());
        academicCalendar.setEndDate(request.getEndDate());

        AcademicCalendar updatedAcademicCalendar = academicCalendarRepository.save(academicCalendar);

        return mapToCalenderDto(updatedAcademicCalendar);
    }

    @Override
    public List<CalendarDto> getAcademicCalenders() {

        return academicCalendarRepository.findAll().stream()
                                                   .map(this::mapToCalenderDto)
                                                   .toList();
    }

    @Override
    public CalendarDto getAcademicCalender(Long calendarId) {

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(calendarId).orElseThrow(
                () -> new ResourceNotFoundException("Academic calendar record not found in database."));

        return mapToCalenderDto(academicCalendar);
    }

    @Override
    public HolidayDto updateHoliday(Long holidayId, Holiday holiday) {

        Holiday existingHoliday = holidayRepository.findById(holidayId).orElseThrow(
                () -> new ResourceNotFoundException("Holiday record not found in database."));

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(holiday.getCalendar().getCalendarId())
                .orElseThrow(() -> new ResourceNotFoundException("Academic calendar record not found in database."));

        existingHoliday.setCalendar(academicCalendar);
        existingHoliday.setHolidayName(holiday.getHolidayName());
        existingHoliday.setHolidayDate(holiday.getHolidayDate());
        existingHoliday.setIsEmergency(holiday.getIsEmergency());
        existingHoliday.setRescheduleRequired(holiday.getRescheduleRequired());
        existingHoliday.setRescheduledDate(holiday.getRescheduledDate());

        return mapToHolidayDto(holidayRepository.save(existingHoliday));
    }

    @Override
    public String removeHoliday(Long holidayId) {

        Holiday holiday = holidayRepository.findById(holidayId).orElseThrow(
                () -> new ResourceNotFoundException("Holiday record not found in database."));

        holidayRepository.delete(holiday);

        return "Holiday record deleted successfully.";
    }

    @Override
    public String removeEvent(Long eventId) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event record not found in database."));

        eventRepository.delete(event);

        return "Event record deleted successfully.";
    }

    @Override
    public EventDto updateEvent(Long eventId, Event event) {

        Event existingEvent = eventRepository.findById(eventId).orElseThrow(
                () -> new ResourceNotFoundException("Event record not found in database."));

        AcademicCalendar academicCalendar = academicCalendarRepository.findById(event.getCalendar()
                .getCalendarId()).orElseThrow(() ->
                new ResourceNotFoundException("Academic calendar record not found in database"));

        if(event.getEventDate().isAfter(academicCalendar.getEndDate()) |
                event.getEventDate().isBefore(academicCalendar.getStartDate())){

            throw new InvalidDateException("Event date must be within academic year.");
        }

        List<Long> classIds = event.getSchoolClasses().stream().map(SchoolClass::getClassId).toList();
        List<SchoolClass> schoolClasses = classRepository.findAllById(classIds);

        List<Long> sectionIds = event.getSections().stream().map(Section::getSectionId).toList();
        List<Section> sections = sectionRepository.findAllById(sectionIds);

        existingEvent.setEventType(event.getEventType());
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setCalendar(academicCalendar);
        existingEvent.setSchoolClasses(schoolClasses);
        existingEvent.setSections(sections);

        return mapToEventDto(eventRepository.save(existingEvent));
    }

    public EventDto mapToEventDto(Event event){

        return EventDto.builder()
                .calenderId(event.getCalendar().getCalendarId())
                .academicYear(event.getCalendar().getAcademicYear())
                .eventType(event.getEventType())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .schoolClasses(event.getSchoolClasses().stream().map(SchoolClass::getClassName).toList())
                .sections(event.getSections().stream().map(Section::getSectionName).toList())
                .description(event.getDescription())
                .build();
    }

    public HolidayDto mapToHolidayDto(Holiday holiday){

        return HolidayDto.builder()
                .calendarId(holiday.getCalendar().getCalendarId())
                .academicYear(holiday.getCalendar().getAcademicYear())
                .holidayDate(holiday.getHolidayDate())
                .holidayName(holiday.getHolidayName())
                .isEmergency(holiday.getIsEmergency())
                .rescheduleRequired(holiday.getRescheduleRequired())
                .rescheduledDate(holiday.getRescheduledDate())
                .build();
    }

    public CalendarDto mapToCalenderDto(AcademicCalendar academicCalendar) {

        return CalendarDto.builder()
                .academicYear(academicCalendar.getAcademicYear())
                .startDate(academicCalendar.getStartDate())
                .endDate(academicCalendar.getEndDate())
                .createdBy(academicCalendar.getCreatedBy().getRole())
                .createdAt(academicCalendar.getCreatedAt())
                .updatedAt(academicCalendar.getUpdatedAt())
                .build();
    }
}
