package com.poker.poker.services;

import com.poker.poker.config.constants.EmitterConstants;
import com.poker.poker.models.EmitterModel;
import com.poker.poker.models.enums.EmitterType;
import com.poker.poker.validation.exceptions.BadRequestException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SseServiceTests {

  @Spy private EmitterConstants emitterConstants;

  @InjectMocks private SseService sseService;

  @BeforeEach
  private void setup() {
    sseService = new SseService(emitterConstants);
  }

  /**
   * Helper that will create an emitter of each type and then execute a lambda function, with the
   * arguments stored in a list.
   */
  private void withEachEmitterType(final Consumer<Map<EmitterType, UUID>> test) {
    // Setup
    final Map<EmitterType, UUID> emitterTypeToUuidMap = new HashMap<>();

    // Create emitters and store type -> ID mappings.
    for (final EmitterType type : EmitterType.values()) {
      final UUID uuid = UUID.randomUUID();
      emitterTypeToUuidMap.put(type, uuid);
      Assertions.assertDoesNotThrow(() -> sseService.createEmitter(type, uuid));
    }

    // Execute the provided test code.
    test.accept(emitterTypeToUuidMap);
  }

  /** Ensures that each emitter type can be created successfully. */
  @Test
  public void createEmitter_createsEmitters_whenArgumentsAreValid() {
    withEachEmitterType(
        (emitters) -> {
          for (final EmitterType type : emitters.keySet()) {
            final UUID uuid = emitters.get(type);
            final EmitterModel[] model = new EmitterModel[1];
            Assertions.assertDoesNotThrow(() -> model[0] = sseService.getEmitterModel(type, uuid));
            Assertions.assertNotNull(sseService.getEmitter(type, uuid));
            Assertions.assertEquals(model[0].getEmitter(), sseService.getEmitter(type, uuid));
          }
        });
  }

  /** Ensures that only the correct emitters are created (i.e. no extra emitters). */
  @Test
  public void createEmitter_createsProperEmitters_whenArgumentsAreValid() {
    withEachEmitterType(
        (emitters) -> {
          for (final EmitterType type : emitters.keySet()) {
            Assertions.assertDoesNotThrow(
                () -> sseService.getEmitterModel(type, emitters.get(type)));
            Assertions.assertDoesNotThrow(() -> sseService.getEmitter(type, emitters.get(type)));
            // Create a list of UUIDs for the emitters for types that are not 'type'
            final List<UUID> uuids =
                emitters.values().stream()
                    .filter(uuid -> !emitters.get(type).equals(uuid))
                    .collect(Collectors.toList());
            // Ensure that the proper exception is thrown when trying to get these emitters.
            for (final UUID uuid : uuids) {
              Assertions.assertThrows(
                  BadRequestException.class, () -> sseService.getEmitterModel(type, uuid));
              Assertions.assertThrows(
                  BadRequestException.class, () -> sseService.getEmitter(type, uuid));
            }
          }
        });
  }

  /** Ensures that data can be sent and that the last sent data is stored in the emitter model. */
  @Test
  public void sendUpdate_succeeds_whenEmittersExist() {
    withEachEmitterType(
        (emitters) -> {
          for (final EmitterType type : emitters.keySet()) {
            final String data1 = "data1";
            // Ensure data is sent without throwing.
            Assertions.assertDoesNotThrow(
                () -> sseService.sendUpdate(type, emitters.get(type), data1));
            // Ensure that the emitter model recorded the last piece of data that was sent.
            Assertions.assertEquals(
                data1, sseService.getEmitterModel(type, emitters.get(type)).getLastSent());

            final String data2 = "data2";
            // Ensure data is sent without throwing.
            Assertions.assertDoesNotThrow(
                () -> sseService.sendUpdate(type, emitters.get(type), data2));
            // Ensure that the emitter model recorded the last piece of data that was sent.
            Assertions.assertEquals(
                data2, sseService.getEmitterModel(type, emitters.get(type)).getLastSent());
          }
        });
  }

  /**
   * Ensures that destroying an emitter does not throw exceptions (exceptions are actually caught,
   * so this needs to be modified in the future). TODO: Update this test.
   */
  @Test
  public void completeEmitter_destroysEmitters_whenEmittersExist() {
    withEachEmitterType(
        (emitters) -> {
          for (final EmitterType type : emitters.keySet()) {
            Assertions.assertDoesNotThrow(
                () -> sseService.completeEmitter(type, emitters.get(type)));
          }
        });
  }
}
