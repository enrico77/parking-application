package com.parking.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

public class InMemoryTicketServiceImplTest {

	@Test
	public void testGenerateUniqueTicketNumber() {
		// Even in case of multi thread, it works because AtomicLong
		InMemoryTicketServiceImpl service = new InMemoryTicketServiceImpl();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		new Thread(new TestKey(service)).start();
		assertFalse(TestKey.error);
	}
	
	private static class TestKey implements Runnable {
		private static Set<Long> usedKeys = ConcurrentHashMap.newKeySet();
		private static boolean error = false;
		private InMemoryTicketServiceImpl service;
		
		private TestKey(InMemoryTicketServiceImpl service){
			this.service = service;
		}

		@Override
		public void run() {
			for(int i = 0; i < 100000; i++) {
				Long ticket = service.generateUniqueTicketNumber();
				if(!usedKeys.add(ticket)) {
					error = true;
					return;
				};
			}
		}
	}
}
