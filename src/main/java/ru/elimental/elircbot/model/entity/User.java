package ru.elimental.elircbot.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "nick")
    private String nick;

    @Column(name = "last_join")
    private LocalDateTime lastJoin;

    @Column(name = "last_exit")
    private LocalDateTime lastExit;

    public User(String nick) {
        this.nick = nick;
    }
}
