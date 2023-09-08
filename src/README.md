### Remote kvs

This was supposed to be a redis clone, but I could not be bothered.
The purpose was just to try out async io, and the purpose was fulfilled.


### Usage

Run the main class:
```
mvn compile exec:java -Dexec.mainClass="org.vguidi.redis.Main"
```

Connect to the server: 

```
netcat localhost 8081
GET $KEY $VAL
```

Only strings supported

### Example usage
```
vguidi@Pato:~/Desktop/RedisJava/Redis$ netcat localhost 8081
SET 4 5
OK
GET 4
5
GET 3
Key not found
LOL 123
unrecognized command
```