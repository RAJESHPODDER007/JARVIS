package com.ai.chat.model;

import java.util.List;

public record TravelPlan(int days, String city, List<DayPlan> itinerary) {
}
