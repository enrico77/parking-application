package com.parking.exception;
/**
 * The parking is full
 * 
 * @author enricomolino
 *
 */
public class ParkingFullException extends Exception {

	private static final long serialVersionUID = 8223510322803506279L;
	private static final String ERROR_MSG = "The parking is full";
	
	public ParkingFullException() {
		super(ERROR_MSG);
	}

}
