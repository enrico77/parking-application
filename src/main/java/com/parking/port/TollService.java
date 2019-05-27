package com.parking.port;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.parking.domain.ParkingSlotTypeEnum;

/**
 * Services to calculate toll
 * 
 * @author enricomolino
 *
 */
public interface TollService extends Serializable {
	
	/**
	 * 
	 * @param arrivalDateTime
	 * @param leaveDateTime
	 * @param type of parking
	 * @return the toll amount
	 */
	BigDecimal parkingToll(@NotNull LocalDateTime arrivalDateTime, @NotNull LocalDateTime leaveDateTime, @NotNull ParkingSlotTypeEnum type);
	
	/**
	 * 
	 * @return the maximum time that you can stay in the parking without paying. It is useful to have some time to exit after paying,
	 * or also to exit from the parking if you are entered in the parking by mistake.
	 */
	Duration maxDurationAllowedBeforeLeave();

}
