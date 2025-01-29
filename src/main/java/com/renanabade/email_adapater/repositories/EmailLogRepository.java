package com.renanabade.email_adapater.repositories;

import com.renanabade.email_adapater.domain.email.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
}