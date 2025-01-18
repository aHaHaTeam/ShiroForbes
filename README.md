# ShiroForbes

ShiroForbes is a website for the math club at Physics-Mathematical Lyceum 239. It provides a platform for teachers to
view and publish student ratings, while students can access their profiles to track their progress. The site is designed
to help students and teachers monitor performance separately in various math topics, including algebra, number theory,
combinatorics, and geometry. The website supports two groups of students with separate ratings.

Currently, as it is Season 2, all development is ongoing in
the [season2](https://github.com/aHaHaTeam/ShiroForbes/tree/season2) branch. The main branch stores code that was
relevant for the summer camp. It has an outdated database schema and non-optimized queries.

## Features

- **Student Profiles**: Each student has a profile page displaying:
    - Overall rating
    - Number of solved problems
    - Number of solved **hard** problems *(aka grobs)*
    - Percentage of solved problems
    - The number and percentage of problems solved per topic (algebra, number theory, combinatorics, geometry)
- **Rating Page**: Users can view student ratings.
- **Fictitious Currency System**: Teachers can award currency to students for various activities, such as
  morning/evening routines, table tennis tournaments, or different quizzes.
- **Rating System**: Ratings and statistics are fetched from a Google Spreadsheet, where solved problems are recorded.
- **Authentication**: User authentication is implemented using Ktor, with passwords hashed using SHA-256.

## Tech Stack

- **Backend**: Written in Kotlin using the [Ktor](https://github.com/ktorio/ktor) framework.
- **Database**: PostgreSQL for storing user and profile data. Queries are handled
  using [Exposed](https://github.com/JetBrains/Exposed).
- **Authentication**: [Ktor](https://github.com/ktorio/ktor) extension with SHA-256 password hashing.
- **Data Integration**: Rating and statistics data are fetched from Google Spreadsheets using the official Google
  library.
- **Markdown to HTML Conversion**: The editor for news on the main page generates HTML code from markdown
  using the [commonmark](https://github.com/commonmark/commonmark-java) library.
- **Config**: [Hoplite](https://github.com/sksamuel/hoplite) library is used as a configuration loader.
- **Frontend**: [Thymeleaf](https://www.thymeleaf.org/) as the server-side template engine, with some vanilla JavaScript
  for
  client-side functionality, and the Material Design CSS component
  library [Beer CSS](https://github.com/beercss/beercss).
