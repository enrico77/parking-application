package com.parking.exception;
/*
 * Error when after paying the car continue to stay in the parking long time
 * 
 * @author enricomolino
 *
 */
public class MoreTollToPayException extends Exception {

	private static final long serialVersionUID = 6140836237595393926L;
	private static final String ERROR_MSG = "You have exceed the maximum time to stay in the parking after payment. You need to pay extra toll";
	
	public MoreTollToPayException() {
		super(ERROR_MSG);
	}

}
