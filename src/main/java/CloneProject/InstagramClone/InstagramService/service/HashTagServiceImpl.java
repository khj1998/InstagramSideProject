package CloneProject.InstagramClone.InstagramService.service;

import CloneProject.InstagramClone.InstagramService.repository.HashTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashTagServiceImpl implements HashTagService {
    private HashTagRepository hashTagRepository;
}
