package flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FlightSearchDemo {
    public static void main(String[] args) {
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
