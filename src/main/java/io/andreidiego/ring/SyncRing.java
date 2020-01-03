package io.andreidiego.ring;

import java.time.Duration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import io.andreidiego.ring.RingManagerActor.InitialMessage;
import io.andreidiego.ring.SyncRingMemberActor.SetNextNode;
import scala.concurrent.Await;
import scala.concurrent.Future;

import static akka.actor.ActorRef.noSender;

class SyncRing {
    private final ActorRef firstRingMember;

    SyncRing(final int size) {
        final ActorSystem actorSystem = ActorSystem.create("Ring");

        //log.debug("Building the ring.");

        final ActorRef lastRingMember = actorSystem.actorOf(SyncRingMemberActor.props(null), String.valueOf(size));
        ActorRef nextRingMember = lastRingMember;

        for (int i = 1; i < size; i++) {
            nextRingMember = actorSystem.actorOf(SyncRingMemberActor.props(nextRingMember), String.valueOf(size - i));
        }

        firstRingMember = nextRingMember;
        lastRingMember.tell(new SetNextNode(firstRingMember), noSender());

        //log.debug("Ring is in place.");
    }

    void sendAsyncMessage(final String message) {
        firstRingMember.tell(new InitialMessage(message), noSender());
    }

    void sendMessage(final String message) throws Exception {
        final Timeout timeout = Timeout.create(Duration.ofSeconds(5));
        final Future<Object> future = Patterns.ask(firstRingMember, new InitialMessage(message), timeout);

        Await.result(future, timeout.duration());
    }
}