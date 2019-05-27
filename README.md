# parking-application
Toll Parking Library Prototype

Java API that meets the following requirements:

A toll parking contains multiple parking slots of different types:

1) The standard parking slots for sedan cars (gasoline-powered)
2) Parking slots with 20kw power supply for electric cars
3) Parking slots with 50kw power supply for electric cars

20kw electric cars cannot use 50kw power supplies and vice-versa.
Every Parking Lot is free to implement is own pricing policy:

1) Some only bill their customers for each hour spent in the parking (nb hours * hour price)
2) Some others bill a fixed amount + each hour spent in the parking (fixed amount + nb hours * hour price)

In the future, there will be other pricing policies.

Cars of all types come in and out randomly, the API must:

1) Send them to the right parking slot of refuse them if there is no slot (of the right type) left.
2) Mark the parking slot as Free when the car leaves it.
3) Bill the customer when the car leaves.

There is main class com.parking.ParkingApp.java for testing purpose (and a more detailed JUnit test com.parking.core.ParkingServiceTest that explore all possible use cases) but it is highly recommended to read [Parking Library Documentation.pdf](https://github.com/enrico77/parking-application/blob/master/Parking%20Library%20Documentation.pdf) for more more details, with architecture, installation instructions, API documentation.
