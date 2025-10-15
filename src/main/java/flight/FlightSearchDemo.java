package flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FlightSearchDemo {
    
    public static void main(String[] args) {
        // Check if user wants to run tests
        if (args.length > 0 && args[0].equals("--test")) {
            runTestsWithReflection();
            return;
        }
        
        // Check if user wants both demo and tests
        if (args.length > 0 && args[0].equals("--all")) {
            runDemo();
            System.out.println("\n");
            runTestsWithReflection();
            return;
        }
        
        // Otherwise run the normal demo
        runDemo();
    }
    
    /**
     * Run JUnit tests using reflection to show detailed descriptions.
     * This avoids duplicating test logic - we reuse FlightSearchTest from test scope.
     */
    private static void runTestsWithReflection() {
        System.out.println("=".repeat(80));
        System.out.println("Running FlightSearch Tests with Detailed Descriptions");
        System.out.println("=".repeat(80) + "\n");
        
        int total = 0, passed = 0, failed = 0;
        
        // Method names and their descriptions
        String[][] tests = {
            {"totalPassengersBoundary", "Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10)"},
            {"childSeatRestrictions", "Condition 2: Children in emergency row seating or first class"},
            {"infantSeatRestrictions", "Condition 3: Infants in emergency row seating or business class"},
            {"childrenAdultRatio", "Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)"},
            {"infantAdultRatio", "Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)"},
            {"departureNotPast", "Condition 6: Departure date in past (boundary: day before current)"},
            {"dateValidation", "Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04)"},
            {"returnAfterDeparture", "Condition 8: Return date before departure and same-day valid"},
            {"seatingClass", "Condition 9: Invalid seating class (luxury) and valid (premium economy)"},
            {"emergencyEconomyOnly", "Condition 10: Emergency row in non-economy (business) and valid (economy)"},
            {"airportCodes", "Condition 11: Same departure/destination airport and invalid airport code"},
            {"allValid", "All inputs valid - Test Data 1-4 (multiple valid combinations)"}
        };
        
        try {
            // Try to load the test class (will fail if running from compiled JAR without test classes)
            Class<?> testClass = Class.forName("flight.FlightSearchTest");
            
            for (String[] test : tests) {
                total++;
                String methodName = test[0];
                String description = test[1];
                System.out.print("Validation: " + description + " ... ");
                
                try {
                    Object testInstance = testClass.getDeclaredConstructor().newInstance();
                    java.lang.reflect.Method method = testClass.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    method.invoke(testInstance);
                    System.out.println("✓ TRUE");
                    passed++;
                } catch (java.lang.reflect.InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    System.out.println("✗ FALSE");
                    if (cause != null) {
                        System.out.println("  Error: " + cause.getMessage());
                    }
                    failed++;
                } catch (Exception e) {
                    System.out.println("✗ ERROR");
                    System.out.println("  Error: " + e.getMessage());
                    failed++;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: FlightSearchTest class not found.");
            System.out.println("This usually means test classes aren't on the classpath.");
            System.out.println("Try running: mvn test-compile exec:java -Dexec.args='--test' -Dexec.classpathScope=test");
            System.exit(1);
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
