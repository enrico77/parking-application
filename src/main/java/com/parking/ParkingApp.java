package com.parking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.parking.adapter.DefaultTollServiceImpl;
import com.parking.adapter.InMemoryParkingRepositoryImpl;
import com.parking.adapter.InMemoryTicketServiceImpl;
import com.parking.core.ParkingService;
import com.parking.core.ParkingServiceImpl;
import com.parking.domain.Park;
import com.parking.domain.Parking;
import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.exception.MoreTollToPayException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.TicketNotFoundException;
import com.parking.exception.TollNotPayedException;

public class ParkingApp {

	public static void main(String[] args) throws ParkingFullException, TicketNotFoundException, TollNotPayedException, MoreTollToPayException {
		// Initialize the application
		ParkingService parkingService = new ParkingServiceImpl(new InMemoryParkingRepositoryImpl(),
				new InMemoryTicketServiceImpl());
		
		// Create a new parking
		Parking parking = new Parking("PK001", "City Center Parking", "unknown address");
		// Create parking slots: 1-100 (level 1) for standard gasoline cars
		parking.generateMultipleSlots((short) 1, (short) 100, (short) 1, ParkingSlotTypeEnum.STANDARD);
		// Create slots: 101-110 (level 1) for electric 20KW cars
		parking.generateMultipleSlots((short) 101, (short) 110, (short) 1, ParkingSlotTypeEnum.ELECTRIC_20KW);
		// Create slots: 111-115 (level 1) for electric 50KW cars
		parking.generateMultipleSlots((short) 111, (short) 115, (short) 1, ParkingSlotTypeEnum.ELECTRIC_50KW);
		// Choose the fee policy: 0$ of fixed amount, 1$/hr for gasoline, 1.5$/hr for 20KW electric and
		// 2$/hr for 50KW 
		parking.setTollService(new DefaultTollServiceImpl(BigDecimal.valueOf(0d), BigDecimal.valueOf(1d),
				BigDecimal.valueOf(1.5d), BigDecimal.valueOf(2d)));
		parkingService.createParking(parking);
		
		System.out.println("Parking initialized");
		System.out.println(String.format("There are %d free parking slots", parkingService.countFreeSlots("PK001", null)));
		//
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime oneHourLater = now.plusHours(1);
		//
		System.out.println("A standard car arrives");
		Park park = parkingService.arrive("PK001", "", ParkingSlotTypeEnum.STANDARD, now);
		System.out.println(String.format("The driver has received ticket n.%d and the assigned slot is %d, at level %d", park.getTicketNumber(), park.getParkingSlotId(), park.getLevel()));
		System.out.println(String.format("There are %d free parking slots", parkingService.countFreeSlots("PK001", null)));
		System.out.println("One hour later, the driver use the ticket to calculate the toll");
		BigDecimal toll = parkingService.calculateToll("PK001", park.getTicketNumber(), oneHourLater);
		System.out.println(String.format("The calculated toll is %s$", toll));
		System.out.println("Pay toll");
		parkingService.payToll(park.getTicketNumber(), oneHourLater);
		System.out.println("Exit from parking");
		parkingService.leave(park.getTicketNumber(), oneHourLater);
		System.out.println(String.format("There are %d free parking slots", parkingService.countFreeSlots("PK001", null)));
	}
}
