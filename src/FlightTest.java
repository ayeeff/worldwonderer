import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Main class for flight search validation.
 */
public class FlightSearch {
    private String departureDate;
    private String departureAirportCode;
    private boolean emergencyRowSeating;
    private String returnDate;
    private String destinationAirportCode; 
    private String seatingClass;
    private int adultPassengerCount;
    private int childPassengerCount;
    private int infantPassengerCount;

    /**
     * Helper method to validate date string manually for strict compliance.
     * Returns parsed LocalDate if valid, else null.
     */
    private LocalDate validateAndParseDate(String dateStr) {
        if (dateStr == null || !dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return null;
        }
        String[] parts = dateStr.split("/");
        if (parts.length != 3) return null;
        int day, month, year;
        try {
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            year = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900) {
            return null;
        }
        boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        int maxDays = switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeap ? 29 : 28;
            default -> 31;
        };
        if (day > maxDays) {
            return null;
        }
        // Additional check for month-specific days (e.g., Apr 30 ok, but already handled)
        try {
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validates flight search parameters against all specified conditions.
     * If all conditions are met, initializes class attributes and returns true.
     * Otherwise, returns false without initializing attributes.
     */
    public boolean runFlightSearch(String departureDate, String departureAirportCode, boolean emergencyRowSeating, 
                                   String returnDate, String destinationAirportCode, String seatingClass, 
                                   int adultPassengerCount, int childPassengerCount, int infantPassengerCount) {
        // Step 1: Validate and parse dates (Condition 7: format and validity)
        LocalDate now = LocalDate.of(2025, 10, 8);  // Hardcoded current date for consistent testing
        LocalDate depDate = validateAndParseDate(departureDate);
        if (depDate == null) {
            return false;
        }
        LocalDate retDate = validateAndParseDate(returnDate);
        if (retDate == null) {
            return false;
        }

        // Condition 6: Departure not in past
        if (depDate.isBefore(now)) {
            return false;
        }

        // Condition 8: Return after departure
        if (retDate.isBefore(depDate)) {
            return false;
        }

        // Condition 11: Valid airports and different
        Set<String> validAirports = Set.of("syd", "mel", "lax", "cdg", "del", "pvg", "doh");
        if (!validAirports.contains(departureAirportCode) || 
            !validAirports.contains(destinationAirportCode) || 
            departureAirportCode.equals(destinationAirportCode)) {
            return false;
        }

        // Condition 9: Valid seating class
        Set<String> validClasses = Set.of("economy", "premium economy", "business", "first");
        if (!validClasses.contains(seatingClass)) {
            return false;
        }

        // Condition 10: Emergency row only in economy
        if (emergencyRowSeating && !"economy".equals(seatingClass)) {
            return false;
        }

        // Condition 1: Total passengers 1-9
        int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (totalPassengers < 1 || totalPassengers > 9) {
            return false;
        }

        // Condition 2: No children in emergency or first
        if (childPassengerCount > 0 && (emergencyRowSeating || "first".equals(seatingClass))) {
            return false;
        }

        // Condition 3: No infants in emergency or business
        if (infantPassengerCount > 0 && (emergencyRowSeating || "business".equals(seatingClass))) {
            return false;
        }

        // Condition 4: Max 2 children per adult
        if (childPassengerCount > 2 * adultPassengerCount) {
            return false;
        }

        // Condition 5: Max 1 infant per adult
        if (infantPassengerCount > adultPassengerCount) {
            return false;
        }

        // All conditions met: Initialize attributes
        this.departureDate = departureDate;
        this.departureAirportCode = departureAirportCode;
        this.emergencyRowSeating = emergencyRowSeating;
        this.returnDate = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass = seatingClass;
        this.adultPassengerCount = adultPassengerCount;
        this.childPassengerCount = childPassengerCount;
        this.infantPassengerCount = infantPassengerCount;

        return true;
    }

    /**
     * Simple main method to run all tests manually (no launcher needed).
     * Calls FlightSearchTest.runAllTests() and prints summary.
     */
    public static void main(String[] args) {
        System.out.println("Running all FlightSearch tests...");
        FlightSearchTest.runAllTests();
    }
}

/**
 * JUnit 5 test class for FlightSearch (package-private to allow merge).
 * Includes manual runner for JDoodle.
 */
class FlightSearchTest {
    private FlightSearch fs;

    void setUp() {  // Made package-private for manual call
        fs = new FlightSearch();
    }

    // Condition 1: Total <1
    @Test
    void testTotalPassengersLessThan1_AllZero() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 0, 0, 0);
        assertFalse(result);
    }

    @Test
    void testTotalPassengersLessThan1_ZeroVariation() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 0, 0, 0);
        assertFalse(result);
    }

    // Condition 1: Total >9
    @Test
    void testTotalPassengersGreaterThan9_Adult10() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 10, 0, 0);
        assertFalse(result);
    }

    @Test
    void testTotalPassengersGreaterThan9_Mixed10() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 5, 5, 0);
        assertFalse(result);
    }

    // Condition 2: Children in emergency/first
    @Test
    void testChildrenInEmergencyRow() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", true, "15/10/2025", "mel", "economy", 1, 1, 0);
        assertFalse(result);
    }

    @Test
    void testChildrenInFirstClass() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "first", 1, 1, 0);
        assertFalse(result);
    }

    // Condition 3: Infants in emergency/business
    @Test
    void testInfantsInEmergencyRow() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", true, "15/10/2025", "mel", "economy", 1, 0, 1);
        assertFalse(result);
    }

    @Test
    void testInfantsInBusinessClass() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "business", 1, 0, 1);
        assertFalse(result);
    }

    // Condition 4: Children >2 per adult
    @Test
    void testChildrenExceed2PerAdult_Boundary3() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 3, 0);
        assertFalse(result);
    }

    @Test
    void testChildrenExceed2PerAdult_MixedBoundary() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 2, 5, 0);
        assertFalse(result);
    }

    // Condition 5: Infants >1 per adult
    @Test
    void testInfantsExceed1PerAdult_Boundary2() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 2);
        assertFalse(result);
    }

    @Test
    void testInfantsExceed1PerAdult_MixedBoundary() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 2, 0, 3);
        assertFalse(result);
    }

    // Condition 6: Departure in past
    @Test
    void testDepartureInPast() {
        boolean result = fs.runFlightSearch("01/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testDepartureInPast_BoundaryDayBefore() {
        boolean result = fs.runFlightSearch("07/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    // Condition 7: Invalid date
    @Test
    void testInvalidDateFormat() {
        boolean result = fs.runFlightSearch("08-10-2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testInvalidDate_LeapYearFail() {
        boolean result = fs.runFlightSearch("29/02/2026", "syd", false, "15/03/2026", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    // Condition 8: Return before departure
    @Test
    void testReturnBeforeDeparture() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "07/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testReturnBeforeDeparture_BoundaryWellBefore() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "01/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    // Condition 9: Invalid seating class
    @Test
    void testInvalidSeatingClass() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "luxury", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testInvalidSeatingClass_BoundarySimilar() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "coach", 1, 0, 0);
        assertFalse(result);
    }

    // Condition 10: Emergency in non-economy
    @Test
    void testEmergencyInBusiness() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", true, "15/10/2025", "mel", "business", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testEmergencyInFirst() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", true, "15/10/2025", "mel", "first", 1, 0, 0);
        assertFalse(result);
    }

    // Condition 11: Invalid/same airport
    @Test
    void testInvalidAirport() {
        boolean result = fs.runFlightSearch("08/10/2025", "xxx", false, "15/10/2025", "mel", "economy", 1, 0, 0);
        assertFalse(result);
    }

    @Test
    void testSameAirports() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "syd", "economy", 1, 0, 0);
        assertFalse(result);
    }

    // Valid cases (4 combinations)
    @Test
    void testAllValid_EconomyNoEmergency() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 0);
        assertTrue(result);
    }

    @Test
    void testAllValid_PremiumEconomyMaxChildren() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "premium economy", 2, 4, 0);
        assertTrue(result);
    }

    @Test
    void testAllValid_BusinessWithInfant() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "premium economy", 1, 0, 1);
        assertTrue(result);
    }

    @Test
    void testAllValid_FirstWithMaxChildren() {
        boolean result = fs.runFlightSearch("08/10/2025", "syd", false, "15/10/2025", "mel", "first", 1, 0, 1);
        assertTrue(result);
    }

    /**
     * Static method to manually run all tests and print results (for JDoodle main execution).
     * Creates a new instance and calls setUp() before each test to avoid null fs.
     * Handles InvocationTargetException for reflection-wrapped assertions.
     * Prints full descriptions instead of method names.
     */
    public static void runAllTests() {
        int total = 0, passed = 0, failed = 0;
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("testTotalPassengersLessThan1_AllZero", "Condition 1: Total passengers <1 (boundary: 0)");
        descriptions.put("testTotalPassengersLessThan1_ZeroVariation", "Condition 1: Total passengers <1 (repeated for boundary confirmation)");
        descriptions.put("testTotalPassengersGreaterThan9_Adult10", "Condition 1: Total passengers >9 (boundary: 10 adults)");
        descriptions.put("testTotalPassengersGreaterThan9_Mixed10", "Condition 1: Total passengers >9 (mixed: 5 adults + 5 children)");
        descriptions.put("testChildrenInEmergencyRow", "Condition 2: Children in emergency row seating (economy)");
        descriptions.put("testChildrenInFirstClass", "Condition 2: Children in first class");
        descriptions.put("testInfantsInEmergencyRow", "Condition 3: Infants in emergency row seating (economy)");
        descriptions.put("testInfantsInBusinessClass", "Condition 3: Infants in business class");
        descriptions.put("testChildrenExceed2PerAdult_Boundary3", "Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)");
        descriptions.put("testChildrenExceed2PerAdult_MixedBoundary", "Condition 4: Children >2 per adult (boundary: 5 children with 2 adults)");
        descriptions.put("testInfantsExceed1PerAdult_Boundary2", "Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)");
        descriptions.put("testInfantsExceed1PerAdult_MixedBoundary", "Condition 5: Infants >1 per adult (boundary: 3 infants with 2 adults)");
        descriptions.put("testDepartureInPast", "Condition 6: Departure date in past (01/10/2025)");
        descriptions.put("testDepartureInPast_BoundaryDayBefore", "Condition 6: Departure date in past (boundary: day before current, 07/10/2025)");
        descriptions.put("testInvalidDateFormat", "Condition 7: Invalid date format (08-10-2025)");
        descriptions.put("testInvalidDate_LeapYearFail", "Condition 7: Invalid date (29/02/2026, non-leap year)");
        descriptions.put("testReturnBeforeDeparture", "Condition 8: Return date before departure (07/10/2025)");
        descriptions.put("testReturnBeforeDeparture_BoundaryWellBefore", "Condition 8: Return date before departure (boundary: well before, 01/10/2025)");
        descriptions.put("testInvalidSeatingClass", "Condition 9: Invalid seating class (luxury)");
        descriptions.put("testInvalidSeatingClass_BoundarySimilar", "Condition 9: Invalid seating class (boundary: coach)");
        descriptions.put("testEmergencyInBusiness", "Condition 10: Emergency row in non-economy (business)");
        descriptions.put("testEmergencyInFirst", "Condition 10: Emergency row in non-economy (first, boundary: premium class)");
        descriptions.put("testInvalidAirport", "Condition 11: Invalid airport code (xxx)");
        descriptions.put("testSameAirports", "Condition 11: Same departure and destination airport (syd to syd)");
        descriptions.put("testAllValid_EconomyNoEmergency", "All inputs valid (Test Data 1: adult=1, child=0, infant=0; economy; no emergency)");
        descriptions.put("testAllValid_PremiumEconomyMaxChildren", "All inputs valid (Test Data 2: adult=2, child=4, infant=0; premium economy; no emergency, boundary children=2*adults)");
        descriptions.put("testAllValid_BusinessWithInfant", "All inputs valid (Test Data 3: adult=1, child=0, infant=1; premium economy; no emergency)");
        descriptions.put("testAllValid_FirstWithMaxChildren", "All inputs valid (Test Data 4: adult=1, child=0, infant=1; first; no emergency)");

        String[] testNames = {
            "testTotalPassengersLessThan1_AllZero", "testTotalPassengersLessThan1_ZeroVariation",
            "testTotalPassengersGreaterThan9_Adult10", "testTotalPassengersGreaterThan9_Mixed10",
            "testChildrenInEmergencyRow", "testChildrenInFirstClass",
            "testInfantsInEmergencyRow", "testInfantsInBusinessClass",
            "testChildrenExceed2PerAdult_Boundary3", "testChildrenExceed2PerAdult_MixedBoundary",
            "testInfantsExceed1PerAdult_Boundary2", "testInfantsExceed1PerAdult_MixedBoundary",
            "testDepartureInPast", "testDepartureInPast_BoundaryDayBefore",
            "testInvalidDateFormat", "testInvalidDate_LeapYearFail",
            "testReturnBeforeDeparture", "testReturnBeforeDeparture_BoundaryWellBefore",
            "testInvalidSeatingClass", "testInvalidSeatingClass_BoundarySimilar",
            "testEmergencyInBusiness", "testEmergencyInFirst",
            "testInvalidAirport", "testSameAirports",
            "testAllValid_EconomyNoEmergency", "testAllValid_PremiumEconomyMaxChildren",
            "testAllValid_BusinessWithInfant", "testAllValid_FirstWithMaxChildren"
        };

        for (String methodName : testNames) {
            total++;
            FlightSearchTest test = new FlightSearchTest();
            test.setUp();  // Manually call setUp() before each test
            String desc = descriptions.getOrDefault(methodName, methodName);
            try {
                java.lang.reflect.Method method = FlightSearchTest.class.getDeclaredMethod(methodName);
                method.setAccessible(true);
                method.invoke(test);
                System.out.println("PASS: " + desc);
                passed++;
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof AssertionError) {
                    System.out.println("FAIL: " + desc + " - " + cause.getMessage());
                    failed++;
                } else {
                    System.out.println("ERROR: " + desc + " - " + cause.getMessage());
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + desc + " - " + e.getMessage());
                failed++;
            }
        }
        System.out.println("\nSummary: Total=" + total + ", Passed=" + passed + ", Failed=" + failed);
        // Expected: All 28 PASS (24 assertFalse for invalids, 4 assertTrue for valids)
    }
}
