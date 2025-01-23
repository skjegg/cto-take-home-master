# Technology Stack

## Core Framework
- Java (Spring Boot)
- Gradle for build management

## Document Processing
- Apache POI
  - Used for reading and writing Microsoft Word documents (.docx)
  - Dynamic template processing and manipulation
  - Flexible content insertion and formatting
  - Table and section management
  - Style preservation

## Data Management
- JSON for data storage and configuration
  - Extensible client data structure (client.json)
  - Multi-organization support (org.json)
  - Support for multiple data formats
- Custom parsing services
  - Generic FileParserService for intent processing
  - Flexible JsonParserService for data handling
  - Natural language processing utilities

## REST API
- Spring Web for REST endpoint implementation
- File upload handling
- Response management

## Business Logic
- Service layer for business operations
  - Enhanced DocumentService for flexible processing
  - LLM Service for intelligent content generation
  - Intent analysis and matching system
  - Risk profile assessment
  - Portfolio matching logic
  - Fee structure analysis

## Testing
- JUnit for unit testing
- Spring Test framework for integration testing
- Test files for template parsing validation

## Project Structure
- Enhanced MVC architecture
  - Generic REST controllers
  - Flexible service layer
  - Extensible data models
- Utility services
  - Natural language processing
  - Provider matching system
  - Document generation
  - Template management
  - Validation framework

## Configuration
- Spring application.properties
- Logback for logging configuration
- Custom configuration classes (AppConfig)

## Resource Management
- Dynamic resource organization
  - Versioned template management
  - Multi-client data storage
  - Organization profiles
  - Intent examples and patterns
  - Configuration management
  - Error templates
