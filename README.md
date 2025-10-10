# WorldWanderer Flight Search Validation

## Overview
This is a Java 17 Maven project that implements flight search parameter validation for the WorldWanderer booking system. It validates 11 different conditions before allowing a flight search to proceed.

## Project Structure
- **Language**: Java 17
- **Build Tool**: Maven
- **Testing Framework**: JUnit Jupiter 5.10.3
- **pom.xml**: pom.xml
The pom.xml file configures the Maven build tool for the project, specifying Java 17 compatibility, dependencies like JUnit for testing, and plugins for compiling code and running tests or demos. It ensures consistent builds across environments, allowing easy commands like mvn test to validate the flight search logic without manual setup.
- **Main Class**: `src/main/java/flight/FlightSearch.java`
This main class implements the core flight search validation, checking parameters like dates, airports, and passengers against 11 business rules before storing valid details. It uses strict date parsing and whitelists for airports and classes to enforce data integrity in booking systems.
- **Test Class**: `src/test/java/flight/FlightSearchTest.java`
The test class uses JUnit to run 28 targeted tests covering edge cases for each validation condition, ensuring the main class handles failures like invalid dates or excess passengers correctly. It includes a manual runner for quick execution and reporting, achieving full coverage for reliable software.
- **Demo Class**: `src/main/java/flight/FlightSearchDemo.java`
This demo class runs a sample valid flight search to showcase the main class in action, printing parameters and results for easy understanding. It also invokes tests via reflection based on command-line args, providing a user-friendly way to demo and verify the application without an IDE.

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

## Running the Project

### 1. Demo Only (shows search parameters and validation)
```bash
mvn exec:java
```
Shows a sample flight search with validation results and stored flight details.

### 2. Tests Only (shows 12 test results with detailed descriptions)
```bash
mvn test-compile exec:java -Dexec.args="--test"
```
Runs all 12 tests with detailed condition descriptions. Each test shows which condition is being validated and whether it passed or failed.

### 3. Both Demo AND Tests
```bash
mvn test-compile exec:java -Dexec.args="--all"
```
Runs the demo followed by all tests with detailed descriptions.

### 4. Standard JUnit Tests (summary only)
```bash
mvn test
```
Runs JUnit tests through Maven with standard test output (method names only, no detailed descriptions).

### Other Commands
- **Build**: `mvn compile`
- **Clean**: `mvn clean`
- **Clean and rebuild**: `mvn clean install`

## Architecture
- **Main validation class**: `FlightSearch.java` with business logic
- **Demo application**: `FlightSearchDemo.java` for interactive demonstration
  - Runs demo with sample data (default)
  - Runs tests with detailed descriptions (`--test` flag)
  - Runs both demo and tests (`--all` flag)
- **Comprehensive test suite**: `FlightSearchTest.java` with 12 test methods covering all validation rules
- Uses Java's DateTimeFormatter with strict resolver for proper date validation
- Immutable whitelists for airports and seating classes
- Single source of truth: test logic only in FlightSearchTest.java, called via reflection from demo

## Test Coverage
- **12 test methods** covering:
  - 11 validation conditions (with boundary testing)
  - 4 valid flight search combinations
- **26 total assertions** (each test method contains 2+ assertions)
- **100% success rate** when all conditions are properly validated

## Files
- `src/main/java/flight/FlightSearch.java` - Core validation logic
- `src/main/java/flight/FlightSearchDemo.java` - Demo application with test runner
- `src/test/java/flight/FlightSearchTest.java` - JUnit 5 test suite
- `pom.xml` - Maven configuration with exec plugin for easy execution

## Example Output

### Demo Mode (`mvn exec:java`)
```
=== WorldWanderer Flight Search Validation Demo ===

Search Parameters:
  Departure: 16/10/2025 from SYD
  Return: 23/10/2025 from MEL
  Class: economy (no emergency row)
  Passengers: 2 adults, 1 child, 0 infants

Validation Result: VALID ✓

Stored Flight Details:
  Departure Date: 16/10/2025
  Departure Airport: SYD
  Return Date: 23/10/2025
  Destination Airport: MEL
  Seating Class: economy
  Adults: 2
  Children: 1
  Infants: 0
  Emergency Row: No
```

### Test Mode (`mvn test-compile exec:java -Dexec.args="--test"`)
```
================================================================================
Running FlightSearch Tests with Detailed Descriptions
================================================================================

Testing: Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10) ... ✓ PASS
Testing: Condition 2: Children in emergency row seating or first class ... ✓ PASS
Testing: Condition 3: Infants in emergency row seating or business class ... ✓ PASS
Testing: Condition 4: Children >2 per adult (boundary: 3 children with 1 adult) ... ✓ PASS
Testing: Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult) ... ✓ PASS
Testing: Condition 6: Departure date in past (boundary: day before current) ... ✓ PASS
Testing: Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04) ... ✓ PASS
Testing: Condition 8: Return date before departure and same-day valid ... ✓ PASS
Testing: Condition 9: Invalid seating class (luxury) and valid (premium economy) ... ✓ PASS
Testing: Condition 10: Emergency row in non-economy (business) and valid (economy) ... ✓ PASS
Testing: Condition 11: Same departure/destination airport and invalid airport code ... ✓ PASS
Testing: All inputs valid - Test Data 1-4 (multiple valid combinations) ... ✓ PASS

================================================================================
Test Execution Summary:
  Total Tests: 12
  Passed:      12 ✓
  Failed:      0
  Success Rate: 100%
================================================================================
```
