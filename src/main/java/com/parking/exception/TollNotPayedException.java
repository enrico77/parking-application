package com.parking.exception;
/**
 * Error when trying to exit without paying toll
 * 
 * @author enricomolino
 *
 */
public class TollNotPayedException extends Exception {

	private static final long serialVersionUID = -4304795010343904823L;
	private static final String ERROR_MSG = "Pay toll before trying to exit";
	
	public TollNotPayedException(){
		super(ERROR_MSG);
	}

}
