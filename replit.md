# WorldWanderer Flight Search Validation

## Project Overview
This is a Java 17 Maven project that implements flight search parameter validation for the WorldWanderer booking system. It validates 11 different conditions before allowing a flight search to proceed.

## Recent Changes
- **October 15, 2025**: Project imported and set up in Replit environment
  - Installed Java GraalVM 22.3 toolchain with Maven
  - Successfully compiled the project
  - Configured workflow to run the flight search demo
  - All 12 tests passing (100% success rate)

## Project Architecture

### Technology Stack
- **Language**: Java 17
- **Build Tool**: Maven
- **Testing Framework**: JUnit Jupiter 5.10.3

### Project Structure
```
src/
├── main/java/flight/
│   ├── FlightSearch.java       # Core validation logic
│   └── FlightSearchDemo.java   # Demo application with test runner
└── test/java/flight/
    └── FlightSearchTest.java   # JUnit 5 test suite (12 tests)
```

### Key Components
- **FlightSearch.java**: Main validation class with 11 business rules
  - Validates passenger counts, dates, airports, and seating restrictions
  - Uses strict date parsing with Java's DateTimeFormatter
  - Immutable whitelists for airports and seating classes
  
- **FlightSearchDemo.java**: Interactive demonstration
  - Runs demo with sample data (default mode)
  - Can run tests with detailed descriptions (--test flag)
  - Supports both demo and tests together (--all flag)
  
- **FlightSearchTest.java**: Comprehensive test suite
  - 12 test methods covering all validation rules
  - 26 total assertions with boundary testing
  - 100% test coverage

## Validation Rules
The system validates 11 conditions:
1. Total passengers between 1-9
2. No children in emergency rows or first class
3. No infants in emergency rows or business class
4. Maximum 2 children per adult
5. Maximum 1 infant per adult
6. Departure date not in the past
7. Valid DD/MM/YYYY date format with strict leap year checking
8. Return date after departure date
9. Valid seating class (economy/premium economy/business/first)
10. Emergency row only in economy class
11. Valid and distinct IATA airport codes

## Running the Project

### Commands
- **Demo Only**: `mvn exec:java`
- **Tests Only**: `mvn test-compile exec:java -Dexec.args="--test"`
- **Both Demo and Tests**: `mvn test-compile exec:java -Dexec.args="--all"`
- **Standard JUnit Tests**: `mvn test`
- **Build**: `mvn compile`
- **Clean and Rebuild**: `mvn clean install`

### Configured Workflows
- **Flight Search Demo**: Runs the demo application showing sample flight validation
- **Tests**: Runs the JUnit test suite

## Development Notes
- The project uses Maven for dependency management and build automation
- Java 17 is required for compilation
- All dependencies are managed through the pom.xml file
- The exec-maven-plugin is configured to run the demo class by default
