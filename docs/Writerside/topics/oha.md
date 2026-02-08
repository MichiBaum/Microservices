# oha

[Github Project](https://github.com/hatoo/oha)

## Usage
Basic Example:
```bash
docker run --rm -it --network=host ghcr.io/hatoo/oha:1.13 https://chess.michibaum.ch/api/openings/starting
```

### Set number of requests
Use the `-n` flag to define the total number of requests (default is 200).

```bash
docker run --rm -it --network=host ghcr.io/hatoo/oha:1.13 -n 1000 https://chess.michibaum.ch/api/openings/starting
```

### Set number of connections
Use the `-c` flag to define the total number of connections (default is 50).

```bash
docker run --rm -it --network=host ghcr.io/hatoo/oha:1.13 -c 100 https://chess.michibaum.ch/api/openings/starting
```