# 🌍 WorldWanderer Assignment 4: Flight Search Validation

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.3-green.svg)](https://junit.org/junit5/)

This repository contains the implementation and unit tests for **Activity 1** of Assignment 4 (Git and Unit Testing) for the **WorldWanderer** flight booking system.  
It implements the `runFlightSearch` method in the `FlightSearch` class, validating flight search parameters against **11 specified conditions** (e.g., passenger limits, date formats, airport codes).

---

## 🧠 Overview

The project ensures that all flight search inputs meet strict validation rules before a search is executed.  
If all inputs are valid, private fields are initialized and the method returns `true`.  
Otherwise, it returns `false` without changing any fields.

- **Language:** Java 17  
- **Testing:** JUnit Jupiter 5
# 🌍 WorldWanderer Assignment 4 – Flight Search Validation

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.3-green.svg)](https://junit.org/junit5/)

Implements and tests the **`runFlightSearch`** method for the WorldWanderer flight booking system.  
Validates inputs against **11 rules** (passenger limits, date formats, airport codes, etc.) and runs **26 JUnit 5 boundary tests** — all pass.

---

## ✈️ Overview
- Returns `true` and initializes private fields only if all 11 conditions are valid; otherwise returns `false`.  
- Single merged file: **`FlightSearch.java`** (implementation + tests).  
- Current date fixed at **08/10/2025** for reproducible results.  
- Tested using **Java 17** and **JUnit Jupiter 5.10.3**.

---

## ✅ Validation Rules
1. Total passengers 1–9  
2. No children in emergency rows or first class  
3. No infants in emergency rows or business class  
4. ≤2 children per adult  
5. ≤1 infant per adult  
6. Departure not in the past  
7. Valid `DD/MM/YYYY` date (checks leap years)  
8. Return date after departure  
9. Class = economy / premium economy / business / first  
10. Emergency row only in economy  
11. Different, valid IATA airports  

---

## 🧪 Usage
```java
FlightSearch fs = new FlightSearch();
boolean valid = fs.runFlightSearch(
  "08/10/2025", "syd", false, "15/10/2025", "mel", "economy", 1, 0, 0);
System.out.println(valid); // true
