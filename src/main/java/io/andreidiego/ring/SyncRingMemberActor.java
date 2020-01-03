package io.andreidiego.ring;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class SyncRingMemberActor extends AbstractActor {
    //private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef nextRingMember;

    static class InitialMessage {
        private final String message;

        InitialMessage(final String message) {
            this.message = message;
        }
    }

    static class SetNextNode {
        private final ActorRef nextNode;

        SetNextNode(final ActorRef nextNode) {
            this.nextNode = nextNode;
        }
    }

    static Props props(final ActorRef nextRingMember) {
        return Props.create(RingMemberActor.class, nextRingMember);
    }

    private SyncRingMemberActor(final ActorRef nextRingMember) {
        this.nextRingMember = nextRingMember;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {

                    //log.debug(format("Message %s received by the actor %s.", msg, self().path().name()));

                    nextRingMember.tell(msg, self());
                })
                .match(SetNextNode.class, nextNodeMessage -> this.nextRingMember = nextNodeMessage.nextNode)
                .build();
    }
}