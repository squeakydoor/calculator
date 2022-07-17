Need to find a best route for your trip? Look no further.

1 - Clone this repo.\
2 - Create your json file in the form:
````json
{
  "trafficMeasurements":
  [
    {
      "measurementTime":86544,
      "measurements": [
        {"startAvenue":"A","startStreet":"1","transitTime":0,"endAvenue":"B","endStreet":"1"},
        {"startAvenue":"B","startStreet":"1","transitTime":10,"endAvenue":"C","endStreet":"1"}
        ...
      ]
    }
    ...
  ]
}

````
3 - Run the program with 5 arguments: < path to json > < start avenue > < start street > < end avenue > < end street >

Example: ```sbt run sample-data.json A 1 B 1```