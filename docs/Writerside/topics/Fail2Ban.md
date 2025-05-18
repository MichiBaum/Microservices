# Fail2Ban

## Version
```Bash
fail2ban-client version
0.11.2
```

## Service Commands
```Bash
# Start Fail2Ban
systemctl start fail2ban

# Stop Fail2Ban
systemctl stop fail2ban

# Restart Fail2Ban (recommended after config changes)
systemctl restart fail2ban

# Check status of the Fail2Ban service
systemctl status fail2ban
```

## Status
```Bash
# Show overall status (enabled jails, etc.)
fail2ban-client status
# Show detailed status for a specific jail
fail2ban-client status <jail-name>
```

## Ban and Unban
```Bash
# Ban an IP manually in a specific jail
fail2ban-client set <jail-name> banip <IP-address>

# Unban an IP manually
fail2ban-client set <jail-name> unbanip <IP-address>

# List currently banned IPs in a jail
fail2ban-client get <jail-name> banned
```