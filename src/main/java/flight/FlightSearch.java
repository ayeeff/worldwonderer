package flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Set;

public class FlightSearch {

    /* -------------------------- attributes -------------------------- */
    private String  departureDate;
    private String  departureAirportCode;
    private boolean emergencyRowSeating;
    private String  returnDate;
    private String  destinationAirportCode;
    private String  seatingClass;
    private int     adultPassengerCount;
    private int     childPassengerCount;
    private int     infantPassengerCount;

    /* ---------------------- white-lists & const --------------------- */
    private static final Set<String> AIRPORTS = Set.of("syd","mel","lax","cdg","del","pvg","doh");
    private static final Set<String> CLASSES  = Set.of("economy","premium economy","business","first");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /* ---------------------- main business method -------------------- */
    public boolean runFlightSearch(String departureDate,
                                   String departureAirportCode,
                                   boolean emergencyRowSeating,
                                   String returnDate,
                                   String destinationAirportCode,
                                   String seatingClass,
                                   int adultPassengerCount,
                                   int childPassengerCount,
                                   int infantPassengerCount) {

        /* ---- C1 ---- */
        int total = adultPassengerCount + childPassengerCount + infantPassengerCount;
        if (total < 1 || total > 9) return false;

        /* ---- C2 ---- */
        if (childPassengerCount > 0 &&
            (emergencyRowSeating || "first".equals(seatingClass))) return false;

        /* ---- C3 ---- */
        if (infantPassengerCount > 0 &&
            (emergencyRowSeating || "business".equals(seatingClass))) return false;

        /* ---- C4 ---- */
        if (childPassengerCount > adultPassengerCount * 2) return false;

        /* ---- C5 ---- */
        if (infantPassengerCount > adultPassengerCount) return false;

        /* ---- C6 & C7 ---- */
        LocalDate depDate, retDate;
        try {
            depDate = LocalDate.parse(departureDate, DF);
            retDate = LocalDate.parse(returnDate, DF);
        } catch (DateTimeParseException ex) {
            return false; // invalid format or illegal date
        }
        if (depDate.isBefore(LocalDate.now())) return false;

        /* ---- C8 ---- */
        if (retDate.isBefore(depDate)) return false;

        /* ---- C9 ---- */
        if (!CLASSES.contains(seatingClass)) return false;

        /* ---- C10 ---- */
        if (emergencyRowSeating && !"economy".equals(seatingClass)) return false;

        /* ---- C11 ---- */
        if (!AIRPORTS.contains(departureAirportCode) ||
            !AIRPORTS.contains(destinationAirportCode) ||
            departureAirportCode.equals(destinationAirportCode)) return false;

        /* ---------- all good – persist attributes ---------- */
        this.departureDate        = departureDate;
        this.departureAirportCode = departureAirportCode;
        this.emergencyRowSeating  = emergencyRowSeating;
        this.returnDate           = returnDate;
        this.destinationAirportCode = destinationAirportCode;
        this.seatingClass         = seatingClass;
        this.adultPassengerCount  = adultPassengerCount;
        this.childPassengerCount  = childPassengerCount;
        this.infantPassengerCount = infantPassengerCount;

        return true;
    }

    /* -------------------- getters (optional) ----------------------- */
    public String getDepartureDate() { return departureDate; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public String getReturnDate() { return returnDate; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public String getSeatingClass() { return seatingClass; }
    public int getAdultPassengerCount() { return adultPassengerCount; }
    public int getChildPassengerCount() { return childPassengerCount; }
    public int getInfantPassengerCount() { return infantPassengerCount; }
}
