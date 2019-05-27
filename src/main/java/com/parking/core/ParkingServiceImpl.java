package com.parking.core;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.parking.domain.Park;
import com.parking.domain.Parking;
import com.parking.domain.ParkingSlot;
import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.domain.User;
import com.parking.exception.DuplicatePlateException;
import com.parking.exception.MoreTollToPayException;
import com.parking.exception.ParkingFullException;
import com.parking.exception.PlateNotFoundException;
import com.parking.exception.TicketNotFoundException;
import com.parking.exception.TollNotPayedException;
import com.parking.port.ParkingRepository;
import com.parking.port.TicketService;
import com.parking.port.TollService;

/**
 * 
 * @author enricomolino
 *
 */
public class ParkingServiceImpl implements ParkingService {

	private final ParkingRepository parkings;
	private final TicketService tickets;
	private static final String ERR_PARKING_ID_MANDATORY = "Parking id is mandatory";
	private static final String ERR_TOLL_CALCULATION_MANDATORY = "Toll calculation method is mandatory";

	/**
	 * As it is plain Java, use the constructor to simulate dependency injection The
	 * port implementation can be different
	 * 
	 * @param parkings
	 * @param tickets
	 */
	public ParkingServiceImpl(@NotNull ParkingRepository parkings, @NotNull TicketService tickets) {
		super();
		this.parkings = parkings;
		this.tickets = tickets;
	}

	@Override
	public Parking getParking(@NotNull String id) {
		return parkings.getParking(id);
	}

	@Override
	public Parking createParking(@NotNull Parking parking) {
		if(parking.getId() == null || parking.getId().isBlank()) {
			throw new IllegalArgumentException(ERR_PARKING_ID_MANDATORY);
		}
		if (parking.getTollService() == null) {
			throw new IllegalArgumentException(ERR_TOLL_CALCULATION_MANDATORY);
		}
		return parkings.createParking(parking);
	}

	@Override
	public void deleteParking(@NotNull Parking parking) {
		parkings.deleteParking(parking);
	}
	
	@Override
	public Long countFreeSlots(@NotNull String parkingId, ParkingSlotTypeEnum type) {
		Parking parking = parkings.getParking(parkingId);
		return parking.getSlots().stream().filter(slot -> slot.getUser() == null && (type == null || slot.getType().equals(type))).count();
	}

	// it is synchronized just in case two car enter in the parking
	// in the exactly same instant, to avoid to have assigned the same slot
	@Override
	public synchronized Park arrive(@NotNull String parkingId, String licensePlate, @NotNull ParkingSlotTypeEnum type, @NotNull LocalDateTime arrivalDateTime)
			throws ParkingFullException {
		Parking parking = parkings.getParking(parkingId);
		// Find a free slot (no user assigned) of the required type, take the first
		// available
		ParkingSlot freeSlot = parking.getSlots().stream()
				.filter(slot -> slot.getUser() == null && slot.getType().equals(type)).findFirst()
				.orElseThrow(() -> new ParkingFullException());
		// Create a ticket
		Long ticketNumber = tickets.generateUniqueTicketNumber();
		// Save the slot user
		User user = new User(ticketNumber, licensePlate, arrivalDateTime);
		freeSlot.setUser(user);
		parkings.createSlotUser(parking.getId(), freeSlot.getId(), freeSlot.getLevel(), user);
		return new Park(freeSlot.getId(), freeSlot.getLevel(), ticketNumber, licensePlate, arrivalDateTime);
	}

	@Override
	public BigDecimal calculateToll(@NotNull String parkingId, @NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime)
			throws TicketNotFoundException {
		Parking parking = parkings.getParking(parkingId);
		// Find the slot assigned to the ticket
		ParkingSlot usedSlot = parking.getSlots().stream()
				.filter(slot -> slot.getUser() != null && ticketNumber.equals(slot.getUser().getTicketNumber()))
				.findAny().orElseThrow(() -> new TicketNotFoundException(ticketNumber));
		// Calculate fee
		User user = usedSlot.getUser();
		LocalDateTime start;
		if (user.getPaymentDateTime() != null) {
			// Fee has been already payed, but maybe they are more fee to pay
			start = user.getPaymentDateTime();
		} else {
			start = user.getArrivalDateTime();
		}
		return parking.getTollService().parkingToll(start, leaveDateTime, usedSlot.getType());
	}

	@Override
	public void payToll(@NotNull Long ticketNumber, @NotNull LocalDateTime paymentDateTime) throws TicketNotFoundException {
		parkings.updateUserPayment(ticketNumber, paymentDateTime);
	}

	@Override
	public void leave(@NotNull Long ticketNumber, @NotNull LocalDateTime leaveDateTime)
			throws TicketNotFoundException, TollNotPayedException, MoreTollToPayException {
		Parking parking = parkings.getParkingByTicket(ticketNumber);
		// Find the slot assigned to the ticket
		ParkingSlot usedSlot = parking.getSlots().stream()
				.filter(slot -> slot.getUser() != null && ticketNumber.equals(slot.getUser().getTicketNumber()))
				.findAny().orElseThrow(() -> new TicketNotFoundException(ticketNumber));
		User user = usedSlot.getUser();
		LocalDateTime paymentDateTime = user.getPaymentDateTime();
		if (paymentDateTime == null) {
			throw new TollNotPayedException();
		}
		Duration maxDuration = parking.getTollService().maxDurationAllowedBeforeLeave();
		if (Duration.between(paymentDateTime, leaveDateTime).compareTo(maxDuration) > 0) {
			throw new MoreTollToPayException();
		}
		parkings.freeSlot(ticketNumber, leaveDateTime);
	}
	
	@Override
	public Long retrieveTicketNumber(@NotNull String parkingId, @NotNull String licensePlate)
			throws DuplicatePlateException, PlateNotFoundException {
		Parking parking = parkings.getParking(parkingId);
		Set<Long> ticketNumbers = parking.getSlots().stream()
				.filter(slot -> slot.getUser() != null && licensePlate.equals(slot.getUser().getLicensePlate()))
				.map(slot -> slot.getUser().getTicketNumber()).collect(Collectors.toSet());
		if (ticketNumbers.isEmpty()) {
			throw new PlateNotFoundException(licensePlate);
		} else if (ticketNumbers.size() > 1) {
			// It is possible to have multiple car with the same plate when
			// there is the park two car registered in a different country that have the
			// same plate number.
			// It is very rare, but it is possible, specially in Europe, as license plate
			// have the same format and same digit/letters in all countries, but it
			// is not unique across the countries
			throw new DuplicatePlateException(licensePlate);
		} else {
			return ticketNumbers.iterator().next();
		}
	}

	@Override
	public void udateTollService(@NotNull String parkingId, @NotNull TollService tollService) {
		parkings.updateTollService(parkingId, tollService);
	}
}
