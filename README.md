# Coinbase-crypto

## Prerequisite
- Java 11
- Maven 3.5.3+
- lombok (https://projectlombok.org/ Remember to restart your IDE if you're installing lombok into your IDE)

## How to run
You can either start the application through your IDE or run ```mvn package``` and run it as a jar with instrument(s) args Eg. ```java -java target/crypto-data-0.0.1-SNAPSHOT.jar BTC-USD```



Example response
```
======================== BTC-USD ========================
_________________________________________________________
| Ask Quantity| Ask Price| Level| Bid price| Bid Quantity|
|========================================================|
| 5.60581460  | 36290.01 | 1    | 36290.00 | 1.62532998  |
| 0.02610000  | 36290.02 | 2    | 36287.99 | 0.00239103  |
| 0.29000000  | 36291.55 | 3    | 36287.65 | 0.05574881  |
| 1.89930160  | 36291.65 | 4    | 36287.00 | 0.69444445  |
| 0.13781704  | 36291.66 | 5    | 36283.59 | 0.10130000  |
| 0.44700000  | 36293.30 | 6    | 36283.44 | 0.10130000  |
| 0.05593740  | 36293.93 | 7    | 36282.50 | 0.10130000  |
| 0.13781109  | 36294.07 | 8    | 36280.16 | 0.48550437  |
| 0.27300000  | 36294.60 | 9    | 36277.25 | 0.27400000  |
| 0.05000000  | 36295.49 | 10   | 36275.43 | 0.13782975  |
```
