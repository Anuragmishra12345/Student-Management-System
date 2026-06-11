# Student-Management-System

Spring Boot + MySQL backend for managing relational student data with secure CRUD REST APIs.

## Features
- Spring Boot 3 REST API for `Student` entity CRUD operations
- MySQL integration using Spring Data JPA
- Input validation and centralized exception handling for safer API behavior
- Indexed and unique `email` column for fast/consistent lookups
- Postman collection for API testing and validation

## Tech Stack
- Java 17
- Spring Boot 3.3.2
- Spring Data JPA
- MySQL
- Maven
- JUnit + MockMvc

## API Endpoints
Base URL: `http://localhost:8080/api/students`

- `POST /` — create student
- `GET /` — list all students
- `GET /{id}` — fetch student by id
- `PUT /{id}` — update student
- `DELETE /{id}` — delete student

### Sample Request Body
```json
{
  "name": "Anurag Mishra",
  "email": "anurag@example.com",
  "course": "Computer Science",
  "age": 20
}
```

## Configuration
Set environment variables (optional defaults are provided in `application.properties`):
- `DB_URL` (default: `jdbc:mysql://localhost:3306/student_management`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: empty)

## Run
```bash
mvn spring-boot:run
```

## Test
```bash
mvn test
```

## Postman Validation
Import:
- `postman/Student-Management-System.postman_collection.json`

Collection includes create, read, update, and delete requests against `{{baseUrl}}`.
