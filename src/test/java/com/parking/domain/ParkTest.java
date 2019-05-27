package com.parking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ParkTest {

	@Test
	void test() {
		LocalDateTime now = LocalDateTime.now();
		Park park = new Park((short)1, (short)2, 999L, "AA 000 BB", now);
		assertEquals((short)1, (short)park.getParkingSlotId());
		assertEquals((short)2, (short)park.getLevel());
		assertEquals(999L, (long)park.getTicketNumber());
		assertEquals("AA 000 BB", park.getLicensePlate());
		assertEquals(now, park.getArrivalDateTime());
	}

}
