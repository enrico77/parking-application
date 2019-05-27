package com.parking.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parking.domain.Parking;
import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.domain.User;
import com.parking.exception.TicketNotFoundException;
import com.parking.port.ParkingRepository;

public class InMemoryParkingRepositoryImplTest {

	private static ParkingRepository parkingRepository = new InMemoryParkingRepositoryImpl();

	@BeforeAll
	public static void createParking() {
		// Create a new parking
		Parking parking = new Parking("PK010", "City Center Parking", "unknown address");
		parking.setTollService(
				new DefaultTollServiceImpl(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
		parking.generateMultipleSlots((short) 1, (short) 100, (short) 1, ParkingSlotTypeEnum.STANDARD);
		parkingRepository.createParking(parking);
	}

	@Test()
	public void testCreateParking() {
		Parking parking = new Parking("PK010", "City Center Parking", "unknown address");
		assertThrows(IllegalArgumentException.class, () -> {
			parkingRepository.createParking(parking);
		});
	}
	
	@Test()
	public void testCreateSlotUser() {
		assertThrows(IllegalArgumentException.class, () -> {
			User user = new User(50L, null, LocalDateTime.now());
			parkingRepository.createSlotUser("PK010", (short) 1, (short) 2, user);
		});
	}

	@Test
	public void testGetParking() {
		assertEquals("PK010", parkingRepository.getParking("PK010").getId());
		assertThrows(IllegalArgumentException.class, () -> {
			parkingRepository.getParking("PK999");
		});
	}

	@Test
	public void testGetParkingByTicket() throws TicketNotFoundException {
		User user = new User(50L, null, LocalDateTime.now());
		parkingRepository.createSlotUser("PK010", (short) 1, (short) 1, user);
		assertEquals("PK010", parkingRepository.getParkingByTicket(50L).getId());
		assertThrows(TicketNotFoundException.class, () -> {
			parkingRepository.getParkingByTicket(999L);
		});
	}

	@Test
	public void testDeleteParking() {
		Parking parking = new Parking("PK020", "City Center Parking 2", "unknown address");
		parking.generateMultipleSlots((short) 1, (short) 100, (short) 1, ParkingSlotTypeEnum.STANDARD);
		parking.setTollService(
				new DefaultTollServiceImpl(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
		assertEquals(parking, parkingRepository.createParking(parking));
		assertEquals(parking, parkingRepository.getParking("PK020"));
		parkingRepository.deleteParking(parking);
		assertThrows(TicketNotFoundException.class, () -> {
			parkingRepository.getParkingByTicket(999L);
		});
	}

	@Test
	public void updateTollService() {
		Parking parking = new Parking("PK030", "City Center Parking 2", "unknown address");
		parking.generateMultipleSlots((short) 1, (short) 100, (short) 1, ParkingSlotTypeEnum.STANDARD);
		parking.setTollService(
				new DefaultTollServiceImpl(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
		parkingRepository.createParking(parking);
		DefaultTollServiceImpl newToll = new DefaultTollServiceImpl(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE,
				BigDecimal.ONE);
		parkingRepository.updateTollService("PK030", newToll);
		assertEquals(newToll, parkingRepository.getParking("PK030").getTollService());
	}

	@Test
	public void testUpdateUserPayment() throws TicketNotFoundException {
		User user = new User(99L, null, LocalDateTime.now());
		parkingRepository.createSlotUser("PK010", (short) 2, (short) 1, user);
		LocalDateTime paymentDateTime = LocalDateTime.now();
		parkingRepository.updateUserPayment(99L, paymentDateTime);
		assertEquals(paymentDateTime, parkingRepository.findSlot(99L).getUser().getPaymentDateTime());
	}

	@Test
	public void testFreeSlot() throws TicketNotFoundException {
		User user = new User(110L, null, LocalDateTime.now());
		parkingRepository.createSlotUser("PK010", (short) 10, (short) 1, user);
		parkingRepository.findSlot(110L);
		parkingRepository.freeSlot(110L, LocalDateTime.now());
		assertThrows(TicketNotFoundException.class, () -> {
			parkingRepository.findSlot(110L);
		});
	}

}
