package flight;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FlightSearchDemo {
    
    public static void main(String[] args) {
        // Debug: print received arguments
        System.out.println("Received " + args.length + " arguments");
        for (int i = 0; i < args.length; i++) {
            System.out.println("arg[" + i + "] = '" + args[i] + "'");
        }
        
        // Check if user wants to run tests
        if (args.length > 0 && args[0].equals("--test")) {
            runAllTestsWithDescriptions();
            return;
        }
        
        // Otherwise run the normal demo
        runDemo();
    }
    
    /**
     * Run all tests with detailed descriptions (like JDoodle version).
     * Call with: java flight.FlightSearchDemo --test
     * Or with Maven: mvn exec:java -Dexec.mainClass="flight.FlightSearchDemo" -Dexec.args="--test"
     */
    public static void runAllTestsWithDescriptions() {
        System.out.println("=".repeat(80));
        System.out.println("Running FlightSearch Tests with Detailed Descriptions");
        System.out.println("=".repeat(80) + "\n");
        
        int total = 0, passed = 0, failed = 0;
        
        // Map method names to descriptions
        Map<String, String> descriptions = new HashMap<>();
        descriptions.put("totalPassengersBoundary", 
            "Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10)");
        descriptions.put("childSeatRestrictions", 
            "Condition 2: Children in emergency row seating (economy) or first class");
        descriptions.put("infantSeatRestrictions", 
            "Condition 3: Infants in emergency row seating (economy) or business class");
        descriptions.put("childrenAdultRatio", 
            "Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)");
        descriptions.put("infantAdultRatio", 
            "Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)");
        descriptions.put("departureNotPast", 
            "Condition 6: Departure date in past (boundary: day before current)");
        descriptions.put("dateValidation", 
            "Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04)");
        descriptions.put("returnAfterDeparture", 
            "Condition 8: Return date before departure (boundary: well before)");
        descriptions.put("seatingClass", 
            "Condition 9: Invalid seating class (luxury) and valid (premium economy)");
        descriptions.put("emergencyEconomyOnly", 
            "Condition 10: Emergency row in non-economy (business) and valid (economy)");
        descriptions.put("airportCodes", 
            "Condition 11: Same departure/destination airport and invalid airport code");
        descriptions.put("allValid", 
            "All inputs valid - Test Data 1-4 (multiple valid combinations)");
        
        String[] testMethods = {
            "totalPassengersBoundary",
            "childSeatRestrictions",
            "infantSeatRestrictions",
            "childrenAdultRatio",
            "infantAdultRatio",
            "departureNotPast",
            "dateValidation",
            "returnAfterDeparture",
            "seatingClass",
            "emergencyEconomyOnly",
            "airportCodes",
            "allValid"
        };
        
        for (String methodName : testMethods) {
            total++;
            String desc = descriptions.getOrDefault(methodName, methodName);
            System.out.print("Testing: " + desc + " ... ");
            
            try {
                FlightSearchTest test = new FlightSearchTest();
                Method method = FlightSearchTest.class.getDeclaredMethod(methodName);
                method.setAccessible(true);
                method.invoke(test);
                System.out.println("✓ PASS");
                passed++;
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof AssertionError) {
                    System.out.println("✗ FAIL");
                    System.out.println("  Error: " + cause.getMessage());
                    failed++;
                } else {
                    System.out.println("✗ ERROR");
                    System.out.println("  Error: " + cause.getMessage());
                    failed++;
                }
            } catch (Exception e) {
                System.out.println("✗ ERROR");
                System.out.println("  Error: " + e.getMessage());
                failed++;
            }
        }
        
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
