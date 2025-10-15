package flight;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.junit.jupiter.api.*;

/**
 * JUnit 5 test class for FlightSearch::runFlightSearch
 * Tests all 11 conditions plus a comprehensive valid input test.
 * Each test verifies both return value AND attribute initialization status.
 * 
 * IMPORTANT: Per Note 7, tests must verify:
 * - Return value matches expected (true/false)
 * - Attributes ARE initialized when validation succeeds
 * - Attributes ARE NOT initialized when validation fails
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
        
        // Verify return value is false
        assertFalse(result1, "Test Data 1: Total passengers = 0 should return false");
        
        // Verify attributes were NOT initialized (remain null/default)
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");
        assertNull(fs1.getDepartureAirportCode(), "Test Data 1: departureAirportCode should remain null when validation fails");
        assertNull(fs1.getReturnDate(), "Test Data 1: returnDate should remain null when validation fails");
        assertNull(fs1.getDestinationAirportCode(), "Test Data 1: destinationAirportCode should remain null when validation fails");
        assertNull(fs1.getSeatingClass(), "Test Data 1: seatingClass should remain null when validation fails");
        assertEquals(0, fs1.getAdultPassengerCount(), "Test Data 1: adultPassengerCount should remain 0 when validation fails");
        assertEquals(0, fs1.getChildPassengerCount(), "Test Data 1: childPassengerCount should remain 0 when validation fails");
        assertEquals(0, fs1.getInfantPassengerCount(), "Test Data 1: infantPassengerCount should remain 0 when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Total = 10 (boundary maximum exceeded)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              9, 1, 0);
        
        // Verify return value is false
        assertFalse(result2, "Test Data 2: Total passengers = 10 should return false");
        
        // Verify attributes were NOT initialized
        assertNull(fs2.getDepartureDate(), "Test Data 2: departureDate should remain null when validation fails");
        assertNull(fs2.getDepartureAirportCode(), "Test Data 2: departureAirportCode should remain null when validation fails");
        assertNull(fs2.getReturnDate(), "Test Data 2: returnDate should remain null when validation fails");
        assertNull(fs2.getDestinationAirportCode(), "Test Data 2: destinationAirportCode should remain null when validation fails");
        assertNull(fs2.getSeatingClass(), "Test Data 2: seatingClass should remain null when validation fails");
        assertEquals(0, fs2.getAdultPassengerCount(), "Test Data 2: adultPassengerCount should remain 0 when validation fails");
        assertEquals(0, fs2.getChildPassengerCount(), "Test Data 2: childPassengerCount should remain 0 when validation fails");
        assertEquals(0, fs2.getInfantPassengerCount(), "Test Data 2: infantPassengerCount should remain 0 when validation fails");
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
        
        assertFalse(result1, "Test Data 1: Children in first class should return false");
        assertNull(fs1.getSeatingClass(), "Test Data 1: seatingClass should remain null when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Children in emergency row (economy)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 1, 0);
        
        assertFalse(result2, "Test Data 2: Children in emergency row should return false");
        assertFalse(fs2.isEmergencyRowSeating(), "Test Data 2: emergencyRowSeating should remain false when validation fails");
        assertNull(fs2.getDepartureDate(), "Test Data 2: departureDate should remain null when validation fails");
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
        
        assertFalse(result1, "Test Data 1: Infants in business class should return false");
        assertNull(fs1.getSeatingClass(), "Test Data 1: seatingClass should remain null when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Infants in emergency row (economy)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 0, 1);
        
        assertFalse(result2, "Test Data 2: Infants in emergency row should return false");
        assertFalse(fs2.isEmergencyRowSeating(), "Test Data 2: emergencyRowSeating should remain false when validation fails");
        assertNull(fs2.getDepartureDate(), "Test Data 2: departureDate should remain null when validation fails");
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
        
        assertFalse(result1, "Test Data 1: 3 children with 1 adult should return false");
        assertEquals(0, fs1.getChildPassengerCount(), "Test Data 1: childPassengerCount should remain 0 when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 4 children with 2 adults (exactly at boundary, valid)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              2, 4, 0);
        
        assertTrue(result2, "Test Data 2: 4 children with 2 adults should return true");
        assertEquals(2, fs2.getAdultPassengerCount(), "Test Data 2: adultPassengerCount should be initialized to 2");
        assertEquals(4, fs2.getChildPassengerCount(), "Test Data 2: childPassengerCount should be initialized to 4");
        assertEquals(VALID_DEP_DATE, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
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
        
        assertFalse(result1, "Test Data 1: 2 infants with 1 adult should return false");
        assertEquals(0, fs1.getInfantPassengerCount(), "Test Data 1: infantPassengerCount should remain 0 when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 1 infant with 0 adults
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              0, 0, 1);
        
        assertFalse(result2, "Test Data 2: 1 infant with 0 adults should return false");
        assertEquals(0, fs2.getInfantPassengerCount(), "Test Data 2: infantPassengerCount should remain 0 when validation fails");
        assertNull(fs2.getDepartureDate(), "Test Data 2: departureDate should remain null when validation fails");
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
        
        assertFalse(result1, "Test Data 1: Departure date in past should return false");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");
        assertNull(fs1.getReturnDate(), "Test Data 1: returnDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Tomorrow (valid future date)
        boolean result2 = fs2.runFlightSearch(TOMORROW_STR, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        
        assertTrue(result2, "Test Data 2: Tomorrow's departure date should return true");
        assertEquals(TOMORROW_STR, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
        assertEquals(VALID_RET_DATE, fs2.getReturnDate(), "Test Data 2: returnDate should be initialized");
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
        
        assertFalse(result1, "Test Data 1: 29/02/2026 (non-leap year) should return false");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");
        assertNull(fs1.getReturnDate(), "Test Data 1: returnDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 31/04/2025 (April has only 30 days)
        boolean result2 = fs2.runFlightSearch("31/04/2025", VALID_DEP, false, 
                                              "07/05/2025", VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        
        assertFalse(result2, "Test Data 2: 31/04/2025 (invalid day for April) should return false");
        assertNull(fs2.getDepartureDate(), "Test Data 2: departureDate should remain null when validation fails");
        assertNull(fs2.getReturnDate(), "Test Data 2: returnDate should remain null when validation fails");
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
        
        assertFalse(result1, "Test Data 1: Return date before departure should return false");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");
        assertNull(fs1.getReturnDate(), "Test Data 1: returnDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Return same as departure (boundary: valid same-day return)
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_DEP_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        
        assertTrue(result2, "Test Data 2: Return date same as departure should return true");
        assertEquals(VALID_DEP_DATE, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
        assertEquals(VALID_DEP_DATE, fs2.getReturnDate(), "Test Data 2: returnDate should be initialized");
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
        
        assertFalse(result1, "Test Data 1: Seating class 'luxury' should return false");
        assertNull(fs1.getSeatingClass(), "Test Data 1: seatingClass should remain null when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Valid class "premium economy"
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, false, 
                                              VALID_RET_DATE, VALID_DES, "premium economy", 
                                              1, 0, 0);
        
        assertTrue(result2, "Test Data 2: Seating class 'premium economy' should return true");
        assertEquals("premium economy", fs2.getSeatingClass(), "Test Data 2: seatingClass should be initialized");
        assertEquals(VALID_DEP_DATE, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
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
        
        assertFalse(result1, "Test Data 1: Emergency row with business class should return false");
        assertFalse(fs1.isEmergencyRowSeating(), "Test Data 1: emergencyRowSeating should remain false when validation fails");
        assertNull(fs1.getDepartureDate(), "Test Data 1: departureDate should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Emergency row with economy class
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, VALID_DEP, true, 
                                              VALID_RET_DATE, VALID_DES, "economy", 
                                              1, 0, 0);
        
        assertTrue(result2, "Test Data 2: Emergency row with economy class should return true");
        assertTrue(fs2.isEmergencyRowSeating(), "Test Data 2: emergencyRowSeating should be initialized to true");
        assertEquals(VALID_DEP_DATE, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
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
        
        assertFalse(result1, "Test Data 1: Same departure and destination should return false");
        assertNull(fs1.getDepartureAirportCode(), "Test Data 1: departureAirportCode should remain null when validation fails");
        assertNull(fs1.getDestinationAirportCode(), "Test Data 1: destinationAirportCode should remain null when validation fails");

        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: Invalid airport code
        boolean result2 = fs2.runFlightSearch(VALID_DEP_DATE, "xyz", false, 
                                              VALID_RET_DATE, VALID_DES, VALID_CLASS, 
                                              1, 0, 0);
        
        assertFalse(result2, "Test Data 2: Invalid airport code 'xyz' should return false");
        assertNull(fs2.getDepartureAirportCode(), "Test Data 2: departureAirportCode should remain null when validation fails");
        assertNull(fs2.getDestinationAirportCode(), "Test Data 2: destinationAirportCode should remain null when validation fails");
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
        
        assertTrue(result1, "Test Data 1: All valid inputs should return true");
        assertEquals(date1, fs1.getDepartureDate(), "Test Data 1: departureDate should be initialized");
        assertEquals("syd", fs1.getDepartureAirportCode(), "Test Data 1: departureAirportCode should be initialized");
        assertFalse(fs1.isEmergencyRowSeating(), "Test Data 1: emergencyRowSeating should be initialized to false");
        assertEquals(ret1, fs1.getReturnDate(), "Test Data 1: returnDate should be initialized");
        assertEquals("pvg", fs1.getDestinationAirportCode(), "Test Data 1: destinationAirportCode should be initialized");
        assertEquals("economy", fs1.getSeatingClass(), "Test Data 1: seatingClass should be initialized");
        assertEquals(2, fs1.getAdultPassengerCount(), "Test Data 1: adultPassengerCount should be initialized");
        assertEquals(2, fs1.getChildPassengerCount(), "Test Data 1: childPassengerCount should be initialized");
        assertEquals(0, fs1.getInfantPassengerCount(), "Test Data 1: infantPassengerCount should be initialized");

        String date2 = TOMORROW_STR;
        String ret2 = TODAY.plusDays(21).format(DF);
        FlightSearch fs2 = new FlightSearch();
        
        // Test Data 2: 1 adult, 1 infant, economy
        boolean result2 = fs2.runFlightSearch(date2, "mel", false, ret2, "lax", "economy", 1, 0, 1);
        
        assertTrue(result2, "Test Data 2: All valid inputs should return true");
        assertEquals(date2, fs2.getDepartureDate(), "Test Data 2: departureDate should be initialized");
        assertEquals("mel", fs2.getDepartureAirportCode(), "Test Data 2: departureAirportCode should be initialized");
        assertEquals(ret2, fs2.getReturnDate(), "Test Data 2: returnDate should be initialized");
        assertEquals("lax", fs2.getDestinationAirportCode(), "Test Data 2: destinationAirportCode should be initialized");
        assertEquals(1, fs2.getAdultPassengerCount(), "Test Data 2: adultPassengerCount should be initialized");
        assertEquals(1, fs2.getInfantPassengerCount(), "Test Data 2: infantPassengerCount should be initialized");

        String date3 = TOMORROW_STR;
        String ret3 = TODAY.plusDays(10).format(DF);
        FlightSearch fs3 = new FlightSearch();
        
        // Test Data 3: 3 adults, emergency row, economy
        boolean result3 = fs3.runFlightSearch(date3, "doh", true, ret3, "cdg", "economy", 3, 0, 0);
        
        assertTrue(result3, "Test Data 3: All valid inputs should return true");
        assertEquals(date3, fs3.getDepartureDate(), "Test Data 3: departureDate should be initialized");
        assertEquals("doh", fs3.getDepartureAirportCode(), "Test Data 3: departureAirportCode should be initialized");
        assertTrue(fs3.isEmergencyRowSeating(), "Test Data 3: emergencyRowSeating should be initialized to true");
        assertEquals(ret3, fs3.getReturnDate(), "Test Data 3: returnDate should be initialized");
        assertEquals("cdg", fs3.getDestinationAirportCode(), "Test Data 3: destinationAirportCode should be initialized");
        assertEquals(3, fs3.getAdultPassengerCount(), "Test Data 3: adultPassengerCount should be initialized");

        String date4 = TOMORROW_STR;
        String ret4 = TODAY.plusDays(5).format(DF);
        FlightSearch fs4 = new FlightSearch();
        
        // Test Data 4: 1 adult, 1 child, premium economy
        boolean result4 = fs4.runFlightSearch(date4, "del", false, ret4, "syd", "premium economy", 1, 1, 0);
        
        assertTrue(result4, "Test Data 4: All valid inputs should return true");
        assertEquals(date4, fs4.getDepartureDate(), "Test Data 4: departureDate should be initialized");
        assertEquals("del", fs4.getDepartureAirportCode(), "Test Data 4: departureAirportCode should be initialized");
        assertEquals(ret4, fs4.getReturnDate(), "Test Data 4: returnDate should be initialized");
        assertEquals("syd", fs4.getDestinationAirportCode(), "Test Data 4: destinationAirportCode should be initialized");
        assertEquals("premium economy", fs4.getSeatingClass(), "Test Data 4: seatingClass should be initialized");
        assertEquals(1, fs4.getAdultPassengerCount(), "Test Data 4: adultPassengerCount should be initialized");
        assertEquals(1, fs4.getChildPassengerCount(), "Test Data 4: childPassengerCount should be initialized");
    }
}
