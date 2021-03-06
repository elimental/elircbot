package ru.elimental.elircbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne
    private Channel channel;

    @ManyToOne
    private User user;

    @Column(name = "text", length = 512)
    private String text;
}
