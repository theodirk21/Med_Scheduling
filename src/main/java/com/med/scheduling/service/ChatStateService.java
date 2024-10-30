package com.med.scheduling.service;

import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.UserState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatStateService {

    private final Map<String, UserState> userStates = new HashMap<>();

    public void startInteraction(String chatId, MedicationState initialState) {
        UserState userState = new UserState();
        userState.setChatId(chatId);
        userState.setCurrentStep(initialState);
        userStates.put(chatId, userState);
    }

    public UserState getUserState(String chatId) {
        return userStates.getOrDefault(chatId, null);
    }

    public void updateUserState(String chatId, MedicationState newState) {
        UserState userState = userStates.get(chatId);
        if (userState != null) {
            userState.setCurrentStep(newState);
        }
    }

    public void endInteraction(String chatId) {
        userStates.remove(chatId);
    }
}
