package com.parking.adapter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.parking.domain.Parking;
import com.parking.domain.ParkingSlot;
import com.parking.domain.User;
import com.parking.exception.TicketNotFoundException;
import com.parking.port.ParkingRepository;
import com.parking.port.TollService;

/**
 * This is an example of repository to read/write data about parking and users
 * In this case all data are stored in memory, but it could be easily replaced
 * by a data base persistence
 * 
 * @author enricomolino
 *
 */
public class InMemoryParkingRepositoryImpl implements ParkingRepository {

	private static final Set<Parking> parkings = new HashSet<>();
	private static final String ERR_PARKING_NOT_EXISTS = "Parking %s does not exist";
	private static final String ERR_PARKING_SLOT_NOT_EXISTS = "Parking slot id:%d at level:%d in the parking %s does not exist";

	@Override
	public Parking getParking(@NotNull String id) {
		return getParkingInternal(id).clone();
	}

	@Override
	public Parking getParkingByTicket(@NotNull Long ticketNumber) throws TicketNotFoundException {
		return parkings.stream()
				.filter(parking -> parking.getSlots().stream()
						.filter(slot -> slot.getUser() != null && slot.getUser().getTicketNumber().equals(ticketNumber))
						.findAny().isPresent())
				.findAny().orElseThrow(() -> new TicketNotFoundException(ticketNumber));
	}

	@Override
	public Parking createParking(@NotNull Parking parking) {
		if (!parkings.add(parking.clone())) {
			throw new IllegalArgumentException(String.format("A parking with id:%s already exists", parking.getId()));
		}
		return parking;
	}

	@Override
	public void deleteParking(@NotNull Parking parking) {
		parkings.remove(parking);
	}
	
	@Override
	public void updateTollService(@NotNull String parkingId, @NotNull TollService tollService) {
		getParkingInternal(parkingId).setTollService(tollService);		
	}

	@Override
	public ParkingSlot createSlotUser(@NotNull String parkingId, @NotNull Short slotId, @NotNull Short slotLevel, @NotNull User user) {
		Parking storedParking = getParkingInternal(parkingId);
		ParkingSlot storedParkingSlot = getParkingSlotInternal(storedParking, slotId, slotLevel);
		storedParkingSlot.setUser(user.clone());
		return storedParkingSlot.clone();
	}

	@Override
	public ParkingSlot updateUserPayment(@NotNull Long ticketNumber, @NotNull LocalDateTime paymentDateTime)
			throws TicketNotFoundException {
		ParkingSlot storedParkingSlot = findSlot(ticketNumber);
		User storedUser = storedParkingSlot.getUser();
		storedUser.setPaymentDateTime(paymentDateTime);
		return storedParkingSlot.clone();
	}

	@Override
	public ParkingSlot findSlot(@NotNull Long ticketNumber) throws TicketNotFoundException {
		return parkings.stream().flatMap(parking -> parking.getSlots().stream())
				.filter(parkingSlot -> parkingSlot.getUser() != null
						&& parkingSlot.getUser().getTicketNumber().equals(ticketNumber))
				.findAny().orElseThrow(() -> new TicketNotFoundException(ticketNumber));
	}

	@Override
	public void freeSlot(@NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime) throws TicketNotFoundException {
		ParkingSlot storedParkingSlot = findSlot(ticketNumber);
		storedParkingSlot.freeSlot(leaveDateTime);
	}

	private Parking getParkingInternal(@NotNull String id) {
		return parkings.stream().filter(parking -> parking.getId().equals(id)).findAny()
				.orElseThrow(() -> new IllegalArgumentException(String.format(ERR_PARKING_NOT_EXISTS, id)));
	}

	private ParkingSlot getParkingSlotInternal(@NotNull Parking parking, @NotNull Short slotId, @NotNull Short slotLevel) {
		return parking.getSlots().stream().filter(
				slot -> slot.getId().equals(slotId) && slot.getLevel().equals(slotLevel))
				.findAny().orElseThrow(() -> new IllegalArgumentException(String.format(ERR_PARKING_SLOT_NOT_EXISTS, slotId, slotLevel, parking.getId())));
	}
}
