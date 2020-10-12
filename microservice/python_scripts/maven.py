import os

stream = os.popen('mvnw spring-boot:build-image')
output = stream.read()
print(output)

# TODO build like docker.py
