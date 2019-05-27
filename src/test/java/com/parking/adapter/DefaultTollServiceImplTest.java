package com.parking.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.parking.domain.ParkingSlotTypeEnum;




public class DefaultTollServiceImplTest {
	
	@Test
	public void testConstructor() {
		DefaultTollServiceImpl toll1 = new DefaultTollServiceImpl(null, null, null, null);
		DefaultTollServiceImpl toll2 = new DefaultTollServiceImpl(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		assertTrue(toll1.equals(toll2) && toll2.equals(toll1));
	    assertTrue(toll1.hashCode() == toll2.hashCode());
	}

	@Test
	public void testParkingToll() {
		DefaultTollServiceImpl toll = new DefaultTollServiceImpl(BigDecimal.valueOf(15), BigDecimal.valueOf(1), BigDecimal.valueOf(1.5), BigDecimal.valueOf(2));
		LocalDateTime arrival = LocalDateTime.of(2000, 1, 1, 12, 30);
		LocalDateTime departure = LocalDateTime.of(2000, 1, 1, 13, 30);
		assertTrue(BigDecimal.valueOf(16).compareTo(toll.parkingToll(arrival, departure, ParkingSlotTypeEnum.STANDARD)) == 0);
		assertTrue(BigDecimal.valueOf(16.5).compareTo(toll.parkingToll(arrival, departure, ParkingSlotTypeEnum.ELECTRIC_20KW)) == 0);
		assertTrue(BigDecimal.valueOf(17).compareTo(toll.parkingToll(arrival, departure, ParkingSlotTypeEnum.ELECTRIC_50KW)) == 0);
		departure = LocalDateTime.of(2000, 1, 1, 12, 40);
		assertTrue(BigDecimal.ZERO.compareTo(toll.parkingToll(arrival, departure, ParkingSlotTypeEnum.STANDARD)) == 0);
	}

	@Test
	public void testMaxDurationAllowedBeforeLeave() {
		DefaultTollServiceImpl toll = new DefaultTollServiceImpl(BigDecimal.valueOf(15), BigDecimal.valueOf(1), BigDecimal.valueOf(1.5), BigDecimal.valueOf(2));
		assertEquals(Duration.ofMinutes(15), toll.maxDurationAllowedBeforeLeave());
	}

}
