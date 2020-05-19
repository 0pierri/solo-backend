package com.fdmgroup.backend.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="persistent_logins")
@Setter
@Getter
@NoArgsConstructor
// Just here to make sure the persistent_logins table gets created.
abstract class PlaceholderTokenEntity {

    @Id
    private String series;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private Date lastUsed;

}
