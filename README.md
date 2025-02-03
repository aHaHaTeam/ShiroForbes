# ShiroForbes

ShiroForbes is a website for the math club at Physics-Mathematical Lyceum 239. It provides a platform for teachers to
view and publish student ratings, while students can access their profiles to track their progress. The site is designed
to help students and teachers monitor performance separately in various math topics, including algebra, number theory,
combinatorics, and geometry. The website supports two groups of students with separate ratings.

## Features

- **Student Profiles**: Each student has a profile page displaying:
    - Overall rating
    - Number of solved problems
    - Number of solved **hard** problems *(aka grobs)*
    - Percentage of solved problems
    - The number and percentage of problems solved per topic (algebra, number theory, combinatorics, geometry)
- **Teacher Dashboard**: Teachers can view and publish student ratings.
- **Rating System**: Ratings and statistics are fetched from a Google Spreadsheet, where solved problems are recorded.
- **Secure Authentication**: User authentication is implemented using Ktor, with passwords hashed using SHA-256.

## Tech Stack

- **Backend**: Written in Kotlin using the [Ktor](https://github.com/ktorio/ktor) framework.
- **Database**: PostgreSQL for storing user and profile data. For
  queries [Exposed](https://github.com/JetBrains/Exposed) is used.
- **Authentication**: [Ktor](https://github.com/ktorio/ktor) extension with SHA-256 password hashing.
- **Data Integration**: Rating and statistics data are fetched from Google Spreadsheets using the official Google
  library.
- **Config**: [Hoplite](https://github.com/sksamuel/hoplite) library as a config loader.
- **Frontend**: [Thymeleaf](https://www.thymeleaf.org/) as the server-side template engine, some vanilla JavaScript for
  client-side functionality, and material design css component library [Beer CSS](https://github.com/beercss/beercss).

## Getting Started

### Prerequisites

- An installed Java runtime environment
- A PostgreSQL database
- Google Sheets API service account

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/aHaHaTeam/ShiroForbes.git
   ```
2. Add a configuration file `/src/main/resources/config.yaml` with content relevant to you, formatted as shown in the
   `/src/main/resources/config.example.yaml` file. Copy your Google service key from the Google Cloud Console and insert
   it to the `/src/main/resources/googlesheets/service-account-key.json` file (or other file you specified in the config
   file).
3. Build jar file with gradlew:
   ```bash
   ./gradlew buildFatJar
   ```
4. Initialize database running:
   ```bash
   java -jar build/libs/shiroforbes-all.jar --init
   ```
5. Run the application:
   ```bash
   java -jar build/libs/shiroforbes-all.jar --port=8080 > ./log 2>&1 &
   ```
6. Now the application is running on port 8080, you can access it at [http://localhost:8080](http://localhost:8080).
