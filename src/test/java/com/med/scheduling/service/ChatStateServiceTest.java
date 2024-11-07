package com.med.scheduling.service;

import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import resources.TestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatStateServiceTest {

    @InjectMocks
    private ChatStateService service;

    private String chatId;

    @BeforeEach
    void setUp() {
        chatId = "123";

    }


    @Test
    void startInteractionWaitingDays() {
        var medState = MedicationState.AWAITING_DAYS;

        service.startInteraction(chatId, medState);
        var userState = service.getUserState(chatId);
        assertNotNull(userState);
        assertEquals(chatId, userState.getChatId());
        assertEquals(medState, userState.getCurrentStep());
    }

    @Test
    void getUserStateNull() {

        var userState = service.getUserState(chatId);
        assertNull(userState);
    }

    @Test
    void updateUserStateAWAITING_TIME() {

        var awaitingDays = MedicationState.AWAITING_DAYS;
        var awaitingTime = MedicationState.AWAITING_TIME;

        service.startInteraction(chatId, awaitingDays);
        service.updateUserState(chatId, awaitingTime);
        var userState = service.getUserState(chatId);

        assertNotNull(userState);
        assertEquals(chatId, userState.getChatId());
        assertEquals(awaitingTime, userState.getCurrentStep());
    }

    @Test
    void updateUserStateAWAITING_NAME() {
        var awaitingDays = MedicationState.AWAITING_DAYS;
        var awaitingName = MedicationState.AWAITING_NAME;

        service.startInteraction(chatId, awaitingDays);
        service.updateUserState(chatId, awaitingName);
        var userState = service.getUserState(chatId);

        assertNotNull(userState);
        assertEquals(chatId, userState.getChatId());
        assertEquals(awaitingName, userState.getCurrentStep());

    }

    @Test
    void updateUserStateAWAITING_DELETE() {
        var awaitingDays = MedicationState.AWAITING_DAYS;
        var awaitingDelete = MedicationState.AWAITING_DELETE;

        service.startInteraction(chatId, awaitingDays);
        service.updateUserState(chatId, awaitingDelete);
        var userState = service.getUserState(chatId);

        assertNotNull(userState);
        assertEquals(chatId, userState.getChatId());
        assertEquals(awaitingDelete, userState.getCurrentStep());
    }

    @Test
    void endInteraction() {
        var awaitingDays = MedicationState.AWAITING_DAYS;

        service.startInteraction(chatId, awaitingDays);
        service.endInteraction(chatId);
        var userState = service.getUserState(chatId);

        assertNull(userState);
    }
}