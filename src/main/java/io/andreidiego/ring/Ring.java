package io.andreidiego.ring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.andreidiego.ring.RingManagerActor.InitialMessage;

import static akka.actor.ActorRef.noSender;

class Ring {
    private final ActorRef ringManager;

    Ring(final int size) {
        final ActorSystem actorSystem = ActorSystem.create("Ring");
        ringManager = actorSystem.actorOf(RingManagerActor.props(size, actorSystem));
    }

    void sendAsyncMessage(final String message) {
        ringManager.tell(new InitialMessage(message), noSender());
    }
}