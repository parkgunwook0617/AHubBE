package ahubbe.ahubbe.service.User;

import ahubbe.ahubbe.entity.AnimationInformation;
import ahubbe.ahubbe.repository.AdminRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final AdminRepository adminRepository;

    public List<AnimationInformation> importAnimationInformation() {

        return adminRepository.findAll();
    }

    public List<AnimationInformation> importAnimationInformationByTitle(String Title) {

        return adminRepository.findAllByTitleContaining(Title);
    }
}
