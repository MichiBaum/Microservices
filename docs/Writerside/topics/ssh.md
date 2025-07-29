# SSH

Add SSH Key to `/root/.ssh/authorized_keys`

Windows ssh-agent
1. Start powershell as Administrator
2. `Get-Service ssh-agent`
3. `Set-Service ssh-agent -StartupType Automatic`
4. `Start-Service ssh-agent`
5. `ssh-add $env:USERPROFILE\.ssh\nbs2_root`
6. `ssh-add $env:USERPROFILE\.ssh\35591_root`
7. `ssh-add $env:USERPROFILE\.ssh\nbs1_root`
8. 
