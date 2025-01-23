# Codebase Summary

## Key Components and Their Interactions

### Controllers
- `Controller.java`: 
  * Implements `/api/user-request` POST endpoint
  * Orchestrates the workflow:
    - File parsing with validation
    - JSON data loading
    - LLM-based intent processing
    - Template manipulation
    - Document generation
    - Error handling

### Services
1. Document Processing
   - `DocumentService.java`: 
     * Template loading and management
     * Document saving functionality
     * Recommendation insertion at marked location
     * Format preservation
     * Section content management

2. File Processing
   - `FileParserService.java`:
     * Content type validation
     * Text file content extraction
     * Error handling for invalid files
     * UTF-8 encoding support

3. Data Processing
   - `JsonParserService.java`: Handles JSON data loading
   - `LlmService.java`: Interface for LLM integration
   - `LlmServiceImpl.java`: Claude integration for:
     * Intent analysis
     * Content generation
     * Risk-appropriate recommendations
     * Content cleaning

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
   - User intent file received and validated
   - Content extracted and checked for validity
   - Intent analyzed using LLM service
   - Risk assessment performed

2. Data Integration
   - Client data loaded and validated
   - Organization data processed
   - Risk profile matching
   - Portfolio selection based on intent

3. Document Generation
   - Template loaded from resources
   - Recommendation generated via LLM
   - Content cleaned of unwanted elements
   - Template populated with:
     * Client details
     * Current provider information
     * Recommended provider details
     * Risk warnings when needed
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
- Enhanced intent processing with LLM integration
- Improved error handling and validation
- Added comprehensive test coverage
- Implemented recommendation cleaning
- Added support for various intent formats
- Enhanced documentation structure

## Development Focus
Completed Implementation:
1. Template Processing
   - POI integration for document manipulation
   - Dynamic recommendation insertion
   - Format preservation
   - Section content management

2. Intent Processing
   - LLM-based intent analysis
   - Support for multiple intent formats
   - Risk-appropriate recommendations
   - Content cleaning and validation

3. Content Generation
   - Integrated Claude LLM service
   - Context-aware recommendations
   - Risk warnings for high-risk requests
   - Clean, focused content generation

4. Data Integration
   - Client profile integration
   - Organization data processing
   - Multi-provider support
   - Risk profile matching

Future Enhancements:
1. Machine learning improvements
   - Enhanced intent understanding
   - Better risk profiling
   - Pattern recognition

2. Template Management
   - Version control
   - Multiple template support
   - Dynamic section handling

3. Performance Optimization
   - Caching strategies
   - Response time improvements
   - Resource management
