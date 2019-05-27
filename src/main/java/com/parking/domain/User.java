package com.parking.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A parking slot user
 * 
 * @author enricomolino
 *
 */
public class User implements Serializable, Cloneable {

	private static final long serialVersionUID = -1128347132960328926L;

	private final Long ticketNumber;
	private final String licensePlate;
	private final LocalDateTime arrivalDateTime;
	private LocalDateTime paymentDateTime;
	private LocalDateTime leaveDateTime;
	
	public User(Long ticketNumber, String licensePlate, LocalDateTime arrivalDateTime) {
		super();
		this.ticketNumber = ticketNumber;
		this.licensePlate = licensePlate;
		this.arrivalDateTime = arrivalDateTime;
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

	public LocalDateTime getPaymentDateTime() {
		return paymentDateTime;
	}

	public void setPaymentDateTime(LocalDateTime paymentDateTime) {
		this.paymentDateTime = paymentDateTime;
	}

	public LocalDateTime getLeaveDateTime() {
		return leaveDateTime;
	}

	public void setLeaveDateTime(LocalDateTime leaveDateTime) {
		this.leaveDateTime = leaveDateTime;
	}
	
	@Override
	public User clone() {
		User user = new User(ticketNumber, licensePlate, arrivalDateTime);
		user.setLeaveDateTime(leaveDateTime);
		user.setPaymentDateTime(paymentDateTime);
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ticketNumber == null) ? 0 : ticketNumber.hashCode());
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
		User other = (User) obj;
		if (ticketNumber == null) {
			if (other.ticketNumber != null)
				return false;
		} else if (!ticketNumber.equals(other.ticketNumber))
			return false;
		return true;
	}
}
