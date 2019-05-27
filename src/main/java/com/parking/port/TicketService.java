package com.parking.port;

/**
 * Generate an unique id ticket
 * (it must be unique across all the parkings)
 * 
 * @author enricomolino
 *
 */
public interface TicketService {
	
	/**
	 * 
	 * @return
	 */
	public Long generateUniqueTicketNumber();

}
