package com.parking.adapter;

import java.util.concurrent.atomic.AtomicLong;

import com.parking.port.TicketService;

/**
 * This is an example of basic implementation of a ticket number generator
 * 
 * @author enricomolino
 *
 */
public class InMemoryTicketServiceImpl implements TicketService {
	
	private static final AtomicLong lastGenerated = new AtomicLong(0);
	
	@Override
	public Long generateUniqueTicketNumber() {
		return lastGenerated.addAndGet(1);
	}
}
