package com.edi.learn.cloud.command.config;

import com.edi.learn.cloud.command.saga.OrderSaga;
import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.SagaConfiguration;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.slf4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.Transactional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Edison on 2017/3/25.
 */
@Configuration
public class AmqpConfiguration {

    private static final Logger LOGGER = getLogger(AmqpConfiguration.class);

    @Value("${axon.amqp.exchange}")
    private String exchangeName;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public Queue queue(){
        return new Queue("orderqueue", true);
    }


    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.fanoutExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Binding queueBinding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("").noargs();
    }

    @Bean
    public SpringAMQPMessageSource queueMessageSource(Serializer serializer){
        return new SpringAMQPMessageSource(serializer){
            @RabbitListener(queues = "orderqueue")
            @Override
            @Transactional
            public void onMessage(Message message, Channel channel) throws Exception {
                LOGGER.debug("Message received: "+message.toString());
                super.onMessage(message, channel);
            }
        };
    }

    @Bean
    public SagaConfiguration<OrderSaga> orderSagaConfiguration(Serializer serializer){
        SagaConfiguration<OrderSaga> sagaConfiguration = SagaConfiguration.subscribingSagaManager(OrderSaga.class, c-> queueMessageSource(serializer));
        //sagaConfiguration.registerHandlerInterceptor(c->transactionManagingInterceptor());
        return sagaConfiguration;
    }

    @Bean
    public TransactionManagingInterceptor transactionManagingInterceptor(){
        return new TransactionManagingInterceptor(new SpringTransactionManager(transactionManager));
    }

}
