package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(TransactionRepository transactionRepository, UserRepository userRepository,
            RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        logger.info("Received transaction: {}", transaction);
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", transaction,
                    Incentive.class);
            float incentiveAmount = incentive != null ? incentive.getAmount() : 0;

            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            userRepository.save(sender);
            userRepository.save(recipient);

            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(),
                    incentiveAmount);
            transactionRepository.save(record);
            logger.info("Transaction processed: {}", record);
        } else {
            logger.info("Transaction discarded: {}", transaction);
        }
    }
}
