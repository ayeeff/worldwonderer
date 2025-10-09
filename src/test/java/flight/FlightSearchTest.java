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
    @Test @DisplayName("C1 – total passengers 0 or 10") void totalPassengersBoundary() {
        assertFalse(search(0,0,0));   // 0 passengers
        assertFalse(search(9,1,0));   // 10 passengers
    }

    @Test @DisplayName("C2 – child in emergency row or first") void childSeatRestrictions() {
        assertFalse(search(1,1,0,true,"first",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE));
        assertFalse(search(1,1,0,true,"economy",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE));
    }

    @Test @DisplayName("C3 – infant in emergency row or business") void infantSeatRestrictions() {
        assertFalse(search(1,0,1,true,"business",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE));
        assertFalse(search(1,0,1,true,"economy",VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE));
    }

    @Test @DisplayName("C4 – children per adult ratio") void childrenAdultRatio() {
        assertFalse(search(1,3,0)); // 1:3  (max 2 allowed)
        assertTrue(search(2,4,0));  // 2:4  (exactly 2 per adult)
    }

    @Test @DisplayName("C5 – infants per adult ratio") void infantAdultRatio() {
        assertFalse(search(1,0,2)); // 1 adult 2 infants
        assertFalse(search(0,0,1)); // 0 adult 1 infant
    }

    @Test @DisplayName("C6 – departure not in past") void departureNotPast() {
        assertFalse(search(YESTERDAY_STR, VALID_DEP_DATE));
        assertTrue(search(TODAY_STR, VALID_DEP_DATE));
    }

    @Test @DisplayName("C7 – strict date validation") void dateValidation() {
        assertFalse(search("29/02/2026","07/03/2026")); // not leap
        assertFalse(search("31/04/2025","07/05/2025")); // 31 Apr
    }

    @Test @DisplayName("C8 – return not before departure") void returnAfterDeparture() {
        String dayBeforeDep = TODAY.plusDays(1).minusDays(1).format(DF);
        assertFalse(search(VALID_DEP_DATE, dayBeforeDep));
        assertTrue(search(VALID_DEP_DATE, VALID_DEP_DATE));
    }

    @Test @DisplayName("C9 – seating class whitelist") void seatingClass() {
        assertFalse(search("luxury"));
        assertTrue(search("premium economy"));
    }

    @Test @DisplayName("C10 – emergency row only in economy") void emergencyEconomyOnly() {
        assertFalse(search(true,"business"));
        assertTrue(search(true,"economy"));
    }

    @Test @DisplayName("C11 – airport whitelist & distinct") void airportCodes() {
        assertFalse(search(1,0,0,false,VALID_CLASS,"syd","syd",VALID_DEP_DATE,VALID_RET_DATE));
        assertFalse(search(1,0,0,false,VALID_CLASS,"xyz","mel",VALID_DEP_DATE,VALID_RET_DATE));
    }

    @Test @DisplayName("All-valid combinations") void allValid() {
        assertTrue(search(2,2,0,false,"economy","syd","pvg",TOMORROW_STR,TODAY.plusDays(14).format(DF)));
        assertTrue(search(1,0,1,false,"economy","mel","lax",TOMORROW_STR,TODAY.plusDays(21).format(DF)));
        assertTrue(search(3,0,0,true,"economy","doh","cdg",TOMORROW_STR,TODAY.plusDays(10).format(DF)));
        assertTrue(search(1,1,0,false,"premium economy","del","syd",TOMORROW_STR,TODAY.plusDays(5).format(DF)));
    }

    /* ---------- overloaded helpers ---------- */
    private boolean search(int a,int c,int i){ return search(a,c,i,false,VALID_CLASS,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); }
    private boolean search(boolean em, String cl){ return search(1,0,0,em,cl,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); }
    private boolean search(String cl){ return search(1,0,0,false,cl,VALID_DEP,VALID_DES,VALID_DEP_DATE,VALID_RET_DATE); }
    private boolean search(String depDate, String retDate){ return search(1,0,0,false,VALID_CLASS,VALID_DEP,VALID_DES,depDate,retDate); }

    /* ---------- master helper ---------- */
    private boolean search(int a,int c,int i,boolean em,String cl,String dep,String des,String depDate,String retDate){
        FlightSearch fs = new FlightSearch();
        return fs.runFlightSearch(depDate,dep,em,retDate,des,cl,a,c,i);
    }
}
