package org.digikat.rabbit;

import org.digikat.rabbit.events.AddRankingEvent;
import org.digikat.rabbit.events.UpdateRankingEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Service
public class RankingMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public RankingMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpdateRankingEvent(final UpdateRankingEvent eventToSend) {
        rabbitTemplate.convertAndSend("update-ranking", eventToSend);
        System.out.println("Wysłano wiadomość: " + eventToSend);
    }

    public void sendAddedRankingEvent(final AddRankingEvent eventToSend){
        rabbitTemplate.convertAndSend("add-ranking", eventToSend);
    }
}
