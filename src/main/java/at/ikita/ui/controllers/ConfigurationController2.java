package at.ikita.ui.controllers;

import at.ikita.configs.AppConfiguration2;
import at.ikita.model.DayCareCalendar;
import at.ikita.services.DayCareCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;


/**
 * 
 * Controller to get opening and closeing times from day care
 * First looks up the DayCareCalendar, if any entries there
 * takes default times from the config file, if nothing there
 * just returns 07:00 / 19:00
 * 
 * @author lucas.markovic@student.uibk.ac.at
 *
 */
@Component
@Scope("view")
public class ConfigurationController2 implements Serializable {

	@Autowired
	AppConfiguration2 appConfiguration;
	@Autowired
	DayCareCalendarService dayCareCalendarService;


	public int getMinHourDayCare(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		LocalTime open = appConfiguration.getOpeningTime(weekDay);

		DayCareCalendar dc = dayCareCalendarService.loadByDate(date);
		if(dc == null) {
			if (open == null)
				return 7;
			return open.getHour();
		}
		return dc.getOpeningTime().getHours();
	}
	public int getMaxHourDayCare(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		LocalTime close = appConfiguration.getClosingTime(weekDay);

		DayCareCalendar dc = dayCareCalendarService.loadByDate(date);
		if(dc == null) {
			if (close == null)
				return 19;
			return close.getHour();
		}
		return dc.getClosingTime().getHours();
	}
	public int getMinMinuteDayCare(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		LocalTime open = appConfiguration.getOpeningTime(weekDay);

		DayCareCalendar dc = dayCareCalendarService.loadByDate(date);
		if(dc == null) {
			if (open == null)
				return 0;
			return open.getMinute();
		}
		return dc.getOpeningTime().getMinutes();
	}
	public int getMaxMinuteDayCare(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		LocalTime close = appConfiguration.getClosingTime(weekDay);

		DayCareCalendar dc = dayCareCalendarService.loadByDate(date);
		if(dc == null) {
			if (close == null)
				return 0;
			return close.getMinute();
		}
		return dc.getClosingTime().getMinutes();
	}
	
}
