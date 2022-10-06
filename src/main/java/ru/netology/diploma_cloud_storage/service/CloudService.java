package ru.netology.diploma_cloud_storage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.netology.diploma_cloud_storage.db.entities.FileEntity;
import ru.netology.diploma_cloud_storage.domain.CloudFile;
import ru.netology.diploma_cloud_storage.domain.FileSize;
import ru.netology.diploma_cloud_storage.exception.ErrorDeleteFileException;
import ru.netology.diploma_cloud_storage.exception.ErrorGettingListException;
import ru.netology.diploma_cloud_storage.exception.ErrorInputDataException;
import ru.netology.diploma_cloud_storage.repository.CloudRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CloudService {
    private final CloudRepository repository;

    @Autowired
    public CloudService(CloudRepository repository) {
        this.repository = repository;
    }

    public void deleteFile(String filename) {
        if (repository.existsById(filename)) {
            repository.deleteById(filename);
        } else throw new ErrorDeleteFileException(filename);
    }

    public void uploadFile(String filename, String hash, String binaryFile) {
        if (!repository.existsById(filename)) {
            final FileEntity entity = new FileEntity(filename, hash, binaryFile);
            repository.save(entity);
        } else
            throw new ErrorInputDataException(filename, "file with such filename is already exist");
    }

    public MultiValueMap<String, String> downloadFile(String filename) {
        if (repository.existsById(filename)) {
            final FileEntity entity = repository.getById(filename);
            final CloudFile cf = new CloudFile(entity.getHash(), entity.getFile());
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
            throw new ErrorInputDataException(oldFilename, "input filenames are equal");
        } else if (!repository.existsById(oldFilename)) {
            throw new ErrorInputDataException(oldFilename, "file with such filename is not exist");
        } else
            throw new ErrorInputDataException(oldFilename, "file '" + newFilename + "' is already exist");
    }

    public List<FileSize> getFileList(int limit) {
        final long storageSize = repository.count();
        if (storageSize >= limit) {
            final Sort sort = Sort.by(Sort.Direction.DESC, "created");
            return repository.findAll(PageRequest.of(0, limit, sort))
                    .stream()
                    .map(x -> new FileSize(
                            x.getFilename(),
                            x.getFile().replace(" ", "").length() / 8))
                    .collect(Collectors.toList());
        } else throw new ErrorGettingListException("fileList",
                "can't get " + limit + " files from storage (max " + storageSize + ")");
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
