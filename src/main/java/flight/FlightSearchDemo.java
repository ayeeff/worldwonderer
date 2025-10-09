package flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class FlightSearchDemo {
    
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    
    public static void main(String[] args) {
        // Check if user wants to run tests
        if (args.length > 0 && args[0].equals("--test")) {
            runAllTestsWithDescriptions();
            return;
        }
        
        // Otherwise run the normal demo
        runDemo();
    }
    
    /**
     * Run all tests with detailed descriptions (standalone, no JUnit dependency).
     */
    public static void runAllTestsWithDescriptions() {
        System.out.println("=".repeat(80));
        System.out.println("Running FlightSearch Tests with Detailed Descriptions");
        System.out.println("=".repeat(80) + "\n");
        
        int total = 0, passed = 0, failed = 0;
        LocalDate TODAY = LocalDate.now();
        String TOMORROW = TODAY.plusDays(1).format(DF);
        String NEXT_WEEK = TODAY.plusDays(7).format(DF);
        String YESTERDAY = TODAY.minusDays(1).format(DF);
        
        // Test 1: Condition 1 - Total passengers boundary
        total++;
        System.out.print("Testing: Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 0, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 9, 1, 0);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 2: Condition 2 - Children restrictions
        total++;
        System.out.print("Testing: Condition 2: Children in emergency row seating or first class ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "first", 1, 1, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "economy", 1, 1, 0);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 3: Condition 3 - Infant restrictions
        total++;
        System.out.print("Testing: Condition 3: Infants in emergency row seating or business class ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "business", 1, 0, 1);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "economy", 1, 0, 1);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 4: Condition 4 - Children per adult ratio
        total++;
        System.out.print("Testing: Condition 4: Children >2 per adult (boundary: 3 children with 1 adult) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 1, 3, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 2, 4, 0);
            if (!r1 && r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 5: Condition 5 - Infants per adult ratio
        total++;
        System.out.print("Testing: Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 1, 0, 2);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 0, 0, 1);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 6: Condition 6 - Departure not in past
        total++;
        System.out.print("Testing: Condition 6: Departure date in past (boundary: day before current) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(YESTERDAY, "syd", false, NEXT_WEEK, "mel", "economy", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "economy", 1, 0, 0);
            if (!r1 && r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 7: Condition 7 - Date validation
        total++;
        System.out.print("Testing: Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch("29/02/2026", "syd", false, "07/03/2026", "mel", "economy", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch("31/04/2025", "syd", false, "07/05/2025", "mel", "economy", 1, 0, 0);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 8: Condition 8 - Return after departure
        total++;
        System.out.print("Testing: Condition 8: Return date before departure and same-day valid ... ");
        try {
            String dayBefore = TODAY.plusDays(1).minusDays(1).format(DF);
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, dayBefore, "mel", "economy", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, TOMORROW, "mel", "economy", 1, 0, 0);
            if (!r1 && r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 9: Condition 9 - Seating class validation
        total++;
        System.out.print("Testing: Condition 9: Invalid seating class (luxury) and valid (premium economy) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "luxury", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "mel", "premium economy", 1, 0, 0);
            if (!r1 && r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 10: Condition 10 - Emergency row only in economy
        total++;
        System.out.print("Testing: Condition 10: Emergency row in non-economy (business) and valid (economy) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "business", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "syd", true, NEXT_WEEK, "mel", "economy", 1, 0, 0);
            if (!r1 && r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 11: Condition 11 - Airport codes
        total++;
        System.out.print("Testing: Condition 11: Same departure/destination airport and invalid airport code ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, NEXT_WEEK, "syd", "economy", 1, 0, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "xyz", false, NEXT_WEEK, "mel", "economy", 1, 0, 0);
            if (!r1 && !r2) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        // Test 12: All valid combinations
        total++;
        System.out.print("Testing: All inputs valid - Test Data 1-4 (multiple valid combinations) ... ");
        try {
            FlightSearch fs1 = new FlightSearch();
            boolean r1 = fs1.runFlightSearch(TOMORROW, "syd", false, TODAY.plusDays(14).format(DF), "pvg", "economy", 2, 2, 0);
            FlightSearch fs2 = new FlightSearch();
            boolean r2 = fs2.runFlightSearch(TOMORROW, "mel", false, TODAY.plusDays(21).format(DF), "lax", "economy", 1, 0, 1);
            FlightSearch fs3 = new FlightSearch();
            boolean r3 = fs3.runFlightSearch(TOMORROW, "doh", true, TODAY.plusDays(10).format(DF), "cdg", "economy", 3, 0, 0);
            FlightSearch fs4 = new FlightSearch();
            boolean r4 = fs4.runFlightSearch(TOMORROW, "del", false, TODAY.plusDays(5).format(DF), "syd", "premium economy", 1, 1, 0);
            if (r1 && r2 && r3 && r4) { System.out.println("✓ PASS"); passed++; }
            else { System.out.println("✗ FAIL"); failed++; }
        } catch (Exception e) { System.out.println("✗ ERROR: " + e.getMessage()); failed++; }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Test Execution Summary:");
        System.out.println("  Total Tests: " + total);
        System.out.println("  Passed:      " + passed + " ✓");
        System.out.println("  Failed:      " + failed + (failed > 0 ? " ✗" : ""));
        System.out.println("  Success Rate: " + (passed * 100 / total) + "%");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Run the normal flight search demo.
     */
    public static void runDemo() {
        FlightSearch fs = new FlightSearch();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String today = LocalDate.now().plusDays(7).format(df);
        String returnDate = LocalDate.now().plusDays(14).format(df);
        
        System.out.println("=== WorldWanderer Flight Search Validation Demo ===\n");
        
        boolean result = fs.runFlightSearch(
            today,
            "syd",
            false,
            returnDate,
            "mel",
            "economy",
            2,
            1,
            0
        );
        
        System.out.println("Search Parameters:");
        System.out.println("  Departure: " + today + " from SYD");
        System.out.println("  Return: " + returnDate + " from MEL");
        System.out.println("  Class: economy (no emergency row)");
        System.out.println("  Passengers: 2 adults, 1 child, 0 infants");
        System.out.println("\nValidation Result: " + (result ? "VALID ✓" : "INVALID ✗"));
        
        if (result) {
            System.out.println("\nStored Flight Details:");
            System.out.println("  Departure Date: " + fs.getDepartureDate());
            System.out.println("  Departure Airport: " + fs.getDepartureAirportCode().toUpperCase());
            System.out.println("  Return Date: " + fs.getReturnDate());
            System.out.println("  Destination Airport: " + fs.getDestinationAirportCode().toUpperCase());
            System.out.println("  Seating Class: " + fs.getSeatingClass());
            System.out.println("  Adults: " + fs.getAdultPassengerCount());
            System.out.println("  Children: " + fs.getChildPassengerCount());
            System.out.println("  Infants: " + fs.getInfantPassengerCount());
            System.out.println("  Emergency Row: " + (fs.isEmergencyRowSeating() ? "Yes" : "No"));
        }
    }
}
