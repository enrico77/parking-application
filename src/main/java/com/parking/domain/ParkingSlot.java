package com.parking.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Parking's slot
 * 
 * @author enricomolino
 *
 */
public class ParkingSlot implements Serializable, Cloneable {

	private static final long serialVersionUID = -2630736053846511979L;
	private final Short id;
	private final Short level;
	private final ParkingSlotTypeEnum type;
	// current parking user
	private User user;
	// history of previous parking users, limited by MAX_HISTORY
	private final LinkedList<User> previousUsers = new LinkedList<User>();
	private final static int MAX_HISTORY = 100;

	public ParkingSlot(Short id, Short level, ParkingSlotTypeEnum type) {
		super();
		this.id = id;
		this.level = level;
		this.type = type;
	}

	public Short getId() {
		return id;
	}

	public Short getLevel() {
		return level;
	}

	public ParkingSlotTypeEnum getType() {
		return type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LinkedList<User> getPreviousUsers() {
		return new LinkedList<>(previousUsers);
	}

	/**
	 * Free a slot: the car leave the slot, and it is added to the history
	 */
	public void freeSlot(LocalDateTime leaveDateTime) {
		if (user != null) {
			if (previousUsers.size() == MAX_HISTORY) {
				previousUsers.remove();
			}
			user.setLeaveDateTime(leaveDateTime);
			previousUsers.add(user);
			user = null;
		}
	}

	@Override
	public ParkingSlot clone() {
		ParkingSlot parkingSlot = new ParkingSlot(id, level, type);
		parkingSlot.setUser(user != null ? user.clone() : null);
		previousUsers.forEach(user -> parkingSlot.previousUsers.add(user.clone()));
		return parkingSlot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
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
		ParkingSlot other = (ParkingSlot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		return true;
	}
}
