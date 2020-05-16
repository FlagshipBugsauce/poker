package com.poker.poker.config.constants;

import com.poker.poker.validation.exceptions.BadRequestException;
import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Component
@Primary
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HandConstants extends GameConstants {

  private final BadRequestException handNotFoundException =
      new BadRequestException("Hand Not Found", "There is no hand associated with the ID provided");

  private final BadRequestException noGameToHandMappingException =
      new BadRequestException(
          "Hand Not Found",
          "There is no mapping from the game provided to a hand that is currently active.");
}
