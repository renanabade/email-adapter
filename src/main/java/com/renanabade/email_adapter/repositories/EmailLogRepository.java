package com.renanabade.email_adapter.repositories;

import com.renanabade.email_adapter.domain.email.model.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
}