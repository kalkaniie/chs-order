package myProject_LSP;

import myProject_LSP.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCookQtyChecked_CookCancelUpdate(@Payload CookQtyChecked cookQtyChecked){
        if(cookQtyChecked.isMe()){
            Optional<Order> orderOptional = orderRepository.findById(cookQtyChecked.getOrderId());
            Order order = orderOptional.get();
            //System.out.println("##### listener CookCancelUpdate : " + cookQtyChecked.toJson());
            //order.setId(cookQtyChecked.getOrderId());
            order.setStatus("ORDER : QTY OVER CANCELED");
            orderRepository.save(order);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverGiftSended_GiftInfoUpdate(@Payload GiftSended giftSended){

        if(giftSended.isMe()){
            System.out.println("##### listener GiftInfoUpdate : " + giftSended.toJson());
            Optional<Order> orderOptional = orderRepository.findById(giftSended.getOrderId());
            Order order = orderOptional.get();
            if("GIFT : GIFT SENDED".equals(giftSended.getStatus())){
                order.setGiftStatus("ORDER : GIFT SENDED SUCCESS");
            }
            orderRepository.save(order);
        }
    }

}