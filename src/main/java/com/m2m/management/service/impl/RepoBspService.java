package com.m2m.management.service.impl;

import com.m2m.management.configuration.UploadConfig;
import com.m2m.management.entity.RepoBsp;
import com.m2m.management.entity.Storage;
import com.m2m.management.repository.IRepoBspsRepository;
import com.m2m.management.service.IRepoBspService;
import com.m2m.management.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.m2m.management.utils.UploadUtils.*;


/**
 * @author ：gangqiang
 * @date ：Created in 4/28/19 3:59 PM
 * @description：action repobsp db table
 */
@Service
@Slf4j
public class RepoBspService implements IRepoBspService {

    private String pathSeparate = File.separator;
    @Autowired
    private IRepoBspsRepository repoBspRepository;

    @Override
    public List<RepoBsp> getAll() {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findAll();
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> getAll(Storage storage) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByStorage(storage);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<RepoBsp> getAllByPage(String keywords, int currentPage, int limit, Storage storage) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoBsp> repoBsps = repoBspRepository.findByStorageAndBoardnameContaining(storage, keywords, pageable);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> getAllByOsAndPage(String os, String keywords, int currentPage, int limit, Storage storage) {
        try{
            keywords = keywords.replaceAll("_", "\\\\_");
            Pageable pageable = new PageRequest(currentPage, limit, Sort.Direction.DESC, "ts");
            List<RepoBsp> repoBsps = repoBspRepository.findByStorageAndOsAndBoardnameContaining(storage, os, keywords, pageable);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> get(String boardname) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByBoardname(boardname);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> get(String boardname, Storage storage) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByBoardnameAndStorage(boardname, storage);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> getByOs(String os) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByOs(os);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> getByOs(String os, Storage storage) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByOsAndStorage(os, storage);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> get(String boardname, String versionname, Storage storage) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByStorageAndBoardnameAndVersionname(storage, boardname, versionname);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<RepoBsp> get(String boardname, String versionname,  Storage storage, String os) {
        try{
            List<RepoBsp> repoBsps = repoBspRepository.findByStorageAndBoardnameAndVersionnameAndOs(storage, boardname, versionname, os);
            return repoBsps;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RepoBsp get(long rbid) {
        try{
            Optional<RepoBsp> opRepoBsps = repoBspRepository.findById(rbid);
            RepoBsp repoBsp = opRepoBsps.get();
            return repoBsp;
        }catch(NoSuchElementException e){
            e.printStackTrace();
            return null;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(RepoBsp repoBsp) {
        try{
            repoBspRepository.save(repoBsp);
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
    public boolean update(long rbid, RepoBsp repoBsp) {
        try{
            RepoBsp crepoBsp = repoBspRepository.findById(rbid).get();
            crepoBsp.setDescription(repoBsp.getDescription());
            crepoBsp.setTs(new Date().getTime());
            repoBspRepository.save(crepoBsp);
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
    public boolean update(long rbid, String presentation) {
        try{
            RepoBsp crepoBsp = repoBspRepository.findById(rbid).get();
            crepoBsp.setPresentation(presentation);
            repoBspRepository.save(crepoBsp);
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
    public boolean delete(long rbid) {
        try{
            repoBspRepository.deleteById(rbid);
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
    public boolean deleteRepoBsp(String boardPath, String versionname){
        boolean isDelDir = false;
        if(FileUtil.isOnlyChildDir(boardPath)){
            isDelDir = FileUtil.delDir(boardPath);
        }else{
            isDelDir = FileUtil.delDir(boardPath + pathSeparate + versionname);
        }
        return isDelDir;
    }

    @Override
    public long count(String keywords, Storage storage) {
        try{
            long count = 0;
            if(keywords == null ||keywords.equals("")){
                count = repoBspRepository.countByStorage(storage);
            }else{
                count = repoBspRepository.countByStorageAndBoardnameContaining(storage, keywords);
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
    public long count(String os, String keywords, Storage storage) {
        try{
            long count = 0;
            if(keywords == null ||keywords.equals("")){
                count = repoBspRepository.countByStorageAndOs(storage, os);
            }else{
                count = repoBspRepository.countByStorageAndOsAndBoardnameContaining(storage, os, keywords);
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
    public long count() {
        try{
            long count = repoBspRepository.count();
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
    public long countByOs(String os, Storage storage) {
        try{
            long count  = repoBspRepository.countByStorageAndOs(storage, os);
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
            long count = repoBspRepository.countByStorage(storage);
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
    public boolean uploadWithBlock(String tname, String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            String fileName = getFileName(md5, chunks);
            FileUtil.writeWithBlok(UploadConfig.path + "/bsprepo/" + tname, size, file.getInputStream(), file.getSize(), chunks, chunk);
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
    public boolean uploadWithBlock(String name, String md5, Long size, Integer chunks, Integer chunk, MultipartFile file) {
        try {
            getFileName(md5, chunks);
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
            repoBspRepository.deleteAll();
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
