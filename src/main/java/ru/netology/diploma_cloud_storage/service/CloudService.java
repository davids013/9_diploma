package ru.netology.diploma_cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;

@Service
public class CloudService {
    private final CloudRepository repository;

    @Autowired
    public CloudService(CloudRepository repository) {
        this.repository = repository;
    }

    public String test() {
        return repository.test();
    }
}
