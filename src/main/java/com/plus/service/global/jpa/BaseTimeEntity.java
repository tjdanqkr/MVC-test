package com.plus.service.global.jpa;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
     @CreatedDate
     @Column(name="CREATED_AT", updatable=false)
     private LocalDateTime createdDate;

     @LastModifiedDate
     @Column(name="UPDATED_AT")
     private LocalDateTime modifiedDate;
}
