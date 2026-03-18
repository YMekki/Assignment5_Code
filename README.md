# Assignment 5 - Software Testing (SE 333)

![Build Status](https://github.com/YMekki/Assignment5_Code/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview

This project contains an Amazon shopping cart system with pricing rules including regular cost calculation, delivery pricing tiers, and an electronics surcharge. The codebase is tested through both integration tests (using a real HSQLDB in-memory database) and unit tests (using Mockito for isolation).

## Test Summary

**Integration Tests** (`AmazonIntegrationTest.java`): Test the full flow of adding items to the database-backed shopping cart and calculating totals with all pricing rules wired together. The database is reset before each test using `@BeforeEach`.

**Unit Tests** (`AmazonUnitTest.java`): Test individual classes in isolation using Mockito mocks for the `ShoppingCart` interface and `PriceRule` dependencies. Covers boundary conditions for delivery pricing, electronics surcharge logic, and regular cost calculation.

Both test files include `@DisplayName("specification-based")` and `@DisplayName("structural-based")` tests.

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/SE333_CI.yml`) runs on every push to `main` and performs:

1. **Checkstyle** static analysis during the validate phase (configured to not fail the build)
2. **JUnit 5** test execution
3. **JaCoCo** code coverage report generation
4. **Artifact uploads** for both Checkstyle and JaCoCo reports

## How to Run Locally

```bash
mvn clean test
```

## Workflow Artifacts

- `checkstyle-report` - Checkstyle XML report
- `jacoco-report` - JaCoCo XML coverage report