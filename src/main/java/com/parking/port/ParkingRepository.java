package com.parking.port;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.parking.domain.Parking;
import com.parking.domain.ParkingSlot;
import com.parking.domain.User;
import com.parking.exception.TicketNotFoundException;

/**
 * Repository to read/write data about parking
 * 
 * @author enricomolino
 *
 */
public interface ParkingRepository {

	/**
	 * Load an existing parking
	 * 
	 * @param id
	 * @return the corresponding parking
	 *
	 */
	Parking getParking(@NotNull String id);

	/**
	 * Find a parking by ticket number
	 * 
	 * @param ticketNumber
	 * @return
	 * @throws TicketNotFoundException
	 */
	Parking getParkingByTicket(@NotNull Long ticketNumber) throws TicketNotFoundException;

	/**
	 * Create a new parking
	 * 
	 * @param parking
	 * @return
	 */
	Parking createParking(@NotNull Parking parking);
	
	/**
	 * Update toll service
	 * 
	 * @param parkingId
	 * @param tollService
	 */
	void updateTollService(@NotNull String parkingId, @NotNull TollService tollService);

	/**
	 * Delete an existing parking
	 * 
	 * @param parking
	 */
	void deleteParking(@NotNull Parking parking);

	/**
	 * Assign an user to a parking slot (park a car) The user will be created
	 * 
	 * @param parkingId
	 * @param slotId
	 * @param slotLevel
	 * @param user
	 * @return
	 */
	ParkingSlot createSlotUser(@NotNull String parkingId, @NotNull Short slotId, @NotNull Short slotLevel, @NotNull User user);

	/**
	 * Update user payment date time
	 * 
	 * @param ticketNumber
	 * @param paymentDateTime
	 * @return
	 * @throws TicketNotFoundException
	 */
	ParkingSlot updateUserPayment(@NotNull Long ticketNumber, @NotNull LocalDateTime paymentDateTime) throws TicketNotFoundException;

	/**
	 * Find slot by ticket number
	 * 
	 * @param ticketNumber
	 * @return
	 * @throws TicketNotFoundException
	 */
	ParkingSlot findSlot(@NotNull Long ticketNumber) throws TicketNotFoundException;

	/**
	 * Free a slot (car leave the parking)
	 * 
	 * @param ticketNumber
	 * @param leaveDateTime
	 * @throws TicketNotFoundException
	 */
	void freeSlot(@NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime) throws TicketNotFoundException;
}
