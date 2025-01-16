package com.team25.event.planner.security.user;

import com.team25.event.planner.user.model.Account;
import com.team25.event.planner.user.model.AccountStatus;
import com.team25.event.planner.user.model.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    @Getter
    private final long userId;

    @Getter
    private final long accountId;

    private final String username;

    private final String password;

    @Getter
    private final UserRole userRole;

    private final boolean isEnabled;

    public UserDetailsImpl(long userId, long accountId, String username, UserRole userRole) {
        this.userId = userId;
        this.accountId = accountId;
        this.username = username;
        this.password = null;
        this.userRole = userRole;
        this.isEnabled = true;
    }

    public UserDetailsImpl(Account account) {
        userId = account.getUser().getId();
        accountId = account.getId();
        username = account.getEmail();
        password = account.getPassword();
        userRole = account.getUser().getUserRole();
        isEnabled = account.getStatus().equals(AccountStatus.ACTIVE);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        switch (userRole) {
            case EVENT_ORGANIZER -> authorities.add(new SimpleGrantedAuthority("ROLE_EVENT_ORGANIZER"));
            case OWNER -> authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
            case ADMINISTRATOR -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
