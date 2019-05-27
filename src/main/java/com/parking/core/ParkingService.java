package com.parking.core;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.parking.domain.Park;
import com.parking.domain.Parking;
import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.exception.DuplicatePlateException;
import com.parking.exception.MoreTollToPayException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.PlateNotFoundException;
import com.parking.exception.TicketNotFoundException;
import com.parking.exception.TollNotPayedException;
import com.parking.port.TollService;

/**
 * Core parking application entry point
 * 
 * @author enricomolino
 *
 */
public interface ParkingService {

	/**
	 * Get an existing parking by unique id
	 * 
	 * @param id
	 * @return
	 */
	Parking getParking(@NotNull String id);

	/**
	 * Create a new parking
	 * 
	 * @param parking
	 * @return
	 */
	Parking createParking(@NotNull Parking parking);

	/**
	 * Delete an existing parking
	 * 
	 * @param parking
	 */
	void deleteParking(@NotNull Parking parking);
	
	/**
	 * Count the number of free slots
	 * 
	 * @param parkingId
	 * @param type optional, if you don't specify it counts all kind of slot
	 * @return
	 */
	Long countFreeSlots(@NotNull String parkingId, ParkingSlotTypeEnum type);
	
	/**
	 * Call this method when entering in the parking
	 * 
	 * 
	 * @param parkingId
	 * @param licensePlate (optional, it can be null)
	 * @param type
	 *            (standard/electric 20KW/electric 50KW)
	 * @param arrivalDateTime
	 *            (provide the current local date time)
	 * @return
	 * @throws ParkingFullException
	 *             (thrown if the parking is full)
	 */
	Park arrive(@NotNull String parkingId, String licensePlate, @NotNull ParkingSlotTypeEnum type, @NotNull LocalDateTime arrivalDateTime)
			throws ParkingFullException;

	/**
	 * Calculate the parking toll
	 * 
	 * @param parkingId
	 * @param ticketNumber
	 * @param leaveDateTime
	 * @return
	 * @throws TicketNotFoundException
	 */
	BigDecimal calculateToll(@NotNull String parkingId, @NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime)
			throws TicketNotFoundException;

	/**
	 * Inform the system that the toll has been payed
	 * 
	 * @throws TicketNotFoundException
	 */
	void payToll(@NotNull Long ticketNumber, @NotNull LocalDateTime paymentDateTime) throws TicketNotFoundException;

	/**
	 * Call this method before authorize the driver to quit the parking
	 * 
	 * @param ticketNumber
	 * @param leaveDateTime
	 * @throws TicketNotFoundException
	 * @throws TollNotPayedException
	 * @throws MoreTollToPayException
	 */
	void leave(@NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime)
			throws TicketNotFoundException, TollNotPayedException, MoreTollToPayException;
	
	/**
	 * If the driver has lost the ticket, the ticket number can be retrieved using
	 * the license plate. However it is not always possible, and a camera with OCR
	 * is needed in the parking, that is optional
	 * 
	 * @param parkingId
	 * @param licensePlate
	 * @return
	 * @throws DuplicatePlateException
	 * @throws PlateNotFoundException
	 */
	Long retrieveTicketNumber(@NotNull String parkingId, @NotNull String licensePlate)
			throws DuplicatePlateException, PlateNotFoundException;
	
	/**
	 * Update toll service for a specific parking
	 * 
	 * @param parkingId
	 * @param tollService
	 */
	void udateTollService(@NotNull String parkingId, @NotNull TollService tollService);
}
