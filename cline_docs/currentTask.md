# Current Task

## Objective
Implement a document automation system that processes user intent for investment transfers and generates personalized recommendations.

## Context
From analyzing the example user intent and data files:
- User intent varies significantly in format and risk appetite:
  * Standard transfers: "transfer from provider A to provider B"
  * Risk-based requests: "transfer to suitable risk profile"
  * Aggressive strategies: "go all in on crypto"
- Client data includes detailed financial profile and pension plans
- Organization data provides:
  * Multiple platform options with different features
  * Range of portfolios from cautious to ultra-high risk (YOLO)
  * Detailed fee structures
- System integrates with Claude LLM for intelligent intent processing
- Apache POI used for document generation

## Current Focus
1. LLM Integration and Intent Processing
   - Claude Integration:
     * Intent analysis and interpretation
     * Risk assessment from natural language
     * Recommendation generation
     * Compliance checking
   - Template Structure:
     * Client information mapping
     * Provider details formatting
     * Risk-appropriate recommendations
     * Regulatory warnings when needed

2. Document Processing Implementation
   - Template Analysis:
     * Dynamic section handling
     * Risk-appropriate content
     * Compliance warnings
   - POI Integration:
     * Flexible content insertion
     * Format preservation
     * Table updates

3. Intelligent Data Processing
   - Intent Analysis:
     * LLM-based text understanding
     * Risk preference extraction
     * Provider identification
     * Compliance flag detection
   - Client Risk Assessment:
     * Current portfolio analysis
     * Risk tolerance evaluation
     * Investment horizon consideration
     * Suitability checking
   - Portfolio Matching:
     * Risk-appropriate options
     * Fee optimization
     * Platform feature matching
     * Regulatory compliance

## Related Tasks from Project Roadmap
- REST Endpoint Implementation
- Template Processing with Apache POI
- Data Integration (Client and Organization)
- Use the LLM provider to further enhance intent and to create recommendations

## Next Steps
1. Implement LLM Integration
   - Set up Claude service connection
   - Design prompt engineering for intent analysis
   - Create risk assessment logic
   - Implement recommendation generation

2. Enhance Intent Processing
   - Integrate LLM with FileParserService
   - Add risk level detection
   - Implement compliance checking
   - Create validation framework

3. Design Template Processing
   - Define dynamic placeholder system
   - Create flexible data mapping framework
   - Support multiple recommendation formats
   - Handle various investment scenarios

4. Develop Data Integration
   - Create flexible client data extraction
   - Implement multi-organization support
   - Build comprehensive validation framework
   - Add risk profile matching logic

## Technical Considerations
- Generic Intent Processing:
  * Natural language understanding using LLM provider
  * Entity recognition (providers, platforms)
  * Investment preference interpretation
  * Support for various phrasings

- Flexible Data Processing:
  * Multi-provider support
  * Platform-agnostic processing
  * Risk profile matching
  * Fee structure comparison

- Dynamic Content Generation:
  * Context-aware recommendations
  * Risk-appropriate suggestions
  * Provider-specific details
  * Customizable formatting

- Robust Error Handling:
  * Unknown provider handling
  * Ambiguous intent resolution
  * Missing data management
  * Validation across providers

## Testing Requirements
Current test coverage is minimal:
- Basic template loading test (TemplateParserTest)
- Spring context loading test (ApplicationTest)

Additional tests needed:
1. Document Processing Tests
   - Template manipulation
   - Placeholder replacement
   - Content formatting
   - Document generation

2. Data Processing Tests
   - User intent parsing
   - JSON data loading
   - Data validation
   - Error handling

3. Integration Tests
   - End-to-end workflow
   - REST endpoint functionality
   - File upload handling
   - Document generation process

4. Unit Tests
   - Individual service methods
   - Data transformation logic
   - Utility functions
   - Error scenarios
