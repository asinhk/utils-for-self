### Problem Statement
A person has been investing in Indian Debt mutual funds for years. Now, if he wants to withdraw an amount 'X', he will need to pay capital gains tax.
The objective here is to get a feel of the amount of capital gains made for each fund.
For a given fund, there may have been a number of buy/sell transactions over the years.
So, when a "Sell" transaction is done, units are redeemed on FIFO basis.

### About this service/calculator
In this calculator, we need the following:
1. List of Securities/Funds
2. Current price/selling price of the securities
3. All the transactions

For my personal use, I download all my transactions data as an excel file from www.valueresearchonline.com.
This file can be uploaded to our service which will use H2 database to populate a transactions table. All H2 tables reside in a local folder for future use.



### Usefule Links
Once spring boot application is started from class CapitalGainsApp.java, use the following for using this calculator:
* [Swagger](http://localhost:8080/swagger-ui/index.htm)
* [h2 console](http://localhost:8080/h2-console)
* [Actuator](http://localhost:8080/actuator)

