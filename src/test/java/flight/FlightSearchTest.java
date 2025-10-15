package flight;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.junit.jupiter.api.*;

/**
 * JUnit 5 test class for FlightSearch::runFlightSearch
 * Tests all 11 conditions plus a comprehensive valid input test.
 * Each test verifies both return value AND attribute initialization.
 */
class FlightSearchTest {

    /* ---------- Date formatter and constants ---------- */
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                                                                  .withResolverStyle(ResolverStyle.STRICT);
    private static final LocalDate TODAY = LocalDate.now();
    private static final String TODAY_STR = TODAY.format(DF);
    private static final String YESTERDAY_STR = TODAY.minusDays(1).format(DF);
    private static final String TOMORROW_STR = TODAY.plusDays(1).format(DF);

    /* ---------- Reusable valid parameters ---------- */
    private static final String VALID_DEP = "syd";
    private static final String VALID_DES = "mel";
    private static final String VALID_DEP_DATE = TOMORROW_STR;
    private static final String VALID_RET_DATE = TODAY.plusDays(7).format(DF);
    private static final String VALID_CLASS = "economy";

    /**
     * CONDITION 1: Total passengers must be at least 1 and cannot exceed 9
     * Test Data 1: 0 adults, 0 children, 0 infants (boundary: total = 0)
     * Test Data 2: 9 adults, 1 child, 0 infants (boundary: total = 10)
     */
    @Test
    @DisplayName("Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10)")
    void totalPassengersBoundary() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Total = 0 (boundary minimum)
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              0, 0, 0);
        assertFalse(result1, "Total passengers = 0 should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized when total = 0");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Total = 10 (boundary maximum exceeded)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              9, 1, 0);
        assertFalse(result2, "Total passengers = 10 should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized when total > 9");
    }

    /**
     * CONDITION 2: Children cannot be in emergency row or first class
     * Test Data 1: 1 adult, 1 child in first class
     * Test Data 2: 1 adult, 1 child in economy with emergency row
     */
    @Test
    @DisplayName("Condition 2: Children in emergency row seating or first class")
    void childSeatRestrictions() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Children in first class
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, "first", 
                                              1, 1, 0);
        assertFalse(result1, "Children in first class should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with children in first class");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Children in emergency row (economy)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 1, 0);
        assertFalse(result2, "Children in emergency row should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized with children in emergency row");
    }

    /**
     * CONDITION 3: Infants cannot be in emergency row or business class
     * Test Data 1: 1 adult, 1 infant in business class
     * Test Data 2: 1 adult, 1 infant in economy with emergency row
     */
    @Test
    @DisplayName("Condition 3: Infants in emergency row seating or business class")
    void infantSeatRestrictions() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Infants in business class
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, "business", 
                                              1, 0, 1);
        assertFalse(result1, "Infants in business class should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with infants in business class");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Infants in emergency row (economy)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 0, 1);
        assertFalse(result2, "Infants in emergency row should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized with infants in emergency row");
    }

    /**
     * CONDITION 4: Up to 2 children per adult (children <= adults * 2)
     * Test Data 1: 1 adult, 3 children (boundary: exceeds ratio)
     * Test Data 2: 2 adults, 4 children (boundary: exactly at limit, valid)
     */
    @Test
    @DisplayName("Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)")
    void childrenAdultRatio() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: 3 children with 1 adult (exceeds boundary)
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 3, 0);
        assertFalse(result1, "3 children with 1 adult should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized when child ratio exceeded");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 4 children with 2 adults (exactly at boundary, valid)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              2, 4, 0);
        assertTrue(result2, "4 children with 2 adults should be valid");
        assertAttributesInitialized(fs2, VALID_DEP_DATE, VALID_DEP, false, VALID_RET_DATE, 
                                    VALID_DES, VALID_CLASS, 2, 4, 0);
    }

    /**
     * CONDITION 5: Exactly 1 infant per adult (infants <= adults)
     * Test Data 1: 1 adult, 2 infants (boundary: exceeds ratio)
     * Test Data 2: 0 adults, 1 infant (no adult for infant)
     */
    @Test
    @DisplayName("Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)")
    void infantAdultRatio() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: 2 infants with 1 adult (exceeds boundary)
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 2);
        assertFalse(result1, "2 infants with 1 adult should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized when infant ratio exceeded");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 1 infant with 0 adults
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              0, 0, 1);
        assertFalse(result2, "1 infant with 0 adults should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized with infant but no adult");
    }

    /**
     * CONDITION 6: Departure date cannot be in the past
     * Test Data 1: Yesterday's date (boundary: 1 day before current)
     * Test Data 2: Tomorrow's date (valid)
     */
    @Test
    @DisplayName("Condition 6: Departure date in past (boundary: day before current)")
    void departureNotPast() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Yesterday (boundary: 1 day in past)
        boolean result1 = fs1.runFlightSearch(YESTERDAY_STR, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result1, "Departure date in past should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with past departure date");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Tomorrow (valid future date)
        boolean result2 = fs2.runFlightSearch(TOMORROW_STR, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertTrue(result2, "Tomorrow's departure date should be valid");
        assertAttributesInitialized(fs2, TOMORROW_STR, VALID_DEP, false, VALID_RET_DATE, 
                                    VALID_DES, VALID_CLASS, 1, 0, 0);
    }

    /**
     * CONDITION 7: Dates must be DD/MM/YYYY format with strict validation
     * Test Data 1: 29/02/2026 (non-leap year, invalid)
     * Test Data 2: 31/04/2025 (April has only 30 days, invalid)
     */
    @Test
    @DisplayName("Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04)")
    void dateValidation() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: 29/02/2026 (2026 is not a leap year)
        boolean result1 = fs1.runFlightSearch("29/02/2026", VALID_DEP, false, 
                                              "07/03/2026", VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result1, "29/02/2026 (non-leap year) should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with invalid date");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 31/04/2025 (April has only 30 days)
        boolean result2 = fs2.runFlightSearch("31/04/2025", VALID_DEP, false, 
                                              "07/05/2025", VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result2, "31/04/2025 (invalid day for April) should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized with invalid date");
    }

    /**
     * CONDITION 8: Return date cannot be before departure date
     * Test Data 1: Return date before departure (invalid)
     * Test Data 2: Return date same as departure (boundary: valid)
     */
    @Test
    @DisplayName("Condition 8: Return date before departure and same-day valid")
    void returnAfterDeparture() {
        FlightSearch fs1 = new FlightSearch();
        String dayBefore = TODAY.plusDays(6).format(DF);
        
        // Test Data 1: Return before departure
        boolean result1 = fs1.runFlightSearch(VALID_RET_DATE, VALID_DEP, false, 
                                              dayBefore, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result1, "Return date before departure should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized when return before departure");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Return same as departure (boundary: valid same-day return)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_DEP_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertTrue(result2, "Return date same as departure should be valid");
        assertAttributesInitialized(fs2, VALID_DEP_DATE, VALID_DEP, false, VALID_DEP_DATE, 
                                    VALID_DES, VALID_CLASS, 1, 0, 0);
    }

    /**
     * CONDITION 9: Seating class must be one of: economy, premium economy, business, first
     * Test Data 1: "luxury" (invalid class)
     * Test Data 2: "premium economy" (valid class)
     */
    @Test
    @DisplayName("Condition 9: Invalid seating class (luxury) and valid (premium economy)")
    void seatingClass() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Invalid class "luxury"
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, "luxury", 
                                              1, 0, 0);
        assertFalse(result1, "Seating class 'luxury' should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with invalid seating class");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Valid class "premium economy"
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, "premium economy", 
                                              1, 0, 0);
        assertTrue(result2, "Seating class 'premium economy' should be valid");
        assertAttributesInitialized(fs2, VALID_DEP_DATE, VALID_DEP, false, VALID_RET_DATE, 
                                    VALID_DES, "premium economy", 1, 0, 0);
    }

    /**
     * CONDITION 10: Only economy class can have emergency row seating
     * Test Data 1: Emergency row with business class (invalid)
     * Test Data 2: Emergency row with economy class (valid)
     */
    @Test
    @DisplayName("Condition 10: Emergency row in non-economy (business) and valid (economy)")
    void emergencyEconomyOnly() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Emergency row with business class
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "business", 
                                              1, 0, 0);
        assertFalse(result1, "Emergency row with business class should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized with emergency row in non-economy");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Emergency row with economy class
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 0, 0);
        assertTrue(result2, "Emergency row with economy class should be valid");
        assertAttributesInitialized(fs2, VALID_DEP_DATE, VALID_DEP, true, VALID_RET_DATE, 
                                    VALID_DES, "economy", 1, 0, 0);
    }

    /**
     * CONDITION 11: Valid airports only, and departure != destination
     * Test Data 1: Same departure and destination (syd to syd)
     * Test Data 2: Invalid airport code (xyz)
     */
    @Test
    @DisplayName("Condition 11: Same departure/destination airport and invalid airport code")
    void airportCodes() {
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: Same departure and destination
        boolean result1 = fs1.runFlightSearch(VALID_DEP_DATE, "syd", false, 
                                              VALID_RET_DATE, "syd", VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result1, "Same departure and destination should be invalid");
        assertAttributesNotInitialized(fs1, "Attributes should not be initialized when departure equals destination");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Invalid airport code
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, "xyz", false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        assertFalse(result2, "Invalid airport code 'xyz' should be invalid");
        assertAttributesNotInitialized(fs2, "Attributes should not be initialized with invalid airport code");
    }

    /**
     * ALL VALID: Test multiple valid combinations
     * Test Data 1: 2 adults, 2 children, 0 infants, economy, no emergency
     * Test Data 2: 1 adult, 0 children, 1 infant, economy, no emergency
     * Test Data 3: 3 adults, 0 children, 0 infants, economy, emergency row
     * Test Data 4: 1 adult, 1 child, 0 infants, premium economy, no emergency
     */
    @Test
    @DisplayName("All inputs valid - Test Data 1-4 (multiple valid combinations)")
    void allValid() {
        String date1 = TOMORROW_STR;
        String ret1 = TODAY.plusDays(14).format(DF);
        FlightSearch fs1 = new FlightSearch();
        
        // Test Data 1: 2 adults, 2 children, economy
        boolean result1 = fs1.runFlightSearch(date1, "syd", false, ret1, "pvg", "economy", 2, 2, 0);
        assertTrue(result1, "All valid inputs (Test Data 1) should be valid");
        assertAttributesInitialized(fs1, date1, "syd", false, ret1, "pvg", "economy", 2, 2, 0);

        String date2 = TOMORROW_STR;
        String ret2 = TODAY.plusDays(21).format(DF);
        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 1 adult, 1 infant, economy
        boolean result2 = fs2.runFlightSearch(date2, "mel", false, ret2, "lax", "economy", 1, 0, 1);
        assertTrue(result2, "All valid inputs (Test Data 2) should be valid");
        assertAttributesInitialized(fs2, date2, "mel", false, ret2, "lax", "economy", 1, 0, 1);

        String date3 = TOMORROW_STR;
        String ret3 = TODAY.plusDays(10).format(DF);
        FlightSearch fs3 = new FlightSearch();
        
        // Test Data 3: 3 adults, emergency row, economy
        boolean result3 = fs3.runFlightSearch(date3, "doh", true, ret3, "cdg", "economy", 3, 0, 0);
        assertTrue(result3, "All valid inputs (Test Data 3) should be valid");
        assertAttributesInitialized(fs3, date3, "doh", true, ret3, "cdg", "economy", 3, 0, 0);

        String date4 = TOMORROW_STR;
        String ret4 = TODAY.plusDays(5).format(DF);
        FlightSearch fs4 = new FlightSearch();
        
        // Test Data 4: 1 adult, 1 child, premium economy
        boolean result4 = fs4.runFlightSearch(date4, "del", false, ret4, "syd", "premium economy", 1, 1, 0);
        assertTrue(result4, "All valid inputs (Test Data 4) should be valid");
        assertAttributesInitialized(fs4, date4, "del", false, ret4, "syd", "premium economy", 1, 1, 0);
    }

    /* ========== HELPER METHODS FOR ATTRIBUTE VERIFICATION ========== */

    /**
     * Verifies that all attributes remain uninitialized (null or default values)
     * This should be the case when validation fails.
     */
    private void assertAttributesNotInitialized(FlightSearch fs, String message) {
        assertNull(fs.getDepartureDate(), message + ": departureDate should be null");
        assertNull(fs.getDepartureAirportCode(), message + ": departureAirportCode should be null");
        assertFalse(fs.isEmergencyRowSeating(), message + ": emergencyRowSeating should be false");
        assertNull(fs.getReturnDate(), message + ": returnDate should be null");
        assertNull(fs.getDestinationAirportCode(), message + ": destinationAirportCode should be null");
        assertNull(fs.getSeatingClass(), message + ": seatingClass should be null");
        assertEquals(0, fs.getAdultPassengerCount(), message + ": adultPassengerCount should be 0");
        assertEquals(0, fs.getChildPassengerCount(), message + ": childPassengerCount should be 0");
        assertEquals(0, fs.getInfantPassengerCount(), message + ": infantPassengerCount should be 0");
    }

    /**
     * Verifies that all attributes are properly initialized with expected values
     * This should be the case when validation succeeds.
     */
    private void assertAttributesInitialized(FlightSearch fs, 
                                            String expectedDepDate, String expectedDepAirport,
                                            boolean expectedEmergency, String expectedRetDate,
                                            String expectedDestAirport, String expectedClass,
                                            int expectedAdults, int expectedChildren, int expectedInfants) {
        assertEquals(expectedDepDate, fs.getDepartureDate(), "departureDate should be initialized");
        assertEquals(expectedDepAirport, fs.getDepartureAirportCode(), "departureAirportCode should be initialized");
        assertEquals(expectedEmergency, fs.isEmergencyRowSeating(), "emergencyRowSeating should be initialized");
        assertEquals(expectedRetDate, fs.getReturnDate(), "returnDate should be initialized");
        assertEquals(expectedDestAirport, fs.getDestinationAirportCode(), "destinationAirportCode should be initialized");
        assertEquals(expectedClass, fs.getSeatingClass(), "seatingClass should be initialized");
        assertEquals(expectedAdults, fs.getAdultPassengerCount(), "adultPassengerCount should be initialized");
        assertEquals(expectedChildren, fs.getChildPassengerCount(), "childPassengerCount should be initialized");
        assertEquals(expectedInfants, fs.getInfantPassengerCount(), "infantPassengerCount should be initialized");
    }
}
