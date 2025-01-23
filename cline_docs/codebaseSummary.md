# Codebase Summary

## Key Components and Their Interactions

### Controllers
- `Controller.java`: 
  * Implements `/api/user-request` POST endpoint
  * Orchestrates the workflow:
    - File parsing
    - JSON data loading
    - LLM processing (TODO)
    - Template manipulation (TODO)
    - Document generation

### Services
1. Document Processing
   - `DocumentService.java`: 
     * Basic template loading from resources
     * Document saving functionality
     * Needs implementation:
       - Template content manipulation
       - Placeholder replacement
       - Section updates

2. File Processing
   - `FileParserService.java`:
     * Basic text file content extraction
     * Needs implementation:
       - User intent parsing logic
       - Intent validation
       - Data extraction

3. Data Processing
   - `JsonParserService.java`: Handles JSON data loading
   - `LlmService.java`: Interface for LLM integration
   - `LlmServiceImpl.java`: Implementation needed for:
     * Intent processing
     * Content generation
     * Recommendation formatting

### Models
1. Client Data
   - `ClientData.java`: Model for client information
   - Represents personal and financial details

2. Organization Data
   - `OrganizationData.java`: Model for organization information
   - Contains platform and portfolio details

### Configuration
- `AppConfig.java`: Application configuration setup

## Data Flow
1. User Intent Processing
   - User intent file received via REST endpoint (/api/user-request)
   - File content extracted by FileParserService
   - TODO: Intent parsing and validation
   - TODO: Data extraction from intent

2. Data Integration
   - Client data loaded from client.json via JsonParserService
   - Organization data loaded from org.json via JsonParserService
   - TODO: Data mapping to template fields
   - TODO: Validation of required data

3. Document Generation
   - Template loaded from resources using DocumentService
   - TODO: Content generation via LLM service
   - TODO: Template population with:
     * Client details
     * Current provider information
     * Recommended provider details
     * Financial overview
   - Document saved as recommendation.docx

## External Dependencies
- Spring Boot Framework
- Apache POI for document processing
- JSON processing libraries
- Testing frameworks (JUnit, Spring Test)

## Resource Structure
- `/templates`: Document templates
- `/clientinfo`: Client data files
- `/orginfo`: Organization data files
- `/userintent`: User intent examples

## Recent Changes
- Initial project setup
- Basic service implementations
- Model class definitions
- Resource file organization

## Development Focus
Current focus is on implementing:
1. Template Processing
   - POI integration for content manipulation
   - Placeholder definition and replacement
   - Table data population
   - Section content updates

2. Intent Processing
   - User intent parsing logic
   - Data extraction from intent text
   - Validation of intent format
   - Error handling for invalid intents

3. Content Generation
   - LLM service implementation
   - Recommendation text generation
   - Dynamic content based on:
     * User intent
     * Client data
     * Organization data
   - Content formatting and structure

4. Data Integration
   - Client data mapping
   - Organization data processing
   - Template field mapping
   - Data validation framework
