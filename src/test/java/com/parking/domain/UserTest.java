package com.parking.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void test() {
		LocalDateTime now = LocalDateTime.now();
		User user1 = new User(1L, "", now);
		User user2 = new User(1L, "", now);
		User user3 = new User(2L, "", now);
		assertTrue(user1.equals(user2) && user2.equals(user1));
		assertFalse(user1.equals(user3) && user3.equals(user1));
		assertTrue(user1.hashCode() == user2.hashCode());
		assertFalse(user1.hashCode() == user3.hashCode());
	}

}
