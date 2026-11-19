# Fail2Ban

## Installation
```Bash
apt install fail2ban
```

## Version
```Bash
fail2ban-client version
1.0.2
```

## Configuration (`/etc/fail2ban/jail.local`)

Never edit `/etc/fail2ban/jail.conf` directly, as package updates will overwrite it. Instead, create `/etc/fail2ban/jail.local` on all three cluster nodes (`35591`, `nbs1`, `nbs2`) to override the default rules with stricter thresholds, progressive ban durations, aggressive SSH filtering, and cluster IP whitelisting.

```Ini
[DEFAULT]
# Whitelist localhost, k0s cluster nodes (35591, nbs1, nbs2)
ignoreip = 127.0.0.1/8 ::1 185.229.90.248 91.99.143.250 5.75.129.76

# Initial ban duration, detection window, and retry limit
bantime  = 2h
findtime = 1d
maxretry = 3

# Exponentially increase ban time for repeat offenders
bantime.increment = true
bantime.factor    = 2
bantime.maxtime   = 4w

# Use systemd journal on Ubuntu 24.04 and ban via UFW
backend             = auto
banaction           = iptables-multiport
banaction_allports  = iptables-allports

[sshd]
enabled  = true
port     = ssh
mode     = aggressive
maxretry = 3

[recidive]
enabled  = true
bantime  = 1w
findtime = 1d
maxretry = 3
```

### Improvements Over Default Rules

| Setting | Default (`jail.conf`) | Improved (`jail.local`) | Reason |
|---|---|---|---|
| `ignoreip` | `127.0.0.1/8 ::1` (commented out) | Cluster Node IPs + `k0s` CIDRs | Prevents `k0s` nodes (`35591`, `nbs1`, `nbs2`) and pods from accidentally banning each other. |
| `bantime` | `10m` | `1h` | Increases the initial ban from 10 minutes to 1 hour to slow down automated brute-force scanners. |
| `maxretry` | `5` | `3` | Reduces allowed failed authentication attempts before triggering a ban within `findtime` (`10m`). |
| `bantime.increment` | `false` | `true` (`factor = 2`, `maxtime = 4w`) | Doubles the ban duration on each subsequent ban for the same IP, up to a maximum of 4 weeks. |
| `backend` | `auto` | `systemd` | Reads directly from `systemd-journald` on Ubuntu 24.04 instead of polling log files. |
| `banaction` | `iptables-multiport` | `ufw` | Integrates bans directly with the host `UFW` firewall rules (use `iptables-multiport` if `UFW` is not enabled). |
| `[sshd] mode` | `normal` | `aggressive` | Detects additional SSH attack patterns, including key-exchange floods, pre-auth disconnects, and DDoS probes. |
| `[recidive]` | Disabled | `enabled = true` (`bantime = 1w`) | Monitors Fail2Ban's own log and bans repeat offenders across all jails for 1 week after 3 bans in 24 hours. |

### Validate and Apply Configuration
```Bash
# Test configuration syntax before restarting
fail2ban-client -t

# Restart Fail2Ban to apply /etc/fail2ban/jail.local
systemctl restart fail2ban

# Reload configuration without dropping active bans
fail2ban-client reload
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
# Show detailed status for a specific jail, example: fail2ban-client status sshd
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