# Development

Requirements:
- Java 23 installed
- Npm installed (lts is best)
- [Update hosts file](#update-hosts-file)



## Update hosts file
*Remember to take Backup before editing your `hosts` file, mistakes there can block your internet access or cause other network-related issues.*


### Windows
You need Administrator access to update hosts.  
In ``C:\Windows\System32\drivers\etc`` find file *hosts* and add those lines:

```
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
```

### Linux
In Linux the file is located under ``/etc/hosts``.
