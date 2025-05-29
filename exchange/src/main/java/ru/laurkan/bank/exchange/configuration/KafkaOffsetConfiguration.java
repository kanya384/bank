package ru.laurkan.bank.exchange.configuration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;

import java.util.Collection;
import java.util.List;

@Configuration
public class KafkaOffsetConfiguration implements ConsumerAwareRebalanceListener {
    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        //перематываем на последнее сообщение в топике 'rates' после перезапуска
        for (TopicPartition partition : partitions) {
            if (partition.topic().equals("rates")) {
                var offset = consumer.endOffsets(List.of(partition))
                        .getOrDefault(partition, 0L);
                consumer.seek(partition, offset == 0 ? 0 : offset - 1);
            }
        }
    }
}
