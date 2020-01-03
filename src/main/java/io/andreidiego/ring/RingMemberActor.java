package io.andreidiego.ring;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

class RingMemberActor extends AbstractActor {
    //private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef nextRingMember;

    static Props props(final ActorRef nextRingMember) {
        return Props.create(RingMemberActor.class, nextRingMember);
    }

    private RingMemberActor(final ActorRef nextRingMember) {
        this.nextRingMember = nextRingMember;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(msg -> {

            //log.debug(format("Message %s received by the actor %s.", msg, self().path().name()));

            nextRingMember.tell(msg, self());
        }).build();
    }
}