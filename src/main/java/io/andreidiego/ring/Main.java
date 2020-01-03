package io.andreidiego.ring;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * mvn clean compile exec:java -Dexec.mainClass="io.andreidiego.ring.Main" -Dexec.args="1000 hello"
 **/
class Main {

    public static void main(String[] args) {
        final Ring ring = new Ring(Integer.parseInt(args[0]));

        //time(args[1], message -> {
        //    try {
        //        ring.sendMessage(message);
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //    }
        //});

        time(args[1], ring::sendAsyncMessage);
    }

    private static <T> void time(T arg, Consumer<T> consumer) {
        final Instant start = Instant.now();
        System.out.println("Main - About to inkoke consumer");

        consumer.accept(arg);

        System.out.println(format("Main - Returned from consumer after %dms.", Duration.between(start, Instant.now()).toMillis()));
    }
}