package com.parking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class ParkingSlotTest {

	@Test
	void testSlotHistory() {
		ParkingSlot parkingSlot = new ParkingSlot((short)1, (short)1, ParkingSlotTypeEnum.STANDARD);
		LocalDateTime localDateTime = LocalDateTime.now();
		for(int i = 1; i<102; i++) {
			User user = new User(Long.valueOf(i), null, null);
			parkingSlot.setUser(user);
			assertEquals(user, parkingSlot.getUser());
			parkingSlot.freeSlot(localDateTime);
			assertNull(parkingSlot.getUser());
			assertEquals(user, parkingSlot.getPreviousUsers().peekLast());
			if(i<101) {
				assertEquals(i, parkingSlot.getPreviousUsers().size());
			} else {
				assertEquals(100, parkingSlot.getPreviousUsers().size());
			}
		}
	}
	
	@Test
	void testSlotEquals() {
		ParkingSlot parkingSlot1 = new ParkingSlot((short)1, (short)1, ParkingSlotTypeEnum.STANDARD);
		ParkingSlot parkingSlot2 = new ParkingSlot((short)1, (short)1, ParkingSlotTypeEnum.STANDARD);
		assertTrue(parkingSlot1.equals(parkingSlot2) && parkingSlot2.equals(parkingSlot1));
	}

}
