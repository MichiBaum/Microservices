# Project structure

The Project is structured in the following way:
- Every Service that runs is a Maven module named ``*-service``
- Every Library without spring boot starter is named ``*-library``
- Every Library that is a spring boot starter is named ``spring-boot-starter-*``
- Run configs for IntelliJ are located under ``.run``. Everyone that opens this project in IntelliJ should get them imported automatically.
- Docs are located under ``docs``