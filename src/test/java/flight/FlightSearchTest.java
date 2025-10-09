package flight;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import org.junit.jupiter.api.*;

/** JUnit-5 test class for FlightSearch::runFlightSearch */
class FlightSearchTest {

    /* ---------- helper to build yesterday / today / tomorrow ---------- */
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final LocalDate TODAY = LocalDate.now();
    private static final String TODAY_STR = TODAY.format(DF);
    private static final String YESTERDAY_STR = TODAY.minusDays(1).format(DF);
    private static final String TOMORROW_STR = TODAY.plusDays(1).format(DF);

    /* ---------- reusable valid core ---------- */
    private static final String VALID_DEP = "syd";
    private static final String VALID_DES = "mel";
    private static final String VALID_DEP_DATE = TOMORROW_STR;
    private static final String VALID_RET_DATE = TODAY.plusDays(7).format(DF);
    private static final String VALID_CLASS = "economy";

    /* ---------- test groups ---------- */
    @Test @DisplayName("Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10)") 
    void totalPassengersBoundary() {
        assertFalse(search(0,0,0), "Total passengers <1 (boundary: 0)");
        assertFalse(search(9,1,0), "Total passengers >9 (mixed: 9 adults + 1 child = 10)");
    }

    @Test @DisplayName("Condition 2: Children in emergency row seating (economy) or first class") 
    void childSeatRestrictions() {
        assertFalse(search(1,1,0,true,"first",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE), 
                   "Condition 2: Children in first class");
        assertFalse(search(1,1,0,true,"economy",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE),
                   "Condition 2: Children in emergency row seating (economy)");
    }

    @Test @DisplayName("Condition 3: Infants in emergency row seating (economy) or business class") 
    void infantSeatRestrictions() {
        assertFalse(search(1,0,1,true,"business",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE),
                   "Condition 3: Infants in business class");
        assertFalse(search(1,0,1,true,"economy",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE),
                   "Condition 3: Infants in emergency row seating (economy)");
    }

    @Test @DisplayName("Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)") 
    void childrenAdultRatio() {
        assertFalse(search(1,3,0), "Condition 4: Children >2 per adult (boundary: 3 children with 1 adult)");
        assertTrue(search(2,4,0), "Condition 4: Valid boundary children=2*adults (2 adults, 4 children)");
    }

    @Test @DisplayName("Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)") 
    void infantAdultRatio() {
        assertFalse(search(1,0,2), "Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult)");
        assertFalse(search(0,0,1), "Condition 5: Infants >1 per adult (0 adults, 1 infant)");
    }

    @Test @DisplayName("Condition 6: Departure date in past (boundary: day before current)") 
    void departureNotPast() {
        assertFalse(search(YESTERDAY_STR, VALID_RET_DATE), 
                   "Condition 6: Departure date in past (boundary: day before current)");
        assertTrue(search(TOMORROW_STR, VALID_RET_DATE),
                  "Condition 6: Valid departure date (tomorrow)");
    }

    @Test @DisplayName("Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04)") 
    void dateValidation() {
        assertFalse(search("29/02/2026","07/03/2026"), 
                   "Condition 7: Invalid date (29/02/2026, non-leap year)");
        assertFalse(search("31/04/2025","07/05/2025"), 
                   "Condition 7: Invalid date (31/04/2025, April has only 30 days)");
    }

    @Test @DisplayName("Condition 8: Return date before departure (boundary: well before)") 
    void returnAfterDeparture() {
        String dayBeforeDep = TODAY.plusDays(1).minusDays(1).format(DF);
        assertFalse(search(VALID_DEP_DATE, dayBeforeDep),
                   "Condition 8: Return date before departure (boundary: day before)");
        assertTrue(search(VALID_DEP_DATE, VALID_DEP_DATE),
                  "Condition 8: Valid return date (same as departure)");
    }

    @Test @DisplayName("Condition 9: Invalid seating class (luxury) and invalid seating class (boundary: coach)") 
    void seatingClass() {
        assertFalse(search("luxury"), "Condition 9: Invalid seating class (luxury)");
        assertTrue(search("premium economy"), "Condition 9: Valid seating class (premium economy)");
    }

    @Test @DisplayName("Condition 10: Emergency row in non-economy (business)") 
    void emergencyEconomyOnly() {
        assertFalse(search(true,"business"), 
                   "Condition 10: Emergency row in non-economy (business)");
        assertTrue(search(true,"economy"),
                  "Condition 10: Valid emergency row in economy");
    }

    @Test @DisplayName("Condition 11: Same departure and destination airport (syd to syd) and invalid airport code") 
    void airportCodes() {
        assertFalse(search(1,0,0,false,VALID_CLASS,"syd","syd",VALID_DEP_DATE,VALID_RET_DATE),
                   "Condition 11: Same departure and destination airport (syd to syd)");
        assertFalse(search(1,0,0,false,VALID_CLASS,"xyz","mel",VALID_DEP_DATE,VALID_RET_DATE),
                   "Condition 11: Invalid airport code (xyz)");
    }

    @Test @DisplayName("All inputs valid - Test Data 1-4") 
    void allValid() {
        assertTrue(search(2,2,0,false,"economy","syd","pvg",TOMORROW_STR,TODAY.plusDays(14).format(DF)),
                  "All inputs valid (Test Data 1: adult=2, child=2, infant=0; economy; no emergency)");
        assertTrue(search(1,0,1,false,"economy","mel","lax",TOMORROW_STR,TODAY.plusDays(21).format(DF)),
                  "All inputs valid (Test Data 2: adult=1, child=0, infant=1; economy; no emergency)");
        assertTrue(search(3,0,0,true,"economy","doh","cdg",TOMORROW_STR,TODAY.plusDays(10).format(DF)),
                  "All inputs valid (Test Data 3: adult=3, child=0, infant=0; economy; emergency row)");
        assertTrue(search(1,1,0,false,"premium economy","del","syd",TOMORROW_STR,TODAY.plusDays(5).format(DF)),
                  "All inputs valid (Test Data 4: adult=1, child=1, infant=0; premium economy; no emergency)");
    }

    /* ---------- overloaded helpers ---------- */
    private boolean search(int a,int c,int i){ 
        return search(a,c,i,false,VALID_CLASS,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); 
    }
    
    private boolean search(boolean em, String cl){ 
        return search(1,0,0,em,cl,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); 
    }
    
    private boolean search(String cl){ 
        return search(1,0,0,false,cl,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); 
    }
    
    private boolean search(String depDate, String retDate){ 
        return search(1,0,0,false,VALID_CLASS,VALID_DEP,VALID_DES,depDate,retDate); 
    }

    /* ---------- master helper ---------- */
    private boolean search(int a,int c,int i,boolean em,String cl,String dep,String des,String depDate,String retDate){
        FlightSearch fs = new FlightSearch();
        return fs.runFlightSearch(depDate,dep,em,retDate,des,cl,a,c,i);
    }
}
