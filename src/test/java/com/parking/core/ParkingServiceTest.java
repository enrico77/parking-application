package com.parking.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parking.adapter.DefaultTollServiceImpl;
import com.parking.adapter.InMemoryParkingRepositoryImpl;
import com.parking.adapter.InMemoryTicketServiceImpl;
import com.parking.domain.Park;
import com.parking.domain.Parking;
import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.exception.DuplicatePlateException;
import com.parking.exception.MoreTollToPayException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.PlateNotFoundException;
import com.parking.exception.TicketNotFoundException;
import com.parking.exception.TollNotPayedException;

public class ParkingServiceTest {

	// Create the service, manually inject dependencies
	private static ParkingService parkingService = new ParkingServiceImpl(new InMemoryParkingRepositoryImpl(),
			new InMemoryTicketServiceImpl());

	@BeforeAll
	public static void createParking() {
		// Create a new parking
		Parking parking = new Parking("PK001", "City Center Parking", "unknown address");
		parking.generateMultipleSlots((short) 1, (short) 100, (short) 1, ParkingSlotTypeEnum.STANDARD);
		parking.generateMultipleSlots((short) 101, (short) 110, (short) 1, ParkingSlotTypeEnum.ELECTRIC_20KW);
		parking.generateMultipleSlots((short) 111, (short) 115, (short) 1, ParkingSlotTypeEnum.ELECTRIC_50KW);
		// Choose the fee policy
		parking.setTollService(new DefaultTollServiceImpl(BigDecimal.valueOf(0d), BigDecimal.valueOf(1d),
				BigDecimal.valueOf(1.5d), BigDecimal.valueOf(2d)));
		parkingService.createParking(parking);
	}
	
	@Test
	public void testCreateParkingErrors() {
		// Create a new parking
		final Parking parking = new Parking(null, "City Center Parking", "unknown address");
		assertThrows(IllegalArgumentException.class, () -> {
			parkingService.createParking(parking);
		});
		final Parking parking2 = new Parking("XXX", "City Center Parking", "unknown address");
		assertThrows(IllegalArgumentException.class, () -> {
			parkingService.createParking(parking2);
		});
	}

	@Test
	public void test()
			throws ParkingFullException, TollNotPayedException, MoreTollToPayException, TicketNotFoundException, DuplicatePlateException, PlateNotFoundException {

		//
		// Park 100 standard cars, all in the same time
		//
		assertEquals(100L,
				parkingService.countFreeSlots("PK001", ParkingSlotTypeEnum.STANDARD).longValue());
		LocalDateTime now = LocalDateTime.now();
		Set<Park> parks = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			parks.add(parkingService.arrive("PK001", "", ParkingSlotTypeEnum.STANDARD, now));
		}
		assertEquals(0L, parkingService.countFreeSlots("PK001", ParkingSlotTypeEnum.STANDARD).longValue());

		// Park another standard car, but the parking is full
		assertThrows(ParkingFullException.class, () -> {
			parkingService.arrive("PK001", "", ParkingSlotTypeEnum.STANDARD, now);
		});

		//
		// Two hours later
		//
		LocalDateTime twoHoursLater = now.plusHours(2);
		// One car tries to leave the parking
		Park park = parks.iterator().next();
		final Long ticketNumber = park.getTicketNumber();
		assertThrows(TollNotPayedException.class, () -> {
			parkingService.leave(ticketNumber, twoHoursLater);
		});

		//
		// Pay toll
		//

		// Calculate toll; after two hours it should be 2$ (see DefaultTollServiceImpl
		// initialization)
		assertTrue(BigDecimal.valueOf(2)
				.compareTo(parkingService.calculateToll("PK001", ticketNumber, twoHoursLater)) == 0);
		// Confirm to the system that toll has been payed
		parkingService.payToll(ticketNumber, twoHoursLater);
		// Now your are free can leave
		parkingService.leave(ticketNumber, twoHoursLater);
		// ...so a new car can park as 1 parking slot is now free
		parkingService.arrive("PK001", "", ParkingSlotTypeEnum.STANDARD, twoHoursLater);

		//
		// Park an electric car
		//
		park = parkingService.arrive("PK001", "", ParkingSlotTypeEnum.ELECTRIC_20KW, twoHoursLater);
		final Long ticketNumber2 = park.getTicketNumber();
		LocalDateTime fourHoursLater = twoHoursLater.plusHours(2);
		// After 2h, calculate toll; it should be 3$ for this kind of electric car (see
		// DefaultTollServiceImpl initialization)
		assertTrue(BigDecimal.valueOf(3)
				.compareTo(parkingService.calculateToll("PK001", ticketNumber2, fourHoursLater)) == 0);
		// Pay toll
		parkingService.payToll(ticketNumber2, fourHoursLater);
		// However, the driver loose time, instead of exit immediately, he stays in the
		// parking 20 minutes more
		LocalDateTime fourHours20Later = fourHoursLater.plusMinutes(20);
		assertThrows(MoreTollToPayException.class, () -> {
			parkingService.leave(ticketNumber2, fourHours20Later);
		});
		
		// After 20 minutes, extra toll are rounded to one hour, so 1.5$ more (see
		// DefaultTollServiceImpl initialization)
		assertTrue(BigDecimal.valueOf(1.5d)
				.compareTo(parkingService.calculateToll("PK001", ticketNumber2, fourHours20Later)) == 0);
		// Pay extra toll
		parkingService.payToll(ticketNumber2, fourHours20Later);
		// Now you are free to exit
		parkingService.leave(ticketNumber2, fourHours20Later);

		// Double parking toll
		parkingService.udateTollService("PK001", new DefaultTollServiceImpl(BigDecimal.valueOf(0d),
				BigDecimal.valueOf(2d), BigDecimal.valueOf(3d), BigDecimal.valueOf(4d)));
		park = parkingService.arrive("PK001", "", ParkingSlotTypeEnum.ELECTRIC_20KW, twoHoursLater);
		final Long ticketNumber3 = park.getTicketNumber();
		// After 2h, calculate toll; it should be 6$ for this kind of electric car with
		// the new
		// DefaultTollServiceImpl initialization
		assertTrue(BigDecimal.valueOf(6)
				.compareTo(parkingService.calculateToll("PK001", ticketNumber3, fourHoursLater)) == 0);
		
		// Another car arrives
		park = parkingService.arrive("PK001", "AB 001 Z", ParkingSlotTypeEnum.ELECTRIC_50KW, twoHoursLater);
		// Ticket has been lost! Try to retrieve the ticket by plate
		assertEquals(park.getTicketNumber(), parkingService.retrieveTicketNumber("PK001", "AB 001 Z"));
		// Now another car with the same plate is arrived
		parkingService.arrive("PK001", "AB 001 Z", ParkingSlotTypeEnum.ELECTRIC_50KW, twoHoursLater);
		// Retry to retrieve the ticket by plate, but now there are two car with the same plate
		assertThrows(DuplicatePlateException.class, () -> {
			parkingService.retrieveTicketNumber("PK001", "AB 001 Z");
		});
		// Try to retrieve the ticket by unknown plate
		assertThrows(PlateNotFoundException.class, () -> {
			parkingService.retrieveTicketNumber("PK001", "AB 001 J");
		});
		// Delete the parking, closed forever
		Parking parking = parkingService.getParking("PK001");
		parkingService.deleteParking(parking);
		assertThrows(IllegalArgumentException.class, () -> {
			parkingService.getParking("PK001");
		});
	}

}
