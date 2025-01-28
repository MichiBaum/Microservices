# Development

Requirements:
- Java 21 installed
- Npm installed (lts is best)
- [Update hosts file](#update-hosts-file)
- Local dev database



## Update hosts file
*Remember to take Backup before editing your `hosts` file, mistakes there can block your internet access or cause other network-related issues.*


### Windows
You need Administrator access to update hosts.  
In ``C:\Windows\System32\drivers\etc`` find file *hosts* and add those lines:

```
# Added Manually
# Microservices
127.0.0.1 chess.michibaum.ch
127.0.0.1 gateway.michibaum.ch
127.0.0.1 registry.michibaum.ch
127.0.0.1 admin.michibaum.ch
127.0.0.1 usermanagement.michibaum.ch
127.0.0.1 authentication.michibaum.ch
127.0.0.1 fitness.michibaum.ch
127.0.0.1 music.michibaum.ch
127.0.0.1 michibaum.ch

127.0.0.1 chess.michibaum.com
127.0.0.1 gateway.michibaum.com
127.0.0.1 registry.michibaum.com
127.0.0.1 admin.michibaum.com
127.0.0.1 usermanagement.michibaum.com
127.0.0.1 authentication.michibaum.com
127.0.0.1 fitness.michibaum.com
127.0.0.1 music.michibaum.com
127.0.0.1 michibaum.com

127.0.0.1 chess.michibaum.eu
127.0.0.1 gateway.michibaum.eu
127.0.0.1 registry.michibaum.eu
127.0.0.1 admin.michibaum.eu
127.0.0.1 usermanagement.michibaum.eu
127.0.0.1 authentication.michibaum.eu
127.0.0.1 fitness.michibaum.eu
127.0.0.1 music.michibaum.eu
127.0.0.1 michibaum.eu
```

### Linux
In Linux the file is located under ``/etc/hosts``.