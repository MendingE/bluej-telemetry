
# BlueJ Telemetry

**Group 12 – Object-Oriented Programming II (COMP3607)**  
University of the West Indies, St. Augustine

## Project Overview

BlueJ Telemetry is a BlueJ extension designed to collect and analyse student coding activity during programming sessions.

The system aims to monitor coding activity, compilation attempts, errors, program executions and class development progress. It will generate structured JSON telemetry data and PDF reports to help analyse students' programming sessions.

## Technologies

- Java 21
- BlueJ 6.0.1
- Maven 3.9+
- JUnit 5
- Jackson (JSON processing)
- OpenPDF (PDF generation)
- Git and GitHub

## Prerequisites

Before building the project, install:

1. JDK 21
2. BlueJ 6.0.1
3. Maven 3.9 or later
4. Git

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/MendingE/bluej-telemetry.git
cd bluej-telemetry
```

### 2. Configure the BlueJ API dependency

The BlueJ API JAR is not included in this repository.

Each developer must install it into their own local Maven repository.

On Windows, run the following command in PowerShell:

```powershell
mvn install:install-file "-Dfile=C:\Program Files\BlueJ\lib\bluej.jar" "-DgroupId=org.bluej" "-DartifactId=bluej" "-Dversion=6.0.1" "-Dpackaging=jar"
```

If BlueJ is installed in a different location, adjust the file path accordingly.

### 3. Configure Java

Ensure Maven uses JDK 21.

Check your Java version:

```bash
java -version
javac -version
mvn --version
```

### 4. Build the project

Run:

```bash
mvn clean test
```

This compiles the project and runs the available automated tests.

## Project Structure

```text
src/
├── main/
│   ├── java/comp3607/telemetry/
│   │   ├── dataCollection/
│   │   ├── events/
│   │   ├── export/
│   │   ├── patterns/
│   │   ├── plugin/
│   │   └── reporting/
│   │ 
│   └── resources/
└── test/
    └── java/comp3607/telemetry/unitTests
        
```

## Team Development

Each team member should work on a separate feature branch.

Changes should be pushed to GitHub and merged into `main` through pull requests.

## Design Patterns

The project will incorporate the Observer, Strategy and Adapter design patterns.

Their implementation details will be documented as development progresses.
