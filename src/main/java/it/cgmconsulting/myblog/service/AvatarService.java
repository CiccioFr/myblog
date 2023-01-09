package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Avatar;
import it.cgmconsulting.myblog.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvatarService {

    @Autowired
    AvatarRepository avatarRepository;

    public void save(Avatar avatar) {
        avatarRepository.save(avatar);
    }

    public void delete(Avatar avatar){
        avatarRepository.delete(avatar);
    }
}
