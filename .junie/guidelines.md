# Junie Guidelines

## Overview
This document provides guidelines for using Junie, the AI assistant in JetBrains IDEs. These guidelines help Junie understand the project context and provide more relevant assistance.

## Project Description
This is a microservices-based application written primarily in Kotlin. The project consists of multiple services including:
- Chess service
- Admin service
- Authentication service
- User management service
- Gateway service
- Alexandria service
- Fitness service
- Music service
- Registry service
- Website service
- Website ([Angular Guidelines](./guidelines-angular.md))

## Code Style and Conventions
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Write clear and concise comments
- Implement proper error handling
- Write unit tests for new functionality

## Architecture Guidelines
- Follow microservices architecture principles
- Maintain service independence
- Use REST for service communication
- Implement proper authentication and authorization
- Follow the Single Responsibility Principle

## Documentation Requirements
- Document public APIs
- Include usage examples where appropriate
- Keep documentation up-to-date with code changes
- Document complex algorithms and business logic

## Testing Strategy
- Write unit tests for business logic
- Implement integration tests for service interactions
- Maintain high test coverage
- Test edge cases and error scenarios

## Performance Considerations
- Optimize database queries
- Implement caching where appropriate
- Consider scalability in design decisions
- Monitor service performance

## Security Guidelines
- Follow OWASP security best practices
- Implement proper input validation
- Use secure communication protocols
- Handle sensitive data appropriately
- Regularly update dependencies

## Preferred Technologies
- Kotlin for backend development
- Spring framework
- Spring Boot framework
- Spring Cloud framework
- MariaDB for database
- K0S for containerization
- [Angular](./guidelines-angular.md)

## Additional Resources
- Project documentation: Documentation written with Writerside in docs directory
- API documentation: Available in the docs directory

## Specific Guidance for Junie
When assisting with this project:
- Consider the microservices architecture
- Suggest solutions that maintain service independence
- Recommend patterns consistent with existing code
- Prioritize maintainability and readability
- Consider security implications of suggested changes