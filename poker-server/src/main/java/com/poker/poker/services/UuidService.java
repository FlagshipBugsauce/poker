package com.poker.poker.services;

import com.poker.poker.config.constants.AppConstants;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@AllArgsConstructor
public class UuidService {
  private final AppConstants appConstants;

  /**
   * Determines whether a string is a valid representation of a UUID.
   *
   * @param uuid String that may or may not be a UUID.
   * @return True if the string is a valid UUID, false otherwise.
   */
  public boolean isValidUuidString(String uuid) {
    if (uuid.length() != 36) return false;
    try {
      UUID.fromString(uuid);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public void checkIfValidAndThrowBadRequest(String uuid) {
    if (!isValidUuidString(uuid)) {
      throw appConstants.getInvalidUuidException();
    }
  }
}
