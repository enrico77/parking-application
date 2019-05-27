package com.parking.exception;
/**
 * Ticket not found
 * 
 * @author enricomolino
 *
 */
public class TicketNotFoundException extends Exception {

	private static final long serialVersionUID = 6614116263331190124L;
	private static final String ERROR_MSG = "Ticket: %d not found";
	
	public TicketNotFoundException(Long ticketNumber){
		super(String.format(ERROR_MSG, ticketNumber));
	}

}
