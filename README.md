# WorldWanderer Flight Search Validation

## Overview
This is a Java 17 Maven project that implements flight search parameter validation for the WorldWanderer booking system. It validates 11 different conditions before allowing a flight search to proceed, ensuring all business rules are met before initializing the flight search attributes.

## Project Structure
- **Language**: Java 17
- **Build Tool**: Maven
- **Testing Framework**: JUnit Jupiter 5.10.3

### Files
- **`pom.xml`**: Maven configuration file that defines project dependencies (JUnit 5), plugins (compiler, surefire for tests, exec for running demos), and Java 17 compatibility settings
- **`src/main/java/flight/FlightSearch.java`**: Core validation class implementing the `runFlightSearch()` method with 11 business rule validations using strict date parsing and airport/class whitelists
- **`src/test/java/flight/FlightSearchTest.java`**: Comprehensive JUnit 5 test suite with 12 test methods (covering all 11 conditions plus valid scenarios) that verify both return values and attribute initialization status
- **`src/main/java/flight/FlightSearchDemo.java`**: Demo application that can run sample searches, execute tests with detailed descriptions via reflection, or both

## Validation Rules
The `runFlightSearch` method validates the following 11 conditions:

1. **Total passengers**: Must be at least 1 and cannot exceed 9
2. **Children restrictions**: Cannot be seated in emergency rows or first class
3. **Infant restrictions**: Cannot be seated in emergency rows or business class
4. **Child-to-adult ratio**: Maximum 2 children per adult
5. **Infant-to-adult ratio**: Maximum 1 infant per adult
6. **Departure date**: Cannot be in the past (based on current date)
7. **Date format**: Must be DD/MM/YYYY with strict validation (e.g., leap year checking)
8. **Return date**: Cannot be before departure date (same-day returns allowed)
9. **Seating class**: Must be one of: "economy", "premium economy", "business", "first"
10. **Emergency row**: Only available in economy class
11. **Airport codes**: Must be valid IATA codes ("syd", "mel", "lax", "cdg", "del", "pvg", "doh") and departure ≠ destination

**Important**: If validation succeeds, all class attributes are initialized. If validation fails, attributes remain uninitialized (null/default values).

## Running the Project

### Prerequisites
**Compile the project first** before running any commands:
```bash
mvn compile
```

### Execution Options

#### 1. Demo Only (shows sample search validation)
```bash
mvn exec:java
```
Runs a sample valid flight search and displays:
- Search parameters (dates, airports, passengers, class)
- Validation result (VALID ✓ or INVALID ✗)
- Initialized flight details (if valid)

#### 2. Tests Only (shows 12 test results with descriptions)
```bash
mvn test-compile exec:java -Dexec.args="--test"
```
Executes all 12 JUnit tests via reflection with detailed descriptions showing:
- Which condition is being tested
- Pass/fail status for each test
- Summary with success rate

#### 3. Demo AND Tests (shows both)
```bash
mvn test-compile exec:java -Dexec.args="--all"
```
Runs the demo first, then all tests with descriptions.

#### 4. Standard JUnit Tests (Maven test runner)
```bash
mvn test
```
Runs JUnit tests through Maven Surefire plugin with standard output format (method names and assertions).

### Other Useful Commands
- **Clean build artifacts**: `mvn clean`
- **Compile only**: `mvn compile`
- **Compile tests**: `mvn test-compile`
- **Clean and rebuild**: `mvn clean install`

## Test Coverage

### Test Structure
- **12 test methods** covering all 11 conditions + comprehensive valid scenarios
- **Each test includes 2 test data points** (except "All Valid" which has 4)
- **Every test verifies**:
  - Return value matches expected (true/false)
  - Attributes ARE initialized when validation succeeds
  - Attributes ARE NOT initialized when validation fails

### Test Method Summary
| Test Method | Condition | Test Data Coverage |
|------------|-----------|-------------------|
| `totalPassengersBoundary` | C1: Passenger count | 0 passengers, 10 passengers |
| `childSeatRestrictions` | C2: Children seating | First class, emergency row |
| `infantSeatRestrictions` | C3: Infants seating | Business class, emergency row |
| `childrenAdultRatio` | C4: Child ratio | 3 children/1 adult, 4 children/2 adults |
| `infantAdultRatio` | C5: Infant ratio | 2 infants/1 adult, 1 infant/0 adults |
| `departureNotPast` | C6: Past dates | Yesterday, tomorrow |
| `dateValidation` | C7: Date format | 29/02/2026, 31/04/2025 |
| `returnAfterDeparture` | C8: Return date | Return before departure, same-day |
| `seatingClass` | C9: Valid classes | Invalid "luxury", valid "premium economy" |
| `emergencyEconomyOnly` | C10: Emergency rows | Business with emergency, economy with emergency |
| `airportCodes` | C11: Airport codes | Same departure/destination, invalid "xyz" |
| `allValid` | All conditions | 4 different valid combinations |

### Assertion Count
- **Total assertions**: 60+ across all tests
- **Boundary testing**: Each condition tests edge cases
- **Attribute verification**: Every test checks attribute initialization state

## Architecture

### Design Patterns
- **Validation Chain**: Sequential condition checking with early returns on failure
- **Immutable Constants**: Static whitelists for airports and seating classes
- **Strict Parsing**: `DateTimeFormatter` with `ResolverStyle.STRICT` for date validation
- **Reflection-based Testing**: Demo class invokes JUnit tests dynamically for user-friendly output

### Class Responsibilities
- **`FlightSearch`**: Business logic and validation rules
- **`FlightSearchTest`**: Comprehensive test suite with boundary testing
- **`FlightSearchDemo`**: User interface for demonstrations and test execution

### Data Flow
```
User Input → runFlightSearch() → Validate 11 Conditions → 
  ├─ All Valid → Initialize Attributes → Return true
  └─ Any Invalid → Skip Initialization → Return false
```

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

Validation: Condition 1: Total passengers <1 (boundary: 0) and >9 (boundary: 10) ... ✓ TRUE
Validation: Condition 2: Children in emergency row seating or first class ... ✓ TRUE
Validation: Condition 3: Infants in emergency row seating or business class ... ✓ TRUE
Validation: Condition 4: Children >2 per adult (boundary: 3 children with 1 adult) ... ✓ TRUE
Validation: Condition 5: Infants >1 per adult (boundary: 2 infants with 1 adult) ... ✓ TRUE
Validation: Condition 6: Departure date in past (boundary: day before current) ... ✓ TRUE
Validation: Condition 7: Invalid date (29/02/2026, non-leap year) and invalid date (31/04) ... ✓ TRUE
Validation: Condition 8: Return date before departure and same-day valid ... ✓ TRUE
Validation: Condition 9: Invalid seating class (luxury) and valid (premium economy) ... ✓ TRUE
Validation: Condition 10: Emergency row in non-economy (business) and valid (economy) ... ✓ TRUE
Validation: Condition 11: Same departure/destination airport and invalid airport code ... ✓ TRUE
Validation: All inputs valid - Test Data 1-4 (multiple valid combinations) ... ✓ TRUE

================================================================================
Test Execution Summary:
  Total Tests: 12
  Passed:      12 ✓
  Failed:      0
  Success Rate: 100%
================================================================================
```

### Standard JUnit Output (`mvn test`)
```
[INFO] Running flight.FlightSearchTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.123 s - in flight.FlightSearchTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```



## Notes

### Maven Tips
- Always run `mvn compile` before executing the application
- Use `mvn clean` to remove old build artifacts
- Run `mvn test-compile` before using `--test` or `--all` flags
- The `exec:java` goal runs the main class without packaging

## Troubleshooting

### Common Issues
**Problem**: `ClassNotFoundException: flight.FlightSearchTest`  
**Solution**: Run `mvn test-compile` before using `--test` or `--all` flags

**Problem**: Tests show "ERROR: FlightSearchTest class not found"  
**Solution**: Ensure classpath includes test classes: `mvn test-compile exec:java -Dexec.classpathScope=test -Dexec.args="--test"`

**Problem**: Compilation errors  
**Solution**: Verify Java 17 is installed and JAVA_HOME is set correctly

**Problem**: Date validation failing  
**Solution**: Dates must be in DD/MM/YYYY format with strict validation (e.g., leap years)

---

**Last Updated**: October 2025  
**Author**: Assignment 4 - WorldWanderer Flight Search  
**Course**: Software Engineering / Testing Course
