package ru.netology.diploma_cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;
import ru.netology.diploma_cloud_storage.db.entities.FileId;
import ru.netology.diploma_cloud_storage.db.entities.UserEntity;
import ru.netology.diploma_cloud_storage.domain.AuthToken;
import ru.netology.diploma_cloud_storage.domain.CloudFile;
import ru.netology.diploma_cloud_storage.domain.FileSize;
import ru.netology.diploma_cloud_storage.domain.JwtToken;
import ru.netology.diploma_cloud_storage.exception.ErrorDeleteFileException;
import ru.netology.diploma_cloud_storage.exception.ErrorGettingListException;
import ru.netology.diploma_cloud_storage.exception.ErrorInputDataException;
import ru.netology.diploma_cloud_storage.exception.UnauthorizedErrorException;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;
import ru.netology.diploma_cloud_storage.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudService {
    private final CloudRepository repository;
    private final UserRepository userRepository;
    private final JwtToken jwt;

    @Autowired
    public CloudService(CloudRepository repository,
                        UserRepository userRepository,
                        JwtToken jwt) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.jwt = jwt;
    }

    public void uploadFile(String filename, String hash, String binaryFile, AuthToken authToken) {
        final FileId fileId = getFileIdFromFilenameAndToken(filename, authToken);
        if (!repository.existsById(fileId)) {
            final FileEntity entity = new FileEntity(fileId, hash, binaryFile);
            repository.save(entity);
        } else
            throw new ErrorInputDataException(filename, "file with such filename is already exist");
    }

    public MultiValueMap<String, String> downloadFile(String filename, AuthToken authToken) {
        final FileId fileId = getFileIdFromFilenameAndToken(filename, authToken);
        if (repository.existsById(fileId)) {
            final FileEntity entity = repository.getById(fileId);
            final CloudFile cf = new CloudFile(entity.getHash(), entity.getFile());
            final MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
            mvm.add("hash", cf.getHash());
            mvm.add("file", cf.getFile());
            return mvm;
        } else
            throw new ErrorInputDataException(filename, "file with such filename is not exist");
    }

    public void renameFile(String oldFilename, String newFilename, AuthToken authToken) {
        final FileId oldFileId = getFileIdFromFilenameAndToken(oldFilename, authToken);
        final FileId newFileId = new FileId(oldFileId.getOwner(), newFilename);
        if (!oldFilename.equals(newFilename)
                && repository.existsById(oldFileId)
                && !repository.existsById(newFileId)) {
            repository.renameFile(oldFileId, newFileId);
        } else if (oldFilename.equals(newFilename)) {
            throw new ErrorInputDataException(oldFilename, "input filenames are equal");
        } else if (!repository.existsById(oldFileId)) {
            throw new ErrorInputDataException(oldFilename, "file with such filename is not exist");
        } else
            throw new ErrorInputDataException(oldFilename, "file '" + newFilename + "' is already exist");
    }

    public List<FileSize> getFileList(int limit, AuthToken authToken) {
        final UserEntity owner = getUserFromToken(authToken);
        final List<FileEntity> files = owner.getFiles();
        final int storageSize = files.size();
        if (storageSize >= limit) {
            return files
                    .parallelStream()
                    .sorted(Comparator.comparing(FileEntity :: getCreated).reversed())
                    .map(x -> new FileSize(
                            x.getId().getFilename(),
                            x.getFile().replace(FileEntity.FILE_BYTES_SEPARATOR, "").length() / 8))
                    .limit(limit)
                    .collect(Collectors.toList());
        } else throw new ErrorGettingListException("fileList",
                "can't get " + limit + " files from storage with size " + storageSize);
    }

    public void deleteFile(String filename, AuthToken authToken) {
        final FileId fileId = getFileIdFromFilenameAndToken(filename, authToken);
        if (repository.existsById(fileId)) {
            repository.deleteById(fileId);
        } else throw new ErrorDeleteFileException(filename);
    }

    private UserEntity getUserFromToken(AuthToken authToken) {
        final String token = authToken.getAuthToken();
        final String login = jwt.getUsernameFromToken(token);
        if (userRepository.existsByLogin(login)) {
            return userRepository.findByLogin(login);
        } else throw new UnauthorizedErrorException("user",
                "user with login '" + login + "' doesn't exist");
    }

    private FileId getFileIdFromFilenameAndToken(String filename, AuthToken authToken) {
        final UserEntity owner = getUserFromToken(authToken);
        return new FileId(owner, filename);
    }

//    private String byteArrayToBinaryString(byte[] input) {
//        final StringBuilder result = new StringBuilder();
//        for (byte b : input) {
//            final String num = Integer.toBinaryString(b + Math.abs(Byte.MIN_VALUE));
//            final String block = String.format("%8s", num).replaceAll(" ", "0");
//            result
//                    .append(block)
//                    .append(FileEntity.FILE_BYTES_SEPARATOR);
//        }
//        return result.toString();
//    }

//    private byte[] binaryStringToByteArray(String binaryString) {
//        final String[] blocks = binaryString.split(FileEntity.FILE_BYTES_SEPARATOR);
//        final List<Byte> list = Stream.of(blocks)
//                .map(b -> (byte) (Integer.parseInt(b, 2) - Math.abs(Byte.MIN_VALUE)))
//                .collect(Collectors.toList());
//        byte[] bytes = new byte[list.size()];
//        for (int i = 0; i < bytes.length; i++)
//            bytes[i] = list.get(i);
//        return bytes;
//    }
}
