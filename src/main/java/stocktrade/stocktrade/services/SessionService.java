package stocktrade.stocktrade.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stocktrade.stocktrade.entities.SessionEntity;
import stocktrade.stocktrade.entities.UserDetailsEntity;
import stocktrade.stocktrade.repositories.SessionRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public void createNewSession(UserDetailsEntity userDetails,String refreshToken){
        if(sessionRepository.existsByUser(userDetails)){
            List<SessionEntity> sessionEntityList = sessionRepository.findByUser(userDetails);
            if(sessionEntityList.size()==2){
                sessionEntityList.sort(Comparator.comparing(SessionEntity::getLastUsedAt));
                SessionEntity session = sessionEntityList.getLast();
                sessionRepository.deleteById(session.getId());
            }
        }
        SessionEntity newSession = SessionEntity.builder()
                .lastUsedAt(LocalDateTime.now())
                .refreshToken(refreshToken)
                .user(userDetails)
                .build();
        sessionRepository.save(newSession);
    }

    public void removeSession(String refreshToken) {
        sessionRepository.deleteByRefreshToken(refreshToken);
    }
}
