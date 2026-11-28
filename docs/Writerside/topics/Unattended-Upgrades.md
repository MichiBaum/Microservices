# Automatic Security Updates (unattended-upgrades)

`unattended-upgrades` automatically downloads and installs security patches on Ubuntu 24.04 LTS so kernel, OpenSSL, and system vulnerabilities are patched promptly across all cluster nodes (`35591`, `nbs1`, `nbs2`).

## Installation

```Bash
apt update && apt install -y unattended-upgrades apt-listchanges
```

## Enable Automatic Updates

Run the interactive configuration command to enable periodic updates:

```Bash
dpkg-reconfigure -plow unattended-upgrades
```

This creates `/etc/apt/apt.conf.d/20auto-upgrades`. Verify that it contains the following settings:

```Ini
APT::Periodic::Update-Package-Lists "1";
APT::Periodic::Download-Upgradeable-Packages "1";
APT::Periodic::AutocleanInterval "7";
APT::Periodic::Unattended-Upgrade "1";
```

## Configuration (`/etc/apt/apt.conf.d/50unattended-upgrades`)

Configure `/etc/apt/apt.conf.d/50unattended-upgrades` to install security updates automatically, clean up unused kernel packages, and manage reboot behavior safely for the multi-node `k0s` cluster:

```Ini
TBD
```

> **Important for `k0s` / `etcd` Quorum**:
> Never configure all three servers (`35591`, `nbs1`, `nbs2`) to reboot at the exact same time. Either keep `Unattended-Upgrade::Automatic-Reboot "false";` and reboot nodes manually one by one, or stagger `Unattended-Upgrade::Automatic-Reboot-Time` by at least 30 minutes across the three nodes (`03:00`, `03:30`, `04:00`).

## Service & Verification Commands

```Bash
# Check that the unattended-upgrades service and APT timers are active
systemctl status unattended-upgrades
systemctl list-timers apt-daily.timer apt-daily-upgrade.timer

# Perform a dry run to verify configuration and see pending security updates
sudo unattended-upgrades --dry-run --debug

# Check if the server currently requires a reboot after an update
cat /var/run/reboot-required
cat /var/run/reboot-required.pkgs
```

## Logs

```Bash
# View unattended-upgrades execution history
cat /var/log/unattended-upgrades/unattended-upgrades.log

# View detailed dpkg output from automatic upgrades
cat /var/log/unattended-upgrades/unattended-upgrades-dpkg.log
```
