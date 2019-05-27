package com.parking.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import com.parking.port.TollService;

/**
 * The parking with all data, toll policy and its parking slots
 * 
 * @author enricomolino
 *
 */
public class Parking implements Serializable, Cloneable {

	private static final long serialVersionUID = -6423042989313317304L;

	private final String id;
	private final String name;
	private final String address;
	private TollService tollService;
	private Set<ParkingSlot> slots = new HashSet<>();
	private final static String ERR_SLOT_ALREADY_EXISTS = "Slot already exists";

	/**
	 * 
	 * @param id parking unique identifier
	 * @param name
	 * @param address
	 */
	public Parking(String id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public Set<ParkingSlot> getSlots() {
		return new HashSet<>(slots);
	}

	public void setSlots(Set<ParkingSlot> slots) {
		if (slots == null) {
			this.slots.clear();
		} else {
			this.slots = new HashSet<>(slots);
		}
	}

	/**
	 * Utility method to be used during parking creation
	 * 
	 * @param slot
	 * @return
	 */
	public Parking addSlot(ParkingSlot slot) {
		if (!slots.add(slot)) {
			throw new IllegalArgumentException(ERR_SLOT_ALREADY_EXISTS);
		}
		;
		return this;
	}

	/**
	 * Utility method to be used during parking creation
	 * 
	 * @param start
	 *            start slot number
	 * @param end
	 *            end slot number
	 * @param level
	 * @param type
	 */
	public void generateMultipleSlots(Short start, Short end, Short level, ParkingSlotTypeEnum type) {
		short current = start;
		while (current <= end) {
			ParkingSlot slot = new ParkingSlot(current, level, type);
			addSlot(slot);
			current++;
		}
	}

	public TollService getTollService() {
		return tollService;
	}

	public void setTollService(TollService tollService) {
		this.tollService = tollService;
	}

	@Override
	public Parking clone() {
		Parking parking = new Parking(id, name, address);
		parking.setTollService(tollService);
		Set<ParkingSlot> clonedSlots = new HashSet<>();
		slots.forEach(slot -> {
			clonedSlots.add(slot.clone());
		});
		parking.setSlots(clonedSlots);
		return parking;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parking other = (Parking) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
