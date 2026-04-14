package com.m2m.management.service.impl;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.entity.RepoFile;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoFilesRepository;
import com.m2m.management.service.IRepoFileService;
import com.m2m.management.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.m2m.management.utils.UploadUtils.*;

/**
 * @date ：Created in 8/1/19 1:18 PM
 * @description：action repofile db
 */
@Service
public class RepoFileService implements IRepoFileService  {
    @Autowired
    IRepoFilesRepository repoFilesRepository;

    @Override
    public List<RepoFile> getAll() {
        try{
            List<RepoFile> repoFiles = repoFilesRepository.findAll();
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> getAll(Storage storage) {
        try{
            List<RepoFile> repoFiles = repoFilesRepository.findByStorage(storage);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> getAll(String keywords, int currentPage, int limit) {
        try{
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoFile> repoFiles = repoFilesRepository.findByFilenameContaining(keywords, pageable);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> getAllByType(String type, String keywords, int currentPage, int limit, Storage storage) {
        try{
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoFile> repoFiles = repoFilesRepository.findByStorageAndTypeAndFilenameContaining(storage, type, keywords, pageable);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoFile getByFilename(String filename) {
        try{
            RepoFile repoFile = null;
            List<RepoFile> repoFiles = repoFilesRepository.findByFilename(filename);
            if(repoFiles.size()> 0){
                repoFile = repoFiles.get(0);
            }
            return repoFile;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoFile get(long rfid) {
        try{
            Optional<RepoFile> opRepoFiles = repoFilesRepository.findById(rfid);
            RepoFile repoFile = opRepoFiles.get();
            return repoFile;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoFile getByFilename(String filename, Storage storage) {
        try{
            RepoFile repoFile = null;
            List<RepoFile> repoFiles = repoFilesRepository.findByStorageAndFilename(storage, filename);
            if(repoFiles.size()> 0){
                repoFile = repoFiles.get(0);
            }
            return repoFile;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoFile getByFilenameAndType(String filename, String type, Storage storage) {
        try{
            RepoFile repoFile = null;
            List<RepoFile> repoFiles = repoFilesRepository.findByFilenameAndTypeAndStorage(filename, type, storage);
            if(repoFiles.size()> 0){
                repoFile = repoFiles.get(0);
            }
            return repoFile;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> get(String type) {
        try{
            List<RepoFile> repoFiles = repoFilesRepository.findByType(type);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> get(String type, Storage storage) {
        try{
            List<RepoFile> repoFiles = repoFilesRepository.findByTypeAndStorage(type, storage);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoFile> getByTypeAndFormat(String type, String format, Storage storage) {
        try{
            List<RepoFile> repoFiles = repoFilesRepository.findByTypeAndFormatAndStorage(type, format, storage);
            return repoFiles;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(RepoFile repoFile) {
        try{
            repoFilesRepository.save(repoFile);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(long rfid, RepoFile repoFile) {
        try{
            RepoFile crepoFile = repoFilesRepository.findById(rfid).get();
            crepoFile.setAddress(repoFile.getAddress());
            crepoFile.setDescription(repoFile.getDescription());
            crepoFile.setFilename(repoFile.getFilename());
            crepoFile.setType(repoFile.getType());
            crepoFile.setTs(new Date().getTime());
            repoFilesRepository.save(crepoFile);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(long rfid, String presentation) {
        try{
            RepoFile crepoFile = repoFilesRepository.findById(rfid).get();
            crepoFile.setPresentation(presentation);
            repoFilesRepository.save(crepoFile);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(long rfid) {
        try{

            repoFilesRepository.deleteById(rfid);
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public long count(String keywords) {
        try{
            long count = 0;
            if(keywords == null ||keywords.equals("")){
                count = repoFilesRepository.count();
            }else{
                count = repoFilesRepository.countByFilenameContaining(keywords);
            }

            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long count(String type, String keywords, Storage storage) {
        try{
            long count = 0;
            count = repoFilesRepository.countByStorageAndTypeAndFilenameContaining(storage, type, keywords);
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long count() {
        try{
            long count = repoFilesRepository.count();
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long count(Storage storage) {
        try{
            long count = repoFilesRepository.countByStorage(storage);
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public long countByType(String type, Storage storage) {
        try{
            long count = repoFilesRepository.countByTypeAndStorage(type, storage);
            return count;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return 0;
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            String fileName = getFileName(md5, chunks);
            FileUtil.writeWithBlok(UploadConfig.path + "/" + md5 + "/" + name, size, file.getInputStream(), file.getSize(), chunks, chunk);
            addChunk(md5, chunk);
            if (isUploaded(md5)) {
                removeKey(md5);
            }
            return true;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAll() {
        try{
            repoFilesRepository.deleteAll();
            return true;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
