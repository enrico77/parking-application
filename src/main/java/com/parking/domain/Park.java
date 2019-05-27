package com.parking.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Informations that a driver receive when entering in the parking:
 * 
 * -The level and parking slot to go
 * -The ticket number (used later to pay the toll)
 * -The license plate (optional, if read from the camera when entering. Not used yet, but it could be used in the future e.g. to pay if the driver has lost the ticket)
 * -The arrival date and time (local time of parking)
 * 
 * @author enricomolino
 *
 */
public class Park implements Serializable {

	private static final long serialVersionUID = -3254863662699989146L;
	private final Short parkingSlotId;
	private final Short level;
	private final Long ticketNumber;
	private final String licensePlate;
	private final LocalDateTime arrivalDateTime;

	/**
	 * 
	 * @param parkingSlotId
	 * @param level
	 * @param ticketNumber
	 * @param licensePlate
	 * @param arrivalDateTime
	 */
	public Park(Short parkingSlotId, Short level, Long ticketNumber, String licensePlate, LocalDateTime arrivalDateTime) {
		super();
		this.parkingSlotId = parkingSlotId;
		this.level = level;
		this.ticketNumber = ticketNumber;
		this.licensePlate = licensePlate;
		this.arrivalDateTime = arrivalDateTime;
	}

	public Short getParkingSlotId() {
		return parkingSlotId;
	}

	public Short getLevel() {
		return level;
	}

	public Long getTicketNumber() {
		return ticketNumber;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public LocalDateTime getArrivalDateTime() {
		return arrivalDateTime;
	}

}
