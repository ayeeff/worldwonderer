# WorldWanderer Flight Search Validation

## Overview
This is a Java 17 Maven project that implements flight search parameter validation for the WorldWanderer booking system. It validates 11 different conditions before allowing a flight search to proceed.

## Project Structure
- **Language**: Java 17
- **Build Tool**: Maven
- **Testing Framework**: JUnit Jupiter 5.10.0
- **Main Class**: `src/main/java/flight/FlightSearch.java`
- **Test Class**: `src/test/java/flight/FlightSearchTest.java`

## Validation Rules
The `runFlightSearch` method validates:
1. Total passengers (1-9)
2. No children in emergency rows or first class
3. No infants in emergency rows or business class
4. Max 2 children per adult
5. Max 1 infant per adult
6. Departure date not in the past
7. Valid DD/MM/YYYY date format (strict leap year checking)
8. Return date after departure date
9. Valid seating class (economy/premium economy/business/first)
10. Emergency row only in economy class
11. Valid and distinct IATA airport codes

## Recent Changes
- **2025-10-09**: Project imported and configured for Replit environment
  - Fixed pom.xml to use proper XML format
  - Updated Java version from 11 to 17 in pom.xml
  - Fixed test file compilation issues
  - Added strict date validation using ResolverStyle.STRICT
  - All 12 JUnit tests now passing

## Running the Project
- **Build**: `mvn compile`
- **Test**: `mvn test` (runs automatically via the Tests workflow)
- **Demo**: `mvn compile exec:java -Dexec.mainClass="flight.FlightSearchDemo"`
- **Clean**: `mvn clean`

## Architecture
- Main validation class: `FlightSearch.java` with business logic
- Demo application: `FlightSearchDemo.java` for interactive demonstration
- Comprehensive test suite with 12 test methods covering all validation rules
- Uses Java's DateTimeFormatter with strict resolver for proper date validation
- Immutable whitelists for airports and seating classes

## Files
- `src/main/java/flight/FlightSearch.java` - Core validation logic
- `src/main/java/flight/FlightSearchDemo.java` - Demo application
- `src/test/java/flight/FlightSearchTest.java` - JUnit test suite
- `pom.xml` - Maven configuration
