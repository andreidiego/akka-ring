package io.andreidiego.ring;

import java.time.Duration;
import java.time.Instant;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static java.lang.String.format;

class RingManagerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorSystem actorSystem;
    private final ActorRef firstRingMember;
    private Instant start;

    static Props props(int ringSize, final ActorSystem actorSystem) {
        return Props.create(RingManagerActor.class, ringSize, actorSystem);
    }

    static class InitialMessage {
        private final String message;

        InitialMessage(final String message) {
            this.message = message;
        }
    }

    private RingManagerActor(int ringSize, final ActorSystem actorSystem) {
        log.debug("Building the ring.");

        this.actorSystem = actorSystem;
        ActorRef nextRingMember = context().actorOf(RingMemberActor.props(self()), String.valueOf(ringSize));

        for (int i = 1; i < ringSize; i++) {
            nextRingMember = context().actorOf(RingMemberActor.props(nextRingMember), String.valueOf(ringSize - i));
        }

        log.debug("Ring is in place.");

        firstRingMember = nextRingMember;
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(InitialMessage.class, msg -> {
                    start = Instant.now();

                    log.debug("Initial message received.");

                    firstRingMember.tell(msg.message, self());
                })
                .match(String.class, msg -> {
                    long elapsedTime = Duration.between(start, Instant.now()).toMillis();

                    log.info(format("Time elapsed: %dms.", elapsedTime));
                    //System.out.println(format("Time elapsed: %dms.", elapsedTime));

                    actorSystem.terminate();
                })
                .build();
    }
}