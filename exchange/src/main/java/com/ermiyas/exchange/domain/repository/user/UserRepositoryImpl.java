package com.ermiyas.exchange.domain.repository.user;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.infrastructure.persistence.JpaUserRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import lombok.*;



@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaRepository;

    @Override
    public User save(User user) { return jpaRepository.save(user); }

    @Override
    public Optional<User> findById(Long id) { return jpaRepository.findById(id); }

    @Override
    public List<User> findAll() { return jpaRepository.findAll(); }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }
    @Override
    public long count(){
        return jpaRepository.count();
    }
    @Override
    public void deleteById(Long id){
        jpaRepository.deleteById(id);
    }
}