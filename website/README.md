# Website

## Update Angular

```
ng version
ng update
```

## Update packages

```
npm outdated
npm update
```

## Potential Issues

```
ng lint
```

## Compodoc

```
npm run compodoc:build-and-serve
```

## Bundle report
bundle-report is a script defined in package.json. This script requires needs source-map-explorer. Install it with:

```
npm install -g source-map-explorer
```

After that you can execute following and get a bundle report.
```
npm run bundle-report
```
