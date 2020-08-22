package com.poker.poker.utilities;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

public final class BigDecimalUtilities {

  private BigDecimalUtilities() {}

  /**
   * Calculates the sum of the numbers provided.
   *
   * @param nums Numbers to be summed.
   * @return The sum of all numbers provided.
   */
  public static BigDecimal sum(final BigDecimal... nums) {
    BigDecimal result = ZERO;
    for (final BigDecimal num : nums) {
      result = result.add(num);
    }
    return result;
  }

  /**
   * Calculates the sum of the numbers provided.
   *
   * @param nums Numbers to be summed.
   * @return The sum of all numbers provided.
   */
  public static BigDecimal sum(final Iterable<BigDecimal> nums) {
    BigDecimal result = ZERO;
    for (final BigDecimal num : nums) {
      result = result.add(num);
    }
    return result;
  }

  /**
   * Calculates the max of the numbers provided.
   *
   * @param nums Numbers to be summed.
   * @return The max of all numbers provided.
   */
  public static BigDecimal max(final BigDecimal... nums) {
    BigDecimal result = ZERO;
    for (BigDecimal num : nums) {
      result = num.compareTo(result) > 0 ? num : result;
    }
    return result;
  }
}
