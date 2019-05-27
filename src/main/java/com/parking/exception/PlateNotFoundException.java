package com.parking.exception;
/**
 * The car with specified license plate was not found
 * 
 * @author enricomolino
 *
 */
public class PlateNotFoundException extends Exception {

	private static final long serialVersionUID = 3909612798560291054L;
	private static final String ERROR_MSG = "Car with license plate: %s not found";
	
	/**
	 * 
	 * @param licensePlate
	 */
	public PlateNotFoundException(String licensePlate){
		super(String.format(ERROR_MSG, licensePlate));
	}

}
