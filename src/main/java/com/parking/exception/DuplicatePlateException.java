package com.parking.exception;

/**
 * Error when there are more than one car with the same license plate
 * 
 * @author enricomolino
 *
 */
public class DuplicatePlateException extends Exception {

	private static final long serialVersionUID = 1614505718935178732L;
	private static final String ERROR_MSG = "Found more than one car with license plate: %s";
	
	/**
	 * 
	 * @param licensePlate
	 */
	public DuplicatePlateException(String licensePlate){
		super(String.format(ERROR_MSG, licensePlate));
	}

}
