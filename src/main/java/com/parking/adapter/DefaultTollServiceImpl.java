package com.parking.adapter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.parking.domain.ParkingSlotTypeEnum;
import com.parking.port.TollService;

/**
 * This is an example of toll service implementation
 * Any kind of implementation could be written and any parking can use a different
 * implementation
 * 
 * @author enricomolino
 *
 */
public class DefaultTollServiceImpl implements TollService {

	private static final long serialVersionUID = 5636268813503766610L;
	private final BigDecimal fixedRate;
	private final BigDecimal standardHRate;
	private final BigDecimal electric20HRate;
	private final BigDecimal electric50HRate;
	private static final long MAX_FREE_DURATION_ALLOWED_MINUTES = 15; 

	/**
	 * 
	 * @param fixedRate (fixed amount to pay, it can be 0 or null)
	 * @param standardHRate (hourly amount to pay for standard cars, it can be 0 or null)
	 * @param electric20hRate (hourly amount to pay for electric 20KW, it can be 0 or null)
	 * @param electric50hRate (hourly amount to pay for electric 50KW, it can be 0 or null)
	 */
	public DefaultTollServiceImpl(BigDecimal fixedRate, BigDecimal standardHRate, BigDecimal electric20hRate,
			BigDecimal electric50hRate) {
		super();
		this.fixedRate = fixedRate != null ? fixedRate : BigDecimal.ZERO;
		this.standardHRate = standardHRate != null ? standardHRate : BigDecimal.ZERO;
		this.electric20HRate = electric20hRate != null ? electric20hRate : BigDecimal.ZERO;
		this.electric50HRate = electric50hRate != null ? electric50hRate : BigDecimal.ZERO;
	}

	@Override
	public BigDecimal parkingToll(@NotNull LocalDateTime arrivalDateTime, @NotNull LocalDateTime leaveDateTime, @NotNull ParkingSlotTypeEnum type) {
		Duration parkingDuration = Duration.between(arrivalDateTime, leaveDateTime);
		// There is nothing to pay if you stay a short time only
		if (maxDurationAllowedBeforeLeave().compareTo(parkingDuration) >= 0) {
			return BigDecimal.ZERO;
		}
		// The number of hours is rounded up
		Double hours = Math.ceil(parkingDuration.getSeconds() / 3600f);
		BigDecimal hRate;
		switch (type) {
		case ELECTRIC_20KW:
			hRate = electric20HRate;
			break;
		case ELECTRIC_50KW:
			hRate = electric50HRate;
			break;
		case STANDARD:
		default:
			hRate = standardHRate;
			break;
		}
		return hRate.multiply(BigDecimal.valueOf(hours)).add(fixedRate);
	}

	@Override
	public Duration maxDurationAllowedBeforeLeave() {
		return Duration.ofMinutes(MAX_FREE_DURATION_ALLOWED_MINUTES);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((electric20HRate == null) ? 0 : electric20HRate.hashCode());
		result = prime * result + ((electric50HRate == null) ? 0 : electric50HRate.hashCode());
		result = prime * result + ((fixedRate == null) ? 0 : fixedRate.hashCode());
		result = prime * result + ((standardHRate == null) ? 0 : standardHRate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultTollServiceImpl other = (DefaultTollServiceImpl) obj;
		if (electric20HRate == null) {
			if (other.electric20HRate != null)
				return false;
		} else if (!electric20HRate.equals(other.electric20HRate))
			return false;
		if (electric50HRate == null) {
			if (other.electric50HRate != null)
				return false;
		} else if (!electric50HRate.equals(other.electric50HRate))
			return false;
		if (fixedRate == null) {
			if (other.fixedRate != null)
				return false;
		} else if (!fixedRate.equals(other.fixedRate))
			return false;
		if (standardHRate == null) {
			if (other.standardHRate != null)
				return false;
		} else if (!standardHRate.equals(other.standardHRate))
			return false;
		return true;
	}
}
