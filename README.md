# Paper Trading Service


## Design decisions

This service implements the tagless final pattern, allowing for easy switching between effect types.

Use of different effect types can be seen in `"com.papertrader.Main"`, where `IO` is passed as the type parameter as well as the unit tests where `Either[Error, *]` is passed as the type parameter.

All other design decisions are made with the simplest option being prioritized. Objects are used over Traits, and Traits over Classes wherever it is feasible.

Error handling is implemented using ADTs defined in `"com.papertrader.service.Error.scala"`.

## How to run

This service uses an external API called alpha vantage. 

To obtain the alpha vantage API key please visit https://www.alphavantage.co/.

To run this service the environment variable `ALPHA_VANTAGE_API_KEY` must be defined.

From root directory, run `sbt run`.

## How to test

From root directory, run `sbt test`.
