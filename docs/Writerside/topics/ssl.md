# SSL

An SSL certificate is needed for https.
This SSL certificate is created with zerossl, acme.sh and Lego.

## Create certificates
Because of wildcard certificate you need to verify ownership of the Domain. This is done with adding a TXT record to the DNS entry.

## Install acme.sh
```Bash
curl https://get.acme.sh | sh -s email=YOUR_EMAIL@GMAIL.COM
acme.sh --register-account -m YOUR_EMAIL@GMAIL.COM --server zerossl
```

## Install Lego
```Bash
curl -s https://api.github.com/repos/go-acme/lego/releases/latest | grep "browser_download_url.*linux_amd64.tar.gz" | cut -d '"' -f 4 | wget -qi -
tar -xzf lego_*_linux_amd64.tar.gz
```

Copy Config files from ssl

## Run Lego

```Bash
./lego --config .lego.yml
```

