# Reverse proxy for: 
- lifemanagement-backend
- lifemanagement-frontend

# Access logs
There are various ways you can use to display or parse the error/access logs. Using the cat command will display the complete access or error log file in your terminal window. For example you could use the following to display the contents of each file:

    cat /var/log/nginx/error.log
    cat /var/log/nginx/access.log

Alternatively, you could use the tail -f command to display the 10 most recent lines of the file and monitor the file for any additional changes.

    tail -f /var/log/nginx/error.log
    tail -f /var/log/nginx/access.log

Additionally, you may use an awk command to display the number of responses that returned a particular status code. For example:

    awk '{print $9}' access.log | sort | uniq -c | sort -rn

    36461 200
    483 500
    87 404
    9 400
    3 302
    1 499
    1 403
    1 301

## Access logs from host

    docker exec reverse-proxy tail -n 100 /var/log/nginx/access.log

    docker exec reverse-proxy tail -n 100 /var/log/nginx/error.log

We can then display the URLs which are returning a particular status code.

    awk '($9 ~ /302/)' access.log | awk '{print $7}' | sort | uniq -c | sort -rn

# Reload config
    docker exec reverse-proxy nginx -s reload