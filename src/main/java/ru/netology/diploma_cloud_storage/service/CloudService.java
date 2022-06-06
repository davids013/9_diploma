package ru.netology.diploma_cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;
import ru.netology.diploma_cloud_storage.domain.CloudFile;
import ru.netology.diploma_cloud_storage.exception.ErrorDeleteFileException;
import ru.netology.diploma_cloud_storage.exception.ErrorInputDataException;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CloudService {
    private final CloudRepository repository;

    @Autowired
    public CloudService(CloudRepository repository) {
        this.repository = repository;
    }

    public FileEntity test(String filename, String file) {
        return repository.save(new FileEntity(filename, "testHash", file));
    }

    public void deleteFile(String filename) {
        if (repository.existsById(filename)) {
            repository.deleteById(filename);
        } else throw new ErrorDeleteFileException(filename);
    }

    public void uploadFile(String filename, String hash, MultipartFile multipartFile) {
        if (!repository.existsById(filename)) {
            String binaryFile = "";
            try {
                binaryFile = byteArrayToBinaryString(multipartFile.getBytes());
            } catch (IOException e) {
                throw new ErrorInputDataException(filename);
            }
            final FileEntity entity = new FileEntity(filename, hash, binaryFile);
            repository.save(entity);
        } else
            throw new ErrorInputDataException(filename, "file with such filename is already exist");
    }

    public MultiValueMap<String, String> downloadFile(String filename) {
        if (repository.existsById(filename)) {
            final FileEntity entity = repository.getById(filename);
            byte[] bytes = binaryStringToByteArray(entity.getFile());
            final String resultFile = Arrays.toString(bytes);
            final CloudFile cf = new CloudFile(entity.getHash(), resultFile);
            final MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
            mvm.add("hash", cf.getHash());
            mvm.add("file", cf.getFile());
            return mvm;
        } else
            throw new ErrorInputDataException(filename, "file with such filename is not exist");
    }


    public void renameFile(String oldFilename, String newFilename) {
        if (!oldFilename.equals(newFilename)
                && repository.existsById(oldFilename)
                && !repository.existsById(newFilename)) {
            repository.renameFile(oldFilename, newFilename);
        } else if (oldFilename.equals(newFilename)) {
            throw new ErrorInputDataException(oldFilename, "input filenames are equeal");
        } else if (!repository.existsById(oldFilename)) {
            throw new ErrorInputDataException(oldFilename, "file with such filename is not exist");
        } else
            throw new ErrorInputDataException(oldFilename, "file '" + newFilename + "' is already exist");
    }

    public void renameFile2(String filename, String newName) {
        if (repository.existsById(filename) && !repository.existsById(newName)) {
            final FileEntity oldFile = repository.getById(filename);
            FileEntity newFile = new FileEntity(oldFile);
            newFile.setFilename(newName);
            repository.saveAndFlush(newFile);
            newFile = repository.getById(newName);
            newFile.setCreated(oldFile.getCreated());
            repository.save(newFile);
            repository.deleteById(filename);
        } else if (!repository.existsById(filename)) {
            throw new ErrorInputDataException(filename, "file with such filename is not exist");
        } else
            throw new ErrorInputDataException(filename, "file '" + newName + "' is already exist");
    }

    private String byteArrayToBinaryString(byte[] input) {
        final StringBuilder result = new StringBuilder();
        for (byte b : input) {
            final String num = Integer.toBinaryString(b + Math.abs(Byte.MIN_VALUE));
            final String block = String.format("%8s", num).replaceAll(" ", "0");
            result
                    .append(block)
                    .append(FileEntity.FILE_BYTES_SEPARATOR);
        }
        return result.toString();
    }

    private byte[] binaryStringToByteArray(String binaryString) {
        final String[] blocks = binaryString.split(FileEntity.FILE_BYTES_SEPARATOR);
        final List<Byte> list = Stream.of(blocks)
                .map(b -> (byte) (Integer.parseInt(b, 2) - Math.abs(Byte.MIN_VALUE)))
                .collect(Collectors.toList());
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = list.get(i);
        return bytes;
    }
}
